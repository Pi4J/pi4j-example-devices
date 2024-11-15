\



Pi4J :: Java I/O Library for Raspberry Pi :: Device :: SN74HC595 8 Bit shift register
==========================================================================

### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================


Specific usage of sn74hc595 to control 1602A LCD

                            9               8         gnd

gpio13                      10              7         QH Red LED   
gpio6                       11              6         QG  
gpio5                       12              5         QF  
gpio20                      13              4         QE  
gpio23                      14              3         QD   
QA                          15              2         QC Green LED
Vdd 5v                      16              1         QB

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runSN74HC595.sh parms........

https://www.ti.com/product/SN74HC595

To turn pin QC Green LED on
sudo ./runSN74HC595.sh -ds 23 -oe 20 -st 5 -mr 13 -sh 6 -rd 0x4

To turn pin QC and QH Green LED on
sudo ./runSN74HC595.sh -ds 23 -oe 20 -st 5 -mr 13 -sh 6 -rd 0x84

Set trace level to trace
sudo ./runSN74HC595.sh -ds 23 -oe 20 -st 5 -mr 13 -sh 6 -rd 0x2 -t trace


