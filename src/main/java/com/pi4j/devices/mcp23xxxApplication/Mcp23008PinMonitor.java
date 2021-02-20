/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23008PinMonitor.java
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

package com.pi4j.devices.mcp23xxxApplication;

        import com.pi4j.Pi4J;
        import com.pi4j.devices.appConfig.AppConfigUtilities;
        import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
        import com.pi4j.devices.base_util.mapUtil.MapUtil;
        import com.pi4j.devices.mcp23008.Mcp23008;

        import com.pi4j.context.Context;
        import com.pi4j.devices.base_util.ffdc.FfdcUtil;
        import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
        import com.pi4j.devices.mcp23xxxCommon.Mcp23xxxUtil;
        import com.pi4j.devices.mcp23xxxCommon.McpConfigData;
        import com.pi4j.exception.LifecycleException;
        import com.pi4j.io.gpio.digital.DigitalState;
        import com.pi4j.util.Console;

        import java.util.HashMap;


        import sun.misc.Signal;
        import sun.misc.SignalHandler;

/**
 * Mcp23008PinMonitor
 *
 * <p>
 *     Read users parameters, control configuring the MCP23008 chip,
 *     reading and writing to pin, and more importantly monitor for
 *     changes on a specific input pin using the MCP23008 Interrupt
 *     line.
 * </p>
 */
public class Mcp23008PinMonitor extends Mcp23008 implements Mcp23xxxPinMonitorIntf{
    /**
     *   CTOR
     * @param pi4j  contect
     * @param parms  users command line parameters
     * @param ffdc    loging
     * @param dioPinData  chip pin configuration
     * @param console  Console
     */
    public Mcp23008PinMonitor(Context pi4j, Mcp23xxxParms parms , FfdcUtil ffdc,
                              HashMap<Integer, GpioPinCfgData> dioPinData, Console console) {

        super(pi4j,parms,ffdc, dioPinData, console);
        this.jumpTable = new PinInterruptActionIntf[8];
        // TODO Auto-generated constructor stub
    }


    /**
     * <p>
     *     Using the listener interface available on Pi GPios,assign an
     *     interruptAction to each pin.  The interrupt action for this example are
     *     subblasses of this packages PinInterruptBase.
     * </p>
     */
    public void installInterruptHandler() {
        // TODO Auto-generated method stub
        this.ffdc.ffdcMethodEntry("installInterruptHandler");

        for (int i = 0; i < 7; i++) {
            System.out.println("");
            PinInterruptDefault dummy = new PinInterruptDefault(this.pi4j, this.pin, this.ffdc, this,
                    this.dioPinData, this.cfgU, this.priChipName);
            this.jumpTable[i] = new PinInterruptActionIntf() {
                public void interruptAction(int pinNumber, DigitalState pinState) {
                    dummy.dummyAct(pinNumber, pinState);
                }
            };
        }

        PinInterruptLED action = new PinInterruptLED(this.pi4j, this.pin, this.ffdc, this, this.dioPinData, cfgU,
                this.priChipName);
        this.jumpTable[3] = new PinInterruptActionIntf() {
            public void interruptAction(int pinNumber, DigitalState pinState) {
                action.changeLed(pinNumber, pinState);
            }
        };
        this.jumpTable[4] = new PinInterruptActionIntf() {
            public void interruptAction(int pinNumber, DigitalState pinState) {
                action.changeLed(pinNumber, pinState);
            }
        };

        this.ffdc.ffdcMethodExit("installInterruptHandler");

    }

    /**
     *<p>
     *     For this piNum, call interruptAction on the interruptAction instance
     *     contained in the jumpTable
     *</p>
     * @param pinNum     MVP pin causing the interrupt
     * @param pinState   Pi Gpio pin state detected
     * @param ffdc       logging
     * @return  true if interrupt processed, false if failed
     */
    public boolean processPinInterrupt(int pinNum, DigitalState pinState, FfdcUtil ffdc) {
        boolean rval = false;
        this.ffdc.ffdcMethodEntry("Application processPinInterrupt PIN " + pinNum);// figure
        this.jumpTable[pinNum].interruptAction(pinNum, pinState);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            rval = false;
        }
        this.ffdc.ffdcMethodExit("Application processPinInterrupt  rval :" + rval);

        return (rval);
    }

    /**
     *  main
     *  <p>
     *      Command line access to the MCP23008 application code.
     *      First process the users input, then call various methods
     *      based upon the users parms.
     *  </p>
     * @param args   Users command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        var console = new Console();
        Context pi4j =  Pi4J.newAutoContext();

        // Print program title/header
        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23008PinMonitor");

        HashMap<Integer, GpioPinCfgData> dioPinData = new HashMap<Integer, GpioPinCfgData>();


        Mcp23xxxParms parmsObj = Mcp23xxxAppProcessParms.processMain(pi4j,args,false, dioPinData, console);

        FfdcUtil ffdc = new FfdcUtil(console, pi4j, parmsObj.ffdcControlLevel , Mcp23008PinMonitor.class);

        ffdc.ffdcDebugEntry("mcp23008PinMonitor : Arg processing completed...\n");

        Mcp23008PinMonitor mcpObj = new Mcp23008PinMonitor(parmsObj.pi4j, parmsObj, ffdc,  dioPinData, console);

        BaseGpioInOut gpio = new BaseGpioInOut(parmsObj.pi4j, mcpObj.ffdc, mcpObj.dioPinData);
        mcpObj.gpio = gpio;

        AppConfigUtilities cfgU = new AppConfigUtilities(parmsObj.pi4j, ffdc, mcpObj.gpio, console);
        mcpObj.cfgU = cfgU;

        mcpObj.mapUtils = new MapUtil(mcpObj.ffdc, mcpObj.gpio);

              // the bus is that of the main chip. The one connected to the Pi i2c
        // bus. The priChipBus may be some other
        // value if the prichipName is behind a mux.
        // If behind mux that was accounted for in the call enableGpioPath
        HashMap<String, String> chipDetails = cfgU.getChipMapRec(parmsObj.mainChip);
        if (chipDetails != null) {
            String chipBus = chipDetails.get("busNum");
            String chipAddr = chipDetails.get("address");
            parmsObj.address = Integer.parseInt(chipAddr.substring(2), 16);
            parmsObj.busNum = Integer.parseInt(chipBus.substring(2), 16);
       }
        HashMap<String, String> priChipDetails = cfgU.getChipMapRec(parmsObj.priChipName);
        if (chipDetails != null) {
            String chipBus = priChipDetails.get("busNum");
            String chipAddr = priChipDetails.get("address");
            parmsObj.priChipAddress = Integer.parseInt(chipAddr.substring(2), 16);
            parmsObj.priChipBusNum = Integer.parseInt(chipBus.substring(2), 16);
           // TODO parmsObj.address = Integer.parseInt(chipAddr.substring(2), 16);
        }

        mcpObj.cfgData = new McpConfigData(ffdc);



            // this
        System.out.println("Args to enable " + parmsObj.pinName + "   " + parmsObj.priChipName);
        boolean returned = cfgU.enableGpioPath(parmsObj.pinName, parmsObj.priChipName); // path
        // to
        // mcp23xx


        // do any extra interrupt handler setup
        mcpObj.installInterruptHandler();


        // set busNum and address based on mcpObj.priChipName

        // the bus is that of the main chip. The one connected to the Pi i2c
        // bus. The priChipBus may be some other
        // value if the prichipName is behind a mux.
        // If behind mux that was accounted for in the above call
        // cfgU.enableGpioPath
        Mcp23xxxUtil mcpUtil = new Mcp23xxxUtil(parmsObj.pi4j, ffdc,  parmsObj.busNum, parmsObj.priChipAddress, mcpObj.cfgData, mcpObj, console);

        // Prior to running methods, set up control-c handler
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Performing ctl-C shutdown");
                ffdc.ffdcFlushShutdown(); // push all logs to the file
                try {
                    pi4j.shutdown();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
                Thread.dumpStack();
                System.exit(2);
            }
        });

        if (parmsObj.hasFullKeyedData) { // -g
            HashMap<String, HashMap<String, String>> outerMap = mcpObj.mapUtils.createFullMap(parmsObj.fullKeyedData);
            mcpObj.cfgData.replaceMap(outerMap);
            gpio.createGpioInstance(mcpObj.cfgData.getFullMap());
        }

        if (parmsObj.doReset) {
            mcpObj.resetChip();
            // mcpObj.dumpRegs();
        }

        // do this before pin data as this will set 'banked', needed for correct
        // addressing
        if (parmsObj.hasIOCONKeyedData) { // -k
            HashMap<String, HashMap<String, String>> mMap;
            mMap = mcpObj.mapUtils.createFullMap(parmsObj.IOCONKeyedData);
            mcpObj.cfgData.replaceMap(mMap);
            mcpUtil.processKeyedData();
        }

        if (parmsObj.hasFullPinKeyedData) { // -m
            HashMap<String, HashMap<String, String>> mMap;
            mMap = mcpObj.mapUtils.createFullMap(parmsObj.fullPinKeyedData);
            mcpObj.cfgData.replaceMap(mMap);
            mcpUtil.processKeyedData();
        }

        System.out.println("Chip register configurations completed");
        mcpObj.reinit(parmsObj.priChipName, parmsObj.pinName,parmsObj.busNum, parmsObj.priChipAddress);

        if (parmsObj.dumpRegs) {
            mcpObj.dumpRegs();
            System.exit(0);
        }

       // mcpObj.cfgData.DumpGpiosConfig();

        if (parmsObj.setPin) {
            mcpObj.drivePin(parmsObj.pin, parmsObj.pinOn);

        }



        if (parmsObj.readPin) {
            mcpObj.readInput(parmsObj.pin);
        }

        if (parmsObj.monitorIntrp) {
            // spin and handle any interrupt that happens
            if ((parmsObj.gpioNum == 0xff) ) { // || (parmsObj.hasUpDown == false)
                mcpObj.ffdc.ffdcConfigWarningEntry("Option -i requires -g ");
                mcpObj.ffdc.ffdcDebugEntry("Spin so any Monitors can execute");
                mcpObj.ffdc.ffdcErrorExit("invalid parms supplied", 550);
            } else {
                mcpObj.addListener(parmsObj.offOn,  parmsObj.gpioNum);
                while (true) {
                    try {
                        Thread.sleep(2000, 0);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }


        mcpObj.ffdc.ffdcDebugEntry("program ending normal");
        // TODO
        // mcpObj.ffdc.displayFfdcEntries();
        // mcpObj.ffdc.displayFfdcConfigWarningEntries();
        //
        ffdc.ffdcFlushShutdown(); // push all logs to the file

        // Shutdown Pi4J
        pi4j.shutdown();
    }






}
