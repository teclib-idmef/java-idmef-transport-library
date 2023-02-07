#!/bin/bash
# This script is used to run the client 

HERE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

if [ $# -ne 0 ] ; then
    ( cd $HERE ; ./gradlew --info runClient -Pargs="$*")
else
    ( cd $HERE ; ./gradlew --info runClient)
fi
