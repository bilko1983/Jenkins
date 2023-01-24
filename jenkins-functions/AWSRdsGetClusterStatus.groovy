#!/usr/bin/env groovy

// Get Aurora Cluster Status
// db: (string) name of the db cluster that we are getting the status from

def call(aws_role, aws_account_number, db){
  role_session_name = "jenkins-session"
  withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 900, roleSessionName: "$role_session_name") {
      output = sh (
          script: "aws rds describe-db-clusters --db-cluster-identifier $db --query='DBClusters[0].Status' ",
          returnStdout: true
      ).trim().replace('"','')
      return output
  }
}
