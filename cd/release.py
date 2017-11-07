import sys

from repository_manager import release_version_regex, bintray_upload, send_release_notes, from_release_to_clappr_dir, from_clappr_to_release_dir, get_gradle_version
from command_manager import print_error, execute_stage, print_success, run_tests


def verify_release_pre_requisites():
    version = get_gradle_version(release_version_regex)
    if version == "":
        print_error("Wrong version format")
        sys.exit(1)

    return True


if __name__ == '__main__':
    print('Starting release process')

    stages = {
        'run_unit_tests': [verify_release_pre_requisites, run_tests],
        'publish_bintray': [verify_release_pre_requisites, bintray_upload],
        'send_release_notes': [verify_release_pre_requisites, send_release_notes]
    }

    if len(sys.argv) != 2:
        print_error("Wrong number of arguments")
        sys.exit(1)

    print('Changing to clappr dir')
    from_release_to_clappr_dir()

    stage = sys.argv[1]
    execute_stage(stages, stage)

    print('Changing back to release dir')
    from_clappr_to_release_dir()

    print_success()
