#!/usr/bin/python
# -*- coding: utf-8 -*- 

# binaries
service = "service"
mvn = "mvn"
apt_get = "apt-get"
pip = "pip"
git = "git"

import logging
import sys
import os
import argparse
base_dir = os.path.realpath(os.path.join(__file__, ".."))
sys.path.append(base_dir)
import subprocess as sp

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

# about the import chain: 
# - in order to acchieve maximal code reusage checking presence of git is 
# necessary which is done with try-catch because which os.utils.which isn't 
# available before checkout with git; augeas is a hard dependency of pm_utils 
# (needs to be split up otherwise), so that installing it with pip is 
# acceptable

try:
    import os_utils
    import check_os
except ImportError:
    checkout_target = os.path.join(base_dir, "python-essentials")
    if os.path.exists(checkout_target):
         try:
             logger.info("updating python-essentials in '%s'" % (checkout_target,)) 
             sp.check_call([git, "checkout", "master"], cwd=checkout_target) # get on a branch because HEAD might be detached (then the following git pull without remote specification would fail)
             sp.check_call([git, "pull", "origin", "master"], cwd=checkout_target) # not 100 % sure why this is necessary
         except Exception as ex:
             logger.error("directory '%s' exists, but updating python-essentials failed (see the following exception for details), something has to be wrong. If you can't figure out what, remove '%s' and run the script again" % (checkout_target, checkout_target))
             raise ex
    else:
        logger.info("checking out python-essentials as script dependency")
        sp.check_call([git, "clone", "https://github.com/krichter722/python-essentials.git"], cwd=base_dir)
    sys.path.append(checkout_target)
    sys.path.append(os.path.join(checkout_target, "lib"))
    import os_utils
    import check_os

if os_utils.which("pip") is None:
    hint = ""
    if check_os.check_ubuntu() or check_os.check_debian() or check_os.check_linuxmint():
        hint = " Install with 'sudo apt-get install python-pip' for example."
    if check_os.check_opensuse():
        hint = " Install with 'zypper install python-pip' for example."
    raise RuntimeError("pip isn't available, install it and rerun the script, please!%s" % (hint,))
# python augeas bindings are not availble in pip, nor in easy_install
try:
    import augeas # just for testing that pm_utils dependencies are fulfilled
except ImportError:
    hint = ""
    if check_os.check_ubuntu() or check_os.check_debian() or check_os.check_linuxmint():
        hint = " Install with 'sudo apt-get install python-augeas' for example."
    if check_os.check_opensuse():
        hint = " Install with 'zypper install python-augeas' for example."
    raise RuntimeError("augeas python bindings aren't available, install it and rerun the script, please!%s" % (hint,))

import pm_utils
import argparse

pm_utils.install_packages(["python-pip"])
sp.check_call(["pip", "install", "plac"]) # use pip_utils when restored from backup
import osm_postgis_transform_prequisites
import bootstrap_globals

postgis_installs = ["source", "pm"]
postgis_install_default = postgis_installs[1]

pg_version_default = (9,2)

skip_apt_update_default=False
skip_apt_update_option = "s"
skip_apt_update_option_long = "skip-apt-update"

# directories
bin_dir = bootstrap_globals.bin_dir
base_dir = bootstrap_globals.base_dir

parser = argparse.ArgumentParser(description="Bootstrap the PGALISE simulation, including installation of dependencies (those which can't be fetched by maven), binaries (postgresql, postgis, etc.), setup of database in ")
parser.add_argument("-%s" % skip_apt_update_option, "--%s" % skip_apt_update_option_long, type=str, nargs='?',
                   help='', dest=skip_apt_update_option_long, default = skip_apt_update_default)

def bootstrap_privileged(skip_apt_update=skip_apt_update_default, postgis_install=postgis_install_default, pg_version=pg_version_default):
    if not os.path.exists(bin_dir):
        os.makedirs(bin_dir)
    
    if check_os.check_ubuntu() or check_os.check_debian() or check_os.check_linuxmint():
        pm_utils.install_packages(["maven", "openjdk-7-jdk", 
            "ant", # for postgis-jdbc
            "sudo", # very small probability that it is not installed, but it is a prequisite of the script...
            "software-properties-common", # provides add-apt-repository which is used by pm_utils module in osm_postgis_transform_prequisites.install_postgresql
            "python-software-properties", # provides add-apt-repository on Ubuntu 12.04.4, is available in Ubuntu 14.04
        ], package_manager=apt_get, skip_apt_update=skip_apt_update)
    elif check_os.check_opensuse():
        # install maven
        #sp.check_call(["/sbin/OCICLI", "http://software.opensuse.org/ymp/Application:Geo/openSUSE_12.3/maven.ymp?base=openSUSE%3A12.3&query=maven"]) # (not sure whether this installs futher instable software, provided maven version 3.0.4 isn't a hit neither)
        maven_bin_dir = os.path.join(external_bin_dir, maven_bin_dir_name)
        if not check_dir(maven_bin_dir):
            maven_bin_archive = os.path.join(external_bin_dir, maven_bin_archive_name)
            if not check_file(maven_bin_archive, maven_bin_archive_md5):
                do_wget(maven_bin_archive_url, maven_bin_archive)
            sp.check_call([tar, "xf", maven_bin_archive], cwd=external_bin_dir)
            shutil.copytree(maven_bin_dir, maven_bin_dir_install_target)
            sp.check_call(chown, "-Rc", "%s:%s" %(user,user), dirpath)
        # install remaining prequisites
        sp.check_call([zypper, "install", "java-1_7_0-openjdk", "java-1_7_0-openjdk-devel", "java-1_7_0-openjdk-src", "java-1_7_0-openjdk-javadoc"])
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("operating system not supported!")
    
    # postgresql
    osm_postgis_transform_prequisites.install_postgresql(pg_version=pg_version, skip_apt_update=skip_apt_update)
        
    # install postgis (demote for source installation)
    if postgis_install == "pm":
        if check_os.check_ubuntu() or check_os.check_debian():
            packages = ["postgis"] # 'postgis-2.1' interpreted as regular expression in apt!
            if pg_version == (9,2):
                packages += ["postgresql-9.2-postgis-2.1", "postgresql-contrib-9.2", "postgresql-9.2-postgis-2.1-scripts"]
            elif pg_version == (9,3):
                packages += ["postgresql-9.3-postgis-2.1", "postgresql-contrib-9.3", "postgresql-9.3-postgis-2.1-scripts"]
            else:
                raise ValueError("postgresql version not supported")
            sp.call([service, "stop", "postgresql"]) # crashes installation if port 5432 is occupied
            pm_utils.install_packages(packages, package_manager=apt_get, skip_apt_update=skip_apt_update)
        elif check_os.check_opensuse():
            sp.check_call([zypper, "install", "postgis2", "postgis2-devel", "postgis2-utils"])
        else:
            raise ValueError("operating system not supported")
    elif postgis_install == "source":
        logger.info("postgis installation from source occurs (unprivileged) in bootstrap.py")
    else:
        raise ValueError("postgis_install has to be one of %s" % str(postgis_installs))

    # prequisites for start_db
    pm_utils.install_packages([
        "python-pip", 
        "python-dev", # soft dependency of `pip install subprocess32`, not fulfilled, e.g. on Debian 7.4
    ], package_manager="apt-get")
    sp.check_call([pip, "install", "--upgrade", "setuptools"]) # saves a lot of trouble and hurts much less than it helps
    sp.check_call([pip, "install", "subprocess32", "pexpect"]) # pip manages update of available import automatically so that import xxx can be invoked, already installed packages don't cause returncode != 0

if __name__ == "__main__":    
    args = vars(parser.parse_args())
    skip_apt_update = args[skip_apt_update_option_long]
    bootstrap_privileged(skip_apt_update=skip_apt_update)


