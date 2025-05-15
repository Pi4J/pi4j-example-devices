

#        * -
#        * #%L
#        * **********************************************************************
#        * ORGANIZATION  :  Pi4J
#        * PROJECT       :  Pi4J :: EXTENSION
#        * FILENAME      : README.md
# *
#        * This file is part of the Pi4J project. More information about
#        * this project can be found here:  https://pi4j.com/
#        * **********************************************************************
#        * %%
#        *   * Copyright (C) 2012 - 2023 Pi4J
#         * %%
# *
#        * Licensed under the Apache License, Version 2.0 (the "License");
#        * you may not use this file except in compliance with the License.
#        * You may obtain a copy of the License at
# *
#        *      http://www.apache.org/licenses/LICENSE-2.0
# *
#        * Unless required by applicable law or agreed to in writing, software
#        * distributed under the License is distributed on an "AS IS" BASIS,
#        * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#        * See the License for the specific language governing permissions and
#        * limitations under the License.
#        * #L%
# *


# PCA9685 12-bit PWM LED controlled
Datasheet
https://www.nxp.com/docs/en/data-sheet/PCA9685.pdf


## Build and Run

1. ./mvnw clean package
2. cd target/distribution
3. ./runPCA9685.sh OPT parms........
   Parm -t traceLevel
   " parms:  -a hex value address, -b bus, -p OE pin, -h help \n" +
   "  -x reset  -s1 addr1   -s2 addr2   -s3 addr3  -q quit -e E enable/ D disable  -t trace \n" +
   "-ledON devaDDR led# timeOn timeOff   -intensity devaDDR led#  intensity \n " +
   " -d debug  -sm1 newVal    -sm2 newVal -sf frequency"
All values are entered as Hex data


Note In example usage the EO pin is connected to gpio 12
VCC connected to Pi 3.3v and V+ connected to Pi 5v
Note  Must enable the LEDs via -e E, this will drive OE GPIO low.


## Usage 
Led intensity and LED time on and off are all decimal input, other parms are hex input.

./runPCA9685.sh -b 0x1 -a 0x70  -p 0x0c -e E -s1 0x72 -s2 0x74 -s3 0x76 -t trace

./runPCA9685.sh -b 0x1 -a 0x70 -p 0x0c -x -e E -s1 0x72 -s2 0x74 -s3 0x76

After setting the above attributes, the chip can be used to continue
controlling LEDs/PWMs

Also, the above .sh will start a simple application.  The following commands
are an example of using the app.  The app displays to possible 
parms you can enter.


## Intensity 
100%
-intensity 0x70 0 4095
 25%
-intensity 0x70 0 1024
50%
-intensity 0x70 0 2048

## ledOn
0xffff will always be high, 0 will always be low and 0x7fff will be half high and then half low.

LED0  set intensity to 0x700  (2000) of a possible 0xFFF (4095)


LED 0 off
      -ledOn 0x70 0 0 4095

LED 0 on
     -ledOn 0x70 0 4095 0 

     

## PWM

Read Datasheet section 7 examples to calculate ON and Off times
 -ledOn 0x70 15 3686 3275

-ledOn 0x70 15 409 1228

-sf 31         LEDs flicker=
-ledOn 0x70 15 3685 3275

-sf 32

-ledOn 0x70 0 1024 3072


# NOTE:

If the chip was never reconfigured its device address is 0x70.

#Wiring                                                                                                                                                                                                                                                                                                 

Pi PCA9685
GRND GRND
GPIO12 OE
SCL SCL
SDA SDA
3.3VDC VCC
5VDC V+
pin0 yellow LED +
pin0 red NC
pin0 black LED -
pin1 yellow LED +
pin1 red NC
pin1 black LED -
pin8 yellow Servo orange
pin8 red Servo red
pin8 black Servo brown