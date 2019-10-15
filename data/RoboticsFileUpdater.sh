#!/usr/bin/env bash

fileName=mecanumtestbot.xml

#Various paths
phonePath=/sdcard/FIRST
compPath=`dirname "$0"`
tmpPath=${compPath}/tmp

#Pretty!  Also absolutely unnecessary, but I like it
RED='\033[0;31m'
NC='\033[0m'

cd ${compPath}
mkdir tmp

adb pull ${phonePath}/${fileName} ${tmpPath}/${fileName}

diff=0
cmp --silent ${compPath}/${fileName} ${tmpPath}/${fileName} || diff=1

if [[ diff -eq 1 ]]; then
    echo "${fileName} is different"

    echo -e "Would you like to ${RED}PUSH${NC} this file to the phone? (Y/N)"
    read push
    if [[ push = "y" ]] || [[ push = "Y" ]]; then
        adb push ${compPath}/${fileName} ${phonePath}

        exit 0
    fi

    echo -e "Would you like to ${RED}PULL${NC} this file from the phone? (Y/N)"
    read pull
    if [[ pull = "y" ]] || [[ pull = "Y" ]]; then
        adb pull ${phonePath}/${fileName} ${compPath}

        exit  0
    fi
fi