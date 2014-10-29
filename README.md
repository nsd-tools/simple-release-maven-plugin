simple-release-plugin
=====================

Maven plugin for simple releasing. It only updates automatically pom version as be needed.

Usage:

simple-release-plugin set param "newVersion" in execution context, used as input for version:set (maven-version-plugin)

To make release (it is required project version be a snapshot):

com.github.nsd-tools.maven.plugins:simple-release-maven-plugin:simple-release versions:set -DdoRelease=true

To make snapshot (it is required project version be a release):

com.github.nsd-tools.maven.plugins:simple-release-maven-plugin:simple-release versions:set -DdoRelease=false