/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23xxxAppProcessParms.java
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

import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.util.Console;
import com.pi4j.context.Context;
import java.util.HashMap;

/**
 * Mcp23xxxAppProcessParms
 * <p>
 *     Common code the process the many arguments passed into the
 *     MCP23008 and MCP23017 application
 * </p>
 */
public class Mcp23xxxAppProcessParms {
    private final Console console;

    /**
     *  Class CTOR
     * @param console Console
     */
    public Mcp23xxxAppProcessParms(Console console){
        super();
        this.console = console;

    }


    /**
     *   processMain
     * @param pi4j  Context
     * @param args  Arguments passed in by application caller.
     * @param bank_capable  Whether this chip CAN be banked. MCP23008 can't, the
     *                      MCP23017 can. This determines the chip register mapping
     * @param dioPinData    Chip Pin data
     * @param generalConsole Console
     * @return  Mcp23xxx Parms instance whose state contains the details pass
     * in by application caller.
     */
    public static Mcp23xxxParms processMain(Context pi4j,String[] args,boolean bank_capable, HashMap<Integer, GpioPinCfgData> dioPinData , Console generalConsole) {
        var console = generalConsole;
        Mcp23xxxParms parmsObj = new Mcp23xxxParms(console);
        parmsObj.pi4j = pi4j;
        parmsObj.ffdcControlLevel = 6;
        parmsObj.bank_capable = bank_capable;
        parmsObj.up_down = "on"; //  presently handlers canot be removed, so not an option
        console.print("entered processMain   \n");
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
            } /*
             * else if (o.contentEquals("-b")) { String a = args[i + 1];
             * parmsObj.bus_num = Integer.parseInt(a.substring(2), 16);
             * haveBus = true; i++; }
             */else if (o.contentEquals("-i")) {
                parmsObj.monitor_intrp = true;
                parmsObj.off_on = args[i + 1];
                i++;
            }  else if (o.contentEquals("-g")) {
                String a = args[i + 1];
                parmsObj.gpio_num = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-x")) {
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
            } /*
             * Note, it si assumed the calling program is using the appConfig
             *     package for resolving the chip bus and address, so these two
             *     options are not accepted.
             * else if (o.contentEquals("-a")) { String a = args[i + 1];
             * i++; parmsObj.address = Integer.parseInt(a.substring(2), 16);
             * haveAddress = true; // display_main.address =
             * Integer.parseInt(a, 16); }
             */else if (o.contentEquals("-z")) {
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
            } else if (o.contentEquals("-c")) { // primarydevice name
                String a = args[i + 1]; // chip name
                i++;
                parmsObj.priChipName = a;
                console.print("   -c : " + parmsObj.priChipName);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-p")) { // primary pin
                String a = args[i + 1]; // chip name/
                i++;
                parmsObj.pinName = a;
                console.print("-p : " + parmsObj.pinName);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-q")) { // mainChip
                parmsObj.mainChip = args[i + 1];

                // >>>tcaObj.bus_num = Integer.parseInt(a.substring(2), 16);
                i++;
            } else {
                console.print("Invalid parm : " + o  + "  ");
                parmsObj.usage();
                System.exit(2);
            }
            if (badParm) {
                parmsObj.usage();
                console.print(badParmDetail);
                System.exit(701);
            }
        }
    return(parmsObj);
    }

}
