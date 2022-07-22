
#
#
#
#     *
#     * -
#     * #%L
#     * **********************************************************************
#     * ORGANIZATION  :  Pi4J
#     * PROJECT       :  Pi4J :: EXTENSION
#     * FILENAME      :  README.sh
#     *
#     * This file is part of the Pi4J project. More information about
#     * this project can be found here:  https://pi4j.com/
#     * **********************************************************************
#     * %%
#     *   * Copyright (C) 2012 - 2022 Pi4J
#      * %%
#     *
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
#     *
#
#
#
#



Default assumption
Chip connected to SPI0   CE0

                 5V  ---------  ch0             DVdd  ------------  3.3V
                3.3v ---------  ch1             VrefP ------------  5V
                                ch2             VrefN ------------  Gnd
                                ch3             Agnd  ------------  Gnd
                                ch4             Sclk  ------------  GPIO11 SCLK
                                ch5             Dout ------------  GPIO9  MISO
                                ch6             Din  ------------  GPIO10 MOSI
                                ch7             CS   ------------  GPIO8  CE0
                                D0              RESET  ----------  5V  tie high
                                D1              Syn/PwrD  ------------  5V  tie high
                                D2              Avdd -------------  5V
                                D3              AINCOM  ---        +- voltage compare single point


DRDY    low ->> data conversion complete, RDATA 24 bits.


>>>>Single shot
WREG set channel
STANDBY cmd
WAKEUP
delay 33 microSeconds
DRDY low
RDATA
STANDBY





>>>  Repeat group to walk through all pins   P21
DRDY low
WREG configure mux usage  EX 0x23  AINp=AIN2  AINn = AIN3
SYNC command                            ------
Delay                                          -  or strobe sync/pdwn pin
WAKEUP command                          -------
Delay
RDATA  low ->> data ready to read

>>> Repeat read of same pin
DRDY low
WREG configure mux usage  EX 0x23  AINp=AIN2  AINn = AIN3
SYNC command           -----
Delay                       -
WAKEUP command              -  Or strobe SYNC/PDWN pin
Delay                 ------
Repeat following:
DRDY low, data read.
RDATA  read 24 bits
Toggle syn/pwr pin High/Low



1. mvn clean package
2. cd target/distribution
3. sudo ./runADS1256.sh


The above command will monitor and display the 24 bit value for all 8 pins.


-vref option: if > 0,  the -d feature will display calculated Channel input voltage.


To monitor all pins 0   Vref of 5.0 volts
sudo ./runADS1256.sh   -vref 5.0


To monitor only pin 0
sudo ./runADS1256.sh -p 0x0

Change the logging detail to 'trace'    vref voltage of 3.3
sudo ./runADS1256.sh -p 0x0 -t trace -vref 3.3 -pp AIN0 -pn AINCOM



Use SPI 1
sudo ./runADS1256.sh -p 0x0 -s 0x01


Use ChipSelect 1
sudo ./runADS1256.sh -p 0x0 -c 0x01



sudo ./runADS1256.sh -p 0x0 -c 0x00
INFO com.pi4j.devices.ads1256.ADS1256App - Channel : 0   Bytes read : 3  Value : 1023



sudo ./runADS1256.sh -p 0x01
INFO com.pi4j.devices.ads1256.ADS1256App - Channel : 1   Bytes read : 3  Value : 647


