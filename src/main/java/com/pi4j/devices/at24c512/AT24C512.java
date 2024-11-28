/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  AT24C512.java
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

package com.pi4j.devices.at24c512;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AT24C512 {

    /**
     * Constant <code>NAME="AT24C512"</code>
     */
    public static final String NAME = "AT24C512";
    /**
     * Constant <code>ID="AT24C512"</code>
     */
    public static final String ID = "AT24C512";


    // I2C Provider name and unique ID
    /**
     * Constant <code>I2C_PROVIDER_NAME="NAME +  I2C Provider"</code>
     */
    public static final String I2C_PROVIDER_NAME = NAME + " AT24C512 I2C Provider";
    /**
     * Constant <code>I2C_PROVIDER_ID="ID + -i2c"</code>
     */
    public static final String I2C_PROVIDER_ID = ID + "-i2c";


    private final Logger logger;
    private final String traceLevel;


    // local/internal I2C reference for communication with hardware chip
    protected I2C i2c = null;

    protected I2CConfig config = null;

    protected Context pi4j = null;

    protected Console console = null;

    protected int busNum;
    protected int address;

    /**
     * @param console Context instance used accross application
     * @param bus     Pi bus
     * @param address Device address
     */

    /**
     * @param console    Context instance used across application
     * @param bus        Pi bus
     * @param address    Device address
     * @param traceLevel for Logger
     */
    public AT24C512(Context pi4j, Console console, int bus, int address, String traceLevel) {
        super();
        this.pi4j = pi4j;
        this.address = address;
        this.busNum = bus;
        this.console = console;
        this.traceLevel = traceLevel;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + AT24C512.class.getName(), this.traceLevel);

        this.logger = LoggerFactory.getLogger(AT24C512.class);
        this.createI2cDevice(); // will set start this.i2c
    }


    /**
     * @param console Context instance used across application
     * @param bus     Pi bus
     * @param address Device address
     * @param logger  Instantiated Logger
     */
    public AT24C512(Context pi4j, Console console, int bus, int address, Logger logger) {
        super();
        this.pi4j = pi4j;
        this.address = address;
        this.busNum = bus;
        this.console = console;
        this.traceLevel = "info";  // we were passed the Logger to use
        this.logger = logger;
        this.createI2cDevice(); // will set start this.i2c
    }

    /**
     * @param device Set i2c state
     */
    public void setI2c(I2C device) {
        this.logger.trace(">>> Enter: setI2c  I2C device   " + device.toString());
        this.i2c = device;
        this.address = device.device();
        this.busNum = device.bus();
        this.logger.trace("<<< exit: setI2c  ");
    }

    /**
     * @return i2c state
     */
    public I2C getI2c() {
        this.logger.trace(">>> Enter: GetI2c ");
        this.logger.trace("<<< Exit: getI2c  I2C device   " + this.i2c);
        return (this.i2c);
    }


    /**
     * Use the state from the Sensor config object and the state pi4j to create
     * a AT24C512 device instance
     */
    private void createI2cDevice() {
        this.logger.trace(">>> Enter:createI2cDevice   bus  " + this.busNum + "  address " + this.address);

        var address = this.address;
        var bus = this.busNum;

        String id = String.format("Bus: 0X%02x: ", bus);
        String name = String.format("Address: 0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
            .bus(bus)
            .device(address)
            .name(name)
            .provider("linuxfs-i2c")  /* "linuxfs-i2c")  "linuxfs-i2c") */
            .build();
        this.config = i2cDeviceConfig;
        this.i2c = this.pi4j.create(i2cDeviceConfig);

        this.logger.trace("<<< Exit:createI2cDevice  ");
    }


    /**
     * @return string containing a desription of the attached I2C path
     */
    public String i2cDetail() {
        this.logger.trace(">>> enter: i2cDetail");
        this.logger.trace("<<< exit: i2cDetail  " + (this.i2c.toString() + " bus : " + this.config.bus() + "  address : " + this.config.device()));
        return (this.i2c.toString() + " bus : " + this.config.bus() + "  address : " + this.config.device());
    }


    public byte[] readCurrentAddrEEPROM(int numBytes) {
        this.logger.trace(">>> enter: readCurrentAddrEEPROM   numByte : " + numBytes);
        byte[] rData = new byte[numBytes];
        int rc = 0;
        String details = "\n     0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f \n";
        details = details + String.format("     %02x: ", 0);
        byte[] compVal = new byte[numBytes];

        for (int i = 0; i < numBytes; i++) {
            rData[i] = (byte) this.i2c.read();// .readRegister((int) register);
            details = details + String.format("%02x ", rData[i]) + " ";
            if (((i > 0) && ((i + 1) % 16) == 0) || (i == 15)) {
                details = details + "\n";
                details = details + String.format(" %02x: ", i + 1);
            }
        }

        rc = rData.length;
        this.logger.trace("read  data :" + details
            + "\n  rc: " + String.format("0X%02x: ", rc) + "\n");

        this.logger.trace("<<< Exit: readCurrentAddrEEPROM numByte read : " + rc);
        return (rData);
    }


    public byte[] readEEPROM(long register, int numBytes) {
        this.logger.trace(">>> enter: readEEPROM register " + String.format("%04x ", register) + " numByte : " + numBytes);
        byte[] rData = new byte[numBytes];

        int rc = 0;
//        int rc = this.i2c.readRegister((int) ((register&0xff00) >> 8),rData, numBytes);
        String details = "\n     0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f \n";
        details = details + String.format(" %02x: ", 0);
        // rData[i] = this.i2c.readRegisterByte(i);
        byte[] compVal = new byte[numBytes];

        // this one needs to handle multibyte reg value
        byte[] regByte = new byte[2]; // This chip is two byte register address
        regByte[0] = (byte) ((register & 0xff00) >> 8);
        regByte[1] = (byte) (register & 0xff);
        rc = this.i2c.readRegister(regByte, rData);
        for (int i = 0; i < numBytes; i++) {
            details = details + String.format("%02x ", rData[i]) + " ";
            if (((i > 0) && ((i + 1) % 16) == 0) || (i == 15)) {
                details = details + "\n";
                details = details + String.format(" %02x: ", i + 1);
            }
        }
        rc = rData.length;
        // rc =  this.i2c.readRegister((int) register, rData, 0, numBytes);
        this.logger.trace("readRegister  data :" + details
            + "\n  rc: " + String.format("0X%02x: ", rc) + "\n");

        this.logger.trace("<<< Exit: readEEPROM numByte read : " + rc);
        return (rData);
    }


    public int writeEEPROM(long register, int numBytes, byte[] wData) {
        this.logger.trace(">>> enter: writeEEPROM register :" + String.format("%04x ", register) + " numBytes " + numBytes);

        // needs to handle multibyte reg value
        byte[] regByte = new byte[2]; // This chip is two byte register address
        regByte[0] = (byte) ((register & 0xff00) >> 8);
        regByte[1] = (byte) (register & 0xff);

        int rc = this.i2c.writeRegister(regByte, wData, numBytes);
        // TODO use rc OR numBytes in loop ???
        var details = "\n      0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f \n";
        details = details + String.format(" %02x:  ", 0);
        for (int i = 0; i < numBytes; i++) {
            details = details + String.format("%02x ", wData[i]) + " ";
            if (((i > 0) && ((i + 1) % 16) == 0) || (i == 15)) {
                details = details + "\n";
                details = details + String.format(" %02x: ", i + 1);
            }
        }
        this.logger.trace("writeRegister  data :" + details
            + "\n  rc: " + String.format("0X%02x: ", rc) + "\n");

        this.logger.trace("<<< Exit: writeEEPROM numBytes written numBytes written regAddr + data :" + rc);
        return (rc);
    }


}
