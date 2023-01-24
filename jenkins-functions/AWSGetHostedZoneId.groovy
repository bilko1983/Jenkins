#!/usr/bin/env groovy

def call(aws_role, aws_account_number, name){
  role_session_name = "jenkins-session"
  withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 900, roleSessionName: "$role_session_name") {
      hostedZoneID = sh (
          script: "aws route53 list-hosted-zones-by-name --dns-name $name --max-items 1 --query 'HostedZones[].Id' --output text | sed 's/\\/hostedzone\\///g'",
          returnStdout: true
      ).trim()
      return hostedZoneID
  }
}