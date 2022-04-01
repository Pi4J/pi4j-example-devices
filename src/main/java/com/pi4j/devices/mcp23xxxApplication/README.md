Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23xxPinMonitor
==========================================================================

## DISCLAIMER :: EXPERIMENTAL VERSION 2 

### This repository is a first device specific support project for the MCP23008 and MCP23017

### !! NOT READY FOR PRODUCTION USE !!

Project by  Tom Aarts

==========================================================================

https://ww1.microchip.com/downloads/en/DeviceDoc/21919e.pdf
MCP23008/MCP23S08   8-Bit I/O Expander with Serial Interface

See com.pi4j.devices.tca9548.Tca9548 for details on the connected I2C switch.

           
Java classes to access the MCP23008 GPIO controller as an application. The MCP23008
is located downstrean of a TCA9548 I2C switch. The Tca9548 I2C switch is automatically 
configured allowing access to the MCP23008.

The sample application configures MCP23008 pin to drive LEDs, and one pin as an input
with interrupts enabled.  A Pi BCM GPIO is used to drive this input pin.  The application 
monitors the Pi BCM GPIO connected to the MCP23008 INY pin. When the MCP23008 interrupts, 
the apllication determines if the pin causing the interrupt is the one monitored, and if so
performs the intended action.  The example interrupt event handler drives LEDs also
connected to the same MCP23008.

Supported functions.
    1. reset chip
    2. Configure any/all pins (all configurations options)
    3. Monitor any specific pin for input interrupts. These interrupts 
       will call the event handler on the event class associated with the pin
    4. Supports logging facilities implemented by log4_v2
    
The classes are implemented using the Pi4j_V2 requirements of September 1 2020.

The project uses one addition pom requirement of log4j version 2. The project
uses the required slf4j basic logging and this Log4j version 2 to support
logging to a file.

Update local repository with this SHA1 id.
In the projects base directory execute the following. Note: this project is not
listed in the parent pom file, therefore mvn (maven) commands in the parent
path will not read this pom file.

Program options
 -q 9548#1    -r 3  -p pin0 -c 23008#1  -g 27 -i on -z 'pi GPIOs'  -m 'MCP23xxx pin configuration'
-q  first chip in the i2c circuit. In the example this is a TCA9548 mux, If the 23008 chip was the 
directly on the Pi bus, the the value would be -q 23008#1
-r read this MCP23xxx pin
-d drive this MCP23xxx pin based on -o  ON or OFF
-g Pi gpio connected to MCP23xxx interrupt line
-i  interrupts monitor is ON or OFF
-z Pi gpio configuration
-m MCP23xxx pin configuration





    1. mvn clean package
    2. cd target/distribution
    3. Execute command to set configuration data
    4. Execute command to reset TCA9548 I2C switch
    5. Execute command to perform desired MCP23008 operation
    6. Use the following python code to drive signals to the MCP23008 pin confirgured
       as input with interrups enabled.
    


Note: If you prefer less or more logging detail, edit run23008App.sh changing the -f parameter VALUE
See com.pi4j.devices.base_util.ffdc.FfdcUtil.java
   
Dependent upon the -f parameter value logging details will be stored in
/tmp/logs/com.pi4j.devices.mcp23xxxApplication.Mcp23008PinMonitor.log

  
java program to ensure the application configuration was completed.
Second, the java MCP23008 apllication is invoked, this will remain active monitoring
interrupts from the MCP23008. 
    The application configures a DigitalStateChangeListener for 
each MCP23008 pin. 
    The listener for pin4 will control LEDs also connected to the same
MCP23008 and LEDs connected to the BCM gpio.  See com.pi4j.devices.appConfig.AppConfigUtilities.java and 
com.pi4j.devices.mcp23xxxApplication.PinInterruptLED.java

TCA9548 I2C switch, on PI bus 1, address 0x70

MCP23008 on TCA9548 switch bus 0  address 0x20

_______________________           _______________________
- Pi BCM    I2C bus 1 -  -------> -  TCA9548            - 
_______________________           -   0x70              -
  |    |     |                    -  Bus0  ..... Bus7   -
  |    |     |                    _______________________
  |    |     |                        |  
  |    |     |                        |
  |    |     |                       ____________________
  |    |     |__________> RESET >   -   MCP23008   0x20 -
  |    |                             ____________________
  |    |________________ < INT  <        |    |    |   |
  |                                      |         LEDs 
  |_____________________> Drive GPIO >___|
 



 BCM gpio16 configured as output connected MCP23008 pin 4           pdip14
 
 BCM gpio13 configured as output connected MCP23008 (bar) RESET     pdip6
 
 BCM gpio27 configured as input connected MCP23008 INT              pdip8
 
 BCM gpio22 configured as output connected to LED
 
 
 Red LED (+) connected to pin0         pdip10
 Green LED (+) connected to pin1       pdip11
 Yellow LED (+) connected to pin2      pdip12
  
 
 




NOTE !!!!!
Using the $@ in the module call alows variable number of arguments. But, agruments wrapped by " " cannot
contain spaces. So as you can see the -m and -z values wrapped by " " contain no spaces..

sudo ./runAppPropertySet.sh 

1. Set pin and chip configuration
property files
  sudo ./runAppPropertySet.sh
  sudo ./runAppPropertyRead.sh


2. Reset TCA9548 mux
   sudo ./runTca9548.sh  -b 0x01 -a 0x70  -f 1   -l    -r 0x6
 
3.  Reset MCP23008
  sudo ./runMcp23008PinMonitor.sh  -q 9548#1   -r 3  -p pin0 -c 23008#1        -z "{{'gpio27':{'name':'InterruptDetect','dir':'in','pull':up'}},{'gpio13':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin3':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin4':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 13 -f 0
 

 
4.  Configure pins of MCP23008
 
 All pins
  sudo ./runMcp23008PinMonitor.sh   -x 13 -q 9548#1  -g 27   -r 3  -p pin0 -c 23008#1      -f 1  -z "{{'gpio27':{'name':'InterruptDetect','dir':'in','pull':up'}},{'gpio13':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin3':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin4':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"


5. Drive MCP23008 pin0 hi low  Red LED on/off
 sudo ./runMcp23008PinMonitor.sh    -q 9548#1    -d 0 -o ON -p pin0 -c 23008#1  -m   "{{'pin0':{'dir':'out','int_ena':'no'}}}"   -f 1
 sudo ./runMcp23008PinMonitor.sh    -q 9548#1    -d 0 -o OFF -p pin0 -c 23008#1   -f 1



6. Read MCP23008 pin4
  Read 4
  sudo ./runMcp23008PinMonitor.sh    -q 9548#1    -r 4  -p pin0 -c 23008#1   -m "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'down','default':'0','do_compare':'no','int_ena':'yes','act':'low'}}}"   -x 13  -f 2
 This will set pin4 high or low
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(16, GPIO.OUT) 
GPIO.output(16,GPIO.LOW)
GPIO.output( 16 , GPIO.HIGH)

 
7.   Monitor pin 4   (does chip reset)
    sudo ./runMcp23008PinMonitor.sh    -q 9548#1   -r 4  -p pin0 -c 23008#1  -g 27 -i on  -z "{{'gpio27':{'name':'InterruptDetect','dir':'in','pull':up'}},{'gpio22':{'name':'LED-gpio','dir':'out,'int_ena':'no','initial':low'}}}"  -m "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'down','default':'0','do_compare':'no','int_ena':'yes','act':'low'}}}" -x 13  -f 2
    
      In another terminal
    i2cdump -y 1 0x20     This will clear existing interrupts in the chip
 
8. Trip interrupt  GPIO16->p4   (in separate terminal console)
pin0 Red LED will reflect state of pin4 when it interrupts
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(16, GPIO.OUT) 
GPIO.output(16,GPIO.LOW)
GPIO.output( 16 , GPIO.HIGH)
GPIO.output(16,GPIO.LOW)

// pin0 Red LED will reflect state of pin4 when it interrupts
  + "{'pin4':{'appName':'input','action':'reflect','chipName':'23008#1','pin':'pin0','pinChip':'23008#1'}} } },"
 
 // pin0 drivenHi then drivenLow when pin4 interrupts
  + "{'pin4':{'appName':'input','action':'hilow','chipName':'23008#1','pin':'pin0','pinChip':'23008#1'}} } },"
 ////////////////////////////////////////////////////////////
 // gpio22 drivenHi then drivenLow when pin4 interrupts
 + "{'pin4':{'appName':'input','action':'hilow','chipName':'23008#1','gpioNumLED':'dio22'}} } },"
   
 // gpio22 reflects pin4 when it interrupts
 + "{'pin4':{'appName':'input','action':'reflect','chipName':'23008#1','gpioNumLED':'dio22'}} } },"



!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
The current usage of PIGPIO prevents the following test. A single application can access the pigpio libraries at one time
Monitor pin 3
  sudo ./runMcp23008PinMonitor.sh    -q 9548#1    -r 3  -p pin0 -c 23008#1  -g 27 -i on  -z "{{'gpio27':{'name':'InterruptDetect','dir':'in','pull':up'}}}"  -m "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin1':{'dir':'out','int_ena':'no'}},{'pin2':{'dir':'out','int_ena':'no'}},{'pin3':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}}}"
 
 Trip interrupt p7->p3
  sudo ./runMcp23008PinMonitor.sh    -q 9548#1    -d 7 -o ON -p pin0 -c 23008#1  -m   "{{'pin7':{'dir':'out','int_ena':'no'}}}"
  sudo ./runMcp23008PinMonitor.sh    -q 9548#1    -d 7 -o OFF -p pin0 -c 23008#1  
  
  


////////////
MCP23017


 BCM gpio12 configured as output connected MCP23017 pin 4            spdip25
 
 BCM gpio18 configured as output connected MCP23017 pin 15           spdip8

 BCM gpio5 configured as output connected MCP23017 (bar) RESET       spdip18
 
 BCM gpio23 configured as input connected MCP23017 INTA              spdip20
 
 BCM gpio21 configured as input connected MCP23017 INTB              spdip19

 BCM gpio22 configured as output connected to LED
 
 
 Red LED (+) connected to pin0         spdip21
 Green LED (+) connected to pin9       spdip2
 Yellow LED (+) connected to pin14     spdip7
  
 
TCA9548 I2C switch, on PI bus 1, address 0x70

MCP23008 on TCA9548 switch bus 1  address 0x22

_______________________           _______________________
- Pi BCM    I2C bus 1 -  -------> -  TCA9548            - 
_______________________           -   0x70              -
  | |  ||    |                    -  . Bus1  ..... Bus7   -
  | |  ||    |                    _______________________
  | |  ||    |                          |  
  | |  ||    |                          |
  | |  ||    |                       ____________________
  | |  ||    |__________> RESET >   -   MCP23017   0x22 -
  | |  ||_______________< INTA  <   ____________________
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
   
   
2. Reset TCA9548 mux
      sudo ./runTca9548.sh -b 0x01 -a 0x70  -f 1   -l    -r 0x6
 
3.  Reset MCP23017
  sudo ./runMcp23017PinMonitor.sh    -q 9548#1   -r 4  -p pin0 -c 23017#1        -z "{{'gpio23':{'name':'InterruptADetect','dir':'in','pull':'up'}},{'gpio21':{'name':'InterruptBDetect','dir':'in','pull':'up'}},{'gpio5':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin15':{'dir':'in','pull':'up','default':'1','do_compare':'yes','int_ena':'yes','act':'low'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 5 -f 0
 
   
 Configure pins of MCP23017
 

 Listen A side
   sudo ./runMcp23017PinMonitor.sh   -q 9548#1  -g 23  -i on -r 4  -p pin0 -c 23017#1         -z "{{'gpio23':{'name':'InterruptADetect','dir':'in','pull':'up'}},{'gpio21':{'name':'InterruptBDetect','dir':'in','pull':'up'}},{'gpio5':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin15':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 5 -f 0
     In another terminal
 i2cdump -y 1 0x22     This will clear existing interrupts in the chip
 
 Yellow LED will flash on/off once each interrupt
Trip interrupt  GPIO12->p4
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(12, GPIO.OUT) 
GPIO.output(12,GPIO.LOW)
GPIO.output( 12 , GPIO.HIGH)
GPIO.output(12,GPIO.LOW)  
   
   
 Listen B side
   sudo ./runMcp23017PinMonitor.sh  -q 9548#1  -g 21 -i on  -r 15  -p pin0 -c 23017#1         -z "{{'gpio23':{'name':'InterruptADetect','dir':'in','pull':'up'}},{'gpio21':{'name':'InterruptBDetect','dir':'in','pull':'up'}},{'gpio5':{'name':'ResetChip','dir':'out,'int_ena':'no','initial':'high'}}}"    -m   "{{'pin0':{'dir':'out','int_ena':'no'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin4':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin15':{'dir':'in','pull':'up','default':'0','do_compare':'no','int_ena':'yes','act':'low'}},{'pin14':{'dir':'out','int_ena':'no'}},{'pin5':{'dir':'out','int_ena':'no'}},{'pin6':{'dir':'out','int_ena':'no'}},{'pin7':{'dir':'out','int_ena':'no'}}}"  -x 5 -f 0
     In another terminal
  i2cdump -y 1 0x22     This will clear existing interruupts in the chip
 
 Trip interrupt  GPIO18->p15     Red LED reflects level of pin 15
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(18, GPIO.OUT) 
GPIO.output(18,GPIO.LOW)
GPIO.output( 18 , GPIO.HIGH)
 GPIO.output(18,GPIO.LOW)
 
Drive pin0 hi low     Red LED

  sudo ./runMcp23017PinMonitor.sh   -q 9548#1    -d 0 -o ON -p pin0 -c 23017#1  -m   "{{'pin0':{'dir':'out','int_ena':'no'}}}"  
  sudo ./runMcp23017PinMonitor.sh   -q 9548#1    -d 0 -o OFF -p pin0 -c 23017#1  

Drive pin14 hi low     Yellow LED

 sudo ./runMcp23017PinMonitor.sh   -q 9548#1    -d 14 -o ON -p pin0 -c 23017#1  -m   "{{'pin14':{'dir':'out','int_ena':'no'}}}"
 sudo ./runMcp23017PinMonitor.sh   -q 9548#1    -d 14 -o OFF -p pin0 -c 23017#1  










 NOTES
~/Pi4j_v2/Pi4J_V2_devices/src/main/python/monitor_py
sudo pigpiod
sudo killall pigpiod

sudo ./monitor.py 27
