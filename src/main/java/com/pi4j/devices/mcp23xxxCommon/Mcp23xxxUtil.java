/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23xxxUtil.java
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

package com.pi4j.devices.mcp23xxxCommon;

import com.pi4j.context.Context;
import com.pi4j.devices.base_i2c.BasicI2cDevice;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.util.Console;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Mcp23xxxUtil
 *
 * <p>
 *     Utilities common to the MCP23008 and MCP23017
 * </p>
 */
public class Mcp23xxxUtil extends BasicI2cDevice{

    /**
     *   CTOR
     *
     * @param pi4j   Context
     * @param ffdc       logging
     * @param busNum    Pi bus number
     * @param address    Chip device address
     * @param cfgData    Chip defines/constants
     * @param mcpObj     MCP23008 and MCP23017 instance
     * @param console    Context
     */
    public Mcp23xxxUtil(Context pi4j,  FfdcUtil ffdc, int busNum, int address, McpConfigData cfgData, McpBase mcpObj, Console console) {
        super(pi4j, ffdc, busNum, address, console);
        this.ffdc = ffdc;
        this.cfgData = cfgData;
        this.mcpB = mcpObj;
    }

    /**
     *
     * @param pin       number of pin on MCPxxxxx chip
     * @param pinOn   drive pin high if true, else low
     *                 <p>
     *                     Use overloaded getAddrMapFirst8
     *                 and getAddrMapSecond8 to obtain correct register address
     *                 </p>
     */
    public void drivePin(int pin, boolean pinOn) {
        // get the regs and make sure the desired pin is configed as output. Log
        // error if not
        byte configed;
        byte b;
        byte reg;
        int absPin = pin; // if in second bank must subtract 8

        byte[] first8 = this.mcpB.getAddrMapFirst8();

        byte thisOffsetIOD = first8[this.cfgData._IODIR];

        byte thisOffsetGPI = first8[this.cfgData._GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.mcpB.getAddrMapSecond8();
            thisOffsetIOD = second8[this.cfgData._IODIRB];
            thisOffsetGPI = second8[this.cfgData._GPIOB];
        }

        this.ffdc.ffdcMethodEntry("drivePin  pin" + String.format("0x%02X", pin) + " pinOn : " + pinOn);
        this.ffdc.ffdcMethodEntry(
                "drivePin banked capable " + this.mcpB.bankCapable + " banked  : " + this.mcpB.banked);

        configed = (byte) this.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) > 0) {
            this.ffdc.ffdcErrorExit("Pin" + pin + "  not configured for output",500);
        }
        reg = (byte) this.readRegister(thisOffsetGPI);
        if (pinOn) {
            Integer integerObject;
            integerObject = 1 << absPin;
            b = integerObject.byteValue();
            reg = (byte) (reg | b);
        } else {
            Integer integerObject = ~(1 << absPin);
            b = integerObject.byteValue();
            reg = (byte) (reg & b);
        }

        this.writeByte( thisOffsetGPI, reg);

       if (pinOn) {
            Integer integerObject;
            integerObject = (1 << absPin);
            b = integerObject.byteValue();
            reg = (byte) (reg | b);
        } else {
            Integer integerObject;
            integerObject = ~(1 << absPin);
            b = integerObject.byteValue();
            reg = (byte) (reg & b);
        }
      this.ffdc.ffdcMethodExit("drivePin");
    }

    /**
     *
     * @param pin       number of pin on MCPxxxxx chip
     *                 <p>
     *                     Use overloaded getAddrMapFirst8
     *                 and getAddrMapSecond8 to obtain correct register address
     *                  <p>
     *                  Read and display pin state high/low
     *                  </p>
     *                 </p>
     */
    public void readInput(int pin) {
        // # get the regs and make sure the desired pin is configed as input.
        // Log error if not
        int configed;
        int reg = 0;
        int absPin = pin; // if in second bank must subtract 8
        byte[] first8 = this.mcpB.getAddrMapFirst8();

        byte thisOffsetIOD = first8[this.cfgData._IODIR];
        byte thisOffsetGPI = first8[this.cfgData._GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.mcpB.getAddrMapSecond8();
            thisOffsetIOD = second8[this.cfgData._IODIRB];
            thisOffsetGPI = second8[this.cfgData._GPIOB];
        }

        this.ffdc.ffdcMethodEntry(" readInput  pin " + String.format("0x%02X", pin));
        this.ffdc.ffdcMethodEntry(
                "drivePin banked capable " + this.mcpB.bankCapable + " banked  : " + this.mcpB.banked);

        configed = this.readRegister(thisOffsetIOD);
        this.examineReturnRead(reg);

        if ((configed & (1 << absPin)) == 0) {
            this.ffdc.ffdcErrorExit("Pin" + String.format("0x%02X", pin) + "  not configured for input",510);
        }

        reg = this.readRegister(thisOffsetGPI);
        // TODO use Console
        if ((reg & (1 << absPin)) == 0) {
            System.out.println("Pin" + pin + " Low");
        } else {
            System.out.println("Pin" + pin + " High");
        }
        this.ffdc.ffdcMethodExit(" readInput");
    }


  public void processKeyedData() {
        // this.cfgData;
        // this.keyedData;
        this.ffdc.ffdcMethodEntry("processKeyedData ");
        HashMap<String, HashMap<String, String>> outerMap = this.cfgData.getFullMap();

        // this.configUtils.confgureGpios(cfgData);
        Set outerSet = outerMap.entrySet();
        Iterator<Map.Entry<String, Map<String, String>>> outerIterator = outerSet.iterator();
        while (outerIterator.hasNext()) {
            Map.Entry<String, Map<String, String>> mentry = (Map.Entry) outerIterator.next();
            // iterate inner map
            HashMap<String, String> innerMap = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> child = (mentry.getValue()).entrySet().iterator();
            while (child.hasNext()) {
                Entry<String, String> childPair = child.next();
                String key =  childPair.getKey();
                byte[] first8 = this.mcpB.getAddrMapFirst8();
                byte regValOffset = first8[this.cfgData._IOCON];
                if (key.equals("act")) {
                    byte ioconReg;
                    ioconReg = (byte) this.readRegister(regValOffset);
                    String level =  childPair.getValue();
                    if (level.contains("low")) {
                        ioconReg = (byte) (ioconReg & (~2));
                    } else if (level.contains("high")) {
                        ioconReg = (byte) (ioconReg | 2);
                    }
                    this.writeByte(regValOffset, ioconReg);
                }
            }
            String[] pinList = { "pin0", "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8", "pin9",
                    "pin10", "pin11", "pin12", "pin13", "pin14", "pin15" };
            for (int i = 0; i < pinList.length; i++) {
                if (outerMap.containsKey(pinList[i])) {
                    this.processPinData(i, pinList[i], outerMap.get(pinList[i]));
                }
            }
        }
        this.ffdc.ffdcMethodExit("processKeyedData ");
    }

    // i2c_utils, bus, address, mcpConfigDatconfig, NOT used pins, i,
    // keyedData[pinList[i]]
    public void processPinData(int pin_num, String pin, HashMap<String, String> innerData) {
        // this.cfgData;
        this.ffdc.ffdcMethodEntry("processPinData  pinNum : " + String.format("0x%02X", pin_num) + "  " + pin
                + "  innerData : " + innerData);
        String value;
        String[] opt_list = { "dir", "pull", "default", "do_compare", "int_ena" };
        for (int c = 0; c < opt_list.length; c++) {
            // System.out.println("pin_list[i] " + pin_list[i] + " \n outerMap "
            // + outerMap);
            if (innerData.containsKey(opt_list[c])) {
                value = innerData.get(opt_list[c]);
                this.processOptData(pin_num, opt_list[c], value);
            }
        }
        this.ffdc.ffdcMethodExit("processPinData");
    }

    public void processOptData(int pin, String key, String value){
        this.ffdc.ffdcMethodEntry(" processOptData  pin " + pin + "  key " + key + "  value " + value);
        byte reg;
        byte b;
        int absPin = pin; // if in second bank must subtract 8
        if (key.contains("dir")) {
            byte[] first8 = this.mcpB.getAddrMapFirst8();
            byte thisOffset = first8[this.cfgData._IODIR];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = this.mcpB.getAddrMapSecond8();
                thisOffset = second8[this.cfgData._IODIRB];
            }

            // # read return a list, get the single entry [0]
            this.ffdc.ffdcDebugEntry(" I2cDevice on bus  " + this.busNum + "   Chip address :   "
                    + String.format("0x%02X", this.address) + " offset  " + thisOffset);
           // TODO  Must we ever remap the bus/address ???
            reg = (byte) this.readRegister(thisOffset);
            this.ffdc.ffdcDebugEntry(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("in")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            this.writeByte( thisOffset, reg);
        } else if (key.contains("pull")) {
            byte[] first8 = this.mcpB.getAddrMapFirst8();
            byte thisOffset = first8[this.cfgData._GPPU];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = this.mcpB.getAddrMapSecond8();
                thisOffset = second8[this.cfgData._GPPUB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) this.readRegister( thisOffset);
            this.ffdc.ffdcDebugEntry(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("up")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            this.writeByte( thisOffset, reg);
        } else if (key.contains("default")) {
            byte[] first8 = this.mcpB.getAddrMapFirst8();
            byte thisOffset = first8[this.cfgData._DEFVAL];
            if (pin > 7) {
                absPin = absPin - 8;
                byte[] second8 = this.mcpB.getAddrMapSecond8();
                thisOffset = second8[this.cfgData._DEFVALB];
                // thisOffset = this.cfgData._DEFVALB;
            }
            // # read return a list, get the single entry [0]
            reg = (byte) this.readRegister(thisOffset);
            this.ffdc.ffdcDebugEntry(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("1")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            this.writeByte(thisOffset, reg);
        } else if (key.contains("do_compare")) {
            byte first8[] = this.mcpB.getAddrMapFirst8();
            byte thisOffset = first8[this.cfgData._INTCON];
            if (pin > 7) {
                absPin = absPin - 8;
                byte second8[] = this.mcpB.getAddrMapSecond8();
                thisOffset = second8[this.cfgData._INTCON];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) this.readRegister(thisOffset );
            this.ffdc.ffdcDebugEntry(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject;
                integerObject = (~(1 << absPin));
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            this.writeByte( thisOffset, reg);
        } else if (key.contains("invert")) {
            byte first8[] = this.mcpB.getAddrMapFirst8();
            byte thisOffset = first8[this.cfgData._IPOL];
            if (pin > 7) {
                absPin = absPin - 8;
                byte second8[] = this.mcpB.getAddrMapSecond8();
                thisOffset = second8[this.cfgData._IPOLB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte)this.readRegister( thisOffset);
            this.ffdc.ffdcDebugEntry(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                Integer integerObject;
                integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = ~(1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            this.writeByte( thisOffset, reg);
        } else if (key.contains("int_ena")) {
            byte first8[] = this.mcpB.getAddrMapFirst8();
            byte thisOffset = first8[this.cfgData._GPINTEN];
            if (pin > 7) {
                absPin = absPin - 8;
                byte second8[] = this.mcpB.getAddrMapSecond8();
                thisOffset = second8[this.cfgData._GPINTENB];
            }
            // # read return a list, get the single entry [0]
            reg = (byte) this.readRegister( thisOffset);
            this.ffdc.ffdcDebugEntry(" Read returned : " + String.format("0x%02X", reg));
            if (value.contains("yes")) {
                Integer integerObject = (1 << absPin);
                b = integerObject.byteValue();
                reg = (byte) (reg | b);
            } else {
                Integer integerObject = (~(1 << absPin));
                b = integerObject.byteValue();
                reg = (byte) (reg & b);
            }
            var i = this.writeByte(thisOffset, reg);
        }
        this.ffdc.ffdcMethodExit(" processOptData");
    }

    public FfdcUtil ffdc;
    public BasicI2cDevice i2cDevice;
    McpConfigData cfgData;
    McpBase mcpB;

    /**
     *
     *
     * @param pi4j
     * @param args
     * @param bankCapable
     * @param dioPinData
     * @param generalConsole
     * @return
     */

    public static Mcp23xxxParms processMain(Context pi4j, String[] args, boolean bankCapable, HashMap<Integer, GpioPinCfgData> dioPinData , Console generalConsole) {
        // TODO Auto-generated method stub

        var console = generalConsole;
        Mcp23xxxParms parmsObj = new Mcp23xxxParms(console);
        parmsObj.pi4j = pi4j;
        parmsObj.ffdcControlLevel = 2;  // log info,starting point, likely user set a value option -f
        parmsObj.bankCapable = bankCapable;
        parmsObj.upDown = "on"; //  presently handlers canot be removed, so not an option

        console.print("entered processMain   \n");

        //lm.addLogger(parmsObj.logger);

        // boolean haveAddress = false;
        // boolean haveBus = false;
        boolean badParm = false;
        boolean dumpRegs = false;
        String badParmDetail = "";
        String mainChip = "";

        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-f")) {
                String a = args[i + 1];
                i++;
                parmsObj.ffdcControlLevel = Integer.parseInt(a);
            } else if (o.contentEquals("-y")) {
                parmsObj.dumpRegs = true;
            }  else if (o.contentEquals("-b")) { // bus
                String a = args[i + 1];
                parmsObj.busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            }  else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                parmsObj.address = Integer.parseInt(a.substring(2), 16);
                // displayMain.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-h")) {
                parmsObj.usage();
                System.exit(0);
            }  else if (o.contentEquals("-x")) {
                String a = args[i + 1];
                parmsObj.doReset = true;
                parmsObj.gpioReset = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-d")) {
                String a = args[i + 1];
                i++;
                // TODO needs work
                parmsObj.pin = Integer.parseInt(a);
                if ((parmsObj.bankCapable) && (parmsObj.pin > 15)) {
                    badParmDetail = ("Pin too large, MAX of 15");
                    badParm = true;
                } else if ((parmsObj.bankCapable == false) && (parmsObj.pin > 7)) {
                    badParmDetail = ("Pin too large, MAX of 7 ");
                    badParm = true;
                } else {
                    parmsObj.setPin = true;
                }
            }  else if (o.contentEquals("-z")) {
                parmsObj.hasFullKeyedData = true;
                parmsObj.fullKeyedData = args[i + 1];
                i++;
            } else if (o.contentEquals("-m")) {
                parmsObj.hasFullPinKeyedData = true;
                parmsObj.fullPinKeyedData = args[i + 1];
                i++;
            } else if (o.contentEquals("-k")) {
                parmsObj.hasIOCONKeyedData = true;
                parmsObj.IOCONKeyedData = args[i + 1];
                i++;
            }  else if (o.contentEquals("-r")) {
                String a = args[i + 1];
                i++;
                // needs work
                parmsObj.pin = Integer.parseInt(a);
                if ((parmsObj.bankCapable) && (parmsObj.pin > 15)) {
                    badParmDetail = ("Pin too large, MAX of 15");
                    badParm = true;
                } else if ((parmsObj.bankCapable == false) && (parmsObj.pin > 7)) {
                    badParmDetail = ("Pin too large, MAX of 7");
                    badParm = true;
                } else {
                    parmsObj.readPin = true;
                }
            } else if (o.contentEquals("-o")) {
                String a = args[i + 1];
                i++;
                if (a.contentEquals("ON")) {
                    parmsObj.pinOn = true;
                } else if (a.contentEquals("OFF")) {
                    parmsObj.pinOn = false;
                } else {
                    badParmDetail = ("Invalid parm : " + a);
                    badParm = true;
                }
            } else {
                console.print("Invalid parm : " + o  + "  ");
                parmsObj.usage();
                System.exit(2);
            }
            if (badParm) {
                Mcp23xxxUtil.usage();
                console.print(badParmDetail);
                System.exit(701);
            }
        }
        return(parmsObj);
    }

    public static void usage() {
        System.out.println("options   -h 'help', -d drive-pin with -o ON/OFF, \n"
                + "-r read-pin , -k \"{'dir':'out','int_ena':'no'}\" (using -d or -r) \n"
                + "   -b bus     -a address  \n"
                + "-m \"{'pin1':{'dir':'in','pull':'down','default':'0','do_compare':'yes','int_ena':'yes','act':'high'}}\" \n"
                + "-c primary chip     -p primary pin \n"
                + "-z  gpios config dict     -q mainChip \n"
                + "-x reset-chip GPIO# -n resetPin -f ffdc_lvl  -y dumpRegs \n"
                + "ffdc_lvl 0 < TRACE 1 DEBUG < 2 INFO < 3 WARN < 4 ERROR < 5 FATAL < 6 OFF    ");
    }

}
