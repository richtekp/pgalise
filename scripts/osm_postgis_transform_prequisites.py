#!/usr/bin/python
# -*- coding: utf-8 -*- 

import argparse
import sys
import os
sys.path.append(os.path.realpath(os.path.join(__file__, "..", 'lib')))
import pm_utils
import check_os
import os_utils
import subprocess as sp
import string

skip_apt_update_default = False
skip_apt_update_option = "s"
skip_apt_update_option_long = "skip-apt-update"

parser = argparse.ArgumentParser(description='Process some integers.')
parser.add_argument("-%s" % skip_apt_update_option, "--%s" % skip_apt_update_option_long, default=skip_apt_update_default, type=bool, nargs='?',
                   help='whether invokation of `apt-get update` ought to be skipped (useful if it has been issues shortly before the invokation of the script and changes are unlikely', dest=skip_apt_update_option_long)

# @args also install db (postgresql and postgis) related prequisites
def install_prequisites(db=True, skip_apt_update=skip_apt_update_default):
    if check_os.check_ubuntu() or check_os.check_debian():
        if not db:
            pm_utils.install_packages(["osm2pgsql"], package_manager="apt-get", skip_apt_update=skip_apt_update, assume_yes=False)
        else:
            release_tuple = check_os.findout_release_ubuntu_tuple()
            install_postgresql(skip_apt_update=skip_apt_update)
    else:
        if not db:
            raise RuntimeError("implement simple installation of only prequisite osm2pgsql")
        else:
            install_postgresql(skip_apt_update=skip_apt_update)

def install_postgresql(pg_version=(9,2), skip_apt_update=skip_apt_update_default):
    if check_os.check_ubuntu() or check_os.check_debian():
        if check_os.check_ubuntu():            
            release_tuple = check_os.findout_release_ubuntu_tuple()
            if release_tuple > (12,4) and release_tuple < (13,10):
                release = "precise" # repository provides for precise, saucy and trusty
            else:
                release = check_os.findout_release_ubuntu()
        elif check_os.check_debian():
            release = check_os.findout_release_debian()
        else:
            raise RuntimeError("operating system not supported")
        apt_url = "http://apt.postgresql.org/pub/repos/apt/"
        release_spec = "%s-pgdg" % release
        identifier = "main"
        deb_line_re = pm_utils.generate_deb_line_re(apt_url, release_spec, identifier)
        ppa_sources_d_file = None
        ppa_spec = "deb %s %s %s" % (apt_url, release_spec, identifier)
        apt_line_added = pm_utils.lazy_add_apt_source_line(deb_line_re, ppa_sources_d_file, ppa_spec)
        if apt_line_added:
            os.system("wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -")
            pm_utils.invalidate_apt() 
        pg_version_string = string.join([str(x) for x in pg_version],".")
        try:
            pm_utils.install_packages([ 
                "postgresql-%s" % pg_version_string, 
                "postgresql-%s-postgis-2.1" % pg_version_string, 
                "postgresql-%s-postgis-2.1-scripts" % pg_version_string,
                "postgresql-contrib-%s" % pg_version_string, 
                "postgresql-client-common", # version independent, no package per version
            ], package_manager="apt-get", skip_apt_update=skip_apt_update)
        except sp.CalledProcessError as ex:
            print("postgresql installation failed (which is possible due to broken package in Ubuntu 13.10")
            #pm_utils.remove_packages(["postgresql", "postgresql-common"], package_manager="apt-get", skip_apt_update=skip_apt_update) 
            #postgresql_deb_path = os.path.join(tmp_dir, postgresql_deb_name)
            #if not check_file(postgresql_deb_path, postgresql_deb_md5):
            #    do_wget(postgresql_deb_url, postgresql_deb_path)
            #    sp.check_call([dpkg, "-i", postgresql_deb_path])
            psql = "/opt/postgres/%s/bin/psql" % pg_version_string
            initdb = "/opt/postgres/%s/bin/initdb" % pg_version_string
            createdb = "/opt/postgres/%s/bin/createdb" % pg_version_string
            postgres = "/opt/postgres/%s/bin/postgres" % pg_version_string
        # osmpgsql
        pm_utils.install_packages(["osm2pgsql"], package_manager="apt-get", skip_apt_update=skip_apt_update)
    elif check_os.check_opensuse():
        if pg_version == (9,2):
            sp.check_call([zypper, "install", "postgresql", "postgresql-contrib", "postgresql-devel", "postgresql-server"])
            psql = "/usr/lib/postgresql92/bin/psql"
            initdb = "/usr/lib/postgresql92/bin/initdb" 
            createdb = "/usr/lib/postgresql92/bin/createdb"
            postgres = "/usr/lib/postgresql92/bin/postgres"
        else:
            # better to let the script fail here than to get some less comprehensive error message later
            raise RuntimeError("postgresql version %s not supported" % string.join([str(x) for x in pg_version],"."))
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("operating system not supported!")

if __name__ == "__main__":
    args = vars(parser.parse_args())
    skip_apt_update=args[skip_apt_update_option_long]
    install_prequisites(skip_apt_update=skip_apt_update)

