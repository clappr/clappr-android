import os
import requests
import re

from command_manager import print_error, execute_gradle
from git_manager import get_current_branch, get_tag_branch, create_tag

release_dir_path = '../cd/'
clappr_dir_path = '../clappr/'
gradle_file_path = 'build.gradle'
release_version_regex = r'version = \'((\d+)\.(\d+)\.(\d+))\''
snapshot_version_regex = r'version = \'((\d+)\.(\d+)\.(\d+)-SNAP[-\w]*-(\d{14}))\''
rc_version_regex = r'version = \'((\d+)\.(\d+)\.(\d+)-RC-(\d{14}))\''


def get_gradle_version(version_regex):
    file_content = open(gradle_file_path, 'r').read()
    full_version = re.search(pattern=version_regex, string=file_content)

    return full_version.group(1) if full_version is not None else ""


def replace_string_on_gradle(old_version, new_version):
    with open(gradle_file_path) as f:
        s = f.read()

    with open(gradle_file_path, 'w') as f:
        print("Changing %s to %s in %s" % (old_version, new_version, gradle_file_path))
        s = s.replace(old_version, new_version)
        f.write(s)

    return True


def from_clappr_to_release_dir():
    os.chdir(path=release_dir_path)


def from_release_to_clappr_dir():
    os.chdir(path=clappr_dir_path)


def bintray_upload():
    bintray_user = "BINTRAY_USER"
    bintray_api_key = "BINTRAY_API_KEY"

    if not bintray_user in os.environ:
        print_error("Env variable '%s' is not defined" % bintray_user)
        return False

    if not bintray_api_key in os.environ:
        print_error("Env variable '%s' is not defined" % bintray_api_key)
        return False

    return execute_gradle(tasks=['bintrayUpload'])


def publish_release_notes(branch_name, new_release_version, draft, prerelease):
    repository_path = "REPOSITORY_PATH"
    repository_token = "REPOSITORY_TOKEN"

    if not repository_path in os.environ:
        print_error("Env variable '%s' is not defined" % repository_path)
        return False

    if not repository_token in os.environ:
        print_error("Env variable '%s' is not defined" % repository_token)
        return False

    body = {
        'tag_name': new_release_version,
        'target_commitish': branch_name,
        'name': 'Alpha Release',
        'body': "add release notes for version %s here..." % new_release_version,
        'draft': draft,
        'prerelease': prerelease
    }
    response = requests.post(url=os.environ[repository_path],
                  auth=('token', os.environ[repository_token]),
                  json=body)

    if response.status_code >= 300:
        print_error(response.text)
        return False

    return True

