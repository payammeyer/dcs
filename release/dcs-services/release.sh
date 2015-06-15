#! /bin/sh

# Run this script fron the release directory after having initialized repositories.
# Arguments are svn revision, module directory like /maven/parent/trunk, release version, dev version

svn_rev=$1
module_dir=$2
release_version=$3
dev_version=$4

svn_tag=${release_version}

release_plugin_version="2.5"
releasedir=`pwd`

svn_local_repo_location="${releasedir}/target/svn-fs-repo"
svn_local_repo_url="file://${svn_local_repo_location}"

maven_local_repo_stage_location="${releasedir}/target/maven-stage-repo"
maven_local_repo_stage_url="file://${maven_local_repo_stage_location}"
git_repo_location="${releasedir}/target/git-shallow-clone-r${svn_rev}"
build_timestamp=`date`

# Also specify various build properties needed for frontend and package tool.

cd ${git_repo_location}${module_dir} && mvn \
    org.apache.maven.plugins:maven-release-plugin:${release_plugin_version}:prepare \
    org.apache.maven.plugins:maven-release-plugin:${release_plugin_version}:perform \
    -e \
    -DautoVersionSubmodules=true  \
    -Dgoals=deploy \
    -DcompletionGoals="clean install deploy clean" \
    -Dtag="${svn_tag}" \
    -DreleaseVersion="${release_version}" \
    -DdevelopmentVersion="${dev_version}" \
    -Dsvn.base="${svn_local_repo_url}" \
    -DstagingRepository=local::default::${maven_local_repo_stage_url} \
    -DaltDeploymentRepository=local::default::${maven_local_repo_stage_url} \
    -DlocalRepoDirectory=${maven_local_repo_stage_location} \
    -Dmaven.repo.local=${maven_local_repo_stage_location} \
    -Darguments="-Ddcs.ui.buildNumber='${release_version}' -Ddcs.ui.buildTimeStamp='${build_timestamp}' -Ddcs.ui.repository.revision.number='${svn_tag}' -Ddcs.pkg.tool.buildNumber='${release_version}' -Ddcs.pkg.tool.buildTimeStamp='${build_timestamp}' -Ddcs.pkg.tool.repository.revision.number='${svn_tag}'"
