#!/usr/bin/env groovy

// Update commit status for a build. Possible values for status are: 'pending', 'failure', 'success', 'error'
// To use this in your repo, the `mf-svc-github` user needs write access to the repo on Github
def call(status, repo, context, description) {
    withCredentials([string(credentialsId: 'github-token', variable: 'TOKEN')]) {
        sh """
            curl --location --request POST 'https://api.github.com/repos/matchesfashion/$repo/statuses/${GIT_COMMIT}' \
            --header 'Accept: application/vnd.github.v3+json' \
            --header 'Authorization: Bearer ${TOKEN}' \
            --header 'Content-Type: text/plain' \
            -d '{ "state": "$status", "context":"$context", "description":"$description", "target_url":"${BUILD_URL}"}'
        """
    }
}