# Pi4J_V2-TemperatureSensor

Implement Temperature/Pressure BMP280

https://www.digikey.com/en/datasheets/bosch-sensortec/bosch-sensortec-bst-bmp280-ds001-19

The example program has the assumption the BMP280 is connected to Pi bus 1, and the device is factory configured to
operate as device address 0x76. If these assumptions are not possible, the bus and device address value can be passed as
parms. Or modify the BMP280I2cExample program to use your specific bus and device address.

The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2 documentation. Consult that logger
class documentation to understand its various logging options.

1. mvn clean package
2. cd target/distribution
3. sudo ./runBMP280.sh

Use of different bus or device address, use the applicable values in the following command. sudo ./runBMP280.sh -b 0x01
-a 0x76

Will create the BMP280 device to create a BMP280Device instance Call the various temperature and pressure methods
defined in the interface. The device defaults to Pi Bus 1 and device address 0X76

No parameters are required. However, if 'any' parameter value is to be supplied:
parms: -b hex value bus -a hex value address -t trace  
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "info"

The file BMP280.pdf documents a method of connecting an Adafruit BMP280 to a Pi.