#!/usr/bin/python
# -*- coding: utf-8 -*- 

import subprocess as sp

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

