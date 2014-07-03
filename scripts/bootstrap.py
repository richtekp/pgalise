#!/usr/bin/python
# -*- coding: utf-8 -*- 

# check that all necessary build tools and helpers are installed
# the installation requires root privileges because postgis handling of GNU autotools prefix is a mess (and officially ignored as staded in the INSTALL information) 
# an installed postgres server and root access (allowing installation of extensions and having grant privileges) to it is required
# all build commands are executed using python's subprocess.check_call which throws a RuntimeError is an error in the command occurs, so you should be able to find out what went wrong in the output
# The script assumes that a postgres server is being run by the user in order to execute pgalise, i.e. it doesn't integrate into the system postgres server. The scripts starts and shuts down a server in order to setup the database
# not using Ubuntu ppa on https://launchpad.net/~ubuntugis/ because it is not available for saucy, so reducing portablility
# not yet paying attention to pg_version (e.g. in OpenSUSE)

# internal implementation notes:
# - gaining privileges with sudo has the advantage of easiest error handling and debugging while forcing the invokation of the script with sudo allows the usage of python function for UNIX commands like chown
# - a parameterization of the functionality of the script based on a base directory which point to the source root is not useful as setting up all directories relative to this base dir is not necessary (it can be used to determine overridable default values though)
# - there's no sense in keeping the privileged code in this script which is 
# intended to run without sudo privileges as invoking sudo in the script causes 
# a hell of maintainance overhead -> source out to bootstrap_privileged and 
# check whether prequisites are met and display warning when not (is an 
# overhead, too, but a more elegant solution)
#
################################################################################
# A Note on pitfalls                                                           #
################################################################################
# - maven doesn't complain if the file it is supposed to install doesn't exist 
# (which makes debugging harder for no reason), so verify that yourself
# 

import os
import subprocess as sp
import time
import re
import sys
import argparse
# further imports below
import logging
import multiprocessing

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

# imports with relative paths allow separation from source (script) and target directory which allows determination of default values for downloads URLs (and sharing them) based on pg_version and architecture (otherwise every combination has to be added as argument to bootstrap function)
sys.path.append(os.path.realpath(os.path.join(__file__, "..")))
sys.path.append(os.path.realpath(os.path.join(__file__, "..", "lib")))
import check_os
import user_group_utils
import postgis_utils
import string
import pm_utils
import file_utils
import bootstrap_globals

bin_dir = bootstrap_globals.bin_dir
base_dir = bootstrap_globals.base_dir
bootstrap_dir_default = os.path.realpath(os.path.join(base_dir, "pgalise-bootstrap"))
pg_version=(9,2) # provides best compatibility with OpenSUSE because in Ubuntu and Debian OpenDSG repository can be used
arch = check_os.findout_architecture()
if pg_version == (9,2):
    postgresql_jdbc_name = "postgresql-9.2-1004.jdbc4.jar"
    postgresql_jdbc_md5 = "23494e0c770cb03045a93d07a8ed2a9f"
    postgresql_jdbc_url_default = "http://jdbc.postgresql.org/download/postgresql-9.2-1004.jdbc4.jar"
    if arch == "x86_64":
        postgresql_deb_url_default = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.2.7-1.amd64.openscg.deb"
        postgresql_deb_name = "postgres_9.2.7-1.amd64.openscg.deb"
        postgresql_deb_md5 = "cea3857cdf42cf18d3995333532e46d0"
    elif arch == "i386" or arch == "i686":
        postgresql_deb_url_default = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.2.7-1.i386.openscg.deb"
        postgresql_deb_name = "postgres_9.2.7-1.i386.openscg.deb"
        postgresql_deb_md5 = "c180d526ef27c31c8e140583bc64a88f"
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("architecture %s not supported" % arch)
elif pg_version == (9,3):
    postgresql_jdbc_name = "postgresql-9.2-1004.jdbc4.jar" #@TODO: not optimal
    postgresql_jdbc_md5 = "23494e0c770cb03045a93d07a8ed2a9f"
    postgresql_jdbc_url_default = "http://jdbc.postgresql.org/download/postgresql-9.2-1004.jdbc4.jar"#@TODO: not optimal
    if arch == "x86_64":
        postgresql_deb_url_default = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.3.1-1.amd64.openscg.deb"
        postgresql_deb_name = "postgres_9.3.1-1.amd64.openscg.deb"
        postgresql_deb_md5 = "9a4f6c6be47a5ea594c3bececc8e82a6"
    elif arch == "i386":
        postgresql_deb_url_default = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.3.1-1.i386.openscg.deb"
        postgresql_deb_name = "postgres_9.3.1-1.i386.openscg.deb"
        postgresql_deb_md5 = "a659ccb8b5fc838a494780ad05a5f856"
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("architecture %s not supported" % arch)
else:
    # better to let the script fail here than to get some less comprehensive error message later
    raise RuntimeError("postgresql version %s is not supported (this needs to be fixed in sources)" % string.join([str(x) for x in pg_version],"."))

geotools_url_default = "http://downloads.sourceforge.net/project/geotools/GeoTools%209%20Releases/9.2/geotools-9.2-project.zip"
geotools_src_archive_name = "geotools-9.2-project.zip"
geotools_src_archive_md5 = "18cc0f21557b2187a89eebd965cbe409"
geotools_src_dir_name = "geotools-9.2"
commons_src_dir_name = "commons-collections4-4.0-src"
commons_src_archive_url_default = "http://mirror.derwebwolf.net/apache//commons/collections/source/commons-collections4-4.0-src.tar.gz"
commons_src_archive_md5 = "d0f1e679725945eaf3e2f0c0a33532a5"
commons_src_archive_name = "commons-collections4-4.0-src.tar.gz"
# no repository provides jgrapht 0.8.3 as org.jgrapht:jgrapht:0.8.3 artifact, but as net.sf.jgrapht:jgrapht:0.8.3 -> download and install manually
jgrapht_url_default = "http://repo1.maven.org/maven2/net/sf/jgrapht/jgrapht/0.8.3/jgrapht-0.8.3.jar"
jgrapht_name = "jgrapht-0.8.3.jar"
jgrapht_md5 = "7b275e7bf54f02ceab2bf5132a8ef6ce"
# no repository provides pcingola:jfuzzylogic:3.0
jfuzzy_url_default = "http://sourceforge.net/projects/jfuzzylogic/files/jfuzzylogic/jFuzzyLogic_v3.0.jar/download"
jfuzzy_name = "jFuzzyLogic_v3.0.jar"
jfuzzy_md5 = "17e1d0e309c3a00321db4f308dbae404"
maven_bin_archive_name = "apache-maven-3.2.1-bin.tar.gz"
maven_bin_archive_md5 = "aaef971206104e04e21a3b580d9634fe"
maven_bin_dir_name = "apache-maven-3.2.1"
maven_bin_archive_name = "apache-maven-3.2.1-bin.tar.gz"
maven_bin_archive_url_default = "http://ftp.fau.de/apache/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz"
maven_bin_dir_install_target = "/usr/local/share/maven-3.2.1"
postgis_jdbc_name = "postgis-jdbc-2.1.0SVN.jar"
postgis_src_dir_name="postgis-2.1.1"
postgis_url_default = "http://download.osgeo.org/postgis/source/postgis-2.1.1.tar.gz"
postgis_src_archive_name = "postgis-2.1.1.tar.gz"
postgis_src_archive_md5 = "4af86a39e2e9dbf10fe894e03c2c7027"
openejb_jar_file_name = "openejb-core-4.7.0-20140619.040749-30.jar"

# necessary build tools and helpers 
# <ul>
ant = "/home/richter/apache-ant-1.7.1/bin/ant"
wget = "wget"
unzip="unzip"
mvn = "mvn"
tar = "tar"
bash = "dash" # bash and ksh cause error when running autogen.sh and configure
make = "make"
su = "su"
psql = "/usr/lib/postgresql/%s/bin/psql" % string.join([str(x) for x in pg_version],".")
initdb = "/usr/lib/postgresql/%s/bin/initdb" % string.join([str(x) for x in pg_version],".")
createdb = "/usr/lib/postgresql/%s/bin/createdb" % string.join([str(x) for x in pg_version],".")
postgres = "/usr/lib/postgresql/%s/bin/postgres" % string.join([str(x) for x in pg_version],".")
apt_get = "apt-get"
zypper = "zypper"
dpkg = "dpkg"
add_apt_repo = "add-apt-repository"
cp="cp"
chown = "chown"
whoami = "whoami"
# </ul>

# some variables to be changed at will (would be more elegant to enable controll as options of this script)
#postgres_user = sp.check_output(["id", "-u", "-n"]).strip()
postgres_user = "pgalise"
postgres_pw = "somepw"
postgres_datadir_path_default = os.path.join(base_dir, "postgis_db-%s" % string.join([str(x) for x in pg_version],"."))
postgres_datadir_path_option = "p"
postgres_datadir_path_option_long = "postgres-datadir"
force_overwrite_postgres_datadir_option = "o"
force_overwrite_postgres_datadir_option_long = "force-overwrite-datadir"
postgres_port = "5201"
postgres_host = "localhost"
pgalise_db_name = "pgalise"
pgalise_db_test_name = "pgalise_test"
skip_tests_default = False
skip_tests_option = "s"
skip_tests_option_long = "skip-tests"

parser = argparse.ArgumentParser(description="Bootstrap the PGALISE simulation, including installation of dependencies (those which can't be fetched by maven), binaries (postgresql, postgis, etc.), setup of database in ")
parser.add_argument("-%s" % postgres_datadir_path_option, "--%s" % postgres_datadir_path_option_long, type=str, nargs='?',
                   help='', dest=postgres_datadir_path_option_long, default = postgres_datadir_path_default)
parser.add_argument("-%s" % force_overwrite_postgres_datadir_option, "--%s" % force_overwrite_postgres_datadir_option_long, type=bool, nargs='?',
                   help='', dest=force_overwrite_postgres_datadir_option_long)
parser.add_argument("-%s" % skip_tests_option, "--%s" % skip_tests_option_long, type=bool, nargs='?',
                   help='Whether tests for maven build actions ought to be skipped', dest=skip_tests_option_long)

# provides a wrapper around the <code>mvn install</code> command for source builds to handle 
# conditional invokation with <code>-DskipTests=true</code> 
def mvn_install(dir_path, skip_tests=skip_tests_default):
    cmds = [mvn, "install"]
    if not skip_tests:
        cmds += ["-DskipTests=true"]
    sp.check_call(cmds, cwd=dir_path)

# @args target the absolute path of the target file (not directory)
def do_wget(url, target):
    sp.check_call([wget, "-O", target, url])

# @args skip_build skip building of packages if directories exist
# @args remove_datadir <code>True</code> or <code>False</code> for removal of stored data in <tt>postgres_datadir_path</tt> or <code>None</code> to interact with the user with a prompt 
def bootstrap(bootstrap_dir=bootstrap_dir_default, skip_build=False, psql=psql, initdb=initdb, createdb=createdb, postgres=postgres, postgres_datadir_path=postgres_datadir_path_default, force_overwrite_postgres_datadir=None, postgresql_jdbc_url=postgresql_jdbc_url_default, postgresql_deb_url=postgresql_deb_url_default, geotools_url=geotools_url_default, jgrapht_url=jgrapht_url_default, jfuzzy_url=jfuzzy_url_default, maven_bin_archive_url=maven_bin_archive_url_default, commons_src_archive_url=commons_src_archive_url_default, skip_tests=skip_tests_default, postgis_install="pm", postgis_url=postgis_url_default):
    # setup directories
    script_dir = os.path.join(base_dir, "scripts")
    external_dir = os.path.join(bootstrap_dir, "external")
    internal_dir = os.path.join(bootstrap_dir, "internal")
    # used for postgresql socket
    tmp_dir = os.path.join(script_dir, "tmp") # "/tmp less save than dir only readable by user"
    external_bin_dir = os.path.join(external_dir, "bin")
    external_src_dir = os.path.join(external_dir, "src")
    if not os.path.exists(bin_dir):
        os.makedirs(bin_dir)
    if not os.path.exists(bootstrap_dir):
        os.makedirs(bootstrap_dir)
    if not os.path.exists(external_dir):
        os.makedirs(external_dir)
    if not os.path.exists(external_bin_dir):
        os.makedirs(external_bin_dir)
    if not os.path.exists(external_src_dir):
        os.makedirs(external_src_dir)        
    if not os.path.exists(tmp_dir):
        os.makedirs(tmp_dir)
    user = sp.check_output([whoami])
    postgres_socket_dir = tmp_dir # is this ok?
    
    # check some prequisites (after installation as they might be installed properly)
    java_home = os.getenv("JAVA_HOME") # necessary for mvn
    if java_home is None or java_home == "":
        raise RuntimeError("JAVA_HOME is not set")
    if not os.path.exists(os.path.join(java_home, "bin/java")):
        raise RuntimeError("JAVA_HOME %s doesn't seem to point to a valid JDK" % java_home)
        
    # install postgresql jdbc driver
    postgresql_jdbc_file = os.path.join(external_bin_dir, postgresql_jdbc_name)
    if not file_utils.check_file(postgresql_jdbc_file, postgresql_jdbc_md5):
        do_wget(postgresql_jdbc_url, postgresql_jdbc_file)
    if pg_version == (9,2):
        postgresql_jdbc_mvn_version = "9.2-1004.jdbc4"
    elif pg_version == (9,3):
        postgresql_jdbc_mvn_version = "9.2-1004.jdbc4" #@TODO: not optimal to have different client and server versions
    else:
        raise RuntimeError("postgresql version %s not supported" % string.join([str(x) for x in pg_version],"."))
    sp.check_call([mvn, "install:install-file", \
        "-Dfile=%s" % postgresql_jdbc_file, "-DartifactId=postgresql",  \
        "-DgroupId=postgresql", "-Dversion=%s" % postgresql_jdbc_mvn_version, "-Dpackaging=jar"], cwd=external_bin_dir)
    # install jgrapht (install net.sf.jgrapht:jgrapht:0.8.3 as org.jgrapht:jgrapht:0.8.3 (could not be found in any repository))
    jgrapht_file = os.path.join(external_bin_dir, jgrapht_name)
    if not file_utils.check_file(jgrapht_file, jgrapht_md5):
        do_wget(jgrapht_url, jgrapht_file)
    sp.check_call([mvn, "install:install-file", \
        "-Dfile=%s" % jgrapht_file, "-DartifactId=jgrapht",
        "-DgroupId=org.jgrapht", "-Dversion=0.8.3", "-Dpackaging=jar"], cwd=external_bin_dir)

    # install jfuzzylogic
    jfuzzy_file = os.path.join(external_bin_dir, jfuzzy_name)
    if not file_utils.check_file(jfuzzy_file, jfuzzy_md5):
        do_wget(jfuzzy_url, jfuzzy_file)
    sp.check_call([mvn, "install:install-file", \
        "-Dfile=%s" % jfuzzy_file, "-DartifactId=jfuzzy", 
        "-DgroupId=pcingola", "-Dversion=3.0", "-Dpackaging=jar"], cwd=external_bin_dir)
    
    # geronimo not available as binary (but depends on eclipse source stuff...)
    #geronimo_file_name = "geronimo-3.0.1"
    #geronimo_src_archive_name = "geronimo-3.0.1-source-release.zip"
    #geronimo_url = "http://mirrors.ibiblio.org/maven2/org/apache/geronimo/geronimo/3.0.1/geronimo-3.0.1-source-release.zip"
    #geronimo_file_path = os.path.join(external_src_dir, geronimo_file_name)
    
    # install geotools
    #geotools_src_dir = os.path.join(external_src_dir, geotools_src_dir_name)
    #if not file_utils.check_dir(geotools_src_dir):
    #    if not ch...le(os.path.join(external_src_dir, geotools_src_archive_name)) or file_utils.retrieve_md5sum(os.path.join(external_src_dir, geotools_src_archive_name)) != geotools_src_archive_md5:
    #        do_wget() ... sp.check_call([wget, geotools_url], cwd=tmp_dir)
    #    sp.check_call([unzip, os.path.join(tmp_dir, geotools_src_archive_name)], cwd=external_src_dir)
    #if not skip_build:
    #    mvn_settings_file_path = os.path.join(script_dir, "settings.xml")
    #    sp.check_call([mvn, "-e", "--global-settings", mvn_settings_file_path, "--settings", mvn_settings_file_path, "install", "-Dall", "-DskipTests=true"], cwd=geotools_src_dir)
    
    # install commons-collections 4
    #commons_src_dir = os.path.join(external_src_dir, commons_src_dir_name)
    #if not file_utils.check_dir(commons_src_dir):
    #    commons_src_archive_file = os.path.join(external_src_dir, commons_src_archive_name)
    #    if not file_utils.check_file(commons_src_archive_file, commons_src_archive_md5):
    #        do_wget(commons_src_archive_url, commons_src_archive_file)
    #        sp.check_call([tar, "xf", commons_src_archive_file], cwd=external_src_dir)
    #if not skip_build:
    #    mvn_install(commons_src_dir, skip_tests)
    
    # install postgis from source (installation with package manager occurs in 
    # bootstrap_privileged.py
    if postgis_install == "source":
        # installation of postgis-jdbc from bin directory should not occur in sources installation as this refers to the version shipped with the tarball -> will install mvn project in subdirectory of tarball root
        postgis_src_dir = os.path.join(external_src_dir, postgis_src_dir_name)
        if check_os.check_ubuntu() or check_os.check_debian():
            if not file_utils.check_dir(postgis_src_dir):
                postgis_src_archive_file = os.path.join(external_src_dir, postgis_src_archive_name)
                if not file_utils.check_file(postgis_src_archive_file, postgis_src_archive_md5):
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
    elif postgis_install == "pm":
        # install postgis from scripts/bin (as it is not available from somewhere online)
        postgis_jdbc_file = os.path.join(bin_dir, postgis_jdbc_name)
        if not os.path.exists(postgis_jdbc_file):
            raise RuntimeError("postgis JDBC jar %s doesn't exist, can't continue, consider fetching it manually" % (postgis_jdbc_file,))
        sp.check_call([mvn, "install:install-file", \
            "-Dfile=%s" % postgis_jdbc_file, "-DartifactId=postgis-jdbc", 
            "-DgroupId=org.postgis", "-Dversion=2.1.0SVN", "-Dpackaging=jar"], cwd=bin_dir)
    else:
        raise RuntimeError("postgis_install %s isn't supported" % (postgis_install,))
    
    # always install openejb from bin directory (might be made flexible with source installation like postgis (see above) later)
    openejb_jar_file_path = os.path.join(bin_dir, openejb_jar_file_name)
    if not os.path.exists(openejb_jar_file_path):
        raise RuntimeError("OpenEJB jar %s doesn't exist, can't continue, consider fetching it manually" % (openejb_jar_file_path,))
    sp.check_call([mvn, "install:install-file", \
        "-Dfile=%s" % (openejb_jar_file_path,), "-DartifactId=openejb-core", 
        "-DgroupId=org.apache.openejb", "-Dversion=4.7.0-SNAPSHOT", "-Dpackaging=jar"], cwd=bin_dir)
        
    # setup postgis datadir and configuration    
    if not file_utils.check_dir(postgres_datadir_path) or force_overwrite_postgres_datadir:
        # os.makedirs(postgres_datadir_path) # causes error if directory exists and is not necessary
        postgis_utils.bootstrap_datadir(postgres_datadir_path, postgres_user, password=postgres_pw, initdb=initdb)            
        postgis_utils.bootstrap_database(postgres_datadir_path, postgres_port, postgres_host, postgres_user, pgalise_db_name, password=postgres_pw, initdb=initdb, postgres=postgres, createdb=createdb, psql=psql)
        postgis_utils.bootstrap_database(postgres_datadir_path, postgres_port, postgres_host, postgres_user, pgalise_db_test_name, password=postgres_pw, initdb=initdb, postgres=postgres, createdb=createdb, psql=psql)
    else:
        logger.info("Postgres datadir %s has not been overwritten. You do it by invoking the script with -%s (--%s)" % (postgres_datadir_path, force_overwrite_postgres_datadir_option, force_overwrite_postgres_datadir_option_long))
# internal implementation notes:
# - passing base_dir as argument of the bootstrap function or in another way doesn't necessarily make sense (default values can be set based on base_dir=source root though)
        
if __name__ == "__main__":
    args = vars(parser.parse_args())
    skip_tests = args[skip_tests_option_long]
    bootstrap(postgres_datadir_path=args[postgres_datadir_path_option_long], force_overwrite_postgres_datadir=args[force_overwrite_postgres_datadir_option_long], skip_tests=skip_tests)

