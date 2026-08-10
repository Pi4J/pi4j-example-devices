#!/usr/bin/env bash


while true; do
    program=$1
    $program $2 $3 $4 $5
    sleep 2  # Waits before running again
done

#  ./loop ./runMcp3008.sh -vref 5.0 -p 0x0
