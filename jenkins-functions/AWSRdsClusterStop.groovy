#!/usr/bin/env groovy

// Stop Aurora DB instance by passing the DB cluster name
// db: (string) name of the db cluster to start

def call(aws_role, aws_account_number, db){
    role_session_name = "jenkins-session"
    withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 1800, roleSessionName: "$role_session_name") {
        sh "aws rds stop-db-cluster --db-cluster-identifier $db"
    }
}
