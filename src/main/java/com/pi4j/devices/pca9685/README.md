PCA9685 12-bit PWM LED controlled

## Wiring:  As coded this program uses I2C

1. ./mvnw clean package
2. cd target/distribution
3. ./runPca9685.sh OPT parms........
   Parm -t traceLevel

./runPca9685.sh -b 0x1 -a 0x70 -s1 0x72 -s2 0x74 -s3 0x76 -t "TRACE"

./runPca9685.sh -b 0x1 -a 0x70 -x -s1 0x72 -s2 0x74 -s3 0x76

# NOTE:

If the chip was never reset its device address is 0x70.
If reset the 0x70 no longer exists, the device will now
ACK and address set into subaddress1 subaddress1 or subaddress1

# Wiring

Pi PCA9685
GRND GRND
GPIO26 OE
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