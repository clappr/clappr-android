# Clappr-Android automated release Step-by-Step

This document describes how to proceed with the required steps to make releases, candidate releases and snapshots
from dev ou feature branches using the automation resources available for the Clappr-Android source code.

All release scripts are written in Python. To make sure your computer environment is ready verify if Python 3
is available in your CLI of choice. For further configuration help, enter the
Python 3 [website](https://www.python.org/download/releases/3.0/).

The Go.CD pipelines include the additional capability to notify Slack channel about all stages running.


## Snapshot
Snapshot releases are published on [snapshot package on Bintray](https://bintray.com/clappr-android/clappr/clappr-android-snapshot) and has no Release Notes published.

### Using scripts-only

1. Checkout desired branch (dev, hotfix or feature). Go to folder cd and run following command to change version to snapshot format:

```shellscript

python3 snapshot.py snapshot_branch

```

2. Since your local branch is now up to date, it' time to build the project and run all automated unit tests from
the project, to make it possible, run:

```shellscript

python3 snapshot.py run_unit_tests

```

3. Once everything is builded and tested, now we can upload the generated artifacts to the Bintray repository
in order to make it public and widely available, so now run:

```shellscript

python3 snapshot.py publish_bintray

```

## Using a Go.CD pipeline
The Go.CD pipeline `clappr-android-dev` will be triggered everytime a change is done to the dev branch.
To generate a snapshot for a feature ou hotfix branch, trigger mannually the Go.CD pipeline `clappr-android-snapshot`


## Release
Before release a new official version, ensure you already proceeded with the release branch creation, configuration and
integration to the master branch with a tag equals to version number.
Releases are published on [release package on Bintray](https://bintray.com/clappr-android/clappr/clappr-android-release) and has
Release Notes published on [Clappr-Android Github](https://github.com/clappr/clappr-android/releases)

### Using scripts-only

1. Checkout master branch and go to folder cd

2. Since your master branch is now up to date, it' time to build the project and run all automated unit tests from
the project, to make it possible, run:

```shellscript

python3 release.py run_unit_tests

```

3. Once everything is builded and tested, now we can upload the generated artifacts to the Bintray repository
in order to make it public and widely available, so now run:

```shellscript

python3 release.py publish_bintray

```

4. Anytime we upload a new Clappr-Android artifact to the release Bintray repository, we need to describe what was
changes or added on this new version. Following command creates a draft release notes on clappr repository on Github:

```shellscript

python3 release.py send_release_notes

```

## Using a Go.CD pipeline
The Go.CD pipeline `clappr-android` will be triggered everytime a change is done to the master branch.


## Candidate Release
Before release a candidate version, ensure you already proceeded with the release branch creation.
Candidate releases are published on [release package on Bintray](https://bintray.com/clappr-android/clappr/clappr-android-release) and has
Release Notes published on [Clappr-Android Github](https://github.com/clappr/clappr-android/releases)

### Using scripts-only

1. Checkout release branch. Go to folder cd and run following command to change version to RC format:

```shellscript

python3 rc.py release_branch

```

2. Since your master branch is now up to date, it' time to build the project and run all automated unit tests from
the project, to make it possible, run:

```shellscript

python3 rc.py run_unit_tests

```

3. Once everything is builded and tested, now we can upload the generated artifacts to the Bintray repository
in order to make it public and widely available, so now run:

```shellscript

python3 rc.py publish_bintray

```

4. Anytime we upload a new Clappr-Android artifact to the release Bintray repository, we need to describe what was
changes or added on this new version. Following command creates a draft release notes on clappr repository on Github:

```shellscript

python3 rc.py send_release_notes

```

## Using a Go.CD pipeline
The Go.CD pipeline `clappr-android-rc` can be triggred manually, since thare is only one release branch on remote repository.
