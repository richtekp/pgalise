#!/usr/bin/python
# -*- coding: utf-8 -*- 

__name__ = "postgis_utils"

import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

import subprocess as sp
import os
import time

# different possiblities of installation of postgis and hstore extension on database (extension support is dropped for postgis above 2.x), some package maintainers include scripts nevertheless -> let the caller choose
EXTENSION_INSTALL_EXTENSION = 1 # use the extension command of postgres
EXTENSION_INSTALL_SQL_FILE= 2 # use 
EXTENSION_INSTALLS = [EXTENSION_INSTALL_EXTENSION, EXTENSION_INSTALL_SQL_FILE]
extension_install_default = EXTENSION_INSTALLS[0]

pwfile_path = "./pwfile" # it's not wise to write to a file in system's temporary file directory which is readable for everybody

# user privileges should be handled by caller (e.g. with os.fork)
# @args extension_install on of EXTENSION_INSTALLS
# @raise ValueError is extension_install is not one of EXTENSION_INSTALLS
def bootstrap_datadir(datadir_path, db_user, password="somepw", initdb="initdb"):
    pwfile = open(pwfile_path, "w")
    pwfile.write(password)
    pwfile.flush()
    pwfile.close()
    sp.check_call([initdb, "-D", datadir_path, "--username=%s" % db_user, "--pwfile=%s" % pwfile_path])
    os.remove(pwfile_path)

def bootstrap_database(datadir_path, db_port, db_host, db_user, db_name, password="somepw", initdb="initdb", postgres="postgres", createdb="createdb", psql="psql", socket_dir="/tmp", extension_install=extension_install_default):    
    if not extension_install in EXTENSION_INSTALLS:
        raise ValueError("extension_install has to be one of %s" % str(EXTENSION_INSTALLS))
    postgres_process = sp.Popen([postgres, "-D", datadir_path, "-p", str(db_port), "-h", db_host, "-k", socket_dir])
    try:
        logger.info("sleeping 10 s to ensure postgres server started")
        time.sleep(10) # not nice (should poll connection until success instead)
        sp.check_call([createdb, "-p", str(db_port), "-h", db_host, "--username=%s" % db_user, db_name])
        #sp.check_call([psql, "-c", "create role pgalise login;", "-p", db_port, "-h", db_host, "--username=%s" % user]) # unnecessary if initdb is invoked with --username
        sp.check_call([psql, "-c", "grant all on database %s to %s;" % (db_name, db_user), "-p", str(db_port), "-h", db_host, "--username=%s" % db_user])
        if extension_install == EXTENSION_INSTALL_EXTENSION:
            sp.check_call([psql, "-d", db_name, "-c", "create extension postgis; create extension postgis_topology;", "-p", str(db_port), "-h", db_host, "--username=%s" % db_user]) # hstore handled below
        elif extension_install == EXTENSION_INSTALL_SQL_FILE:
            # ON_ERROR_STOP=1 causes the script to fail after the first failing command which is very useful for debugging as proceed doesn't make sense in a lot of cases
            sp.check_call([psql, "-d", db_name, "-f", "/usr/share/postgresql/%s/contrib/postgis-%s/postgis.sql" % (pg_version, postgis_version_string), "-p", str(db_port), "-h", db_host, "--username=%s" % db_user, "-v", "ON_ERROR_STOP=1"])
            sp.check_call([psql, "-d", db_name, "-f", "/usr/share/postgresql/%s/contrib/postgis-%s/topology.sql" % (pg_version, postgis_version_string), "-p", str(db_port), "-h", db_host, "--username=%s" % db_user, "-v", "ON_ERROR_STOP=1"])
            sp.check_call([psql, "-d", db_name, "-f", "/usr/share/postgresql/%s/contrib/postgis-%s/spatial_ref_sys.sql" % (pg_version, postgis_version_string), "-p", str(db_port), "-h", db_host, "--username=%s" % db_user, "-v", "ON_ERROR_STOP=1"])
        sp.check_call([psql, "-d", db_name, "-c", "create extension hstore;", "-p", str(db_port), "-h", db_host, "--username=%s" % db_user, "-v", "ON_ERROR_STOP=1"]) # no sql file for hstore found, so install it from postgresql-contrib-x.x deb package (find another way (check wiki for existing solutions) if trouble occurs)
    finally:
        postgres_process.terminate()
        logger.info("waiting for postgres process to terminate")
        postgres_process.wait()

