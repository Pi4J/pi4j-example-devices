Pi4J :: Java I/O Library for Raspberry Pi :: Device :: Mcp23008
==========================================================================

### This repository is a first device specific support project for the MCP23017

### This is an example implementation and will need to be adjusted to fit your needs.

Project by Tom Aarts

==========================================================================

# This device example os being rewritten to simplify and use the associate Driver
MCP23017/MCP23S08 16-Bit I/O Expander with Serial I2C Interface

Java classes to access the MCP23017 GPIO controller as an application.


## Details using Mcp23017Arg.

This program uses a fixed configuration, demonstrates steps to configure and use the MCP23017. 

### Build
    1. ./mvnw clean package
    2. cd target/distribution 
    3. Execute command to run the example program

These example commands assume:
1. MCP23017 pin0 is connected to the + lead of an LED
2. MCP23017 pin1 connected to pin7
3. MCP23017 pin8 is connected to the + lead of an LED
4. MCP23017 pin9 connected to pin15 MCP23017 
5. MCP23017 INTA line is connected to GPIO5
6. MCP23017 INTB line is connected to GPIO6
7. 5MCP23017 RESET pin connected to GPIO19



- Pi BCM I2C bus 1 - ______________
  _______________________               |
  | | | |
  | | | |
  | | | |  
  | | | |
  | | |                       ____________________
  | | |__________> RESET >   - MCP23017 
  | |                         ____________________
  | |_________________ < INTA <        
  | 
  |___________________ < INTB < 

The script will use the Mcp23017AppArg to access methods in the 'Driver' code. If you do not
POR the chip or execute the -do_reset command the configuration will be persisted in the MCP23017 IC.
However, when the program end, all details within the Pi4J code are lost.  The next time you run this
command you must include the parms used earlier to recreate their data within Pi4J. Example : -cI 7 arg 
must be passed if you intend to use that input pin during this invocation.

### Pertinent Arguments
- Using bus 0x01 and device address 0x27 
  ./runMcp23017Arg.sh -b 0x1 -a 0x24
- Create pini as output
  ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cO 1
- Create pin7 as input wi*th pullup resis*tor
  ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 7 true
-  Create pin0 as output with pullup resistor
  ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cO 0
-  Create GPIO5 Interrupt connection
   ./runMcp23017Arg.sh -b 0x1 -a 0x24 -intrA_gpio 5 -intrB_gpio 6
-  Create GPIO19 Reset line
   ./runMcp23017Arg.sh -b 0x1 -a 0x24
-  Drive pin0 high
   ./runMcp23017Arg.sh -b 0x1 -a 0x24  -reset_gpio 19 -do_reset -cO 0 -dH 0
-  Drive pin1 high
   ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cO 1 -dH 1
-  Read  pin7
   ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 7 true -r 7
-  Drive pin1 low
   ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cO 1 -dL 1
-  Read  pin7
   ./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 7 true -r 7
- Enable interrupts for pin7, any change
  ./runMcp23017Arg.sh -b 0x1 -a 0x24 -sIntr 7 ON_CHANGE
- Drive pin1 high  Force interrupt trigger
  ./runMcp23017Arg.sh -b 0x1 -a 0x24  -cO 1  -dH 1
- Drive pin1 high  Reset MCP23017
  ./runMcp23017Arg.sh -b 0x1 -a 0x24 -do_reset  -reset_gpio 19


## Persistence
To say it again, the MCP23017 chip persists state until reset of POR (PowerOnReset).   The Pi4J
does not persist any state across invocations so the pin configuration must be included in
subsequent usage.  
To use pin1 to drive pin7 and read the state of pin7, the pin1 and pin7 configuration must be repeated.

### Pins in Bank A     0 - 7
./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 7 true -cO 1 -dH 1 -r 7
./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 7 true -cO 1 -dL 1 -r 7
 
To fire the event monitor. 

./runMcp23017Arg.sh -b 0x1 -a 0x24  -do_reset -reset_gpio 19 -intrA_gpio 5 -intrB_gpio 6 -cI 7 true -sIntr 7 ON_CHANGE -cO 1 -dH 1 -r 7


./runMcp23017Arg.sh -b 0x1 -a 0x24  -do_reset -reset_gpio 19 -intrA_gpio 5 -intrB_gpio 6 -cI 15 true -sIntr 15 ON_CHANGE -cO 9 -dH 9 -r 15



### Pins in Bank B     8 - 15
./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 15 true -cO 9 -dH 9 -r 15
./runMcp23017Arg.sh -b 0x1 -a 0x24 -cI 15 true -cO 9 -dL 9 -r 15


./runMcp23017Arg.sh -b 0x1 -a 0x24  -reset_gpio 19 -cO 8 -dH 8


