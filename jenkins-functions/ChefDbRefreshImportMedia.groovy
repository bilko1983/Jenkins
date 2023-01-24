#!/usr/bin/env groovy

// This Function allows to import media specific directories when db refresh starts
//
// nodeName:    (string) Name of the node which where import of media files to efs happen
// env:         (string) Name of environment (e.g. )
// chefEnv:     (string) This is chef environment variable (Will be either AWS-PRD or new-dev-env)

def call(credentialId, nodeName, env, chefEnv, service) {
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        allowedServices = ['hybris']
        if (service in allowedServices) {
            sh """
            CHEF_ENV=${chefEnv} knife ssh -x ${USERNAME} --ssh-identity-file ${PEM} 'chef_environment:${env} AND (name:*${nodeName}*)' 'cd /opt/hybris_media/media/sys_master/ && rm -rfv !\(catalogsync|cronjob|email-body|hmc|images|impex\)' -a ipaddress
            CHEF_ENV=${chefEnv} knife ssh -x ${USERNAME} --ssh-identity-file ${PEM} 'chef_environment:${env} AND (name:*${nodeName}*)' 'cd /opt/hybris_media/media/sys_master/ && aws s3 cp s3://team-devops-db-refresh-storage/media.tgz - | tar -xz' -a ipaddress
            """
        } else {
            error ("${service} is not a valid option. Please select either one of these services:\n${allowedServices}")
        }
    }
}