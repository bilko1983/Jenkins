#!/usr/bin/env groovy

def call(){
  def shebang = (env.DEBUG == 'TRUE') ? '' : '#!/bin/bash +x'
  return shebang
}
