#!/usr/bin/env groovy

// This Function allows to start or stop EC2 instances based on a filter
// Pre-requirements
// AWS cli will use default profile, otherwise overwrite with ENV. AWS_PROFILE
// AWS cli will use default region, otherwise overwrite with ENV. AWS_DEFAULT_REGION

// action: (string) either start or stop
// filter: (string) these are the names of the instances separated by a coma. We can use wildcards.

def call(action, filter) {
    action = action.toLowerCase()
    if ("${action}"=='start' || "${action}"=='stop') {
        if ("${action}"=='start') {
          //Find the current stopped instance
          INSTANCE_STOP_IDS = sh (
                script: "aws ec2 describe-instances --query 'Reservations[*].Instances[*].{Instance:InstanceId}' --filters 'Name=instance-state-name,Values=stopped' 'Name=tag:Name,Values=${filter}' --output text",
                returnStdout: true
          )
          // Check if the command has returned any instance Ids
          if (INSTANCE_STOP_IDS != "" ){ 
            // if the check is valid, convert the string ids into a list
            instance_id_list = INSTANCE_STOP_IDS.split('\n');
            println("Starting the following instance $instance_id_list")
            // Using for loop, go over the list of ids and start the current stopped instance
            for( String values : instance_id_list )
              sh """
                aws ec2 ${action}-instances --instance-ids ${values}
              """
            //check if all the instance has pass the status check
            println("Checking Instance Status")
            for( String values : instance_id_list )
              sh"""
                aws ec2 wait instance-status-ok --instance-ids ${values}
              """
          }
          // if the above check return false then, the following message will be displayed.
          else {
            println("Nothing to start in AWS.")
          }
    
        } else if ("${action}"=='stop') {
            //Find the current running instance
            INSTANCE_RUNNING_IDS = sh (
                  script: "aws ec2 describe-instances --query 'Reservations[*].Instances[*].{Instance:InstanceId}' --filters 'Name=instance-state-name,Values=running' 'Name=tag:Name,Values=${filter}' --output text",
                  returnStdout: true
            )
            // Check if the command has returned any instance Ids
            if (INSTANCE_RUNNING_IDS != "" ){ 
              // if the check is valid, convert the string ids into a list
              instance_id_list = INSTANCE_RUNNING_IDS.split('\n');
              println("Stopping the following instance $instance_id_list")
              // Using for loop, go over the list of ids and start the current stopped instance
              for( String values : instance_id_list )
                sh """
                  aws ec2 ${action}-instances --instance-ids ${values}
                """
            }
            // if the above check return false then, the following message will be displayed.
            else {
              println("Nothing to stop in AWS.")
            }
        }
    } else {
      error ("${action} is not a valid command. Valid options are 'start' 'stop'.")
    }
}


