Pi4J :: Java I/O Library for Raspberry Pi :: Device :: TCA9548
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2 

### This repository is a first device specific support project for the TCA9548

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts

==========================================================================
            
Java classes to access the TCA9548 Low-Voltage 8-Channel I2C Switch.  Includes
a Sample application class SampleTca9548App. This sample demonstrates using
the Tca9548 class to effect chip register modifications and show recovery
concerns if an incorrect request fails.

Supported functions.
    1. reset chip
    2. enable a particular bus
    3. disable a particular bus
    4. Pretty-Print bus enablement status

The classes are implemented using the Pi4j_V2 requirements of September 1 2020.

The project uses one addition pom requirement of log4j version 2. The project
uses the required slf4j basic logging and this Log4j version 2 to support
logging to a file.

Update local repository with this SHA1 id.
In the projects base directory execute the following. Note: this project is not
listed in the parent pom file, therefore mvn (maven) commands in the parent
path will not read this pom file.

    1. mvn clean install
    2. cd target/distribution/
    3. sudo ./run.sh
    
Dependent upon the -f parameter value logging details will be stored in
/tmp/logs/com.pi4j.devices.tca9548.Tca9548.log

The run command will cd into the base classes directory and invoke the
application with parameters that reset the chip and then enables bus 2.
   You can review the SampleTca9548App usage() method for parameter values.
 The command assumes GPIO6 is wired to the TCA9548 reset pin, modify the
 parameter "-r 0x06" value to align with your environment.  In addition
 parameter -b 0x01 value assumes the chip is connected to raspberry I2C bus 1,
 and the chip is operating at default address -a 0x70.


Classes:
com.pi4j.devices.base_i2c.BasicI2cDevice  Simple layer between TCA9548 and the
 pi4j_v2 infratructure to add logging details to assist in debug.
com.pi4j.devices.base_i2c.I2cSimpleRead  Utility to read bytes one at a time.
Used to debug.

com.pi4j.devices.base_util.ffdc.FfdcLoggingSystem Interface to print system details.
com.pi4j.devices.base_util.ffdc.FfdcLoggingModule Interface to perform logging.
Allows implementing class to add details to any message.
com.pi4j.devices.base_util.ffdc.FfdcUtil   Implements the FfdcLoggingSystem and
FfdcLoggingModule interface

com.pi4j.devices.base_util.gpio.GpioBasics  Gpio utilties

com.pi4j.devices.base_util.PrintInfo  Static methods used by FfdcLoggingSystem
implemtentor.

com.pi4j.devices.tca9548.Tca9548ConfigData Maps bus number mask value. Although
 a simple 1 shifted left busNumberTimes could accomplish the same, this was
 implemented as a method to support any future chip datasheet.
com.pi4j.devices.tca9548.Tca9548 TCA9548 implementation
com.pi4j.devices.tca9548.SampleTca9548App  Demonstrate application use of the
Tca9548 class.



