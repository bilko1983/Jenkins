def notifySlackStarted() {
    notifySlack("Build triggered\n<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER> *started*")
}

def notifySlackSuccess() {
    notifySlack("\n<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER> completed *successfully*")
}

def notifySlackFailure() {
    notifySlack("\n<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER> *failed*\nLogs can be seen <$BUILD_URL/console|*here*>")
}

def notifySlack(message) {
    channel = SlackGetEnvChannel()

    Slack("$channel", message)
}

def notifyGithubStarted() {
    notifyGithub('pending')
}

def notifyGithubSuccess() {
    notifyGithub('success')
}

def notifyGithubFailure() {
    notifyGithub('failure')
}

def notifyGithub(status) {
    def repoName = 'hybris-custom-development'
    def context = 'jenkins-ci'
    def description = 'Jenkins CI Pipeline'

    GithubPushCommitStatus(status, repoName, context, description)
}

def install() {
    sh """
        eval "\$(chef shell-init bash)"
        ./ci/prepare-ci-dependencies.sh
    """
    sh "cd matches && . ./hybris-install-all.sh ci"
}

def build() {
    sh 'printenv'

    dir("matches/hybris") {
        sh "ant clean -Denvironment=ci"
        sh "ant all   -Denvironment=ci"
    }
}

def unitTests() {
    dir("matches/hybris") {
        sh "ant mf-unittests -Denvironment=ci -Dtest.reportformat=noframes"
        // sh "ant mf-webtests  -Denvironment=ci -Dtestclasses.failonerror=no -Dtest.reportformat=noframes"
    }
}

def integrationTests() {
    lock("mediaconversion") {
        dir("matches/hybris") {
            sh "ant mf-junitinit -Denvironment=ci -Djunitinit.reusedb=true"
            sh "ant mf-integrationtests -Denvironment=ci -Dtest.reportformat=noframes"
        }
    }
}

def createArtifacts() {
    dir("matches/hybris") {
        sh "ant production-tk"
    }
}

def uploadArtifacts() {
    s3Upload acl: 'Private', bucket: 'matches-ci-artifacts', path: "jobs/$JOB_NAME/$BUILD_NUMBER/", file: "matches/hybris/temp/hybris/hybrisServer/"
}

def sonarCloud() {
    withSonarQubeEnv('SonarCloud') {
        sh "$SCANNER_HOME/bin/sonar-scanner -Dsonar.branch.name=${env.BRANCH_NAME} -Dsonar.organization=matchesfashion -Dsonar.projectKey=matches_fashion_hybris"
    }
}

def publishTestReports() {
    junit 'matches/hybris/log/junit/*.xml,matches/hybris/log/junit-web/*.xml,matches/hybris/log/junit-integration/*.xml'

    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/junit',
                 reportFiles : 'junit-noframes.html', reportName: 'Unit Tests Report', reportTitles: ''])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/junit-web',
                 reportFiles : 'junit-noframes.html', reportName: 'Web Unit Tests Report', reportTitles: ''])
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/junit-integration',
                 reportFiles : 'junit-noframes.html', reportName: 'Integration Tests Report', reportTitles: ''])
}

def publishCoverageReport() {
    dir("matches/hybris") {
        sh "ant -Denvironment=ci mf-jacoco-report"
    }
    publishCoverage adapters: [jacocoAdapter('matches/hybris/log/jacoco/jacoco.xml')]
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/jacoco', reportFiles: 'index.html', reportName: 'Coverage Report', reportTitles: ''])
}
