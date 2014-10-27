#!/usr/bin/python
# -*- coding: utf-8 -*- 

import os
import sys
import check_os
import subprocess as sp

# replacement for python3's shutil.which
def which(pgm):
    if os.path.exists(pgm) and os.access(pgm,os.X_OK):
        return pgm
    path=os.getenv('PATH')
    for p in path.split(os.path.pathsep):
        p = os.path.join(p,pgm)
        if os.path.exists(p) and os.access(p,os.X_OK):
            return p

def hostname():
    if check_os.check_linux():
        return sp.check_output(["hostname"]).strip().decode("utf-8")
    else:
        raise RuntimeError("operating system not supported")

