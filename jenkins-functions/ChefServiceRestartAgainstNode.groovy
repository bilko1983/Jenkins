#!/usr/bin/env groovy

// This Function allows to restart service on a given node
//
// service:     (string) name of service to restart
// nodeName:    (string) Name of the node which the service will be restarted against.
// env:         (string) Name of environment (e.g. )
// chefEnv:     (string) This is chef environment variable (Will be either AWS-PRD or new-dev-env)

def call(credentialId, nodeName, env, chefEnv, service) {
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        allowedServices = ['hybris', 'solr', 'mule', 'nginx']
        if (service in allowedServices) {
            sh """
            CHEF_ENV=${chefEnv} knife ssh -x ${USERNAME} --ssh-identity-file ${PEM} 'chef_environment:${env} AND (name:*${nodeName}*)' 'sudo service ${service} restart' -a ipaddress
            """
        } else {
            error ("${service} is not a valid option. Please select either one of these services:\n${allowedServices}")
        }
    }
}