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

# Integration tests in LXC:
# LXCs (Linux containers) are created on demand from remote resources (handled 
# be the script). The containers are snapshoted after the installation and their 
# status is restored after the test on the LXC (currently efficient snapshoting 
# with btrfs and lvm2 doesn't work, so that a plain copy of the container is 
# created with lxc-clone (doing practically the same as an ordinary copy) which 
# should be fast if the copying occurs on a btrfs filesystem (e.g. when 
# lxc_storage is 'btrfs') (LXCs have to be stored in a subdirectory of btrfs 
# root in order to let the snapshots be stored on the btrfs)).

# internal implementation notes:
# - script has to be invoked with sudo not only because of privileges for lxc 
# but also to avoid the necessity to manage sudo password within the script 
# (easier: the script requires lxc which only runs with sudo -> the script has 
# to run with sudo!)

wget = "wget"
apt_get = "apt-get"
lxc_create = "lxc-create"
umount = "umount"
lxc_execute = "lxc-execute"
lxc_snapshot = "lxc-snapshot"
lxc_stop = "lxc-stop"
lxc_start = "lxc-start"
lxc_clone = "lxc-clone"
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
lxc_backingstores = ["dir", "btrfs", "lvm", "overlayfs"] 
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
lxc_dir = os.path.realpath(os.path.join(__file__, "..", "lxc"))
lxc_storages = ["dir", "image"] # store in a simple directory or create a btrfs image and mount it via loop device
lxc_storage = lxc_storages[0]
lxc_storage_btrfs_image = os.path.realpath(os.path.join(__file__, "..", "lxc-btrfs.img"))
lxc_storage_btrfs_image_overwrite = True
skip_apt_update = True

# download all files which would be retrieved remotely to the FTP base directory
def init_ftp():
    if not os.path.exists(ftp_dir):
        os.makedirs(ftp_dir)
    postgresql_jdbc_file = os.path.join(ftp_dir, bootstrap.postgresql_jdbc_name)
    if not bootstrap.check_file(postgresql_jdbc_file, bootstrap.postgresql_jdbc_md5):
        sp.check_call([wget, "-O", postgresql_jdbc_file, bootstrap.postgresql_jdbc_url_default])
    postgresql_deb_file = os.path.join(ftp_dir, bootstrap.postgresql_deb_name)
    if not bootstrap.check_file(postgresql_deb_file, bootstrap.postgresql_deb_md5):
        sp.check_call([wget, "-O", postgresql_deb_file, bootstrap.postgresql_deb_url_default])
    geotools_src_archive_file = os.path.join(ftp_dir, bootstrap.geotools_src_archive_name)
    if not bootstrap.check_file(geotools_src_archive_file, bootstrap.geotools_src_archive_md5):
        sp.check_call([wget, "-O", geotools_src_archive_file, bootstrap.geotools_url_default])
    postgis_src_archive_file = os.path.join(ftp_dir, bootstrap.postgis_src_archive_name)
    if not bootstrap.check_file(postgis_src_archive_file, bootstrap.postgis_src_archive_md5):
        sp.check_call([wget, "-O", postgis_src_archive_file, bootstrap.postgis_url_default])
    commons_src_archive_file = os.path.join(ftp_dir, bootstrap.commons_src_archive_name)
    if not bootstrap.check_file(commons_src_archive_file, bootstrap.commons_src_archive_md5):
        sp.check_call([wget, "-O", commons_src_archive_file, bootstrap.commons_src_archive_url_default])
    jgrapht_file = os.path.join(ftp_dir, bootstrap.jgrapht_name)
    if not bootstrap.check_file(jgrapht_file, bootstrap.jgrapht_md5):
        sp.check_call([wget, "-O", jgrapht_file, bootstrap.jgrapht_url_default])
    jfuzzy_file = os.path.join(ftp_dir, bootstrap.jfuzzy_name)
    if not bootstrap.check_file(jfuzzy_file, bootstrap.jfuzzy_md5):
        sp.check_call([wget, "-O", jfuzzy_file, bootstrap.jfuzzy_url_default])
    maven_bin_archive_file = os.path.join(ftp_dir, bootstrap.maven_bin_archive_name)
    if not bootstrap.check_file(maven_bin_archive_file, bootstrap.maven_bin_archive_md5):
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
        def __test__():
            os.setuid(sudo_uid)
            os.environ["JAVA_HOME"] = "/usr/java/latest"
            bootstrap_dir = tempfile.mkdtemp()
            postgres_datadir_path = bootstrap.postgres_datadir_path_default
            bootstrap.bootstrap(bootstrap_dir=bootstrap_dir, skip_build=False, postgres_datadir_path=postgres_datadir_path, force_overwrite_postgres_datadir=None, privileged_uid=0, postgresql_jdbc_url=postgresql_jdbc_url, postgresql_deb_url=postgresql_deb_url, geotools_url=geotools_url, postgis_url=postgis_url, jgrapht_url=jgrapht_url, jfuzzy_url=jfuzzy_url, maven_bin_archive_url=maven_bin_archive_url, commons_src_archive_url=commons_src_archive_url)
        p = multiprocessing.Process(target=__test__)
        p.start()
        p.join()
        
        # tests in LXCs
        if lxc_backingstore == "btrfs":
            pm_utils.install_packages(["btrfs-tools"], package_manager="apt-get", skip_apt_update=skip_apt_update)
        elif lxc_backingstore == "lvm":
            pm_utils.install_packages(["lvm2"], package_manager="apt-get", skip_apt_update=skip_apt_update)
        elif lxc_backingstore == "dir":
            pass
        else:
            raise ValueError("lxc_backingstore has to be one of %s" % str(lxc_backingstores))
        bootstrap.create_dir(lxc_dir, allow_content=True)
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
        config = ConfigParser.ConfigParser()
        config.read([ os.path.realpath(os.path.join(__file__, "..", "bootstrap_test.conf"))])
        mirror_debian = config.get("lxc", "mirror.debian")
        mirror_ubuntu = config.get("lxc", "mirror.ubuntu")
        #passwd = None
        try:
            lxc_dir_root = os.path.join(lxc_dir, "lxc-root") # create one under btrfs fs root in order to make snapshoting possible (stores snapshots above, next to mount point)
            bootstrap.create_dir(lxc_dir_root, True)
            # handling of apt-get proxy is too complicated and takes away too much flexibility of the default behavior of the script and can be handled with transparent proxy on host anyway
            # not useful to iterate over list of tuples for all systems because too many differences between OSs and distributions (but for different architectures of the same OS or distribution)
            # all debian systems
            for lxc_name,lxc_release,lxc_template,lxc_mirror,lxc_arch in [("debian-wheezy","wheezy", "debian", mirror_debian, "amd64"), ("debian-wheezy", "wheezy", "debian", mirror_debian, "i386")]:
                lxc_name = "%s-%s" % (lxc_name, lxc_arch)
                lxc_dir_target = os.path.join(lxc_dir_root, lxc_name) # doesn't need to be created as long as lxc_dir exists
                if not bootstrap.check_dir(lxc_dir_target):
                    sp.check_call([lxc_create, "-B", lxc_backingstore, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root, "-t", lxc_template, "--", "--arch", lxc_arch, "--release", lxc_release], env={"MIRROR": mirror_debian}) # -t argument is mandatory; "long" names, like bootstrap_test_ubuntu-precise-amd64 are too long
                    # it isn't possible to communicate with the lxc over stdin and subprocess.Popen.communicate -> use pexpect
                    # -> stop the container (stared with lxc-create) before it is started as pexpect.spawn again in order to be able to communicate
                    #sp.check_call([lxc_stop, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root]) # lxc-create doesn't seem to start now...
                    #if passwd is None:
                    #    passwd = getpass.getpass()
                    child = pexpect.spawn(string.join([lxc_start, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root], " "))
                    #child.logfile_read = sys.stdout
                    #child.timeout = 1000
                    child.setecho(False) # makes output faster?
                    #child.expect(":") # sudo password for pexpect.spawn (regardless of whether it has been entered before (sudo -E doesn't work nicely))
                    #child.sendline(passwd)
                    child.expect("login:")
                    child.sendline("root")
                    child.expect("Password:") # login password
                    child.sendline("root")
                    child.expect("[#$]")                    
                    child.sendline("adduser user")
                    child.expect("[pP]assword:") # user password for adduser
                    child.sendline("user") 
                    child.expect("[pP]assword:") # retype password for adduser
                    child.sendline("user") 
                    for i in range(0,5):
                        child.expect(":")
                        child.sendline("")
                    child.expect("\\[\\]")
                    child.sendline("Y")
                    child.expect("[#$]")
                    # install python and sudo
                    child.sendline("apt-get update")
                    child.expect("[#$]")
                    child.sendline("apt-get --assume-yes install sudo python")
                    child.expect("[#$]")
                    child.sendline("adduser user sudo")
                    child.expect("[#$]")
                    child.sendline("shutdown -h 0")
                    child.wait()
                    # creating snapshot (stored as snapX where X is a digit)
                    sp.check_call([lxc_clone, "--lxcpath=%s" % lxc_dir_root, lxc_name, "%s-orig" % lxc_name]) # lxc-clone doesn't work if container is running
            # all ubuntu systems
            lxc_systems = [("ubuntu-precise", "precise", "ubuntu", mirror_ubuntu, "amd64"), ("ubuntu-precise", "precise", "ubuntu", mirror_ubuntu, "i386"), ("ubuntu-trusty","trusty", "ubuntu", mirror_ubuntu, "amd64"), ("ubuntu-trusty","trusty", "ubuntu", mirror_ubuntu, "i386")]
            for lxc_name,lxc_release,lxc_template,lxc_mirror, lxc_arch in lxc_systems:
                lxc_name = "%s-%s" % (lxc_name, lxc_arch)
                lxc_dir_target = os.path.join(lxc_dir_root, lxc_name) # doesn't need to be created as long as lxc_dir exists
                if not bootstrap.check_dir(lxc_dir_target):
                    sp.check_call([lxc_create, "-B", lxc_backingstore, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root, "-t", lxc_template, "--", "--arch", lxc_arch, "--release", lxc_release], env={"MIRROR": lxc_mirror})
                        # -t argument is mandatory; "long" names, like bootstrap_test_ubuntu-precise-amd64 are too long
                        # architecture is specified by arguments passed to template
                    # it isn't possible to communicate with the lxc over stdin and subprocess.Popen.communicate -> use pexpect
                    # -> stop the container (stared with lxc-create) before it is started as pexpect.spawn again in order to be able to communicate
                    #sp.check_call([lxc_stop, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root]) # lxc-create doesn't seem to start now...
                    #if passwd is None:
                    #    passwd = getpass.getpass()
                    child = pexpect.spawn(string.join([lxc_start, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root], " "))
                    child.logfile = sys.stdout
                    child.timeout = 1000
                    child.setecho(True) # makes output faster?
                    #child.expect(":") # sudo password for pexpect.spawn (regardless of whether it has been entered before (sudo -E doesn't work nicely))
                    #child.sendline(passwd)
                    child.expect("login:")
                    child.sendline("ubuntu")
                    child.expect(":") # login password
                    child.sendline("ubuntu")
                    child.expect("[#$]")
                    # initial login as root not possible, but sudo is installed
                    child.sendline("sudo adduser user")
                    child.expect(":") # sudo password for adduser
                    child.sendline("ubuntu")
                    child.expect("[pP]assword:") # user password for adduser
                    child.sendline("user") 
                    child.expect("[pP]assword:") # retype password for adduser
                    child.sendline("user") 
                    for i in range(0,5):
                        child.expect(":")
                        child.sendline("")
                    child.expect("\\[\\]")
                    child.sendline("Y")
                    child.expect("[#$]")
                    # install python and sudo
                    child.sendline("sudo apt-get update")
                    child.expect("[#$]")
                    child.sendline("sudo apt-get --assume-yes install sudo python")
                    i = child.expect(["[#$]", "Password:"])
                    if i == 1:
                       child.sendline("user")
                       child.expect("[#$]")
                    child.sendline("sudo adduser user sudo")
                    child.expect("[#$]")
                    child.sendline("sudo shutdown -h 0")
                    child.wait()
                    # creating snapshot (stored as snapX where X is a digit)
                    #try:
                    #    # still not sure whether container is started or not and what causes IO error
                    #    sp.check_call([lxc_stop, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root])
                    #except Exception:
                    #    pass
                    sp.check_call([lxc_clone, "--lxcpath=%s" % lxc_dir_root, lxc_name, "%s-orig" % lxc_name]) # lxc-clone doesn't work if container is running
                
            # testing (initial setup provides user 'user' with password 'user' (sudoer) which is used to test bootstrap.py)
            for lxc_name,lxc_release,lxc_template,lxc_mirror, lxc_arch in lxc_systems:
                # simple sync with lxc doesn't seem to work (maybe due to different inode management) -> create a copy of scripts directory (in order to not mess up the sources) and mount-bind that directory (simple rsync both before and after boot  causes the dissappearence of file after some instants)
                lxc_name = "%s-%s" % (lxc_name, lxc_arch)
                lxc_dir_target = os.path.join(lxc_dir_root, lxc_name)
                script_copy_dir = os.path.join(lxc_dir_root, "scripts-copy/") # trailing slash for rsync
                if not os.path.exists(script_copy_dir):
                    os.makedirs(script_copy_dir)
                sp.check_call([rsync, "-a", "--progress", "--exclude", "tests", 
                  os.path.join(os.path.realpath(os.path.join(__file__, "..")), "../"), # trailing slash for rsync 
                  script_copy_dir]
                )
                  # can't handle permissions with shutil.copyX
                  # only copy scripts subdirectory (as it should work independently from source root)
                  # exclude tests in order to avoid endless recursion with copies into source (specification of absolute exclusions doesn't work -> specify relatively)
                  # always copy, not only at installations, in order update changes
                  # only os.path.realpath allows to resolve /path/to/file.ext/.. (os.path.join doesn't), os.path.realpath removes trailing '/' (os.path.join doesn't) -> use os.path.realpath to resolve the direct parent of __file__ and use os.path.join to ensure trailing '/' (trailing / is necessary for rsync to work correctly)
                bind_target = os.path.join(lxc_dir_root, lxc_name, "rootfs", "home/user/scripts/")
                if not os.path.exists(bind_target):
                    os.makedirs(bind_target)
                sp.check_call([mount, "-o", "rw,bind", script_copy_dir, bind_target]) # has to unmounted before the original is restored below
                try:
                    #if passwd is None:
                    #    passwd = getpass.getpass()
                    child = pexpect.spawn(string.join([lxc_start, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root], " "))
                    #child.logfile_read = sys.stdout
                    child.timeout = 1000
                    child.setecho(True) # facilitates retrieval of return code below ??
                    #child.expect(":") # sudo password for pexpect.spawn (regardless of whether it has been entered before (sudo -E doesn't work nicely))
                    #child.sendline(passwd)
                    child.expect("login:")
                    child.sendline("user")
                    child.expect(":") # login password
                    child.sendline("user")
                    child.expect("[#$][\\s]") # login successful; need to expect [#$][\\s] because '#' might be contained optionally in uname output of some systems (e.g. Debian), but not of others (e.g. Ubuntu)
                    
                    # test unset JAVA_HOME
                    child.sendline("unset JAVA_HOME")
                    child.sendline("python /home/user/scripts/bootstrap.py")
                    i = child.expect(["[#$][\\s]", "Password:"]) # bootstrap.py might require sudo password
                    if i == 1:
                        child.sendline("user")
                        child.expect("[#$][\\s]")
                    child_output = child.before.strip()
                    self.assertTrue("JAVA_HOME is not set" in child_output) # @TODO: check that JAVA_HOME is not set by installation
                                  
                    child.sendline("export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-%s" % lxc_arch)      
                    # test success
                    child.sendline("python /home/user/scripts/bootstrap.py")
                    i = child.expect(["[#$][\\s]", "Password:"]) # bootstrap.py might require sudo password @TODO: NO, it must not!!
                    if i == 1:
                        child.sendline("user")
                        child.expect("[#$][\\s]")
                    child.sendline("echo $?")
                    child.expect("[0-9]+")
                    child_output = child.after.strip()
                    child.expect("[#$][\\s]") # after parsing the return of echo $? the prompt has to be expected
                    self.assertTrue(0 == int(child_output))                    
                    child.sendline("sudo shutdown -h 0")
                    child.expect(":") # sudo password for shutdown
                    child.sendline("user")        
                    child.wait()
                finally:
                    # restoring snapshot state
                    try:
                        sp.check_call([lxc_stop, "--name=%s" % lxc_name, "--lxcpath=%s" % lxc_dir_root])
                    except Exception:
                        pass
                    sp.check_call([umount, bind_target]) # doesn't really make sense to ignore failure (just messes up the failure output) because removing a mounted directory fails certainly
                    sp.check_call([rm, "-R", os.path.join(lxc_dir_root, lxc_name)])
                    sp.check_call([cp, "-a", os.path.join(lxc_dir_root, "%s-orig" % lxc_name), os.path.join(lxc_dir_root, lxc_name)])
        finally:
            if lxc_storage == "image":
                sp.check_call([umount, lxc_dir])
                sparse_file_utils.detach_loop_device(lxc_storage_btrfs_image)

def check_ip(ip_string):
    try:
        socket.inet_aton(ip)
        return True
    except socket.error:
        return False

if __name__ == "__main__":
    unittest.main()
    

