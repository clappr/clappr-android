# Clappr-Android automated release Step-by-Step

This document describes how to proceed with 
the required steps to make a release using the automation
resources available for the Clappr-Android source code. Before doing any action
described in this document, ensure you already proceeded with the release branch
creation, configuration and integration to the master branch.

## Release using scripts-only
All release scripts are written in Python, to make sure
your computer environment is ready verify if Python 3 is available 
in your CLI of choice. For further configuration help,
enter the Python 3 [website](https://www.python.org/download/releases/3.0/).


### 1. Checkout Branch:
First you will need to ensure your master branch
        is updated with the latest code available in the remote repo.
        In order to do so, run:
        
```shellscript

python3 release.py checkout_branch

```
        
### 2. Run Tests:
Since your local master branch is now up to date,
        it' time to build the project and run all automated
        tests from the project,  to make it possible, run: 
        
```shellscript

python3 release.py run_tests

```
        
### 3. Bintray Upload:
Once everything is builded and tested, now we can upload 
        the generated artifact to the Bintray repository in order
        to make it public and widely available, so now run:
        
```shellscript

python3 release.py bintray_upload

```
        
### 4. Send Release Notes:
Anytime we upload a new Clappr-Android artifact to
        the release Bintray repository, we need to describe
        what was changes or added on this new version, ensure then
        that an `release_notes.md` file is available at the `../player` folder.
        When those requirements are met, run:
        
```shellscript

python3 release.py send_release_notes

```
        
            
after that, the release file will be uploaded to the [Clappr-Android Github
        release notes](https://github.com/clappr/clappr-android/releases) notifying everyone watching it.        


## Release using a Go.CD pipeline
The Go.CD pipeline is an almost-full automated release process, all commands will
be executed by itself and the pipeline will handle every step, including the additional capability
to notify via e-mail and Slack channel that a new Clappr-Android release is available. The Go.CD pipeline
will be triggered everytime a change is done to the master branch, so all you need is ensure that the release branch
process was applied and a `release_notes.md` file was added.
