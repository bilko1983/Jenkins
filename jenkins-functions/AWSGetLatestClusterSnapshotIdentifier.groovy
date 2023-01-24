#!/usr/bin/env groovy

def call(snapshotIdentifier) {

  identifier = sh (
      script: """aws rds describe-db-cluster-snapshots \
      --db-cluster-identifier $snapshotIdentifier \
      --query="reverse(sort_by(DBClusterSnapshots[? SnapshotType=='automated'], &SnapshotCreateTime))[0]".DBClusterSnapshotIdentifier""",
      returnStdout: true
  ).trim()
  return identifier
}
