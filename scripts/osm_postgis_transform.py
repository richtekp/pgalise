#!/usr/bin/python
# -*- coding: utf-8 -*- 

import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

import subprocess as sp
import os
import time
import string
import argparse
import sys
sys.path.append(os.path.realpath(os.path.join(__file__, "..", 'lib')))
import pm_utils
import check_os
import postgis_utils

pg_version = "9.2"
postgis_version = (2,0)
postgis_version_string = string.join([str(i) for i in postgis_version],".")
initdb = "/usr/lib/postgresql/%s/bin/initdb" % pg_version
postgres = "/usr/lib/postgresql/%s/bin/postgres" % pg_version
psql = "/usr/lib/postgresql/%s/bin/psql" % pg_version
createdb = "/usr/lib/postgresql/%s/bin/createdb" % pg_version
osm2pgsql = "osm2pgsql"
osm2pgsql_number_processes = int(sp.check_output(["grep", "-c", "^processor", "/proc/cpuinfo"]).strip())
db_socket_dir = "/tmp"
unprivileged_uid = 1000

start_db_default = False
start_db_option = "t"
start_db_option_long = "start-db"
db_host_default = "richter-local.de"
db_host_option = "j"
db_host_option_long = "host"
db_port_default = 5204
db_port_option = "p"
db_port_option_long = "port"
db_user_default = "postgis"
db_user_option = "u"
db_user_option_long = "user"
db_password_default = "postgis"
db_password_option = "w"
db_password_option_long = "password"
db_name_default = "postgis"
db_name_option = "n"
db_name_option_long = "database-name"

data_dir_default = "/mnt/osm_postgis/postgis-%s" % pg_version
data_dir_option = "d"
data_dir_option_long = "data-dir"

osm_files_option = "o"
osm_files_option_long = "osm-files"

cache_size_default=1000
cache_size_option = "c"
cache_size_option_long = "cache-size"

skip_apt_update_default = False
skip_apt_update_option = "s"
skip_apt_update_option_long = "skip-apt-update"

install_prequisites_default = False
install_prequisites_option = "i"
install_prequisites_option_long = "install-prequisites"

parser = argparse.ArgumentParser(description='Process some integers.')
parser.add_argument("-%s" % data_dir_option, "--%s" % data_dir_option_long, type=str, nargs='?',
                   help='', dest=data_dir_option_long, required=True)
parser.add_argument("-%s" % osm_files_option, "--%s" % osm_files_option_long, type=str, nargs='+',
                   help='list of OSM files to be passed to osm2pgsql', dest=osm_files_option_long, required=True)
parser.add_argument("-%s" % cache_size_option, "--%s" % cache_size_option_long, type=int, nargs='?', default=cache_size_default, 
                   help='size of osm2pgsql cache', dest=cache_size_option_long)
parser.add_argument("-%s" % skip_apt_update_option, "--%s" % skip_apt_update_option_long, default=skip_apt_update_default, type=bool, nargs='?',
                   help='whether invokation of `apt-get update` ought to be skipped (useful if it has been issues shortly before the invokation of the script and changes are unlikely', dest=skip_apt_update_option_long)
parser.add_argument("-%s" % install_prequisites_option, "--%s" % install_prequisites_option_long, default=install_prequisites_default, type=bool, nargs='?',
                   help='install postgresql and postgis packages with package manager', dest=install_prequisites_option_long)                   
parser.add_argument("-%s" % start_db_option, "--%s" % start_db_option_long, default=start_db_default, type=bool, nargs='?',
                   help='whether to start an postgres process as a child of the script process  (using the following arguments) or to connect to an already running process (using the following arguments)', dest=start_db_option_long)
parser.add_argument("-%s" % db_host_option, "--%s" % db_host_option_long, default=db_host_default, type=str, nargs='?',
                   help='host where the database ought to be available for further connections (if -%s (--%s) is True) or the host where to reach the already running postgres process (if -%s (--%s) is False)' % (start_db_option, start_db_option_long, start_db_option, start_db_option_long), dest=db_host_option_long)
parser.add_argument("-%s" % db_port_option, "--%s" % db_port_option_long, default=db_port_default, type=int, nargs='?',
                   help='@TODO', dest=db_port_option_long)
parser.add_argument("-%s" % db_user_option, "--%s" % db_user_option_long, default=db_user_default, type=str, nargs='?',
                   help='@TODO', dest=db_user_option_long)
parser.add_argument("-%s" % db_password_option, "--%s" % db_password_option_long, default=db_password_default, type=str, nargs='?',
                   help='@TODO', dest=db_password_option_long)
parser.add_argument("-%s" % db_name_option, "--%s" % db_name_option_long, default=db_name_default, type=str, nargs='?',
                   help='@TODO', dest=db_name_option_long)

# fails by default because osm_files mustn't be empty
# @args db_user when <tt>start_db</tt> is <code>True</code> used as superuser name, otherwise user to connect as to the database denotes by <tt>db_*</tt> parameter of this function
def osm_postgis_transform(data_dir=data_dir_default, osm_files=[], cache_size=cache_size_default, skip_apt_update=skip_apt_update_default, install_prequisites=install_prequisites_default, start_db=start_db_default, db_host=db_host_default, db_port=db_port_default, db_user=db_user_default, db_password=db_password_default, db_name=db_name_default):
    if osm_files is None:
        raise ValueError("osm_files mustn't be None")
    if str(type(osm_files)) != "<type 'list'>":
        raise ValueError("osm_files has to be a list")
    if len(osm_files) == 0:
        raise ValueError("osm_files mustn't be empty")
    if pg_version == "9.2":
        if postgis_version > (2,0):
            raise ValueError("postgis > %s is not compatible with postgresql %s" % (postgis_version_string, pg_version))
        
    # install prequisites
    if install_prequisites:
        if check_os.check_ubuntu() or check_os.check_debian():
            if not start_db:
                pm_utils.install_packages(["osm2pgsql"], package_manager="apt-get")
            else:
                release_tuple = check_os.findout_release_ubuntu_tuple()
                if release_tuple == (14,04):
                    print("apt.postgresql.org dependencies broken, install from sources")
                else:
                    install_prequisites_package_manager(skip_apt_update=skip_apt_update)
        else:
            if not start_db:
                raise RuntimeError("implement simple installation of only prequisite osm2pgsql")
            else:
                install_prequisites_package_manager(skip_apt_update=skip_apt_update)
    # always check, even after install_prequisites
    #@TODO: not sufficient to binary name; necessary to evaluate absolute path with respect to $PATH
    #if not os.path.exists(osm2pgsql):
    #    raise RuntimeError("osm2pgsql %s not found, make sure to install it or invoke the script with -%s (--%s)" % (osm2pgsql, install_prequisites_option, install_prequisites_option_long))
    
    # parsing
    # postgres binary refuses to run when process uid and effective uid are not identical
    newpid = os.fork()
    if newpid == 0:
        os.setuid(1000)
        postgres_proc = None
        try:
            if start_db:
                if not os.path.exists(data_dir) or len(data_dir) == 0:
                    postgis_utils.bootstrap_datadir(data_dir, db_user, password=db_password, initdb=initdb)
                    postgis_utils.bootstrap_database(data_dir, db_port, db_host, db_user, db_name, password=db_password, initdb=initdb, postgres=postgres, createdb=createdb, psql=psql, socket_dir=db_socket_dir)
                if postgres_proc is None:
                    postgres_proc = sp.Popen([postgres, "-D", data_dir, "-p", str(db_port), "-h", db_host, "-k", db_socket_dir])
                    print("sleeping 10 s to ensure postgres server started")
                    time.sleep(10) # not nice (should poll connection until success instead)
            osm2pgsql_proc = sp.Popen([osm2pgsql, "--create", "--database", db_name, "--cache", str(cache_size), "--number-processes", str(osm2pgsql_number_processes), "--slim", "--port", str(db_port), "--host", db_host, "--username", db_user, "--latlong", "--password", "--keep-coastlines", "--extra-attributes", "--hstore-all"]+osm_files)
            #osm2pgsql_proc.communicate(db_password) # doesn't seem to be possible to pipe the password into the prompt and there's no option to pass password or a password file @TODO: check whether piping to stdin works
            osm2pgsql_proc.wait()
        finally:
            if not postgres_proc is None:
                postgres_proc.terminate()
                postgres_proc.wait()
        os._exit(0)
    else:
        os.waitpid(newpid, 0)

def install_prequisites_package_manager(skip_apt_update=skip_apt_update_default):
    if check_os.check_ubuntu() or check_os.check_debian():
        if check_os.check_ubuntu():
            release_tuple = check_os.finout_release_ubuntu_tuple()
            if release_tuple > (12,4):
                release = "precise" # latest supported release for the repository
            else:
                release = check_os.findout_release_ubuntu()
        else:
            release = check_os.findout_release_debian()
        release_spec = "%s-pgdg" % release
        apt_line = "http://apt.postgresql.org/pub/repos/apt/"
        deb_line_re = pm_utils.generate_deb_line_re(apt_line, release_spec, "main")
        deb_line = pm_utils.generate_deb_line(apt_line, release_spec, "main")
        deb_line_added = pm_utils.lazy_add_apt_source_line(deb_line_re, None, deb_line)
        if deb_line_added:
            os.system("wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -")
            sp.check_call(["apt-get", "update"])
        pm_utils.install_packages(["postgresql-%s" % pg_version, 
                    "postgresql-%s-postgis-2.1" % pg_version, 
                    "postgresql-%s-postgis-2.1-scripts" % pg_version,
                    "postgresql-contrib-%s" % pg_version, 
                    "postgresql-client-common", # version independent, no package per version
                    "osm2pgsql",
                    ], package_manager="apt-get", skip_apt_update=skip_apt_update)
    else:
        raise RuntimeError("operating system not supported!")

if __name__ == "__main__":
    args = vars(parser.parse_args())
    data_dir = args[data_dir_option_long]
    osm_postgis_transform(data_dir = data_dir, osm_files=args[osm_files_option_long], cache_size=args[cache_size_option_long],skip_apt_update=args[skip_apt_update_option_long], install_prequisites=args[install_prequisites_option_long], db_user=args[db_user_option_long], db_password=args[db_password_option_long], db_host=args[db_host_option_long], db_port=args[db_port_option_long], db_name=args[db_name_option_long], start_db=args[start_db_option_long])

