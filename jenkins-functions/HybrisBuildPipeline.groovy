def call() {
    def identify = awsIdentity()
    def buildSteps = new HybrisBuildSteps()
    AGENT = "test-agent"
    pipeline {
        agent { label "${AGENT}" }
        parameters {
            booleanParam(name: 'INTEGRATION_TESTS', defaultValue: false, description: 'Run integration tests. This will add up to 20 minutes to the build time.')
            booleanParam(name: 'SONAR_ANALYSIS', defaultValue: true, description: 'Run Sonar analysis. This will add up to 5 minutes to the build time.')
        }

        environment {
            JAVA_HOME = "$WORKSPACE/ci/builddeps/jdk"
            ANT_HOME = "$WORKSPACE/ci/builddeps/ant"
            ANT_OPTS = "-Xmx2G"
            PATH = "$PATH:$ANT_HOME/bin:$JAVA_HOME/bin:/bin:/opt/chefdk/embedded/bin:/usr/lib64/qt-3.3/bin:/usr/local/bin:/bin:/usr/bin:/usr/local/sbin:/usr/sbin:/sbin"
        }

        options {
            withAWS(region: 'eu-west-1')
            disableConcurrentBuilds()
        }

        stages {
            stage('Env. Configuration') {
                when { not { environment name: 'SKIP_CONF', value: 'true' } }
                steps {
                    script {
                        buildSteps.notifySlackStarted()
                        buildSteps.notifyGithubStarted()
                        buildSteps.install()
                    }
                }
            }
            stage('Build') {
                when { not { environment name: 'SKIP_BUILD', value: 'true' } }
                steps {
                    script {
                        buildSteps.build()
                    }
                }
            }
            stage('Unit Tests') {
                when { not { environment name: 'SKIP_TESTS', value: 'true' } }
                steps {
                    script {
                        buildSteps.unitTests()
                    }
                }
            }
            stage('Integrations Tests') {
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
                    script {
                        buildSteps.integrationTests()
                    }
                }
            }
            stage('SonarCloud') {
                tools {
                    jdk 'JDK 11'
                }
                environment {
                    SCANNER_HOME = tool 'SonarQubeScanner'
                }
                when { environment name: 'SONAR_ANALYSIS', value: 'true' }
                steps {
                    script {
                        buildSteps.sonarCloud()
                    }
                }
            }
            stage('Create Artifacts') {
                when { not { environment name: 'SKIP_ARTIFACT', value: 'true' } }
                steps {
                    script {
                        buildSteps.createArtifacts()
                    }
                }
            }
            stage('Upload Artifacts to S3') {
                when { not { environment name: 'SKIP_ARTIFACT', value: 'true' } }
                steps {
                    script {
                        buildSteps.uploadArtifacts()
                    }
                }
            }
        }
        post {
            always {
                script {
                    buildSteps.publishTestReports()

                    if (env.ENABLE_COVERAGE == 'true') {
                        buildSteps.publishCoverageReport()
                    }

                    // delete workspace folder
                    cleanWs notFailBuild: true
                }
            }
            success {
                script {
                    buildSteps.notifySlackSuccess()
                    buildSteps.notifyGithubSuccess()
                }
            }
            failure {
                script {
                    buildSteps.notifySlackFailure()
                    buildSteps.notifyGithubFailure()
                }
            }
        }
    }
}
