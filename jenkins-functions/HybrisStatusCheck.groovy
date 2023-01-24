#!/usr/bin/env groovy

//Author: Shahin Ahmed 
// The purpose of this function is to  check if the hybris node is up and runnng
// domain: address of the target Machine
// nodeName: Name of the Node

def call(domain,nodeName){
    try{    
        println "Waiting for server ${nodeName} to come up"
        COMMAND_OUTPUT = sh (
        script: "curl -I -k https://${domain}:9002/buildinfo --connect-timeout 900",
        returnStdout: true
        ).trim()
         
        if (COMMAND_OUTPUT.contains('200')){
        println "The server ${nodeName} is up."
        println COMMAND_OUTPUT
        }
    }
    catch(Exception ex){
        println ("Error: Something Went wrong please check the below error message \n"+ ex.getMessage())
    }
}
