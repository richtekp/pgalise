#!/usr/bin/python
# -*- coding: utf-8 -*- 

import logging
import sys
import os
sys.path.append(os.path.realpath(os.path.join(__file__, "..")))
sys.path.append(os.path.realpath(os.path.join(__file__, "..", "lib")))
import check_os
import pm_utils
import argparse
import osm_postgis_transform_prequisites
import subprocess as sp
import bootstrap

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

# binaries
service = "service"
mvn = "mvn"
apt_get = "apt-get"
pip = "pip"

postgis_installs = ["source", "pm"]
postgis_install_default = postgis_installs[1]

postgis_src_dir_name="postgis-2.1.1"
postgis_url_default = "http://download.osgeo.org/postgis/source/postgis-2.1.1.tar.gz"
postgis_src_archive_name = "postgis-2.1.1.tar.gz"
postgis_src_archive_md5 = "4af86a39e2e9dbf10fe894e03c2c7027"

pg_version_default = (9,2)

skip_apt_update_default=False
skip_apt_update_option = "s"
skip_apt_update_option_long = "skip-apt-update"

bin_dir = bootstrap.bin_dir
base_dir = bootstrap.base_dir

parser = argparse.ArgumentParser(description="Bootstrap the PGALISE simulation, including installation of dependencies (those which can't be fetched by maven), binaries (postgresql, postgis, etc.), setup of database in ")
parser.add_argument("-%s" % skip_apt_update_option, "--%s" % skip_apt_update_option_long, type=str, nargs='?',
                   help='', dest=skip_apt_update_option_long, default = skip_apt_update_default)

def bootstrap_privileged(skip_apt_update=skip_apt_update_default, postgis_install=postgis_install_default, pg_version=pg_version_default):
    if check_os.check_ubuntu() or check_os.check_debian():
        pm_utils.install_packages(["maven", "openjdk-7-jdk", 
            "ant", # for postgis-jdbc
            "sudo", # very small probability that it is not installed, but it is a prequisite of the script...
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
    pm_utils.install_packages(["python-pip"], package_manager="apt-get")
    sp.check_call([pip, "install", "subprocess32"]) # pip manages update of available import automatically so that import xxx can be invoked

if __name__ == "__main__":    
    args = vars(parser.parse_args())
    skip_apt_update = args[skip_apt_update_option_long]
    bootstrap_privileged(skip_apt_update=skip_apt_update)

