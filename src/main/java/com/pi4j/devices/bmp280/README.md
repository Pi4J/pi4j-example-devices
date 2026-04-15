# Pi4J_V2-TemperatureSensor

Implement Temperature/Pressure BMP280

https://www.digikey.com/en/datasheets/bosch-sensortec/bosch-sensortec-bst-bmp280-ds001-19

The I2C example program has the assumption the BMP280 is connected to Pi bus 1, and the device is factory configured to
operate as device address 0x77. If these assumptions are not possible, the bus and device address value can be passed
as parms. Or modify the BMP280I2cExample program to use your specific bus and device address.

The SPI example assumes SPI bus 0, ChipSelect 0.

The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2 documentation. Consult that logger
class documentation to understand its various logging options.

The I2C connection functions as written in the Phillips spec.


I2C
1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runBMP280I2C.sh
   Args if bus and or address must be set
   -b 0x01 -a 0x77

The SPI connection per the Datasheet supports MODE0 or MODE3.

SPI 
1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runBMP280SPI.sh



Will create the BMP280 device to create a BMP280Device instance Call the various temperature and pressure methods
defined in the interface. 

No parameters are required. However, if 'any' parameter value is to be supplied:
parms: -b hex value bus -a hex value address -t trace  
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "info"

The file BMP280.pdf documents a method of connecting an Adafruit BMP280 to a Pi.

I2C connection

| RPi        | Color  | Sensor |
|:-----------|:-------|:-------|
| Pin1  3.3V | 	Red   | Vin    |
| N/C        |        | 3v	    |
| Pin6  Gnd  | Brown  | Gnd    |
| Pin5  SCL  | Green  | SCK    |
| N/C        | Yellow | SDO    |
| Pin3  SDA  | Blue   | SDI    |
| Pin17 3.3v | Orange | CS     |

SPI connection using 4-wire SPI

| RPi          | Color  | Sensor |
|:-------------|:-------|:-------|
| Pin19 MOSI   | Blue   | SDI    |
| Pin21 MISO   | Yellow | SDO    |
| Pin23 SCLK   | Green  | SCK    |
| Pin24 SPICE0 | Orange | CS     |
| Pin6  Gnd    | Brown  | GND    |
| Pin1  3.3V   | Red    | Vin    |


