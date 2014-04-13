#!/usr/bin/python
# -*- coding: utf-8 -*- 

import file_line_utils
import subprocess

##############################
# user and group tools 
##############################

# @return the username associated with the specified id (as listed in /etc/passwd) or <tt>None</tt> if there's no such uid
# can be done with <code>id -u -n <uid></code>, but this is buggy in OpenSUSE due to missing services (as so many other commands)
def username_by_id(uid):
    if str(type(uid)) != "<type 'int'>":
        raise ValueError("uid has to be an int")
    passwd_lines = file_line_utils.file_lines("/etc/passwd", "#")
    for passwd_line in passwd_lines:
        passwd_line_content = passwd_line.split(":")
        if int(passwd_line_content[2]) == uid:
            return passwd_line_content[0]
    return None

def groupname_by_id(gid):
    if str(type(uid)) != "<type 'int'>":
        raise ValueError("uid has to be an int")
    passwd_lines = file_line_utils.file_files("/etc/passwd", "#")
    for passwd_line in passwd_lines:
        passwd_line_content = passwd_line.split(":")
        if (passwd_line_content[3]) == gid:
            return passwd_line_content[0]
    return None

# @return the effective gid or <code>-1</code> if group <tt>groupname</tt> doesn't exist
def id_by_username(username):
    if not check_user_exists(username):
        return -1
    ret_value = subprocess.check_output(["id", "-u", username])
    return int(ret_value)

# @return the effective gid or <code>-1</code> if group <tt>groupname</tt> doesn't exist
def id_by_groupname(groupname):
    if not check_group_exists(groupname):
        return -1
    ret_value = subprocess.check_output(["id", "-g", groupname])
    return int(ret_value)

# doesn't handle lines which start with whitespace in <tt>/etc/passwd</tt> correctly
# @return <code>True</code> if <tt>username</tt> exists (in <tt>/etc/passwd</tt>)
def check_user_exists(username):
    passwd_lines = file_line_utils.file_lines("/etc/passwd", comment_symbol="#")
    for passwd_line in passwd_lines:
        if passwd_line.startswith(username):
            return True
    return False

# doesn't handle lines which start with whitespace in <tt>/etc/group</tt> correctly
# @return <code>True</code> if <tt>groupname</tt> exists (in <tt>/etc/group</tt>)
def check_group_exists(groupname):
    group_lines = file_line_utils.file_lines("/etc/group", comment_symbol="#")
    for group_line in group_lines:
        if group_line.startswith(groupname):
            return True
    return False

