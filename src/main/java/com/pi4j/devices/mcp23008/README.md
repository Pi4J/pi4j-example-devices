Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23008
==========================================================================

### This repository is a first device specific support project for the MCP23008

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts

==========================================================================

# This device example os being rewritten to simplify and use the associate Driver
MCP23008/MCP23S08 8-Bit I/O Expander with Serial I2C Interface

Java classes to access the MCP23008 GPIO controller as an application.

Supported functions.


    1. ./mvnw clean package
    2. cd target/distribution 
    3. Execute command to run the example program


BCM gpio16 configured as output connected MCP23008 pin 4 

BCM gpio13 configured as output connected MCP23008 (bar) RESET 

BCM gpio27 configured as input connected MCP23008 INT 

BCM gpio18 configured as output connected to LED

LED (+) connected to pin0 

MCP23008 on Pi i2c bus 1 address 0x27
All address pins (A0 A1 A2) are strapped to 3.3v for the chips address 0x27

MCP23008 pins are configured:
pin0 output  + LED
pin1 output  pin7
pin3 input   gpio16
pin7 input   pullDown


Write pin0   illuminate LED


Write pin1       Read pin7


gpio16 output, drive  see results pin3






_______________________           

```
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
```

1. Drive MCP23008 pin0 Red Led hi low
   sudo ./runMcp23008.sh -b 0x1 -a 0x27 -d 0 -o ON -m   
   sudo ./runMcp23008.sh -b 0x1 -a 0x27 -d 0 -o OFF

2. Read MCP23008 pin4
   Read 4
   sudo ./runMcp23008.sh -b 0x1 -a 0x27 -r 4 
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