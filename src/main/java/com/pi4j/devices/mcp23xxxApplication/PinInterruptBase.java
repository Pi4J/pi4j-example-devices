/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  PinInterruptBase.java
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

import com.pi4j.context.Context;
import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;

import java.util.HashMap;

/**
 * PinInterruptBase
 * <p>
 * Instances of this class are associated with an PinInterruptActionIntf as
 * the instance to call for effecting some action.
 * <p>
 * Subclasses can take unique and specific steps when the interrupt occurs.
 */
public class PinInterruptBase {

    public PinInterruptBase(Context pi4j, int pin, FfdcUtil ffdc, Mcp23xxxPinMonitorIntf mcpObj,
                            HashMap<Integer, GpioPinCfgData> pinDict, AppConfigUtilities cfgU, String priChipName) {


        this.pi4j = pi4j;
        this.ffdc = ffdc;
        this.mcpObj = mcpObj;
        this.pin = pin;
        this.pinDict = pinDict;
        this.cfgU = cfgU;
        this.priChipName = priChipName;
    }

    FfdcUtil ffdc;
    Mcp23xxxPinMonitorIntf mcpObj;
    int pin;
    AppConfigUtilities cfgU;
    String priChipName;
    public HashMap<Integer, GpioPinCfgData> pinDict;
    Context pi4j;


}
