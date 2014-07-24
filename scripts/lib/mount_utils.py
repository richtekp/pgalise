#!/usr/bin/python
# coding: utf-8

import argparse
import subprocess as sp
import os
import sys
import re
import logging

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

try:
    import file_line_utils
    import pm_utils
except ImportError as ex:
    logger.error("Import of modules failed (see following exception for details). Make sure the containing directory is in the PYTHONPATH variable and that it is passed correctly during environment changes (e.g. sudo)")
    raise ex

# binaries
mount = "mount"
bash = "dash"
ifconfig = "ifconfig"
losetup = "losetup"
partprobe = "partprobe"
btrfs = "btrfs"
umount = "umount"

IMAGE_MODE_PT="partition-table"
IMAGE_MODE_FS="file-system"
image_modes = [IMAGE_MODE_PT, IMAGE_MODE_FS]

# asserts that the corresponding NFS is mounted (includes checking and lazily 
# initializing (including driver installation) of required network interface 
# (might differ for access to NAS which might be resticted to certain LANs))
MOUNT_CHECK_NFS=1
# checks that the programs for an NFS client are installed and working
MOUNT_CHECK_NFS_INSTALL=2
MOUNT_CHECK_ALL = MOUNT_CHECK_NFS+MOUNT_CHECK_NFS_INSTALL

skip_apt_update_default = False

MOUNT_MODE_NFS = 1
MOUNT_MODE_CIFS = 2
mount_mode_default = MOUNT_MODE_CIFS

# a wrapper around <tt>mount_sparse_file</tt> and different remote mount methods (NFS, cifs, etc.) (sparse file support is horrible for all of them...) of Synology DSM 5.0 (path specifications, etc.)
# @args credentials_file if not <code>None</code> the credentials option will be passed to the <tt>mount</tt> command, otherwise the <tt>username</tt> option with value <tt>richter</tt> will be passed to the <tt>mount</tt> command which will request the password from input at a prompt
def mount_dsm_sparse_file(shared_folder_name, image_mount_target, network_mount_target, image_file_name, remote_host, mount_mode=mount_mode_default, credentials_file=None):
    if mount_mode == MOUNT_MODE_NFS:
        lazy_mount(source="%s:/volume1/%s" % (remote_host, shared_folder_name), target=network_mount_target, fs_type="nfs", options_str="nfsvers=4") 
                # handles inexistant target
                # omitting nfsvers=4 causes 'mount.nfs: requested NFS version or transport protocol is not supported' (not clear with which protocol this non-sense error message refers to)
    elif mount_mode == MOUNT_MODE_CIFS:
        if credentials_file is None:
            options_str="username=richter,rw,uid=1000,gid=1000"
        else:
            if not os.path.exists(credentials_file):
                raise ValueError("credentials_file '%s' doesn't exist" % (credentials_file,))
            options_str="credentials=%s,rw,uid=1000,gid=1000" % credentials_file
        lazy_mount(source="//%s/%s" % (remote_host, shared_folder_name), target=network_mount_target, fs_type="cifs", options_str=options_str) # handles inexistant target
    else:
        raise ValueError("mount_mode '%s' not supported" % (mount_mode,))
    mount_sparse_file(
        image_file=os.path.join(network_mount_target, image_file_name), 
        image_mount_target=image_mount_target, 
        image_mode=IMAGE_MODE_FS
    )

def mount_prequisites():
    return mount_prequisites_software() and mount_prequisites_network()

# @return True if packages have been installed
def mount_prequisites_software(skip_apt_update=skip_apt_update_default):
    installed = False
    if not pm_utils.dpkg_check_package_installed("nfs-common"):
        pm_utils.install_packages(["nfs-common"], skip_apt_update=skip_apt_update)
        installed = True
    if not pm_utils.dpkg_check_package_installed("cifs-utils"):
        pm_utils.install_packages(["cifs-utils"], skip_apt_update=skip_apt_update)
        installed = True
    return installed

# as the host diskstation will always be connected through either LAN or WLAN 
# (i.e. maybe at some point), it will receive an appropriate IP of the 
# corresponding (separated) subnet. Accessing diskstation will always occur on 
# the correct subnet. Therefore both interfaces can be up when accessing 
# diskstation.
# diskstation will refuse connections from WLAN, but as its IP is resolved to 
# a host in the LAN subnet this shouldn't cause trouble as packages would never 
# be send over the interface of the WLAN interface.
# @args assure_own_ip if != None the function checks that the invoking host has 
# the specified IP and ...
# @args own_ip_if ... setups the interface <tt>own_ip_if</tt> if not
# @return True if network has been set up
def mount_prequisites_network(assure_own_ip="192.168.178.62", own_ip_if="eth1"):
    ifconfig_output = sp.check_output([ifconfig])
    if not assure_own_ip in ifconfig_output:
        raise RuntimeError("@TODO: implement")
        return True
    return False

# should be able to handle both NFS and CIFS specifications as mount_source
def mount_sparse_file(image_file, image_mount_target, image_mode=IMAGE_MODE_FS):
    image_file_loop_dev = losetup_wrapper(image_file)
    if image_mode == IMAGE_MODE_PT:
        sp.check_call([partprobe, image_file_loop_dev])
        lazy_mount("%sp1" % image_file_loop_dev, image_mount_target, "btrfs")
        sp.check_call([btrfs, "device", "scan", "%sp1" % image_file_loop_dev]) # scan fails if an image with a partition table is mounted at the loop device -> scan partitions 
    elif image_mode == IMAGE_MODE_FS:
        lazy_mount(image_file_loop_dev, image_mount_target, "btrfs")
        sp.check_call([btrfs, "device", "scan", image_file_loop_dev]) # do this always as it doesn't fail if not btrfs image 
        # has been mounted and doesn't take a lot of time -> no need to add 
        # a parameter to distungish between btrfs and others (would be more 
        # elegant though)
    else:
        raise ValueError("image_mode has to be one of %s, but is %s" % (str(image_modes), image_mode))

# @args mount_target the mount point of the image (loopback device will be 
# determined automatically and teared down)
def unmount_sparse_file(mount_target):
    mount_source = get_mount_source(mount_target)
    if mount_source is None:
        raise ValueError("mount_target '%s' isn't using a loop device" % (mount_target,))
    logger.info("mount_target '%s' was using loop device '%s'" % (mount_target, mount_source))
    sp.check_call([umount, mount_target])
    sp.check_call([losetup, "-d", mount_source])

def get_mount_source(mount_target):
    for mount_source, mount_target0 in [tuple(re.split("[\\s]+", x)[0:2]) for x in file_line_utils.file_lines("/proc/mounts", comment_symbol="#")]:
        if mount_target0 == mount_target:
            return mount_source
    return None

# a wrapper around losetup, finding the next free loop device and returning it
# @return the loop device which has been found and setup up for the image file
def losetup_wrapper(file):
    try:
        loop_dev = sp.check_output([losetup, "-f"]).strip()
    except sp.CalledProcessError as ex:
        raise RuntimeError("no free loop device")
    sp.check_call([losetup, loop_dev, file])
    return loop_dev

# @return <code>True</code> iff source is mounted under target
def check_mounted(source, target):
    mount_lines = open("/proc/mounts", "r").readlines()
    for mount_line in mount_lines:
        mount_line_split = mount_line.split(" ")
        target0 = mount_line_split[1]
        if target0 == target: # don't check equality of source with (1st column of) mount output because multiple usage of mount target isn't possible and therefore the check should already succeed if the mount target is used by a (possibly other) mount source
            return True
    return False

# checks if <tt>source</tt> is already mounted under <tt>target</tt> and skips (if it is) or mounts <tt>source</tt> under <tt>target</tt>
def lazy_mount(source, target, fs_type, options_str=None):
    if check_mounted(source, target):
        return
    if not os.path.exists(target):
        if os.path.isfile(source):
            os.mknod(target, 755)
        else:
            os.makedirs(target)
    cmds = [mount, "-t", fs_type,]
    if not options_str is None and options_str != "":
        cmds += ["-o", options_str]
    cmds += [ source, target]
    sp.check_call(cmds)

