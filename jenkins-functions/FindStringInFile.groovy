#!/usr/bin/env groovy

def call(search, file) {
  status = sh (
      script: """
      if grep -q $search $file; then
          echo true
      else
          echo false
      fi
      """,
      returnStdout: true
      ).trim()
      return status
}