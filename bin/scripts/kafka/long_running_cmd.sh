#!/usr/bin/env bash

##########################
# Program to repeat running
# given command in command.txt
# default is 24 hours
# eg: long_running_cmd.sh [hours]
#
##########################

find_command ()
{
  while read LINE
  do
    exec $LINE &
  done < command.txt
}

#take parameter and get number of hour
if [ -z $1 ]
then
  period=24
else
  period=$1
fi

period=$(( ${period}*3600 ))

#loop and run command one by one till timeout
while [ ${period} -gt 0 ]
do
  echo ${period}
  find_command
  sleep 1
  echo reach here
  period=$(( ${period}-1 ))
  echo ${period}
done






