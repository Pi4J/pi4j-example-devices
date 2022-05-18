Pi4J :: Java I/O Library for Raspberry Pi :: Device :: VL53L0X TimeOfFlight (TOF) Sensor
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2

### This repository contains device specific support for various devices.

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts
==========================================================================

VL53L0X TimeOfFlight (TOF) device.

Uses I2C connectivity.

The ST corp does not release a real datasheet for this device. Their document DocID029105 does explain some APIs at a
high level. I found the Adafruit website contained 'C' code based on the DocID029105 APIs.

This program uses the VL53L0X_PerformSingleRangingMeasurement() pattern where a ranging operation is requested the
interrupt status polled to determine range data is now available.

The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2 documentation. Consult that logger
class documentation to understand its various logging options.

Build execution:

1. mvn clean package
2. cd target/distribution
3. sudo ./runVL53L0X.sh

Note: If you connect the reset line, you must execute the program including the -r option. This will set the correct
default logic level for the Pi GPIO.

The following are possible parameters parms: -b hex value bus   
-a hex value address  
-r reset integer value GPIO --> XSHUT    
-x hex value existing address prior to reset -t trace trace values : "trace", "debug", "info", "warn", "error" or "off"
Default "info"

The program defaults to the device being connected to Pi i2c bus 1, and ACKs device address 0X29.

At power on the chip is configured to 0x29 device address, default parm values will operate correctly.

sudo ./runVL53L0X.sh

The user can reconfigure the chips device (ACK) address

sudo ./runVL53L0X.sh -b 0x1 -a 0x2D -r 21 -x 0x29 -t info 
               -a 0x2D desired future address -r 21 Pi gpio connected to the chips XSHUT pin -x existing address

sudo ./runVL53L0X.sh -b 0x1 -a 0x2D Must supply the newly configured device address