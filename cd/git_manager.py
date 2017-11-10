import subprocess


def get_current_branch():
    output = subprocess.check_output(['git', 'rev-parse', '--abbrev-ref', 'HEAD']).decode('utf8').replace('\n', '')
    print("current branch=%s" % output)
    return output


def checkout_remote_branch(branch):
    try:
        subprocess.check_output(["git checkout %s" % branch], shell=True)
    except subprocess.CalledProcessError:
        try:
            subprocess.check_output(["git checkout -b "+branch+" origin/"+branch], shell=True)
        except subprocess.CalledProcessError:
            return False

    return True


def get_tag_branch(tag_name):
    try:
        output = subprocess.check_output(["git branch --contains %s" % tag_name]).decode('utf8')
        return output
    except subprocess.CalledProcessError:
        return None


def create_tag(tag_name):
    try:
        subprocess.check_output(["git tag -a %s -m %s" % tag_name, tag_name], shell=True)
        subprocess.check_output(["git push origin %s" % tag_name], shell=True)
        return True
    except subprocess.CalledProcessError:
        return False


def find_release_branch():
    try:
        output = subprocess.check_output(["git ls-remote --heads origin | grep release"], shell=True).decode('utf8')
        print("release branch=\n%s" % output)

        list = output.split('\n')

        if len(list) != 2:
            return None
        return list[0].split('refs/heads/')[1].strip(" ")

    except subprocess.CalledProcessError:
        return None
