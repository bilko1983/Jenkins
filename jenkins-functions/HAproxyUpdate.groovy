#!/usr/bin/env groovy

def call(dnsRecordsFile,mfEnvironment){
    sh """
    export CHEF_ENV=new-dev-env
    ssh ec2-user@bastion-public-001.dev.matchescloud.com -i ~/.ssh/eks-dev-ssh "sudo kubectl get configmaps -n ${mfEnvironment} -o yaml haproxy-cfgmap > /home/ec2-user/${mfEnvironment}.yaml"
    cat ${dnsRecordsFile} | grep ${mfEnvironment}-app | grep '[0-9].matches' > app_records.txt
    while read line; do
        trimmed_line=\$(echo \$line | sed 's/.matcheslocal.com//')
        server_number=\$(echo \$line | sed "s/${mfEnvironment}.app.//g" | sed "s/.matcheslocal.com//g")
        server_ip=\$(knife search "chef_environment:${mfEnvironment} AND name:\$trimmed_line*" | grep IP | awk '{print \$2}')
        ssh ec2-user@bastion-public-001.dev.matchescloud.com -i ~/.ssh/eks-dev-ssh "sed -i '/server app\$server_number/ c\\        server app\$server_number \$server_ip:9001 cookie s1 check maxconn 25 maxqueue 500 weight 18' /home/ec2-user/${mfEnvironment}.yaml"
    done <app_records.txt
    ssh ec2-user@bastion-public-001.dev.matchescloud.com -i ~/.ssh/eks-dev-ssh "sudo kubectl replace -n ${mfEnvironment} -f /home/ec2-user/${mfEnvironment}.yaml"
    """
}
