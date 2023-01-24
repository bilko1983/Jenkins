#!/usr/bin/env groovy

// This Function requires confirmation from a team on a slack channel to continue with a pipeline
// channel: (string) Name of the channel where the message for approval will appear
// initialMessage: (string) Message asking for approval
// link: (string) Name of the Jenkins URL that points to the input. This is posted at the end of the initialMessage
// responseMessage: (string) Response after input has been approved
// inputMessage: (string) Question
// inputConfirm: (string) confirm message to approve the input

def call(channel, initialMessage, link, responseMessage, inputMessage, inputConfirm){
    Slack($channel,"$initialMessage <${BUILD_URL}input| $link >")
    input message: $inputMessage, ok: $inputConfirm
    Slack($channel,$responseMessage)
}
