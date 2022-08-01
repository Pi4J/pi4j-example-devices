
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





#     #                                                   ###
#     # #    # ##### ######  ####  ##### ###### #####     ###
#     # ##   #   #   #      #        #   #      #    #    ###                              
#     # # #  #   #   #####   ####    #   #####  #    #     #                                      
#     # #  # #   #   #           #   #   #      #    #                                            
#     # #   ##   #   #      #    #   #   #      #    #    ###                                     
#####   #   ##   ##  ######  ####    #   ###### #####     ###                                 ### 
             ##    #  ####  #####     ####   ####  #    # #####  #      ###### ##### ######    ### 
             # #   # #    #   #      #    # #    # ##  ## #    # #      #        #   #         ### 
             #  #  # #    #   #      #      #    # # ## # #    # #      #####    #   #####      #  
             #   # # #    #   #      #      #    # #    # #####  #      #        #   #             
             #    ## #    #   #      #    # #    # #    # #      #      #        #   #         ### 
             #     #  ####    #       ####   ####  #    # #      ###### ######   #   ######    ### 
                      ######                                                           ###         
                      #     #  ####     #    #  ####  #####    #    #  ####  ######    ###         
                      #     # #    #    ##   # #    #   #      #    # #      #         ###         
                      #     # #    #    # #  # #    #   #      #    #  ####  #####      #          
                      #     # #    #    #  # # #    #   #      #    #      # #                     
                      #     # #    #    #   ## #    #   #      #    # #    # #         ###         
                      ######   ####     #    #  ####    #       ####   ####  ######    ###         


When I have the HW I will complete this work.




ADS1256 module mounted Waveshare  AD/DA board
Chip connected to SPI0   

                 5V  ---------  ch0          P13    Syn/PwrD  --------- GPIO 27 or 5V  tie high
                3.3v ---------  ch1          P15    CS   ------------   GPIO22  Chip select
                                ch2          P11    DRDY  ------------  GPIO17
                                ch3          P21    Dout ------------   GPIO9  MISO 
                                ch4          P19    Din  ------------   GPIO10 MOSI
                                ch5          P23    Sclk  ------------  GPIO11 SCLK
                                ch6          P6     Agnd  ------------  Gnd
                                ch7          P2     DVdd  ------------  5V
                GPIO6           D0           P12    RESET  -----------  GPIO18
                GPIO13          D1           
                GPIO19          D2           
                GPIO26          D3           



ADS1256 module mounted
Chip connected to SPI0                      Module pin number
                                             \/   
                 5V  ---------  ch0          P1 Syn/PwrD  ------------ GPIO 27 or 5V  tie high
                3.3v ---------  ch1          P2 CS   ------------  GPIO22  Chip select
                                ch2          P3 DRDY  ------------ GPIO17
                                ch3          P4 Dout ------------  GPIO9  MISO 
                                ch4          P5 Din  ------------  GPIO10 MOSI
                                ch5          P6 Sclk  ------------  GPIO11 SCLK
                                ch6          P7 Agnd  ------------  Gnd
                                ch7          P8 DVdd  ------------  5V
                                D0           
                                D1           
                                D2           
                                D3           
                                             




ADS1256 direct connect to the Pi.
Chip connected to SPI0   

                 5V  ---------  ch0             DVdd  ------------  5V
                3.3v ---------  ch1             VrefP ------------  5V
                                ch2             VrefN ------------  Gnd
                                ch3             Agnd  ------------  Gnd
                                ch4             Sclk  ------------  GPIO11 SCLK
                                ch5             Dout ------------  GPIO9  MISO
                                ch6             Din  ------------  GPIO10 MOSI
                                ch7             CS   ------------  GPIO22  Chip select
                                D0              RESET  ----------  GPIO18 or 5V  tie high
                                D1              Syn/PwrD  ------------ GPIO 27 or 5V  tie high
                                D2              Avdd -------------  5V
                                D3              AINCOM  ---        +- voltage compare single point
                                                DRDY  ------------ GPIO17

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
sudo ./runADS1256.sh   -vref 5.0 -rst 18  -cs 22  -drdy 17


To monitor only pin 0
sudo ./runADS1256.sh -p 0x0  -pp AIN0 -pn AINCOM  -rst 18  -cs 22  -drdy 17

sudo ./runADS1256.sh -p 0x0  -pp AIN0 -pn AIN1  -rst 18  -cs 22  -drdy 17




Change the logging detail to 'trace'    vref voltage of 3.3
sudo ./runADS1256.sh  -t trace -vref 3.3 -pp AIN0 -pn AINCOM  -rst 18  -cs 22  -drdy 17

sudo ./runADS1256.sh  -t trace -vref 3.3 -pp AIN0 -pn AIN1  -rst 18  -cs 22  -drdy 17


Use SPI 1
sudo ./runADS1256.sh  -s 0x01  -rst 18  -cs 22  -drdy 17





sudo ./runADS1256.sh  -rst 18  -cs 22  -drdy 17  -pdwn 27 
INFO com.pi4j.devices.ads1256.ADS1256App - Channel : 0   Bytes read : 3  Value : 1023



sudo ./runADS1256.sh    -cs 22  -drdy 17  -pp AIN0 -pn AIN1  -t trace
INFO com.pi4j.devices.ads1256.ADS1256App - Channel : 1   Bytes read : 3  Value : 647
omitt  -rst 18  -pdwn 27




//    -c 0x00   not used when -cs parm is used



sudo ./runADS1256.sh   -pp AIN0 -pn AIN1  -cs 22  -drdy 17  -pdwn 27  -t trace
// no  -rst 18    use command
