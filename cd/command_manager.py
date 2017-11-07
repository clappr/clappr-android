import sys

from subprocess import call

def execute_command(command, attributes):
    task_executed_with_success = 0
    attributes.insert(0, command)
    return call(attributes) == task_executed_with_success


class bcolors:
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


def print_error(message):
    print(bcolors.FAIL + "Error: " + message + bcolors.ENDC)


def print_success():
    print(bcolors.BOLD + "Uhuuuuu! zo/ Success" + bcolors.ENDC)


def execute_stage(stages, stage_key):
    try:
        for task in stages[stage_key]:
            print("task: %s , Clappr on branch" % (task.__name__))
            if not task():
                sys.exit(1)

    except IndexError:
        print("Can not Moisés! The options are: ")
        for stage_key in stages.keys():
            print(stage_key)


def execute_gradle(tasks):
    return execute_command(command='../gradlew', attributes=tasks)


def run_tests():
    response = execute_gradle(tasks=['clean', 'build', 'test', '--continue'])

    if not response:
        print_error("Tests failed")

    return response