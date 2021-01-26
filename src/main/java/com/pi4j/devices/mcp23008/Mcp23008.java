/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23008.java
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

package com.pi4j.devices.mcp23008;

import com.pi4j.context.Context;
import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.mcp23xxxApplication.Mcp23008PinMonitor;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxAppProcessParms;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.devices.mcp23xxxCommon.Mcp23xxxUtil;
import com.pi4j.devices.mcp23xxxCommon.McpBase;

import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.Pi4J;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import java.util.HashMap;
import com.pi4j.context.Context;
import com.pi4j.devices.base_i2c.BasicI2cDevice;
import com.pi4j.devices.mcp23xxxCommon.McpConfigData;
import com.pi4j.exception.LifecycleException;
import com.pi4j.io.exception.IOException;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Mcp23008   subclass of McpBase, used with chip MCP23008
 */

public class Mcp23008 extends McpBase {

    /**
     * CTOR
      * @param pi4j        Context
     * @param parms         Contains all parms supplied by the program called
     * @param ffdc          logging
     * @param dioPinData    Pi Gpio config devices
     * @param console       Console
     */
    public Mcp23008(Context pi4j,Mcp23xxxParms parms, FfdcUtil ffdc,  HashMap<Integer, GpioPinCfgData> dioPinData, Console console) {
        super(parms,false, dioPinData, pi4j, ffdc,  console);
    }


    /**
     *
     * @return    Array of register addresses, offset defined by McpConfigData
     * <p>
     *     Overridden by each subclass to return the proper register offsets
     * </p>
     */
    public byte[] getAddrMapFirst8() {
        this.ffdc.ffdcMethodEntry("23008 getAddrMapFirst8");
        byte regAddr[] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A };
        this.ffdc.ffdcMethodExit("23008 getAddrMapFirst8");
        return (regAddr);
    }

    /**
     * <p>
     *     Invoke various methods on MCP23008 instance
     * </p>
     * @param args       user params
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        var console = new Console();
        Context pi4j =  Pi4J.newAutoContext();

        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23008");

        HashMap<Integer, GpioPinCfgData> dioPinData = new HashMap<Integer, GpioPinCfgData>();

        Mcp23xxxParms parmsObj = Mcp23xxxUtil.processMain(pi4j,args,false, dioPinData, console);

        FfdcUtil ffdc = new FfdcUtil(console, pi4j, parmsObj.ffdcControlLevel , Mcp23008.class);

        ffdc.ffdcDebugEntry("mcp23008 : Arg processing completed...\n");


        Mcp23008 mcpObj = new Mcp23008(parmsObj.pi4j, parmsObj, ffdc,  dioPinData, console);

        BaseGpioInOut gpio = new BaseGpioInOut(parmsObj.pi4j, mcpObj.ffdc, mcpObj.dioPinData);
        mcpObj.gpio = gpio;

        AppConfigUtilities cfgU = null; // The config utils will not be used in this example
        mcpObj.cfgU = cfgU;

        mcpObj.mapUtils = new MapUtil(mcpObj.ffdc, mcpObj.gpio);



        mcpObj.cfgData = new McpConfigData(ffdc);

       Mcp23xxxUtil mcpUtil = new Mcp23xxxUtil(parmsObj.pi4j, ffdc,  parmsObj.bus_num, parmsObj.address, mcpObj.cfgData, mcpObj, console);

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

        if (parmsObj.has_full_keyed_data) { // -g
            HashMap<String, HashMap<String, String>> outerMap = mcpObj.mapUtils.createFullMap(parmsObj.full_keyed_data);
            mcpObj.cfgData.replaceMap(outerMap);
            gpio.createGpioInstance(mcpObj.cfgData.getFullMap());
        }

        if (parmsObj.do_reset) {
            mcpObj.reset_chip();
        }

        // do this before pin data as this will set 'banked', needed for correct
        // addressing
        if (parmsObj.has_IOCON_keyed_data) { // -k
            HashMap<String, HashMap<String, String>> mMap;
            mMap = mcpObj.mapUtils.createFullMap(parmsObj.IOCON_keyed_data);
            mcpObj.cfgData.replaceMap(mMap);
            mcpUtil.process_keyed_data();
        }

        if (parmsObj.has_full_pin_keyed_data) { // -m
            HashMap<String, HashMap<String, String>> mMap;
            mMap = mcpObj.mapUtils.createFullMap(parmsObj.full_pin_keyed_data);
            mcpObj.cfgData.replaceMap(mMap);
            mcpUtil.process_keyed_data();
        }

        System.out.println("Chip register configurations completed");

        mcpObj.reinit("Mcp23008", "Mcp23008",parmsObj.bus_num, parmsObj.address);

        if (parmsObj.dumpRegs) {
            mcpObj.dump_regs();
            System.exit(0);
        }


        if (parmsObj.set_pin) {
            try {
                mcpObj.drive_pin(parmsObj.pin, parmsObj.pin_on);
            } catch (InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }



        if (parmsObj.read_pin) {
            try {
                mcpObj.read_input(parmsObj.pin);
            } catch (InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }



        mcpObj.ffdc.ffdcDebugEntry("program ending normal");
         //
        ffdc.ffdcFlushShutdown(); // push all logs to the file

        // Shutdown Pi4J
        pi4j.shutdown();
    }


}
