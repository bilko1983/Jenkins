#!/usr/bin/env groovy

def call(new_cluster_id, destiny_account) {
    sh """aws rds modify-db-cluster-snapshot-attribute \
    --db-cluster-snapshot-identifier $new_cluster_id \
    --attribute-name restore \
    --values-to-add $destiny_account"""
}
