#!/usr/bin/env groovy

// This function requests for QA to approve the pipeline in order to continue

def call(String channel, String build, String ip, String node){
    Slack("${channel}","Finished deployment on `${node}`\nPlease begin QA tests and confirm the outcome <${BUILD_URL}input| here >")
    input message: "Were tests on ${node} Successful?", ok: "Tests Passed"
    Slack("${channel}", "Successfully deployed build *[${build}]* to `${node}` [${ip}] :white_check_mark:")
}