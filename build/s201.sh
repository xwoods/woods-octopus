#!/bin/bash

. ./pub.sh 201

cd $BUILD_PATH

scp *.tar.gz strato@192.168.2.201:/home/strato/octopus2/
