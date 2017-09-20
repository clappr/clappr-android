import sys
import time
from repository_manager import bintray_upload, get_gradle_version, from_release_to_clappr_dir, from_clappr_to_release_dir, update_gradle_version
from command_manager import print_error, execute_stage, print_success, run_tests

release_version_regex = r'version = \'((\d+)\.(\d+)\.(\d+))\''
snapshot_version_regex = r'version = \'((\d+)\.(\d+)\.(\d+)-dev-(\d+))\''


def verify_snapshot_pre_requisites(version):
    if version == "":
        print_error("Wrong version format")
        sys.exit(1)


if __name__ == '__main__':
    print('Starting snapshot process')

    stages = {
        'update_gradle_version': [],
        'run_tests': [run_tests],
        'bintray_upload': [bintray_upload]
    }

    if len(sys.argv) != 2:
        print_error("Wrong number of arguments")
        sys.exit(1)

    print('Changing to clappr dir')
    from_release_to_clappr_dir()

    stage = sys.argv[1]
    version = ""
    if stage == 'update_gradle_version':
        version = get_gradle_version(release_version_regex)
        verify_snapshot_pre_requisites(version)
        new_version = version + '-dev-' + str(round(time.time() * 1000))
        update_gradle_version(version, new_version)

    version = get_gradle_version(snapshot_version_regex)
    verify_snapshot_pre_requisites(version)

    execute_stage(version, stages, stage)

    print('Changing back to release dir')
    from_clappr_to_release_dir()

    print_success()
