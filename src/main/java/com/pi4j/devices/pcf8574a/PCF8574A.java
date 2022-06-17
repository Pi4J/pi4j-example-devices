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

package com.pi4j.devices.pcf8574a;

import com.pi4j.context.Context;
import com.pi4j.devices.hd44780u.HD44780U;
import com.pi4j.devices.lcd1602a.LCD1602A;
import com.pi4j.devices.lcd1602a.LCD1602A_Declares;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;
import org.slf4j.LoggerFactory;


public class PCF8574A extends LCD1602A {

    private I2C pcfDev = null;
    private int busNum;
    private int address;

    private int ctrlData;


    public PCF8574A(Context pi4j, Console console, boolean clearIt, String traceLevel, int bus, int address) {
        super(pi4j, console, clearIt, traceLevel);
        this.busNum = bus;
        this.address = address;
        this.ctrlData = 0x00;

        this.init();
    }


    public void init() {
        System.setProperty("org.slf4j.simpleLogger.log." + PCF8574A.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(PCF8574A.class);

        super.init();
        this.logger.trace(">>>Enter init   Bus  : " + this.busNum + "  address  " + this.address);
        this.createI2cDevice();
// blahhhhh

        /*
        this.writeToDev(0x00); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();
        this.writeToDev(0x10); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();
        this.writeToDev(0x00); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();
        this.writeToDev(0x00); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();
        this.writeToDev(0x10); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();








        this.writeToDev(0x30); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();

        this.writeToDev(0x30); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();

        this.writeToDev(0x30); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();

        this.writeToDev(0x20); // FUNC  but accepted as 8bit op
        this.sleepTimeMilliS(15);
        this.pulseEnable();






        this.sendCommand(0x28);
        this.sleepTimeMilliS(15);
        this.sendCommand(0x0c);
        this.sleepTimeMilliS(15);
        this.sendCommand(0x01);
        this.sleepTimeMilliS(15);
        this.sendCommand(0x06);
        this.sleepTimeMilliS(10);

        this.sendChar('B');

*/



        //this.setRWLow();

/*
        this.sendCommand(0x20); // FUNC--4-bit
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
       // this.pulseEnable();
//        this.sendCommand(0x00); // 1 line , 5x8
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
      //  this.pulseEnable();

        this.sendCommand(0x0e); // Display  01
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
     //   this.pulseEnable();
//        this.sendCommand(0x0e); // ON and  cursor
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
     //   this.pulseEnable();


        this.sendCommand(0x16); // entry mode   00
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
     //   this.pulseEnable();
        //this.sendCommand(0x06); // INC to right
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
     //   this.pulseEnable();

*/


//        this.sendChar('a');


       // this.setEnLow();
       // this.setRWLow();
        this.writeToDev(0x20); // FUNC 4-bit, but accepted as 8bit op

        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);

        this.sendCommand(LCD1602A_Declares.funcSetCMD | LCD1602A_Declares.func4BitsBit); // | LCD1602A_Declares.func5x8TwoBit );
        // enable LCD with blink
        this.sendCommand(LCD1602A_Declares.dispCMD | LCD1602A_Declares.dispOnBit | LCD1602A_Declares.dispCrsOnBit);   // | LCD1602A_Declares.dispBlnkOnBit

         // entry mode, cursor moves right each character
        this.sendCommand(LCD1602A_Declares.entryModeCMD | LCD1602A_Declares.entryModeIncCMD);


        if (this.clearDisplay) {
            this.logger.trace("Clear Display");
            this.clearDisplay();
        }

        this.sendChar('H');

        this.logger.trace("<<< Exit: init  device  ");
    }



    private void writeToDev(int data){
        this.logger.trace(">>> Enter: writeToDev  data: "+ String.format("%02x ", data) );

        String logData = "";
        logData += " \n    P7-DB7: "+ ((data >> 7) & 0x1) +   " P6-DB6: "+ ((data >> 6) & 0x1) + " P5-DB5: "+ ((data >> 5) & 0x1) + "  P4-DB4: "  + ((data >> 4) & 0x1) +
                "\n  EN: " + ((data >> 2) & 0x1)  + " RW: "  + ((data>>1) & 0x1) +    " RS: "  + ((data) & 0x1) +  "\n  Data : " + String.format("0X%02x: ",((data >> 4 ) &0xf));
        this.logger.trace(logData);
        int rc = this.pcfDev.write(data);
        this.logger.trace("Exit: writeToDev  RC : "  + rc);
    }

    private void setEnLow() {
        this.logger.trace(">>> Enter: setEnLow");
        this.ctrlData &= PCF8574A_Declares.E_bit_mask_off;
        this.ctrlData |= PCF8574A_Declares.E_low;
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setEnLow");
    }


    private void setEnHigh() {
        this.logger.trace(">>> Enter: setEnHigh");
        this.ctrlData &= PCF8574A_Declares.E_bit_mask_off;
        this.ctrlData |= PCF8574A_Declares.E_high;
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setEnHigh");
    }


    private void setRSLow() {
        this.logger.trace(">>> Enter: setRSLow");
        this.ctrlData &= PCF8574A_Declares.RS_bit_mask_off;
        this.ctrlData |= PCF8574A_Declares.RS_low;
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setRSLow");
    }


    private void setRSHigh() {
        this.logger.trace(">>> Enter: setRSHigh");
        this.ctrlData &= PCF8574A_Declares.RS_bit_mask_off;
        this.ctrlData |= PCF8574A_Declares.RS_high;
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setRSHigh");
    }

    private void setRWLow() {
        this.logger.trace(">>> Enter: setRWLow");
        this.ctrlData &= PCF8574A_Declares.RW_bit_mask_off;
        this.ctrlData |= PCF8574A_Declares.RW_low;
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setRWLow");
    }


    private void setRWHigh() {
        this.logger.trace(">>> Enter: setRWHigh");
        this.ctrlData &= PCF8574A_Declares.RW_bit_mask_off;
        this.ctrlData |= PCF8574A_Declares.RW_high;
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.writeToDev(this.ctrlData);
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setRWHigh");
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
                .provider("linuxfs-i2c")
                .build();
        this.pcfDev = this.pi4j.create(i2cDeviceConfig);
        this.logger.trace("<<< Exit:createI2cDevice  ");
    }


    protected void sendChar(char c) {
        this.logger.trace(">>> Enter: sendChar   : " +  c + String.format("    0X%02x: ", (int)c));
        this.ctrlData &= 0x0f;    // only include control bits, no char data

        this.ctrlData = this.ctrlData | ((c )  & 0xf0);

        if (this.lcdAvailable()) {
            this.setRSHigh();
            this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
            this.writeToDev(this.ctrlData);
            this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
            this.pulseEnable();
            this.ctrlData &= 0x0f;    // only include control bits, no data
            this.ctrlData = this.ctrlData | ((c << 4 )& 0xf0);
            this.writeToDev(this.ctrlData);
            this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
            this.pulseEnable();
        } else {
            this.logger.trace("LCD in busy state, request not possible");
        }
        this.logger.trace("<<<  Exit: sendChar  ");
    }

    // do required gpio->LCD_input dance before and after actual LCD pin update
    protected void sendCommand(int cmd) {
        this.logger.trace(">>> Enter: sendCommand  : " + String.format("0X%02x: ",cmd));
        this.ctrlData &= 0x0f;    // only include control bits, no data

        this.ctrlData = this.ctrlData | ((cmd )  & 0xf0);

        if (this.lcdAvailable()) {
            this.setRSLow();
            this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
            this.writeToDev(this.ctrlData);
            this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
            this.pulseEnable();
            this.ctrlData &= 0x0f;    // only include control bits, no data
            this.ctrlData = this.ctrlData | ((cmd << 4 )& 0xf0);
            this.writeToDev(this.ctrlData);
            this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
            this.pulseEnable();
        } else {
            this.logger.trace("LCD in busy state, request not possible");
        }
        this.logger.trace("<<< Exit: sendCommand   ");
    }


    /**
     * Value of 0 indicates the device is not performing internal
     * operations and will accept commands
     * Not possible with the Pi GPIOs. Later if the D0_D7 interface is on an
     * MCP230xx, this maybe possible.  For present time, use timing values
     * documented in datasheet
     *
     * @return bit value of DB7
     */


    protected void pulseEnable() {
        this.logger.trace(">>> Enter: pulseEnable  ");
        this.setEnHigh();
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.setEnLow();
        this.sleepTimeMilliS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: pulseEnable  ");
    }


}
