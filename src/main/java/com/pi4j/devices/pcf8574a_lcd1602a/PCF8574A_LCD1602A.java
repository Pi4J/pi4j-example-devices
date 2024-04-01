/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  PCF8574A.java
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

package com.pi4j.devices.pcf8574a_lcd1602a;

import com.pi4j.context.Context;
import com.pi4j.devices.lcd1602a.LCD1602A;
import com.pi4j.devices.lcd1602a.LCD1602A_Declares;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;
import org.slf4j.LoggerFactory;


public class PCF8574A_LCD1602A extends LCD1602A {

    private I2C pcfDev = null;
    private int busNum;
    private int address;



    public PCF8574A_LCD1602A(Context pi4j, Console console,  String traceLevel, int bus, int address) {
        super(pi4j, console, false, traceLevel);
        this.busNum = bus;
        this.address = address;

        this.init();
    }


    /**
     * Configure I2C LCD display
     */
    public void init() {
        System.setProperty("org.slf4j.simpleLogger.log." + PCF8574A_LCD1602A.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(PCF8574A_LCD1602A.class);

        super.init();
        this.logger.trace(">>>Enter init   Bus  : " + this.busNum + "  address  " + this.address);
        this.createI2cDevice();


        this.sendCommand((byte) 0x3);
        this.sendCommand((byte) 0x3);
        this.sendCommand((byte) 0x3);
        this.sendCommand((byte) 0x2);

        this.sendCommand(LCD1602A_Declares.funcSetCMD | LCD1602A_Declares.func4BitsBit  | LCD1602A_Declares.func5x8TwoBit  );
        // enable LCD with blink
        this.sendCommand(LCD1602A_Declares.dispCMD | LCD1602A_Declares.dispOnBit   ); //| LCD1602A_Declares.dispCrsOnBit
        this.sendCommand( LCD1602A_Declares.clearDispCMD);
         // entry mode, cursor moves right each character
        this.sendCommand(LCD1602A_Declares.entryModeCMD | LCD1602A_Declares.entryModeIncCMD);

        this.writeToDev((byte) LCD1602A_Declares.dispCMD);

        this.sleepTimeMicroS(LCD1602A_Declares.postWrtEnableCycleDelay);

        this.logger.trace("<<< Exit: init  device  ");
    }


    /**
     * Write byte to actual device I2C interface
      * @param data
     */
    private void writeToDev(byte data){
        data |=   PCF8574A_Declares_LCD1602A.backlight_on;
        this.logger.trace(">>> Enter: writeToDev  data: "+ String.format("%02x ", data) );

        String logData = "";
        logData += " \n    P7-DB7: "+ ((data >> 7) & 0x1) +   " P6-DB6: "+ ((data >> 6) & 0x1) + " P5-DB5: "+ ((data >> 5) & 0x1) + "  P4-DB4: "  + ((data >> 4) & 0x1) +
                "\n    BackLight: " +   ((data >> 3) & 0x1)  +  "  EN: " + ((data >> 2) & 0x1)  + " RW: "  + ((data>>1) & 0x1) +    " RS: "  + ((data) & 0x1) +  "\n  Data : " + String.format("0X%02x: ",((data >> 4 ) &0xf));
        this.logger.trace(logData);
        int rc = this.pcfDev.write(data);
        this.sleepTimeMicroS(LCD1602A_Declares.preAddressWrtSetupDelay*2);

        this.logger.trace("Exit: writeToDev  RC : "  + rc);
    }

    /**
     *  Set EN bit low
     * @param b byte
     * @return  modified byte
     */
    private byte setEnLow(byte b) {
        this.logger.trace(">>> Enter: setEnLow");
        b &= PCF8574A_Declares_LCD1602A.E_bit_mask_off;
        b |= PCF8574A_Declares_LCD1602A.E_low;
        this.logger.trace("<<< Exit: setEnLow");
        return (b);
    }


    /**
     *  Set EN bit high
     * @param b byte
     * @return  modified byte
     */
    private byte setEnHigh(byte b) {
        this.logger.trace(">>> Enter: setEnHigh");
        b  &= PCF8574A_Declares_LCD1602A.E_bit_mask_off;
        b |= PCF8574A_Declares_LCD1602A.E_high;
        this.logger.trace("<<< Exit: setEnHigh");
        return(b);
    }


    /**
     *  Set RS bit low
     * @param b byte
     * @return  modified byte
     */
    private byte setRSLow(byte b) {
        this.logger.trace(">>> Enter: setRSLow");
        b  &= PCF8574A_Declares_LCD1602A.RS_bit_mask_off;
        b |= PCF8574A_Declares_LCD1602A.RS_low;
        this.logger.trace("<<< Exit: setRSLow");
        return(b);
    }


    /**
     *  Set RS bit high
     * @param b byte
     * @return  modified byte
     */
    private  byte setRSHigh(byte b) {
        this.logger.trace(">>> Enter: setRSHigh");
        b  &= PCF8574A_Declares_LCD1602A.RS_bit_mask_off;
        b  |= PCF8574A_Declares_LCD1602A.RS_high;
        this.logger.trace("<<< Exit: setRSHigh");
        return(b);
    }

    /**
     *  Set RW bit low
     * @param b byte
     * @return  modified byte
     */
    private byte setRWLow(byte b) {
        this.logger.trace(">>> Enter: setRWLow");
        b  &= PCF8574A_Declares_LCD1602A.RW_bit_mask_off;
        b  |= PCF8574A_Declares_LCD1602A.RW_low;
        this.logger.trace("<<< Exit: setRWLow");
        return(b);
    }


    /**
     *  Set RW bit high
     * @param b byte
     * @return  modified byte
     */
    private byte setRWHigh(byte b) {
        this.logger.trace(">>> Enter: setRWHigh");
        b  &= PCF8574A_Declares_LCD1602A.RW_bit_mask_off;
        b  |= PCF8574A_Declares_LCD1602A.RW_high;
        this.logger.trace("<<< Exit: setRWHigh");
        return(b);
    }
    private void createI2cDevice() {
        this.logger.trace(">>> Enter:createI2cDevice   bus  " + this.busNum + "  address " + this.address);

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
        this.pcfDev = this.pi4j.create(i2cDeviceConfig);
        this.logger.trace("<<< Exit:createI2cDevice  ");
    }

    /**
     * Write byte to device P4-P7, then pulse EN pin so P4-P7 are read into LCD
     * @param b
     */
    protected void writeFourBits(byte b) {
        this.logger.trace(">>> Enter: writeFourBits   : " +  b + String.format("    0X%02x: ", (int)b));
        this.writeToDev((byte) (b));
        this.pulseEnable(b);
        this.logger.trace("<<<  Exit: writeFourBits  ");
    }




    /**
     * Set RS bit high and write high then low nibble to device
     * @param data
     */
    protected void sendChar(char data) {
        this.logger.trace(">>> Enter: sendChar   : " +  data + String.format("    0X%02x: ", (int)data));

        if (this.lcdAvailable()) {
            byte c= this.setRSHigh((byte) (data & 0xf0));
            this.writeFourBits(c);
            c = this.setRSHigh((byte)((data << 4) & 0xF0));
            this.writeFourBits(c);
        } else {
            this.logger.trace("LCD in busy state, request not possible");
        }
        this.logger.trace("<<<  Exit: sendChar  ");
    }

    // do required gpio->LCD_input dance before and after actual LCD pin update

    /**
     * Set RS bit low and write high then low nibble to device
     * @param data
     */
    protected void sendCommand(int data) {
        this.logger.trace(">>> Enter: sendCommand  : " + String.format("0X%02x: ", data));
        if (this.lcdAvailable()) {
            byte cmd = this.setRSLow((byte) (data & 0xf0));
            this.writeFourBits(cmd);
            cmd = this.setRSLow((byte) ((data << 4) & 0xF0));
            this.writeFourBits(cmd);
        } else {
            this.logger.trace("LCD in busy state, request not possible");
        }
        this.logger.trace("<<< Exit: sendCommand   ");
    }


    /**
     * Modify data byte to set EN bit, write to device.
     *  Wait
     * Clear bit and write to device
     *
     * @param b
     *
     */

    protected void pulseEnable(byte b){
        this.logger.trace(">>> Enter: pulseEnable  ");
        b = this.setEnHigh(b);
        this.writeToDev(b);
        this.sleepTimeMicroS(LCD1602A_Declares.postWrtEnableCycleDelay);
        b = this.setEnLow(b);
        this.writeToDev(b);
        this.sleepTimeMicroS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: pulseEnable  ");
    }


}
