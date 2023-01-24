#!/usr/bin/env groovy

// Start Aurora DB instance by passing the DB cluster name and wait for DB instances to be available
// db: (string) name of the db cluster to start

def call(aws_role, aws_account_number, db){
    role_session_name = "jenkins-session"
    withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 3600, roleSessionName: "$role_session_name") {
        sh "aws rds start-db-cluster --db-cluster-identifier $db"
        sh """
        status="unknown"
        while [[ "\$status" != "available" ]]; do
          sleep 60
          status=`aws rds describe-db-clusters --db-cluster-identifier $db --query 'DBClusters[*].Status' --output text --region eu-west-1`
        done
        """
    }
}
