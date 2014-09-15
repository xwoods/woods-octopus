#!/bin/bash

. ./app_stop.sh

rm -rf conf
rm -rf lib
rm -rf ROOT

rm *.sh
rm *.jar
rm server.log

echo "clear old verson"