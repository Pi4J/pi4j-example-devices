### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

                   MCP4921 

                3.3V --------   p1 Vdd         p8 Vout   -> mcp3008 ch5
                GPIO8  CE0      p2 CS          p7 AV      grnd
                GPIO11 SCLK     p3 SCK         p6 Vrefs   3.3V
                GPIO10 MOSI     p4 SDI         p5 LDAC    grnd

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4921.sh parms........


https://www.futurlec.com/SFMicrochip/MCP4921.shtml







-vref reference voltage
-c HEX value chip select
-s HEX value SPI bus #  
-tb twelveBit value to write to chip
-vout float Vout Used in conjunction with vref to calculate twelveBit value
-shdn boolean true active
-b boolean true buffered operation
-ga2x boolean true gain 2x

MCP4922 only
-AB  A or B     Use VoutA or VoutB




Validate Vout was correctly set
./runMcp3008.sh -vref 3.3 -p 0x0(PiN)




Updates using raw binary value
To Update DAC and with twelveBit value

Vout 3.3 V
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -tb 4095


Updates using voltage value     1.3 V
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -vout 1.3


Disable bit set    Expect no Vout
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn false 


Buffered bit set   Check twelveBits written to the chip
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -vout 3.3   -b true

Gain 2x bit set  Check twelveBits written to the chip   outv = 2.0
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -vout 2.0 -ga2x true


Gain 2x bit set  Check twelveBits written to the chip   outv = 2.4
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -tb 1500 -ga2x true

Gain 2x bit clear  Check twelveBits written to the chip   outv = 1.2
sudo ./runMcp4921.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -tb 1500 -ga2x false



