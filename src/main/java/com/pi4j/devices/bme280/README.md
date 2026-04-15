https://pdf1.alldatasheet.com/datasheet-pdf/view/1132060/BOSCH/BME280.html

This example device has been migrated under the drivers. The device code can be found at
https://github.com/Pi4J/pi4j-drivers/tree/main/src/main/java/com/pi4j/drivers/sensor/bmx280


I2C 

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runBME280I2C.sh
   Args if address must be set
   -a 0x77

   No parameters are required. However, if 'any' parameter value is to be supplied:
   parms: -a hex value address
   

SPI
The SPI connection per the Datasheet supports MODE0 or MODE3.
The SPI example assumes SPI bus 0, ChipSelect 0

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runBME280SPI.sh 
4. 


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


