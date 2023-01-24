#!/usr/bin/env groovy

// This function returns the name of the user triggering the job

def call(db){
    if (currentBuild.rawBuild.getCause(Cause.UserIdCause)) {
        return currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId()
    } else {
        return "Automated Trigger"
    }
}
