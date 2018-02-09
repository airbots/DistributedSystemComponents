#This script is used for killing all processes that have given match in the first arg

#!/bin/bash
user=(`whoami`)
pids=(`ps aux|grep $1 | sed -r 's/^'$user'\s+(\S+).*/\1/'`)
for i in "${pids[@]}"
do
  kill -9 $i
done