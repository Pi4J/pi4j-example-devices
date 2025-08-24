/*
 *    * Copyright (C) 2012 - 2025 Pi4J
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
 *  FILENAME      :  PCA9685.java
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

//import java.nio.ByteBuffer;

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

    private I2CProvider i2CProvider = null;
    private final I2C tempDeviceReset = null;
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
        // possible providers left as comment
        i2CProvider = this.pi4j.provider("linuxfs-i2c");   // linuxfs-i2c  FFMI2CProviderImpl


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


        var old_mode1 = this.device.readRegister(PCA9685Declares.MODE1_ADDR);
        // clear power mode bit to normal operation
        old_mode1 = old_mode1 & ~PCA9685Declares.SLEEP_LOW_PWR;
        // ensure AutoInc bitis set
        this.device.writeRegister(PCA9685Declares.MODE1_ADDR, (old_mode1 | PCA9685Declares.AUTO_INC_ENAB));

        // set freq to 50
        this.setFreq(50);
        this.showMode1();

       this.logger.trace("<<< Initializing the chip");
    }


    public void showFreq() {
        var prescale = this.device.readRegister(PCA9685Declares.PRE_SCALE);
        int freq = PCA9685Declares.defaultClockSpeed / 4096 / (prescale + 1);
        String formattedString = String.format(">>> showFreq   (DEC) %d", freq);
        this.logger.debug(formattedString);
    }

    public void setFreq(int freq) {
       this.logger.trace(">>> setFreq  " + freq);

        int prescale = (int) (((PCA9685Declares.defaultClockSpeed / 4096.0 / freq) + 0.5) - 1.0);
        if (prescale < 3) {
           this.logger.error("Invalid freq  " + freq);
           return;
         }
        this.logger.trace("prescale  " + prescale);
        var old_mode = this.device.readRegister(PCA9685Declares.MODE1_ADDR);
        //  byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE1_AI)) | AUTO_INC_ENAB .MODE1_AI);
        // byte newVal = (byte) (presentValue | 0XA0);
        this.device.writeRegister(PCA9685Declares.MODE1_ADDR, (old_mode & 0x7F) | PCA9685Declares.SLEEP_LOW_PWR); //  # Mode 1, sleep
        this.device.writeRegister(PCA9685Declares.PRE_SCALE, (byte) prescale);
        this.device.writeRegister(PCA9685Declares.MODE1_ADDR, (old_mode));
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.device.writeRegister(PCA9685Declares.MODE1_ADDR, (old_mode | 0x20));
    }

    /**
     * Reset   Reset IC and then set Mode1 t0 auto increment registers
     */
    public void reset() {
       this.logger.trace(">>> Reset");
        if (tempDeviceReset == null) {
            I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("pca9685Reset")
                .bus(this.bus)
                .device(0x00)
                .build();
            //  tempDeviceReset = i2CProvider.create(i2cConfig);
        }
        // enable restart
        var old_mode = this.device.readRegister(PCA9685Declares.MODE1_ADDR);
        //       tempDeviceReset.writeRegister(PCA9685Declares.MODE1_ADDR, old_mode|0x80);
        //  Reset chip
        this.device.write(PCA9685Declares.RESET_ADDR);
        //set AI
        this.device.writeRegister(PCA9685Declares.MODE1_ADDR, old_mode);

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
        var presentValue = tempDeviceAddr1.readRegister(PCA9685Declares.MODE1_ADDR);
        byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE1_SUBADDR1)) | PCA9685Declares.MODE1_SUBADDR1);
        tempDeviceAddr1.writeRegister(PCA9685Declares.MODE1_ADDR, newVal);
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
        var presentValue = tempDeviceAddr2.readRegister(PCA9685Declares.MODE1_ADDR);
        byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE1_SUBADDR2)) | PCA9685Declares.MODE1_SUBADDR2);
        tempDeviceAddr2.writeRegister(PCA9685Declares.MODE1_ADDR, newVal);
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
        var presentValue = tempDeviceAddr3.readRegister(PCA9685Declares.MODE1_ADDR);
        byte newVal = (byte) ((presentValue & (~PCA9685Declares.MODE1_SUBADDR3)) | PCA9685Declares.MODE1_SUBADDR3);
        tempDeviceAddr3.writeRegister(PCA9685Declares.MODE1_ADDR, newVal);
       this.logger.trace("<<< setSubAddr3");
    }

    public void enablePCA(boolean enable) {
       this.logger.trace(">>> enablePCA {}", enable);
        if (enable) {
            this.oePin.low();
        } else {
            this.oePin.high();
        }
       this.logger.trace("<<< enablePCA");
    }

    public void setLedIntensity(int devAddr, int ledNum, int intensity) {
        String formattedString = String.format(">>> setLedIntensity DevAddr %x  led %d intensity  %d", devAddr, ledNum, intensity);
       this.logger.trace(formattedString);

        if (intensity >= 0xFFF) {
            // "fully on":
            this.setLedOn(devAddr, ledNum, 0x1000, 0);
        } else if (intensity < 0x0010) {
            // case of "fullyoff"
            this.setLedOn(devAddr, ledNum, 0, 0x1000);
        } else {
            intensity = intensity >> 4;
         //   this.setLedOn(devAddr, ledNum, (intensity & 0x1f) >> 8,(intensity& 0xff0));
            this.setLedOn(devAddr, ledNum,  8, intensity);

        }
    }


    public void setLedOn(int devAddr, int ledNum, int ledOn, int ledOff) {
        String formattedString = String.format(">>> setLedOn DevAddr %x  led %d time ON %d, led OFF %d", devAddr, ledNum, ledOn, ledOff);
       this.logger.trace(formattedString);
        I2C tempDeviceOn = this.device; //null;
        if (tempDeviceOn == null) {
            I2CProvider i2CProvider = this.pi4j.provider("linuxfs-i2c");
            I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                .id("setLedOn_" + devAddr + "_" + ledNum)
                .bus(this.bus)
                .device(devAddr) //Pca9685Declares.CONFIG_ADDRESS)
                .build();
            //  tempDeviceOn = i2CProvider.create(i2cConfig);
        }
        byte[] data = new byte[4];
        data[0] = (byte) (ledOn & 0x00ff);
        data[1] = (byte) ((ledOn & 0x1f00) >> 8);
        data[2] = (byte) (ledOff & 0x00ff);
        data[3] = (byte) ((ledOff & 0x1f00) >> 8);
        tempDeviceOn.writeRegister(PCA9685Declares.LED0_ON_L + (ledNum * 4), data);
    }

    public void setMode1(int newModeVal) {
        var presentValue = this.device.readRegister(PCA9685Declares.MODE1_ADDR);
        this.device.writeRegister(PCA9685Declares.MODE1_ADDR, newModeVal);
        String formattedString = String.format(">>> setMode1 old value %x  ,newValue %x", presentValue, newModeVal);
       this.logger.debug(formattedString);
    }

    public void setMode2(int newModeVal) {
        var presentValue = this.device.readRegister(PCA9685Declares.MODE2_ADDR);
        this.device.writeRegister(PCA9685Declares.MODE2_ADDR, newModeVal);
        String formattedString = String.format(">>> setMode1 old value %x  ,newValue %x", presentValue, newModeVal);
       this.logger.debug(formattedString);
    }

        public void showMode1() {
        var presentValue = this.device.readRegister(PCA9685Declares.MODE1_ADDR);
        String formattedString = String.format(">>> showMode1  %x", presentValue);
       this.logger.debug(formattedString);
        formattedString = "";
        if ((presentValue & 0x1) == 0) {
            formattedString = formattedString.concat("\nbit 0 allCall no\n");
        } else {
            formattedString = formattedString.concat("\nbit 0 allCall yes\n");
        }

        if ((presentValue & 0x2) == 0) {
            formattedString = formattedString.concat("bit 1 subaddress3response  no\n");
        } else {
            formattedString = formattedString.concat("bit 1 subaddress3 response  yes\n");
        }

        if ((presentValue & 0x4) == 0) {
            formattedString = formattedString.concat("bit 2 subaddress2 response  no\n");
        } else {
            formattedString = formattedString.concat("bit 2 subaddress2 response  yes\n");
        }
        if ((presentValue & 0x8) == 0) {
            formattedString = formattedString.concat("bit 3 subaddress1 response  no\n");
        } else {
            formattedString = formattedString.concat("bit 3 3subaddress1 response  yes\n");
        }
        if ((presentValue & 0x10) == 0) {
            formattedString = formattedString.concat("bit 4 normal mode  \n");
        } else {
            formattedString = formattedString.concat("bit 4 low power mode response \n");
        }
        if ((presentValue & 0x20) == 0) {
            formattedString = formattedString.concat("bit 5 auto INC disabled  \n");
        } else {
            formattedString = formattedString.concat("bit 5 auto INC enabled \n");
        }
        if ((presentValue & 0x40) == 0) {
            formattedString = formattedString.concat("bit 6 internal clock  \n");
        } else {
            formattedString = formattedString.concat("bit 6 EXTCLK \n");
        }
        if ((presentValue & 0x80) == 0) {
            formattedString = formattedString.concat("bit 7 restart disabled  \n");
        } else {
            formattedString = formattedString.concat("bit 7 restart enabled \n");
        }
       this.logger.debug(formattedString);


    }

        public void showMode2() {
        var presentValue = this.device.readRegister(PCA9685Declares.MODE2_ADDR);
        String formattedString = String.format(">>> showMode2  %x", presentValue);
       this.logger.debug(formattedString);
        formattedString = "";
        if ((presentValue & 0x1) == 0) {
            formattedString = formattedString.concat("\nbit 0 OUTNE[1:0]  0\n");
        } else {
            formattedString = formattedString.concat("\nbit 0 OUTNE[1:0]  1\n");
        }

        if ((presentValue & 0x2) == 0) {
            formattedString = formattedString.concat("bit 1 OUTNE[1:0]  0\n");
        } else {
            formattedString = formattedString.concat("bit 1 OUTNE[1:0]  0\n");
        }

        if ((presentValue & 0x4) == 0) {
            formattedString = formattedString.concat("bit 2 open drain \n");
        } else {
            formattedString = formattedString.concat("bit 2 totem \n");
        }
        if ((presentValue & 0x8) == 0) {
            formattedString = formattedString.concat("bit 3 change on STOP  \n");
        } else {
            formattedString = formattedString.concat("bit 3 change on Ack \n");
        }
        if ((presentValue & 0x10) == 0) {
            formattedString = formattedString.concat("bit 4 output NOT inverted  \n");
        } else {
            formattedString = formattedString.concat("bit 4 output inverted \n");
        }
       this.logger.debug(formattedString);
    }

    public void showLedOnOff(int ledNum) {
        String formattedString = String.format(">>> showLedOnOff  led %d", ledNum);
       this.logger.debug(formattedString);

        int ledValue = this.device.readRegister(PCA9685Declares.LED0_ON_L + (ledNum * 4));
        formattedString = String.format(" led%d_ON_L    %x", ledNum, ledValue);
       this.logger.debug(formattedString);
        ledValue = this.device.readRegister(PCA9685Declares.LED0_ON_H + (ledNum * 4));
        formattedString = String.format(" led%d_ON_H    %x", ledNum, ledValue);
       this.logger.debug(formattedString);
        ledValue = this.device.readRegister(PCA9685Declares.LED0_OFF_L + (ledNum * 4));
        formattedString = String.format(" led%d_OFF_L   %x", ledNum, ledValue);
       this.logger.debug(formattedString);
        ledValue = this.device.readRegister(PCA9685Declares.LED0_OFF_H + (ledNum * 4));
        formattedString = String.format(" led%d_OFF_H   %x", ledNum, ledValue);
       this.logger.debug(formattedString);
    }


    public void showPrescale() {
        var prescale = this.device.readRegister(PCA9685Declares.PRE_SCALE);

        String formattedString = String.format(">>> showPrescale    %d", prescale);
        this.logger.debug(formattedString);

    }
}
