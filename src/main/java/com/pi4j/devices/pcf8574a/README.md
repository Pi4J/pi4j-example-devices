=========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2

### This repository contains device specific support for various devices.

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts
==========================================================================

PCF8574A I2C controlling a 1602A LCD
https://www.playembedded.org/blog/hd44780-backpack-stm32/



P0-P7 Pins on controller

P0      RS
P1      RW
P2      CS
P3      EN (CS)
P4      DB4
P5      DB5
P6      DB6
P7      DB7



Init  
file:///home/pi/Documents/HD44780.pdf table 12


LCD1602A doc

page12 4 bit init steps
D7-D4     High bits sent first, then lower



i2c
https://www.robotshop.com/community/forum/t/drive-a-standard-hd44780-lcd-using-a-pcf8574-and-i2c/12876



PCF                                                    LCD
P0                                                      D4
P1                                                      D5
P2                                                      D6
P3                                                      D7
P4                                                      RS
P5                                                      NC
P6                                                      NC
P7                                                      E



1. mvn clean package
2. cd target/distribution
3. sudo ./runPCF8574A.sh parms........


sudo ./runPCF8574A.sh   -b 0x01   -a 0x27  -t trace -line1  aBc   -line2  XyZ

Clears display, Line1 begins in fourth position, line2 begins in second position
sudo ./runPCF8574A.sh    -b 0x01   -a 0x27    -clearLCD  -t trace -line1  aaaaaa -line1Offset 4 -line2  zzzzzz  -line2Offset 2


Param  -shiftL 2  shift both lines 2 positions to left
