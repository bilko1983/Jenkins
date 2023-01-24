#!/usr/bin/env groovy

def call(ROLE_SESSION_NAME, type) {
    AWS_USER          = "jenkins"
    DEV_ACCOUNT_ID    = "953612792372"
    ASSUME_ROLE       = "arn:aws:iam::${DEV_ACCOUNT_ID}:role/ExternalAdminRole"
    TMP_FILE          = ".temp_credentials"


    // File myFile = new File(TMP_FILE);
    def exists = fileExists TMP_FILE

    if (!exists){
        sh """aws sts assume-role \
            --profile $AWS_USER \
            --output json \
            --role-arn $ASSUME_ROLE \
            --duration-seconds 14400 \
            --role-session-name $ROLE_SESSION_NAME \
            > $TMP_FILE"""
        // sh "cat $TMP_FILE"
    }

    output = sh (
    script: "cat ${TMP_FILE} | jq -r '.Credentials."+type+"'",
    returnStdout: true
    ).trim()

    return output
}
