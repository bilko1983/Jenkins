#!/usr/bin/env groovy
def call(dnsRecordsFile,mfEnvironment,nodeType,chefRoles,chefClientVersion){
 withCredentials([file(credentialsId: ENVIRONMENT_KEY, variable: 'PEM')]) {
 sh """
   export PATH=/opt/chefdk/embedded/bin:\$PATH
   export RUBYOPTS="-E utf-8"
   export LC_CTYPE=en_US.UTF-8
   export CHEF_ENV=new-dev-env
   
   case ${nodeType} in
     solr-master|esb-legacy) cat ${dnsRecordsFile} | grep "${mfEnvironment}-${nodeType}" > ${nodeType}_records.txt ;;
     *) cat ${dnsRecordsFile} | grep "${mfEnvironment}-${nodeType}-[[:digit:]]" > ${nodeType}_records.txt
   esac

   while read line; do
     echo \$line
     trimmed_line=\$(echo \$line | sed 's/.matcheslocal.com//')
     echo \$trimmed_line
     knife bootstrap \$line -E ${mfEnvironment} -i "${PEM}" -x jenkins -r \"${chefRoles}\" --sudo -N \$trimmed_line --ssh-user centos --yes --bootstrap-version ${chefClientVersion}

    # Stopping Chef client
    knife ssh -x centos --ssh-identity-file "${PEM}" \"chef_environment:${mfEnvironment} AND name:\$trimmed_line\"  \"sudo service chef-client stop\" -a ipaddress
   done <${nodeType}_records.txt
 """
 }
}
