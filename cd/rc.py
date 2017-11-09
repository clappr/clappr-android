import sys
import time
from repository_manager import release_version_regex, rc_version_regex, bintray_upload, get_gradle_version, from_release_to_clappr_dir, from_clappr_to_release_dir, replace_string_on_gradle, publish_release_notes
from command_manager import print_error, execute_stage, print_success, run_tests
from git_manager import create_tag, find_release_branch, get_current_branch, checkout_branch


def search_release_branch():
    branch = find_release_branch()
    if branch is None or branch == "":
        print_error("Release branch does not exist or is not unique")
        return False

    return checkout_branch(branch)


def update_gradle_version():
    version = get_gradle_version(release_version_regex)
    new_version = version + '-RC-' + time.strftime('%Y%m%d%H%M%S')
    return replace_string_on_gradle(version, new_version)


def verify_release_pre_requisites():
    version = get_gradle_version(release_version_regex)
    if version == "":
        print_error("Wrong release version format")
        sys.exit(1)

    branch_name = get_current_branch()
    if branch_name != "release/%s" % version:
        print_error("Branch is not a release branch: %s" % branch_name)
        sys.exit(1)

    return True


def verify_rc_pre_requisites():
    version = get_gradle_version(rc_version_regex)
    if version == "":
        print_error("Wrong RC version format")
        sys.exit(1)

    return True


def send_release_notes():
    branch_name = get_current_branch()
    version = get_gradle_version(rc_version_regex)

    if not create_tag(version):
        print_error("Tag '%s' cannot be created on branch %s" % version, branch_name)
        return False

    return publish_release_notes(branch_name, version, True, False)


if __name__ == '__main__':
    print('Starting RC process')

    stages = {
        'release_branch': [search_release_branch, verify_release_pre_requisites, update_gradle_version, verify_rc_pre_requisites],
        'run_unit_tests': [verify_rc_pre_requisites, run_tests],
        'publish_bintray': [verify_rc_pre_requisites, bintray_upload],
        'send_release_notes': [verify_rc_pre_requisites, send_release_notes]
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
