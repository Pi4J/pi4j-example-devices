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
                                p6 NC           p9  SHDN     -> 3.3v
                                p7 NC           P8  LDAC     -> grmd


1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runMcp4922.sh parms........

https://ww1.microchip.com/downloads/en/DeviceDoc/22250A.pdf






-vrefA reference voltage
-vrefB reference voltage
-c HEX value chip select
-s HEX value SPI bus #  
-tb twelveBit value to write to chip
-voutA float Vout Used in conjunction with vrefA to calculate twelveBit value    Note 1.
-voutB float Vout Used in conjunction with vrefB to calculate twelveBit value    Note 1.
-shdn boolean true active
-b boolean buffered operation
-ga2x boolean true gain 2x
-AB  A or B     Use VoutA or VoutB   Note 1.



Note 1.  When -voutA or voutB supplied the -AB value will be set by this program.




Validate Vout was correctly set
./runMcp3008.sh -vref 3.3 -p 0x0(PiN)




Updates using raw binary value
To Update DAC and with twelveBit value

VoutB 2.0 V
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefB 3.3 -voutB 2.0  -AB B

Updates using digital  value     4095  (MAX)   
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefB 3.3 -tb 4095 -AB B

Updates using digital  value     2046
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefB 3.3 -tb 2046 -AB B


Updates using voltage value     2.0 V  using VoutA
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefA 3.3 -voutA 2.0 -AB A

Updates using voltage value     -tb 2046 V  using VoutA
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefA 3.3 -tb 2046 -AB A

Updates using voltage value     3.3 V  using VoutB
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefB 3.3 -voutB 3.3 -AB B

Disable bit set    Expect no VoutA
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vrefA 3.3 -voutA 2.0  -AB A

Disable bit set    Expect no VoutB
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn false -vrefB 3.3 -voutB 2.0   -AB B

Buffered bit set   Check twelveBits written to the chip
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefA 3.3 -voutA 3.3   -AB A  -b true

Gain 2x bit set  Check twelveBits written to the chip  vout 2.0
sudo ./runMcp4922.sh -s 0x00 -c 0x01 -shdn true -vrefA 3.3 -voutA 1.0 -ga2x true -AB A


                                                                                                    



