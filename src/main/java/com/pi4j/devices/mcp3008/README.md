Pi4J :: Java I/O Library for Raspberry Pi :: Device :: MCP3008 10 bit A/D converter
==========================================================================

### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

https://datasheetspdf.com/pdf-file/439989/MicrochipTechnology/MCP3008/1

Default assumption
Chip connected to SPI0 CE0

                 5V  ---------  ch0             Vdd  ------------  5V
                3.3v ---------  ch1             Vref ------------  5V
                                ch2             Agnd ------------  Gnd
                                ch3             Clk  ------------  GPIO11 SCLK
                                ch4             Dout ------------  GPIO9  MISO
                                ch5             Din  ------------  GPIO10 MOSI
                                ch6             CS   ------------  GPIO8  CE0
                                ch7             Dgnd ------------  Gnd

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp3008.sh

-vref option: if > 0, the will display calculated Channel input voltage.

To monitor all pins 0 Vref of 5.0 volts
sudo ./runMcp3008.sh -vref 5.0

To monitor only pin 0
sudo ./runMcp3008.sh -p 0x0

Change the logging detail to 'trace' vref voltage of 3.3
sudo ./runMcp3008.sh -p 0x0 -t trace -vref 3.3

Use SPI 1
sudo ./runMcp3008.sh -p 0x0 -s 0x01

Use ChipSelect 1
sudo ./runMcp3008.sh -p 0x0 -c 0x01

sudo ./runMcp3008.sh -p 0x0 -c 0x00
INFO com.pi4j.devices.mcp3008.MCP3008 - Channel : 0 Bytes read : 3 Value : 1023

sudo ./runMcp3008.sh -p 0x01
INFO com.pi4j.devices.mcp3008.MCP3008 - Channel : 1 Bytes read : 3 Value : 647
