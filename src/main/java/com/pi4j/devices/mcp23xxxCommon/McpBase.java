/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  McpBase.java
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

import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.devices.base_i2c.MutableI2cDevice;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.context.Context;
import com.pi4j.devices.base_i2c.BasicI2cDevice;
import com.pi4j.util.Console;
import java.util.HashMap;

import com.pi4j.devices.mcp23xxxApplication.PinInterruptActionIntf;



import com.pi4j.io.gpio.digital.DigitalState;

/**
 * McpBase
 * <p>
 *     Base class for simple MCP23008 and MCP23017 implementations and the more complex
 *     Application for these two classes
 * </p>
 */
public class McpBase extends MutableI2cDevice  {

    /**
     *  CTOR
     * @param parmsObj   Contains the many options supplied to the main functions
     * @param bank_capable   If true a MCP23017 chip, else a MCP23008 chip
     * @param dioPinData    MCP chip pin  configuration objects
     * @param pi4j          Context
     * @param ffdc           Logging
     * @param console          Console
     */
    public McpBase(Mcp23xxxParms parmsObj, boolean bank_capable, HashMap<Integer, GpioPinCfgData> dioPinData, Context pi4j, FfdcUtil ffdc, Console console) {
        super(pi4j, ffdc,  console);
        this.parmsObj = parmsObj;
        // copy all parmsObj attributes into the MCPxxxx Base class.
        this.priChipName = parmsObj.priChipName;
        this.pin = parmsObj.pin;
        this.do_reset = parmsObj.do_reset;
        this.pin_on = parmsObj.pin_on;
        this.read_pin = parmsObj.read_pin;
        this.set_pin = parmsObj.set_pin;
        this.has_full_keyed_data = parmsObj.has_full_keyed_data;
        this.monitor_intrp = parmsObj.monitor_intrp;
        this.has_full_pin_keyed_data = parmsObj.has_full_pin_keyed_data;
        this.gpio_num = parmsObj.gpio_num;
        this.has_up_down = false;
        this.intrpt_count = parmsObj.intrpt_count;
        this.ffdc = ffdc;
        this.banked = false; // always false to lock in register address scheme
        this.bank_capable = bank_capable;
        this.intfA = parmsObj.intfA;
        this.intfB = parmsObj.intfB;
        this.dioPinData = dioPinData;
        this.full_keyed_data = parmsObj.full_keyed_data;
        this.full_pin_keyed_data = parmsObj.full_pin_keyed_data;
        this.has_IOCON_keyed_data = parmsObj.has_IOCON_keyed_data;
        this.IOCON_keyed_data = parmsObj.IOCON_keyed_data;
        this.pinName = parmsObj.pinName;
        this.priChipBus_num = parmsObj.priChipBus_num;
        this.priChipAddress = parmsObj.priChipAddress;
        this.config_info = parmsObj.config_info;
        this.off_on = parmsObj.off_on;
        this.up_down = parmsObj.up_down;
        this.gpio_reset = parmsObj.gpio_reset;

    }

    /**
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapFirst8() {
        byte regAddr[] = {};
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
        byte regAddr[] = {};
        return (regAddr);
    }

    /**
     * PrettyPrint register values
     */
    public void dump_regs() {
        this.ffdc.ffdcMethodEntry("dump_regs ");

        String reg_name[] = { "_IODIR  ", "_IPOL   ", "_GPINTE ", "_DEFVAL ", "_INTCON ", "_IOCON  ", "_GPPU   ",
                "_INTF   ", "_INTCAP ", "_GPIO   ", "_OLAT   " };
        byte regAddr[] = this.getAddrMapFirst8();
        this.ffdc.ffdcDebugEntry("this.getAddrMapFirst8() function returned     " + this.getAddrMapFirst8());

        String reg_nameB[] = { "_IODIRB ", "_IPOLB  ", "_GPINTEB", "_DEFVALB", "_INTCONB", "_IOCON  ", "_GPPUB  ",
                "_INTFB  ", "_INTCAPB", "_GPIOB  ", "_OLATB  " };
        String[][] pin_name = { { "IO7    IO6    IO5    IO4    IO3    IO2    IO1    IO0" },
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

        this.ffdc.ffdcDebugEntry("this.getAddrMapSecond8() function returned     " + this.getAddrMapSecond8());
        String regAstr = "";
        int reg ;

        for (int i = 0; i < reg_name.length; i++) {
            // this.ffdc.ffdcDebugEntry(" regAddr " + regAddr[i]);
            // System.out.println(" regAddr " + regAddr[i]);

            reg = this.readRegister(regAddr[i]);
            regAstr = regAstr.concat("\n   Reg " + reg_name[i] + " offset ("+ i + ")  data: "  +   String.format("0x%02X", reg) + "\n");
            int val =  reg;
            // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1 pin0");
            regAstr = regAstr.concat(pin_name[i][0] + "\n");
            regAstr = regAstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                    + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
        }
        this.ffdc.ffdcDebugEntry(regAstr);
        //System.out.println(regAstr);
        if (this.bank_capable) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                 e.printStackTrace();
            }
            String regBstr = "";
            int regB;

            for (int i = 0; i < reg_nameB.length; i++) {
                // this.ffdc.ffdcDebugEntry(" regAddrB " + regAddrB[i]);
                // System.out.println(" regAddrB " + regAddrB[i]);

                regB = this.readRegister(regAddrB[i]);
                regBstr = regBstr.concat("\n   RegB " + reg_nameB[i] + " offset ("+ i + ")  data: "  +   String.format("0x%02X", regB) + "\n");
                int val =  regB;
                // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1
                // pin0");
                regBstr = regBstr.concat(pin_name[i][0] + "\n");
                regBstr = regBstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                        + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3)
                        + "      " + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01))
                        + "\n");
            }
            this.ffdc.ffdcDebugEntry(regBstr);
            //System.out.println(regBstr);
        }

        this.ffdc.ffdcMethodExit("dump_regs");
    }

    public void pgmSleep(int mills) {
        this.ffdc.ffdcMethodEntry("pgmSleep mills : " + String.format("0x%02X", mills));
        this.gpio.sleepMS(mills, this.ffdc);
        this.ffdc.ffdcMethodExit("pgmSleep");
    }

    public void reset_chip() {
        this.ffdc.ffdcMethodEntry("reset_chip ");
        this.gpio.reset_chip(this.gpio_reset,this.pi4j,5000,true, this.ffdc);
        this.ffdc.ffdcMethodExit("reset_chip");
    }

    /**
     *
     * @param pin         MCP pin to drive
     * @param pin_on       If true drive HIGH, else drive LOW
     * @throws InterruptedException
     * @throws IOException
     */
    public void drive_pin(int pin, boolean pin_on)
            throws InterruptedException, IOException {
        // get the regs and make sure the desired pin is configed as output. Log
        // error if not
        int configed;
        int b;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte first8[] = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[this.cfgData._IODIR];

        byte thisOffsetGPI = first8[this.cfgData._GPIO];
        byte thisOffsetOLA = first8[this.cfgData._OLAT];
        if (pin > 7) {
            absPin = absPin - 8;
            byte second8[] = this.getAddrMapSecond8();

            thisOffsetIOD = second8[this.cfgData._IODIRB];
            thisOffsetGPI = second8[this.cfgData._GPIOB];
            thisOffsetOLA = second8[this.cfgData._OLATB];
        }

        this.ffdc.ffdcMethodEntry("drive_pin  pin" + String.format("0x%02X", pin) + " pin_on : " + pin_on);

        configed = this.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) > 0) {
            System.out.println("Pin" + pin + "  not configured for output");
            System.exit(500);
        }
        reg = this.readRegister( thisOffsetGPI);
        // System.out.println("read GPI " + String.format("0x%02X", reg[0]));
        if (pin_on) {
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
        this.writeByte(thisOffsetGPI, (byte) reg);

        // OLAT
        reg = this.readRegister( thisOffsetOLA);
        if (pin_on) {
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg =  (reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (reg & b);
        }
        reg = reg & 0xff;

        this.writeByte(thisOffsetOLA, (byte) reg);

        this.ffdc.ffdcMethodExit("drive_pin");
    }

    /**
     *
      * @param pin    MCP pin to read
     *               <p>
     *                Pin read and detail logged.
     *               </p>
     * @throws InterruptedException
     * @throws IOException
     */
    public void read_input(int pin)
            throws InterruptedException, IOException {
        // # get the regs and make sure the desired pin is configed as input.
        // Log error if not
        int configed;
        int b;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte first8[] = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[this.cfgData._IODIR];
        byte thisOffsetGPI = first8[this.cfgData._GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte second8[] = this.getAddrMapSecond8();
            thisOffsetIOD = second8[this.cfgData._IODIRB];
            thisOffsetGPI = second8[this.cfgData._GPIOB];
        }

        this.ffdc.ffdcMethodEntry(" read_input  pin " + String.format("0x%02X", pin));

        configed = this.readRegister(thisOffsetIOD);
        // System.out.println("configed from _IODIR : " +
        // String.format("0x%02X", configed[0]));
        // System.out.println("(configed[0] & (1 << pin)) : " + (configed[0] &
        // (1 << pin)));
        // System.out.println("(configed[0] & (1 << pin))>0 : " + ( (configed[0]
        // & (1 << pin))>0) );

        if ((configed & (1 << absPin)) == 0) {
            this.ffdc.ffdcConfigWarningEntry("Pin" + String.format("0x%02X", pin) + "  not configured for input");
            this.ffdc.ffdcErrorExit("Incorrect Pin direction",  510);
        }

        reg = this.readRegister( thisOffsetGPI);
        if ((reg & (1 << absPin)) == 0) {
            System.out.println("Pin" + pin + " Low");
            this.ffdc.ffdcDebugEntry("Pin" + pin + " Low");
        } else {
            System.out.println("Pin" + pin + " High");
            this.ffdc.ffdcDebugEntry("Pin" + pin + " High");
        }
        this.ffdc.ffdcMethodExit(" read_input");
    }


    /**
     *
     * @param on_off   Enable listener if true, else (at present disable not possible)
     * @param gpioPin  Pi GPIO to apply the listener
     * These listen to pi DIO interrupts. If on occurs this pgm handler is
     *  called. The handler then
     *  interrogates the 23xxx chip to see which of its pins created the
     *  interrupt.
     *                 <P>
     *                 PreCond:  BaseGpioInOut contains a relevant GpioPinCfgData.
     *                 This data supplied by the program user must include gpioPin
     *                 as Input and the needed pull_down value
     *                 </P>
     */
    public void addListener(String on_off,int gpioPin) {
        // find BCM number for this pin
        this.ffdc.ffdcMethodEntry(" addListener on_off : " + on_off + "  pin" + gpioPin);
        int intPin = gpioPin;
        if (on_off.equals("on")) {
            this.gpio.getCfgData(intPin).input.addListener((DigitalStateChangeListener) new McpBaseIntrpListener(this));
        } else if (on_off.equals("off")) {
            ; // cannot remove handler (yet)
        } else {
            this.ffdc.ffdcErrorExit("addListener: invalid on_off", 521);
        }
        this.ffdc.ffdcMethodExit(" addListener ");
    }

    /**
     *  <p>
     *
     *  </p>When a Pi gpio detects a level change, if a Listener is attached, this
     *  method is called.
     *  </p>
     *  <p>
     *      The method will examine registers in the MCP chip to determine which
     *      pin changed and caused the interrupt. When the interrupting pin is
     *      determined, the processPinInterrupt method in a subclasss is called.
     *  </p>
     *
     *
     * @param event     DigitalStateChangeEvent
     */
    public void intrp_happened(DigitalStateChangeEvent event) {
        // System.out.println("intrp_happened");
        this.ffdc.ffdcMethodEntry(
                " intrp_happened : GPIO PIN STATE CHANGE: " + event.state());// figure
        // out, which, pins // interrupted
        int reg;
        boolean foundIntrBit = false;
        int pin_num = 0;
        int testVal = 0;
        DigitalState effectedPinState = DigitalState.HIGH;
        if (this.pin < 8) {
            byte first8[] = this.getAddrMapFirst8();

            reg =  this.readRegister(first8[this.cfgData._INTF]);
            // find the bit (pin) that interrupted this time.
            testVal = reg;
            testVal = testVal & 0xff;
            pin_num = 0;
            this.ffdc.ffdcDebugEntry("A reg INTF " + String.format("0x%02X", reg));

            this.ffdc.ffdcDebugEntry("Pin 0 - 7, inspect _INTCAP ");
            for (int c = 0; c < 8; c++) {
                if ((testVal >> c) == 1) {
                    pin_num = c;
                    foundIntrBit = true;
                    // get pin state for c + *. Set effectedPinState
                    reg =  this.readRegister( first8[this.cfgData._INTCAP]);
                    testVal = reg;
                    testVal = testVal & 0xff;
                    if ((testVal >> c) == 1) {
                        effectedPinState = DigitalState.HIGH;
                    } else {
                        effectedPinState = DigitalState.LOW;
                    }
                    this.ffdc.ffdcDebugEntry("A reg interrupt on pin_num :" + pin_num);
                    break;
                }
            }
        } else {
            if ((foundIntrBit == false) && (this.pin > 7)) { // search B bank
                this.ffdc.ffdcDebugEntry("Pin 8 - 15, inspect _INTCAPB ");
                byte second8[] = this.getAddrMapSecond8();

                reg = this.readRegister( second8[this.cfgData._INTFB]);
                // find the bit (pin) that interrupted this time.
                testVal = reg;
                testVal = testVal & 0xff;
                pin_num = 0;
                this.ffdc.ffdcDebugEntry("B reg " + String.format("0x%02X", reg));
                for (int c = 0; c < 8; c++) {
                    if ((testVal >> c) == 1) {
                        pin_num = c + 8;
                        foundIntrBit = true;
                        // get pin state for c + *. Set effectedPinState
                        reg = this.readRegister(second8[this.cfgData._INTCAPB]);
                        testVal = reg;
                        testVal = testVal & 0xff;
                        if ((testVal >> c) == 1) {
                            effectedPinState = DigitalState.HIGH;
                        } else {
                            effectedPinState = DigitalState.LOW;
                        }
                        this.ffdc.ffdcDebugEntry("B reg interrupt on  pin_num : " + pin_num);
                        break;
                    }
                }

            }
        } // pin 8-15

        if (foundIntrBit == false) {
            this.ffdc.ffdcDebugEntry("Bit not found in _INTF(B) ");
        } else {

            String[] pin_list = { "pin0", "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8", "pin9",
                    "pin10", "pin11", "pin12", "pin13", "pin14", "pin15" };
            if (event.state() == DigitalState.LOW) {
                this.ffdc.ffdcDebugEntry("    McpBase  GPIO " + pin_num + " LOW, chip pin: " + pin_list[pin_num]
                        + " State: " + effectedPinState);
            } else {
                this.ffdc.ffdcDebugEntry("    McpBase  GPIO " + pin_num + " HIGH, chip pin: " + pin_list[pin_num]
                        + " State: " + effectedPinState);
            }
            if (this.pin == pin_num) {
                this.processPinInterrupt(pin_num, effectedPinState, this.ffdc);
            } else {
                this.ffdc.ffdcDebugEntry(
                        "    McpBase  effected pin: " + pin_num + " is not our monitored pin: " + this.pin);
            }
            this.intrpt_count++;
            this.ffdc.ffdcDebugEntry(
                    "Interrupt occured " + "  interrupt count : " + String.format("0x%02X", this.intrpt_count));
        }
        this.ffdc.ffdcMethodExit(" intrp_happened");
    }

    /**
     * <p>
     *     The  McpBase subclass installed event handler will have control.
     * </p>
     * @param pinNum     MVP pin causing the interrupt
     * @param pinState   Pi Gpio pin state detected
     * @param ffdc       logging
     * @return  true if interrupt processed, false if failed
     */
    public boolean processPinInterrupt(int pinNum, DigitalState pinState, FfdcUtil ffdc) {
        boolean rval = false;
        this.ffdc.ffdcMethodEntry("BaseClass processPinInterrupt PIN " + pinNum);// figure

        this.ffdc.ffdcMethodExit("BaseClass processPinInterrupt");

        return (rval);
    }

    // , -b bus, -a address
    public void usage() {
        System.out.println("options   -h 'help', -d drive-pin with -o ON/OFF, "
                + "-r read-pin , -k \"{'dir':'out','int_ena':'no'}\" (using -d or -r)"
                + "-m \"{'pin1':{'dir':'in','pull':'down','default':'0','do_compare':'yes','int_ena':'yes','act':'high'}}\""
                + "-p (uses compiled in pin data) " + "-c primary chip     -p primary pin"
                + "-z  gpios config dict     -q mainChip"
                + "Interrupt detect via gpio -p, monitoring pin -p  -g gpio, -i on./off  " +
                " -x reset-chip  -n resetPin -f ffdc_lvl   -y dumpRegs ");
    }

    /**
     * CTL-C handler to attempt clean shutdown
     */
    private class ShutDownTask extends Thread {
        public ShutDownTask(McpBase mcp) {
        }

        @Override
        public void run() {
            System.out.println("Performing ctl-C shutdown");
            // TODO
          //  this.ffdc.ffdcFlushShutdown(); // push all logs to the file
            // Shutdown Pi4J
           // pi4j.shutdown();

        }

    }

    boolean read_pin;
    boolean pin_on;
    boolean set_pin;
    String full_keyed_data;
    String full_pin_keyed_data;
    boolean has_full_keyed_data;
    boolean has_full_pin_keyed_data;

    boolean has_IOCON_keyed_data;
    String IOCON_keyed_data;

    public String priChipName;
    String pinName;
    int priChipBus_num;
    int priChipAddress;

    boolean do_reset;

    byte config_info;
    byte intfA;
    byte intfB;
    int gpio_num;
    String off_on;
    String up_down;
    boolean has_up_down;
    int intrpt_count;
    boolean tmpFileUse;
    int gpio_reset;
    boolean banked;
    boolean bank_capable;

    public boolean monitor_intrp;
    public int pin;
    public HashMap<Integer, GpioPinCfgData> dioPinData;
    public McpConfigData cfgData;
    public BaseGpioInOut gpio;
    public MapUtil mapUtils;
    public AppConfigUtilities cfgU;
    public PinInterruptActionIntf[] jumpTable;

    protected McpBase chip;

    public Mcp23xxxParms parmsObj;



    public class McpBaseIntrpListener implements DigitalStateChangeListener {
        ;
        public McpBaseIntrpListener(McpBase chip) {
            this.chip = chip;
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("McpBaseIntrpListener: Performing ctl-C shutdown");
                    // Thread.dumpStack();
                }
            });
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            // display pin state on console
            // ystem.out.println(" Matrix -->Utility : GPIO PIN STATE CHANGE: "
            // + event.getPin() + " = " + event.getState());

            if (event.state() == DigitalState.LOW) {
                // System.out.println("Pin went low");

                this.chip.intrp_happened(event);
            }
        }
         McpBase chip;
    }



}
