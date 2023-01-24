#!/usr/bin/env groovy

// Execute Terragrunt plan, destroy or apply

// The function needs 4 parameters to work:
// Parameter 1 (mandatory): String with action to execute on terragrunt (options:'plan', 'apply', destroy). An empty string can be passed, not action will be taken.
// Parameter 2 (mandatory): String with path where our terragrunt script resides
// Parameter 3 (mandatory): String with value 'true' or 'false'. This enables debug mode while running
// Parameter 4 (mandatory): environment where the plan/apply/destroy should be applied

def call(String action, String path, String debug, String env){
    if ( "${env}".contains('prd')) {
        genericVars = "prd.generic"
    } else {
        genericVars = "dev.generic"
    }
    if ("${debug}"=='true') {
        addDebug = "TF_LOG='DEBUG'"
    } else {
        addDebug = ""
    }
    if ("${action}"!='plan' && "${action}"!='plan-all') {
        autoApprove = "-auto-approve"
    } else {
        autoApprove = ""
    }
    if ("${action}"=='plan' || "${action}"=='apply' || "${action}"=='destroy' || "${action}"=='plan-all' || "${action}"=='apply-all' || "${action}"=='destroy-all') {
        target = path.replace("$WORKSPACE", "")
        echo "Terraform $action\nEnvironment: $env\nTarget: $target"
        sh "TF_VAR_env=${env} TF_VAR_generic=$genericVars $addDebug terragrunt ${action} --terragrunt-working-dir $path $autoApprove --terragrunt-source-update --terragrunt-non-interactive"
    }
}
