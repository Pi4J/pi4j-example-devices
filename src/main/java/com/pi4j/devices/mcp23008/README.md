Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23008
==========================================================================

### This repository is a first device specific support project for the MCP23008

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts

==========================================================================

# This device example os being rewritten to simplify and use the associate Driver
MCP23008/MCP23S08 8-Bit I/O Expander with Serial I2C Interface

Java classes to access the MCP23008 GPIO controller as an application.


## Details using Mcp23008Arg.

This program uses fixed configuration, demonstrates steps to configure and use the MCP23008.  There
is explanation documentation in the source code.
Supported functions.

### Build
    1. ./mvnw clean package
    2. cd target/distribution 
    3. Execute command to run the example program

These example commands assume:
1. MCP23008 pin0 is connected to the + lead of an LED
2. MCP23008 pin1 connected to pin7
3. MCP23008 INT line is connected to GPIO27
4. MCP23008 RESET pin connected to GPIO13



- Pi BCM I2C bus 1 - ______________
  _______________________               |
  | | | |
  | | | |
  | | | |  
  | | | |
  | | |                       ____________________
  | | |__________> RESET >   - MCP23008 0x27 -
  | |                             ____________________
  | |________________ < INT  <        | | | |
  | | LEDs
  |_____________________> Drive GPIO >___|

The script will use the Mcp23008AppArg to access methods in the 'Driver' code. If you do not
POR the chip or execute the -do_reset command the configuration will be persisted in the MCP23008 IC.
However, when the program end, all details within the Pi4J code are lost.  The next time you run this
command you must include the parms used earlier to recreate their data within Pi4J. Example : -cI 7 arg 
must be passed if you intend to use that input pin during this invocation.


### Pertinent Arguments
## Note
``` If you wire the GPIOs as described using GPIO13 for reset and GPIO27 to  
monitor Interrupts, those arguments are not required. If you use different GPIOs 
then the arguments are required.
```

- Using bus 0x01 and device address 0x27 
  ./runMcp23008Arg.sh -b 0x1 -a 0x27
- Create GPIO13 Reset line
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -reset_gpio 13
- Reset MCP23008
-  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -do_reset  -reset_gpio 13
- Create pini as output
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -cO 1
- Create pin7 as input wi*th pullup resis*tor
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -cI 7 true
-  Create pin0 as output 
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -cO 0
- Drive pin0 high   (LED on)
   ./runMcp23008Arg.sh -b 0x1 -a 0x27 -dH 0
-  Drive pin1 high
   ./runMcp23008Arg.sh -b 0x1 -a 0x27 -dH 1
-  Read  pin7
   ./runMcp23008Arg.sh -b 0x1 -a 0x27 -r 7
-  Drive pin1 low
   ./runMcp23008Arg.sh -b 0x1 -a 0x27 -dL 1
-  Read  pin7
   ./runMcp23008Arg.sh -b 0x1 -a 0x27 -r 7
  Create GPIO27 Interrupt connection
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -intr_gpio 27
- Enable interrupts for pin7, any change
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -sIntr 7 ON_CHANGE
- Drive pin1 high  Force interrupt trigger
  ./runMcp23008Arg.sh -b 0x1 -a 0x27 -dH 1



## Persistence
To say it again, the MCP23008 chip persists state until reset of POR (PowerOnReset).   The Pi4J
does not persist all state across invocations so the GPIO assignments must be repeated in
subsequent usage.  

./runMcp23017Arg.sh -b 0x1 -a 0x27 -cI 7 true -cO 1 -dH 1 -r 7
./runMcp23017Arg.sh -b 0x1 -a 0x27 -cI 7 true -cO 1 -dL 1 -r 7

To fire the event monitor. 

./runMcp23008Arg.sh -b 0x1 -a 0x27  -do_reset -reset_gpio 13 -intr_gpio 27 -cI 7 true -sIntr 7 ON_CHANGE -cO 1 -dH 1 -r 7




## Details using Mcp23008App.

This program uses fixed configuration, demonstrates to configure and use the MCP23008.  There
is explanation documentation in the source code.
Supported functions.


    1. ./mvnw clean package
    2. cd target/distribution 
    3. Execute command to run the example program



BCM gpio13 configured as output connected MCP23008 (bar) RESET 

BCM gpio27 configured as input connected MCP23008 INT 


LED (+) connected to pin0 

MCP23008 on Pi i2c bus 1 address 0x27
All address pins (A0 A1 A2) are strapped to 3.3v for the chips address 0x27

MCP23008 pins are configured:
pin0 output  + LED
pin1 output  pin7
pin7 input   pullDown


Write pin0   illuminate LED


Write pin1       Read pin7







_______________________           

```
- Pi BCM I2C bus 1 - ______________
  _______________________               |
    | | |
    | | |
    | | |  
    | | |
    | |                       ____________________
    | |__________> RESET >   - MCP23008 0x27 -
    |                             ____________________
    |________________ < INT  <        | | | |
    

```

1. Execute the predefined pin and interrupt activity.  Console output and/or debugger usage
   will assist in explaining the chip.
   sudo ./runMcp23008.sh -b 0x1 -a 0x27 
 
 
 
 






# Testing 

   This will set pin4 high or low
   python3
   import RPi.GPIO as GPIO
   GPIO.setmode(GPIO.BCM)
   GPIO.setup(16, GPIO.OUT)
   GPIO.output(16,GPIO.LOW)
   GPIO.output( 16 , GPIO.HIGH)

pythonimport lgpio
import time

# 1. Open the gpiochip (usually 0)
h = lgpio.gpiochip_open(0)

LED_PIN = 17

# 2. YOU MUST CLAIM THE PIN FIRST (This allocates it)
lgpio.gpio_claim_output(h, LED_PIN)

try:
while True:
# 3. Now you can safely use it
lgpio.gpio_write(h, LED_PIN, 1)
time.sleep(1)
lgpio.gpio_write(h, LED_PIN, 0)
time.sleep(1)
finally:
# 4. Always release the chip when done
lgpio.gpiochip_close(h)

``````````````````````````````````````````````````````````````````````````````````