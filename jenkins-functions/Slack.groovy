#!/usr/bin/env groovy

def call(channel,message){
    def COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'danger', 'ABORTED': 'warning']
    if (message) {
        m = message
    }else{
        m = "*["+currentBuild.currentResult+"]* ${env.JOB_NAME} - #${env.BUILD_NUMBER}\n${env.BUILD_URL}"
    }
    slackSend (
        channel: channel, 
        color: COLOR_MAP[currentBuild.currentResult], 
        message: m)
}