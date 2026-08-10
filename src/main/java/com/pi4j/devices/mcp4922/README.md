### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================



                    MCP4922

                3.3V  -------   p1 Vdd          p14 VoutA   -> mcp3008 ch4
                                p2 NC           p13 VrefA       3.3V
                GPIO7  CE1      p3 CS           p12 AV          grnd
                GPIO11 SCLK     p4 SCK          p11 VrefB       3.3V
                GPIO10 MOSI     p5 SDI          p10 VoutB   ->  mcp3008 ch 3
                                p6 NC           p9  SHDN
                                p7 NC           P8  LDAC


1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4922.sh parms........

https://ww1.microchip.com/downloads/en/DeviceDoc/22250A.pdf






-vref reference voltage
-c HEX value chip select
-s HEX value SPI bus #  
-tb twelveBit value to write to chip
-vout float Vout Used in conjunction with vref to calculate twelveBit value
-shdn boolean false enabled
-b boolean buffered operation
-ga boolean true gain 2x
-AB  A or B     Use VoutA or VoutB




Validate Vout was correctly set
./runMcp3008.sh -vref 3.3 -p 0x0PiN$




Updates using raw binary value
To Update DAC and with twelveBit value

Vout 3.3 V
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vref 3.3 -tb 4095


Updates using voltage value     3.3 V  using VoutA
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vref 3.3 -vout 3.3 -AB A

Updates using voltage value     3.3 V  using VoutB
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vref 3.3 -vout 3.3 -AB B

Disable bit set    Expect no VoutA
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -vout 0.0  -AB A

Disable bit set    Expect no VoutB
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vref 3.3 -vout 0.0   -AB B


Multiplier bit set   Check twelveBits written to the chip
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vref 3.3 -vout 3.3

Buffered bit set   Check twelveBits written to the chip
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vref 3.3 -vout 3.3   -b true

Gain 2x bit set  Check twelveBits written to the chip
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vref 3.3 -vout 3.3 -ga true






