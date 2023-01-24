#!/usr/bin/env groovy

// Update status of the nodes managed by HAProxy, this is done by calling HAProxy API /update endpoint
// name:    (string) the name of the node you are targeting
// status:  (string) one of the following values MAINT, DRAIN, READY
// slackChannel: (string) slack channel where we will be posting haproxy status

def call(nodeName, status, slackChannel) {
    protocol = "https"
    endpoint = "haproxy-api.internal.prd.matchescloud.com"
    desiredStatus = status.toLowerCase()
    count = 0

    allowedStatuses = ['ready', 'maint', 'drain']
    if (!(status in allowedStatuses)) {
        error("${status} is not a valid option. Please select either one of these statuses:\n${allowedStatuses}")
    }

    currentStatus = HAProxyGetStatus(nodeName, protocol, endpoint)

    // If current status is different from the desired status, update it
    if (currentStatus != status) {
        HAProxyUpdateNodeStatus(nodeName, desiredStatus, protocol, endpoint)
    } else {
        echo "$nodeName already in $status"
    }

    currentStatus = HAProxyGetStatus(nodeName, protocol, endpoint)

    // It might take a few seconds for the status to be updated
    // Retry to get the status every 5 seconds 6 times.
    while ((currentStatus != status) && (count < 6)) {
        sh 'sleep 5'
        echo "Waiting for status [$currentStatus] to change to [$status]"
        currentStatus = HAProxyGetStatus(nodeName, protocol, endpoint)
        count = count + 1
    }

    // If the status could not be updated fail the pipeline
    if (currentStatus != status) {
        slackSend(
            channel: "$slackChannel",
            message: ":x: Failed to update `$nodeName` to *$status*\nLogs can be found <$BUILD_URL/console|*here*>",
            color: 'danger'
        )
        error 'The status could not be updated'
    } else {
        slackSend(
            channel: "$slackChannel",
            message: ":white_check_mark: Updated `$nodeName` to status: *$status*",
            color: 'good'
        )
        if (currentStatus == "drain") {
            sh 'sleep 120'
        }
    }
}

def HAProxyGetStatus(nodeName, protocol, endpoint) {
    status = sh(
        script: "curl -s $protocol://$endpoint/node?name=$nodeName | cut -d ',' -f 2 | cut -d ':' -f 2",
        returnStdout: true
    ).trim().replace('"', "").toLowerCase().replaceAll("\\d", "").replaceAll("/", "");
    if (status == 'up') {
        status = 'ready'
    }
    return status
}

def HAProxyUpdateNodeStatus(nodeName, desiredStatus, protocol, endpoint) {
    sh "curl -s -X GET -G $protocol://$endpoint/update -d name=$nodeName -d status=$desiredStatus -d backend=hybris"
    sh "curl -s -X GET -G $protocol://$endpoint/update -d name=$nodeName -d status=$desiredStatus -d backend=hybris_priority"
}
