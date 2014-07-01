#!/usr/bin/python
# -*- coding: UTF-8 -*-

################################################################################
# apt proxy
################################################################################
# Handling of apt proxy with <tt>MIRROR</tt> variable in /etc/default/lxc 
# doesn't make sense because it only points to a mirror for one distribution, 
# passing mirror environment variable to <tt>lxc-create</tt> work before, but 
# no longer does (seems to be undocumented and maybe an internally used 
# workaround -> add config file to /etc/apt/apt.conf.d manually for each lxc
# 

import os
import file_utils
import pm_utils
import logging
import subprocess as sp
import pexpect
import sys
import shutil

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

lxc_username_default = "user"
lxc_user_password_default = "user"
LXC_BACKINGSTORE_DIR = "dir"
LXC_BACKINGSTORE_BTRFS = "btrfs"
LXC_BACKINGSTORE_LVM = "lvm"
LXC_BACKINGSTORE_OVERLAYFS = "overlayfs"
lxc_backingstores = [LXC_BACKINGSTORE_DIR, LXC_BACKINGSTORE_BTRFS, LXC_BACKINGSTORE_LVM, LXC_BACKINGSTORE_OVERLAYFS] 
  # btrfs (means that the btrfs capabilities of the underlying filesystem are used) seems to be buggy, see https://github.com/lxc/lxc/issues/131, doesn't work when specifying both the root of the btrfs image and a subfolder
  # 
  # lvm doesn't work:
  # File descriptor 3 (/mnt/DATA/richter/DropBox/stratosphere/pgalise/scripts/tests/lxc/debian-wheezy-amd64/partial) leaked on lvcreate invocation. Parent PID 10702: lxc-create
  # Volume group "lxc" not found
  # lxc_container: Error creating new lvm blockdev /dev/lxc/debian-wheezy-amd64 size 1073741824 bytes
  # lxc_container: Failed to create backing store type lvm
  # lxc_container: Error creating backing store type lvm for debian-wheezy-amd64
  # lxc_container: Error creating container debian-wheezy-amd64
  # -> might be fixable (if btrfs doesn't work definitely)
  # 
  # overlayfs doesn't work
  # lxc_container: No such device - overlayfs: error mounting /tmp/xxx/debian-wheezy-amdaaa/rootfs onto /usr/lib/x86_64-linux-gnu/lxc options upperdir=/tmp/xxxsnaps/debian-wheezy-amdaaa/snap0/delta0,lowerdir=/tmp/xxx/debian-wheezy-amdaaa/rootfs
  # lxc_container: clone of /tmp/xxx:debian-wheezy-amdaaa failed
  # lxc_container: Error creating a snapshot
lxc_backingstore = lxc_backingstores[0]
lxc_storages = ["dir", "image"] # store in a simple directory or create a btrfs image and mount it via loop device
lxc_storage = lxc_storages[0]
lxc_storage_btrfs_image = os.path.realpath(os.path.join(__file__, "..", "lxc-btrfs.img"))
lxc_storage_btrfs_image_overwrite = True
skip_apt_update = True
# the time the lxc has to shutdown if soft shutdown is tried after an exception occured
lxc_pexpect_exception_shutdown_delay = 3

# binaries
lxc_create = "lxc-create"
lxc_execute = "lxc-execute"
lxc_snapshot = "lxc-snapshot"
lxc_stop = "lxc-stop"
lxc_start = "lxc-start"
umount = "umount"
rsync = "rsync"

common_essential_packages = ["sudo"]

# sudo will be installed in container and user will be made a sudoer in order to facilitate the installation of programs in <tt>target</tt>
# @args lxc_dir the directory above the root where lxc containers are stored 
# @args pre_target a <tt>callable</tt> invoked before <tt>target</tt>, invoked with <tt>lxc_spec</tt> argument
# @args post_target invoked after <tt>target</tt> before the restoring of the initial container state, invoked with <tt>lxc_spec</tt> argument
# @args target the actual test method, invoked with the <tt>pexpect.spawn</tt> instance of each container
# @args username create a user on each LXC with <tt>username</tt>, <code>None</code> indicates skip user creation, <tt>user_password, user_uid and user_gid are silently ignored in this case
# @args user_password 
# @args user_uid the numerical id of the user to be created, currently the script simply fails if the initial setup of the LXC already uses it, <code>None</code> indicates using the next free id
# @args user_gid intentionally the save as <tt>user_uid</tt>
# @args login if <code>True</code> login into each LXC before target is invoked (the first prompt after the login has already been expected)
# @args mirror_debian currently silently ignored, see introduction
# @args mirror_ubuntu currently silently ignored, see introduction
# @args apt_proxy the URL to reference the apt-cacher-ng instance, <code>None</code> indicates to skip the setup of usage of apt-cacher-ng, example: http://richter-local.de:3142
def test(target, lxc_dir, pre_target=None, post_target=None, lxc_username=lxc_username_default, lxc_user_password=lxc_user_password_default, lxc_user_uid=None, lxc_user_gid=None, lxc_login=True, lxc_backingstore=LXC_BACKINGSTORE_DIR, mirror_debian="http://localhost:3142/ftp.de.debian.org/debian", mirror_ubuntu="http://localhost:3142/archive.ubuntu.com/ubuntu", apt_proxy=None):
    if target is None:
        raise ValueError("target mustn't be None")

    if lxc_backingstore == "btrfs":
        pm_utils.install_packages(["btrfs-tools"], package_manager="apt-get", skip_apt_update=skip_apt_update)
    elif lxc_backingstore == "lvm":
        pm_utils.install_packages(["lvm2"], package_manager="apt-get", skip_apt_update=skip_apt_update)
    elif lxc_backingstore == "dir":
        pass
    else:
        raise ValueError("lxc_backingstore has to be one of %s" % str(lxc_backingstores))
    file_utils.create_dir(lxc_dir, allow_content=True)
    if lxc_storage == "image":
        pm_utils.install_packages(["btrfs-tools"], package_manager="apt-get")
        if not os.path.exists(lxc_storage_btrfs_image):
            sparse_file_utils.create_sparse_file(lxc_storage_btrfs_image, 107374182400) # 100 GiB
        sparse_file_utils.lazy_mount_sparse_file(lxc_storage_btrfs_image, lxc_dir, fs_type="btrfs", force=lxc_storage_btrfs_image_overwrite)
    elif lxc_storage == "dir":
        pass
    else:
        raise ValueError("lxc_storage has to be one of %s" % str(lxc_storages))
    pm_utils.install_packages(["lxc", "lxc-templates", 
        "debootstrap", # should be a dependency, but isn't in Ubuntu trusty
    ], package_manager="apt-get", skip_apt_update=skip_apt_update)
    try:
        # handling of apt-get proxy is too complicated and takes away too much flexibility of the default behavior of the script and can be handled with transparent proxy on host anyway
        # not useful to iterate over list of tuples for all systems because too many differences between OSs and distributions (but for different architectures of the same OS or distribution)
        
        # using shutil.copytree doesn't work with lxc root directories (hangs, reported as http://bugs.python.org/issue21885), therefore `rsync -a` is used as a workaround
        def __initial_snapshot__(lxc_dir, lxc_spec):
            # creating snapshot (stored as snapX where X is a digit) (use plain file copy, see internal implementation notes above)
            snapshot_src_path = os.path.join(lxc_dir, lxc_spec)
            snapshot_target_path = os.path.join(lxc_dir, "%s-orig" % lxc_spec)
            logger.debug("copying\n\t%s\nto\n\t%s" % (snapshot_src_path, snapshot_target_path))
            if not os.path.exists(snapshot_target_path):
                os.makedirs(snapshot_target_path)
            sp.check_call([rsync, "-a", snapshot_src_path.rstrip("/")+"/", snapshot_target_path.rstrip("/")+"/"])
        def __handle_exception_in_lxc__(ex, lxc_spec, lxc_dir, child):
            # explicit stopping is only necessary if exception occured, this has to be as soft as possible though in order to avoid errors like 'lxc-start: Executing '/sbin/init' with no configuration file may crash the host'
            logger.error("exception occured (see details below)") # inform why container is stopped before it is stopped
            if not child is None:
                child.timeout = lxc_pexpect_exception_shutdown_delay
                try:
                    child.expect(pexpect.eof) # don't use wait as is blocks for ever, expect will time out
                except:
                    logger.debug("soft shutdown of lxc %s after exception failed, forcing shutdown with lxc-stop" % (lxc_spec,))
                    sp.check_call([lxc_stop, "--name=%s" % lxc_spec, "--lxcpath=%s" % lxc_dir]) # this is complex to cover, earase lxc directory if this happens, it shouldn't though and is therefore ok to not be covered
            raise RuntimeError("exception '%s' occured during setup of lxc %s" % (str(ex), lxc_spec))
    
        # all debian systems                
        for lxc_name,lxc_release,lxc_template,lxc_mirror,lxc_arch in [("debian-wheezy","wheezy", "debian", mirror_debian, "amd64"), ("debian-wheezy", "wheezy", "debian", mirror_debian, "i386")]:
            lxc_spec = "%s-%s" % (lxc_name, lxc_arch)
            lxc_dir_target = os.path.join(lxc_dir, lxc_spec) # doesn't need to be created as long as lxc_dir exists
            if not file_utils.check_dir(lxc_dir_target):
                try:
                    sp.check_call([lxc_create, "-B", lxc_backingstore, "--name=%s" % lxc_spec, "--lxcpath=%s" % lxc_dir, "-t", lxc_template, "--", "--arch", lxc_arch, "--release", lxc_release], env={"MIRROR": mirror_debian}) # -t argument is mandatory; "long" names, like bootstrap_test_ubuntu-precise-amd64 are too long
                    # it isn't possible to communicate with the lxc over stdin and subprocess.Popen.communicate -> use pexpect
                    child = pexpect.spawn(str.join(" ", [lxc_start, "--name=%s" % lxc_spec, "--lxcpath=%s" % lxc_dir]))
                    child.logfile = sys.stdout
                    child.timeout = 1000
                    child.expect("login:")
                    child.sendline("root")
                    child.expect("Password:") # login password
                    child.sendline("root")
                    child.expect("[#$]")                    
                    child.sendline("adduser %s" % (lxc_username,))
                    child.expect("[pP]assword:") # user password for adduser
                    child.sendline(lxc_user_password) 
                    child.expect("[pP]assword:") # retype password for adduser
                    child.sendline(lxc_user_password) 
                    for i in range(0,5):
                        child.expect(":")
                        child.sendline("")
                    child.expect("\\[\\]")
                    child.sendline("Y")
                    child.expect("[#$]")
                    if not apt_proxy is None:
                        # setup apt-cacher-ng (see introduction comments above)
                        child.sendline("echo 'Acquire::http { Proxy \"%s\"; };\nAcquire::https { Proxy \"%s\"; };' > /etc/apt/apt.conf.d/01proxy" % (apt_proxy, apt_proxy))
                        child.expect("[#$]")
                    # install python and sudo
                    child.sendline("apt-get update")
                    child.expect("[#$]")
                    child.sendline("apt-get --assume-yes install %s" % (str.join(" ", common_essential_packages),))
                    child.expect("[#$]")
                    child.sendline("adduser %s sudo" % (lxc_username,))
                    child.expect("[#$]")
                    child.sendline("halt")
                    child.wait()
                    __initial_snapshot__(lxc_dir, lxc_spec)
                except Exception as ex:
                    __handle_exception_in_lxc__(ex, lxc_spec, lxc_dir, child)
        # all ubuntu systems
        lxc_systems = [("ubuntu-precise", "precise", "ubuntu", mirror_ubuntu, "amd64"), ("ubuntu-precise", "precise", "ubuntu", mirror_ubuntu, "i386"), ("ubuntu-trusty","trusty", "ubuntu", mirror_ubuntu, "amd64"), ("ubuntu-trusty","trusty", "ubuntu", mirror_ubuntu, "i386")]
        for lxc_name,lxc_release,lxc_template,lxc_mirror, lxc_arch in lxc_systems:
            lxc_spec = "%s-%s" % (lxc_name, lxc_arch)
            lxc_dir_target = os.path.join(lxc_dir, lxc_spec) # doesn't need to be created as long as lxc_dir exists
            if not file_utils.check_dir(lxc_dir_target):
                try:
                    sp.check_call([lxc_create, "-B", lxc_backingstore, "--name=%s" % lxc_spec, "--lxcpath=%s" % lxc_dir, "-t", lxc_template, "--", "--arch", lxc_arch, "--release", lxc_release], env={"MIRROR": lxc_mirror})
                        # -t argument is mandatory; "long" names, like bootstrap_test_ubuntu-precise-amd64 are too long
                        # architecture is specified by arguments passed to template
                    # it isn't possible to communicate with the lxc over stdin and subprocess.Popen.communicate -> use pexpect
                    child = pexpect.spawn(str.join(" ", [lxc_start, "--name=%s" % lxc_spec, "--lxcpath=%s" % lxc_dir]))
                    child.logfile = sys.stdout
                    child.timeout = 1000
                    child.expect("login:")
                    child.sendline("ubuntu")
                    child.expect(":") # login password
                    child.sendline("ubuntu")
                    child.expect("[#$]")
                    # initial login as root not possible, but sudo is installed
                    child.sendline("sudo adduser %s" % (lxc_username,))
                    child.expect(":") # sudo password for adduser
                    child.sendline("ubuntu")
                    child.expect("[pP]assword:") # user password for adduser
                    child.sendline(lxc_user_password) 
                    child.expect("[pP]assword:") # retype password for adduser
                    child.sendline(lxc_user_password) 
                    for i in range(0,5):
                        child.expect(":")
                        child.sendline("")
                    child.expect("\\[\\]")
                    child.sendline("Y")
                    child.expect("[#$]")
                    if not apt_proxy is None:
                        # setup apt-cacher-ng (see introduction comments above)
                        child.sendline("echo 'Acquire::http { Proxy \"%s\"; };\nAcquire::https { Proxy \"%s\"; };\n' | sudo tee /etc/apt/apt.conf.d/01proxy" % (apt_proxy, apt_proxy))
                        child.expect("[#$]")
                    # install python and sudo
                    child.sendline("sudo apt-get update")
                    child.expect("[#$]")
                    child.sendline("sudo apt-get --assume-yes install %s" % (str.join(" ", common_essential_packages),))
                    i = child.expect(["[#$]", "Password:"])
                    if i == 1:
                       child.sendline(lxc_user_password)
                       child.expect("[#$]")
                    child.sendline("sudo adduser %s sudo" % (lxc_username,))
                    child.expect("[#$]")
                    child.sendline("sudo halt")
                    child.wait()
                    __initial_snapshot__(lxc_dir, lxc_spec)
                except Exception as ex:
                    __handle_exception_in_lxc__(ex, lxc_spec, lxc_dir, child)
            
        # testing (initial setup provides user 'user' with password 'user' (sudoer) which is used to test bootstrap.py)
        for lxc_name,lxc_release,lxc_template,lxc_mirror, lxc_arch in lxc_systems:
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
            lxc_spec = "%s-%s" % (lxc_name, lxc_arch)
            if not pre_target is None:
                pre_target(lxc_spec)
            try:
                child = pexpect.spawn(str.join(" ", [lxc_start, "--name=%s" % lxc_spec, "--lxcpath=%s" % lxc_dir]))
                child.logfile = sys.stdout
                child.timeout = 1000
                if lxc_login:
                    child.expect("login:")
                    child.sendline(lxc_username)
                    child.expect("[pP]assword:") # login password
                    child.sendline(lxc_user_password)
                    child.expect("[#$][\\s]") # login successful; need to expect [#$][\\s] because '#' might be contained optionally in uname output of some systems (e.g. Debian), but not of others (e.g. Ubuntu)     
                target(child)
            except Exception as ex:
                __handle_exception_in_lxc__(ex, lxc_spec, lxc_dir, child)
            finally:
                if not post_target is None:
                    post_target(lxc_spec)
                # restoring snapshot state
                snapshot_src_path = os.path.join(lxc_dir, lxc_spec)
                snapshot_target_path = os.path.join(lxc_dir, "%s-orig" % lxc_spec)
                shutil.rmtree(snapshot_src_path)
                sp.check_call([rsync, "-a", snapshot_target_path, snapshot_src_path]) # use rsync as workaround for issue described above
    finally:
        if lxc_storage == "image":
            sp.check_call([umount, lxc_dir])
            sparse_file_utils.detach_loop_device(lxc_storage_btrfs_image)
# internal implementation notes:
# - specifying a filesystem root as lxc_dir caused trouble before, but this might be related to another error (therefore a subdirectory was generally created under lxc_dir)

