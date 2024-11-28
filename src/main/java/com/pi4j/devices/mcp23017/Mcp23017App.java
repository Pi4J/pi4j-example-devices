/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23017App.java
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

package com.pi4j.devices.mcp23017;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.devices.mcp23xxxCommon.Mcp23xxxUtil;
import com.pi4j.devices.mcp23xxxCommon.McpConfigData;
import com.pi4j.util.Console;

import java.util.HashMap;

public class Mcp23017App extends Mcp23017 {


    /**
     * CTOR
     *
     * @param pi4j       Context
     * @param parmsObj   Contains all parms supplied by the program called
     * @param ffdc       logging
     * @param dioPinData Pi Gpio config devices
     * @param console    Console
     */
    public Mcp23017App(Context pi4j, Mcp23xxxParms parmsObj, FfdcUtil ffdc, HashMap<Integer, GpioPinCfgData> dioPinData, Console console) {
        super(pi4j, parmsObj, ffdc, dioPinData, console);
    }


    /**
     * <p>
     * Invoke various methods on MCP23008 instance
     * </p>
     *
     * @param args user params
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();

        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23017App");

        HashMap<Integer, GpioPinCfgData> dioPinData = new HashMap<Integer, GpioPinCfgData>();

        Mcp23xxxParms parmsObj = Mcp23xxxUtil.processMain(pi4j, args, true, dioPinData, console);

        FfdcUtil ffdc = new FfdcUtil(console, pi4j, parmsObj.ffdcControlLevel, Mcp23017.class);

        ffdc.ffdcDebugEntry("mcp23017App : Arg processing completed...\n");


        Mcp23017App mcpObj = new Mcp23017App(parmsObj.pi4j, parmsObj, ffdc, dioPinData, console);

        BaseGpioInOut gpio = new BaseGpioInOut(parmsObj.pi4j, mcpObj.ffdc, mcpObj.dioPinData);
        mcpObj.gpio = gpio;

        AppConfigUtilities cfgU = null; // The config utils will not be used in this example

        mcpObj.cfgU = cfgU;

        mcpObj.mapUtils = new MapUtil(mcpObj.ffdc, mcpObj.gpio);

        mcpObj.cfgData = new McpConfigData(ffdc);

        Mcp23xxxUtil mcpUtil = new Mcp23xxxUtil(parmsObj.pi4j, ffdc, parmsObj.busNum, parmsObj.address, mcpObj.cfgData, mcpObj, console);


        if (parmsObj.hasFullKeyedData) { // -g
            HashMap<String, HashMap<String, String>> outerMap = mcpObj.mapUtils.createFullMap(parmsObj.fullKeyedData);
            mcpObj.cfgData.replaceMap(outerMap);
            gpio.createGpioInstance(mcpObj.cfgData.getFullMap());
        }

        if (parmsObj.doReset) {
            mcpObj.resetChip();
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

        console.print("Chip register configurations completed");
        mcpObj.reinit("Mcp23017", "Mcp23017", parmsObj.busNum, parmsObj.address);

        if (parmsObj.dumpRegs) {
            mcpObj.dumpRegs();
            System.exit(0);
        }


        if (parmsObj.setPin) {
            mcpObj.drivePin(parmsObj.pin, parmsObj.pinOn);

        }


        if (parmsObj.readPin) {
            mcpObj.readInput(parmsObj.pin);
        }


        mcpObj.ffdc.ffdcDebugEntry("program ending normal");
        //
        ffdc.ffdcFlushShutdown(); // push all logs to the file

        // Shutdown Pi4J
        pi4j.shutdown();
    }
}

