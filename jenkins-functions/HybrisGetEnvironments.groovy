#!/usr/bin/env groovy

def call(){
    allEnvironments = ['dev1', 'dev2', 'dev3', 'dev4', 'dev5', 'dev6', 'dev7', 'dev8', 'dev9', 'dev10', 'dev11', 'integration', 'regression', 'hybris-test']
    folder = "$JOB_NAME".split("/")[0]
    if (folder in allEnvironments) {
        return folder
    } else {
        return allEnvironments
    }
}
