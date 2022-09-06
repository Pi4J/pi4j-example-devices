Pi4J :: Java I/O Library for Raspberry Pi :: Devices
====================================================

Collection of devices implemented for use with Pi4J V2.

Project by Tom Aarts.

The following lists the currently supported devices within this project:

* [1602A LCD  HD44780U](src/main/java/com/pi4j/devices/hd44780u_lcd1602a/README.md): App uses SN74HC595 to control the LCD
* [1602A LCD MCP23017 I2C  Controller](src/main/java/com/pi4j/devices/mcp23017_lcd1602a/README.md): App uses MCP23017 to control the LCD
* [1602A_LCD_PCF8574A I2C  Controller](src/main/java/com/pi4j/devices/pcf8574a_lcd1602a/README.md): App uses PCF8574A to control the LCD
* [ADS1256 24bit A-to-D](src/main/java/com/pi4j/devices/ads1256/README.md) (1)
* [BMP280  Temperature and Pressure Sensor](src/main/java/com/pi4j/devices/bmp280/README.md) (1)
* [DAC8552  16bit DAC  SPI connected](src/main/java/com/pi4j/devices/dac8552/README.md)
* [DHT22 Temp/Humidity sensor](src/main/java/com/pi4j/devices/dht22/README.md) (1)
* [Is31fl3731 matrix controller](src/main/java/com/pi4j/devices/is31Fl37Matrix/README.md) (1)
* [MCP23008 drive and read chip GPIOs](src/main/java/com/pi4j/devices/mcp23008/README.md)
* [MCP23008 and MCP23017 Pin monitoring (interrupt support)](src/main/java/com/pi4j/devices/mcp23xxxApplication/README.md)
* [MCP23017 drive and read chip GPIOs](src/main/java/com/pi4j/devices/mcp23017/README.md)
* [MCP3008 A/D 10bit converter](src/main/java/com/pi4j/devices/mcp3008/README.md) (1)
* [MCP4725  12 bit DAC](src/main/java/com/pi4j/devices/mcp4725/README.md) (1)
* [MPL3115A2 Temp/Pressure/Altitude device](src/main/java/com/pi4j/devices/mpl3115a2/README.md) (1)
* [SN74HC595 8 Bit shift register](src/main/java/com/pi4j/devices/sn74hc595/README.md) (1)
* [TCA9548 (1x8 I2C switch)](src/main/java/com/pi4j/devices/tca9548/README.md)
* [VL53L0X TimeOfFlight device](src/main/java/com/pi4j/devices/vl53L0X/README.md) (1)


    * Simple module mount of the chip  
    * Mounted within WaveShare AD/DA Board  
 
(1): This package does not use any other code within this repo
