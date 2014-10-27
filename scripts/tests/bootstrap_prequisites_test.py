#!/usr/bin/python

################################################################################
# Integration tests in LXC:
################################################################################
# LXCs (Linux containers) are created on demand from remote resources (handled 
# be the script). The containers are snapshoted after the installation and their 
# status is restored after the test on the LXC (currently efficient snapshoting 
# with btrfs and lvm2 doesn't work, so that a plain copy of the container is 
# created with on file basis (cp/rsync) which 
# should be fast if the copying occurs on a btrfs filesystem (e.g. when 
# lxc_storage is 'btrfs') (LXCs have to be stored in a subdirectory of btrfs 
# root in order to let the snapshots be stored on the btrfs)).
# 
# As bootstrap_prequisites.py encapsulates all privileged tests, they're 
# executed in LXCs only in order to not mess up the system the test is run on.

# internal implementation notes:
# - script has to be invoked with sudo not only because of privileges for lxc 
# but also to avoid the necessity to manage sudo password within the script 
# (easier: the script requires lxc which only runs with sudo -> the script has 
# to run with sudo!)
# - don't use lxc-clone for snapshoting the initial state of the container 
# because this overwrites the hostname when copying to the original location
#
################################################################################
# As passing arguments to unittest if invoked on command line seems to be 
# impossible, the tests are configured with the <tt>bootstrap_test.conf</tt>.
# 

import unittest
import sys
import os
sys.path.append(os.path.realpath(os.path.join(__file__, "..")))
sys.path.append(os.path.realpath(os.path.join(__file__, "..", "..")))
sys.path.append(os.path.realpath(os.path.join(__file__, "..", "../../lib")))
import bootstrap_prequisites
import pm_utils
import sparse_file_utils
import subprocess as sp
import logging
import file_line_utils
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)
import threading
import tempfile
import ConfigParser
import shutil
import getpass
import pexpect
import string
import signal
import multiprocessing
import time
import re
import lxc_test
import file_utils

wget = "wget"
apt_get = "apt-get"
umount = "umount"
cp = "cp"
rm = "rm"
bash = "bash"
rsync = "rsync"
mount = "mount"

ftp_dir = os.path.realpath(os.path.join(__file__, "..", "ftp-base"))
ftp_user = "user"
ftp_pw = "12345"
ftp_address = "127.0.0.1"
ftp_port = 21215
ftp_url = "ftp://%s:%s" % (ftp_address, str(ftp_port))
server = None

ftp_server_start_timeout = 5

# it shouldn't be necessary to deal with with trailing ':' because after the 'password ... :' string is written to stdout, the password can already be entered
def __sudo_queries__(username):
    return [
        "[pP]assword", # doesn't seem to work: "[sudo] password for %s:" % username, # Ubuntu 14.04 and 12.04
    ]
# credentials of the user to be used in the lxc (set up for every container to 
# simulate non-privileged user with same credentials)
lxc_username = "user"
lxc_user_password = "user"
lxc_dir = os.path.realpath(os.path.join(__file__, "..", "lxc"))
                   
class Test(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        super(Test, cls).setUpClass()

    @classmethod
    def tearDownClass(cls):
        super(Test, cls).tearDownClass()
        
    # not testing
    # - pass different postgresql binaries as arguments
    def test_bootstrap(self):
        file_utils.create_dir(lxc_dir, True)
        script_base_dir = os.path.realpath(os.path.join(__file__, "..", ".."))
        
        def __generate_bind_target__(lxc_spec):
            bind_target = os.path.join(lxc_dir, lxc_spec, "rootfs", "home/user/scripts/")
            return bind_target
            
        def __pre_target__(lxc_spec):
            ################################################################
            # Copying scripts
            ###############################################################
            # - create a copy of scripts directory (in order to not mess up the sources)
            # - ?? simple sync with lxc doesn't seem to work (maybe due to different inode management) (simple rsync both before and after boot  causes the dissappearence of file after some instants) -> mount-bind that directory 
            # - shutil.copy2 should copy sufficient metadata
            # - only copy scripts subdirectory (as it should work independently from source root)
            # - exclude tests in order to avoid endless recursion with copies into source (specification of absolute exclusions doesn't work -> specify relatively), exclude *.pyc because it might causes errors
            # - always copy, not only at installations, in order update changes
            # - only os.path.realpath allows to resolve /path/to/file.ext/.. (os.path.join doesn't)
            script_copy_dir = os.path.join(lxc_dir, "scripts-copy") 
            if not os.path.exists(script_copy_dir):
                os.makedirs(script_copy_dir)
            for root, dirnames, filenames in os.walk(
                script_base_dir, 
                topdown=True, # necessary for in-place manipulation of dirnames
            ):
                dirnames[:] = [d for d in dirnames if d not in ["tests", "tmp"]] # also excludes subdirs
                for filename in filenames:
                    if filename == "lxc-btrfs.img":
                        continue
                    if filename.endswith(".pyc"):
                        continue
                    script = os.path.join(root,filename)
                    rel_to_base_dir = os.path.relpath(script, script_base_dir)
                    target = os.path.join(script_copy_dir, rel_to_base_dir)
                    target_parent = os.path.realpath(os.path.join(target, ".."))
                    if not os.path.exists(target_parent):
                        os.makedirs(target_parent)
                    logger.debug("copying %s -> %s" % (script, target))
                    shutil.copy2(script, target)
            bind_target = __generate_bind_target__(lxc_spec)
            if not os.path.exists(bind_target):
                os.makedirs(bind_target)
            sp.check_call([mount, "-o", "rw,bind", script_copy_dir, bind_target]) # has to be unmounted before the original is restored below
        
        def __target__(child):               
            child.sendline("sudo chown -R user:user /home/user/scripts") # chown at every start because script are copied at every run
            child.expect(__sudo_queries__(lxc_username))
            child.sendline("user")
            child.expect("[#$][\\s]")
            
            # test success
            child.sendline("sudo python /home/user/scripts/bootstrap_prequisites.py")
            child.expect("Do you want to continue \\[Y/n\\]\\?") # apt (don't use --assume-yes or --yes in the script!)
            child.sendline("y")
            child.expect("[#$][\\s]")
            child.sendline("echo $?")
            child.expect("[0-9]+")
            child_output = child.after.strip()
            child.expect("[#$][\\s]") # after parsing the return of echo $? the prompt has to be expected
            self.assertTrue(0 == int(child_output))
            child.sendline("sudo halt")
            child.expect(":") # sudo password for shutdown
            child.sendline("user")
            child.wait()
            
        def __post_target__(lxc_spec):
            bind_target = __generate_bind_target__(lxc_spec)
            sp.check_call([umount, bind_target]) # doesn't really make sense to ignore failure (just messes up the failure output) because removing a mounted directory fails certainly
        
        config_file_path = os.path.realpath(os.path.join(__file__, "..", "bootstrap_test.conf"))
        logger.debug("reading config file %s" % (config_file_path,))
        config = ConfigParser.ConfigParser()
        config.read([config_file_path])
        apt_proxy = config.get("lxc-apt", "apt.proxy")
        lxc_test.test(lxc_dir=lxc_dir, target=__target__, pre_target=__pre_target__, post_target=__post_target__, apt_proxy=apt_proxy)

if __name__ == "__main__":
    unittest.main()

