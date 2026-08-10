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
MCP3008 ch7 Vout

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4725.sh parms........

https://datasheetspdf.com/pdf-file/634126/MicrochipTechnology/MCP4725/1

-rde anf -rdf both have max input value 0x0FFF

-ef and -ev cannot exceed -vref

-vref option:  the -d feature will display calculated output voltage.

Updates using raw binary value
To Update DAC and EEPROM, set to 50 % of reference voltage Vin of 3.3 volts

sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rde 0x7ff -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage     
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rdf 0x7ff -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage Set trace level 'off'
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rdf 0x07ff -t off -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage, reset chip, dump DAC and EEPROM   
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -rdf 0x07ff -r -vref 3.3

Updates using voltage value

To Update DAC and EEPROM, set to 50 % of reference voltage Vin of 3.3 volts

sudo ./runMcp4725.sh -b 0x01 -a 0x62 -fv 2 -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage     
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -fv 1.8 -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage Set trace level 'off'
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -ev 5.0 -t off -vref 3.3

To Update DAC Fast, set to 50 % of reference voltage, reset chip, dump DAC and EEPROM   
sudo ./runMcp4725.sh -b 0x01 -a 0x62 -ev 3.1 -r -vref 3.3





Test program.  Loop indefinitely executinf the shell command

./runLoop.sh  program.sh args ......

Example usage:
./runLoop.sh ./runMcp3008.sh -vref 3.3 -p 0x0