#!/usr/bin/env groovy

// This function executes chef-client command against a paticular node and then deploys given artifacts
// credentialId: (string) credentials passed through function with variable name 'PEM' and 'USERNAME' attached to be able to ssh onto server
// buildNr: (integar) Build number of the artifacts bbeing deployed
// nodeIp: (string) Specific IP of the node being deployed to

def call (credentialId,buildNr,nodeIp,node){
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        // sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo chef-client -l info"

        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo mkdir -p /opt/hybris/deploy-agent"

        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo chown -R ${USERNAME} /opt/hybris/deploy-agent"

        sh "scp -i ${PEM} -o BatchMode=yes -o StrictHostKeyChecking=no /var/lib/jenkins/jobs/PRD-Deployment/jobs/deploy/workspace/lib/deploy-agents/hybris.rb ${USERNAME}@${nodeIp}:/opt/hybris/deploy-agent/hybris.rb"
        
        sh """knife ssh -x "${USERNAME}" --ssh-identity-file "${PEM}" 'name:*${node}*' 'sudo su - hybris -c \"ruby /opt/hybris/deploy-agent/hybris.rb /opt/hybris ${buildNr} --force --url https://aws-prd-inf-ci-1.prd.aws.matchesfashion.com/job/aws-copy//ws/matches/hybris/temp/hybris/hybrisServer/    --wait-up\"' -a ipaddress"""

        sh """knife ssh -x "${USERNAME}" --ssh-identity-file "${PEM}" 'name:*${node}*' 'sudo su - hybris -c \"ruby /opt/hybris/deploy-agent/hybris.rb /opt/hybris ${buildNr} --force --clean\"' -a ipaddress"""
    }
    withCredentials([string(credentialsId: 'datadog_API', variable: 'KEY')]){
        sh """
            curl -X POST "https://api.datadoghq.com/api/v1/events?api_key=${KEY}" -H "Content-Type: application/json" -d '{ "title":"Hybris_Production_Deployment", "alert_type":"info", "host":"aws-prd-inf-ci-1", "tags":["env:aws-prd","node:${node}","build:${buildNr}"], "text":"Build Number: ${buildNr} successfully deployed to ${node}", "source_type_name":"JENKINS"}'
        """
    }
}

