
```
1. mvn clean package
2. cd target/distribution
3. sudo ./runBasicADS1256.sh
```

Note_0  Within the code of this application,  The use of -rp or -sp and
all AD/DA parms will be ignored


Tested

ADS1256 module mounted.  The chip I have has no vref input pin and no labeling
nor documentation. Used a VOM to map lines from the ADS1256 to external pins  
It appears the DVdd 5V is used as the V-reference.


At present time the code does not support access to the chips four GPIOs.

```
--Connections
--Chip connected to SPI0   
-Pi----------------Module pin number----------------------Pi   
--3.3V-----------AIN0 ch0----------P1 Syn/PwrD--------------3.3V  tie high   
--5v------------ AIN1 ch1----------P2 CS-----------------GPIO22  Chip select  
-----------------AIN2 ch2----------P3 DRDY----------------PIO17  
-----------------AIN3 ch3----------P4 Dout---------------GPIO9  MISO  
-----------------AIN4 ch4----------P5 Din----------------GPIO10 MOSI  
-----------------AIN5 ch5----------P6 Sclk----------------GPIO11 SCLK  
-----------------AIN6 ch6----------P7 Agnd----------------Gnd  
-----------------AIN7 ch7----------P8 DVdd----------------5V  
-----------------D0             
-----------------D1             
-----------------D2             
-----------------D3
```

The Syn/PwrD is tied high. The program does not use the Sync pin to initiate
a conversion, rather the issue the SYNC and WAKEUP commands.
This module does not expose the RESET line.

Also note, the ChipSelect 'CS' uses GPIO22, not the Pi normal CS0 or CS1
associated with SPI0. Required as operations span more than a single SPI
function and the chip must remain selected.

-cs and -drdy required for this chip configuration.  

-rst and -pdwn should not be used

```
sudo ./runBasicADS1256.sh   -pp AIN0 -pn AIN1  -cs 22  -drdy 17  -t trace -x
```

No reset pin and no powerDown pin omitt  -rst 18  -pdwn 27

```
sudo ./runBasicADS1256.sh -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -t trace -x -vref 5.0
TRACE com.pi4j.devices.ads1256.ADS1256  Channel  :1/8  value  :8388607
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 5.0


sudo ./runBasicADS1256.sh -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -t trace -x -vref 5.0

TRACE com.pi4j.devices.ads1256.ADS1256  Channel  :0/8  value  :5441076
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 3.243134408370782


sudo ./runBasicADS1256.sh -pp AIN1 -pn AIN0 -cs 22 -drdy 17 -t trace -x -vref 5.0
TRACE com.pi4j.devices.ads1256.ADS1256 - Channel  :1/0  value  :3031976
INFO com.pi4j.devices.ads1256.ADS1256 - A/D read input voltage : 1.8071987399099756
```



-vref option: if > 0,  feature will display calculated Channel input voltage.

ADS1256 module mounted within Waveshare  AD/DA board

Chip connected to SPI0      
```
Pi---------------AD/DA---BOARD---------------------------Pi   
-10K-pot-5V------ch0----------P13----Syn/PwrD------------GPIO 27   
-LDR-------------ch1----------P15----CS------------------GPIO22  Chip select  
-----------------ch2----------P11----DRDY----------------GPIO17  
-----------------ch3----------P21----Dout----------------GPIO9  MISO   
-----------------ch4----------P19----Din-----------------GPIO10 MOSI  
-----------------ch5----------P23----Sclk----------------GPIO11 SCLK  
-----------------ch6----------P6-----Agnd----------------Gnd
-----------------ch7----------P2-----DVdd----------------5V     See Note_1
-GPIO26----------D0-----------P12----RESET---------------GPIO18  
-GPIO19----------D1             
-GPIO13----------D2             
-GPIO6-----------D3
```

The Syn/PwrD is configured as HIGH under all conditions. The program does not
use the Sync pin to initiate a conversion, rather to issue the
SYNC and WAKEUP commands.

Also note, the ChipSelect 'CS' uses GPIO22, not the Pi normal CS0 or CS1
associated with SPI0. Required as operations span more than a single SPI
function and the chip must remain selected.

-cs  -drdy -rst and -pdwn required for this chip configuration.

```
sudo ./runBasicADS1256.sh -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -rst 18 -pdwn 27  -t trace -x -vref 5.0
```

-vref option: if > 0,  feature will display calculated Channel input voltage.

```
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
NOTE_1 !!!!!!   ADS1256 must operate with Vcc 3.3v.  Needed to be compatible with Pi logic voltage
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
!!!!! WARNING:  WHen the AD/DA bard is strapped VCC-3.3, the Pi4 may not power on. Seems the drag
on the 3.3v prevents the Pi boot.
sudo ./runBasicADS1256.sh -p 3 -rp -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -rst 18 -pdwn 27  -t trace -x -vref 5.0
Read DIO 0,  Pi gpio 6
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(6, GPIO.OUT)
GPIO.output(6,GPIO.LOW)
GPIO.output(6 , GPIO.HIGH)
python3
import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BCM)
GPIO.setup(6, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
for i in range(100):
time.sleep(0.1)
print('Pin status = ', GPIO.input(6))
GPIO.cleanup()
sudo ./runBasicADS1256.sh -p 0 -sp HIGH -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -rst 18 -pdwn 27  -t trace -x -vref 5.0
Write setting HIGH DIO 0,
?? Open question to Waveshare. Do-D3 not connected to P1
Pi gpio 26
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(26, GPIO.OUT)
GPIO.output(26,GPIO.LOW)
GPIO.output(26 , GPIO.HIGH)
python3
import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BCM)
GPIO.setup(26, GPIO.IN, pull_up_down=GPIO.PUD_UP)
for i in range(100):
time.sleep(0.1)
print('Pin status = ', GPIO.input(26))
GPIO.cleanup()
pull_up_down=GPIO.PUD_UP
python3
sudo ./runBasicADS1256.sh -p 3 -rp -pp AIN1 -pn AINCOM -cs 22 -drdy 17 -rst 18 -pdwn 27  -t trace -x -vref 5.0Read
Read DIO 0,
?? Open question to Waveshare. Do-D3 not connected to P1
Pi gpio 6
python3
import RPi.GPIO as GPIO
GPIO.setmode(GPIO.BCM)
GPIO.setup(6, GPIO.OUT)
GPIO.output(6,GPIO.LOW)
GPIO.output(6 , GPIO.HIGH)

DRDY    low ->> data conversion complete, RDATA 24 bits.

>>>>Single shot
WREG set channel
STANDBY cmd
WAKEUP
delay 33 microSeconds
DRDY low
RDATA
STANDBY
```
