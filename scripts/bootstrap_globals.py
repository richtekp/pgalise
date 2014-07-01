#!/usr/bin/python
# -*- coding: utf-8 -*- 

# a place to share constants to avoid too complex dependency issues in import 
# chains. The location of the file influences the constants!

import os

base_dir = os.path.realpath(os.path.join(os.path.dirname(__file__), ".."))
bin_dir = os.path.join(base_dir, "scripts", "bin")

