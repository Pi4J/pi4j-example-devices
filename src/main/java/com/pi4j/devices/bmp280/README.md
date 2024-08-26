# Pi4J_V2-TemperatureSensor

Implement Temperature/Pressure BMP280

https://www.digikey.com/en/datasheets/bosch-sensortec/bosch-sensortec-bst-bmp280-ds001-19

The I2C example program has the assumption the BMP280 is connected to Pi bus 1, and the device is factory configured to
operate as device address 0x77. If these assumptions are not possible, the bus and device address value can be passed
as parms. Or modify the BMP280I2cExample program to use your specific bus and device address.


The SPI example assumes SPI bus 0, ChipSelect GPIO 21. The GPIO is configurable as a program parm option.

The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2 documentation. Consult that logger
class documentation to understand its various logging options.

The I2C connection functions as written in the Phillips spec.


I2C connection path.
1. mvn clean package
2. cd target/distribution
3. sudo ./runBMP280I2C.sh
   Args if bus and or address must be set
   -b 0x01 -a 0x77



The SPI connection per the Datasheet supports MODE0 or MODE3.  The spec shows reading a register consists of:
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
1. mvn clean package
2. cd target/distribution
3. sudo ./runBMP280SPI.sh -csp 21




Use of different bus or device address, use the applicable values in the following command. 
sudo ./runBMP280I2C.sh -b 0x01 -a 0x77  


Will create the BMP280 device to create a BMP280Device instance Call the various temperature and pressure methods
defined in the interface. The device defaults to Pi Bus 1 and device address 0X77

No parameters are required. However, if 'any' parameter value is to be supplied:
parms: -b hex value bus -a hex value address -t trace  
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "info"

The file BMP280.pdf documents a method of connecting an Adafruit BMP280 to a Pi.


I2C connection 

| RPi         | Color   | Sensor |
| :---        | :---    | :---   |
| Pin1  3.3V  |	Red     | Vin    |
| N/C         |         | 3v	   |
| Pin6  Gnd   | Brown   | Gnd    |
| Pin5  SCL   | Green   | SCK    |
| N/C         | Yellow  | SDO    |
| Pin3  SDA   | Blue    | SDI    |
| Pin17 3.3v  | Orange  | CS     |

SPI connection using 4-wire SPI

| RPi         | Color   | Sensor |
| :---        | :---    | :---   |
| Pin19 MOSI  | Blue    | SDI    |
| Pin21 MISO  | Yellow  | SDO    |
| Pin23 SCLK  | Green   | SCK    |
| Pin40 GPIO21| Orange  | CS     |
| Pin6  Gnd   | Brown   | GND    |
| Pin1  3.3V  | Red     | Vin    |


