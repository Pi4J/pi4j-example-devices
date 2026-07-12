Pi4J :: Java I/O Library for Raspberry Pi :: Device :: MCP3008 10 bit A/D converter
==========================================================================

### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

https://datasheetspdf.com/pdf-file/439989/MicrochipTechnology/MCP3208/1


## Default assumption
   Chip connected to SPI0 CE0

                 5V  ---------  ch0             Vdd  ------------  5V
                3.3v ---------  ch1             Vref ------------  5V
                                ch2             Agnd ------------  Gnd
                                ch3             Clk  ------------  GPIO11 SCLK
                                ch4             Dout ------------  GPIO9  MISO
                                ch5             Din  ------------  GPIO10 MOSI
                                ch6             CS   ------------  GPIO8  CE0
                                ch7             Dgnd ------------  Gnd
 
 
### Build
1. /mvnw clean package
2. cd target/distribution
3. sudo ./runMcp3208.sh


### Usage

-vref option: if > 0, the will display calculated Channel input voltage.

To monitor all pins 0 Vref of 5.0 volts
sudo ./runMcp3208.sh -vref 5.0

To monitor only pin 0
sudo ./runMcp3208.sh -p 0x0
 
vref voltage of 3.3
sudo ./runMcp3208.sh -p 0x0  -vref 3.3

chip select 0
./runMcp3208.sh -p 0x0 -t trace -vref 3.3 -c 0x00

_Use SPI 1_
sudo ./runMcp3208.sh -p 0x0 -s 0x01

Use ChipSelect 1
sudo ./runMcp3208.sh -p 0x0 -c 0x01

sudo ./runMcp3208.sh -p 0x0 -c 0x00
INFO MCP3208 - Channel : 0 Bytes read : 3 Value : 1023

sudo ./runMcp3208.sh -p 0x01
INFO MCP3208 - Channel : 1 Bytes read : 3 Value : 647








## Complex wiring example used in unit testing


MCP4921
D = Digital value of DAC (0 – 4096)
G = Gain select (1x or 2x)

V-OUT = VREF * G  * D / 4096

    Pi                MCP4921    
    Pi 5V Vin           Pin1          
    Pi  CS_1            Pin2
    Pi  SCK             Pin3
    Pi  SDI             Pin4 
    Pi  GPIO24          Pin5
    Pi  5V vref         Pin6
    Pi  Grd             Pin7
                        Pin8    outA        CHN6 MCP3208 


MCP4725
Output voltage
Dn = Input Code (0 to 4095)
                    
V-OUT = VDD × Dn / 4096



    Pi                  MCP4725
    Pi 5V               Vin
    Pi Grnd             GND
    Pi Pin5             SCL
    Pi Pin3             SDA
    Pi Grnd             A0
                       Vout       CHN7 MCP32008   
