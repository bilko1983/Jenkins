#!/usr/bin/env groovy

def call(source_id, target_id, kms_id) {
  sh """aws rds copy-db-cluster-snapshot \
  --source-db-cluster-snapshot-identifier $source_id \
  --target-db-cluster-snapshot-identifier $target_id \
  --kms-key-id $kms_id"""
}
