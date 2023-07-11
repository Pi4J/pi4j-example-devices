#!/usr/bin/env bash
#
#
#
#
#      * #%L
#      * **********************************************************************
#      * ORGANIZATION  :  Pi4J
#      * PROJECT       :  Pi4J :: EXTENSION
#      * FILENAME      :  runBMP280I2C.sh
#      *
#      * This file is part of the Pi4J project. More information about
#      * this project can be found here:  https://pi4j.com/
#      * **********************************************************************
#     * %%
#     * Copyright (C) 2012 - 2022 Pi4J
#     * %%
#     * Licensed under the Apache License, Version 2.0 (the "License");
#     * you may not use this file except in compliance with the License.
#     * You may obtain a copy of the License at
#     *
#     *      http://www.apache.org/licenses/LICENSE-2.0
#     *
#     * Unless required by applicable law or agreed to in writing, software
#     * distributed under the License is distributed on an "AS IS" BASIS,
#     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     * See the License for the specific language governing permissions and
#     * limitations under the License.
#     * #L%
#   *
#
#
#

java --module-path . --module  com.pi4j.devices/com.pi4j.devices.bmp280.BMP280I2cExample  "$@"