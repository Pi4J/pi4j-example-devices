/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  SN74HC595.java
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

package com.pi4j.devices.sn74hc595;

import com.pi4j.context.Context;
import com.pi4j.devices.vl53L0X.VL53L0X_Device;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SN74HC595 {
    private final Console console;
    private final Context pi4j;
    private DigitalOutput oeGpio = null;
    private DigitalOutput latchPin = null;   // otcp  pin 12
    private DigitalOutput clockPin = null;   // shcp pin 11
    private DigitalOutput dataPin = null;  // DS pin 14
    private DigitalOutput mrGpio = null;
    private final byte registerData;
    private final String traceLevel;
    private Logger logger;


    public SN74HC595(Context pi4j, Console console, DigitalOutput dsGpio, DigitalOutput oeGpio, DigitalOutput stcpGpio, DigitalOutput shcpGpio, DigitalOutput mrGpio, byte registerData, String traceLevel) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.oeGpio = oeGpio;
        this.latchPin = stcpGpio;
        this.clockPin = shcpGpio;
        this.mrGpio = mrGpio;
        this.dataPin = dsGpio;
        this.registerData = registerData;
        this.traceLevel = traceLevel;
        this.init();
    }

    void init() {

        System.setProperty("org.slf4j.simpleLogger.log." + SN74HC595.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(VL53L0X_Device.class);
        this.logger.info(">>> Enter: init");

        // raise to get chip out of reset
        this.mrGpio.low();
        this.sleepTime(100);
        this.mrGpio.high();
        // Enable for output
        this.oeGpio.high();
        this.sleepTime(100);
        this.oeGpio.low();
        this.logger.info("<<< Exit: init  device  ");
    }


    void updateSN74() {
        String binaryString = Integer.toBinaryString(this.registerData & 0xff);
        String withLeadingZeros = String.format("%8s", binaryString).replace(' ', '0');
        this.logger.info(">>> Enter: updateSN74  shift data  " + withLeadingZeros);
        // walk through the byte of shift data, LSB to MSB
        // Set the dataPin same as the shift bit. For each bit toggle the clockPin.
        // After all bits processed, Toggle latchPin
        this.latchPin.low();      // make certain chip is quiet
        this.clockPin.low();
        this.dataPin.low();
        for (int i = 7; i >= 0; i--) {
            this.clockPin.low();
            int compareBit = 1;
            compareBit = compareBit << i;
            boolean bitSet = ((this.registerData & compareBit) > 0);
            if (bitSet) {
                this.dataPin.high();
            } else {
                this.dataPin.low();
            }
            this.clockPin.high();
            this.dataPin.low();
            // this.sleepTime(100);

        }
        this.clockPin.low();
        this.latchPin.high();

        this.logger.info("<<< Exit: updateSN74");

    }


    private void sleepTime(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
