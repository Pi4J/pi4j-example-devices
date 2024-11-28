Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23xxPinMonitor
==========================================================================

### This repository is a first device specific support project for the MCP23008 and MCP23017

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts

==========================================================================
Simple program to execute and I2C read at the specified bus -b, address -a,
-o offset, -n number of bytes. Other parms, -f FFDC level, -s dump pi4j sys data

    1. ./mvnw clean package
    2. cd target/distribution
    3. sudo ./runSimpleRead.sh  -b 0x1 -a 0x22 -r register -n 0x1


