
Pi4J :: Java I/O Library for Raspberry Pi :: Device :: DAC8552
==========================================================================

### This repository contains device specific support for various devices.

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts
==========================================================================


Pi------------------DAC---------------------Pi   
5V---------------P1-------P8----------------GND   
5V---------------P2-------P7----------------MOSI   
LED-B------------P3-------P6----------------SCLK    
LED-A------------P4-------P5----------------GPIO22    

GPIO22 could be any Pi GPIO of your chosing, use its numeric value with -cs


sudo ./runDAC8552.sh -s 0x0 -cs 23 -t trace -vout 2.1 -vref 5.0 -chn channel_A  

Channel A set to approximately 2.1 V   


sudo ./runDAC8552.sh -s 0x0 -cs 23 -t trace -vout 5.0 -vref 5.0 -chn channel_B  

Channel B set to approximately 5 V  

sudo ./runDAC8552.sh  -cs 23 -vout 3.0  -chn channel_B   

Will operate with the default trace_level 'info', SPI0 and -vref 3.3    


