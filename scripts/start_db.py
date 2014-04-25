#!/usr/bin/python

import os
import sys
import datetime
import subprocess as sp
import argparse
import string

base_dir_path = os.path.realpath(os.path.join(os.path.dirname(__file__), ".."))
script_dir = os.path.join(base_dir_path, "scripts")
sys.path.append(os.path.join(script_dir, "lib"))
sys.path.append("lib")
import check_os

postgresql_pgalise_version_default = tuple([9,2])
postgresql_pgalise_version_default_string = string.join([str(x) for x in postgresql_pgalise_version_default], ".")
postgresql_pgalise_version_option = "p"
postgresql_pgalise_version_option_long = "postgresql-pgalise-version"
postgresql_osm_version_default = tuple([9,2])
postgresql_osm_version_default_string = string.join([str(x) for x in postgresql_osm_version_default], ".")
postgresql_osm_version_option = "o"
postgresql_osm_version_option_long = "postgresql-osm-version"
pgalise_data_dir_default = os.path.join(base_dir_path, "postgis_db-%s" % postgresql_pgalise_version_default_string)
pgalise_data_dir_option = "b"
pgalise_data_dir_option_long = "pgalise-data-dir"
osm_data_dir_default = "/mnt/osm_postgis/postgis_db-%s" % postgresql_pgalise_version_default_string
osm_data_dir_option = "a"
osm_data_dir_option_long = "osm-data-dir"

tmp_dir = "/tmp" # os.path.join(script_dir, "tmp") #

#class VersionTupleAction(argparse.Action):
#    def __call__(self, parser, namespace, values, option_string=None):
#        print('%r %r %r' % (namespace, values, option_string))
#        setattr(namespace, self.dest, values)

def version_tuple(string):
    ret_value = tuple([int(x) for x in string.split(".")])
    return ret_value

parser = argparse.ArgumentParser(description='Process some integers.')
parser.add_argument("-%s" % postgresql_pgalise_version_option, "--%s" % postgresql_pgalise_version_option_long, nargs="?", type=version_tuple, default=postgresql_pgalise_version_default,
                   help='postgres version to use for pgalise database server', dest=postgresql_pgalise_version_option)
parser.add_argument("-%s" % postgresql_osm_version_option, "--%s" % postgresql_osm_version_option_long, nargs="?", type=version_tuple, default=postgresql_osm_version_default,
                   help='postgres version to use for OSM PostGIS database server', dest=postgresql_osm_version_option)
parser.add_argument("-%s" % pgalise_data_dir_option, "--%s" % pgalise_data_dir_option_long, nargs="?", type=str, default=pgalise_data_dir_default,
                   help='data directory for to use for pgalise database serser', dest=pgalise_data_dir_option)
parser.add_argument("-%s" % osm_data_dir_option, "--%s" % osm_data_dir_option_long, nargs="?", type=str, default=osm_data_dir_default,
                   help='data directory for to use for OSM PostGIS database serser', dest=osm_data_dir_option)

def start_db(postgresql_pgalise_version=postgresql_pgalise_version_default, postgresql_osm_version=postgresql_osm_version_default, pgalise_data_dir=pgalise_data_dir_default, osm_data_dir=osm_data_dir_default):
    if not os.path.exists(tmp_dir):
        os.makedirs(tmp_dir)
    postgresql_pgalise_version_string = string.join([str(x) for x in postgresql_pgalise_version], ".")
    postgresql_osm_version_string = string.join([str(x) for x in postgresql_osm_version], ".")
    if check_os.check_ubuntu():
        pgalise_psql = "/usr/lib/postgresql/%s/bin/psql" % postgresql_pgalise_version_string
        pgalise_initdb = "/usr/lib/postgresql/%s/bin/initdb" % postgresql_pgalise_version_string
        pgalise_createdb = "/usr/lib/postgresql/%s/bin/createdb" % postgresql_pgalise_version_string
        pgalise_postgres = "/usr/lib/postgresql/%s/bin/postgres" % postgresql_pgalise_version_string
        osm_psql = "/usr/lib/postgresql/%s/bin/psql" % postgresql_osm_version_string
        osm_initdb = "/usr/lib/postgresql/%s/bin/initdb" % postgresql_osm_version_string
        osm_createdb = "/usr/lib/postgresql/%s/bin/createdb" % postgresql_osm_version_string
        osm_postgres = "/usr/lib/postgresql/%s/bin/postgres" % postgresql_osm_version_string
    elif check_os.check_opensuse():
        opensuse_postgresql_pgalise_version_string = string.join([str(x) for x in postgresql_pgalise_version], "")
        pgalise_psql = "/usr/lib/postgresql%s/bin/psql" % opensuse_postgresql_pgalise_version_string
        pgalise_initdb = "/usr/lib/postgresql%s/bin/initdb"  % opensuse_postgresql_pgalise_version_string
        pgalise_createdb = "/usr/lib/postgresql%s/bin/createdb" % opensuse_postgresql_pgalise_version_string
        pgalise_postgres = "/usr/lib/postgresql%s/bin/postgres" % opensuse_postgresql_pgalise_version_string
        opensuse_postgresql_osm_version_string = string.join([str(x) for x in postgresql_osm_version], "")
        osm_psql = "/usr/lib/postgresql%s/bin/psql" % opensuse_postgresql_osm_version_string
        osm_initdb = "/usr/lib/postgresql%s/bin/initdb"  % opensuse_postgresql_osm_version_string
        osm_createdb = "/usr/lib/postgresql%s/bin/createdb" % opensuse_postgresql_osm_version_string
        osm_postgres = "/usr/lib/postgresql%s/bin/postgres" % opensuse_postgresql_osm_version_string

    log_dir = os.path.join(base_dir_path, "log")
    if not os.path.exists(log_dir):
        os.mkdir(log_dir)
    timestamp_suffix = datetime.datetime.today().isoformat()
    terminal_cmds = ["xterm", "-hold", "-e"]

    postgres_log_file_path = os.path.join(log_dir, "postgis-%s.log" % timestamp_suffix)
    postgres_log_file = None # open(postgres_log_file_path, "w") # None doesn't work correctly on OpenSUSE (no output) (not sure which xterm version)
    postgres_proc = sp.Popen(terminal_cmds+[pgalise_postgres, "-D", pgalise_data_dir, "-p", "5201", "-h", "localhost", "-k", tmp_dir], stdout=postgres_log_file)
    print("started pgalise postgresql database process with data directory %s, logging to %s" % (pgalise_data_dir, postgres_log_file_path))
    osm_postgres_log_file_path = os.path.join(log_dir, "postgis-osm-%s.log" % timestamp_suffix)
    osm_postgres_log_file = None # open(osm_postgres_log_file_path, "w") # None doesn't work correctly on OpenSUSE (no output) (not sure which xterm version)
    osm_postgres_proc = sp.Popen(terminal_cmds+[osm_postgres, "-D", osm_data_dir, "-p", "5204", "-h", "localhost", "-k", tmp_dir], stdout=osm_postgres_log_file)
    print("started PostGIS postgresql database process with data directory %s, logging to %s" % (osm_data_dir, osm_postgres_log_file_path))
        
    print("waiting for both terminal processes (of pgalise and PostGIS database processes) to terminate")
    postgres_proc.communicate() # necessary to prevent Popen from waiting for ever
    postgres_proc.wait()
    osm_postgres_proc.communicate()
    osm_postgres_proc.wait()

if __name__ == "__main__":
    args = vars(parser.parse_args())
    start_db(postgresql_pgalise_version=args[postgresql_pgalise_version_option], postgresql_osm_version=args[postgresql_osm_version_option], pgalise_data_dir=args[pgalise_data_dir_option], osm_data_dir=args[osm_data_dir_option])

