def call() {
    def identity = awsIdentity()
    def repoName = 'hybris-custom-development'
    def context = 'jenkins-ci'
    def description = 'Jenkins CI Pipeline'
    AGENT = "test-agent"
    pipeline {
        agent {label "${AGENT}"}
        parameters {
            booleanParam(name: 'INTEGRATION_TESTS', defaultValue: false, description: 'Run integration tests. This will add up to 20 minutes to the build time.')
        }

        environment {
            JAVA_HOME="$WORKSPACE/ci/builddeps/jdk"
            ANT_HOME ="$WORKSPACE/ci/builddeps/ant"
            ANT_OPTS="-Xmx2G"
            PATH="$PATH:$ANT_HOME/bin:$JAVA_HOME/bin:/bin:/opt/chefdk/embedded/bin:/usr/lib64/qt-3.3/bin:/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin"
            CHANNEL=SlackGetEnvChannel()
        }

        options {
            withAWS(region: 'eu-west-1')
            disableConcurrentBuilds()
        }

        stages {
            stage('Env. Configuration') {
                when { not { environment name: 'SKIP_CONF', value: 'true' } }
                steps {
                    Slack("$CHANNEL","Build triggered\n<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER> *started*")
                    GithubPushCommitStatus("pending", repoName, context, description)
                    sh """
                        eval "\$(chef shell-init bash)"
                        ./ci/prepare-ci-dependencies.sh
                        """
                    sh "cd matches && . ./hybris-install-all.sh ci"
                }
            }
            stage ('Build'){
                when { not { environment name: 'SKIP_BUILD', value: 'true'} }
                steps {
                    sh 'printenv'
                    dir ("matches/hybris") {
                        sh "ant clean -Denvironment=ci"
                        sh "ant all   -Denvironment=ci"
                    }
                }
            }
            stage ('Unit Tests'){
                when { not { environment name: 'SKIP_TESTS', value: 'true' } }
                steps {
                    dir ("matches/hybris") {
                        sh "ant mf-unittests -Denvironment=ci -Dtest.reportformat=noframes"
                        // sh "ant mf-webtests  -Denvironment=ci -Dtestclasses.failonerror=no -Dtest.reportformat=noframes"
                    }
                }
            }
            stage ('Integration Tests'){
                when {
                    allOf {
                        not { environment name: 'SKIP_TESTS', value: 'true' }
                        anyOf {
                            environment name: 'INTEGRATION_TESTS', value: 'true'
                            branch 'develop'
                            branch 'release/*'
                            branch 'master'
                        }
                    }
                }
                steps {
                    lock("mediaconversion") {
                        dir ("matches/hybris") {
                            sh "ant mf-junitinit -Denvironment=ci -Djunitinit.reusedb=true"
                            sh "ant mf-integrationtests -Denvironment=ci -Dtest.reportformat=noframes"
                        }
                    }
                }
            }
            stage ('Create Artifacts'){
                when { not { environment name: 'SKIP_ARTIFACT', value: 'true' } }
                steps {
                    dir ("matches/hybris") {
                        sh "ant production-tk"
                    }
                }
            }
            stage ('Upload Artifacts to S3') {
                when { not { environment name: 'SKIP_ARTIFACT', value: 'true' } }
                 steps {
                            s3Upload acl: 'Private', bucket: 'matches-ci-artifacts', path:"jobs/$JOB_NAME/$BUILD_NUMBER/", file: "matches/hybris/temp/hybris/hybrisServer/"
                 }
            }
        }
        post {
            always {
                script {
                    junit 'matches/hybris/log/junit/*.xml,matches/hybris/log/junit-web/*.xml,matches/hybris/log/junit-integration/*.xml'

                    // Publish all junit tests reports
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/junit',
                        reportFiles: 'junit-noframes.html', reportName: 'Unit Tests Report', reportTitles: ''])
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/junit-web',
                        reportFiles: 'junit-noframes.html', reportName: 'Web Unit Tests Report', reportTitles: ''])
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/junit-integration',
                        reportFiles: 'junit-noframes.html', reportName: 'Integration Tests Report', reportTitles: ''])

                    if (env.ENABLE_COVERAGE == 'true') {
                        // Publish a coverage report
                        dir ("matches/hybris") {
                            sh "ant -Denvironment=ci mf-jacoco-report"
                        }
                        publishCoverage adapters: [jacocoAdapter('matches/hybris/log/jacoco/jacoco.xml')]
                        publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'matches/hybris/log/jacoco', reportFiles: 'index.html', reportName: 'Coverage Report', reportTitles: ''])
                    }

                    // delete workspace folder
                    cleanWs notFailBuild: true
                }
            }
            success {
                Slack("$CHANNEL","\n<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER> completed *successfully*")
                GithubPushCommitStatus("success", repoName, context, description)
            }
            failure {
                Slack("$CHANNEL","\n<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER> *failed*\nLogs can be seen <$BUILD_URL/console|*here*>")
                GithubPushCommitStatus("failure", repoName, context, description)
            }
        }
    }
}