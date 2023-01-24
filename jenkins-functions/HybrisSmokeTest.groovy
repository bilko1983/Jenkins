#!/usr/bin/env groovy

// This Function runs an automated smoke test via endtest.io

def call() {
    sh """
        #!/bin/bash

        hash=\$(curl -k -X GET --header "Accept: */*" "https://endtest.io/api.php?action=runTestSuite&appId=73374758&appCode=83359067&testSuite=246392&selectedPlatform=mac&selectedOs=j&selectedBrowser=chrome&selectedResolution=d&selectedLocation=london&selectedCases=all&writtenAdditionalNotes=Prod-chrome")
        for run in {1..7}
        do
          sleep 30
          result=\$(curl -k -X GET --header "Accept: */*" "https://endtest.io/api.php?action=getResults&appId=73374758&appCode=83359067&hash=\$hash&format=json")
          if [ "\$result" == "Test is still running." ]
          then
            status=\$result
            # Don't print anything
          elif [ "\$result" == "Processing video recording." ]
          then
            status=\$result
            # Don't print anything
          elif [ "\$result" == "Stopping." ]
          then
            status=\$result
          elif [ "\$result" == "Erred." ]
          then
            status=\$result
            echo \$status
          elif [ "\$result" == "" ]
          then
            status=\$result
            # Don't print anything
          else
             echo "\$result" | jq
             exit 0
          fi
        done
        exit
    """
}
