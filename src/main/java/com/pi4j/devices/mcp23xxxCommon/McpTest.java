/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  McpTest.java
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

import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.util.Console;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;

import java.util.HashMap;
import com.pi4j.context.Context;

/**
 * McpTest
 * <p>
 *     Unit testing
 * </p>
 */
public class McpTest {

    public boolean testConfigAndMux(Context pi4j, FfdcUtil ffdc, McpBase mcpObj,McpConfigData mcpCfgD, AppConfigUtilities cfgU, Mcp23xxxParms parmsObj, BaseGpioInOut gpio, Console console ){
        cfgU.disableBus(0, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(1, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(2, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(3, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(4, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(5, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(6, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(7, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);
        // see if target chip behind a mux. If so set up mux

        boolean returned = false;
        returned = cfgU.enableGpioPath(parmsObj.pinName, parmsObj.priChipName); // path
        // to
        // mcp23xx

        //mUtil.ffdc.ffdcDebugEntry("bus  " + parmsObj.bus_num + "    distant device " + parmsObj.chipAddr + " should be visible");
        cfgU.runCli();
        cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);

        HashMap<String, String> initialChipD = cfgU.getChipMapRec(parmsObj.mainChip);
        String initialChipBus = initialChipD.get("busNum");
        String initialChipAddr = initialChipD.get("address");

        HashMap<String, String> chipD = cfgU.getChipMapRec(parmsObj.priChipName);
        String banked = chipD.get("banked");
        String chipBus = chipD.get("busNum");
        String chipAddr = chipD.get("address");
       int chipAddrInt = Integer.parseInt(chipAddr.substring(2), 16);
        Mcp23xxxUtil mcpUtil = new Mcp23xxxUtil(pi4j,ffdc, Integer.parseInt(initialChipBus.substring(2), 16),
                chipAddrInt, mcpCfgD, mcpObj, console);

            // test the configured pin
            gpio.drivePinHigh(Integer.parseInt(parmsObj.pinName.substring(3)));
            gpio.sleepMS(2000, ffdc);
            gpio.drivePinLow(Integer.parseInt(parmsObj.pinName.substring(3)));
        gpio.sleepMS(2000, ffdc);
            gpio.drivePinHigh(Integer.parseInt(parmsObj.pinName.substring(3)));
        gpio.sleepMS(2000, ffdc);
            gpio.drivePinLow(Integer.parseInt(parmsObj.pinName.substring(3)));
        gpio.sleepMS(2000, ffdc);
            gpio.drivePinHigh(Integer.parseInt(parmsObj.pinName.substring(3)));
        gpio.sleepMS(2000, ffdc);
            gpio.drivePinLow(Integer.parseInt(parmsObj.pinName.substring(3)));



        cfgU.disableBus(0, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(1, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(2, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(3, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(4, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(5, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(6, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(7, parmsObj.bus_num, parmsObj.mainChip);

        // expect fail
        returned = cfgU.enableGpioPath("pin42", "23008#1");

        // no mapping
        console.println("bus Nope, Pi pin");

        returned = cfgU.enableGpioPath("dio12", "23008#1");

        // Windows
        console.println("bus 1");
        returned = cfgU.enableGpioPath("pin14", "23008#1");

        cfgU.runCli();

        cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);

        cfgU.disableBus(1, parmsObj.bus_num, parmsObj.mainChip);

        // pin15
        if (parmsObj.bank_capable) {
            returned = cfgU.enableGpioPath("pin4", "23017#2");
            console.println("bus 3");
            cfgU.runCli();

            cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);

            cfgU.disableBus(3, parmsObj.bus_num, parmsObj.mainChip);
            //
            console.println("bus 4");
            returned = cfgU.enableGpioPath("pin15", "23017#2");
            cfgU.runCli();

            cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);

            cfgU.disableBus(4, parmsObj.bus_num, parmsObj.mainChip);
        }
        returned = cfgU.enableGpioPath(parmsObj.pinName, parmsObj.priChipName);
        console.println("Use in/out data pin : " + parmsObj.pinName + "   chip   :" + parmsObj.priChipName);
        console.println("bus dependent on input data");

        HashMap<String, String> chipD2 = cfgU.getChipMapRec(parmsObj.priChipName);
        String banked2 = chipD2.get("banked");
        String chipBus2 = chipD2.get("busNum");
        String chipAddr2 = chipD2.get("address");

        boolean isBanked2 = false;
        if (banked2.equalsIgnoreCase("y")) {
            isBanked2 = true;
        }
        try {
            if (isBanked2) {
                console.println("Drive pin15 high");
                gpio.drivePinHigh(15);
                Thread.sleep(2000);
                gpio.drivePinLow(15);
            } else {
                console.println("Drive pin2 high");
                gpio.drivePinHigh(2);
                Thread.sleep(2000);
                gpio.drivePinLow(2);
            }
        } catch (InterruptedException  e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ffdc.ffdcErrorExit("failed", 2003);
        } 

        console.println("Drive Gpio18 low");
        int p18 = 18;
        gpio.drivePinHigh(p18);
        cfgU.runCli();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ffdc.ffdcErrorExit("sleep failed",2005);
        }

        console.println("Drive Gpio18 low");
        gpio.drivePinLow(p18);

        cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);

        cfgU.disableBus(0, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(1, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(2, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(3, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(4, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(5, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(6, parmsObj.bus_num, parmsObj.mainChip);
        cfgU.disableBus(7, parmsObj.bus_num, parmsObj.mainChip);
        //

        if (parmsObj.bank_capable == false) {
            boolean eChip = cfgU.enableChipPath("matrix#1");
            console.println("matrix#1  bus 7");
            cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);
            cfgU.runCli();
            cfgU.disableBus(7, parmsObj.bus_num, parmsObj.mainChip);
            cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);
            eChip = cfgU.enableChipPath("BMP#1");
            console.println("BMP#1  bus 7");
            cfgU.displayEnableReg(parmsObj.bus_num, parmsObj.mainChip);
            cfgU.runCli();
            cfgU.disableBus(7, parmsObj.bus_num, parmsObj.mainChip);
        }
        // use input details

        cfgU.runCli();
        return(returned);
    }
}
