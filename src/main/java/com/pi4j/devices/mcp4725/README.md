
## DISCLAIMER :: EXPERIMENTAL VERSION 2

### This repository contains device specific support for various devices.

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts
==========================================================================

                   Adafruit MCP4725 

  Pi 5V                 Vin
  Pi Grnd               GND
  Pi Pin5               SCL
  Pi Pin3               SDA
  Pi Grnd               A0
  MCP3008 ch7           Vout



1. mvn clean package
2. cd target/distribution
3. sudo ./runMCP4725.sh parms........


https://datasheetspdf.com/pdf-file/634126/MicrochipTechnology/MCP4725/1

-rde anf -rdf both have max input value 0x0FFF

-vref option: if > 0,  the -d feature will display calculated output voltage.

To Update DAC and EEPROM, set to 50 %  of reference voltage   Vin of 5 volts    

sudo ./runMCP4725.sh -b 0x01  -a 0x62  -rde 0x7ff   -vref 5


To Update DAC Fast, set to 50 %  of reference voltage     
sudo ./runMCP4725.sh -b 0x01  -a 0x62  -rdf 0x7ff

To Update DAC Fast, set to 50 %  of reference voltage     Set trace level 'off'
sudo ./runMCP4725.sh -b 0x01  -a 0x62  -rdf 0x07ff  - trace off

To Update DAC Fast, set to 50 %  of reference voltage, reset chip, dump DAC and EEPROM   
sudo ./runMCP4725.sh -b 0x01  -a 0x62  -rdf 0x07ff   -r   -d






