#!/usr/bin/env groovy

def call(){
  output = sh (script: "date +'%Y-%m-%d-%H-%M-%S'",returnStdout: true).trim()
  return output
}