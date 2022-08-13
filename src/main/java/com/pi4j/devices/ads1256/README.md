
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





1. mvn clean package
2. cd target/distribution
3. sudo ./runADS1256.sh




Tested 
ADS1256 module mounted.  The chip I have has no vref input pin and no labeling nor
documentation. Used a VOM to map lines from the ADS1256 to external pins
It appears the DVdd 5V is used as the V-reference.


At present time the code does not support access to the chips four GPIOs.


Chip connected to SPI0                  Module pin number
                                     \/           \/
                3.3V  --------- AIN0 ch0          P1 Syn/PwrD  ------------  5V  tie high
                5  v ---------  AIN1 ch1          P2 CS   ------------  GPIO22  Chip select
                                AIN2 ch2          P3 DRDY  ------------ GPIO17
                                AIN3 ch3          P4 Dout ------------  GPIO9  MISO 
                                AIN4 ch4          P5 Din  ------------  GPIO10 MOSI
                                AIN5 ch5          P6 Sclk  ------------  GPIO11 SCLK
                                AIN6 ch6          P7 Agnd  ------------  Gnd
                                AIN7 ch7          P8 DVdd  ------------  5V
                                D0           
                                D1           
                                D2           
                                D3           





-cs and -drdy required for this chip configuration.  -rst and -pdwn should not be used
sudo ./runADS1256.sh   -pp AIN0 -pn AIN1  -cs 22  -drdy 17  -t trace -x

No reset pin and no powerDown pin omitt  -rst 18  -pdwn 27


sudo ./runADS1256.sh   -pp AIN1 -pn AINCOM  -cs 22  -drdy 17  -t trace -x -vref 5.0
TRACE com.pi4j.devices.ads1256.ADS1256  Channel  :1/8  value  :8388607
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 5.0


sudo ./runADS1256.sh   -pp AIN1 -pn AINCOM  -cs 22  -drdy 17  -t trace -x -vref 5.0

TRACE com.pi4j.devices.ads1256.ADS1256  Channel  :0/8  value  :5441076
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 3.243134408370782


sudo ./runADS1256.sh   -pp AIN1 -pn AIN0  -cs 22  -drdy 17  -t trace -x -vref 5.0
TRACE com.pi4j.devices.ads1256.ADS1256 - Channel  :1/0  value  :3031976
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 1.8071987399099756




-vref option: if > 0,  feature will display calculated Channel input voltage.





DRDY    low ->> data conversion complete, RDATA 24 bits.


>>>>Single shot
WREG set channel
STANDBY cmd
WAKEUP
delay 33 microSeconds
DRDY low
RDATA
STANDBY




!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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



NOT Tested  When I have the HW I will complete this work.


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




