#!/usr/bin/python
# -*- coding: utf-8 -*- 

import os
import shutil
import subprocess as sp
import logging
import re

logger = logging.getLogger("file_utils")
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

# binaries
md5sum = "md5sum"

##############################
# link tools
##############################

# If <tt>link_name</tt> doesn't exist, creates link to <tt>point_to</tt> named <tt>link_name</tt>, otherwise checks 
# 1.) if existing <tt>link_name</tt> is a link and points already to <tt>point_to</tt>
# 2.) or a link pointing to another location than point_to
# and skips if 1. is the case and overwrites <tt>link_name</tt> if 2. is the case. Creates a backup of overwritten files or links if <tt>backup</tt> is <code>True</code>.
# @args backup creates a backup in the form filename.bk<N> where N is the minimal number denoting a filesname which doesn't exist. Backup is not created if the content of <tt>point_to</tt> and <tt>link_name</tt> are identical. If <tt>link_name</tt> is a link, the link target is backed up (because backing up the link doesn't make sense).
# @return <code>False</code> if the link could not be created due to existance of the <tt>link_name</tt> (and <tt>force</tt> being <code>False</code>).
def checked_link(point_to, link_name, force=False, backup=True):
    target_parent = os.path.abspath(os.path.join(link_name, '..'))
    if not os.path.lexists(target_parent):
        os.makedirs(target_parent)
    if not os.path.lexists(link_name):
        os.symlink(point_to,link_name)
        return True
    else:
        if not force:
            return False
    if os.path.islink(link_name) and os.path.realpath(link_name) == point_to:
        return True
    if backup == True:
        renamepostfix = ".bk"
        renamepostfixcount = 0
        backup_file = link_name
        if os.path.islink(link_name):
            # if link_name is a link it makes only sense to backup it's target (with the same mechanism)
            backup_file = os.path.realpath(link_name)
        while os.path.lexists(backup_file+renamepostfix):
            renamepostfix = ".bk%d" % renamepostfixcount
            renamepostfixcount += 1
        target_backup_path = backup_file+renamepostfix
        logger.debug("backing up %s to %s" % (backup_file, target_backup_path))
        os.rename(backup_file, target_backup_path)
    else:
        if os.path.isdir(link_name):
            os.remove(link_name)
        else:
            shutil.rmtree(link_name)
    os.symlink(point_to, link_name)
    return True
# implementation notes:
# - skipping backup if file content are identical is not useful because if was initially intended to avoid endless identical backups of links which now handled smarter by skipping link creation when link exists and points to the source already

#############
# Write tools
#############

def write_file(file0, what):
    if os.path.isdir(file0):
        raise ValueError("file %s is a directory" % file0)
    if lazy_newline and not what.endswith("\n"):
        what = "%s\n" % what
    file_obj = open(file0, 'w')
    file_obj.write(what)
    file_obj.flush()
    file_obj.close()

# appends <tt>what</tt> to file with path <tt>file0</tt>. Creates file if it doesn't exist. Newline character is appended if <tt>lazy_newline</tt> is <code>True</code> and <tt>what</tt> doesn't end with a newline yet.
# raises <tt>ValueError</tt> if <tt>file0</tt> is a directory
def append_file(file0, what, lazy_newline=True):
    if os.path.isdir(file0):
        raise ValueError("file %s is a directory" % file0)
    if lazy_newline and not what.endswith("\n"):
        what = "%s\n" % what
    file_obj = open(file0, 'a')
    file_obj.write(what)
    file_obj.flush()
    file_obj.close()

def check_dir(dir_path):
    if not os.path.exists(dir_path):
        return False
    if not os.path.isdir(dir_path):
        raise ValueError("dir_path has to point to a directory")
    if len(os.listdir(dir_path)) == 0:
        logger.debug("%s is empty" % dir_path)
        return False
    return True

def create_dir(dir_path, allow_content):
    if os.path.exists(dir_path):
        if os.path.isdir(dir_path):
            if len(os.listdir(dir_path)) > 0 and not allow_content:
                raise RuntimeError("%s exists and has content, a directory is supposed to be created. Remove or move the content of the directory" % dir_path)
        else:
            # file
            raise RuntimeError("%s exists, a directory is supposed to be created. Remove or move the file" % dir_path)
    else:
        os.makedirs(dir_path)

def check_file(file_path, md5sum):
    if not os.path.exists(file_path):
        return False
    if not os.path.isfile(file_path):
        raise ValueError("file_path has to point to a file")
    retrieved_md5sum = retrieve_md5sum(file_path)
    if not retrieved_md5sum == md5sum:
        logger.debug("%s has md5 sum %s instead of %s" % (file_path, retrieved_md5sum, md5sum))
        return False
    return True

def retrieve_md5sum(path):
    md5sum_output = sp.check_output([md5sum, path]).strip().decode('utf-8') # subprocess.check_output returns byte string which has to be decoded
    ret_value = str(re.split("[\\s]+", md5sum_output) [0])
    return ret_value

