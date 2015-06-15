#!/bin/bash

TXT_OUT=target/generated-sources/license/THIRD-PARTY.txt
WIKI_OUT=target/generated-sources/license/THIRD-PARTY.wiki
AWK_SCRIPT=src/license/3rd-party-report-to-wiki.awk
mvn -Dall -DuseMissingFile clean license:aggregate-add-third-party
awk -f $AWK_SCRIPT $TXT_OUT > $WIKI_OUT
echo "3rd Party reports generated in target/generated-sources/license:"
echo $TXT_OUT
echo $WIKI_OUT
