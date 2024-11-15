# Pi4J_V2-SSD1306 OLED

Implement SSD1306 OLED Display 128x64

https://www.digikey.com/htmldatasheets/production/2047793/0/0/1/ssd1306.html

The example program has the assumption the SSD1306 is connected to Pi bus 1, and
the device is factory configured to
operate as device address 0x3C. If these assumptions are not possible, the bus
and device address value can be passed as
parms.
The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2
documentation. Consult that logger
class documentation to understand its various logging options.

Example code uses Vertical Address Mode.  
No Page Address Mode verification has been performed.

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runSSD1306.sh

Use of different bus or device address, use the applicable values in the
following command.
sudo ./runSSD1306.sh -b 0x01 -a 0x3C

Will create the SSD1306 device

1. Write buffer of 0x00 to RAM, all pixels off
2. Write buffer of 0xFF to RAM. All pixels set
3. Write buffer half 0x00, half 0xff to RAM. Left half of display blank
4. Write image to RAM.
5. Alter column offset
6. Restore column offset
7. Alter display start line
8. Restore display start line
9. Alter column address range
10. restore column address range

No parameters are required. However, if 'any' parameter value is to be supplied:
parms: -b hex value bus -a hex value address -t trace  
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "
info"


