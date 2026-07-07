#!/usr/bin/env bash




java  -classpath ./*:../classes  com.pi4j.devices.bmp280.BMP280SpiExample  "$@"
