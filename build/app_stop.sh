#!/bin/bash
. ./app_env.sh

RUN_CMD=`echo java $JAVA_OPTS $APP_LAUNCHER`

echo $RUN_CMD

RUN_PID=`ps -C $RUN_CMD -f | grep $RUN_CMD | awk '{print $2}'`

echo 'PID For $APP_JAR : '$RUN_PID
kill $RUN_PID

