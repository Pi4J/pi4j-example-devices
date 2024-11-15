Pi4J :: Java I/O Library for Raspberry Pi :: Device ::  DHT22

Uses SN74HC595 to update D0-D7 of the LCD display module
==========================================================================

### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

/**

* If the commented use of the listener DataInGpioListener, this would
* be a more normal implementation. However, the time to idle the gpio from output operation and
* re-init the gpio as an input with a listener takes too long to complete
* and DHT22 signals are lost and the device attempt to send data fails.
*
* So, for the time being a simple polling implementation is used.
  */

https://www.sparkfun.com/datasheets/Sensors/Temperature/DHT22.pdf

Simple usage of the DHT22 Humidity and Temperature sensor.

Pi           DHT22   
5+           1. VDD    
GPIO         2. Data   
Gnd          3. Ground

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runDHT22.sh parms........

sudo ./runDHT22.sh -d 22 -t trace  
