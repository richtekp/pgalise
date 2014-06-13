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

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

apt_get = "apt-get"
skip_apt_update_default=False
skip_apt_update_option = "s"
skip_apt_update_option_long = "skip-apt-update"

parser = argparse.ArgumentParser(description="Bootstrap the PGALISE simulation, including installation of dependencies (those which can't be fetched by maven), binaries (postgresql, postgis, etc.), setup of database in ")
parser.add_argument("-%s" % skip_apt_update_option, "--%s" % skip_apt_update_option_long, type=str, nargs='?',
                   help='', dest=skip_apt_update_option_long, default = skip_apt_update_default)

def bootstrap_privileged(skip_apt_update=skip_apt_update_default):
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
    osm_postgis_transform_prequisites.install_postgresql(version_tuple=(9,2), skip_apt_update=skip_apt_update)
        
    # install postgis (demote for source installation)
    if postgis_install == "source":
        def __install_postgis__():
            sudo_uid = int(os.getenv("SUDO_UID"))
            os.setuid(sudo_uid)
            logger.info("demoting to $SUDO_USER for building and installing postgis from sources")
            postgis_src_dir = os.path.join(external_src_dir, postgis_src_dir_name)
            if check_os.check_ubuntu() or check_os.check_debian():
                if not check_dir(postgis_src_dir):
                    postgis_src_archive_file = os.path.join(external_src_dir, postgis_src_archive_name)
                    if not check_file(postgis_src_archive_file, postgis_src_archive_md5):
                        do_wget(postgis_url, postgis_src_archive_file)
                        sp.check_call([tar, "xf", postgis_src_archive_file], cwd=external_src_dir)
                if not skip_build:
                    pm_utils.install_apt_get_build_dep(["postgis"], package_manager=apt_get, skip_apt_update=skip_apt_update) # might not be sufficient because Ubuntu 13.10's version of postgis is 1.5.x (we're using 2.x)
                    pm_utils.install_packages(["libgdal-dev"], package_manager=apt_get, skip_apt_update=skip_apt_update) # not covered by 1.5.x requirements (see above)    
                    sp.check_call([bash, "autogen.sh"], cwd=postgis_src_dir)
                    sp.check_call([bash, "configure"], cwd=postgis_src_dir)
                    sp.check_call([make, "-j8"], cwd=postgis_src_dir)
                    sp.check_call([make, "install"], cwd=postgis_src_dir)
                    
                # install postgis-jdbc (project is shipped inside postgis project (as long as a fixed version of postgis is used here, the version of postgis-jdbc will be fixed, too (postgis 2.1.1 -> postgis-jdbc 2.1.0SVN)
                postgis_mvn_project_path = os.path.join(postgis_src_dir, "java/jdbc")
                sp.check_call([ant], cwd=postgis_mvn_project_path) # generates maven project
                mvn_install(postgis_mvn_project_path, skip_tests)
            elif check_os.check_opensuse():
                raise RuntimeError("PostGIS source installation not supported")
            else:
                # better to let the script fail here than to get some less comprehensive error message later
                raise RuntimeError("Operating system not supported!")
        p = multiprocessing.Process(target=__install_postgis__)
        p.start()
        p.join()
    elif postgis_install == "pm":
        packages = ["postgis-2.1"]
        if check_os.check_ubuntu() or check_os.check_debian():
            if pg_version == (9,2):
                packages += ["postgresql-9.2-postgis-2.1", "postgresql-contrib-9.2", "postgresql-9.2-postgis-2.1-scripts"]
            elif pg_version == (9,3):
                packages += ["postgresql-9.3-postgis-2.1", "postgresql-contrib-9.3", "postgresql-9.3-postgis-2.1-scripts"]
            else:
                raise ValueError("postgresql version not supported")
        elif check_os.check_opensuse():
            sp.check_call([zypper, "install", "postgis2", "postgis2-devel", "postgis2-utils"])
        else:
            raise ValueError("operating system not supported")
        pm_utils.install_packages(packages, package_manager=apt_get, skip_apt_update=skip_apt_update)
        # install postgis from scripts/bin (as it is not available from somewhere online)
        postgis_jdbc_file = os.path.join(bin_dir, postgis_jdbc_name)
        sp.check_call([mvn, "install:install-file", \
            "-Dfile=%s" % postgis_jdbc_file, "-DartifactId=postgis-jdbc", 
            "-DgroupId=org.postgis", "-Dversion=2.1.0SVN", "-Dpackaging=jar"], cwd=bin_dir)
    else:
        raise ValueError("postgis_install has to be one of %s" % str(postgis_installs))

    # prequisites for start_db
    pm_utils.install_packages(["python-pip"], package_manager="apt-get")
    sp.check_call([pip, "install", "subprocess32"]) # pip manages update of available import automatically so that import xxx can be invoked

if __name__ == "__main__":    
    args = vars(parser.parse_args())
    skip_apt_update = args[skip_apt_update_option_long]
    bootstrap_privileged(skip_apt_update=skip_apt_update)

