#!/usr/bin/env bash

 java  -classpath ./*:../classes  com.pi4j.devices.pca9685.PCA9685App  "$@"
