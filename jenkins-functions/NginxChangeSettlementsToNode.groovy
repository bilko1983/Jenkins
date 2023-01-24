#!/usr/bin/env groovy

def call (credentialId,nodeIp){
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo rm -rf /etc/nginx/conf.d/default.conf && cp /etc/nginx/conf.d/default.conf.man_primary /etc/nginx/conf.d/default.conf"
    }
}