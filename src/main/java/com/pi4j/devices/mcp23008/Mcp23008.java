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
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.devices.mcp23xxxCommon.Mcp23xxxUtil;
import com.pi4j.devices.mcp23xxxCommon.McpBase;

import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.Pi4J;

import java.util.HashMap;

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



}
