#!/bin/bash
. ./app_env.sh

RUN_CMD=`echo java $JAVA_OPTS $APP_LAUNCHER`

echo $RUN_CMD

RUN_PID=`ps -C 'java -Xms256m -Xmx512m -Djava.awt.headless=true org.octopus.OctopusLauncher' -f | grep 'java -Xms256m -Xmx512m -Djava.awt.headless=true org.octopus.OctopusLauncher' | awk '{print $2}'`

echo 'PID For $APP_JAR : '$RUN_PID
kill $RUN_PID