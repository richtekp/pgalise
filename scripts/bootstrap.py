#!/usr/bin/python

# check that all necessary build tools and helpers are installed
# the installation requires root privileges because postgis handling of GNU autotools prefix is a mess (and officially ignored as staded in the INSTALL information) 
# an installed postgres server and root access (allowing installation of extensions and having grant privileges) to it is required
# all build commands are executed using python's subprocess.check_call which throws a RuntimeError is an error in the command occurs, so you should be able to find out what went wrong in the output
# The script assumes that a postgres server is being run by the user in order to execute pgalise, i.e. it doesn't integrate into the system postgres server. The scripts starts and shuts down a server in order to setup the database
# not using Ubuntu ppa on https://launchpad.net/~ubuntugis/ because it is not available for saucy, so reducing portablility
# not yet paying attention to pg_version (e.g. in OpenSUSE)

import os
import subprocess as sp
import time
import re
import sys

pg_version="9.2" # provides best compatibility with OpenSUSE because in Ubuntu OpenDSG repository can be used

base_dir = os.path.realpath(os.path.join(os.path.dirname(__file__), ".."))
script_dir = os.path.join(base_dir, "scripts")
bootstrap_dir = os.path.realpath(os.path.join(base_dir, "..", "pgalise-bootstrap"))
sys.path.append(os.path.join(script_dir, "lib"))
import check_os
import user_group_utils
external_dir = os.path.join(bootstrap_dir, "external")
internal_dir = os.path.join(bootstrap_dir, "internal")
# used for postgresql socket
tmp_dir = os.path.join(script_dir, "tmp") # "/tmp"
external_bin_dir = os.path.join(external_dir, "bin")
external_src_dir = os.path.join(external_dir, "src")
arch = check_os.findout_architecture()
if pg_version == "9.2":
    postgresql_jdbc_name = "postgresql-9.2-1004.jdbc4.jar"
    postgresql_jdbc_md5 = "23494e0c770cb03045a93d07a8ed2a9f"
    postgresql_jdbc_url = "http://jdbc.postgresql.org/download/postgresql-9.2-1004.jdbc4.jar"
    if arch == "x86_64":
        postgresql_deb_url = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.2.7-1.amd64.openscg.deb"
        postgresql_deb_name = "postgres_9.2.7-1.amd64.openscg.deb"
        postgresql_deb_md5 = "cea3857cdf42cf18d3995333532e46d0"
    elif arch == "i386":
        postgresql_deb_url = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.2.7-1.i386.openscg.deb"
        postgresql_deb_name = "postgres_9.2.7-1.i386.openscg.deb"
        postgresql_deb_md5 = "c180d526ef27c31c8e140583bc64a88f"
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("architecture %s not supported" % arch)
elif pg_version == "9.3":
    postgresql_jdbc_name = "postgresql-9.2-1004.jdbc4.jar" #@TODO: not optimal
    postgresql_jdbc_md5 = "23494e0c770cb03045a93d07a8ed2a9f"
    postgresql_jdbc_url = "http://jdbc.postgresql.org/download/postgresql-9.2-1004.jdbc4.jar"#@TODO: not optimal
    if arch == "x86_64":
        postgresql_deb_url = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.3.1-1.amd64.openscg.deb"
        postgresql_deb_name = "postgres_9.3.1-1.amd64.openscg.deb"
        postgresql_deb_md5 = "9a4f6c6be47a5ea594c3bececc8e82a6"
    elif arch == "i386":
        postgresql_deb_url = "http://oscg-downloads.s3.amazonaws.com/packages/postgres_9.3.1-1.i386.openscg.deb"
        postgresql_deb_name = "postgres_9.3.1-1.i386.openscg.deb"
        postgresql_deb_md5 = "a659ccb8b5fc838a494780ad05a5f856"
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("architecture %s not supported" % arch)
else:
    # better to let the script fail here than to get some less comprehensive error message later
    raise RuntimeError("postgresql version %s is not supported (this needs to be fixed in sources)" % pg_version)
geotools_url = "http://downloads.sourceforge.net/project/geotools/GeoTools%209%20Releases/9.2/geotools-9.2-project.zip"
geotools_md5 = "18cc0f21557b2187a89eebd965cbe409"
geotools_src_dir_name = "geotools-9.2"
geotools_src_archive_name = "geotools-9.2-project.zip"
postgis_src_dir_name="postgis-2.1.1"
postgis_url = "http://download.osgeo.org/postgis/source/postgis-2.1.1.tar.gz"
postgis_archive_name = "postgis-2.1.1.tar.gz"
commons_src_dir_name = "commons-collections4-4.0-src"
# no repository provides jgrapht 0.8.3 as org.jgrapht:jgrapht:0.8.3 artifact, but as net.sf.jgrapht:jgrapht:0.8.3 -> download and install manually
jgrapht_url = "http://repo1.maven.org/maven2/net/sf/jgrapht/jgrapht/0.8.3/jgrapht-0.8.3.jar"
jgrapht_name = "jgrapht-0.8.3.jar"
jgrapht_md5 = "7b275e7bf54f02ceab2bf5132a8ef6ce"
# no repository provides pcingola:jfuzzylogic:3.0
jfuzzy_url = "http://sourceforge.net/projects/jfuzzylogic/files/jfuzzylogic/jFuzzyLogic_v3.0.jar/download"
jfuzzy_name = "jFuzzyLogic_v3.0.jar"
jfuzzy_md5 = "17e1d0e309c3a00321db4f308dbae404"
maven_bin_archive_name = "apache-maven-3.2.1-bin.tar.gz"
maven_bin_archive_md5 = "aaef971206104e04e21a3b580d9634fe"
maven_bin_dir_name = "apache-maven-3.2.1"
maven_bin_dir_url = "http://ftp.fau.de/apache/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz"
maven_bin_dir_install_target = "/usr/local/share/maven-3.2.1"

# necessary build tools and helpers 
# <ul>
wget = "wget"
unzip="unzip"
mvn = "mvn"
tar = "tar"
bash = "dash" # bash and ksh cause error when running autogen.sh and configure
make = "make"
sudo = "sudo"
ant = "ant"
su = "su"
psql = "/usr/lib/postgresql/%s/bin/psql" % pg_version
initdb = "/usr/lib/postgresql/%s/bin/initdb" % pg_version
createdb = "/usr/lib/postgresql/%s/bin/createdb" % pg_version
postgres = "/usr/lib/postgresql/%s/bin/postgres" % pg_version
apt_get = "apt-get"
zypper = "zypper"
md5sum = "md5sum"
dpkg = "dpkg"
add_apt_repo = "add-apt-repository"
cp="cp"
chown = "chown"
# </ul>

# some variables to be changed at will (would be more elegant to enable controll as options of this script)
#postgres_user = sp.check_output(["id", "-u", "-n"]).strip()
postgres_user = "pgalise"
postgres_pw = "somepw"
postgres_datadir_path = os.path.join(base_dir, "postgis_db-%s" % pg_version)
postgres_port = "5201"
postgres_host = "localhost"
postgres_socket_dir = tmp_dir # is this ok?
pgalise_db_name = "pgalise"
pgalise_db_test_name = "pgalise_test"

# @args skip_build skip building of packages if directories exist
def bootstrap(skip_build=False, psql=psql, initdb=initdb, createdb=createdb, postgres=postgres, privileged_uid=0, unprivileged_uid=1000):
    if int(sp.check_output(["id", "-u"]).strip().decode("utf-8")) != privileged_uid:
        raise RuntimeError("script has to be invoked as priviledged user with id %s!" % str(privileged_uid)) #@TODO: more sophisticated privileges check
    if not os.path.exists(base_dir):
        os.makedirs(base_dir)
        os.chown(base_dir, unprivileged_uid, unprivileged_uid)
    if not os.path.exists(external_dir):
        os.makedirs(external_dir)
        os.chown(external_dir, unprivileged_uid, unprivileged_uid)
    if not os.path.exists(external_bin_dir):
        os.makedirs(external_bin_dir)
        os.chown(external_bin_dir, unprivileged_uid, unprivileged_uid)
    if not os.path.exists(external_src_dir):
        os.makedirs(external_src_dir)        
        os.chown(external_src_dir, unprivileged_uid, unprivileged_uid)
    if not os.path.exists(tmp_dir):
        os.makedirs(tmp_dir)
        os.chown(tmp_dir, unprivileged_uid, unprivileged_uid)
    # install prequisites
    if check_os.check_ubuntu() or check_os.check_debian():
        sp.check_call([sudo, apt_get, "install", "maven", "openjdk-7-jdk"])
    elif check_os.check_opensuse():
        # install maven
        #sp.check_call([sudo, "/sbin/OCICLI", "http://software.opensuse.org/ymp/Application:Geo/openSUSE_12.3/maven.ymp?base=openSUSE%3A12.3&query=maven"]) # (not sure whether this installs futher instable software, provided maven version 3.0.4 isn't a hit neither)
        maven_bin_dir = os.path.join(external_bin_dir, maven_bin_dir_name)
        if not os.path.exists(maven_bin_dir) or len(os.listdir(maven_bin_dir)) == 0:
            maven_bin_archive = os.path.join(external_bin_dir, maven_bin_archive_name)
            if not os.path.exists(maven_bin_archive) or not retrieve_md5sum(maven_bin_archive) == maven_bin_archive_md5:
                sp.check_call([wget, maven_bin_dir_url], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=external_bin_dir)
            sp.check_call([tar, "xf", maven_bin_archive], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=external_bin_dir)
            shutil.copytree(maven_bin_dir, maven_bin_dir_install_target)
            for  dirpath, dirnames, filenames in os.walk(maven_bin_dir_install_target):
                os.chown(dirpath, privileged_uid)
                os.chgrp(dirpath, privileged_uid)
                for dirname in dirnames:
                    os.chown(os.path.join(dirpath, dirname), privileged_uid)
                    os.chgrp(os.path.join(dirpath, dirname), privileged_uid)
                for filename in filenames:
                    os.chown(os.path.join(dirpath, filename), privileged_uid)
                    os.chgrp(os.path.join(dirpath, filename), privileged_uid)
        # install remaining prequisites
        sp.check_call([sudo, zypper, "install", "java-1_7_0-openjdk", "java-1_7_0-openjdk-devel", "java-1_7_0-openjdk-src", "java-1_7_0-openjdk-javadoc"])
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("operating system not supported!")
        
    newpid = os.fork()
    if newpid == 0:
        os.setuid(unprivileged_uid)
        # install postgresql jdbc driver
        postgresql_jdbc_file = os.path.join(external_bin_dir, postgresql_jdbc_name)
        if not os.path.exists(postgresql_jdbc_file):
            sp.check_call([wget, postgresql_jdbc_url], cwd=external_bin_dir)
        if pg_version == "9.2":
            postgresql_jdbc_mvn_version = "9.2-1004.jdbc4"
        elif pg_version == "9.3":
            postgresql_jdbc_mvn_version = "9.2-1004.jdbc4" #@TODO: not optimal
        else:
            raise RuntimeError("postgresql version %s not supported" % pg_version)
        sp.check_call([mvn, "install:install-file", \
            "-Dfile=%s" % postgresql_jdbc_file, "-DartifactId=postgresql",  \
            "-DgroupId=postgresql", "-Dversion=%s" % postgresql_jdbc_mvn_version, "-Dpackaging=jar"], cwd=external_bin_dir)
    
        # install jgrapht (install net.sf.jgrapht:jgrapht:0.8.3 as org.jgrapht:jgrapht:0.8.3 (could not be found in any repository))
        jgrapht_file = os.path.join(external_bin_dir, jgrapht_name)
        if not os.path.exists(jgrapht_file) or retrieve_md5sum(jgrapht_file) != jgrapht_md5:
            sp.check_call([wget, jgrapht_url], cwd=external_bin_dir)
        sp.check_call([mvn, "install:install-file", \
            "-Dfile=%s" % jgrapht_file, "-DartifactId=jgrapht",
            "-DgroupId=org.jgrapht", "-Dversion=0.8.3", "-Dpackaging=jar"], cwd=external_bin_dir)
        os._exit(0)

        # install jfuzzylogic
        jfuzzy_file = os.path.join(external_bin_dir, jfuzzy_name)
        if not os.path.exists(jfuzzy_file) or retrieve_md5sum(jfuzzy_file) != jfuzzy_md5:
            sp.check_call([wget, "--output-document=%s" % jfuzzy_name, jfuzzy_url], cwd=external_bin_dir)
        sp.check_call([mvn, "install:install-file", \
            "-Dfile=%s" % jfuzzy_file, "-DartifactId=jfuzzy", 
            "-DgroupId=pcingola", "-Dversion=3.0", "-Dpackaging=jar"], cwd=external_bin_dir)
            
        # install geotools
        geotools_src_dir = os.path.join(external_src_dir, geotools_src_dir_name)
        if not os.path.exists(geotools_src_dir) or len(os.listdir(geotools_src_dir)) == 0:
            if not os.path.exists(os.path.join(external_src_dir, geotools_src_archive_name)) or retrieve_md5sum(os.path.join(external_src_dir, geotools_src_archive_name)) != geotools_md5:
                sp.check_call([wget, geotools_url], cwd=tmp_dir)
            sp.check_call([unzip, os.path.join(tmp_dir, geotools_src_archive_name)], cwd=external_src_dir)
        if not skip_build:
            mvn_settings_file_path = os.path.join(script_dir, "settings.xml")
            #sp.check_call([mvn, "--global-settings", mvn_settings_file_path, "--settings", mvn_settings_file_path, "install", "-Dall", "-DskipTests=true"], cwd=geotools_src_dir)
        
        # install postgis
        # install from mvn project from internal directory and not from source to have consistent installation behavior
        postgis_mvn_project_path = os.path.join(internal_dir, "postgis-jdbc-2.1.0SVN")
        sp.check_call([ant], cwd=postgis_mvn_project_path) # generates maven project
        sp.check_call([mvn, "install"], cwd=postgis_mvn_project_path)
        
        # install commons-collections 4
        commons_src_dir = os.path.join(external_src_dir, commons_src_dir_name)
        if not os.path.exists(commons_src_dir) or len(os.listdir(commons_src_dir)) == 0:
            sp.check_call([wget, "http://mirror.derwebwolf.net/apache//commons/collections/source/commons-collections4-4.0-src.tar.gz"], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=tmp_dir)
            sp.check_call([tar, "xf", os.path.join(tmp_dir, "commons-collections4-4.0-src.tar.gz")], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=external_src_dir)
        if not skip_build:
            sp.check_call([mvn, "install"], cwd=commons_src_dir)
        os._exit(0)
    else:
        os.waitpid(newpid, 0)

    # install postgis
    if check_os.check_ubuntu() or check_os.check_debian():
        postgis_src_dir = os.path.join(external_src_dir, postgis_src_dir_name)
        if not os.path.exists(postgis_src_dir) or len(os.listdir(postgis_src_dir)) == 0:
            sp.check_call([wget, postgis_url], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=tmp_dir)
            sp.check_call([tar, "xf", os.path.join(tmp_dir, postgis_archive_name)], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=external_src_dir)
        if not skip_build:
            sp.check_call([sudo, apt_get, "build-dep", "--assume-yes", "postgis"]) # might not be sufficient because Ubuntu 13.10's version of postgis is 1.5.x (we're using 2.x)
            sp.check_call([sudo, apt_get, "install", "--assume-yes", "libgdal-dev"]) # not covered by 1.5.x requirements (see above)    
            sp.check_call([bash, "autogen.sh"], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=postgis_src_dir)
            sp.check_call([bash, "configure"], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=postgis_src_dir)
            sp.check_call([make, "-j8"], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=postgis_src_dir)
            sp.check_call([sudo, make, "install"], cwd=postgis_src_dir)
    elif check_os.check_opensuse():
        sp.check_call([sudo, zypper, "install", "postgis2", "postgis2-devel", "postgis2-utils"])
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("Operating system not supported!")
        
    # setup postgis datadir and configuration
    if check_os.check_ubuntu() or check_os.check_debian():
        apt_sources_file_path = "/etc/apt/sources.list"
        apt_sources_file = open(apt_sources_file_path, "rw+") # don't trust a = append mode of file, file.write overwrite the whole file nevertheless (doc of file.write contains to information at all !!)
        apt_sources_file_lines = apt_sources_file.readlines()
        # file.seek doesn't seem to work on Debian 7.0.4 -> close and open a new instance
        apt_sources_file.seek(0, 0) # reset pointer to 0 for writing
        #apt_sources_file.close()
        #apt_sources_file = open(apt_sources_file_path, "w")
        if check_os.check_ubuntu():            
            release_tuple = check_os.findout_release_ubuntu_tuple()
            if release_tuple > (12,4):
                release = "precise" # latest supported release for the repository
            else:
                release = check_os.findout_release_ubuntu()
        else:
            release = check_os.findout_release_debian()
        apt_line = "deb http://apt.postgresql.org/pub/repos/apt/ %s-pgdg main\n" % release
        if not apt_line in apt_sources_file_lines:
            apt_sources_file.writelines(apt_sources_file_lines+[apt_line])
            apt_sources_file.flush()
            apt_sources_file.close()
        os.system("wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | sudo apt-key add -")
        try:
            sp.check_call([sudo, apt_get, "update"])
            sp.check_call([sudo, apt_get, "--assume-yes", "install", 
                "postgresql-%s" % pg_version, 
                "postgresql-%s-postgis-2.1" % pg_version, 
                "postgresql-%s-postgis-2.1-scripts" % pg_version,
                "postgresql-contrib-%s" % pg_version, 
                "postgresql-client-common", # version independent, no package per version
            ])
        except:
            print("postgresql installation failed (which is possible due to broken package in Ubuntu 13.10")
            sp.check_call([sudo, apt_get, "remove", "--assume-yes", "postgresql", "postgresql-common"]) 
            postgresql_deb_path = os.path.join(tmp_dir, postgresql_deb_name)
            if not os.path.exists(postgresql_deb_path) or retrieve_md5sum(postgresql_deb_path) != postgresql_deb_md5:
                sp.check_call([wget, postgresql_deb_url], preexec_fn=user_group_utils.demote_uid(unprivileged_uid), cwd=tmp_dir)
                sp.check_call([sudo, dpkg, "-i", postgresql_deb_path])
            psql = "/opt/postgres/%s/bin/psql" % pg_version
            initdb = "/opt/postgres/%s/bin/initdb" % pg_version
            createdb = "/opt/postgres/%s/bin/createdb" % pg_version
            postgres = "/opt/postgres/%s/bin/postgres" % pg_version
    elif check_os.check_opensuse():
        if pg_version == "9.2":
            sp.check_call([sudo, zypper, "install", "postgresql", "postgresql-contrib", "postgresql-devel", "postgresql-server"])
            psql = "/usr/lib/postgresql92/bin/psql"
            initdb = "/usr/lib/postgresql92/bin/initdb" 
            createdb = "/usr/lib/postgresql92/bin/createdb"
            postgres = "/usr/lib/postgresql92/bin/postgres"
        else:
            # better to let the script fail here than to get some less comprehensive error message later
            raise RuntimeError("postgresql version %s not supported" % pg_version)
    else:
        # better to let the script fail here than to get some less comprehensive error message later
        raise RuntimeError("operating system not supported!")
    if not os.path.exists(postgres_datadir_path) or len(os.listdir(postgres_datadir_path)) == 0:
        newpid = os.fork()
        if newpid == 0:
            os.setuid(unprivileged_uid)
            # os.makedirs(postgres_datadir_path) # causes error if directory exists and is not necessary
            postgis_utils.bootstrap_datadir(postgres_datadir_path, postgres_port, postgres_host, postgres_user, ppgalise_db_name, password=postgres_pw, initdb=initdb, postgres=postgres, createdb=createdb, pgsql=pgsql)
            postgis_utils.bootstrap_datadir(postgres_datadir_path, postgres_port, postgres_host, postgres_user, pgalise_db_test_name, password=postgres_pw, initdb=initdb, postgres=postgres, createdb=createdb, pgsql=pgsql)
            os._exit(0)
        else:
            os.waitpid(newpid,0)
        
def retrieve_md5sum(path):
    md5sum_output = sp.check_output([md5sum, path]).strip().decode('utf-8') # subprocess.check_output returns byte string which has to be decoded
    ret_value = str(re.split("[\\s]+", md5sum_output) [0])
    return ret_value
        
if __name__ == "__main__":
    bootstrap()

