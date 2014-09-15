#!/bin/bash

export JAVA_OPTS="-Xms256m -Xmx512m -Djava.awt.headless=true"
export APP_LAUNCHER=org.octopus.OctopusLauncher
export APP_JAR=woods-octopus
export APP_HOME=`pwd`
export APP_CONF=$APP_HOME/conf
export APP_LIB=$APP_HOME/lib

