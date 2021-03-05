Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23008
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2 

### This repository is a first device specific support project for the MCP23008 
### !! NOT READY FOR PRODUCTION USE !!

Project by Tom Aarts

==========================================================================

https://ww1.microchip.com/downloads/en/DeviceDoc/21919e.pdf
MCP23008/MCP23S08   8-Bit I/O Expander with Serial Interface

           
Java classes to access the MCP23008 GPIO controller as an application. 


Supported functions.
    1. reset chip
    2. Configure any/all pins (all configurations options)
    3. Supports logging facilities implemented by log4_v2
    
    Example does not use the MCp23008 interrupt capability. 
    To view/use code that confogures and uses the chip interrupt,
     see-- com/pi4j/devices/mcp23xxxApplication/Mcp23008PinMonitor 
    
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
    4. Execute command to reset Mcp23008
    5. Execute command to perform desired MCP23008 operation
    


ependent upon the -f parameter value logging details will be stored in
/tmp/logs/com.pi4j.devices.mcp23008.Mcp23008.log

  

 
 
 
 BCM gpio16 configured as output connected MCP23008 pin 4           pdip14
 
 BCM gpio13 configured as output connected MCP23008 (bar) RESET     pdip6
 
 BCM gpio27 configured as input connected MCP23008 INT              pdip8
 
 BCM gpio22 configured as output connected to LED
 
 
 Red LED (+) connected to pin0         pdip10
 Green LED (+) connected to pin1       pdip11
 Yellow LED (+) connected to pin2      pdip12
  
 
MCP23008 on Pi BCM 1  address 0x20
All address pins (A0 A1 A2) are strapped to ground for the chips default address 0x20

_______________________           
- Pi BCM    I2C bus 1 - ______________ 
_______________________               |
  |    |     |                        |
  |    |     |                        |
  |    |     |                        |  
  |    |     |                        |
  |    |     |                       ____________________
  |    |     |__________> RESET >   -   MCP23008   0x20 -
  |    |                             ____________________
  |    |________________ < INT  <        |    |    |   |
  |                                      |         LEDs 
  |_____________________> Drive GPIO >___|
 


NOTE !!!!!
Using the $@ in the module call alows variable number of arguments. But, agruments wrapped by " " cannot
contain spaces. So as you can see the -m and -z values wrapped by " " contain no spaces..





1. Set pin and chip configuration
property files
  sudo ./runAppPropertySet.sh
  sudo ./runAppPropertyRead.sh


2.  Reset MCP23008
  sudo ./runMcp23008.sh   -b 0x1 -a 0x20   -r 3          -z "{{'gpio27':{'name':'InterruptDetect','dir':'in','pull':'up'}},{'gpio13':{'name':'ResetChip','dir':'out','int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin3':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin4':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 13 -f 1
 

 
3.  Configure pins of MCP23008, do reset first
 
 All pins
   sudo ./runMcp23008.sh   -b 0x1 -a 0x20  -r 3       -z "{{'gpio27':{'name':'InterruptDetect','dir':'in','pull':'up'}},{'gpio13':{'name':'ResetChip','dir':'out','int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin3':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin4':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 13 -f 1


4. Drive MCP23008 pin0 Red Led hi low
  sudo ./runMcp23008.sh    -b 0x1 -a 0x20   -d 0 -o ON  -m   "{{'pin0':{'dir':'out','int_ena':'no'}}}"  -f 1
  sudo ./runMcp23008.sh    -b 0x1 -a 0x20   -d 0 -o OFF   -f 1


5. Read MCP23008 pin4
  Read 4
  sudo ./runMcp23008.sh    -b 0x1 -a 0x20   -r 4  -m "{{'pin4':{'dir':'in','pull':'down'}}}"     -f 1
 This will set pin4 high or low
python
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(16, GPIO.OUT) 
GPIO.output(16,GPIO.LOW)
GPIO.output( 16 , GPIO.HIGH)

