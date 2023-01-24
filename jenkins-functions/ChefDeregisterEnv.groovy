#!/usr/bin/env groovy

// This Function removes chef nodes and clients from a specified environment
// env: Environment to target

def call(env){
    sh (script:  """
    knife node list | grep -w $env > deregisterEnv.txt || echo "Environment not found"
    cat deregisterEnv.txt
    if [ -s deregisterEnv.txt ]
    then
      knife client delete -y \$(knife client list | grep -w $env) || echo "Client not found"
      knife node delete -y \$(knife node list | grep -w $env) || echo "Node not found"
    else
      echo "No environments currently bootstrapped"
    fi
    """, returnStdout: true).trim()
}
