/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  BasicI2cDevice.java
 *  *   *
 *  *   * This file is part of the Pi4J project. More information about
 *  *   * this project can be found here:  https://pi4j.com/
 *  *   * **********************************************************************
 *    * %%
 *  *   * Copyright (C) 2012 - 2021 Pi4J
 *     * %%
 *    * Licensed under the Apache License, Version 2.0 (the "License");
 *    * you may not use this file except in compliance with the License.
 *    * You may obtain a copy of the License at
 *    *
 *    *      http://www.apache.org/licenses/LICENSE-2.0
 *    *
 *    * Unless required by applicable law or agreed to in writing, software
 *    * distributed under the License is distributed on an "AS IS" BASIS,
 *    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    * See the License for the specific language governing permissions and
 *    * limitations under the License.
 *    * #L%
 *  *
 *  *
 *
 *
 */

package com.pi4j.devices.base_i2c;

import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.context.Context;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.exception.IOReadException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    public BasicI2cDevice(Context pi4j, FfdcUtil ffdc, int busNum, int address, Console console) {
        this.ffdc = ffdc;
        this.busNum = busNum;
        this.address = address;
        this.console = console;
        this.pi4j = pi4j;
        String details = String.format("BasicI2cDevice ctor bus %s   address %s  ", String.format("0X%02x: ", this.busNum), String.format("0X%02x: ", this.address));
        this.ffdc.ffdcDebugEntry(details);
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
            String id = String.format("0X%02x: ", this.busNum);
            String name = String.format("0X%02x: ", this.address);
            this.i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
                    .bus(this.busNum)
                    .device(this.address)
                    .id(id+" "+name)
                    .name(name)
                    .provider("pigpio-i2c")
                    .build();
        } catch (Pi4JException e) {
            String details = String.format("new config create failed bus %s   address %s  ", String.format("0X%02x: ", this.busNum), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorEntry(details);
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            this.ffdc.ffdcErrorExit("i2C NEW CONFIG failed", 105);
        }
        try {
            this.i2cDevice = this.pi4j.create(this.i2cDeviceConfig);
        } catch (Exception e) {
            this.ffdc.ffdcErrorEntry(e.getMessage() + " /n" + e.toString());
            String details = String.format("device create failed bus %s   address %s  ", String.format("0X%02x: ", this.busNum), String.format("0X%02x: ", this.address));
            this.ffdc.ffdcErrorExit(details, 104);
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
     *         PostCond:  Register contents returned if successful, else negative value
     */
    protected int readRegister(int offset) {
        int reg = 0;
        this.ffdc.ffdcMethodEntry("readRegister : offset " + String.format("0X%02x: ", offset) + " bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address));
        reg = this.i2cDevice.readRegister(offset);
        this.examineReturnRead(reg);
        this.ffdc.ffdcMethodExit("readRegister  data :" + String.format("0X%02x: ", reg));
        return (reg);
    }

    /**
     * I2C device access,read device register located at 'offset'.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     *
     * @param offset Device register
     *
     *               <p>
     *               PostCond:  Register contents returned if successful, else
     *               exception will surface
     *
     * @throws IOException, IOReadException
     */
    protected byte readRegisterByte(int offset) throws IOException, IOReadException {
        this.ffdc.ffdcMethodEntry("readRegisterBye : offset " + String.format("0X%02x: ", offset) + " bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address));
        byte reg = 0;
        reg = this.i2cDevice.readRegisterByte(offset);
        this.ffdc.ffdcMethodExit("readRegisterByte data :" + String.format("0X%02x: ", reg));
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
        this.ffdc.ffdcMethodEntry("read  bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address));
        int reg = 0;
        reg = this.i2cDevice.read();
        this.examineReturnRead(reg);
        this.ffdc.ffdcDebugEntry("read :" + String.format("0X%02x: ", reg));
        this.ffdc.ffdcMethodExit("read data:" + String.format("0X%02x: ", reg));
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
     * PostCond:  Register contents returned if successful, else exception will surface
     *
     * @throws IOException
     */
    protected byte readByte() throws IOException {
        this.ffdc.ffdcMethodEntry("readByte bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address));
        byte reg = 0;
        reg = this.i2cDevice.readByte();
        this.ffdc.ffdcDebugEntry("readByte  data :" + String.format("0X%02x: ", reg));
        return (reg);
    }

    /**
     *
     * @param register  offset into device
     * @param buffer    storage to contain contents read
     * @param bufferOffset    offset in buffer to place read data
     * @param length    length of data to read
     * @return          number bytes read else negative number
     */
    protected  int readRegister(int register, byte[] buffer, int bufferOffset, int length){
        this.ffdc.ffdcMethodEntry("readRegister bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address)
                + "  register:  " + String.format("0X%02x: ",bufferOffset)+ "  length:  " + String.format("0X%02x: ",length));
        int rc = 0;

        //  StandardCharsets.UTF_8,
        rc = this.i2cDevice.readRegister(register, buffer, bufferOffset, length);
        this.examineReturnRead(rc);
        var details = "\n     0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f \n";
        details = details + String.format("%02x: ", 0);
        for (int i = 0; i < rc; i++) {
            details = details + String.format("%02x ", buffer[i]) + " ";
            if ((i > 0) && ((i + 1) % 16) == 0) {
                details = details + "\n";
                details = details + String.format("%02x: ", i + 1);
            }
        }

        this.ffdc.ffdcDebugEntry("readRegister  data :" + details
                + "  rc: " + "\n" + String.format("0X%02x: ",rc));
        return(rc);
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
        this.ffdc.ffdcMethodEntry("write : data " + String.format("0X%02x: ", data) + " bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address));
        int rval = 0;
        rval = this.i2cDevice.write(data);
        this.examineReturnWrite(rval);
        this.ffdc.ffdcDebugEntry("write :" + String.format("0X%02x: ", data));
        this.ffdc.ffdcMethodExit("write rval :" + String.format("0X%02x: ", rval));
        return (rval);
    }
    /**
     * I2C device access, write data to device register to set offset, then write
     * data byte.
     * <p>
     * PreCond: BasicI2cDevice instance initialized.  See CTOR
     *
     * @param offset Device Register address
     * @param data Register updated with byte data
     *
     *             <p>
     *             PostCond:  0 returned if successful, else non-zero
     */
    protected int writeByte(int offset, byte data) {
        this.ffdc.ffdcMethodEntry("writeByte : offset  " + String.format("0X%02x: ", offset) + " data : " + String.format("0X%02x: ",data )  + " bus : " + String.format("0X%02x: ",this.busNum) + "  device address:  " + String.format("0X%02x: ",this.address));
        int rval = 0;
        rval = this.i2cDevice.writeRegister(offset, data);
        this.examineReturnWrite(rval);
        this.ffdc.ffdcDebugEntry("writeByte :" + String.format("0X%02x: ", data));
        this.ffdc.ffdcMethodExit("writeByte rval :" + String.format("0X%02x: ", rval));
        return (rval);
    }


    /**
     * examineReturnWrite.I2C write return code is analyzed.
     * <p>
     * PreCond: Device initialized.  See CTOR
     *
     * @param rval return value to examine
     *
     *             <p>
     *             PostCond:  If rval indicates success return true, else false
     */
    protected boolean examineReturnWrite(int rval) {
        this.ffdc.ffdcMethodEntry(this.getMethodName());
        boolean test = true;
        if (rval >= 0) {
            this.ffdc.ffdcDebugEntry("return value success");
        } else {
            test = false;
            this.ffdc.ffdcDebugEntry("return value Write failure");
            this.ffdc.ffdcErrorEntry("return value Write failure");
        }
        this.ffdc.ffdcMethodExit(this.getMethodName() + " examine results " + rval);
        return (test);
    }

    /**
     * examineReturnRead.I2C read return code is analyzed.
     * <p>
     * PreCond: Device instance initialized.  See CTOR
     *
     * @param rval return value to examine
     *
     *             <p>
     *             PostCond:  If rval indicates success return true, else false
     */
    protected boolean examineReturnRead(int rval) {
        this.ffdc.ffdcMethodEntry(this.getMethodName());
        boolean test = true;
        if (rval >= 0) {
            this.ffdc.ffdcDebugEntry("return value success");
        } else {
            test = false;
            this.ffdc.ffdcDebugEntry("return value Read failure");
            this.ffdc.ffdcErrorEntry("return value Read failure");
        }
        this.ffdc.ffdcMethodExit(this.getMethodName() + " examine results " + rval);
        return (test);
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


    protected FfdcUtil ffdc;
    protected int busNum;
    protected int address;
    protected Console console;
    protected Context pi4j;
    protected I2CConfig i2cDeviceConfig;
    protected I2C i2cDevice;


}
