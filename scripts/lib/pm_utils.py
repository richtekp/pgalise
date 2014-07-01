#!/usr/bin/python
# -*- coding: utf-8 -*- 

# pm_utils should definitely use the sude command as it makes it portable between root and non-root users

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
import re
sys.path.append(os.path.realpath(os.path.join(__file__, "..", 'lib')))
import check_os
import file_line_utils
import string

# indicates whether apt is up to date, i.e. whether `apt-get update` has been invoked already
aptuptodate = False
# indicates that apt sources are invalid, e.g. after packages sources have been changed @TODO: explain why in comment here
apt_invalid = False
dpkglock = "/var/lib/dpkg/lock"
apt_get= "apt-get"
add_apt_repository = "add-apt-repository"

assume_yes_default = False
skip_apt_update_default = False
install_recommends_default=True
install_suggests_default = False

APT_OUTPUT_CONSOLE=1
APT_OUTPUT_TMP_FILE=2
APT_OUTPUT=APT_OUTPUT_CONSOLE

PACKAGE_MANAGER_APT_GET="apt-get"

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
def upgrade(package_manager=PACKAGE_MANAGER_APT_GET , assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default, install_recommends=install_recommends_default, install_suggests=install_suggests_default):
    if package_manager == PACKAGE_MANAGER_APT_GET:
        aptupdate(skip_apt_update)
        command_list = [apt_get, "dist-upgrade"]
        options_command_list = __generate_apt_options_command_list__(assume_yes=assume_yes, install_recommends=install_recommends, install_suggests=install_suggests)
        subprocess.check_call(command_list+options_command_list)
    else:
        raise RuntimeError("package_manager %s not yet supported" % (package_manager,))

def install_apt_get_build_dep(packages, package_manager="apt-get", assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default):
    if packages == None or not type(packages) == type([]):
        raise Exception("packages has to be not None and a list")
    if len(packages) == 0:
        return 0
    aptupdate(skip_apt_update)
    for package in packages:
        apt_get_output = subprocess.check_output([apt_get, "--dry-run", "build-dep", package]).strip()
        apt_get_output_lines = apt_get_output.split("\n")
        build_dep_packages = []
        for apt_get_output_line in apt_get_output_lines:
            if apt_get_output_line.startswith("  "):
                build_dep_packages += re.split("[\\s]+", apt_get_output_line)
        build_dep_packages = [x for x in build_dep_packages if x != ""]
        install_packages(build_dep_packages, package_manager, assume_yes, skip_apt_update=skip_apt_update) 

# only checks for the specified packages, no for their recommends or suggests
# @return <code>True</code> if all packages in <tt>packages</tt> are installed via <tt>package_manager</tt>, <code>False</code> otherwise
def check_packages_installed(packages, package_manager="apt-get", skip_apt_update=skip_apt_update_default):
    package_managers = ["apt-get"]
    if package_manager == "apt-get":
        for package in packages:
            package_installed = dpkg_check_package_installed(package)
            if not package_installed:
                return False
        return True
    else:
        raise Exception("package_manager has to be one of "+str(package_managers))
# internal implementation notes:
# - python-apt bindings have been dropped due to slow speed (dpkg queries are much faster)

# quiet flag doesn't make sense because update can't be performed quietly obviously (maybe consider to switch to apt-api)
# return apt-get return code or <code>0</code> if <tt>packages</tt> are already installed or <tt>packages</tt> is empty
def install_packages(packages, package_manager="apt-get", assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default, install_recommends=install_recommends_default, install_suggests=install_suggests_default):
    if check_packages_installed(packages, package_manager, skip_apt_update=skip_apt_update):
        return 0
    return __package_manager_action__(packages, package_manager, ["install"], assume_yes, skip_apt_update=skip_apt_update, install_recommends=install_recommends, install_suggests=install_suggests)

# doesn't check whether packages are installed
# @return apt-get return code or <code>0</code> if <tt>packages</tt> is empty
def reinstall_packages(packages, package_manager="apt-get", assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default, stdout=None):
    return __package_manager_action__(packages, package_manager, ["--reinstall", "install"], assume_yes, skip_apt_update=skip_apt_update,stdout=None)

# @return apt-get return code oder <code>0</code> if none in <tt>packages</tt> is installed or <tt>packages</tt> is empty
def remove_packages(packages, package_manager="apt-get", assume_yes=assume_yes_default, skip_apt_update=skip_apt_update_default):
    return __package_manager_action__(packages, package_manager, ["remove"], assume_yes, skip_apt_update=skip_apt_update)

def __generate_apt_options_command_list__(assume_yes=assume_yes_default, install_recommends=install_recommends_default, install_suggests=install_suggests_default):
    # apt-get installs recommended packages by default, therefore the 
    # option to deactivate it is negative (--no-install-recommends), while 
    # the option to install suggests is option, there the option is positive 
    command_list = []
    if not install_recommends:
        command_list.append("--no-install-recommends")
    if install_suggests:
        command_list.append("--install-suggests")
    if assume_yes:
        command_list.append("--assume-yes")
    return command_list

# quiet flag doesn't make sense because update can't be performed quietly obviously (maybe consider to switch to apt-api)
# @args a list of command to be inserted after the package manager command and default options and before the package list
def __package_manager_action__(packages, package_manager, package_manager_action, assume_yes, skip_apt_update=skip_apt_update_default, stdout=None, install_recommends=install_recommends_default, install_suggests=install_suggests_default):
    if not "<type 'list'>" == str(type(packages)) and str(type(packages)) != "<class 'list'>":
        raise ValueError("packages isn't a list")
    if len(packages) == 0:
        return 0
    if package_manager == "apt-get":
        aptupdate(skip_apt_update)
        command_list = [apt_get]
        options_command_list = __generate_apt_options_command_list__(assume_yes=assume_yes, install_recommends=install_recommends, install_suggests=install_suggests)
        subprocess.check_call(command_list+options_command_list+package_manager_action+packages)
    elif package_manager == "yast2":
        subprocess.check_call(["/sbin/yast2", "--"+package_manager_action]+packages) # yast2 doesn't accept string concatenation of packages with blank, but the passed list (it's acutually better style...)
    elif package_manager == "zypper":
        subprocess.check_call(["zypper", package_manager_action]+packages)
    elif package_manager == "equo":
        subprocess.check_call(["equo", package_manager_action]+packages)
    else:
        raise ValueError(str(package_manager)+" is not a supported package manager")
# implementation notes:
# - changed from return value to void because exceptions can be catched more elegant with subprocess.check_call
# - not a good idea to redirect output of subcommand other than update to file because interaction (e.g. choosing default display manager, comparing config file versions) is useful and necessary and suppressed when redirecting to file
# - checking availability of apt lock programmatically causes incompatibility with invokation without root privileges -> removed

# updates apt using apt-get update command. Throws exceptions as specified by subprocess.check_call if the command fails
def invalidate_apt():
    logger.debug("invalidating apt status (update forced at next package manager action)")
    global apt_invalid
    apt_invalid = True
    global aptuptodate
    aptuptodate = False

# updates apt using subprocess.check_call, i.e. caller has to take care to handle exceptions which happen during execution
def aptupdate(skip=skip_apt_update_default, force=False):
    global aptuptodate
    if (not aptuptodate and not skip) or force or apt_invalid:
        print("updating apt sources")
        apt_stdout = None
        if APT_OUTPUT == APT_OUTPUT_TMP_FILE:
            apt_get_update_log_file_tuple = tempfile.mkstemp("libinstall_apt_get_update.log")
            logger.info ("logging output of apt-get update to %s" % apt_get_update_log_file_tuple[1])
            apt_stdout = apt_get_update_log_file_tuple[0]
        subprocess.check_call([apt_get, "--quiet", "update"], stdout=apt_stdout)
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
# @args ppa_spec the ppa specification to be passed to <tt>add-apt-repository</tt> (can be a ppa-URL or a sources.list line), mustn't be <code>None</code> (<tt>ValueError</tt> will be raised, otherwise), not validated (i.e. adds to your sources.list file whatever you specify)
def lazy_add_apt_source_line(deb_line_re, ppa_sources_d_file, ppa_spec):
    if ppa_spec is None:
        raise ValueError("ppa_spec mustn't be None")
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
            logger.debug("deb_line_re %s found commented in %s; commenting in; content of %s:\n%s" % (deb_line_re, ppa_sources_d_file, ppa_sources_d_file, open(ppa_sources_d_file, "r").read()))
            return False
    logger.debug("adding package source %s with add-apt-repository" % ppa_spec)
    command_args = ["--yes", "--enable-source"]
    if check_os.check_debian():
        command_args.remove("--enable-source")
    subprocess.check_call([add_apt_repository]+command_args+["\"%s\"" % (ppa_spec,)]) # "" is necessary for deb lines to be passed and doesn't hurt for others
    return True

