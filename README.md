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



(1): This package uses code within this repo and Pi4J
(2): Requires 2.2.2-SNAPSHOT of Pi4j that supports i2c multibyte write/restart
(3): SPI versions of the device uses Pigpio, cannot be used on Raspberry Pi5


Python
For details see https://github.com/Pi4J/pi4j-example-devices/pull/80
monitor.py
monitor_single_line.py:

