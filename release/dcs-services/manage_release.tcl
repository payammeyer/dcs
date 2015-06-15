#! /usr/bin/tclsh

# Automate answering release questions for a module
# Arguments: svn_revision module_path module_release_version module_dev_version parent_dependency_release_version parent_dependency_dev_version dependency_release_version dependency_dev_version

package require Expect

set release_script "./release.sh"

set svn_revision [lindex $argv 0]
set module_path [lindex $argv 1]
set release_version [lindex $argv 2]
set dev_version [lindex $argv 3]

set parent_dependency_release_version [lindex $argv 4]
set parent_dependency_dev_version [lindex $argv 5]

set dependency_release_version [lindex $argv 6]
set dependency_dev_version [lindex $argv 7]

set svn_tag $release_version

puts "Running release script $release_script on module $module_path svn revision $svn_revision"
puts "Module release: $release_version, Dev: $dev_version"
puts "Dependency release: $dependency_release_version, Dev: $dependency_dev_version"

spawn $release_script $svn_revision $module_path $release_version $dev_version

# Long timeout
set timeout [expr {20 * 60}]

set done_parent 0

expect {
    {What version should the dependency be reset to for development} {
	if {$done_parent} {
	    send "$dependency_dev_version\r"
	} else {
	    send "$parent_dependency_dev_version\r"
	    set done_parent 1
	}

	exp_continue;
    } {Which release version should it be set to} {
	if {$done_parent} {
	    send "$dependency_release_version\r"
	} else {
	    send "$parent_dependency_release_version\r"
	}

	exp_continue;
    } {Do you want to resolve them now} {
	send "yes\r"
	exp_continue;
    } {What is the release version for} {
	send "$release_version\r"
	exp_continue;
    } {What is the new development version for} {
	send "$dev_version\r"
	exp_continue;
    } {What is SCM release tag or label for} {
	send "$svn_tag\r"
	exp_continue;
    } {specify the selection number} {
	send "0\r"
	exp_continue;
    } timeout {
	send_user "timeout\n"
	send "\r"
	exp_continue;
    } eof {
	send_user "exit\n"
	exit
    }
}
