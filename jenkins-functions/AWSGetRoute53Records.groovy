#!/usr/bin/env groovy

def call(aws_role, aws_account_number, hostedZoneID,contains){
  role_session_name = "jenkins-session"
  withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 900, roleSessionName: "$role_session_name") {
      hostedZoneID = sh (
          script: "aws route53 list-resource-record-sets --hosted-zone-id $hostedZoneID --query 'ResourceRecordSets[].Name' | grep $contains",
          returnStdout: true
      ).trim()
      return hostedZoneID
  }
}