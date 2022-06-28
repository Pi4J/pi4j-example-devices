Pi4J :: Java I/O Library for Raspberry Pi :: Device :: TCA9548
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2 

### This repository contains device specific support for various devices.

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts
==========================================================================

The following lists the currently supported devices:


MCP23008 I2C:  drive and read chip GPIOs
src/main/java/com/pi4j/devices/mcp23008/README.md

MCP23017 I2C:  drive and read chip GPIOs
src/main/java/com/pi4j/devices/mcp23017/README.md

MCP23008 and MCP23017 I2C:  Pin monitoring (interrupt support)
src/main/java/com/pi4j/devices/mcp23xxxApplication/README.md

TCA9548 I2C:  I2C Bus switch 
src/main/java/com/pi4j/devices/tca9548/README.md


VL53L0X I2C:  TimeOfFlight device
src/main/java/com/pi4j/devices/vl53L0X/README.md


BMP280  I2C:  Temperature and Pressure Sensor
src/main/java/com/pi4j/devices/bmp280/README.md

MCP3008 SPI:  10bit A/D converter
src/main/java/com/pi4j/devices/mcp3008/README.md

SN74HC595 GPIO Manipulation (bit bang): 8 Bit shift register   
src/main/java/com/pi4j/devices/sn74hc595/README.md   
This package does not use any other code within my repo.   

MCP4725 I2C:  12 bit DAC   
src/main/java/com/pi4j/devices/mcp4725/README.md  
This package does not use any other code within my repo.  


hd44780u_lcd1602a  1602A LCD
src/main/java/com/pi4j/devices/hd44780u_lcd1602a/README.md
This package uses the SN74HC595 within my repo.

pcf8574a_lcd1602a  1602A LCD    
src/main/java/com/pi4j/devices/pcf8574a_lcd1602a/README.md   
This package does not use any other code within my repo.   


mcp23017_lcd1602a   1602A LCD    
src/main/java/com/pi4j/devices//cp23017_lcd1602a/README.md    
This package does not use any other code within my repo.   

