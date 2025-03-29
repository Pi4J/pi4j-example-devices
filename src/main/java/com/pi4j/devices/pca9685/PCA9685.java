/*
 *    * Copyright (C) 2012 - 2024 Pi4J
 *  * %%
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * -
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: EXTENSION
 *  FILENAME      :  Pca9685.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */

package com.pi4j.devices.pca9685;

//private static final int MCP4725_DEFAULT_ADDRESS = 0x62;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

public class PCA9685 {
    private Logger logger;

    private final Console console;
    private int bus = 0;
    private int address = 0;
    private final Context pi4j;
    private I2C device;

    String traceLevel = "info";

    private int OEPinNum = 0;
    private final DigitalInput OE = null;
    private final PCA9685.DataInGpioListener listener = null;

    private I2CProvider i2CProvider = null;
    private I2C tempDeviceReset = null;
    private I2C tempDeviceAddr1 = null;
    private I2C tempDeviceAddr2 = null;
    private I2C tempDeviceAddr3 = null;

    private DigitalOutput oePin = null;

    public PCA9685(Console console, int bus, int address, int pin, Context pi4j, String traceLevel) {
        this.address = address;
        this.bus = bus;
        this.OEPinNum = pin;
        this.console = console;
        this.pi4j = pi4j;
        this.traceLevel = traceLevel;
        this.init();

    }


    private void init() {
        System.setProperty("org.slf4j.simpleLogger.log." + PCA9685.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(PCA9685.class);
        this.logger.trace(">>> Initializing the chip");

        i2CProvider = this.pi4j.provider("linuxfs-i2c");


        // create i2c device with new address
        var i2cConfig = I2C.newConfigBuilder(pi4j)
            .id("pca9685")
            .bus(this.bus)
            .device(this.address)
            .build();


        this.device = i2CProvider.create(i2cConfig);

        var outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
            .id("OE_pin")
            .name("OE_Pin")
            .address(this.OEPinNum)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH)
            .provider("gpiod-digital-output");
        try {
            this.oePin = pi4j.create(outputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut oePin failed");
            System.exit(201);
        }


        this.logger.trace("<<< Initializing the chip");
    }

    public void reset() {
        this.logger.trace(">>> Reset");
        if (tempDeviceReset == null) {
            I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("pca9685Reset")
                .bus(this.bus)
                .device(0x00)
                .build();
            tempDeviceReset = i2CProvider.create(i2cConfig);
        }
        //  Reset chip
        tempDeviceReset.write(PCA9685Declares.RESET_ADDR);
        this.logger.trace("<<< Reset");
    }

    public void setSubAddr1(int devAddr, int subAddress) {
        String formattedString = String.format(">>> setSubAddr1 DevAddr %x  subAddr %x", devAddr, subAddress);
        this.logger.trace(formattedString);
        if (tempDeviceAddr1 == null) {
            I2CProvider i2CProvider = this.pi4j.provider("linuxfs-i2c");
            I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("pca9685SetAddr1_" + subAddress)
                .bus(this.bus)
                .device(devAddr) //Pca9685Declares.CONFIG_ADDRESS)
                .build();
            tempDeviceAddr1 = i2CProvider.create(i2cConfig);
        }
        // Program subaddr1 to Usersupplied value.
        tempDeviceAddr1.writeRegister(PCA9685Declares.SUBADR1, (subAddress << 1));
        var presentValue = tempDeviceAddr1.readRegister(PCA9685Declares.MODE0_ADDR);
        byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE0_SUBADDR1)) | PCA9685Declares.MODE0_SUBADDR1);
        tempDeviceAddr1.writeRegister(PCA9685Declares.MODE0_ADDR, newVal);
        this.logger.trace("<<< setSubAddr1");
    }

    public void setSubAddr2(int devAddr, int subAddress) {
        String formattedString = String.format(">>> setSubAddr2 DevAddr %x  subAddr %x", devAddr, subAddress);
        this.logger.trace(formattedString);
        if (tempDeviceAddr2 == null) {
            I2CProvider i2CProvider = this.pi4j.provider("linuxfs-i2c");
            I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("pca9685SetAddr2_" + subAddress)
                .bus(this.bus)
                .device(devAddr) //Pca9685Declares.CONFIG_ADDRESS)
                .build();
            tempDeviceAddr2 = i2CProvider.create(i2cConfig);
        }
        // Program subaddr1 to Usersupplied value.
        tempDeviceAddr2.writeRegister(PCA9685Declares.SUBADR2, (subAddress << 1));
        var presentValue = tempDeviceAddr2.readRegister(PCA9685Declares.MODE0_ADDR);
        byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE0_SUBADDR2)) | PCA9685Declares.MODE0_SUBADDR2);
        tempDeviceAddr2.writeRegister(PCA9685Declares.MODE0_ADDR, newVal);
        this.logger.trace("<<< setSubAddr2");
    }

    public void setSubAddr3(int devAddr, int subAddress) {
        String formattedString = String.format(">>> setSubAddr3 DevAddr %x  subAddr %x", devAddr, subAddress);
        this.logger.trace(formattedString);
        if (tempDeviceAddr3 == null) {
            I2CProvider i2CProvider = this.pi4j.provider("linuxfs-i2c");
            I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("pca9685SetAddr3_" + subAddress)
                .bus(this.bus)
                .device(devAddr) //Pca9685Declares.CONFIG_ADDRESS)
                .build();
            tempDeviceAddr3 = i2CProvider.create(i2cConfig);
        }
        // Program subaddr1 to Usersupplied value.
        tempDeviceAddr3.writeRegister(PCA9685Declares.SUBADR3, (subAddress << 1));
        var presentValue = tempDeviceAddr3.readRegister(PCA9685Declares.MODE0_ADDR);
        byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE0_SUBADDR3)) | PCA9685Declares.MODE0_SUBADDR3);
        tempDeviceAddr3.writeRegister(PCA9685Declares.MODE0_ADDR, newVal);
        this.logger.trace("<<< setSubAddr3");
    }

    public void enablePCA(boolean enable){
        this.logger.trace(">>> enablePCA {}", enable);
       if (enable) {
            this.oePin.low();
        } else {
            this.oePin.high();
        }
        this.logger.trace("<<< enablePCA");
    }

    private static class DataInGpioListener implements DigitalStateChangeListener {

        Instant startInstant;
        Duration timeElapsed;
        boolean data_bits_started = false;

        long dataBits = 0;
        int bitCounter = 0;
        PCA9685 theDevice = null;

        public DataInGpioListener(PCA9685 pca) {

            this.theDevice = pca;
            System.out.println("DataInGpioListener ctor");
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            System.out.println(">>> Enter: onDigitalStateChange");
            this.startInstant = Instant.now(); //  init Duration because first event is Low,
            // this is in prep to begin sending high----low transition to signify 0 or 1
            if (event.state() == DigitalState.HIGH) {  // LED off
                //this.startInstant = Instant.now();
                System.out.println("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) { // LEDs on
                System.out.println("onDigitalStateChange Pin went Low");
                //  this.timeElapsed = Duration.between(startInstant, endInstant);
            } else {
                System.out.println("Strange event state  " + event.state());
            }
            System.out.println("<<< Exit: onDigitalStateChange");
        }

    }


    /**
     * The chip will be reset, forcing the POR (PowerOnReset)
     * steps to occur. Once completes the chip will be configured
     * to operate 'forced' mode and single sample.
     *
     * @throws Exception
     */
    void resetSensor() {


        //  device.writeRegister(regVal, ctlVal, ctlVal.length);
    }

    /**
     * Three register sets containing the readings are read, then all factory
     * compensation registers are read. The compensated reading are calculated and
     * displayed.
     */
    public void getMeasurements() {
        byte[] buff = new byte[6];

//        device.readRegister(Pca9685.BMP280Declares.reg_dig_h6, charVal);

        console.println("Humidity: ");


    }

    /**
     * @param read 8 bits data
     * @return unsigned value
     */
    private static int castOffSignByte(byte read) {
        return ((int) read & 0Xff);
    }

    /**
     * @param read 8 bits data
     * @return signed value
     */
    private static int signedByte(byte[] read) {
        return read[0];
    }

    /**
     * @param read 16 bits of data  stored in 8 bit array
     * @return 16 bit signed
     */
    private static int signedInt(byte[] read) {
        int temp = 0;
        temp = (read[0] & 0xff);
        temp += (((long) read[1]) << 8);
        return (temp);
    }

    /**
     * @param read 16 bits of data  stored in 8 bit array
     * @return 64 bit unsigned value
     */
    private static long castOffSignInt(byte[] read) {
        long temp = 0;
        temp = ((long) read[0] & 0xff);
        temp += (((long) read[1] & 0xff)) << 8;
        return (temp);
    }


}
