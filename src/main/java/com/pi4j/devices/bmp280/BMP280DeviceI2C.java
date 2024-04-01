/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  BMP280DeviceI2C.java
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

package com.pi4j.devices.bmp280;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BMP280DeviceI2C extends BMP280Device{

    protected int busNum = BMP280Declares.DEFAULT_BUS;
    protected int address = BMP280Declares.DEFAULT_ADDRESS;
    // local/internal I2C reference for communication with hardware chip
    protected I2C i2c = null;

    protected I2CConfig config = null;
    // I2C Provider name and unique ID
    /**
     * Constant <code>I2C_PROVIDER_NAME="NAME +  I2C Provider"</code>
     */
    public static final String I2C_PROVIDER_NAME = NAME + " BMP280 I2C Provider";
    /**
     * Constant <code>I2C_PROVIDER_ID="ID + -i2c"</code>
     */
    public static final String I2C_PROVIDER_ID = ID + "-i2c";


    public BMP280DeviceI2C(Context pi4j, Console console, int bus, int address, String traceLevel) {
        super(pi4j, console, traceLevel);
        this.pi4j = pi4j;
        this.address = address;
        this.busNum = bus;
        this.console = console;
       // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + BMP280DeviceI2C.class.getName(), traceLevel);

        this.logger = LoggerFactory.getLogger(BMP280DeviceI2C.class);
        this.createI2cDevice(); // will set start this.i2c
    }

        /**
          * @param console Context instance used across application
          * @param bus     Pi bus
          * @param address Device address
          * @param logger  Instantiated Logger
          */
      public BMP280DeviceI2C(Context pi4j, Console console, int bus, int address, Logger logger) {
                super(pi4j, console, "info");
                this.address = address;
                this.busNum = bus;
                this.logger = logger;
                this.createI2cDevice(); // will set start this.i2c
         }

    /**
     * @param device Set i2c state
     */
    public void setI2c(I2C device) {
        this.logger.info("Enter: setI2c  I2C device   " + device.toString());
        this.i2c = device;
        this.address = device.device();
        this.busNum = device.bus();
        this.logger.info("exit: setI2c  ");
    }

    /**
     * @return i2c state
     */
    public I2C getI2c() {
        this.logger.info("Enter: GetI2c ");
        this.logger.info("Exit: getI2c  I2C device   " + this.i2c);
        return (this.i2c);
    }


    /**
     * Use the state from the Sensor config object and the state pi4j to create
     * a BMP280 device instance
     */
    private void createI2cDevice() {
        this.logger.info("Enter:createI2cDevice   bus  " + this.busNum + "  address " + this.address);

        var address = this.address;
        var bus = this.busNum;

        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
                .bus(bus)
                .device(address)
                .id(id + " " + name)
                .name(name)
                .provider("linuxfsi2c")
                .build();
        this.config = i2cDeviceConfig;
        this.i2c = this.pi4j.create(i2cDeviceConfig);
        this.logger.info("Exit:createI2cDevice  ");
    }


    /**
     * @return string containing a description of the attached I2C path
     */
    public String i2cDetail() {
        this.logger.trace("enter: i2cDetail");
        this.logger.trace("exit: i2cDetail  " + (this.i2c.toString() + " bus : " + this.config.bus() + "  address : " + this.config.device()));
        return (this.i2c.toString() + " bus : " + this.config.bus() + "  address : " + this.config.device());
    }


    /**
     * @return The  device I2cConfig object
     */
    public I2CConfig config() {

        this.logger.trace("enter: config");
        this.logger.trace("exit: config  " + this.config.toString());
        return this.config;
    }

    /**
     *
     * @param   register
     * @return  8bit value read from register
     */
    public int readRegister(int register){
        int rval = 0;
        rval =this.i2c.readRegister(register);
        return(rval);

    }

    /**
     *
     * @param register   register address
     * @param buffer     Buffer to return read data
     * @return count     number bytes read or fail -1
     */
    public int readRegister(int register, byte[] buffer){
        int rval = 0;
        rval =this.i2c.readRegister(register, buffer);
        return(rval);
    }




    /**
     *
     * @param register  register
     * @param data      byte to write
     * @return bytes written, else -1
     */
    public int writeRegister(int register, int data){
        int rval = 0;
        rval = this.i2c.writeRegister(register, data);
        return(rval);
    }


}
