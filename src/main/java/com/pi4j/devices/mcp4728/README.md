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
P1 Gpio12   RDY/BSY
            Ch B   MCP3008 ch6
            Ch D   MCP3008 ch1



1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4728.sh parms........

https://datasheetspdf.com/pdf-file/634126/MicrochipTechnology/MCP4728/1

-rde anf -rdf both have max input value 0x0FFF

-ef and -ev cannot exceed -vdd



        Read output votage on channel 1 of MCP3008
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rde 2047 -vdd 3.3 -ch D


        All remainig examples read output votage on channel 6 of MCP3008

Updates using raw binary value
To Update DAC and EEPROM, set to 50 % of reference voltage Vin of 3.3 volts

vrefBit 1 by default      vout = 1.0v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rde 2047 -vdd 3.3 -ch B -d 12

set vrefBit 0, uses vdd
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 0  -vdd 3.3 -ch B -d 12

vrefBit 0      vout = 1.6 v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rde 2047 -vdd 3.3 -ch B -d 12

To Update DAC Fast, set to 50 % of reference voltage
vrefBit 0, vout 1.6v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rdf 2047 -vdd 3.3 -ch B -d 12

set vrefBit 1, uses 2.048
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 1  -vdd 3.3 -ch B -d 12

set vrefBit 1, set gain to 2x  
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sg 1  -vdd 3.3 -ch B -d 12

To Update DAC Fast, set to 50 % of reference voltage
vrefBit 0, gain 2x  vout 3.3v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rdf 2047 -vdd 3.3 -ch B -d 12


To Update DAC Fast, set to 100 % of reference voltage   
vrefBit 0, vout 3.3v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -rdf 4095 -r -vdd 3.3 -ch B -d 12

Updates using voltage value

To Update DAC Fast, set to 1.8v of reference voltage     
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -fv 1.8 -vdd 3.3 -ch B -d 12

To Update DAC and EEPROM, set to 3.3v
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -ev 3.3 -vdd 3.3 -ch B -d 12

To Update DAC Fast, reset chip,  set to 3.1     
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -ev 3.1 -r -vdd 3.3 -ch B -d 12





UNIT TEST
Validate dumps registers show bits updated correctly

To Update vref bit channel 1     
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sv 0 -vdd 3.3 -ch B -d 12

To Update gain bit channel 1     
sudo ./runMcp4728.sh -b 0x01 -a 0x60 -sg 1 -vdd 3.3 -ch B -d 12




vref 0 = vdd
     1 = 2.048

gain 0 = 1
     1 = 2x



Test program.  Loop indefinitely executing the shell command

./runLoop.sh  program.sh args ......

Example usage:
./runLoop.sh ./runMcp3008.sh -vref 3.3 -p 0x0?