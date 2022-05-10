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
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SN74HC595 8 Bit shift register. Serial device.  Uses GPIOs line manipulation to clock data into the device
 */
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
    int OEPinNum = 0xff;
    int STCPPinNum = 0xff;
    int SHCPPinNum = 0xff;
    int MRPinNum = 0xff;
    int DSPinNum = 0xff;



    /**
     *
     * @param pi4j
     * @param console
     *     GPIO output lines from Pi, all are input signals to the chip
     *     The param name matches the spec.  Three of the params document the
     *     state name used with in the source code. This rename makes their usage more obvious
     * @param dsGpio   DataPin sending from Pi to the chip
     * @param oeGpio
     * @param stcpGpio  Latch control
     * @param shcpGpio  Clock signal
     * @param mrGpio
     *
     * @param registerData   8 bita, each representing the intended state of the output lines.  QH------Qa
     * @param traceLevel   slf4j log level
     */
    public SN74HC595(Context pi4j, Console console, int dsGpio, int oeGpio, int stcpGpio, int shcpGpio, int mrGpio, byte registerData, String traceLevel) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.OEPinNum = oeGpio;
        this.STCPPinNum = stcpGpio;
        this.SHCPPinNum = shcpGpio;
        this.MRPinNum = mrGpio;
        this.DSPinNum = dsGpio;

        this.registerData = registerData;
        this.traceLevel = traceLevel;
        this.init();
    }

    /**
     *   Create the five output pins
     *   Reset the chip, output enable the pins QH------Qa
     */
    void init() {

        System.setProperty("org.slf4j.simpleLogger.log." + SN74HC595.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(SN74HC595.class);
        this.logger.trace(">>> Enter: init");

        this.logger.trace("DS Pin  " + this.DSPinNum);
        this.logger.trace("OE Pin  " + this.OEPinNum);
        this.logger.trace("STCP Pin  " + this.STCPPinNum);
        this.logger.trace("MR Pin  " + this.MRPinNum);
        this.logger.trace("SHCP Pin  " + this.SHCPPinNum);

        var outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
                .id("OE_pin")
                .name("Enable")
                .address(this.OEPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        try {
            this.oeGpio = pi4j.create(outputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut OE failed");
            System.exit(201);
        }
        var outputConfig2 = DigitalOutput.newConfigBuilder(pi4j)
                .id("STCP_pin")
                .name("STCP")
                .address(this.STCPPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            this.latchPin = pi4j.create(outputConfig2);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut STCP failed");
            System.exit(201);
        }
        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
                .id("SHCP_pin")
                .name("SHCP")
                .address(this.SHCPPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        try {
            this.clockPin = pi4j.create(outputConfig3);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut SHCP failed");
            System.exit(201);
        }
        var outputConfig4 = DigitalOutput.newConfigBuilder(pi4j)
                .id("MR_pin")
                .name("MR")
                .address(this.MRPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            this.mrGpio = pi4j.create(outputConfig4);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut MR failed");
            System.exit(201);
        }
        var outputConfig5 = DigitalOutput.newConfigBuilder(pi4j)
                .id("DS_pin")
                .name("DS")
                .address(this.DSPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        try {
            this.dataPin = pi4j.create(outputConfig5);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut MR failed");
            System.exit(201);
        }

        // raise to get chip out of reset
        this.mrGpio.low();
        this.sleepTime(100);
        this.mrGpio.high();
        // Enable for output
        this.oeGpio.high();
        this.sleepTime(100);
        this.oeGpio.low();
        this.logger.trace("<<< Exit: init  device  ");
    }

    /**
     * Walk the registerData from MSB to LSB. This is required as the first bit loaded into the chip
     * will bit by bit move to the right, ultimately the latch data will contain QH in the LSB.
     */
    void updateSN74() {
        String binaryString = Integer.toBinaryString(this.registerData & 0xff);
        String withLeadingZeros = String.format("%8s", binaryString).replace(' ', '0');
        this.logger.trace(">>> Enter: updateSN74  shift data  " + withLeadingZeros);

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
;

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
