#!/usr/bin/env groovy

//Author: Shahin Ahmed
// This function executes command over SSH against a paticular node and then deploys given artifacts
// credentialId: (string) credentials passed through function with variable name 'PEM' and 'USERNAME' attached to be able to ssh onto server
// buildNr: (integar) Build number of the artifacts bbeing deployed
// domain: address of the target domain
// nodeName Human readable node name

def call (credentialId, domain, nodeName){
    println "Restarting hybris Server ${nodeName}" 
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${domain} sudo service hybris restart"
    }
}