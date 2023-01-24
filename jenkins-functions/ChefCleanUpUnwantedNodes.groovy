#!/usr/bin/env groovy

// This Function checks all AWS instances in a 'running' state and compares this against the nodes in our chef server. Then removes the anomolies
// env:     This is the Environment you wish to rn the job over e.g. dev11
// domain:  the matches domain to be passed, e.g. 'matcheslocal.com' or 'matchesfashion.com' etc.

def call(aws_role, aws_account_number, env, domain){
    role_session_name = "jenkins-session"
    withAWS(role:"$aws_role", roleAccount:"$aws_account_number", duration: 900, roleSessionName: "$role_session_name") {
        sh (script:  """
        export CHEF_ENV=new-dev-env;
        knife node list | grep -w "$env" > chefNodesToDestroy.txt || echo "Environment not found"
        cat chefNodesToDestroy.txt
        if [ -s chefNodesToDestroy.txt ]
        then
          while read line; do
                INSTANCESTATE=`aws ec2 describe-instances --query 'Reservations[*].Instances[*].[PrivateIpAddress,InstanceId,State.Name]' --filters Name=tag:Name,Values=\$line Name=instance-state-name,Values=running  --output json >> /dev/null`

                if [ "\$INSTANCESTATE" != "[]" ]
                    then
                        echo "This node EXISTS on aws-test account: \$line.$domain"
                else
                        echo "#### instance \$line.$domain does NOT EXISTS in aws account, It is going to be deleted from chef server!!!: ####"
                        {
                            knife client delete -y \$line
                        } || {
                            echo "Instance \$line.$domain knife client delete command failed!!!"
                            exit 1
                        }
                        {
                            knife node delete -y \$line
                        } || {
                            echo "Instance \$line.$domain knife node delete command failed with: "
                            exit 1
                        }
                fi
          done <chefNodesToDestroy.txt
        else
          echo "No environments currently bootstrapped"
        fi
        """, returnStdout: true).trim()
    }
}

