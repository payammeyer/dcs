#! /bin/sh

# Create a local git/svn repo for commits as target by using dcs-services ant build build
# Must give svn revision argument.

svn_rev=$1

ant -Dsvn.release.rev=${svn_rev} initialize-repositories



