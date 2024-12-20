# Pi4J_V2-ADS1256

1. ./mvnw clean package
2. cd target/distribution
3. sudo ./runADS1256.sh

## Application Functions
This test application performs the following mutually-exclusive functions:
### Read a GPIO Pin (-rp)
Read a the binary state of the GPIO pin specified by the -p option.
### Set a GPIO Pin's Output State (-sp)
Write the binary state of the GPIO pin specified by the -p option. Allowed values are HIGH and LOW.
### Dump All Analog and GPIO Input States (-di)
Put all analog and digital I/O to input mode, and print a table of every analog single-ended voltage and the binary state of every GPIO. Repeat as quickly as possible.
### Modulate the GPIO Output States To A Chase Pattern (-chase)
Put all GPIO pins to output mode. Turn one GPIO on at a time, sequentially. The update interval is 2s. An integer interval, in milliseconds, may be specified after this option. A reflection of the current state will be printed to the screen.
### Perform a differential read of the specified analog input pair (default)
Use -pp to set positive analog pin and -pn to set negative analog pin. Legal values are AIN0...AIN7 and AINCOM. Both properties default to AINCOM.

## Options
### Dump Repeatedly (-dr)
When dumping all input states or performing a differential read, dump repeatedly. For differential reads, the dump rate is 1/sec or as fast as can be done, whichever is slower. When dumping input states, the dump rate is as fast as possible.
### Use Buffer (-buf)
Enables the AD125x analog input buffer.
### Use Voting (-vo)
Applies a 3-way error-correction voting algorithm to the inputs. For each read request, three are actually made and the best value is returned. Digital inputs are majority vote, analog inputs are median.
### Specify Control Pins and Busses (-rst -cs -drdy -pdwn -s)
 -rst Reset pin number (default 18)
 -cs Chip Select GPIO pin number (default 22)
 -drdy DRDY GPIO pin number (default 17)
 -pdwn PDWN GPIO pin number (default 27)
 -s SPI bus number, not pin (default 0)
 The default pin numbers should automatically work with the pin numbers on the Waveshare High Precision AD/DA Board.
### Specify Reference Voltage (-vref)
 Notify the app of the reference voltage being fed between the VREFP and VEREFN pins. Defaults to 2.5 as that is what the Waveshare board uses.
### Specify GPIO Pin On Which To Operate(-p)
Used in combination with -rp and -sp to set or read a pin.
### Reset Chip On Startup (-x)
### Specify ADC Data Rate (-sps)
Specify ADC data rate in samples per second written as a single value as shown in the datasheet. Confirmed stable values are: 2.5, 5, 10, 15, 25, 30, 50, 60. High rates result in shorter response times while lower rates result in higher precision.
## Notes
All pin numbers are GPIO pin numbers, not physical pin numbers. For example, the GPIO 17 pin which is the default pin to use
for DRDY is actually on physical pin 11. Consult a raspberry pi
GPIO pinout chart for details.
## Examples (Tested against Waveshare High Precision AD/DA board)
### Dump all analog and digital inputs (-di) at 2/sec, 10SPS (-sps 10), repeatedly (-dr), reset before starting (-x)
sudo ./runADS1256.sh -x -dr -di -sps 10
### Dump all analog and digital inputs (-di) at 2/sec, buffer on (-buf), 50SPS (-sps 50), repeatedly (-dr), use 3-way voting/median (-vo), and reset before starting (-x)
sudo ./runADS1256.sh -x -dr -di -sps 50 -buf -vo
### Chase the GPIOs at 2sec interval (-chase) and reset before starting (-x)
sudo ./runADS1256.sh -x -chase
### Chase the GPIOs as quickly as possible (-chase 0) and reset before starting (-x)
sudo ./runADS1256.sh -x -chase 0
### Chase the GPIOs at 250ms intervals (-chase 250) and reset before starting (-x)
sudo ./runADS1256.sh -x -chase 250
### Get a differential reading (-rp) of AIN0 and AIN1 and exit
sudo ./runADS1256.sh -pp AIN0 -pn AIN1
### Get a single-ended reading of AIN0 against AINCOM and exit
sudo ./runADS1256.sh -pp AIN0 -pn AINCOM
### Set GPIO 1 to input-mode, print the state of GPIO 1 and exit
sudo ./runADS1256.sh -p 1 -rp
### Set GPIO 0 to output-mode, turn the pin on.
sudo ./runADS1256.sh -p 0 -sp HIGH

## Test Configuration (new)
Waveshare High Precision AD/DA Board with all jumpers in their factory positions. Installed on a raspberry pi 3.
In this case, AIN0 is controlled by the onboard potentiometer and AIN1 is controlled by the photocell.
## Test Configuration (old)
ADS1256 module mounted. The chip I have has no vref input pin and no labeling
nor documentation. Used a VOM to map lines from the ADS1256 to external pins  
It appears the DVdd 5V is used as the V-reference.

--Connections
--Chip connected to SPI0   
-Pi----------------Module pin number----------------------Pi   
--3.3V-----------AIN0 ch0----------P1 Syn/PwrD--------------3.3V tie high   
--5v------------ AIN1 ch1----------P2 CS-----------------GPIO22 Chip select  
-----------------AIN2 ch2----------P3 DRDY----------------PIO17  
-----------------AIN3 ch3----------P4 Dout---------------GPIO9 MISO  
-----------------AIN4 ch4----------P5 Din----------------GPIO10 MOSI  
-----------------AIN5 ch5----------P6 Sclk----------------GPIO11 SCLK  
-----------------AIN6 ch6----------P7 Agnd----------------Gnd  
-----------------AIN7 ch7----------P8 DVdd----------------5V  
-----------------D0             
-----------------D1             
-----------------D2             
-----------------D3

The Syn/PwrD is tied high. The program does not use the Sync pin to initiate
a conversion, rather the issue the SYNC and WAKEUP commands.
This module does not expose the RESET line.

Also note, the ChipSelect 'CS' uses GPIO22, not the Pi normal CS0 or CS1
associated with SPI0. Required as operations span more than a single SPI
function and the chip must remain selected.

-cs and -drdy required for this chip configuration.  
-rst and -pdwn should not be used

sudo ./runADS1256.sh -pp AIN0 -pn AIN1 -cs 22 -drdy 17 -t trace -x

No reset pin and no powerDown pin omitt -rst 18 -pdwn 27

sudo ./runADS1256.sh -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -t trace -x -vref 5.0
TRACE com.pi4j.devices.ads1256.ADS1256 Channel  :1/8 value  :8388607
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 5.0

sudo ./runADS1256.sh -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -t trace -x -vref 5.0

TRACE com.pi4j.devices.ads1256.ADS1256 Channel  :0/8 value  :5441076
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 3.243134408370782

sudo ./runADS1256.sh -pp AIN1 -pn AIN0 -cs 22 -drdy 17 -t trace -x -vref 5.0
TRACE com.pi4j.devices.ads1256.ADS1256 - Channel  :1/0 value  :3031976
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 1.8071987399099756

-vref option: if > 0, feature will display calculated Channel input voltage.

ADS1256 module mounted within Waveshare AD/DA board
Chip connected to SPI0      
Pi---------------AD/DA---BOARD---------------------------Pi   
-10K-pot-5V------ch0----------P13----Syn/PwrD------------GPIO 27   
-LDR-------------ch1----------P15----CS------------------GPIO22 Chip select  
-----------------ch2----------P11----DRDY----------------GPIO17  
-----------------ch3----------P21----Dout----------------GPIO9 MISO   
-----------------ch4----------P19----Din-----------------GPIO10 MOSI  
-----------------ch5----------P23----Sclk----------------GPIO11 SCLK  
-----------------ch6----------P6-----Agnd----------------Gnd  
-----------------ch7----------P2-----DVdd----------------5V See Note_1
-GPIO26----------D0-----------P12----RESET---------------GPIO18  
-GPIO19----------D1             
-GPIO13----------D2             
-GPIO6-----------D3

The Syn/PwrD is configured as HIGH under all conditions. The program does not
use the Sync pin to initiate a conversion, rather to issue the
SYNC and WAKEUP commands.

Also note, the ChipSelect 'CS' uses GPIO22, not the Pi normal CS0 or CS1
associated with SPI0. Required as operations span more than a single SPI
function and the chip must remain selected.

-cs -drdy -rst and -pdwn required for this chip configuration.

sudo ./runADS1256.sh -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -rst 18 -pdwn 27 -t trace -x -vref 5.0

-vref option: if > 0, feature will display calculated Channel input voltage.

!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
NOTE_1 !!!!!!   ADS1256 must operate with Vcc 3.3v. Needed to be compatible with Pi logic voltage
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!!! WARNING:  WHen the AD/DA bard is strapped VCC-3.3, the Pi4 may not power on. Seems the drag
on the 3.3v prevents the Pi boot.

DRDY low ->> data conversion complete, RDATA 24 bits.


> > > > Single shot
> > > > WREG set channel
> > > > STANDBY cmd
> > > > WAKEUP
> > > > delay 33 microSeconds
> > > > DRDY low
> > > > RDATA
> > > > STANDBY
