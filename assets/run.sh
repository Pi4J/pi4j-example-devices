#!/usr/bin/env bash
cd ../classes
java -cp ../distribution/*:.:   com.pi4j.devices.tca9548.SampleTca9548App -b 0x01 -a 0x70  -f 1   -l  -e 0x2  -r 0x6
$@

