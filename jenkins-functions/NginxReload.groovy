#!/usr/bin/env groovy

// This Function allows to reload the nginx config on a specific server
// credentialId: pem file used with user to authenticate with user name
// nodeIp: IP of node where we are stopping chef client

def call(credentialId,nodeIp){

    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        try{
            sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} 'sudo service nginx reload'"
        }
        catch(Exception ex ){
            throw new Exception("The Nginx reload has failed. Please check the Nginx log.")
        }
     }
}

