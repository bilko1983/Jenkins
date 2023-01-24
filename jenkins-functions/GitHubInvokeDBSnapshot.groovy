#!/usr/bin/env groovy

def call() {
    withCredentials([string(credentialsId: 'github_db_refresh_token', variable: 'TOKEN')]) {
    sh """
        curl --location --request POST 'https://api.github.com/repos/matchesfashion/db-refresh/actions/workflows/create-prd-db-snapshot-and-optimize.yml/dispatches' \
        --header 'Accept: application/vnd.github.v3+json' \
        --header 'Authorization: Bearer ${TOKEN}' \
        --header 'Content-Type: text/plain' \
        -d '{ "ref": "master" }'
    """
    }
}