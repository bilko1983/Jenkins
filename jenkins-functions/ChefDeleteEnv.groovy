#!/usr/bin/env groovy

// This Function removes chef nodes and clients from a specified environment
// env: Environment to target

def call(env){
    sh (script:  """export CHEF_ENV=new-dev-env
    knife node list | grep $env > chefnodes.txt
    while read line; do
        knife node delete \$line --yes
        knife client delete \$line --yes
    done <chefnodes.txt
    """, returnStdout: true).trim()
}
