package com.pi4j.devices.bme280;
/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  BME280DeviceI2C.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2022 Pi4J
 *      * %%
 *     *
 *     * Licensed under the Apache License, Version 2.0 (the "License");
 *     * you may not use this file except in compliance with the License.
 *     * You may obtain a copy of the License at
 *     *
 *     *      http://www.apache.org/licenses/LICENSE-2.0
 *     *
 *     * Unless required by applicable law or agreed to in writing, software
 *     * distributed under the License is distributed on an "AS IS" BASIS,
 *     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     * See the License for the specific language governing permissions and
 *     * limitations under the License.
 *     * #L%
 *     *
 *
 *
 *
 */


import com.pi4j.Pi4J;
import com.pi4j.driver.sensor.bmx280.Bmx280Driver;
import com.pi4j.driver.sensor.bmx280.Bmx280Driver.SensorType;
import com.pi4j.driver.sensor.bmx280.Bmx280Driver.Measurement;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.util.Console;

import java.text.DecimalFormat;

/**
 * Example code to read the temperature, humidity and pressure from a BME280 sensor, on an Adafruit board via I2C and SPI.
 * <p>
 * This example can be executed without sudo with:
 * jbang Pi4JTempHumPressI2C.java
 * <p>
 * Based on:
 *
 * <ul>
 *  <li>https://github.com/Pi4J/pi4j-example-devices/blob/master/src/main/java/com/pi4j/devices/bmp280/README.md</li>
 *  <li>https://www.adafruit.com/product/2652</li>
 *  <li>https://learn.adafruit.com/adafruit-bme280-humidity-barometric-pressure-temperature-sensor-breakout/pinouts</li>
 * </ul>
 * <p>
 * I2C Wiring
 *
 * <ul>
 *  <li>Vin to 3.3V</li>
 *  <li>GND to GND</li>
 *  <li>SCK to I2C clock SCL (pin 5)</li>
 *  <li>SDI to I2C data SDA (pin 3)</li>
 *  <li>CS to 3.3v</li>
 * </ul>
 * <p>
 * Make sure I2C is enabled on the Raspberry Pi. Use `sudo raspi-config' > Interface Options > I2C.
 * <p>
 * Check that the sensor is detected on address 0x77 with ``.
 * <p>
 * $ i2cdetect -y 1
 *      0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
 * 00:                         -- -- -- -- -- -- -- --
 * 10: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 20: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 30: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 40: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 50: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 60: -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
 * 70: -- -- -- -- -- -- -- 77
 */
public class BME280DeviceI2C {
    private static final Console console = new Console(); // Pi4J Logger helper

    private static final int I2C_BUS = 0x01;
    private static final int I2C_ADDRESS = 0x77; // When connecting SDO to GND = 0x76

    public static void main(String[] args) throws Exception {

        var pi4j = Pi4J.newAutoContext();
        int address = I2C_ADDRESS;
        String helpString = " parms:  -a hex value address  -h help \n ";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }
        // Initialize I2C
        console.println("Initializing the sensor via I2C");
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
            .id("BME280")
            .bus(I2C_BUS)
            .device(address)
            .build();

        // Read values 10 times
        try (I2C i2c = i2CProvider.create(i2cConfig)) {

            Bmx280Driver bmx280Driver = new Bmx280Driver(i2c);

            DecimalFormat df = new DecimalFormat("0.###");

            for (int counter = 0; counter < 10; counter++) {
                console.println("**************************************");
                console.println("Reading values, loop " + (counter + 1));

                Measurement measurement = bmx280Driver.readMeasurement();

                console.println("Temperature: " + df.format(measurement.getTemperature()) + " °C");
                console.println("Temperature: " + df.format(measurement.getTemperature() * 1.8 + 32) + " °F ");

                console.println("Pressure: " + df.format(measurement.getPressure()) + " Pa");
                // 1 Pa = 0.00001 bar or 1 bar = 100,000 Pa
                console.println("Pressure: " + df.format(measurement.getPressure() / 100_000) + " bar");
                // 1 Pa = 0.0000098692316931 atmosphere (standard) and 1 atm = 101.325 kPa
                console.println("Pressure: " + df.format(measurement.getPressure() / 101_325) + " atm");

                if (type == SensorType.BME280) {
                    console.println("Humidity: " + df.format(measurement.getHumidity()) + " %");
                }
            }
        }

        pi4j.shutdown();

        console.println("**************************************");
        console.println("Finished");
    }
}
