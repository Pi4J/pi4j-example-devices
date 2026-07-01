
https://github.com/adafruit/Adafruit_CircuitPython_seesaw

Interrupt pin GPIO 21, Hex 15

I2C connection path.

1. mvn clean install
2. cd target/distribution
3. sudo ./runRotaryEncoder.sh
   Args Interrupt pin or address must be set, optionally -p

```
sudo ./runRotaryEncoder.sh -i 0x15 -a 0x36 -p 0x0
```

parms:  -a hex address -i hex interrupt GPIO number, -p hex position, -h help

As the knob is twisted, each indent should change the returned
encoder position by a value of 1.

sudo is included in the command, depending on the I2C provider being used this may
not be required.
