/*
 *
 * -
 *  * #%L
 *  * **********************************************************************
 *  * ORGANIZATION  :  Pi4J
 *  * PROJECT       :  Pi4J :: EXTENSION
 *  * FILENAME      :  BasicI2cDevice
 *  *
 *  * This file is part of the Pi4J project. More information about
 *  * this project can be found here:  https://pi4j.com/
 *  * **********************************************************************
 *  * %%
 *  * Copyright (C) 2012 - 2020 Pi4J
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 *
 *
 */

package com.pi4j.devices.base_i2c;

import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;

import java.io.IOException;

/**
 * BasicI2cDevice creates and uses i2cDeviceConfig for read and write operations.This class adds
 * one level to the read and write in order to create additional log entries to assist in debug.
 */
public class BasicI2cDevice {

    /**
     * I2C device CTOR
     * <p>
     * PreCond: BasicI2cDevice CTOR called with valid parameters
     * <ul>
     *     <li>Instantiated Context class
     *     <li> Instantiated FFDC class
     *     <li> Number of existing/functional Pi i2c bus
     *     <li> Address of i2c device connected to the bus identified by bus_num
     *     <li> Instantiated Console class
     * </ul>
     * <p>
     * PostCond:  Class Read and write methods are now accessable
     */
    public BasicI2cDevice(Context pi4j, FfdcUtil ffdc, int bus_num, int address, Console console) {
        this.ffdc = ffdc;
        this.bus_num = bus_num;
        this.address = address;
        this.console = console;
        this.pi4j = pi4j;
        this.init();
    }

    /**
     * I2C device init,
     * <p>
     * PreCond: BasicI2cDevice CTOR called with valid parameters
     * <p>
     * PostCond:  Class Read and write methods are now accessable
     */
    private void init() {
        try {
            this.i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
                    .bus(this.bus_num)
                    .device(this.address)
                    .id("ONE")
                    .name("TOF")
                    .provider("pigpio-i2c")
                    .build();
        } catch (Pi4JException e) {
            String details = String.format("new config create failed bus %s   address %s  ", String.format("0X%02x: ", this.bus_num), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            this.ffdc.ffdcErrorExit("i2C NEW CONFIG failed", 105);
        }
        try {
            this.i2cDevice = this.pi4j.create(this.i2cDeviceConfig);
        } catch (Exception e) {
            String details = String.format("device create failed bus %s   address %s  ", String.format("0X%02x: ", this.bus_num), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            this.ffdc.ffdcErrorExit("device create failed", 104);
            //e.printStackTrace();
        }
    }


    /**
     * I2C device access,read device register located at 'offset'.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     *
     * @param offset Device register
     *
     *               <p>
     *               PostCond:  Register contents returned if successful, else negative value
     */
    protected int readRegister(int offset) {
        int reg = 0;
        try {
            reg = this.i2cDevice.readRegister(offset);
            this.ffdc.ffdcDebugEntry("i2cRead :" + String.format("0X%02x: ", reg));
        } catch (IOException e) {
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            e.printStackTrace();
        }
        if (reg < 0) {
            String details = String.format("readRegister failed bus %s   address %s  ", String.format("0X%02x: ", this.bus_num), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
        }
        return (reg);
    }

    /**
     * I2C device access,read device register presently referenced in device control register.
     *
     *
     * <p>
     * Note: Repeated call to this same device instance will return the data determined by the
     * device auto-increment characteristic.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     *
     * <p>
     * PostCond:  Register contents returned if successful, else negative value
     */
    protected int read() {
        int reg = 0;
        try {
            reg = this.i2cDevice.read();
            this.ffdc.ffdcDebugEntry("i2cRead :" + String.format("0X%02x: ", reg));
        } catch (IOException e) {
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            e.printStackTrace();
        }
        if (reg < 0) {
            String details = String.format("read failed bus %s   address %s  ", String.format("0X%02x: ", this.bus_num), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
        }
        return (reg);
    }


    /**
     * I2C device access, write data to device register presently referenced in device control register.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     *
     * @param data Register updated with byte data
     *
     *             <p>
     *             PostCond:  0 returned if successful, else non-zero
     */
    protected int write(byte data) {
        int rval = 0;
        try {
            rval = this.i2cDevice.write(data);
            this.ffdc.ffdcDebugEntry("i2cwrite :" + String.format("0X%02x: ", data));
        } catch (IOException e) {
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            String details = String.format("write failed bus %s   address %s  ", String.format("0X%02x: ", this.bus_num), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
            e.printStackTrace();
        }
        return (rval);
    }

    /**
     * I2C device access, write data to device register presently referenced in device control register.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     *
     * @param data Register updated with least Significant byte written to
     *             register referenced in control register.
     *
     *             <p>
     *             PostCond:   0 returned if successful, else non-zero
     */
    protected int write(int data) {
        int rval = 0;
        try {
            rval = this.i2cDevice.write(data);
            this.ffdc.ffdcDebugEntry("i2cwrite :" + String.format("0X%02x: ", data));
        } catch (IOException e) {
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            String details = String.format("write failed bus %s   address %s  ", String.format("0X%02x: ", this.bus_num), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
            e.printStackTrace();
        }
        return (rval);
    }

    /**
     * Utility used by logging actions.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     * <p>
     * PostCond:  String containing the calling methods name.
     */
    protected String getMethodName() {
        return (new Throwable().getStackTrace()[1].getMethodName());
    }


    protected FfdcUtil ffdc = null;
    protected int bus_num = 0xff;
    protected int address = 0xff;
    protected Console console = null;
    protected Context pi4j = null;
    protected I2CConfig i2cDeviceConfig = null;
    protected I2C i2cDevice = null;


}
