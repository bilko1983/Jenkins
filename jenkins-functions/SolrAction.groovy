#!/usr/bin/env groovy

// Puts monit status on given solr node into either 'Monitor' or 'Unmonitor'

// credentialId:    (string) credentials passed through function with variable name 'PEM' and 'USERNAME' attached to be able to ssh onto server
// action:          (string) 'monitor' or 'unmonitor'
// node:            (string) node to be actioned against e.g. 'solr-master' or 'solr-slave'

def call (credentialId,action,nodeIp) {
    withCredentials([sshUserPrivateKey(credentialsId: "${credentialId}", keyFileVariable: 'PEM', passphraseVariable: '', usernameVariable: 'USERNAME')]) {
        allowedActions = ['monitor', 'unmonitor']
        if (action in allowedActions) {
            echo "Changing Solr status on $nodeIp to $action"
            sh """
            set +x
            ssh -i ${PEM} ${USERNAME}@${nodeIp} sudo monit $action solr
            current_status=\$(ssh -i ${PEM} ${USERNAME}@${nodeIp} sudo monit status solr | grep 'status' | head -1 | awk '{print \$2}')
            if [ "$action" == "monitor" ] && [ "\$current_status" != "OK" ]; then
                while [ "\$current_status" != "OK" ]; do
                    sleep 10
                    current_status=\$(ssh -i ${PEM} ${USERNAME}@${nodeIp} sudo monit status solr | grep "status" | head -1 | awk '{print \$2}')
                done
            elif [ "$action" == "unmonitor" ] && [ "\$current_status" == "OK" ]; then
                while [ "\$current_status" == "OK" ]; do
                    sleep 10
                    current_status=\$(ssh -i ${PEM} ${USERNAME}@${nodeIp} sudo monit status solr | grep "status" | head -1 | awk '{print \$2}')
                done
            fi"""
        } else {
            error ("${action} is not a valid option. Please select either one of these actions:\n${allowedActions}")
        }
    }
}