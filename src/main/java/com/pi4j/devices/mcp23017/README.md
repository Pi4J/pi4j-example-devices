Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23017
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2 

### This repository is a first device specific support project for the MCP23017 

### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts

==========================================================================

https://ww1.microchip.com/downloads/en/devicedoc/20001952c.pdf
MCP23017/MCP23S17
16-Bit I/O Expander with Serial Interface


           
Java classes to access the MCP23017 GPIO controller as an application. 


Supported functions.
    1. reset chip
    2. Configure any/all pins (all configurations options)
    3. Supports logging facilities implemented by log4_v2
    
    Example does not use the MCp23017 interrupt capability. 
    To view/use code that confogures and uses the chip interrupt,
     see-- com/pi4j/devices/mcp23xxxApplication/Mcp23017PinMonitor 
    
The classes are implemented using the Pi4j_V2 requirements of September 1 2020.

The project uses one addition pom requirement of log4j version 2. The project
uses the required slf4j basic logging and this Log4j version 2 to support
logging to a file.

Update local repository with this SHA1 id.
In the projects base directory execute the following. Note: this project is not
listed in the parent pom file, therefore mvn (maven) commands in the parent
path will not read this pom file.


Program options
 -q 9548#1    -r 3  -p pin0 -c 23008#1   -z 'pi GPIOs'  -m 'MCP23xxx pin configuration'
-q  first chip in the i2c circuit. In the example this is a TCA9548 mux, If the 23008 chip was the 
directly on the Pi bus, the the value would be -q 23008#1
-r read this MCP23xxx pin
-d drive this MCP23xxx pin based on -o  ON or OFF
-z Pi gpio configuration
-m MCP23xxx pin configuration

    1. mvn clean package
    2. cd target/distribution
    3. Execute command to set configuration data  (will not be used by example)
    4. Execute command to reset Mcp23017
    5. Execute command to perform desired MCP23017 operation
    
  


ependent upon the -f parameter value logging details will be stored in
/tmp/logs/com.pi4j.devices.mcp23017.Mcp23017.log

  

 
 BCM gpio12 configured as output connected MCP23017 pin 4            spdip25
 
 BCM gpio18 configured as output connected MCP23017 pin 15           spdip8

 BCM gpio5 configured as output connected MCP23017 (bar) RESET       spdip18
 
 BCM gpio23 configured as input connected MCP23017 INTA              spdip20
 
 BCM gpio21 configured as input connected MCP23017 INTB              spdip19

 BCM gpio22 configured as output connected to LED
 
 
 Red LED (+) connected to pin0         spdip21
 Green LED (+) connected to pin9       spdip2
 Yellow LED (+) connected to pin14     spdip7
  
 

MCP23008 on TCA9548 switch bus 1  address 0x22

All address pins (A0 A2) are strapped to ground, (A1) strapped to 3.3 V  for the chip address 0x22

_______________________           
- Pi BCM    I2C bus 1 -  _______________ 
_______________________                 |
  | |  ||    |                          |
  | |  ||    |                          |
  | |  ||    |                          |  
  | |  ||    |                          |
  | |  ||    |                      ____|________________
  | |  ||    |__________> RESET >   -   MCP23017   0x22 -
  | |  ||_______________< INTA  <   _____________________
  | |  |________________ < INTB <      | |    |    |   |
  | |___________________> Drive GPIO > | |        LEDs 
  |_____________________> Drive GPIO >___|
 

 
 NOTE !!!!!
 Using the $@ in the module call alows variable number of arguments. But, agruments wrapped by " " cannot
 contain spaces. So as you can see the -m and -z values wrapped by " " contain no spaces..


1. Set pin and chip configuration
property files
  sudo ./runAppPropertySet.sh
  sudo ./runAppPropertyRead.sh


2.  Reset MCP23017
 sudo ./runMcp23017.sh    -b 0x01 -a 0x22   -r 4   -z "{{'gpio23':{'name':'InterruptADetect','dir':'in','pull':'up'}},{'gpio21':{'name':'InterruptBDdetect','dir':'in','pull':'up'}},{'gpio5':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin15':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 5 -f 1
 
 

3. Drive pin0 hi low.  Drives Red LED.

 sudo ./runMcp23017.sh    -b 0x01 -a 0x22    -d 0 -o ON   -m   "{{'pin0':{'dir':'out','int_ena':'no'}}}"   -f 1
 sudo ./runMcp23017.sh   -b 0x01 -a 0x22    -d 0 -o OFF   -f 1

4. Drive pin14 hi low.   Drives Yellow LED.

 sudo ./runMcp23017.sh   -b 0x01 -a 0x22    -d 14 -o ON    -m   "{{'pin14':{'dir':'out','int_ena':'no'}}}"  -f 1
 sudo ./runMcp23017.sh   -b 0x01 -a 0x22    -d 14 -o OFF   -f 1

5. Read pin4
sudo ./runMcp23017.sh    -b 0x01 -a 0x22   -r 4      -z "{{'gpio23':{'name':'InterruptADdetect','dir':'in','pull':'up'}},{'gpio21':{'name':'InterruptBDetect','dir':'in','pull':'up'}},{'gpio5':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin15':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 5 -f 1
sudo ./runMcp23017.sh    -b 0x01 -a 0x22   -r 4  -f 1   

In separate terminal, alter pin4
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(12, GPIO.OUT) 
GPIO.output(12,GPIO.LOW)
GPIO.output( 12 , GPIO.HIGH)



6. Read pin 15
 sudo ./runMcp23017.sh   -b 0x01 -a 0x22   -r 15     -z "{{'gpio23':{'name':'InterruptADetect','dir':'in','pull':'up'}},{'gpio21':{'name':'InterruptBDetect','dir':'in','pull':'up'}},{'gpio5':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin15':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 5 -f 1
 sudo ./runMcp23017.sh   -b 0x01 -a 0x22   -r 15   -f 1


In separate terminal, alter pin15
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(18, GPIO.OUT) 
GPIO.output(18,GPIO.LOW)
GPIO.output( 18 , GPIO.HIGH)



