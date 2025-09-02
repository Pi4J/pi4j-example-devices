/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  BME280DeviceSPI.java
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

package com.pi4j.devices.bme280;

import com.pi4j.Pi4J;
import com.pi4j.drivers.sensor.bmx280.Bmx280Driver;
import com.pi4j.drivers.sensor.bmx280.Bmx280Driver.Measurement;
import com.pi4j.drivers.sensor.bmx280.Bmx280Driver.Model;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;

import java.text.DecimalFormat;

/**
 * Example code to read the temperature, humidity and pressure from a BME280 sensor, on an Adafruit board via I2C and SPI.
 * <p>
 * Based on:
 *
 * <ul>
 *  <li>https://github.com/Pi4J/pi4j-example-devices/blob/master/src/main/java/com/pi4j/devices/bmp280/README.md</li>
 *  <li>https://www.adafruit.com/product/2652</li>
 *  <li>https://learn.adafruit.com/adafruit-bme280-humidity-barometric-pressure-temperature-sensor-breakout/pinouts</li>
 * </ul>
 * <p>
 * SPI Wiring
 *
 * <ul>
 *  <li>Vin to 3.3V</li>
 *  <li>GND to GND</li>
 *  <li>SDI to MOSI (BCM10, pin 19)</li>
 *  <li>SDO to MISO (BCM9, pin 21)</li>
 *  <li>SCK to SCLK (BCM11, pin 23)</li>
 *  <li>CS to BCM21 (pin 40)</li>
 * </ul>
 */
public class BME280DeviceSPI {


    private static final Console console = new Console(); // Pi4J Logger helper

    private static final String SPI_PROVIDER_NAME = "BME280 SPI Provider";
    private static final String SPI_PROVIDER_ID = "BME280-spi";

    private static final SpiChipSelect chipSelect = SpiChipSelect.CS_0;
    private static final SpiBus spiBus = SpiBus.BUS_0;
    private static DigitalOutput csGpio;
    private static Spi spi;

    public static void main(String[] args) throws Exception {
        var pi4j = Pi4J.newAutoContext();
        var csPin = 21; // BCM 21 = physical pin 40

        // Initialize SPI
        console.println("Initializing the sensor via SPI");

        String helpString = " parms:      -csp  chipSelectGPIO    \n ";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-csp")) { // device address
                String a = args[i + 1];
                i++;
                csPin = Integer.parseInt(a);
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }

        var csGpioConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id("CS_pin")
            .name("CS")
            .address(csPin)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH)
            .provider("gpiod-digital-output");
        csGpio = pi4j.create(csGpioConfig);

        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id(SPI_PROVIDER_ID)
            .name(SPI_PROVIDER_NAME)
            .bus(spiBus)
            .chipSelect(chipSelect)
            .baud(Spi.DEFAULT_BAUD)
            .mode(SpiMode.MODE_0)
            .provider("linuxfs-spi")
            .build();
        spi = pi4j.create(spiConfig);

        Bmx280Driver bmx280Driver = new Bmx280Driver(spi, csGpio);

        console.println("Device model detected: " + bmx280Driver.getModel());

        // Read values 10 times
        for (int counter = 0; counter < 10; counter++) {
            console.println("**************************************");
            console.println("Reading values, loop " + (counter + 1));

            printMeasurements(bmx280Driver);
        }

        pi4j.shutdown();

        console.println("**************************************");
        console.println("Finished");
    }

    /**
     * Three register sets containing the readings are read, then all factory
     * compensation registers are read. The compensated reading are calculated and
     * displayed.
     */
    private static void printMeasurements(Bmx280Driver bmx280Driver) {
        DecimalFormat df = new DecimalFormat("0.###");

        Measurement measurement = bmx280Driver.readMeasurement();

        console.println("Temperature: " + df.format(measurement.getTemperature()) + " °C");
        console.println("Temperature: " + df.format(measurement.getTemperature() * 1.8 + 32) + " °F ");

        console.println("Pressure: " + df.format(measurement.getPressure()) + " Pa");
        // 1 Pa = 0.00001 bar or 1 bar = 100,000 Pa
        console.println("Pressure: " + df.format(measurement.getPressure() / 100_000) + " bar");
        // 1 Pa = 0.0000098692316931 atmosphere (standard) and 1 atm = 101.325 kPa
        console.println("Pressure: " + df.format(measurement.getPressure() / 101_325) + " atm");

        if (bmx280Driver.getModel() == Model.BME280) {
            console.println("Humidity: " + df.format(measurement.getHumidity()) + " %");
        }
    }
}
