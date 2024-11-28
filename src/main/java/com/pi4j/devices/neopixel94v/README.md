#

#

# *

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

#

#

# NeoPixel94V GRB Stick

## Wiring:  As coded this program uses SPI_0

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runNeopixel.sh OPT parms........
   Parm -t traceLevel

# NOTE:

At present this code uses the SPI.
The timing is calculated for the Pi4 hardware.

NEOPIXEL WS2812B uses the following timing to represent a '1' or a '0' bit. To
accomplish in SPI, a '1' sends a byte0b11111000 and a '0' sends a byte 0b11000000.
When the array of bytes are sent via SPI at a specific frequency the WS2812B
interprets the bytes as correctly timed 0's and 1's.
To accomplish the SPI frequency is set to 8*500_000.

Pulse duration to represent a one or a zero.
int32_t highTime0NanoSeconds, 400 ns
int32_t lowTime0NanoSeconds, 850 ns
int32_t highTime1NanoSeconds, 800 ns
int32_t lowTime1NanoSeconds, 450 ns

RED LEDSTRIP 0x00FF00;
Transmits 8 short, 8 long, 8 short

GREEN LEDSTRIP 0xFF0000
Transmits 8 long, 8 short, 8 short

BLUE LEDSTRIP 0x0000FF
Transmits 8 short, 8 short, 8 long

800/400

300/900

Composition of 24bit data:
G7 G6 G5 G4 G3 G2 G1 G0 R7 R6 R5 R4 R3 R2 R1 R0 B7 B6 B5 B4 B3 B2 B1 B0
Note: Follow the order of GRB to sent data and the high bit sent at first.

The NeoPixel94V class assumes the above order Green Red Blue. The code
method Render() uses this assumption when inflating the LEDs integer bits
to individual bytes.

If your LED strip uses a different order within the 24 bits the render function
must be modified.




