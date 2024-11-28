=========================================================================

### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================

Specific usage of PCF8574A to control 1602A LCD

1602A
https://www.sparkfun.com/datasheets/LCD/ADM1602K-NSR-FBS-3.3v.pdf

PCF8574A I2C controlling a 1602A LCD

D7-D4 High bit nibble sent first, then lower nibble

i2c
https://www.robotshop.com/community/forum/t/drive-a-standard-hd44780-lcd-using-a-pcf8574-and-i2c/12876

PCF LCD
P0 RS
P1 RW
P2 E
P3 backlight
P4 D4
P5 D5
P6 D6
P7 D7

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runPCF8574A_LCD1602A.sh parms........

sudo ./runPCF8574A_LCD1602A.sh -b 0x01 -a 0x27 -t trace -line1 aBc -line2 XyZ

Clears display, Line1 begins in fourth position, line2 begins in second position
sudo ./runPCF8574A_LCD1602A.sh -b 0x01 -a 0x27 -clearLCD -t trace -line1 aaaaaa -line1Offset 4 -line2 zzzzzz
-line2Offset 2

Param -shiftL 2 shift both lines 2 positions to left
