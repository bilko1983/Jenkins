#!/usr/bin/env groovy

// This Function Purges All Fastly cache for a given service e.g. www-prd or dev10-web etc.

// This function needs 2 parameters to work:
// service_id:     (string) This is the fastly service id for a given envronment which is being passed via the FastlyGetServiceID function in jenkins libraries
// credentialId:   (string) This is the Fastly API token stored in a secure credentials file within the Jenkins server


def call (service_id,credentialId){
    withCredentials([string(credentialsId: "${credentialId}", variable: 'FASTLY_API_TOKEN')]){
        sh "curl -X POST -H 'Fastly-Key:${FASTLY_API_TOKEN}' https://api.fastly.com/service/$service_id/purge_all"
    }
}