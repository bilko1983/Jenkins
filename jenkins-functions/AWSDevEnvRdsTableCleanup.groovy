#!/usr/bin/env groovy

// Daily hyris.JGROUPSPING table cleanup.
// Takes db 
//    - DB cluster identifier per environment 
//    - DB Username|Passwd

def call(db){
    withCredentials([usernamePassword(credentialsId: 'dbs-creds', passwordVariable: 'PASSWD', usernameVariable: 'USERNAME')]) {
        sh """
        docker run --rm mysql/mysql-server '/bin/bash' -c \"mysql -u $USERNAME -p$PASSWD -h '$db'.cluster-ctm9wjkfghio.eu-west-1.rds.amazonaws.com -e \'TRUNCATE table hybris.JGROUPSPING;\'\"
        """
    }
}
