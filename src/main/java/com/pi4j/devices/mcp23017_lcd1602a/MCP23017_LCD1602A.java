/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  MCP23017_LCD1602A.java
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

package com.pi4j.devices.mcp23017_lcd1602a;

import com.pi4j.context.Context;
import com.pi4j.devices.lcd1602a.LCD1602A;
import com.pi4j.devices.lcd1602a.LCD1602A_Declares;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;
import org.slf4j.LoggerFactory;

public class MCP23017_LCD1602A extends LCD1602A {

    private I2C mcpDev = null;
    private int busNum;
    private int address;

    private DigitalOutput ResetPin = null;   //
    private int resetPinNum = 0x42;


    /**
     *
     * @param pi4j
     * @param console
     * @param resetPin  GPIO connected to MCP23017 RESET line
     * @param bus
     * @param address
     * @param traceLevel
     */
    public MCP23017_LCD1602A(Context pi4j, Console console ,int resetPin,  int bus, int address , String traceLevel) {
        super(pi4j,  console,  false, traceLevel);
        this.resetPinNum = resetPin;
        this.traceLevel = traceLevel;
        this.busNum = bus;
        this.address = address;

        this.init();
    }

    /**
     * Configure I2C LCD display
     */
    public void init() {
        System.setProperty("org.slf4j.simpleLogger.log." + MCP23017_LCD1602A.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(MCP23017_LCD1602A.class);

        super.init();
        this.logger.trace("Reset Pin  " + this.resetPinNum);

        this.createI2cDevice();


        var outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
                .id("RESET_pin")
                .name("Reset")
                .address(this.resetPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("linuxfs-digital-output");
        try {
            this.ResetPin = pi4j.create(outputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut Reset failed");
            System.exit(201);
        }




        // POR and reset configures all pins as input.
        // Set IODIR A&B to zeros so pins 0-11 are output.
       for(int pin = 0; pin < 11; pin++){
            this.configPinOutput(pin);
        }

        this.sendCommand(0x00); // ensure all pins are low

        //this.setEnLow(0x00);
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

        this.pulseEnable(0x00);

        this.logger.trace("<<< Exit: init  device  ");
    }

    /**
     *
     * @param pin   MCP23017 pin to configure as output
     */
    private void configPinOutput(int pin) {
        this.logger.trace(">>> Enter: configPinOutput  Pin:  " + pin);
        byte reg;
        byte b;
        int absPin = pin; // if in second bank must subtract 8

        byte[] first8 = this.getAddrMapFirst8();
        byte thisOffset = first8[MCP23017_LCD1602A_Declares._IODIR];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.getAddrMapSecond8();
            thisOffset = second8[MCP23017_LCD1602A_Declares._IODIRB];
        }

        // # read return a list, get the single entry [0]
        logger.trace(" I2cDevice on bus  " + this.busNum + "   Chip address :   "
                + String.format("0x%02X", this.address) + " offset  " + thisOffset);
        reg = (byte) this.mcpDev.readRegister(thisOffset);
        logger.trace(" Read returned : " + String.format("0x%02X", reg));
        /* ("in"))
            Integer integerObject = (1 << absPin);
            b = integerObject.byteValue();
            reg = (byte) (reg | b);
            */
        Integer integerObject = ~(1 << absPin);
        b = integerObject.byteValue();
        reg = (byte) (reg & b);
        this.mcpDev.writeRegister(thisOffset, reg);
        this.logger.trace("<<< Exit: configPinOutput  ");

    }

    /**
     * Write byte to actual device I2C interface
     * @param data
     */
    private void writeToDev(int data){
        // TODO data |=   MCP23017_LCD1602A_Declares.backlight_on;
        this.logger.trace(">>> Enter: writeToDev  data: "+ String.format("%02x ", data) );

        String logData = "";
        logData += " \n    P7-DB7: "+ ((data >> 7) & 0x1) +   " P6-DB6: "+ ((data >> 6) & 0x1) + " P5-DB5: "+ ((data >> 5) & 0x1) + "  P4-DB4: "  + ((data >> 4) & 0x1) +
                "\n    BackLight: " +   ((data >> 3) & 0x1)  +  "  EN: " + ((data >> 2) & 0x1)  + " RW: "  + ((data>>1) & 0x1) +    " RS: "  + ((data) & 0x1) +  "\n  Data : " + String.format("0X%02x: ",((data >> 4 ) &0xff));
        this.logger.trace(logData);
        // TODO drive each pin, start from pin0 through pin10

        int rc = this.mcpDev.write(data);
        this.sleepTimeMicroS(LCD1602A_Declares.preAddressWrtSetupDelay*2);

        this.logger.trace("Exit: writeToDev  RC : "  + rc);
    }


    /**
     * Write byte to device P0-P7, RS RW and EN pins,  then pulse EN
     * pin so all pins are read into LCD
     * @param data
     */
    protected void writeElevenBits(int data) {
        this.logger.trace(">>> Enter: writeElevenBits   : " +  data + String.format("    0X%02x: ", data));

        String logData = "";
        logData += " \n    P7-DB7: "+ ((data >> 7) & 0x1) +   " P6-DB6: "+ ((data >> 6) & 0x1) + " P5-DB5: "+ ((data >> 5) & 0x1) + "  P4-DB4: "  + ((data >> 4) & 0x1) +
               " \n    P3-DB3: "+ ((data >> 3) & 0x1) +   " P2-DB2: "+ ((data >> 2) & 0x1) + " P1-DB1: "+ ((data >> 1) & 0x1) + "  P0-DB0: "  + ((data >> 0) & 0x1) +
                 "\n   EN: " + ((data >> 10) & 0x1)  + " RW: "  + ((data>>9) & 0x1) +    " RS: "  + ((data>>8) & 0x1) +  "\n  Data : " + String.format("0X%02x: ",((data ) & 0xff));
        this.logger.trace(logData);
        for(int i = 0; i < 11; i++){
            int pin = i;
            boolean level = (((data>>i)&0x01) == 1);
            this.drivePin(pin, level);
        }
        this.pulseEnable(data);
        this.logger.trace("<<<  Exit: writeElevenBits  ");
    }


    /**
     * Write byte to device P0-P7, then pulse EN pin so all pins are read into LCD
     * @param data
     */
    protected void writeEightBits(int data) {
        this.logger.trace(">>> Enter: writeEightBits   : " +  data + String.format("    0X%02x: ", data));
        String logData = "";
        logData += " \n    P7-DB7: "+ ((data >> 7) & 0x1) +   " P6-DB6: "+ ((data >> 6) & 0x1) + " P5-DB5: "+ ((data >> 5) & 0x1) + "  P4-DB4: "  + ((data >> 4) & 0x1) +
                " \n    P3-DB3: "+ ((data >> 3) & 0x1) +   " P2-DB2: "+ ((data >> 2) & 0x1) + " P1-DB1: "+ ((data >> 1) & 0x1) + "  P0-DB0: "  + ((data >> 0) & 0x1) +
                "\n   EN: " + ((data >> 10) & 0x1)  + " RW: "  + ((data>>9) & 0x1) +    " RS: "  + ((data>>8) & 0x1) +  "\n  Data : " + String.format("0X%02x: ",((data ) & 0xff));

        this.logger.trace(logData);
        for(int i = 0; i < 8; i++){
            int pin = i;
            boolean level = (((data>>i)&0x01) == 1);
            this.drivePin(pin, level);
        }
        this.pulseEnable(data);
        this.logger.trace("<<<  Exit: writeEightBits  ");
    }


    /**
     * Set RS bit high and write the 11 bits to LCD via MCP23017
     * * @param data
     */
    protected void sendChar(char data) {
        this.logger.trace(">>> Enter: sendChar   : " +  data + String.format("    0X%02x: ", (int)data));

        if (this.lcdAvailable()) {
            int c= this.setRSHigh((byte) (data & 0xfff));
            this.writeElevenBits(c);
        } else {
            this.logger.trace("LCD in busy state, request not possible");
        }
        this.logger.trace("<<<  Exit: sendChar  ");
    }

    // do required gpio->LCD_input dance before and after actual LCD pin update

    /**
     * Set RS bit low and the 11 bits to LCD via MCP23017
     * @param data
     */
    protected void sendCommand(int data) {
        this.logger.trace(">>> Enter: sendCommand  : " + String.format("0X%02x: ", data));
        if (this.lcdAvailable()) {
            int cmd = this.setRSLow(data & 0xfff);
            this.writeElevenBits(cmd);
       } else {
            this.logger.trace("LCD in busy state, request not possible");
        }
        this.logger.trace("<<< Exit: sendCommand   ");
    }

    /**
     * Modify data byte to set EN bit, write to device.
     *  Wait
     * Clear EN bit and write to device
     *
     * @param b
     *
     */

    protected int pulseEnable(int b){
        this.logger.trace(">>> Enter: pulseEnable  ");
        b = this.setEnHigh(b);
        b = this.setEnLow(b);
        this.logger.trace("<<< Exit: pulseEnable : " + b);
        return(b);
    }



    /**
     *  Set EN bit low.  Drive MCP23017 pin 10 low, wait, return
     * @param b byte
     * @return  modified byte
     */
    private int setEnLow(int b) {
        this.logger.trace(">>> Enter: setEnLow");
        b &= MCP23017_LCD1602A_Declares.E_bit_mask_off;
        b |= MCP23017_LCD1602A_Declares.E_low;
        this.drivePin(MCP23017_LCD1602A_Declares.EN_MCP_PinNum, false);
        this.sleepTimeMicroS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setEnLow");
        return (b);
    }


    /**
     *  Set EN bit high.  Drive MCP23017 pin 10 high, wait, return
     * @param b int
     * @return  modified int
     */
    private int setEnHigh(int b) {
        this.logger.trace(">>> Enter: setEnHigh");
        b  &= MCP23017_LCD1602A_Declares.E_bit_mask_off;
        b |= MCP23017_LCD1602A_Declares.E_high;
        this.drivePin(MCP23017_LCD1602A_Declares.EN_MCP_PinNum, true);
        this.sleepTimeMicroS(LCD1602A_Declares.postWrtEnableCycleDelay);
        this.logger.trace("<<< Exit: setEnHigh");
        return(b);
    }


    /**
     *  Set RS bit low
     * @param b int
     * @return  modified int
     */
    private int setRSLow(int b) {
        this.logger.trace(">>> Enter: setRSLow");
        b  &= MCP23017_LCD1602A_Declares.RS_bit_mask_off;
        b |= MCP23017_LCD1602A_Declares.RS_low;
        this.logger.trace("<<< Exit: setRSLow");
        return(b);
    }


    /**
     *  Set RS bit high
     * @param b int
     * @return  modified int
     */
    private  int setRSHigh(int b) {
        this.logger.trace(">>> Enter: setRSHigh");
        b  &= MCP23017_LCD1602A_Declares.RS_bit_mask_off;
        b  |= MCP23017_LCD1602A_Declares.RS_high;
        this.logger.trace("<<< Exit: setRSHigh");
        return(b);
    }

    /**
     *  Set RW bit low
     * @param b int
     * @return  modified int
     */
    private int setRWLow(int b) {
        this.logger.trace(">>> Enter: setRWLow");
        b  &= MCP23017_LCD1602A_Declares.RW_bit_mask_off;
        b  |= MCP23017_LCD1602A_Declares.RW_low;
        this.logger.trace("<<< Exit: setRWLow");
        return(b);
    }


    /**
     *  Set RW bit high
     * @param b int
     * @return  modified int
     */
    private int setRWHigh(int b) {
        this.logger.trace(">>> Enter: setRWHigh");
        b  &= MCP23017_LCD1602A_Declares.RW_bit_mask_off;
        b  |= MCP23017_LCD1602A_Declares.RW_high;
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
        this.mcpDev = this.pi4j.create(i2cDeviceConfig);
        this.logger.trace("<<< Exit:createI2cDevice  ");
    }


/////////////////////////////////////////////////////////////////////////////////
    // PIN behaviour
    /**
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapFirst8() {
        this.logger.trace(">>> Enter 23017 getAddrMapFirst8");
        byte regAddr[] = { 0x00, 0x02, 0x04, 0x06, 0x08, 0x0A, 0x0C, 0x0E, 0x10, 0x12, 0x14 };
        this.logger.trace("<<< Exit 23017 getAddrMapFirst8");
        return (regAddr);
    }

    /**
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapSecond8() {
        this.logger.trace(">>> Enter 23017 getAddrMapSecond8");
        byte regAddr[] = { 0x01, 0x03, 0x05, 0x07, 0x09, 0x0B, 0x0D, 0x0F, 0x11, 0x13, 0x15 };
        this.logger.trace("<<< Exit 23017 getAddrMapSecond8");
        return (regAddr);
    }

    /**
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
  
    /**
     * PrettyPrint register values
     */
    public void dumpRegs() {
        this.logger.info(">>> Enter dump_regs ");

        String regName[] = { "_IODIR  ", "_IPOL   ", "_GPINTE ", "_DEFVAL ", "_INTCON ", "_IOCON  ", "_GPPU   ",
                "_INTF   ", "_INTCAP ", "_GPIO   ", "_OLAT   " };
        byte regAddr[] = this.getAddrMapFirst8();
        this.logger.info("this.getAddrMapFirst8() function returned     " + this.getAddrMapFirst8());

        String regNameB[] = { "_IODIRB ", "_IPOLB  ", "_GPINTEB", "_DEFVALB", "_INTCONB", "_IOCON  ", "_GPPUB  ",
                "_INTFB  ", "_INTCAPB", "_GPIOB  ", "_OLATB  " };
        String[][] pinName = { { "IO7    IO6    IO5    IO4    IO3    IO2    IO1    IO0" },
                { "IP7    IP6    IP5    IP4    IP3    IP2    IP1    IP0   " },
                { "GPINT7 GPINT6 GPINT5 GPINT4 GPINT3 GPINT2 GPINT1 GPINT0  " },
                { "DEF7   DEF6   DEF5   DEF4   DEF3   DEF2   DEF1   DEF0    " },
                { "IOC7   IOC6   IOC5   IOC4   IOC3   IOC2   IOC1   IOC0    " },
                { "BANK   MIRROR SEQOP  DISSLW HAEN   ODR    INTPOL     " },
                { "PU7    PU6    PU5    PU4    PU3    PU2    PU1    PU0     " },
                { "INT7   INT6   INT5   INT4   INT3   INT2   INT1   INT0    " },
                { "ICP7   ICP6   ICP5   ICP4   ICP3   ICP2   ICP1   ICP0    " },
                { "GP7    GP6    GP5    GP4    GP3    GP2    GP1    GP0     " },
                { "OL7    OL6    OL5    OL4    OL3    OL2    OL1    OL0     " }, };
        byte regAddrB[] = this.getAddrMapSecond8();

        this.logger.info("this.getAddrMapSecond8() function returned     " + this.getAddrMapSecond8());
        String regAstr = "";
        int reg ;

        for (int i = 0; i < regName.length; i++) {
            // this.logger.trace(" regAddr " + regAddr[i]);
            // System.out.println(" regAddr " + regAddr[i]);

            reg = this.mcpDev.readRegister(regAddr[i]);
            regAstr = regAstr.concat("\n   Reg " + regName[i] + " offset ("+ i + ")  data: "  +   String.format("0x%02X", reg) + "\n");
            int val =  reg;
            // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1 pin0");
            regAstr = regAstr.concat(pinName[i][0] + "\n");
            regAstr = regAstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                    + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
        }
        this.logger.info(regAstr);
        //System.out.println(regAstr);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String regBstr = "";
            int regB;

            for (int i = 0; i < regNameB.length; i++) {
                // this.logger.trace(" regAddrB " + regAddrB[i]);
                // System.out.println(" regAddrB " + regAddrB[i]);

                regB = this.mcpDev.readRegister(regAddrB[i]);
                regBstr = regBstr.concat("\n   RegB " + regNameB[i] + " offset ("+ i + ")  data: "  +   String.format("0x%02X", regB) + "\n");
                int val =  regB;
                // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1
                // pin0");
                regBstr = regBstr.concat(pinName[i][0] + "\n");
                regBstr = regBstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                        + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3)
                        + "      " + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01))
                        + "\n");
            }
            this.logger.info(regBstr);
            //System.out.println(regBstr);

        this.logger.info("<<< Exit dumpRegs");
    }


    /**
     * Toggle GPIO connected to MCP23017 reset line, clear display
     */
    public void resetChip() {
        this.logger.trace(">>> Enter resetChip ");
        // POR/Reset set output values to zero
        try {
                this.ResetPin.low();
                this.sleepTimeMilliS(5000);
                this.ResetPin.high();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // POR/Reset set output values to zero
        for(int pin = 0; pin < 11; pin++){
            this.configPinOutput(pin);
        }

        this.clearDisplay();

        this.sendCommand(0x00); // ensure all pins are low after clear command

        this.logger.trace("<<< Exit resetChip");
    }

    /**
     *
     * @param pin         MCP pin to drive
     * @param pinOn       If true drive HIGH, else drive LOW
     */
    public void drivePin(int pin, boolean pinOn){
        this.logger.trace(">>> Enter drivePin");
        // get the regs and make sure the desired pin is configed as output. Log
        // error if not
        int configed;
        int b;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte first8[] = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[MCP23017_LCD1602A_Declares._IODIR];

        byte thisOffsetGPI = first8[MCP23017_LCD1602A_Declares._GPIO];
        byte thisOffsetOLA = first8[MCP23017_LCD1602A_Declares._OLAT];
        if (pin > 7) {
            absPin = absPin - 8;
            byte second8[] = this.getAddrMapSecond8();

            thisOffsetIOD = second8[MCP23017_LCD1602A_Declares._IODIRB];
            thisOffsetGPI = second8[MCP23017_LCD1602A_Declares._GPIOB];
            thisOffsetOLA = second8[MCP23017_LCD1602A_Declares._OLATB];
        }

        this.logger.trace("drivePin  pin" + String.format("0x%02X", pin) + " pinOn : " + pinOn);

        configed = this.mcpDev.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) > 0) {
            System.out.println("Pin" + pin + "  not configured for output");
            System.exit(500);
        }
        reg = this.mcpDev.readRegister( thisOffsetGPI);
        // System.out.println("read GPI " + String.format("0x%02X", reg[0]));
        if (pinOn) {
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg = (byte)(reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (byte)(reg & b);
        }

        // System.out.println("write GPI " + String.format("0x%02X", reg[0]));

        reg = reg & 0xff;
        this.mcpDev.writeRegister(thisOffsetGPI, (byte) reg);

        // OLAT
        reg = this.mcpDev.readRegister( thisOffsetOLA);
        if (pinOn) {
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg =  (reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (reg & b);
        }
        reg = reg & 0xff;

        this.mcpDev.writeRegister(thisOffsetOLA, (byte) reg);

        this.logger.trace("<<< Exit drivePin");
    }

    /**
     *
     * @param pin    MCP pin to read
     *               <p>
     *                Pin read and detail logged.
     *               </p>
     */
    public void readInput(int pin){
        this.logger.trace(">>> Enter readPin");

        // # get the regs and make sure the desired pin is configed as input.
        // Log error if not
        int configed;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte first8[] = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[MCP23017_LCD1602A_Declares._IODIR];
        byte thisOffsetGPI = first8[MCP23017_LCD1602A_Declares._GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte second8[] = this.getAddrMapSecond8();
            thisOffsetIOD = second8[MCP23017_LCD1602A_Declares._IODIRB];
            thisOffsetGPI = second8[MCP23017_LCD1602A_Declares._GPIOB];
        }

        this.logger.trace(" readInput  pin " + String.format("0x%02X", pin));

        configed = this.mcpDev.readRegister(thisOffsetIOD);
        // System.out.println("configed from _IODIR : " +
        // String.format("0x%02X", configed[0]));
        // System.out.println("(configed[0] & (1 << pin)) : " + (configed[0] &
        // (1 << pin)));
        // System.out.println("(configed[0] & (1 << pin))>0 : " + ( (configed[0]
        // & (1 << pin))>0) );

        if ((configed & (1 << absPin)) == 0) {
            this.logger.trace("Pin" + String.format("0x%02X", pin) + "  not configured for input");
            this.logger.trace("Incorrect Pin direction",  510);
        }

        reg = this.mcpDev.readRegister( thisOffsetGPI);
        if ((reg & (1 << absPin)) == 0) {
            //System.out.println("Pin" + pin + " Low");
            this.logger.trace("Pin" + pin + " Low");
        } else {
            // System.out.println("Pin" + pin + " High");
            this.logger.trace("Pin" + pin + " High");
        }
        this.logger.trace(" <<< Exit readInput");
    }


}