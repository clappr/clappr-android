import sys

from repository_manager import release_version_regex, bintray_upload, from_release_to_clappr_dir, from_clappr_to_release_dir, get_tag_branch, get_gradle_version, publish_release_notes, get_current_branch
from command_manager import print_error, execute_stage, print_success, run_tests


def verify_release_pre_requisites():
    version = get_gradle_version(release_version_regex)
    if version == "":
        print_error("Wrong version format")
        sys.exit(1)

    return True


def send_release_notes():
    branch_name = get_current_branch()
    version = get_gradle_version(release_version_regex)

    tag_branch = get_tag_branch(version)
    if tag_branch is None or tag_branch.strip(" ") != branch_name:
        print_error("Tag '%s' not exist on branch %s" % version, branch_name)
        return False

    return publish_release_notes(branch_name, version, True, False)


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
