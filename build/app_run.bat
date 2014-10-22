@echo off
mode con cols=200 lines=50 & color 03
call app_env.bat
title octopus-service
set cp=%APP_CONF%
SetLocal EnableDelayedExpansion  
FOR /r %APP_LIB% %%i IN (*.jar) DO (
	SET cp=!cp!;%%i
)
set CLASSPATH=!cp!;%APP_HOME%/%APP_JAR%.jar
java %JAVA_OPTS% %APP_LAUNCHER% > server.log
EndLocal  



