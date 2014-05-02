#!/usr/bin/python
# -*- coding: utf-8 -*- 

# This script requires a "Full Java EE Platform" version of glassfish (which 
# you can usually get at https://glassfish.java.net/download.html). The 
# script will search it in $HOME/glassfish-4.0, $HOME/glassfish-current and in 
# in $BASE_DIR/glassfish-4.0 (where BASE_DIR refers to the base directory of 
# the PGALISE project relative to the path of this script. A directoy different 
# from the before mentioned can be passed as command line argument or entered 
# interactively after the search failed for all directories.
# Currently only glassfish version 4.0 is supported.

# internal implementation notes @TODO: read filenames from properties file shared with maven

import os
import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)
import argparse
import subprocess as sp

glassfish_dir_default = os.path.join(os.environ["HOME"], "glassfish-4.0")
glassfish_dir_option = "g"
glassfish_dir_option_long = "glassfish-dir"

glassfish_version_default = (4,0)

parser = argparse.ArgumentParser(description='Process some integers.')
parser.add_argument("-%s" % glassfish_dir_option, "--%s" % glassfish_dir_option_long, nargs="?", type=str, default=glassfish_dir_default,
                   help='The path to a Oracle glassfish Full Java EE edition', dest=glassfish_dir_option)

base_dir = os.path.realpath(os.path.join(__file__, "..", ".."))

app_name = "PG_Alise_Component"
ear_name = "simulation.ear"
ear_path = os.path.join(base_dir, "ear/target/", ear_name)
domain_name = "netbeans_domain"

def deploy_glassfish(glassfish_dir=glassfish_dir_default, glassfish_version=glassfish_version_default):
    if glassfish_version != (4,0):
        raise ValueError("glassfish version != 4.0 not supported")
    logger.info("checking glassfish dirs")
    if not os.path.exists(glassfish_dir):
        logger.info("%s doesn't exist" % glassfish_dir)
        glassfish_dir = os.path.join(os.environ["HOME"], "glassfish-current")
        if not os.path.exists(glassfish_dir):
            logger.info("%s doesn't exist" % glassfish_dir)
            glassfish_dir = os.path.join(base_dir, "glassfish-4.0")
            if not os.path.exists(glassfish_dir):
                logger.info("%s doesn't exist" % glassfish_dir)
                logger.info("Enter path to glassfish directory interactively (skip this with -%s (--%s))" % (glassfish_dir_option, glassfish_dir_option_long))
                glassfish_dir = raw_input("Enter path to glassfish directory: ")
                while not os.path.exists(glassfish_dir):
                    logger.error("%s doesn't exist" % glassfish_dir)
                    glassfish_dir = raw_input("Enter path to glassfish directory: ")
    logger.info("glassfish directory is %s" % glassfish_dir)
    
    # glassfish's main command is asadmin
    asadmin = os.path.join(glassfish_dir, "bin/asadmin")
    # check asadmin working
    if not "AS_JAVA" in os.environ or os.environ["AS_JAVA"] == "":
        logger.warn("environment variable AS_JAVA not set, this might cause trouble if a JRE 6 is found before 7")
    try:
        sp.check_call([asadmin, "version"], cwd=glassfish_dir)
    except sp.CalledProcessError as ex:
        logger.error("execution of glassfish main command asadmin failed, make sure you have a JRE 7 installed and environment variable AS_JAVA pointing to it (check warnings above)")
        raise ex   
    
    list_domains_output_lines = sp.check_output([asadmin, "list-domains"], cwd=glassfish_dir).decode("utf-8").strip().split("\n")
    domain_line = None
    for line in list_domains_output_lines:
        if (glassfish_version < (4,0) and "Name: %s Status: Running" % domain_name in line or "Name: %s Status: Not Running" % domain_name in line) or (glassfish_version >= (4,0) and "%s running" % domain_name in line or "%s not running" % domain_name in line):
            domain_line = line
            break        
    if domain_line is None:
        # domain not yet created
        logger.info("Creating missing domain %s" % domain_name)
        sp.check_call([asadmin, "create-domain", "--adminport", "4848", domain_name], cwd=glassfish_dir) # server is not started after domain creation
        logger.info("Starting domain %s" % domain_name)
        sp.check_call([asadmin, "start-domain", domain_name], cwd=glassfish_dir)
    else:
        if glassfish_version < (4,0) and domain_line == "Name: %s Status: Not Running" % domain_name or glassfish_version >= (4,0) and domain_line == "%s not running" % domain_name:
            # domain needs to be started
            logger.info("Starting domain %s" % domain_name)
            sp.check_call([asadmin, "start-domain", domain_name], cwd=glassfish_dir)
        else:
            logger.info("An instance of glassfish is already running, deploying to it")
    sp.call([asadmin, "undeploy", app_name], cwd=glassfish_dir) # use subprocess.call because this might fail if the application if deployed for the first time (the undeployment failure doesn't matter in this case)
    try:
        sp.check_call([asadmin, "deploy", ear_path], cwd=glassfish_dir)
    except sp.CalledProcessError as ex:
        logger.error("Deployment failed. If this continues to happen, login as admin (default admin username is 'admin') at the admin console (default admin console URL is 'https://localhost:4848') and deactive/remove the application manually")
        raise ex
    
if __name__ == "__main__":
    args = vars(parser.parse_args())
    deploy_glassfish(glassfish_dir= args[glassfish_dir_option])

