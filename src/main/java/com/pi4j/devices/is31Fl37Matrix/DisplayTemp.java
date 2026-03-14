package com.pi4j.devices.is31Fl37Matrix;/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  DisplayTemp.java
 *      *
 *      * This file is part of the Pi4J project. More information about
 *      * this project can be found here:  https://pi4j.com/
 *      * **********************************************************************
 *     * %%
 *     * Copyright (C) 2012 - 2022 Pi4J
 *     * %%
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
 *   *
 *
 *
 */


import com.pi4j.context.Context;
import com.pi4j.devices.bmp280.BMP280Declares;
import com.pi4j.devices.bmp280.BMP280DeviceI2C;
import com.pi4j.drivers.sensor.environment.bmx280.Bmx280Driver;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;
import org.slf4j.Logger;

import java.text.DecimalFormat;
import java.time.LocalTime;

/**
 * Class will obtain the temperature from the BMP280 and display it through the matrix controller.
 * Then obtain the system time-of-day and display that through the matrix controller.
 */
public class DisplayTemp {

    private final Is31Fl37Matrix display;
    private final Logger logger;
    private final Context pi4j;
    private final Console console;
    private BMP280DeviceI2C bmpDev;
    private I2C bmeDev;
    private Bmx280Driver bmx280Driver;

    /**
     * This class includes the state -  private BMP280Device bmpDev;    This is required as Pi4j_V2
     * does not permit repeated instantiations of an I2C device using the same Pi i2c Bus and address. So
     * this class creates the BMP280 device once and reuses that same instance on subsequent invocation.
     */

    public DisplayTemp(Context pi4j, Console console, Is31Fl37Matrix display, Logger logger) {
        this.display = display;
        this.logger = logger;
        this.pi4j = pi4j;
        this.console = console;
        this.bmpDev = null;
        this.bmx280Driver = null;

    }

    /**
     * Note, the bmp_bus and bmp_address supplied on the initial call to this method will continue to be used
     * on subsequent calls regardless of values supplied.
     */
    public void process_bmp_data(ControlLeds pin_monitor, int led_blink,
                                 Integer loop_count, Integer bmp_bus, Integer bmp_address, byte sensorID) {
        int display_num = 42;
        double[] readings = new double[2];
        // bmp = read_BMP180.ReadBmp180()
        this.logger.trace("process_bmp_data");
        this.logger.trace("parms :  blink : " + String.format("0x%02X", led_blink) + " loop_count : "
            + String.format("0x%02X", loop_count) + " bmp180 address : " + String.format("0x%02X", bmp_address));

        if (sensorID == BMP280Declares.idValueMskBMP) {
            if (this.bmpDev == null) {
                this.bmpDev = new BMP280DeviceI2C(this.pi4j, this.console, bmp_bus, bmp_address, this.logger);
            }
            readings[0] = bmpDev.temperatureC();

        } else if (sensorID == BMP280Declares.idValueMskBME) {
            if (this.bmeDev == null) {
                I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                    .id("BME280")
                    .bus(bmp_bus)
                    .device(bmp_address)
                    .build();

                // Read values 10 times
                this.bmeDev = pi4j.create(i2cConfig);
                bmx280Driver = new Bmx280Driver(bmeDev);
            }
            DecimalFormat df = new DecimalFormat("0.###");

            console.println("**************************************");
            console.println("Reading values,");

            Bmx280Driver.Measurement measurement = bmx280Driver.readMeasurement();

            console.println("Temperature: " + df.format(measurement.getTemperature()) + " °C");
            console.println("Temperature: " + df.format(measurement.getTemperature() * 1.8 + 32) + " °F ");

            console.println("Pressure: " + df.format(measurement.getPressure()) + " Pa");
            // 1 Pa = 0.00001 bar or 1 bar = 100,000 Pa
            console.println("Pressure: " + df.format(measurement.getPressure() / 100_000) + " bar");
            // 1 Pa = 0.0000098692316931 atmosphere (standard) and 1 atm = 101.325 kPa
            console.println("Pressure: " + df.format(measurement.getPressure() / 101_325) + " atm");

            console.println("Humidity: " + df.format(measurement.getHumidity()) + " %");

            readings[0] = Double.parseDouble(df.format(measurement.getTemperature()));

        }
        double temp = readings[0];
        // double pressure = readings[1];
        // (temp, pressure) = bmp.readBmp180()
        // if verbose:
        // print " Temp Celsius ", temp
        // print " Presure " , pressure

        boolean time_mode = false;
        display_num = (int) ((temp * 1.8) + 32);
        // if verbose:
        // print "Display fahrenheit temp " ,display_num
        char[] display_asc = new char[5];
        DisplayLED disp_worker = new DisplayLED(this.logger);
        disp_worker.create_led_pattern(pin_monitor, this.display, display_num, display_asc, led_blink, loop_count,
            time_mode);
        /*
         * System.out.println("sit and spin to see if the java trigger fires");
         * try { Thread.sleep(10); } catch (InterruptedException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); }
         */

        InterruptDetails completed = pin_monitor.wait_for_interrupt();
        if (completed.getSuccessVal()) {

            pin_monitor.toggle_led(false);
        } else {
            pin_monitor.flash_alarm_led(this.display);
        }
        this.logger.trace("process_bmp_data");

    }

    public void show_time(ControlLeds pin_monitor, int led_blink,
                          Integer loop_count) {
        int display_num = 42;
        boolean time_mode = true;
        // # obtain a viewable version of the time and display the time
        LocalTime thisSec;
        this.logger.trace("show_time");
        this.logger.trace("parms :   blink : " + String.format("0x%02X", led_blink) + " loop_count : "
            + String.format("0x%02X", loop_count));

        thisSec = LocalTime.now();
        String string_asc = String.format("%1$02d:%2$02d", thisSec.getHour(), thisSec.getMinute());
        char[] display_asc = string_asc.toCharArray();
        DisplayLED disp_worker = new DisplayLED(this.logger);
        disp_worker.create_led_pattern(pin_monitor, this.display, display_num, display_asc, led_blink, loop_count,
            time_mode);

        InterruptDetails completed = pin_monitor.wait_for_interrupt();
        if (completed.getSuccessVal()) {
            pin_monitor.toggle_led(false);
        } else {
            pin_monitor.flash_alarm_led(this.display);
        }
        this.logger.trace("show_time");

    }

}
