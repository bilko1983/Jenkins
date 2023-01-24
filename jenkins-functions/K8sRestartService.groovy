#!/usr/bin/env groovy

def call(service,environment){
    sh "echo Restarting Service ${service} under ${environment}" 
    sh "ssh ec2-user@bastion-public-001.dev.matchescloud.com -i ~/.ssh/eks-dev-ssh  'sudo kubectl delete rs -n ${environment} -l app=${service}'"
}