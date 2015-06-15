#!/bin/bash

#
# The purpose of this script is to let the release manager
# know what dependencies have missing or incorrect license
# information.  The release manager uses this information
# to populate the supplemental-models.xml file in the 
# org.dataconservancy:supplemental-resources module.
#
# This script generates a report of all dependencies that
# lack a code license in $REPORT. After executing this 
# script, review the $REPORT for dependencies that 
# lack a license.
#
# Dependencies that lack a code license should be addressed by:
#   1) Determining what the dependency's code license actually is
#   2) Updating the supplemental-models.xml file in the 
#      org.dataconservancy:supplemental-resources    
#      module with the correct license information for the 
#      dependency.
#
# After the supplemental-resources module has been updated, deploy it.
#

OUT="target/license-warning.txt"
REPORT="target/3rd-party-license-report.txt"

echo "Generating a 3rd-party license report to $REPORT ..."
echo "Executing mvn clean"
mvn clean 
mkdir target
echo "Downloading licenses for all dependencies"
mvn -Dall license:download-licenses | grep WARNING > $OUT
echo "Generating report ..."
cat $OUT |sort -u > $REPORT
echo "... complete.  Report generated to $REPORT"
