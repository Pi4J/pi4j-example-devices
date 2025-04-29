PCA9685 12-bit PWM LED controlled

## Wiring:  As coded this program uses I2C

1. ./mvnw clean package
2. cd target/distribution
3. ./runPCA9685.sh OPT parms........
   Parm -t traceLevel

Note In example usage the EO pin is connected to gpio 12
VCC and V+ connected to Pi 5v
Note  Must enable the LEDs via -e E, this will drive OE GPIO low.




./runPCA9685.sh -b 0x1 -a 0x70  -p 0x0c -s1 0x72 -s2 0x74 -s3 0x76 -t "TRACE"

./runPCA9685.sh -b 0x1 -a 0x70 -p 0x0c -x -s1 0x72 -s2 0x74 -s3 0x76

After settting the above attributes, the chip can be used to continue
controlling LEDs/PWMs

# LED0  set intensity to 0x700  (2000) of a possible 0xFFF (4095)
./runPCA9685.sh -intensity 0x70 0x00 0x07d0


LED 0 off
-ledOn 0x70 0x00 0x0 0xfff

LED 0 off
-ledOn 0x70 0x00 0xfff 0x0

# NOTE:

If the chip was never reconfigured its device address is 0x70.

# Wiring

Pi PCA9685
GRND GRND
GPIO12 OE
SCL SCL
SDA SDA
3.3VDC VCC
5VDC V+
pin0 yellow LED +
pin0 red NC
pin0 black LED -
pin1 yellow LED +
pin1 red NC
pin1 black LED -
pin8 yellow Servo orange
pin8 red Servo red
pin8 black Servo brown