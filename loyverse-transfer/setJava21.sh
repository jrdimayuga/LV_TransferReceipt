#!/bin/sh
## Run the script as source or the shorthand .
## ensuring any variables created or modified by the script will be available after the script completes
## source ./setJava17.sh
## . ./setJava17.sh

echo "Updating java version to 21"
export JAVA_HOME=/Users/jdimayuga/Library/Java/JavaVirtualMachines/jdk-21.0.2.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:/Users/jdimayuga/apache-maven-3.8.8/bin:/usr/local/bin:/System/Cryptexes/App/usr/bin:/usr/bin:/bin:/usr/sbin:/sbin:/var/run/com.apple.security.cryptexd/codex.system/bootstrap/usr/local/bin:/var/run/com.apple.security.cryptexd/codex.system/bootstrap/usr/bin:/var/run/com.apple.security.cryptexd/codex.system/bootstrap/usr/appleinternal/bin:/Applications/Docker.app/Contents/Resources/bin/
JAVA_VER=$(java -version)
echo "$JAVA_VER"
echo "Java version successfully updated to 21"