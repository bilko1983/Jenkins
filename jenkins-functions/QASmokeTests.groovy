#!/usr/bin/env groovy

// Execute Smoke tests against a specific environment

// The function needs 3 parameters to work:
// credID (mandatory): credential id for Jenkins QA
// browser (mandatory): where the tests whill run
// url (mandatory): url of environment

def call(String credID, String browser, String url){
    withCredentials([usernamePassword(credentialsId: credID, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
        sh """
        curl -X POST -H \"Content-Length:0\" http://${USERNAME}:${PASSWORD}@10.10.7.126/job/smokeProd_gradle/buildWithParameters --data-urlencode json=\'{\"parameter\": [{\"name\":\"Browser\", \"value\":\"${browser}\"}, {\"name\":\"url\", \"value\":\"${url}\"}]}\'
        """
    } 
}