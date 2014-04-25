#!/usr/bin/python

import unittest
import sys
import os
sys.path.append(os.path.realpath(".."))
import bootstrap

class Test(unittest.TestCase):
    
    @classmethod
    def setUpClass(cls):
        super(Test, cls).setUpClass()

    @classmethod
    def tearDownClass(cls):
        pass
        
    def test_bootstrap(self):
        

if __name__ == "__main__":
    unittest.main()

