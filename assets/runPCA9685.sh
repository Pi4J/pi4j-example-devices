
#
# *   * Copyright (C) 2012 - 2024 Pi4J
#  * %%
# *
# * Licensed under the Apache License, Version 2.0 (the "License");
# * you may not use this file except in compliance with the License.
# * You may obtain a copy of the License at
# *
# *      http://www.apache.org/licenses/LICENSE-2.0
# *
# * Unless required by applicable law or agreed to in writing, software
# * distributed under the License is distributed on an "AS IS" BASIS,
# * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# * See the License for the specific language governing permissions and
# * limitations under the License.
# *
# *-
# * #%L
# * **********************************************************************
# * ORGANIZATION  :  Pi4J
# * PROJECT       :  Pi4J :: EXTENSION
# * FILENAME      :  runPCA9685.sh
# *
# * This file is part of the Pi4J project. More information about
# * this project can be found here:  https://pi4j.com/
# * **********************************************************************
# * %%
#

java --module-path . --module  com.pi4j.devices/com.pi4j.devices.pca9685.PCA9685App  "$@"
