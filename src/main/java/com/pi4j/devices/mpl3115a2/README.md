#
#
#      *
#      * -
#      * #%L
#      * **********************************************************************
#      * ORGANIZATION  :  Pi4J
#      * PROJECT       :  Pi4J :: EXTENSION
#      * FILENAME      : README.md
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


https://www.nxp.com/docs/en/data-sheet/MPL3115A2.pdf      



----Pi------------------MPL3115A2 -------      
3.3v--------------------P1---Vdd   
Grnd--------------------P2---Gnd   
3.3v--------------------P3---3v   
GPIO26------------------P4---INT2    
GPIO15------------------P5---INT1   
SCL---------------------P6---SCL    
SDA---------------------P7---SDA  


At present supports retrieval Temperature Pressure and Altitude.     
A future commit will incorporate 'target' interrupt support.        




1. mvn clean package      
2. cd target/distribution      
3. sudo ./runMPL3115A2.sh   

Use of different bus or device address or GPIO, use the applicable values in the following command.      
sudo ./runMPL3115A2.sh -b 0x01 -a 0x60   -int1 15   -int2 26   -t trace       

Bus and address, GPIOs connected to int1 and int2    Trace level (default 'info')  


sudo ./runMPL3115A2.sh -b 0x01 -a 0x60   -int1 15   -int2 26   -t trace  -x   
Bus and address, GPIOs connected to int1 and int2    Trace level (default 'info')  
-x reset chip.   Initial chip access may fail to retrieve data and log an error message.   
When the chip has completed reset normal data retrieval will resume.   


     




