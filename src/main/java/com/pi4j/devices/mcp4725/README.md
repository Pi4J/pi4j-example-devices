### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

                   Adafruit MCP4725 

Pi 3.3V Vin
Pi Grnd GND
Pi Pin5 SCL
Pi Pin3 SDA
Pi Grnd A0
        Vout  MCP3008 ch7


1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4725.sh parms........

https://datasheetspdf.com/pdf-file/634126/MicrochipTechnology/MCP4725/1

-rde anf -rdf both have max input value 0x0FFF

-ef and -ev cannot exceed -vref

-vref option:  the -d feature will display calculated output voltage.

Updates using raw binary value
To Update DAC and EEPROM, set to 50 % of reference voltage Vin of 3.3 volts

sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rde 2047 -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage     
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rdf 2047 -vref 3.3


To Update DAC Fast, set to 100 % of reference voltage   
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rdf 4095 -r -vref 3.3

Updates using voltage value

To Update DAC Fast, set to 1.8v of reference voltage     
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -fv 1.8 -vref 3.3

To Update DAC/eeprom Fast, set to 3.3v
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -ev 3.3 -vref 3.3

To Update DAC Fast, reset chip,  set to 3.1     
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -ev 3.1 -r -vref 3.3





Test program.  Loop indefinitely execute the shell command

./runLoop.sh  program.sh args ......

Example usage:
./runLoop.sh ./runMcp3008.sh -vref 3.3 -p 0x0