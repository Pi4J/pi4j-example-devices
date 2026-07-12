#!/usr/bin/env bash



 java  -classpath ./*:../classes  com.pi4j.devices.bme280.BME280DeviceSPI  "$@"

