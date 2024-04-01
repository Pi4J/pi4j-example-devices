/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  HD44780U.java
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

package com.pi4j.devices.hd44780u_lcd1602a;


import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;
import org.slf4j.LoggerFactory;
import com.pi4j.devices.lcd1602a.LCD1602A;
import com.pi4j.devices.lcd1602a.LCD1602A_Declares;

public class HD44780U_LCD1602A extends LCD1602A{


    private DigitalOutput RsPin = null;
    private DigitalOutput EnPin = null;   //

      private int RsPinNum = 0xff;
    private int EnPinNum = 0xff;

    private String traceLevel;


    private HD44780U_Interface_LCD1602A D0_D7;

    public HD44780U_LCD1602A(Context pi4j, Console console, HD44780U_Interface_LCD1602A d0_d7, int rsGpio, int enGpio, boolean clearIt, String traceLevel) {
        super(pi4j,  console,  clearIt, traceLevel);
        this.D0_D7 = d0_d7;
        this.RsPinNum = rsGpio;
        this.EnPinNum = enGpio;
        this.traceLevel = traceLevel;

        this.init();
    }


    public void init() {
        System.setProperty("org.slf4j.simpleLogger.log." + HD44780U_LCD1602A.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(LCD1602A.class);

        super.init();
        this.logger.trace("DR Pin  " + this.RsPinNum);
        this.logger.trace("EN Pin  " + this.EnPinNum);



        var outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
                .id("RS_pin")
                .name("Enable")
                .address(this.RsPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("linuxfs-digital-output");
        try {
            this.RsPin = pi4j.create(outputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut RS failed");
            System.exit(201);
        }

        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
                .id("EN_pin")
                .name("EN")
                .address(this.EnPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("linuxfs-digital-output");
        try {
            this.EnPin = pi4j.create(outputConfig3);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut EN failed");
            System.exit(203);
        }



        this.D0_D7.sendCommand(0x00); // ensure all pins are low

        this.EnPin.low();
        this.sleepTimeNanoS(LCD1602A_Declares.postWrtEnableCycleDelay);
          // enable LCD with blink
        this.sendCommand(LCD1602A_Declares.dispCMD | LCD1602A_Declares.dispOnBit| LCD1602A_Declares.dispBlnkOnBit | LCD1602A_Declares.dispCrsOnBit );
        // entry mode, cursor moves right each character

        this.sendCommand(LCD1602A_Declares.entryModeCMD | LCD1602A_Declares.entryModeIncCMD);

        this.sendCommand(LCD1602A_Declares.funcSetCMD | LCD1602A_Declares.func8BitsBit  | LCD1602A_Declares.func5x8TwoBit);
        if(this.clearDisplay){
            this.logger.trace("Clear Display");
            this.clearDisplay();
        }

        this.pulseEnable();

        this.logger.trace("<<< Exit: init  device  ");
    }








    protected void sendChar(char c) {
         this.logger.trace(">>> Enter: sendChar   : " + c );
        if (this.lcdAvailable()) {
            this.RsPin.high();
             this.sleepTimeNanoS(LCD1602A_Declares.dataWrtSetupDuration);
            this.D0_D7.sendCommand(c);
            this.sleepTimeNanoS(LCD1602A_Declares.postAddressWrtSetupDelay);
            this.pulseEnable();
        } else {
            this.logger.info("LCD in busy state, request not possible");
        }
        this.logger.trace("<<<  Exit: sendChar  ");
    }
    // do required gpio->LCD_input dance before and after actual LCD pin update
    protected void sendCommand(int cmd){
        this.logger.trace(">>> Enter: sendCommand   ");
            if(this.lcdAvailable()){
                this.RsPin.low();
                this.sleepTimeNanoS(LCD1602A_Declares.dataWrtSetupDuration);
                this.D0_D7.sendCommand(cmd);
                this.sleepTimeNanoS(LCD1602A_Declares.postAddressWrtSetupDelay);
                this.pulseEnable();
            }else {
                this.logger.info("LCD in busy state, request not possible");
            }
        this.logger.trace("<<< Exit: sendCommand   ");
    }


    /**
     *   Value of 0 indicates the device is not performing internal
     *   operations and will accept commands
     *   Not possible with the Pi GPIOs. Later if the D0_D7 interface is on an
     *   MCP230xx, this maybe possible.  For present time, use timing values
     *   documented in datasheet
     * @return  bit value of DB7
     */


    protected void pulseEnable() {
        this.logger.trace(">>> Enter: pulseEnable  ");
        this.EnPin.high();
        this.sleepTimeNanoS(LCD1602A_Declares.enableWidthDuration);
        this.EnPin.low();
        this.sleepTimeNanoS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: pulseEnable  ");
    }




}