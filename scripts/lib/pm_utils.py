#!/usr/bin/python
# -*- coding: utf-8 -*- 

__name__ = "pm_utils"

import logging
logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)
logger.addHandler(ch)

import subprocess
import os
import sys
import tempfile
sys.path.append(os.path.realpath(os.path.join(__file__, "..", 'lib')))
import check_os
import file_line_utils

aptuptodate = False
dpkglock = "/var/lib/dpkg/lock"
apt_get= "apt-get"

assume_yes_default = True
skip_apt_update_default = False

##############################
# dpkg tools
##############################
# @return <code>True</code> if <tt>package_name</tt> is installed, <code>False</code> otherwise
def dpkg_check_package_installed(package_name):
    return_code = subprocess.call(["dpkg", "-s", package_name], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    return return_code == 0

##############################
# apt-get tools
##############################
# a wrapper around <tt>apt-get dist-upgrade</tt> to use the internal aptuptodate flag
def apt_get_dist_upgrade(skip_apt_update=skip_apt_update_default):
    if not skip_apt_update:
        aptupdate()
    subprocess.check_call(["apt-get", "dist-upgrade"])

def install_apt_get_build_dep(packages, package_manager, assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default):
    if packages == None or not type(packages) == type([]):
        raise Exception("packages has to be not None and a list")
    if len(packages) == 0:
        return 0
    if not skip_apt_update:
        aptupdate()
    for package in packages:
        output = subprocess.check_output([apt_get, "--dry-run", "build-dep", package]).strip().decode("utf-8")
        start_marker = "Die folgenden NEUEN Pakete werden installiert:"
        try:
            start = output.index(start_marker)
        except ValueError:
            # now new packages will be installed
            continue
        start = start+len(start_marker)
        end_marker = "[0-9]+ aktualisiert,.*"
        end = re.search(end_marker, output).start()
        output = output[start : end].strip()
        build_dep_packages = output.split(" ")
        return install_packages(build_dep_packages, package_manager, assume_yes) 
        
# @return <code>True</code> if all packages in <tt>packages</tt> are installed via <tt>package_manager</tt>, <code>False</code> otherwise
def check_packages_installed(packages, package_manager, skip_apt_update=skip_apt_update_default):
    package_managers = ["apt-get"]
    if package_manager == "apt-get":
        #try:
        #    import apt
        #    import apt_pkg
        #except ImportError:
        #    print("installing missing apt python library")
        #    install_apt_python()
        #apt_pkg.init()
        #cache = apt.Cache()
        for package in packages:
            package_installed = dpkg_check_package_installed(package)
            if not package_installed:
                return False            
            #found = False
            #for cache_entry in cache:
            #    if cache_entry.name == package:
            #        found = True
            #        if not cache_entry.is_installed: # cache_entry.isInstalled in version ?? before ??
            #            cache.close()
            #            return False
            #        break
            #if not found:
            #    cache.close()
            #    return False
        #cache.close()
        return True
    else:
        raise Exception("package_manager has to be one of "+str(package_managers))

# quiet flag doesn't make sense because update can't be performed quietly obviously (maybe consider to switch to apt-api)
# return apt-get return code or <code>0</code> if <tt>packages</tt> are already installed or <tt>packages</tt> is empty
def install_packages(packages, package_manager,assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default):
    if check_packages_installed(packages, package_manager, skip_apt_update=skip_apt_update):
        return 0
    return _package_manager_action(packages, package_manager, ["install"], assume_yes, skip_apt_update=skip_apt_update)

# doesn't check whether packages are installed
# @return apt-get return code or <code>0</code> if <tt>packages</tt> is empty
def reinstall_packages(packages, package_manager, assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default, stdout=None):
    return _package_manager_action(packages, package_manager, ["--reinstall", "install"], assume_yes, skip_apt_update=skip_apt_update,stdout=None)

# @return apt-get return code oder <code>0</code> if none in <tt>packages</tt> is installed or <tt>packages</tt> is empty
def remove_packages(packages, package_manager, assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default):
    return _package_manager_action(packages, package_manager, ["remove"], assume_yes, skip_apt_update=skip_apt_update)

# quiet flag doesn't make sense because update can't be performed quietly obviously (maybe consider to switch to apt-api)
# @args a list of command to be inserted after the package manager command and default options and before the package list
def _package_manager_action(packages, packageManagerName, packagemanageraction, assumeyes, skip_apt_update=skip_apt_update_default, stdout=None):
    if len(packages) == 0:
        return 0
    if not check_os.check_root():
        raise RuntimeError("root priviledges necessary (caller has to gain them)")
    assumeyescommand = ""
    if assumeyes:
        assumeyescommand = "--assume-yes"
    if packageManagerName == "apt-get":
        #check whether dpkg lock can be obtained
        import struct
        import fcntl
        import os
        import sys
        lockobtainable = False
        while not lockobtainable:
            f = open(dpkglock, 'w')
            try:
                fcntl.lockf(f, fcntl.LOCK_EX|fcntl.LOCK_NB)
                fcntl.lockf(f, fcntl.LOCK_UN)
                lockobtainable = True
            except IOError:
                confirm("Cannot obtain lock on dpkg lock file "+dpkglock+". Close other instances of synaptic, apt-get, update-manager, etc. (If you're 100 % sure that no application using dpkg is running call 'sudo fuser -cuk /var/lib/dpkg/lock; sudo rm -f /var/lib/dpkg/lock'!","continue")
        packagestring = ""
        for package in packages:
            packagestring += package+" "
        if not skip_apt_update:
            aptupdate()
        command_list = [apt_get, "--no-install-recommends" ,"--auto-remove"]
        if assumeyes:
            command_list.append("--assume-yes")
        try:
            subprocess.check_call(command_list+packagemanageraction+packages)
        except subprocess.CalledProcessError as ex:
            raise ex
    elif packageManagerName == "yast2":
        subprocess.check_call(["/sbin/yast2", "--"+packagemanageraction]+packages) # yast2 doesn't accept string concatenation of packages with blank, but the passed list (it's acutually better style...)
    elif packageManagerName == "zypper":
        subprocess.check_call(["zypper", packagemanageraction, packagestring])
    elif packageManagerName == "equo":
        subprocess.check_call(["equo", packagemanageraction, packagestring])
    else:
        raise ValueError(str(packageManagerName)+" is not a supported package manager")
# implementation notes:
# - changed from return value to void because exceptions can be catched more elegant with subprocess.check_call
# - not a good idea to redirect output of subcommand other than update to file because interaction (e.g. choosing default display manager, comparing config file versions) is useful and necessary and suppressed when redirecting to file

# updates apt using apt-get update command. Throws exceptions as specified by subprocess.check_call if the command fails
def invalidate_apt():
    global aptuptodate
    aptuptodate = False

# updates apt using subprocess.check_call, i.e. caller has to take care to handle exceptions which happen during execution
def aptupdate(force=False):
    global aptuptodate
    if not aptuptodate or force:
        print("updating apt sources")
        try:
            apt_get_update_log_file_tuple = tempfile.mkstemp("libinstall_apt_get_update.log")
            logger.info ("logging output of apt-get update to %s" % apt_get_update_log_file_tuple[1])
            subprocess.check_call([apt_get, "--quiet", "update"], stdout=apt_get_update_log_file_tuple[0])
        except subprocess.CalledProcessError as ex:
            raise ex       
        aptuptodate = True

def generate_deb_line(url, release, identifier):
    ret_value = "deb %s %s %s" % (url, release, identifier)
    return ret_value

def generate_deb_line_re(url, release, identifier):
    ret_value = "[\\s]*deb[\\s]+%s[\\s]+%s[\\s]+%s[\\s]*" % (url, release, identifier)
    return ret_value

# Avoids the weakness of <tt>add-apt-repository</tt> command to add commented duplicates of lines which are already present by not adding those at all.
# <tt>ppa_sources_d_file_name</tt> is optional (i.e. the deb line has to be researched and added to this script manually after the first installation as a test whether the file is present only is nonsense anyway).
# @args deb_line_re a regular expression to search for an existing line in <tt>/etc/apt/sources.list and the <tt>ppa_sources_d_file_name</tt> file
# @args ppa_sources_d_file_name absolute path of the file which would be added if the ppa would have been added into <tt>/etc/apt/sources.list.d/</tt> with the <tt>add-apt-repository</tt> command (insertion of release substring ought to be handled by the caller)
# @args ppa_spec the ppa specification to be passed to <tt>add-apt-repository</tt> (can be a ppa-URL or a sources.list line)
def lazy_add_apt_source_line(deb_line_re, ppa_sources_d_file, ppa_spec):
    if file_line_utils.file_lines_match("/etc/apt/sources.list", deb_line_re, comment_symbol="#"):
        # line is in sources.list
        logger.debug("deb_line_re %s found in /etc/apt/sources.list; not adding %s" % (deb_line_re, ppa_spec))
        return False
    if not ppa_sources_d_file is None and os.path.exists(ppa_sources_d_file):
        if file_line_utils.file_lines_match("/etc/apt/sources.list", deb_line_re, comment_symbol="#"):
            # line is in ppa file
            logger.debug("deb_line_re %s found in %s; not adding %s" % (deb_line_re, ppa_sources_d_file, ppa_spec))
            return False
        if file_line_utils.file_lines_match(ppa_sources_d_file, deb_line_re, comment_symbol=None):
            # line is in ppa file but commented
            file_line_utils.comment_in(ppa_sources_d_file, deb_line_re, "#")
            logger.debug("deb_line_re %s found commented in %s; commenting in; content of %s:\n%s" % (deb_line_re, ppa_sources_d_file, ppa_sources_d_file, file(ppa_sources_d_file, "r").read()))
            return False
    logger.debug("adding package source %s with add-apt-repository" % ppa_spec)
    command_args = ["--yes", "--enable-source"]
    if check_os.check_debian():
        command_args.remove("--enable-source")
    subprocess.check_call(["add-apt-repository"]+comman_args+[ppa_spec])
    return True
