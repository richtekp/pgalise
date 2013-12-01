#!/usr/bin/python

# check that all necessary build tools and helpers are installed
# the installation requires root privileges because postgis handling of GNU autotools prefix is a mess (and officially ignored as staded in the INSTALL information) 
# an installed postgres server and root access (allowing installation of extensions and having grant privileges) to it is required
# all build commands are executed using python's subprocess.check_call which throws a RuntimeError is an error in the command occurs, so you should be able to find out what went wrong in the output
# The script assumes that a postgres server is being run by the user in order to execute pgalise, i.e. it doesn't integrate into the system postgres server. The scripts starts and shuts down a server in order to setup the database
# not using Ubuntu ppa on https://launchpad.net/~ubuntugis/ because it is not available for saucy, so reducing portablility

import os
import subprocess as sp
import time

base_path= os.path.realpath(os.path.dirname(__file__))
tmp_path = "/tmp"
external_dir = os.path.join(base_path, "external")
external_bin_dir = os.path.join(external_dir, "bin")
external_src_dir = os.path.join(external_dir, "src")
postgresql_jdbc_name = "postgresql-9.2-1004.jdbc4.jar"
postgresql_jdbc_url = "http://jdbc.postgresql.org/download/postgresql-9.2-1004.jdbc4.jar"
geotools_src_dir_name = "geotools-9.2"
postgis_src_dir_name="postgis-2.1.1"
commons_src_dir_name = "commons-collections4-4.0-src"

# necessary build tools and helpers <ul>
wget = "wget"
unzip="unzip"
mvn = "mvn"
tar = "tar"
bash = "dash" # bash and ksh cause error when running autogen.sh and configure
make = "make"
sudo = "sudo"
ant = "ant"
su = "su"
psql = "/usr/lib/postgresql/9.1/bin/psql"
initdb = "/usr/lib/postgresql/9.1/bin/initdb"
createdb = "/usr/lib/postgresql/9.1/bin/createdb"
apt_get = "apt-get"
# </ul>

# some variables to be changed at will (would be more elegant to enable controll as options of this script)
postgres_user = sp.check_output(["id", "-u", "-n"]).strip()
postgres_datadir_path = os.path.join(base_path, "postgis_db")
postgres_port = "5201"
postgres_host = "localhost"
postgres_socket_dir = tmp_path # is this ok?
pgalise_user = "pgalise"
pgalise_pw = "somepw"

# @args skip_build skip building of packages if directories exist
def bootstrap(skip_build=False):
    # raise RuntimeError("not supported yet. All necessary sources are in %s and %s" % 
    if not os.path.exists(external_dir):
        os.makedirs(external_dir)
    if not os.path.exists(external_bin_dir):
        os.makedirs(external_bin_dir)
    if not os.path.exists(external_src_dir):
        os.makedirs(external_src_dir)
    sp.check_call([sudo, apt_get, "install", "maven", "openjdk-7-jdk"])
        
    # install postgresql jdbc driver
    postgresql_jdbc_file = os.path.join(external_bin_dir, postgresql_jdbc_name)
    if not os.path.exists(postgresql_jdbc_file):
        sp.check_call(["wget", postgresql_jdbc_url], cwd=external_bin_dir)
    sp.check_call([mvn, "install:install-file", \
        "-Dfile=%s" % postgresql_jdbc_file, "-DartifactId=postgresql",  \
        "-DgroupId=postgresql", "-Dversion=9.2-1004.jdbc4", "-Dpackaging=jar"], cwd=external_bin_dir)
        
    # install geotools
    geotools_src_dir = os.path.join(external_src_dir, geotools_src_dir_name)
    if not os.path.exists(geotools_src_dir) or len(os.listdir(geotools_src_dir)) == 0:
        sp.check_call([wget, "http://downloads.sourceforge.net/project/geotools/GeoTools%209%20Releases/9.2/geotools-9.2-project.zip"], cwd=tmp_path)
        sp.check_call([unzip, os.path.join(tmp_path, "geotools-9.2-project.zip")], cwd=external_src_dir)
    if not skip_build:
        mvn_settings_file_path = os.path.join(base_path, "settings.xml")
        sp.check_call([mvn, "--global-settings", mvn_settings_file_path, "install", "-Dall", "-DskipTests=true"], cwd=geotools_src_dir)
        
    # install postgis
    postgis_src_dir = os.path.join(external_src_dir, postgis_src_dir_name)
    if not os.path.exists(postgis_src_dir) or len(os.listdir(postgis_src_dir)) == 0:
        sp.check_call([wget, "http://download.osgeo.org/postgis/source/postgis-2.1.1.tar.gz"], cwd=tmp_path)
        sp.check_call([tar, "xf", os.path.join(tmp_path, "postgis-2.1.1.tar.gz")], cwd=external_src_dir)
    if not skip_build:
        sp.check_call([sudo, apt_get, "build-dep", "postgis"]) # might not be sufficient because Ubuntu 13.10's version of postgis is 1.5.x (we're using 2.x)
        sp.check_call([sudo, apt_get, "install", "--assume-yes", "libgdal-dev"]) # not covered by 1.5.x requirements (see above)
        sp.check_call([bash, "autogen.sh"], cwd=postgis_src_dir)
        sp.check_call([bash, "configure"], cwd=postgis_src_dir)
        sp.check_call([make, "-j8"], cwd=postgis_src_dir)
        sp.check_call([sudo, make, "install"], cwd=postgis_src_dir)
        postgis_jdbc_src_dir = os.path.join(postgis_src_dir, "java", "jdbc")
        sp.check_call([ant], cwd=postgis_jdbc_src_dir) # generates maven project
        sp.check_call([mvn, "install"], cwd=postgis_jdbc_src_dir)
        
    # setup postgis datadir and configuration
    sp.check_call([sudo, apt_get, "install", "--assume-yes", "postgresql", "postgresql-common"])
    if not os.path.exists(postgres_datadir_path) or len(os.listdir(postgres_datadir_path)) == 0:
        # os.mkdir(postgres_datadir_path) # unnecessary
        sp.check_call([initdb, "-D", postgres_datadir_path])
        postgres_process = sp.Popen(["/usr/lib/postgresql/9.1/bin/postgres", "-D", postgres_datadir_path, "-p", postgres_port, "-h", postgres_host, "-k", postgres_socket_dir])
        try:
            print("sleeping to ensure postgres server started")
            time.sleep(10) # not nice (should poll connection until success instead)
            sp.check_call([createdb, "-p", postgres_port, "-h", postgres_host, postgres_user])
            sp.check_call([createdb, "-p", postgres_port, "-h", postgres_host, pgalise_user])
            os.system("echo '%s' | psql -p %s -h %s" % ("create role pgalise;", postgres_port, postgres_host)) # specifying command using --command parameter of psql doesn't seem to work
            os.system("echo '%s' | psql -p %s -h %s" % ("grant all on database pgalise to pgalise", postgres_port, postgres_host))
            os.system("echo '%s' | psql -p %s -h %s" % ("create extension postgis; create extension postgis_topology;", postgres_port, postgres_host))
        finally:
            postgres_process.terminate()
        
    # install commons-collections 4
    commons_src_dir = os.path.join(external_src_dir, commons_src_dir_name)
    if not os.path.exists(commons_src_dir) or len(os.listdir(commons_src_dir)) == 0:
        sp.check_call([wget, "http://mirror.derwebwolf.net/apache//commons/collections/source/commons-collections4-4.0-src.tar.gz"], cwd=tmp_path)
        sp.check_call([tar, "xf", os.path.join(tmp_path, "commons-collections4-4.0-src.tar.gz")], cwd=external_src_dir)
    if not skip_build:
        sp.check_call([mvn, "install"], cwd=commons_src_dir)
        
if __name__ == "__main__":
    bootstrap()
