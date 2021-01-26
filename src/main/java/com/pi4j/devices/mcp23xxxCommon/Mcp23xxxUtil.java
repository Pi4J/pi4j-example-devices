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
     * @param bus_num    Pi bus number
     * @param address    Chip device address
     * @param cfgData    Chip defines/constants
     * @param mcpObj     MCP23008 and MCP23017 instance
     * @param console    Context
     */
    public Mcp23xxxUtil(Context pi4j,  FfdcUtil ffdc, int bus_num, int address, McpConfigData cfgData, McpBase mcpObj, Console console) {
        super(pi4j, ffdc, bus_num, address, console);
        this.ffdc = ffdc;
        this.bus_num = bus_num;
        this.address = address;
        this.cfgData = cfgData;
        this.mcpB = mcpObj;
    }

    /**
     *
     * @param pin       number of pin on MCPxxxxx chip
     * @param pin_on   drive pin high if true, else low
     *                 <p>
     *                     Use overloaded getAddrMapFirst8
     *                 and getAddrMapSecond8 to obtain correct register address
     *                 </p>
     */
    public void drive_pin(int pin, boolean pin_on) {
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

        this.ffdc.ffdcMethodEntry("drive_pin  pin" + String.format("0x%02X", pin) + " pin_on : " + pin_on);
        this.ffdc.ffdcMethodEntry(
                "drive_pin banked capable " + this.mcpB.bank_capable + " banked  : " + this.mcpB.banked);

        configed = (byte) this.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) > 0) {
            this.ffdc.ffdcErrorExit("Pin" + pin + "  not configured for output",500);
        }
        reg = (byte) this.readRegister(thisOffsetGPI);
        if (pin_on) {
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

       if (pin_on) {
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
      this.ffdc.ffdcMethodExit("drive_pin");
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
    public void read_input(int pin) {
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

        this.ffdc.ffdcMethodEntry(" read_input  pin " + String.format("0x%02X", pin));
        this.ffdc.ffdcMethodEntry(
                "drive_pin banked capable " + this.mcpB.bank_capable + " banked  : " + this.mcpB.banked);

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
        this.ffdc.ffdcMethodExit(" read_input");
    }


  public void process_keyed_data() {
        // this.cfgData;
        // this.keyed_data;
        this.ffdc.ffdcMethodEntry("process_keyed_data ");
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
                    byte iocon_reg;
                    iocon_reg = (byte) this.readRegister(regValOffset);
                    String level =  childPair.getValue();
                    if (level.contains("low")) {
                        iocon_reg = (byte) (iocon_reg & (~2));
                    } else if (level.contains("high")) {
                        iocon_reg = (byte) (iocon_reg | 2);
                    }
                    this.writeByte(regValOffset, iocon_reg);
                }
            }
            String[] pin_list = { "pin0", "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8", "pin9",
                    "pin10", "pin11", "pin12", "pin13", "pin14", "pin15" };
            for (int i = 0; i < pin_list.length; i++) {
                // System.out.println("pin_list[i] " + pin_list[i] + " \n
                // outerMap " + outerMap);
                if (outerMap.containsKey(pin_list[i])) {
                    this.process_pin_data(i, pin_list[i], outerMap.get(pin_list[i]));
                }
            }
        }
        this.ffdc.ffdcMethodExit("process_keyed_data ");
    }

    // i2c_utils, bus, address, mcpConfigDatconfig, NOT used pins, i,
    // keyed_data[pin_list[i]]
    public void process_pin_data(int pin_num, String pin, HashMap<String, String> inner_data) {
        // this.cfgData;
        this.ffdc.ffdcMethodEntry("process_pin_data  pin_num : " + String.format("0x%02X", pin_num) + "  " + pin
                + "  inner_data : " + inner_data);
        String value;
        String[] opt_list = { "dir", "pull", "default", "do_compare", "int_ena" };
        for (int c = 0; c < opt_list.length; c++) {
            // System.out.println("pin_list[i] " + pin_list[i] + " \n outerMap "
            // + outerMap);
            if (inner_data.containsKey(opt_list[c])) {
                value = inner_data.get(opt_list[c]);
                this.process_opt_data(pin_num, opt_list[c], value);
            }
        }
        this.ffdc.ffdcMethodExit("process_pin_data");
    }

    public void process_opt_data(int pin, String key, String value){
        this.ffdc.ffdcMethodEntry(" process_opt_data  pin " + pin + "  key " + key + "  value " + value);
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
            this.ffdc.ffdcDebugEntry(" I2cDevice on bus  " + this.bus_num + "   Chip address :   "
                    + String.format("0x%02X", this.address) + " offset  " + thisOffset);
 // TODO           this.i2cDevice = new I2cBase(this.bus_num, this.address, this.ffdc);
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
        this.ffdc.ffdcMethodExit(" process_opt_data");
    }

    public FfdcUtil ffdc;
    public BasicI2cDevice i2cDevice;
    int bus_num;
    int address;
    McpConfigData cfgData;
    McpBase mcpB;

    /**
     *  <p>
     *      Testing only
     *  </p>
     * @param pi4j
     * @param args
     * @param bank_capable
     * @param dioPinData
     * @param generalConsole
     * @return
     */

    public static Mcp23xxxParms processMain(Context pi4j, String[] args, boolean bank_capable, HashMap<Integer, GpioPinCfgData> dioPinData , Console generalConsole) {
        // TODO Auto-generated method stub

        var console = generalConsole;
        Mcp23xxxParms parmsObj = new Mcp23xxxParms(console);
        parmsObj.pi4j = pi4j;
        parmsObj.ffdcControlLevel = 6;
        parmsObj.bank_capable = bank_capable;
        parmsObj.up_down = "on"; //  presently handlers canot be removed, so not an option

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
                parmsObj.bus_num = Integer.parseInt(a.substring(2), 16);
                i++;
            }  else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                parmsObj.address = Integer.parseInt(a.substring(2), 16);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-h")) {
                parmsObj.usage();
                System.exit(0);
            }  else if (o.contentEquals("-x")) {
                String a = args[i + 1];
                parmsObj.do_reset = true;
                parmsObj.gpio_reset = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-d")) {
                String a = args[i + 1];
                i++;
                // TODO needs work
                parmsObj.pin = Integer.parseInt(a);
                if ((parmsObj.bank_capable) && (parmsObj.pin > 15)) {
                    badParmDetail = ("Pin too large, MAX of 15");
                    badParm = true;
                } else if ((parmsObj.bank_capable == false) && (parmsObj.pin > 7)) {
                    badParmDetail = ("Pin too large, MAX of 7 ");
                    badParm = true;
                } else {
                    parmsObj.set_pin = true;
                }
            }  else if (o.contentEquals("-z")) {
                parmsObj.has_full_keyed_data = true;
                parmsObj.full_keyed_data = args[i + 1];
                i++;
            } else if (o.contentEquals("-m")) {
                parmsObj.has_full_pin_keyed_data = true;
                parmsObj.full_pin_keyed_data = args[i + 1];
                i++;
            } else if (o.contentEquals("-k")) {
                parmsObj.has_IOCON_keyed_data = true;
                parmsObj.IOCON_keyed_data = args[i + 1];
                i++;
            }  else if (o.contentEquals("-r")) {
                String a = args[i + 1];
                i++;
                // needs work
                parmsObj.pin = Integer.parseInt(a);
                if ((parmsObj.bank_capable) && (parmsObj.pin > 15)) {
                    badParmDetail = ("Pin too large, MAX of 15");
                    badParm = true;
                } else if ((parmsObj.bank_capable == false) && (parmsObj.pin > 7)) {
                    badParmDetail = ("Pin too large, MAX of 7");
                    badParm = true;
                } else {
                    parmsObj.read_pin = true;
                }
            } else if (o.contentEquals("-o")) {
                String a = args[i + 1];
                i++;
                if (a.contentEquals("ON")) {
                    parmsObj.pin_on = true;
                } else if (a.contentEquals("OFF")) {
                    parmsObj.pin_on = false;
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
        System.out.println("options   -h 'help', -d drive-pin with -o ON/OFF, "
                + "-r read-pin , -k \"{'dir':'out','int_ena':'no'}\" (using -d or -r)"
                + "   -b bus     -a address "
                + "-m \"{'pin1':{'dir':'in','pull':'down','default':'0','do_compare':'yes','int_ena':'yes','act':'high'}}\""
                + "-c primary chip     -p primary pin"
                + "-z  gpios config dict     -q mainChip"
                + "-x reset-chip GPIO# -n resetPin -f ffdc_lvl  -y dumpRegs ");
    }

}
