#!/usr/bin/python
# -*- coding: utf-8 -*- 

# doesn't handle exceptions
# @return: the return code of the process
def call_wrapper(commands, user, cwd, stdout=None, stderr=None):
    current_uid = int(subprocess.check_output(["id","-u"]))
    user_uid = int(pwd.getpwnam(user)[2])
    if current_uid != user_uid:
        return subprocess.call(["su", user, "-c", string.join(commands, " ")], cwd=cwd, stdout=stdout, stderr=stderr)
    else:
        return subprocess.call(commands, cwd=cwd, stdout=stdout, stderr=stderr)

# doesn't handle exceptions
# @return: the return code  of the process
def check_call_wrapper(commands, user, cwd):
    if str(type(commands)) != "<type 'list'>":
        raise Exception("commands has to be a list")
    current_uid = int(subprocess.check_output(["id","-u"]))
    user_uid = int(pwd.getpwnam(user)[2])
    if current_uid != user_uid:
        return subprocess.check_call(["su", user, "-c", string.join(commands, " ")], cwd=cwd)
    else:
        return subprocess.check_call(commands, cwd=cwd)

