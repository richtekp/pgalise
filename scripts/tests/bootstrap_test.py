#!/usr/bin/python

import unittest
import sys
import os
sys.path.append(os.path.realpath(os.path.join(__file__, "..")))
sys.path.append(os.path.realpath(os.path.join(__file__, "..", "..")))
sys.path.append(os.path.realpath(os.path.join(__file__, "..", "../../lib")))
import bootstrap
import pm_utils
import sparse_file_utils
import subprocess as sp
import logging
import file_line_utils
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

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

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

# internal implementation notes:
# - script has to be invoked with sudo not only because of privileges for lxc 
# but also to avoid the necessity to manage sudo password within the script 
# (easier: the script requires lxc which only runs with sudo -> the script has 
# to run with sudo!)
# - don't use lxc-clone for snapshoting the initial state of the container 
# because this overwrites the hostname when copying to the original location

wget = "wget"
apt_get = "apt-get"
lxc_create = "lxc-create"
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
lxc_password = "user"
lxc_dir = os.path.realpath(os.path.join(__file__, "..", "lxc"))

common_programs = ["python", "openjdk-jre"]

# download all files which would be retrieved remotely to the FTP base directory
def init_ftp():
    if not os.path.exists(ftp_dir):
        os.makedirs(ftp_dir)
    postgresql_jdbc_file = os.path.join(ftp_dir, bootstrap.postgresql_jdbc_name)
    if not file_utils.check_file(postgresql_jdbc_file, bootstrap.postgresql_jdbc_md5):
        sp.check_call([wget, "-O", postgresql_jdbc_file, bootstrap.postgresql_jdbc_url_default])
    postgresql_deb_file = os.path.join(ftp_dir, bootstrap.postgresql_deb_name)
    if not file_utils.check_file(postgresql_deb_file, bootstrap.postgresql_deb_md5):
        sp.check_call([wget, "-O", postgresql_deb_file, bootstrap.postgresql_deb_url_default])
    geotools_src_archive_file = os.path.join(ftp_dir, bootstrap.geotools_src_archive_name)
    if not file_utils.check_file(geotools_src_archive_file, bootstrap.geotools_src_archive_md5):
        sp.check_call([wget, "-O", geotools_src_archive_file, bootstrap.geotools_url_default])
    postgis_src_archive_file = os.path.join(ftp_dir, bootstrap.postgis_src_archive_name)
    if not file_utils.check_file(postgis_src_archive_file, bootstrap.postgis_src_archive_md5):
        sp.check_call([wget, "-O", postgis_src_archive_file, bootstrap.postgis_url_default])
    commons_src_archive_file = os.path.join(ftp_dir, bootstrap.commons_src_archive_name)
    if not file_utils.check_file(commons_src_archive_file, bootstrap.commons_src_archive_md5):
        sp.check_call([wget, "-O", commons_src_archive_file, bootstrap.commons_src_archive_url_default])
    jgrapht_file = os.path.join(ftp_dir, bootstrap.jgrapht_name)
    if not file_utils.check_file(jgrapht_file, bootstrap.jgrapht_md5):
        sp.check_call([wget, "-O", jgrapht_file, bootstrap.jgrapht_url_default])
    jfuzzy_file = os.path.join(ftp_dir, bootstrap.jfuzzy_name)
    if not file_utils.check_file(jfuzzy_file, bootstrap.jfuzzy_md5):
        sp.check_call([wget, "-O", jfuzzy_file, bootstrap.jfuzzy_url_default])
    maven_bin_archive_file = os.path.join(ftp_dir, bootstrap.maven_bin_archive_name)
    if not file_utils.check_file(maven_bin_archive_file, bootstrap.maven_bin_archive_md5):
        sp.check_call([wget, "-O", maven_bin_archive_file, bootstrap.maven_bin_archive_url_default])
    
    packages = ["python-pyftpdlib"]
    sp.check_call([apt_get, "install", ]+packages)
    
    # as long as pyftpdlib doesn't support shutdown (see https://code.google.com/p/pyftpdlib/issues/detail?id=267) use a subprocess and kill it if SIGINT is received (listening to SIGINT is also necessary if FTP server runs in a separate thread)
    def __ftp_process__():
        from pyftpdlib.authorizers import DummyAuthorizer
        from pyftpdlib.handlers import FTPHandler
        from pyftpdlib.servers import FTPServer

        authorizer = DummyAuthorizer()
        authorizer.add_user(ftp_user, ftp_pw, ftp_dir, perm="elradfmw")
        authorizer.add_anonymous(ftp_dir)

        handler = FTPHandler
        handler.authorizer = authorizer    
        handler.banner = "pyftpdlib based ftpd ready."
        
        server = FTPServer((ftp_address, ftp_port), handler)
        server.serve_forever()
    global ftp_process
    ftp_process = multiprocessing.Process(target=__ftp_process__)
    ftp_process.start()
    logger.info("sleeping %s s to ensure ftp server is started" % str(ftp_server_start_timeout))
    time.sleep(ftp_server_start_timeout)

def uninit_ftp():
    if not ftp_process is None and ftp_process.is_alive():
        ftp_process.terminate()

class Test(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        super(Test, cls).setUpClass()
        init_ftp()

    @classmethod
    def tearDownClass(cls):
        uninit_ftp()
        super(Test, cls).tearDownClass()
        
    # not testing
    # - pass different postgresql binaries as arguments
    def test_bootstrap(self): 
        sudo_uid = int(os.getenv("SUDO_UID"))
        if sudo_uid != os.getuid():
            logger.info("recognized sudo invokation, using invoking user's maven cache and uid for user space files and directories")
        postgresql_jdbc_url= "%s/%s" % (ftp_url, bootstrap.postgresql_jdbc_name) 
        postgresql_deb_url= "%s/%s" % (ftp_url, bootstrap.postgresql_deb_name) 
        geotools_url= "%s/%s" % (ftp_url, bootstrap.geotools_src_archive_name) 
        postgis_url= "%s/%s" % (ftp_url, bootstrap.postgis_src_archive_name) 
        jgrapht_url= "%s/%s" % (ftp_url, bootstrap.jgrapht_name) 
        jfuzzy_url= "%s/%s" % (ftp_url, bootstrap.jfuzzy_name) 
        maven_bin_archive_url= "%s/%s" % (ftp_url, bootstrap.maven_bin_archive_name)
        commons_src_archive_url = "%s/%s" % (ftp_url, bootstrap.commons_src_archive_name)
        
        # local unit test
        def __test__(q):
            try:
                os.setuid(sudo_uid)
                os.environ["JAVA_HOME"] = "/usr/java/latest"
                bootstrap_dir = tempfile.mkdtemp()
                postgres_datadir_path = bootstrap.postgres_datadir_path_default
                bootstrap.bootstrap(bootstrap_dir=bootstrap_dir, skip_build=False, postgres_datadir_path=postgres_datadir_path, force_overwrite_postgres_datadir=None, postgresql_jdbc_url=postgresql_jdbc_url, postgresql_deb_url=postgresql_deb_url, geotools_url=geotools_url, postgis_url=postgis_url, jgrapht_url=jgrapht_url, jfuzzy_url=jfuzzy_url, maven_bin_archive_url=maven_bin_archive_url, commons_src_archive_url=commons_src_archive_url)
            except Exception as ex:
                q.put(ex)
        q = multiprocessing.Queue()
        p = multiprocessing.Process(target=__test__, args=(q,))
        p.start()
        p.join()
        if not q.empty():
            raise q.get()
        
        # tests in LXCs
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
            script_base_dir = os.path.realpath(os.path.join(__file__, "..", ".."))
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
            child.sendline("sudo apt-get install %s" % str.join(" ", common_programs))
            child.expect("[#$][\\s]")
            
            # test unset JAVA_HOME
            child.sendline("unset JAVA_HOME")
            child.expect("[#$][\\s]")
            child.sendline("python /home/user/scripts/bootstrap.py")
            child.expect("[#$][\\s]")
            child_output = child.before.strip()
            self.assertTrue("JAVA_HOME is not set" in child_output) # @TODO: check that JAVA_HOME is not set by installation
            # test JAVA_HOME set to inexisting path
            child.sendline("export JAVA_HOME=/some/inexisting/path")      
            child.sendline("python /home/user/scripts/bootstrap.py")
            child.expect("[#$][\\s]")
            child_output = child.after.strip()
            if not re.match(".*JAVA_HOME .* doesn't seem to point to a valid JDK.*", child_output) is None:
                self.fail("output should match 'JAVA_HOME .* doesn't seem to point to a valid JDK'")
            # test success
            child.sendline("export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-%s" % lxc_arch)
            child.expect("[#$][\\s]")
            child.sendline("sudo python /home/user/scripts/bootstrap_prequisites.py") # assume that bootstrap_prequisites.py just works; it is tested elsewhere
            child.expect("[#$][\\s]")            
            child.sendline("python /home/user/scripts/bootstrap.py")
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
            logger.debug("unmounting %s" % (bind_target,))
            sp.check_call([umount, bind_target]) # doesn't really make sense to ignore failure (just messes up the failure output) because removing a mounted directory fails certainly
            
        config_file_path = os.path.realpath(os.path.join(__file__, "..", "bootstrap_test.conf"))
        logger.debug("reading config file %s" % (config_file_path,))
        config = ConfigParser.ConfigParser()
        config.read([config_file_path])
        mirror_debian = config.get("lxc", "mirror.debian")
        mirror_ubuntu = config.get("lxc", "mirror.ubuntu")
        lxc_test.test(lxc_dir=lxc_dir, target=__target__, pre_target=__pre_target__, post_target=__post_target__)

if __name__ == "__main__":
    unittest.main()

