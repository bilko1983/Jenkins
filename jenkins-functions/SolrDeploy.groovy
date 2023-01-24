#!/usr/bin/env groovy

// This function executes chef-client command against solr nodes and then deploys given artifacts
// credentialId: (string) credentials passed through function with variable name 'PEM' and 'USERNAME' attached to be able to ssh onto server
// buildNr: (integar) Build number of the artifacts bbeing deployed
// nodeIp: (string) Specific IP of the node being deployed to

def call (credentialId,buildNr,nodeIp,node){
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        //sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo chef-client -l info"

        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo mkdir -p /opt/solr/deploy-agent"

        sh "ssh -i ${PEM} -t -t -t -o BatchMode=yes -o StrictHostKeyChecking=no ${USERNAME}@${nodeIp} sudo chown -R ${USERNAME} /opt/solr/deploy-agent"

        sh "scp -i ${PEM} -o BatchMode=yes -o StrictHostKeyChecking=no ${WORKSPACE}/lib/deploy-agents/hybris_solr.rb ${USERNAME}@${nodeIp}:/opt/solr/deploy-agent/hybris_solr.rb"

        sh """knife ssh -x "${USERNAME}" --ssh-identity-file "${PEM}" 'name:*${node}*' 'sudo su - hybrissolr -c \"ruby /opt/solr/deploy-agent/hybris_solr.rb /opt/solr ${buildNr} --force --url https://aws-prd-inf-ci-1.prd.aws.matchesfashion.com/job/aws-copy//ws/matches/hybris/temp/hybris/hybrisServer/ \"' -a ipaddress"""

        sh """knife ssh -x "${USERNAME}" --ssh-identity-file "${PEM}" 'name:*${node}*' 'sudo su - hybrissolr -c \"ruby /opt/solr/deploy-agent/hybris_solr.rb /opt/solr ${buildNr} --force --clean\"' -a ipaddress"""
    }
}
