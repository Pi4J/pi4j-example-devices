Pi4J :: Java I/O Library for Raspberry Pi :: Devices
====================================================

Collection of devices implemented for use with Pi4J V2.

Project by Tom Aarts.

```text
Note: This repository has a tag identifying the code level that works with
2.4.0-SNAPSHOT.   Commits newer than this tag are migrations to the 
2.6.0-SNAPSHOT. These commits may contain  code using providers and/or Pi4J-V2
interfaces not available in prior SNAPSHOTs.. 
```
The following lists the currently supported devices within this project:

* [1602A LCD  HD44780U](src/main/java/com/pi4j/devices/hd44780u_lcd1602a/README.md): App uses SN74HC595 to control the LCD
* [1602A LCD MCP23017 I2C  Controller](src/main/java/com/pi4j/devices/mcp23017_lcd1602a/README.md): App uses MCP23017 to control the LCD
* [1602A_LCD_PCF8574A I2C  Controller](src/main/java/com/pi4j/devices/pcf8574a_lcd1602a/README.md): App uses PCF8574A to control the LCD
* [ADS1256 24bit A-to-D](src/main/java/com/pi4j/devices/ads1256/README.md) (1)
* [AT24C512 SEEPROM](src/main/java/com/pi4j/devices/at24c512/README.md) (1) (2)
* [BMP280  Temperature and Pressure Sensor](src/main/java/com/pi4j/devices/bmp280/README.md) (1)
* [BME280  Temperature, Pressure and Humidity Sensor (I2C & SPI)](src/main/java/com/pi4j/devices/bme280/README.md) (1)(3)
* [DAC8552  16bit DAC  SPI connected](src/main/java/com/pi4j/devices/dac8552/README.md) (3)
* [DHT22 Temp/Humidity sensor](src/main/java/com/pi4j/devices/dht22/README.md) (1)
* [HC-SR04 Ultrasonic Sensor](src/main/java/com/pi4j/devices/hcsr04/README.md)
* [Is31fl3731 matrix controller](src/main/java/com/pi4j/devices/is31Fl37Matrix/README.md) (1)
* [MCP23008 drive and read chip GPIOs](src/main/java/com/pi4j/devices/mcp23008/README.md)
* [MCP23008 and MCP23017 Pin monitoring (interrupt support)](src/main/java/com/pi4j/devices/mcp23xxxApplication/README.md)
* [MCP23017 drive and read chip GPIOs](src/main/java/com/pi4j/devices/mcp23017/README.md)
* [MCP3008 A/D 10bit converter](src/main/java/com/pi4j/devices/mcp3008/README.md) (1)(3)
* [MCP4725  12 bit DAC](src/main/java/com/pi4j/devices/mcp4725/README.md) (1)
* [MPL3115A2 Temp/Pressure/Altitude device](src/main/java/com/pi4j/devices/mpl3115a2/README.md) (1)
* [NeoPixel94V  Intelligent Control LED device](src/main/java/com/pi4j/devices/neopixel94v/README.md) (1)(3)
* [QT Rotary Encoder](src/main/java/com/pi4j/devices/rotary_encoder/README.md) (1)
* [SN74HC595 8 Bit shift register](src/main/java/com/pi4j/devices/sn74hc595/README.md) (1)
* [SSD1306 OLED I2C](src/main/java/com/pi4j/devices/ssd1306/README.md) (1)
* [TCA9548 (1x8 I2C switch)](src/main/java/com/pi4j/devices/tca9548/README.md)
* [VL53L0X TimeOfFlight device](src/main/java/com/pi4j/devices/vl53L0X/README.md) (1)

### Notes:

(1): This package uses code within this repo and Pi4J \
(2): Requires 2.2.2-SNAPSHOT of Pi4j that supports i2c multibyte write/restart \
(3): SPI versions of the device uses Pigpio, cannot be used on Raspberry Pi5 \


### GPIO monitor

This repository includes two Python scripts to monitor GPIO pin states in real-time on a Raspberry Pi. \
**Note**: These scripts use the pigpio library, which is currently incompatible with the Raspberry Pi 5.

**Setting Up pigpio**

To install pigpio, run:

```bash
pip install pigpio
```

Also, make sure the `pigpiod` daemon is running before executing either script:

```bash
sudo pigpiod
```

1. `monitor.py`

This script provides a detailed, line-by-line log of GPIO state changes, including the time difference in microseconds between state changes for each pin.
 - Usage:
   
```bash
python3 monitor.py              # Monitor all GPIO pins
python3 monitor.py 23 24 25     # Monitor only GPIO pins 23, 24, and 25
```

 - Features:
    - Logs state changes (`HIGH`/`LOW`) for each monitored GPIO pin.
    - Displays the time difference between successive state changes in microseconds.
    - Structured with modular functions for initializing GPIO monitoring, handling state changes, and cleanup on exit.
 - Example Output:

```bash
Monitoring GPIO pins... Press Ctrl+C to stop.
GPIO=23 Level=1 Time Diff=120 μs
GPIO=24 Level=0 Time Diff=95 μs
GPIO=25 Level=1 Time Diff=110 μs
```

2. `monitor_table_format.py`
   
This script offers a compact, tabular format for monitoring GPIO states, displaying the current state of each pin in a single, updating line. This view is ideal for visualizing slow state changes without excessive log clutter.

 - Usage:
```bash
python3 monitor_table_format.py          # Monitor all GPIO pins
python3 monitor_table_format.py 23 24 25 # Monitor only GPIO pins 23, 24, and 25
```

 - Features:
   - Displays pin states in a single, continuously updating line.
   - Ideal for slow changes, as it reduces visual clutter by showing only the current state of each pin.
   - Automatically updates the state every second (customizable by adjusting the sleep interval).
 - Example Output:

```bash
Monitoring GPIO pins... Press Ctrl+C to stop.
GPIO   23 | 24 | 25
STATE   1 |  0 |  1
```



