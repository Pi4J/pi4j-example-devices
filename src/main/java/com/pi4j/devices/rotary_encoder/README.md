#!/usr/bin/env bash

#

#

#

# *

#       * -

#       * #%L

#       * **********************************************************************

#       * ORGANIZATION  :  Pi4J

#       * PROJECT       :  Pi4J :: EXTENSION

#       * FILENAME      :  runRotaryEncoder.sh

# *

#       * This file is part of the Pi4J project. More information about

#       * this project can be found here:  https://pi4j.com/

#       * **********************************************************************

#       * %%

#       *   * Copyright (C) 2012 - 2022 Pi4J

#        * %%

# *

#       * Licensed under the Apache License, Version 2.0 (the "License");

#       * you may not use this file except in compliance with the License.

#       * You may obtain a copy of the License at

# *

#       *      http://www.apache.org/licenses/LICENSE-2.0

# *

#       * Unless required by applicable law or agreed to in writing, software

#       * distributed under the License is distributed on an "AS IS" BASIS,

#       * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

#       * See the License for the specific language governing permissions and

#       * limitations under the License.

#       * #L%

# *

#

#

#

https://github.com/adafruit/Adafruit_CircuitPython_seesaw

Interrupt pin GPIO 21, Hex 15

I2C connection path.

1. mvn clean install
2. cd target/distribution
3. sudo ./runRotaryEncoder.sh
   Args Interrupt pin or address must be set, optionally -p

sudo ./runRotaryEncoder.sh -i 0x15 -a 0x36 -p 0x0

parms:  -a hex address -i hex interrupt GPIO number, -p hex position, -h help

As the knob is twisted, each indent should change the returned
encoder position by a value of 1.

sudo is included in the command, depending on the I2C provider being used this may
not be required.