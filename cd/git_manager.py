from command_manager import execute_command
import subprocess


def get_current_branch():
    output = subprocess.check_output(['git', 'rev-parse', '--abbrev-ref', 'HEAD']).decode('utf8').replace('\n','')
    print("branch=%s" % output)
    return output


def checkout_branch(branch):
    return execute_command(command='git', attributes=['checkout', '-b', branch, 'origin/'+branch])

def get_tag_branch(tag_name):
    output = subprocess.check_output(['git', 'branch', '--contains', tag_name]).decode('utf8')
    return output


def create_tag(tag_name):
    if execute_command(command='git', attributes=['tag', '-a', tag_name, '-m', tag_name]):
        return execute_command(command='git', attributes=['push', 'origin', tag_name])
    else:
        return False


def find_release_branch():
    output = subprocess.check_output(["git branch -r | grep ci_rc"], shell=True).decode('utf8')
    print("release branch=\n%s" % output)

    list = output.split('\n')

    if len(list) != 2:
        return None
    return list[0].strip(" ").strip("origin/")
