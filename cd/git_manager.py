from command_manager import execute_command, print_error
import subprocess

def execute_git(commands):
    for task in commands:
        if not execute_command(command='git', attributes=task):
            print_error("Can not execute git command!")
            return False
    return True


def get_current_branch():
    output = subprocess.check_output(['git', 'rev-parse', '--abbrev-ref', 'HEAD']).decode('utf8').replace('\n','')
    print("branch=%s" % output)
    return output


def get_tag_branch(tag_name):
    output = subprocess.check_output(['git', 'branch', '--contains', tag_name]).decode('utf8')
    return output
