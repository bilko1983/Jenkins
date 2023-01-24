#!/usr/bin/env groovy

// This Function allows to stop Chef client on a specific machine
// credentialId: pem file used with user to authenticate with user name
// nodeIp: IP of node where we are stopping chef client

def call(credentialId,nodeIp){
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} 'sudo service chef-client stop'"
    }
}