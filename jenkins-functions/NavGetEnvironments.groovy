#!/usr/binenv groovy

// This Function will call all view in the wintel jenkins environment 


def call(){
    allEnvironments = ['dev01']
    folder = "$JOB_NAME".split("/")[0]
    if (folder in allEnvironments) {
        return folder
    } else {
        return allEnvironments
    }
}