def call() {
    pipeline {

        agent any

        environment {
            PATH= "/home/linuxbrew/.linuxbrew/bin/:${PATH}"
            ENVIRONMENT_KEY="dev_aws_private_key"
            CHEF_CLIENT_VERSION="12.9.38"
            CHANNEL=SlackGetEnvChannel()
            SLACK_MESSAGE="<$JOB_URL|$JOB_NAME> - <$BUILD_URL|#$BUILD_NUMBER>"
            AWS_DEFAULT_REGION = "eu-west-1"
            ROLE_SESSION_NAME="jenkins-session"
            AWS_ROLE="ExternalAdminRole"
            AWS_ACCOUNT="953612792372"
            //RDS_STATUS = AWSRdsGetClusterStatus("${ENVIRONMENT}")
        }

        parameters {
            choice(name: 'ENVIRONMENT', choices: HybrisGetEnvironments(), description: 'Choose environment')
            choice(name: 'DEPLOY', choices: ["Configure Env and Deploy","Only configure Env"], description: '"Only Configure environment" just runs chef. This skips the deployment and the options below are not used.')
            string(name: "JOB", defaultValue: "$ENVIRONMENT/build", description: '"master-ci" references to PROD. Otherwise use DEVXX for a specific build for each environment.')
            string(name: "BUILD", defaultValue: '', description: 'Build number to deploy, if no BUILD is passed it will get the latest.', )
            choice(name: "DB_UPDATE_OPT", choices: ["","--hybris-rolling-update"], description: 'Runs update-system and then deploys')
        }

        options {
            disableConcurrentBuilds()
            buildDiscarder(logRotator(numToKeepStr: '10'))
        }

        stages {
            stage ('Initial Set Up'){
                parallel {
                    stage('Get Environment DNS Records') {
                      environment {
                            HOSTED_ZONE_ID = AWSGetHostedZoneId("${AWS_ROLE}", "${AWS_ACCOUNT}", 'matcheslocal.com') // The ID is needed to get all DNS records for the environment.
                        }
                        steps {
                            Slack("$CHANNEL","Deployment *started* on `$ENVIRONMENT`\n$SLACK_MESSAGE")
                            withAWS(role:"${AWS_ROLE}", roleAccount:"${AWS_ACCOUNT}", duration: 900, roleSessionName: "${ROLE_SESSION_NAME}") {
                                // Cleaning all records from Chef for the environment
                                // ChefDeleteEnv(ENVIRONMENT)
                                // Get all DNS's for the given environment and put them in a txt file. This will later be used by the 'Chef' function
                                sh "aws route53 list-resource-record-sets --hosted-zone-id $HOSTED_ZONE_ID --query 'ResourceRecordSets[].Name' --output json | grep ${ENVIRONMENT}- | sed 's/,//g' | sed 's/\"//g' | sed 's/com./com/g' > ${ENVIRONMENT}_records.txt"
                                // Get all DNS's for the app nodes - used by HAproxyUpdate() below
                                sh "cat ${ENVIRONMENT}_records.txt | grep app | grep '[0-9].matches' > ${ENVIRONMENT}_app_records.txt"
                            }
                        }
                    }
                    //stage('Chef Deregister Environments') {
                        //steps {
                            //ChefDeregisterEnv("${ENVIRONMENT}")
                        //}
                    //}
                }
            }
            // Calling legacy job. This should moved into this pipeline
            stage('Copy Artifacts from S3') {
                when { environment name: "DEPLOY", value: "Configure Env and Deploy" }
                steps {
                    build (job: 'legacy-jobs/copy-artifacts' , wait: true, parameters:[
                        [$class: 'StringParameterValue', name: 'JOB', value: "${JOB}"],
                        [$class: 'StringParameterValue', name: 'BUILD', value: "${BUILD}" ]]
                    )
                }
            }
            //stage('Bring Up Environment') {
                //parallel {
                    //stage('Starting DB') {
                        //when { not { environment name: "RDS_STATUS", value: "available" } }
                        //options { retry(3) }
                        //steps {
                            //AWSRdsClusterStart("${ENVIRONMENT}")
                        //}
                    //}
                    //stage('EC2 Instances Starting') {
                        //steps {
                            //AWSActionEC2Instance("start", "${ENVIRONMENT}-app*,${ENVIRONMENT}-man*,${ENVIRONMENT}-cron*,${ENVIRONMENT}-solr*,${ENVIRONMENT}-esb*")
                            //sh 'sleep 60'
                        //}
                    //}
                //}
            //}
            stage('Chef base role Solr Master') {
                steps {
                    ChefBootstrap("${ENVIRONMENT}_records.txt","${ENVIRONMENT}",'solr-master','role[aws-base],role[hybris-search-master]',"${CHEF_CLIENT_VERSION}")
                }
            }
            stage('Chef base role Solr Slave') {
                steps {
                    ChefBootstrap("${ENVIRONMENT}_records.txt","${ENVIRONMENT}",'solr-slave','role[aws-base],role[hybris-search-slave]',"${CHEF_CLIENT_VERSION}")
                }
            }
            stage ('Chef base roles') {
                environment {
                    ESB = FindStringInFile("${ENVIRONMENT}-esb-1", "${ENVIRONMENT}_records.txt")
                    ESB_LEGACY = FindStringInFile("${ENVIRONMENT}-esb-legacy-1", "${ENVIRONMENT}_records.txt")
                }
                parallel {
                    stage('Cron') {
                        steps {
                            ChefBootstrap("${ENVIRONMENT}_records.txt","${ENVIRONMENT}",'cron','role[aws-base],recipe[mf-nfs::default],role[hybris-cron-v3]',"${CHEF_CLIENT_VERSION}")
                        }
                    }
                    stage('App') {
                        steps {
                            ChefBootstrap("${ENVIRONMENT}_app_records.txt","${ENVIRONMENT}",'app','role[aws-base],role[hybris-store-v3]',"${CHEF_CLIENT_VERSION}")
                        }
                    }
                    stage('Man') {
                        steps {
                            ChefBootstrap("${ENVIRONMENT}_records.txt","${ENVIRONMENT}",'man','role[aws-base],role[hybris-management-v3],recipe[mf-nfs::mount-hybris-dataimport]',"${CHEF_CLIENT_VERSION}")
                        }
                    }
                    stage('ESB') {
                        when { environment name: "ESB", value: "true" }
                        steps {
                            ChefBootstrap("${ENVIRONMENT}_records.txt","${ENVIRONMENT}",'esb','role[aws-base], role[mule-server-v3], role[activemq-server-v2], recipe[mf-nfs::mount-esb], recipe[mf-datadog::default]',"${CHEF_CLIENT_VERSION}")
                        }
                    }
                    stage('ESB-LEGACY') {
                        when { environment name: "ESB_LEGACY", value: "true" }
                        steps {
                            ChefBootstrap("${ENVIRONMENT}_records.txt","${ENVIRONMENT}",'esb-legacy','role[aws-base], role[mule-server], role[activemq-server-v2], recipe[mf-nfs::mount-esb], recipe[mf-datadog::default]',"${CHEF_CLIENT_VERSION}")
                        }
                    }
                }
            }
            stage ('Env. Configuration') {
                parallel {
                    stage('Update HAProxy') {
                        steps {
                            HAproxyUpdate("${ENVIRONMENT}_records.txt","${ENVIRONMENT}")
                        }
                    }
                    stage('Set EFS folder permissions') {
                        steps {
                            ChefKnife('centos',ENVIRONMENT_KEY,ENVIRONMENT,'hybris-search-master','recipe[mf-nfs::mount_efs_permissions]')
                        }
                    }
                }
            }
            // Calling legacy job. This should moved into this pipeline
            stage('No Downtime Deploy') {
                when { environment name: "DEPLOY", value: "Configure Env and Deploy" }
                steps {
                    build (job: 'legacy-jobs/hybris_deploy_NO_DOWNTIME' , wait: true, parameters:[
                        [$class: 'StringParameterValue', name: 'ENVIRONMENT', value: "${ENVIRONMENT}"],
                        [$class: 'StringParameterValue', name: 'DB_FLAG', value: "${DB_UPDATE_OPT}"]]
                    )
                }
            }
            stage('Deploy Solr Cores') {
                when { environment name: "DEPLOY", value: "Configure Env and Deploy" }
                steps {
                    ChefKnife('centos',ENVIRONMENT_KEY,ENVIRONMENT,'hybris-search-*','recipe[mf-hybris::solr_cores]')
                }
            }
            stage('DNS Summary') {
                when { environment name: "DEPLOY", value: "Configure Env and Deploy" }
                steps {
                    echo """Endpoints:
                    https://${ENVIRONMENT}.matchesfashion.com
                    http://${ENVIRONMENT}-solr-master.matcheslocal.com:8983/solr
                    http://${ENVIRONMENT}-solr-slave-1.matcheslocal.com:8983/solr
                    https://${ENVIRONMENT}-app-1.matcheslocal.com:9002
                    https://${ENVIRONMENT}-cron-1.matcheslocal.com:9002
                    https://${ENVIRONMENT}-app-1.matcheslocal.com:9002/hmc/hybris
                    """
                }
            }
        }
        post {
            always {
                deleteDir()
            }
            success {
                Slack("$CHANNEL","Deployment completed *successfully* on `$ENVIRONMENT`\n$SLACK_MESSAGE")
                K8sRestartService("varnish",ENVIRONMENT)
                K8sRestartService("haproxy",ENVIRONMENT)
            }
            failure {
                Slack("$CHANNEL","Deployment *failed* on `$ENVIRONMENT`\n$SLACK_MESSAGE\nLogs can be seen <$BUILD_URL/console|*here*>")
            }
        }
    }
}
