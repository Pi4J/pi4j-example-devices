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

import com.pi4j.context.Context;
import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.devices.base_i2c.MutableI2cDevice;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.devices.mcp23xxxApplication.PinInterruptActionIntf;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.util.Console;

import java.util.HashMap;

/**
 * McpBase
 * <p>
 * Base class for simple MCP23008 and MCP23017 implementations and the more complex
 * Application for these two classes
 * </p>
 */
public class McpBase extends MutableI2cDevice {

    /**
     * CTOR
     *
     * @param parmsObj    Contains the many options supplied to the main functions
     * @param bankCapable If true a MCP23017 chip, else a MCP23008 chip
     * @param dioPinData  MCP chip pin  configuration objects
     * @param pi4j        Context
     * @param ffdc        Logging
     * @param console     Console
     */
    public McpBase(Mcp23xxxParms parmsObj, boolean bankCapable, HashMap<Integer, GpioPinCfgData> dioPinData, Context pi4j, FfdcUtil ffdc, Console console) {
        super(pi4j, ffdc, console);
        this.parmsObj = parmsObj;
        // copy all parmsObj attributes into the MCPxxxx Base class.
        this.priChipName = parmsObj.priChipName;
        this.pin = parmsObj.pin;
        this.doReset = parmsObj.doReset;
        this.pinOn = parmsObj.pinOn;
        this.readPin = parmsObj.readPin;
        this.setPin = parmsObj.setPin;
        this.hasFullKeyedData = parmsObj.hasFullKeyedData;
        this.monitorIntrp = parmsObj.monitorIntrp;
        this.hasFullPinKeyedData = parmsObj.hasFullPinKeyedData;
        this.gpioNum = parmsObj.gpioNum;
        this.hasUpDown = false;
        this.intrptCount = parmsObj.intrptCount;
        this.ffdc = ffdc;
        this.banked = false; // always false to lock in register address scheme
        this.bankCapable = bankCapable;
        this.intfA = parmsObj.intfA;
        this.intfB = parmsObj.intfB;
        this.dioPinData = dioPinData;
        this.fullKeyedData = parmsObj.fullKeyedData;
        this.fullPinKeyedData = parmsObj.fullPinKeyedData;
        this.hasIOCONKeyedData = parmsObj.hasIOCONKeyedData;
        this.IOCONKeyedData = parmsObj.IOCONKeyedData;
        this.pinName = parmsObj.pinName;
        this.priChipBusNum = parmsObj.priChipBusNum;
        this.priChipAddress = parmsObj.priChipAddress;
        this.configInfo = parmsObj.configInfo;
        this.offOn = parmsObj.offOn;
        this.upDown = parmsObj.upDown;
        this.gpioReset = parmsObj.gpioReset;

    }

    /**
     * @return Array of register addresses, offset defined by McpConfigData
     * <p>
     * Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapFirst8() {
        byte[] regAddr = {};
        return (regAddr);
    }

    /**
     * @return Array of register addresses, offset defined by McpConfigData
     * <p>
     * Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapSecond8() {
        byte[] regAddr = {};
        return (regAddr);
    }

    /**
     * PrettyPrint register values
     */
    public void dumpRegs() {
        this.ffdc.ffdcMethodEntry("dump_regs ");

        String[] regName = {"_IODIR  ", "_IPOL   ", "_GPINTE ", "_DEFVAL ", "_INTCON ", "_IOCON  ", "_GPPU   ",
            "_INTF   ", "_INTCAP ", "_GPIO   ", "_OLAT   "};
        byte[] regAddr = this.getAddrMapFirst8();
        this.ffdc.ffdcDebugEntry("this.getAddrMapFirst8() function returned     " + this.getAddrMapFirst8());

        String[] regNameB = {"_IODIRB ", "_IPOLB  ", "_GPINTEB", "_DEFVALB", "_INTCONB", "_IOCON  ", "_GPPUB  ",
            "_INTFB  ", "_INTCAPB", "_GPIOB  ", "_OLATB  "};
        String[][] pinName = {{"IO7    IO6    IO5    IO4    IO3    IO2    IO1    IO0"},
            {"IP7    IP6    IP5    IP4    IP3    IP2    IP1    IP0   "},
            {"GPINT7 GPINT6 GPINT5 GPINT4 GPINT3 GPINT2 GPINT1 GPINT0  "},
            {"DEF7   DEF6   DEF5   DEF4   DEF3   DEF2   DEF1   DEF0    "},
            {"IOC7   IOC6   IOC5   IOC4   IOC3   IOC2   IOC1   IOC0    "},
            {"BANK   MIRROR SEQOP  DISSLW HAEN   ODR    INTPOL     "},
            {"PU7    PU6    PU5    PU4    PU3    PU2    PU1    PU0     "},
            {"INT7   INT6   INT5   INT4   INT3   INT2   INT1   INT0    "},
            {"ICP7   ICP6   ICP5   ICP4   ICP3   ICP2   ICP1   ICP0    "},
            {"GP7    GP6    GP5    GP4    GP3    GP2    GP1    GP0     "},
            {"OL7    OL6    OL5    OL4    OL3    OL2    OL1    OL0     "},};
        byte[] regAddrB = this.getAddrMapSecond8();

        this.ffdc.ffdcDebugEntry("this.getAddrMapSecond8() function returned     " + this.getAddrMapSecond8());
        String regAstr = "";
        int reg;

        for (int i = 0; i < regName.length; i++) {
            // this.ffdc.ffdcDebugEntry(" regAddr " + regAddr[i]);
            // System.out.println(" regAddr " + regAddr[i]);

            reg = this.readRegister(regAddr[i]);
            regAstr = regAstr.concat("\n   Reg " + regName[i] + " offset (" + i + ")  data: " + String.format("0x%02X", reg) + "\n");
            int val = reg;
            // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1 pin0");
            regAstr = regAstr.concat(pinName[i][0] + "\n");
            regAstr = regAstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
        }
        this.ffdc.ffdcDebugEntry(regAstr);
        //System.out.println(regAstr);
        if (this.bankCapable) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String regBstr = "";
            int regB;

            for (int i = 0; i < regNameB.length; i++) {
                // this.ffdc.ffdcDebugEntry(" regAddrB " + regAddrB[i]);
                // System.out.println(" regAddrB " + regAddrB[i]);

                regB = this.readRegister(regAddrB[i]);
                regBstr = regBstr.concat("\n   RegB " + regNameB[i] + " offset (" + i + ")  data: " + String.format("0x%02X", regB) + "\n");
                int val = regB;
                // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1
                // pin0");
                regBstr = regBstr.concat(pinName[i][0] + "\n");
                regBstr = regBstr.concat(" " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3)
                    + "      " + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01))
                    + "\n");
            }
            this.ffdc.ffdcDebugEntry(regBstr);
            //System.out.println(regBstr);
        }

        this.ffdc.ffdcMethodExit("dumpRegs");
    }

    public void pgmSleep(int mills) {
        this.ffdc.ffdcMethodEntry("pgmSleep mills : " + String.format("0x%02X", mills));
        this.gpio.sleepMS(mills, this.ffdc);
        this.ffdc.ffdcMethodExit("pgmSleep");
    }

    public void resetChip() {
        this.ffdc.ffdcMethodEntry("resetChip ");
        this.gpio.resetChip(this.gpioReset, this.pi4j, 5000, true, this.ffdc);
        this.ffdc.ffdcMethodExit("resetChip");
    }

    /**
     * @param pin   MCP pin to drive
     * @param pinOn If true drive HIGH, else drive LOW
     */
    public void drivePin(int pin, boolean pinOn) {
        // get the regs and make sure the desired pin is configed as output. Log
        // error if not
        int configed;
        int b;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte[] first8 = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[this.cfgData._IODIR];

        byte thisOffsetGPI = first8[this.cfgData._GPIO];
        byte thisOffsetOLA = first8[this.cfgData._OLAT];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.getAddrMapSecond8();

            thisOffsetIOD = second8[this.cfgData._IODIRB];
            thisOffsetGPI = second8[this.cfgData._GPIOB];
            thisOffsetOLA = second8[this.cfgData._OLATB];
        }

        this.ffdc.ffdcMethodEntry("drivePin  pin" + String.format("0x%02X", pin) + " pinOn : " + pinOn);

        configed = this.readRegister(thisOffsetIOD);

        if ((configed & (1 << absPin)) > 0) {
            System.out.println("Pin" + pin + "  not configured for output");
            System.exit(500);
        }
        reg = this.readRegister(thisOffsetGPI);
        // System.out.println("read GPI " + String.format("0x%02X", reg[0]));
        if (pinOn) {
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg = (byte) (reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (byte) (reg & b);
        }

        // System.out.println("write GPI " + String.format("0x%02X", reg[0]));

        reg = reg & 0xff;
        this.writeByte(thisOffsetGPI, (byte) reg);

        // OLAT
        reg = this.readRegister(thisOffsetOLA);
        if (pinOn) {
            Integer integerObject = ((1 << absPin));
            b = integerObject.byteValue();
            reg = (reg | b);
        } else {
            Integer integerObject = (~(1 << absPin));
            b = integerObject.byteValue();
            reg = (reg & b);
        }
        reg = reg & 0xff;

        this.writeByte(thisOffsetOLA, (byte) reg);

        this.ffdc.ffdcMethodExit("drivePin");
    }

    /**
     * @param pin MCP pin to read
     *            <p>
     *            Pin read and detail logged.
     *            </p>
     */
    public void readInput(int pin) {
        // # get the regs and make sure the desired pin is configed as input.
        // Log error if not
        int configed;
        int reg;
        int absPin = pin; // if in second bank must subtract 8
        byte[] first8 = this.getAddrMapFirst8();

        byte thisOffsetIOD = first8[this.cfgData._IODIR];
        byte thisOffsetGPI = first8[this.cfgData._GPIO];
        if (pin > 7) {
            absPin = absPin - 8;
            byte[] second8 = this.getAddrMapSecond8();
            thisOffsetIOD = second8[this.cfgData._IODIRB];
            thisOffsetGPI = second8[this.cfgData._GPIOB];
        }

        this.ffdc.ffdcMethodEntry(" readInput  pin " + String.format("0x%02X", pin));

        configed = this.readRegister(thisOffsetIOD);
        // System.out.println("configed from _IODIR : " +
        // String.format("0x%02X", configed[0]));
        // System.out.println("(configed[0] & (1 << pin)) : " + (configed[0] &
        // (1 << pin)));
        // System.out.println("(configed[0] & (1 << pin))>0 : " + ( (configed[0]
        // & (1 << pin))>0) );

        if ((configed & (1 << absPin)) == 0) {
            this.ffdc.ffdcConfigWarningEntry("Pin" + String.format("0x%02X", pin) + "  not configured for input");
            this.ffdc.ffdcErrorExit("Incorrect Pin direction", 510);
        }

        reg = this.readRegister(thisOffsetGPI);
        if ((reg & (1 << absPin)) == 0) {
            System.out.println("Pin" + pin + " Low");
            this.ffdc.ffdcDebugEntry("Pin" + pin + " Low");
        } else {
            System.out.println("Pin" + pin + " High");
            this.ffdc.ffdcDebugEntry("Pin" + pin + " High");
        }
        this.ffdc.ffdcMethodExit(" readInput");
    }


    /**
     * @param onOff   Enable listener if true, else (at present disable not possible)
     * @param gpioPin Pi GPIO to apply the listener
     *                These listen to pi DIO interrupts. If on occurs this pgm handler is
     *                called. The handler then
     *                interrogates the 23xxx chip to see which of its pins created the
     *                interrupt.
     *                <p>
     *                PreCond:  BaseGpioInOut contains a relevant GpioPinCfgData.
     *                This data supplied by the program user must include gpioPin
     *                as Input and the needed pullDown value
     *                </P>
     */
    public void addListener(String onOff, int gpioPin) {
        // find BCM number for this pin
        this.ffdc.ffdcMethodEntry(" addListener onOff : " + onOff + "  pin" + gpioPin);
        int intPin = gpioPin;
        if (onOff.equals("on")) {
            this.gpio.getCfgData(intPin).input.addListener(new McpBaseIntrpListener(this));
        } else if (onOff.equals("off")) {
            // cannot remove handler (yet)
        } else {
            this.ffdc.ffdcErrorExit("addListener: invalid onOff", 521);
        }
        this.ffdc.ffdcMethodExit(" addListener ");
    }

    /**
     * <p>
     *
     * </p>When a Pi gpio detects a level change, if a Listener is attached, this
     * method is called.
     * </p>
     * <p>
     * The method will examine registers in the MCP chip to determine which
     * pin changed and caused the interrupt. When the interrupting pin is
     * determined, the processPinInterrupt method in a subclasss is called.
     * </p>
     *
     * @param event DigitalStateChangeEvent
     */
    public void intrpHappened(DigitalStateChangeEvent event) {
        // System.out.println("intrpHappened");
        this.ffdc.ffdcMethodEntry(
            " intrpHappened : GPIO PIN STATE CHANGE: " + event.state());// figure
        // out, which, pins // interrupted
        int reg;
        boolean foundIntrBit = false;
        int pinNum = 0;
        int testVal = 0;
        DigitalState effectedPinState = DigitalState.HIGH;
        if (this.pin < 8) {
            byte[] first8 = this.getAddrMapFirst8();

            reg = this.readRegister(first8[this.cfgData._INTF]);
            // find the bit (pin) that interrupted this time.
            testVal = reg;
            testVal = testVal & 0xff;
            pinNum = 0;
            this.ffdc.ffdcDebugEntry("A reg INTF " + String.format("0x%02X", reg));

            this.ffdc.ffdcDebugEntry("Pin 0 - 7, inspect _INTCAP ");
            for (int c = 0; c < 8; c++) {
                if ((testVal >> c) == 1) {
                    pinNum = c;
                    foundIntrBit = true;
                    // get pin state for c + *. Set effectedPinState
                    reg = this.readRegister(first8[this.cfgData._INTCAP]);
                    testVal = reg;
                    testVal = testVal & 0xff;
                    if ((testVal >> c) == 1) {
                        effectedPinState = DigitalState.HIGH;
                    } else {
                        effectedPinState = DigitalState.LOW;
                    }
                    this.ffdc.ffdcDebugEntry("A reg interrupt on pin_Nm :" + pinNum);
                    break;
                }
            }
        } else {
            if (this.pin > 7) { // search B bank
                this.ffdc.ffdcDebugEntry("Pin 8 - 15, inspect _INTCAPB ");
                byte[] second8 = this.getAddrMapSecond8();

                reg = this.readRegister(second8[this.cfgData._INTFB]);
                // find the bit (pin) that interrupted this time.
                testVal = reg;
                testVal = testVal & 0xff;
                pinNum = 0;
                this.ffdc.ffdcDebugEntry("B reg " + String.format("0x%02X", reg));
                for (int c = 0; c < 8; c++) {
                    if ((testVal >> c) == 1) {
                        pinNum = c + 8;
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
                        this.ffdc.ffdcDebugEntry("B reg interrupt on  pinNum : " + pinNum);
                        break;
                    }
                }

            }
        } // pin 8-15

        if (!foundIntrBit) {
            this.ffdc.ffdcDebugEntry("Bit not found in _INTF(B) ");
        } else {

            String[] pinList = {"pin0", "pin1", "pin2", "pin3", "pin4", "pin5", "pin6", "pin7", "pin8", "pin9",
                "pin10", "pin11", "pin12", "pin13", "pin14", "pin15"};
            if (event.state() == DigitalState.LOW) {
                this.ffdc.ffdcDebugEntry("    McpBase  GPIO " + pinNum + " LOW, chip pin: " + pinList[pinNum]
                    + " State: " + effectedPinState);
            } else {
                this.ffdc.ffdcDebugEntry("    McpBase  GPIO " + pinNum + " HIGH, chip pin: " + pinList[pinNum]
                    + " State: " + effectedPinState);
            }
            if (this.pin == pinNum) {
                this.processPinInterrupt(pinNum, effectedPinState, this.ffdc);
            } else {
                this.ffdc.ffdcDebugEntry(
                    "    McpBase  effected pin: " + pinNum + " is not our monitored pin: " + this.pin);
            }
            this.intrptCount++;
            this.ffdc.ffdcDebugEntry(
                "Interrupt occured " + "  interrupt count : " + String.format("0x%02X", this.intrptCount));
        }
        this.ffdc.ffdcMethodExit(" intrpHappened");
    }

    /**
     * <p>
     * The  McpBase subclass installed event handler will have control.
     * </p>
     *
     * @param pinNum   MVP pin causing the interrupt
     * @param pinState Pi Gpio pin state detected
     * @param ffdc     logging
     * @return true if interrupt processed, false if failed
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

    boolean readPin;
    boolean pinOn;
    boolean setPin;
    String fullKeyedData;
    String fullPinKeyedData;
    boolean hasFullKeyedData;
    boolean hasFullPinKeyedData;

    boolean hasIOCONKeyedData;
    String IOCONKeyedData;

    public String priChipName;
    String pinName;
    int priChipBusNum;
    int priChipAddress;

    boolean doReset;

    byte configInfo;
    byte intfA;
    byte intfB;
    int gpioNum;
    String offOn;
    String upDown;
    boolean hasUpDown;
    int intrptCount;
    int gpioReset;
    boolean banked;
    boolean bankCapable;

    public boolean monitorIntrp;
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
            // system.out.println(" Matrix -->Utility : GPIO PIN STATE CHANGE: "
            // + event.getPin() + " = " + event.getState());

            if (event.state() == DigitalState.LOW) {
                // System.out.println("Pin went low");

                this.chip.intrpHappened(event);
            }
        }

        McpBase chip;
    }


}
