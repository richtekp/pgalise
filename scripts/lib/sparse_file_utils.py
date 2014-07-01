#!/usr/bin/python

# mounts NFS volume $1 under $2 and then mounts the btrfs file image $3 (using the next free block device) under $4 (the first and only partition) 
# 
# This doesn't handle the occupation of loop devices, they will be freed at shutdown

import subprocess as sp
import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)
import os
import string
import pexpect
import sys
sys.path.append(os.path.realpath(os.path.join(__file__, "..")))
import mount_utils

truncate = "truncate"
mount = "mount"
losetup = "losetup"
partprobe = "partprobe"
sudo = "sudo"
mkfs_btrfs = "mkfs.btrfs"
sfdisk = "sfdisk"
gdisk = "gdisk"
umount = "umount"

def create_sparse_file(file_path, size_in_bytes):
    sp.check_call([truncate, "-s", str(size_in_bytes), file_path])

def lazy_mount_sparse_file(image_file_path, mount_target, fs_type="btrfs", force=False):
    free_loop = sp.check_output([sudo, losetup, "-f"]).strip().decode("utf-8")
    if not os.path.exists(mount_target):
        os.makedirs(mount_target)
    sp.check_call([sudo, losetup, free_loop, image_file_path])
    if sp.call([sudo, partprobe, free_loop]) != 0: # partprobe failed if partition table is missing
        if not force:
            raise Exception("mounting %s failed, partition won't be overwritten as force is False" % image_file_path)
        else:
            logger.warn("mounting %s failed, creating btrfs partition" % image_file_path)
            if sp.call([sudo, sfdisk, "-V", free_loop]) != 0:
                # assume no partition table
                p = sp.Popen([sudo, gdisk, free_loop],stdin=sp.PIPE)
                p.communicate(input="q")
            sp.check_call([sudo, mkfs_btrfs, free_loop])
            sp.check_call([sudo, partprobe, free_loop])
    mount_utils.lazy_mount(free_loop, mount_target, fs_type)

def detach_loop_device(image_file_path):
    logger.debug("checking loop device of image file %s" % image_file_path)
    loop_device_lines = sp.check_output([sudo, losetup, "-j", image_file_path]).split("\n")
    for loop_device_line in loop_device_lines:
        loop_device = loop_device_line.split(":")[0]
        if loop_device == "":
            continue
        logger.debug("detaching loop device %s" % loop_device)
        sp.check_call([sudo, losetup, "-d", loop_device])   

