#!/usr/bin/python
# -*- coding: utf-8 -*- 

import subprocess as sp
import check_os
import re

def check_linux():
    if not check_python3():
        ret_value = sys.platform == "linux2"
        return ret_value
    else:
        ret_value = sys.platform == "linux"
        return ret_value

def check_opensuse():
    try:
        lsb_release_id_short = sp.check_output(["lsb_release", "-d", "-s"])
        return "openSUSE" in lsb_release_id_short
    except Exception:
        return False

def check_ubuntu():
    try:
        lsb_release_id_short = sp.check_output(["lsb_release", "-d", "-s"])
        return "Ubuntu" in lsb_release_id_short
    except Exception:
        return False

def check_root():
    uid = check_output(["id","-u"]).strip()
    ret_value = int(uid) == 0
    return ret_value

# @return <code>True</code> if the python version is >= 3.0, otherwise <code>False</code>
def check_python3():
    ret_value = sys.version_info >= (3,0)
    return ret_value

def findout_architecture():
    architecture = sp.check_output(["uname","-m"]).strip()
    return architecture

