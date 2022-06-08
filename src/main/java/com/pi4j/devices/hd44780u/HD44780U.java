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

package com.pi4j.devices.hd44780u;


import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class HD44780U {


    private Console console = null;
    private final Context pi4j;
    private DigitalOutput RsPin = null;
    private DigitalOutput EnPin = null;   //

    private String traceLevel = "";
    private Logger logger;
    private int RsPinNum = 0xff;
    private int EnPinNum = 0xff;
    private boolean clearDisplay = false;



    private HD44780U_Interface D0_D7;

    public HD44780U(Context pi4j, Console console,HD44780U_Interface d0_d7, int rsGpio, int enGpio, boolean clearIt, String traceLevel) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.D0_D7 = d0_d7;
        this.RsPinNum = rsGpio;
        this.EnPinNum = enGpio;
        this.clearDisplay = clearIt;
        this.traceLevel = traceLevel;
        this.init();
    }


    void init() {
        System.setProperty("org.slf4j.simpleLogger.log." + HD44780U.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(HD44780U.class);
        this.logger.trace(">>> Enter: init");

        this.logger.trace("DR Pin  " + this.RsPinNum);
        this.logger.trace("EN Pin  " + this.EnPinNum);

        var outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
                .id("RS_pin")
                .name("Enable")
                .address(this.RsPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
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
                .provider("pigpio-digital-output");
        try {
            this.EnPin = pi4j.create(outputConfig3);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut EN failed");
            System.exit(203);
        }



        this.D0_D7.sendCommand(0x00); // ensure all pins are low

        this.EnPin.low();
        this.sleepTimeNS(HD44780U_Declares.postWrtEnableCycleDelay);
          // enable LCD with blink
        this.sendCommand(HD44780U_Declares.dispCMD | HD44780U_Declares.dispOnBit| HD44780U_Declares.dispBlnkOnBit | HD44780U_Declares.dispCrsOnBit );
        // entry mode, cursor moves right each character

        this.sendCommand(HD44780U_Declares.entryModeCMD | HD44780U_Declares.entryModeIncCMD);

        this.sendCommand(HD44780U_Declares.funcSetCMD | HD44780U_Declares.func8BitsBit  | HD44780U_Declares.func5x8TwoBit);
        if(this.clearDisplay){
            this.logger.trace("Clear Display");
            this.clearDisplay();
        }

        this.pulseEnable();

        this.logger.trace("<<< Exit: init  device  ");
    }

    public void shiftLeft(int places){
        this.logger.trace(">>> Enter: shiftLeft  : " + places);
        for(int i = 0; i < places; i++){
            this.sendCommand(HD44780U_Declares.cursorCMD | HD44780U_Declares.displayLeftBit);
        }
        this.logger.trace("<<< Exit: shiftLeft  ");

    }

    public void clearDisplay(){
        this.logger.trace(">>> Enter: clearDisplay   ");
        this.sendCommand(HD44780U_Declares.clearDispCMD);
        this.logger.trace("<<< Exit: clearDisplay   ");
    }


    public void sendStringLineOne(String str,  int offset) {
        this.logger.trace(">>> Enter: sendStringLineOne   : " + str + "  Offset  : " + offset);
        char[] chars = str.toCharArray();
        this.sendCommand(HD44780U_Declares.setDDRAMCMD | offset);
        for(int i = 0; i < chars.length; i++){
            this.sendChar(chars[i]);
        }
        this.logger.trace("<<<  Exit: sendStringLineOne  ");
    }




    public void sendStringLineTwo(String str, int offset) {
        this.logger.trace(">>> Enter: sendStringLineTwo   : " + str + "  Offset  : " + offset);
        char[] chars = str.toCharArray();
       this.sendCommand(HD44780U_Declares.setDDRAMCMD | 0x40 | offset);
       this.sleepTimeMS(4);
        for(int i = 0; i < chars.length; i++){
            this.sendChar(chars[i]);
        }
        this.logger.trace("<<<  Exit: sendStringLineTwo  ");
    }

    private void sendChar(char c) {
         this.logger.trace(">>> Enter: sendChar   : " + c );
        if (this.lcdAvailable()) {
            this.RsPin.high();
             this.sleepTimeNS(HD44780U_Declares.dataWrtSetupDuration);
            this.D0_D7.sendCommand(c);
            this.sleepTimeNS(HD44780U_Declares.postAddressWrtSetupDelay);
            this.pulseEnable();
        } else {
            this.logger.info("LCD in busy state, request not possible");
        }
        this.logger.trace("<<<  Exit: sendChar  ");
    }
    // do required gpio->LCD_input dance before and after actual LCD pin update
    private void sendCommand(int cmd){
        this.logger.trace(">>> Enter: sendCommand   ");
            if(this.lcdAvailable()){
                this.RsPin.low();
                this.sleepTimeNS(HD44780U_Declares.dataWrtSetupDuration);
                this.D0_D7.sendCommand(cmd);
                this.sleepTimeNS(HD44780U_Declares.postAddressWrtSetupDelay);
                this.pulseEnable();
            }else {
                this.logger.info("LCD in busy state, request not possible");
            }
        this.logger.trace("<<< Exit: sendCommand   ");
    }


    private boolean lcdAvailable(){
        int c = 0;
        this.logger.trace(">>> Enter: lcdAvailable  ");
        this.sleepTimeMS(10);
        boolean rval = this.isBfLow();
        while(rval == false){
            this.logger.info("\n\n\n !!!!!   BF was busy  \n\n");
            this.sleepTimeMS(400);
            c++;
            if(c > 10){
                this.logger.info(" LCD remained busy state ");
                console.println(" LCD remained busy state ");
                System.exit(100);
            }
            rval = this.isBfLow();
        }
        this.logger.trace("<<< Exit: lcdAvailable  : "   + rval);
        return(rval);
    }

    /**
     *   Value of 0 indicates the device is not performing internal
     *   operations and will accept commands
     *   Not possible with the Pi GPIOs. Later if the D0_D7 interface is on an
     *   MCP230xx, this maybe possible.  For present time, use timing values
     *   documented in datasheet
     * @return  bit value of DB7
     */
    private boolean isBfLow(){
        this.logger.trace(">>> Enter Enter: isBfLow  NOP Not possible on Pi ");
        boolean rval = true;
        // set read mode
        this.logger.trace("<<< Exit: isBfLow  : "   + rval);
        return (rval);

    }


    private void pulseEnable() {
        this.logger.trace(">>> Enter: pulseEnable  ");
        this.EnPin.high();
        this.sleepTimeNS(HD44780U_Declares.enableWidthDuration);
        this.EnPin.low();
        this.sleepTimeNS(HD44780U_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: pulseEnable  ");
    }

    private void sleepTimeNS(int nanoSec) {
        try {
            Thread.sleep(0, nanoSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void sleepTimeMS(int milliSec) {
        try {
            Thread.sleep(milliSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}