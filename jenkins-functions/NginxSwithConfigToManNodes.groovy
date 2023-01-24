#!/usr/bin/env groovy

// This Function replaces the nginx config file to point to the man nodes instead of cron
// There's a dependency to alreay have a config file available with the new config under /etc/nginx/conf.d/remove_cron_config/default.remove.cron.conf
// This dependency is currently being managed by CHEF

// credentialId: pem file used with user to authenticate with user name
// nodeIp: IP of node where we are stopping chef client

def call(credentialId,nodeIp){
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} 'sudo mv /etc/nginx/conf.d/remove_cron_config/default.remove.cron.conf /etc/nginx/conf.d/default.conf'"
    }
}