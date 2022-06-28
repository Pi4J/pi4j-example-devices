



Pi4J :: Java I/O Library for Raspberry Pi :: Device :: HD44780U LCD

Uses SN74HC595 to update D0-D7 of the LCD display module
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2

### This repository contains device specific support for various devices.

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts
==========================================================================
2X40 LCD.   1602A

Controlled by Pi GPIOs, data lines controlled by 8 bit shift register.

1602A
https://www.sparkfun.com/datasheets/LCD/ADM1602K-NSR-FBS-3.3v.pdf

Shift register
https://www.ti.com/product/SN74HC595



HD44780U details
Pi                      LCD1602
Grnd     Write always    rw
GPIO17                   rs
GPIO27                   en
NC  Not possible on Pi   bf
Grnd                    VSS
5VDC                    VDD
5VDC                    V0
5VDC                    A
Grnd                    K

SN74HC595               LCD1602
QA                      D0  
QB                      D1
QC                      D2
QD                      D3
QE                      D4
QF                      D5
QG                      D6
QH                      D7






SN74HC595 details
                            9               8         gnd

gpio13                      10              7         QH Red LED   
gpio6                       11              6         QG  
gpio5                       12              5         QF  
gpio20                      13              4         QE  
gpio23                      14              3         QD   
QA                          15              2         QC Green LED
Vdd 5v                      16              1         QB

1. mvn clean package
2. cd target/distribution
3. sudo ./runHD44780U_LCD1602A.sh parms........


sudo ./runHD44780U_LCD1602A.sh   -rs 17 -en 27   -ds 23 -oe 20 -st 5 -mr 13 -sh 6 -rd 0x84 -t trace -line1  aBc   -line2  XyZ  

Clears display, Line1 begins in fourth position, line2 begins in second position
sudo ./runHD44780U_LCD1602A.sh   -rs 17 -en 27  -clearLCD -ds 23 -oe 20 -st 5 -mr 13 -sh 6 -rd 0x84 -t trace -line1  aaaaaa -line1Offset 4 -line2  zzzzzz  -line2Offset 2


Param  -shiftL 2  shift both lines 2 positions to left
