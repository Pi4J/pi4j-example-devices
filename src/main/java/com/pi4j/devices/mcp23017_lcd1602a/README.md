#
#
#      *
#      * -
#      * #%L
#      * **********************************************************************
#      * ORGANIZATION  :  Pi4J
#      * PROJECT       :  Pi4J :: EXTENSION
#      * FILENAME      :  README.md
#      *
#      * This file is part of the Pi4J project. More information about
#      * this project can be found here:  https://pi4j.com/
#      * **********************************************************************
#      * %%
#      *   * Copyright (C) 2012 - 2022 Pi4J
#       * %%
#      *
#      * Licensed under the Apache License, Version 2.0 (the "License");
#      * you may not use this file except in compliance with the License.
#      * You may obtain a copy of the License at
#      *
#      *      http://www.apache.org/licenses/LICENSE-2.0
#      *
#      * Unless required by applicable law or agreed to in writing, software
#      * distributed under the License is distributed on an "AS IS" BASIS,
#      * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#      * See the License for the specific language governing permissions and
#      * limitations under the License.
#      * #L%
#      *
#
#


MCP23017                        1602A
GPB0                            RS
GPB1                            RW
GPB2                            EN
GPA0                            D0
GPA1                            D1
GPA2                            D2
GPA3                            D3
GPA4                            D4
GPA5                            D5
GPA6                            D6
GPA7                            D7
                                VSS grnd
                                VDD +5
                                V0  grnd
                                A   +5
                                K   grnd
                                A2  grnd
                                A1  +5
                                A0  grnd





Simple implementation of MCP23017, specific to controlling a 1602A LCD.  If you are interested   
in a full utilization of all MCP23017 functionality, see :  
src/main/java/com/pi4j/devices/mcp23017/README.md  
and
src/main/java/com/pi4j/devices/mcp23xxxApplication/README.md  



1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMCP23017_LCD.sh.sh parms........


sudo ./runMCP23017_LCD.sh   -b 0x01   -a 0x21  -x 4 -t trace -line1  aBc   -line2  XyZ
Bus 1, address 0x21,  reset pin GPIO4,  detailed logging , line one text, line two text  
Clears display, Line1 begins in fourth position, line2 begins in second position

sudo ./runMCP23017_LCD.sh    -b 0x01   -a 0x21  -x 4  -r   -clearLCD  -t trace -line1  aaaaaa -line1Offset 4  -line2  zzzzzz  -line2Offset 2

line one starts at offset 4, line two offset 2
Param  -shiftL 2  shift both lines 2 positions to left
