#!/usr/bin/env groovy

// Restart Aurora DB instance by passing the DB instance name

def call(aws_role, aws_account_number, db){
    role_session_name = "jenkins-session"
    withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 900, roleSessionName: "$role_session_name") {
        sh "aws rds reboot-db-instance --db-instance-identifier $db"
    }
}