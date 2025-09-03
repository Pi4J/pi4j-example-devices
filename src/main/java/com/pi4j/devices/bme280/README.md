https://pdf1.alldatasheet.com/datasheet-pdf/view/1132060/BOSCH/BME280.html



This example device has been migrated under the drivers. The device code can be found at
https://github.com/Pi4J/pi4j-drivers/tree/main/src/main/java/com/pi4j/drivers/sensor/bmx280




The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2 documentation.
Consult that logger class documentation to understand its various logging options.

I2C connection path.

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runBME280I2C.sh
   Args if bus and or address must be set
   -b 0x01 -a 0x77

The SPI example assumes SPI bus 0, ChipSelect GPIO 21. The GPIO is configurable as a program parm option.

The SPI connection per the Datasheet supports MODE0 or MODE3. The spec shows reading a register consists of:

1. Chip select driven low
2. Eight clocks pulses to write the register address via the Pi MOSI
3. Multiple eight clock pulses for each byte of data returned thru the Pi MISO
4. Chip select driven high

In either Mode0 or Mode3 the read data bytes are miss-read and the clock pulses are not signaled for the expected reads.
The SPI open flags support configuration of writing a determined number of bytes before continuing the clock pulses
for the expected read data . This however requires the BMP280 chip connect using the 3-wire configuration and
the chips config register be modified. As most chips use 4-wire configuration I use 4-wire, and use a configurable
GPIO as the CS (chip select)

SPI connection path.

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runBME280SPI.sh -csp 21

No parameters are required. However, if 'any' parameter value is to be supplied:
parms: -b hex value bus -a hex value address -t trace  
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "info"

SPI connection using 4-wire SPI

Pi p19 MOSI BMP SDI Blue
Pi p21 MISO BMP SDO Yellow
Pi p23 SCLK BMP SCK Green
Pi p40 GPIO21 BMP CS Orange
Pi Gnd BMP GND Brown
Pi 3.3 BMP Vin Red