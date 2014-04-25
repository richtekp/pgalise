#!/usr/bin/python
# -*- coding: utf-8 -*- 

import subprocess as sp
import re
import os
import sys

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
        lsb_release_id_short = sp.check_output(["lsb_release", "-d", "-s"]).strip().decode("utf-8")
        ret_value = "Ubuntu" in lsb_release_id_short
        return ret_value
    except Exception:
        return False

def check_debian():
    try:
        lsb_release_id_short = sp.check_output(["lsb_release", "-d", "-s"]).strip().decode("utf-8")
        ret_value = "Debian" in lsb_release_id_short
        return ret_value
    except Exception:
        return False

def check_root():
    uid = sp.check_output(["id","-u"]).strip()
    ret_value = int(uid) == 0
    return ret_value

# @return <code>True</code> if the python version is >= 3.0, otherwise <code>False</code>
def check_python3():
    ret_value = sys.version_info >= (3,0)
    return ret_value

def findout_architecture():
    architecture = sp.check_output(["uname","-m"]).strip()
    return architecture

#lsb_release only works with python2.x
def findout_release_ubuntu():
#    python2=None
#    if os.path.isfile("/usr/bin/python2.6"):
#        python2="/usr/bin/python2.6"
#    elif os.path.isfile("/usr/bin/python2.7"):
#        python2="/usr/bin/python2.7"
#    else:
#        print("Neither python2.6 nor python 2.7 could be found in /usr/bin/. It's necessary to determine your distribution release")
#        exit(5)
    while not os.path.isfile("/usr/bin/python2.6") and not os.path.isfile("/usr/bin/python2.7"):
        print("Neither python2.6 nor python 2.7 could be found in /usr/bin/. It's necessary to determine your distribution release")
        confirm("proceed","Install python 2.6 or 2.7 and make it available at /usr/bin/python2.6 or /usr/lib/python2.7")
    release= sp.check_output(["lsb_release","-cs"]).strip().decode("utf-8")
    return release

def findout_release_debian():
    return findout_release_ubuntu()

# useful is a feature is available for any version up from a certain (the tuple contains ints because strings are less comparable)
def findout_release_ubuntu_tuple():
    while not os.path.isfile("/usr/bin/python2.6") and not os.path.isfile("/usr/bin/python2.7"):
        print("Neither python2.6 nor python 2.7 could be found in /usr/bin/. It's necessary to determine your distribution release")
        confirm("proceed","Install python 2.6 or 2.7 and make it available at /usr/bin/python2.6 or /usr/lib/python2.7")
    release_number = sp.check_output(["lsb_release", "-rs"]).strip().decode("utf-8")
    release_tuple = tuple([int(x) for x in release_number.split(".")])
    return release_tuple

