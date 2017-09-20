import sys

from repository_manager import release_note_file_path, bintray_upload, send_release_notes, get_gradle_version, from_release_to_clappr_dir, from_clappr_to_release_dir, read_release_notes
from command_manager import print_error, execute_stage, print_success, run_tests

release_version_regex = r'version = \'((\d+)\.(\d+)\.(\d+))\''


def verify_release_pre_requisites(version):
    if version == "":
        print_error("Wrong version format")
        sys.exit(1)

    release_notes = read_release_notes(release_note_file_path)
    if release_notes is None or release_notes == "":
        print_error("Release notes is not consistent with version '%s'" % version)
        sys.exit(1)


if __name__ == '__main__':
    print('Starting release process')

    stages = {
        'run_tests': [run_tests],
        'bintray_upload': [bintray_upload],
        'send_release_notes': [send_release_notes]
    }

    if len(sys.argv) != 2:
        print_error("Wrong number of arguments")
        sys.exit(1)

    print('Changing to clappr dir')
    from_release_to_clappr_dir()

    stage = sys.argv[1]
    version = get_gradle_version(release_version_regex)
    verify_release_pre_requisites(version)

    execute_stage(version, stages, stage)

    print('Changing back to release dir')
    from_clappr_to_release_dir()

    print_success()
