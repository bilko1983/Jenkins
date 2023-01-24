#!/usr/bin/env groovy

// This Function allows to run Chef recipies on DEV environments
// user: user needed to authenticate
// credentialID: pem file used with user to authenticate
// env: Environment to target
// role: Resources to target (ex. hybris-search-master)
// recipe: Recipe(s) to run on target (ex. recipe[mf-hybris::solr_cores])

def call(user,credentialID,env,role,recipe){
  withCredentials([file(credentialsId: credentialID, variable: 'PEM')]) {
      sh "CHEF_ENV=new-dev-env knife ssh -x ${user} --ssh-identity-file ${PEM} 'chef_environment:${env} AND role:${role}' \"sudo chef-client -o '${recipe}'\" -a ipaddress"
  }
}
