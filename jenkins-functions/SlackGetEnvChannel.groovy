#!/usr/bin/env groovy

def call(){

    def map = [
            "dev1": "product_team",
            "dev3": "product_team",
            "dev9": "product_team",
            "TPR-": "product_team",

            "dev6": "team-secure",
            "dev4": "team-secure",
            "TSE-": "team-secure",

            "dev7": "engagement-ci",
            "dev8": "engagement-ci",
            "FD-": "engagement-ci",

            "TL-": "account-team",

            "dev10": "devops-priv",
            "dev11": "devops-priv",

            "integration": "environments",
            "regression": "environments",

            "master": "ecom-git-flow"
    ]

    def channel = "no_channel"
    map.each { key, val ->
        if("$JOB_NAME".contains(key)){
            channel = val
            return
        }
    }
    return channel

}
