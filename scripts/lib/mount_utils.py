#!/usr/bin/python
# coding: utf-8

import argparse
import subprocess as sp
import os

# binaries
mount = "mount"
sudo = "sudo"
bash = "dash"
ifconfig = "ifconfig"
losetup = "losetup"
partprobe = "partprobe"
btrfs = "btrfs"

IMAGE_MODE_PT="partition-table"
IMAGE_MODE_FS="file-system"
image_modes = [IMAGE_MODE_PT, IMAGE_MODE_FS]

SOURCES = "sources"
# asserts that the corresponding NFS is mounted (includes checking and lazily 
# initializing (including driver installation) of required network interface 
# (might differ for access to NAS which might be resticted to certain LANs))
MOUNT_CHECK_NFS=1
# checks that the programs for an NFS client are installed and working
MOUNT_CHECK_NFS_INSTALL=2
MOUNT_CHECK_ALL = MOUNT_CHECK_NFS+MOUNT_CHECK_NFS_INSTALL

skip_apt_update_default = False

def mount_prequisites():
    return mount_prequisites_software() and mount_prequisites_network()

# @return True if packages have been installed
def mount_prequisites_software(skip_apt_update=skip_apt_update_default):
    if not pm_utils.dpkg_check_package_installed("nfs-common"):
        pm_utils.install_packages(["nfs-common"], skip_apt_update=skip_apt_update)
        return True
    return False

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

def mount_sparse_file(nfs_mount_source, nfs_mount_target, image_file, image_mount_target, image_mode=IMAGE_MODE_FS):
    lazy_mount(nfs_mount_source, nfs_mount_target, "nfs") # handles inexistant target

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

# a wrapper around losetup, finding the next free loop device and returning it
# @return the loop device which has been found and setup up for the image file
def losetup_wrapper(file):
    try:
        loop_dev = sp.check_output([losetup, "-f"]).strip()
    except sp.CalledProcessError as ex:
        raise RuntimeError("no free loop device")
    sp.check_call([losetup, loop_dev, file])
    return loop_dev

# checks if <tt>source</tt> is already mounted under <tt>target</tt> and skips (if it is) or mounts <tt>source</tt> under <tt>target</tt>
def lazy_mount(source, target, fs_type):
    mount_lines = open("/proc/mounts", "r").readlines()
    for mount_line in mount_lines:
        mount_line_split = mount_line.split(" ")
        source0 = mount_line_split[0]
        target0 = mount_line_split[1]
        if source0 == source and target0 == target:
            return
    if not os.path.exists(target):
        if os.path.isfile(source):
            os.mknod(target, 755)
        else:
            os.makedirs(target)    
    sp.check_call([sudo, mount, "-t", fs_type, source, target])

if __name__ == "__main__":
    predefined = SOURCES
    if predefined == SOURCES:
        mount_sources_image()
    else:
        raise ValueError("predefined mount behavior %s not supported" % predefined)

