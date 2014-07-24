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
domain_name = "pgalise"

def deploy_glassfish(glassfish_dir=glassfish_dir_default, glassfish_version=glassfish_version_default):
    if glassfish_version != (4,0):
        raise ValueError("glassfish version != 4.0 not supported")
    if not os.path.exists(ear_path):
        raise ValueError("ear_path %s doesn't exist" % ear_path)
    logger.info("checking glassfish some directories where glassfish is expected")
    if not os.path.exists(glassfish_dir):
        logger.info("%s doesn't exist" % glassfish_dir)
        glassfish_dir = os.path.join(os.environ["HOME"], "glassfish-current")
        if not os.path.exists(glassfish_dir):
            logger.info("%s doesn't exist" % glassfish_dir)
            glassfish_dir = os.path.join(base_dir, "glassfish-4.0")
            if not os.path.exists(glassfish_dir):
                logger.info("%s doesn't exist" % glassfish_dir)
                logger.info("Enter path to glassfish directory interactively (skip this with -%s (--%s))" % (glassfish_dir_option, glassfish_dir_option_long))
                # python has no do-while-loop :(, but nested functions :)
                glassfish_dir = None
                def test():
                    glassfish_dir = os.path.join(os.getcwd(), raw_input("Enter path to glassfish directory (relative or absolut): "))
                test()
                while not os.path.exists(glassfish_dir):
                    logger.error("%s doesn't exist" % glassfish_dir)
                    test()
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
    
    # setting options is simply ignored though command return successfully which is very strange
    jvm_options = ["-XX:MaxPermSize=1024m", "-Xmx8192m", ] # wrap in '' in order to distinguish : in -XX: from separator for multiple options
    jvm_options_output_before = sp.check_output([asadmin, "list-jvm-options"])
    for jvm_option in jvm_options:
        if jvm_option in jvm_options_output_before:
            continue # adding the exact same option causes error (see also stackoverflow.com/questions/24699202/how-to-add-a-jvm-option-to-glassfish-4-0)
        sp.check_call([asadmin, "create-jvm-options", jvm_option])
    jvm_options_output_after = sp.check_output([asadmin, "list-jvm-options"])
    if not jvm_options_output_before == jvm_options_output_after:
        logger.info("JVM options applied, restart of domain is necessary and done now")
        sp.check_call([asadmin, "restart-domain", domain_name]) # if this isn't sufficient, use subcommands stop-domain and start-domain explicitly or visit http://yourhost:4848/__asadmin/restart-domain programmatically
    
    #logger.info("Setting JVM options %s" % str(jvm_options))
    #list_jvm_options_output = sp.check_output([asadmin, "list-jvm-options"])
    ## delete all -Xmx and -XX:MaxPermSize options because overwriten isn't possible
    #for list_jvm_options_line in list_jvm_options_output.split("\n"):
    #    if list_jvm_options_line.startswith("-Xmx"):
    #        sp.check_call([asadmin, "delete-jvm-options", list_jvm_options_line.strip()])
    #    elif list_jvm_options_line.startswith("-XX:MaxPermSize"):
    #        sp.check_call([asadmin, "delete-jvm-options", list_jvm_options_line.strip()])
    #for jvm_option in jvm_options:
    #    sp.check_call([asadmin, "create-jvm-options", jvm_option])
     
     
     
    # redeploy subcommand is recommended in the GF 4.0 deployment guid (https://glassfish.java.net/docs/4.0/application-deployment-guide.pdf), i.e. undeploying and deploying should be unless issues are encountered
    try:
        sp.check_call([asadmin, "deploy", ear_path], cwd=glassfish_dir)
    except sp.CalledProcessError as ex:
        logger.info("initial deployment failed, forcing the deployment")
        try:
            sp.check_call([asadmin, "deploy", "--force=true", ear_path], cwd=glassfish_dir)
        except sp.CalledProcessError as ex1:
            logger.error("Deployment failed. If this continues to happen, login as admin (default admin username is 'admin') at the admin console (default admin console URL is 'https://localhost:4848') and deactive/remove the application manually")
            raise ex1
    
if __name__ == "__main__":
    args = vars(parser.parse_args())
    deploy_glassfish(glassfish_dir= args[glassfish_dir_option])

