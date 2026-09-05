### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

                   Adafruit MCP4728 

Pi 3.3V     Vin
Pi Grnd     GND
Pi Pin5     SCL
Pi Pin3     SDA
Pi Grnd     LDAC
            Ch B   MCP3008 ch6
            Ch D   MCP3008 ch1



1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4728.sh parms........

-b 0x? hex value bus    
-a 0x?? hex value address  
-vdd float reference voltage
-pv true/false.  Persist values, default false.
-r  reset chip  
All following parms effect channel -ch 
-ch channel A B C D  
-d  digital input   0 - 4095 
-v  voltage  0 - vdd
-sv vref 0 or 1  
-sg gain 0 or 1" +

Note    -v -d -sv -sg mutually exclusive 

String helpString = " parms: -b 0x? hex value bus    -a 0x?? hex value address   \n " +
"  -r  reset chip    -ch channel A B C D    -d  digital value (0 ... 4095) \n" +
"  -v  voltage  (0 ... vdd)   -pv  persist values true/false  -vdd decimal reference voltage \n" +
" -sv vrefBit 0 or 1    -sg gainBit 0 or 1 \n" +
"-d -v -sv -sg mutually exclusive  \n" ;


https://datasheetspdf.com/pdf-file/634126/MicrochipTechnology/MCP4728/1

-rde anf -rdf both have max input value 0x0FFF

-ef and -ev cannot exceed -vdd



        Read output votage on channel 1 of MCP3008
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rde 2047 -vdd 3.3 -ch D


        All remainig examples read output votage on channel 6 of MCP3008


Assume starting with channel B vrefBit 1 use 2.048V, gainBit 0  1x
Updates using raw binary value
To Update DAC and EEPROM, set to 50 % of reference voltage Vin of 3.3 volts

vrefBit 1 by default      vout = 1.0v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -d 2047 -vdd 3.3 -ch B

vrefBit 1 by default      vout = 1.0v   persist values
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -d 2047 -vdd 3.3 -ch B -pv true


set vrefBit 0, uses vdd
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 0  -vdd 3.3 -ch B  -pv true

vrefBit 0 persisted previous test      vout = 1.6 v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -d 2047 -vdd 3.3 -ch B  


set vrefBit 1, uses 2.048
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 1  -vdd 3.3 -ch B  -pv true  

set gainBit 1, set gain to 2x  
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sg 1  -vdd 3.3 -ch B   -pv true

To Update DAC Fast, set to 50 % of reference voltage with 2x gain
50% 2.048 = 1.0v      2x 1.0 = 2.0v
vrefBit 0, gain 2x  vout 2.0v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -d 2047 -vdd 3.3 -ch B  


set vrefBit 0, uses vdd
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 0  -vdd 3.3 -ch B  -pv true

To Update DAC Fast, set to 100 % of reference voltage   
vrefBit 0, vout 3.2v      2x gain not used when vrefBit 0 (use vdd)
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -d 4095 -r -vdd 3.3 -ch B  

Updates using voltage value

To Update DAC Fast, set to 1.8v      vout 1.8v      
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -v 1.8 -vdd 3.3 -ch B  

To Update DAC and EEPROM, set to 3.3v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -v 3.3 -vdd 3.3 -ch B  -pv true  

To set vrefBit 1 
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 1 -vdd 3.3 -ch B  -pv true

To Update DAC and EEPROM, set to 3.3v   ????
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -v 3.3 -vdd 3.3 -ch B  -pv true


To Update DAC Fast, reset chip,  set to 3.1     
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -v 3.1 -r -vdd 3.3 -ch B  





UNIT TEST
Validate dumps registers show bits updated correctly

To Update vref bit channel 1 of MCP3008    
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 0 -vdd 3.3 -ch D  

To Update gain bit channel 1 of MCP3008    
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sg 1 -vdd 3.3 -ch D  






vref 0 = vdd
     1 = 2.048

gain 0 = 1
     1 = 2x



Test program.  Loop indefinitely executing the shell command

./runLoop.sh  program.sh args ......

Example usage:
./runLoop.sh ./runMcp3008.sh -vref 3.3 -p 0x0?