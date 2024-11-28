package com.pi4j.devices.vl53L0X;


/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  VL53L0X_App.java
 *      *
 *      * This file is part of the Pi4J project. More information about
 *      * this project can be found here:  https://pi4j.com/
 *      * **********************************************************************
 *     * %%
 *     * Copyright (C) 2012 - 2022 Pi4J
 *     * %%
 *     * Licensed under the Apache License, Version 2.0 (the "License");
 *     * you may not use this file except in compliance with the License.
 *     * You may obtain a copy of the License at
 *     *
 *     *      http://www.apache.org/licenses/LICENSE-2.0
 *     *
 *     * Unless required by applicable law or agreed to in writing, software
 *     * distributed under the License is distributed on an "AS IS" BASIS,
 *     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     * See the License for the specific language governing permissions and
 *     * limitations under the License.
 *     * #L%
 *   *
 *
 *
 */

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.util.Console;


/**
 * Configure and repeatedly access the TOF chip, displaying the measured distance.
 */
public class VL53L0X_App {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Context pi4j = Pi4J.newAutoContext();

/*        var pi4j = Pi4J.newContextBuilder().add(
                linuxfs-i2cProvider.newInstance()).build();
*/


        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  VL53L0X_App ");
        console.print("==============================================================");


        // if user reset the Pi MUX to enable more I2C bus', they can pass that bus number as parm -b
        int busNum = 1;
        int address = Vl53L0X_Declares._VL53L0X_DEFAULT_ADDRESS;
        int existingAddress = Vl53L0X_Declares._VL53L0X_DEFAULT_ADDRESS;
        boolean existingSet = false;
        boolean doReset = false;
        int gpioReset = 0;

        String helpString = " parms: -b hex value bus    -a hex value address  -t trace   \n " +
            "  -r  reset integer value GPIO  -x hex value address prior to reset  \n " +
            "    trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
            " or \"off\"  Default \"info\"";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-b")) { // bus
                String a = args[i + 1];
                busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-t")) { // device address
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(40);
                }
            } else if (o.contentEquals("-x")) { // device address
                String a = args[i + 1];
                i++;
                existingSet = true;
                existingAddress = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-r")) {
                String a = args[i + 1];
                doReset = true;
                gpioReset = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }
        if ((doReset && (!existingSet)) || ((!doReset) && (existingSet))) {
            console.println("  !!! Invalid Parm combination, if either  -r or -x  is used, requires both");
            console.println(" parms: -b hex value bus    -a hex value address  -r  reset integer value GPIO  -x hex value existing address prior to reset ");
            System.exit(43);

        }

        VL53L0X_Device vl53Existing = null;
        // doRest assume we are changing the device address. No matter, the reset will set the chip to
        // default address 0x29.
        if (doReset) {
            vl53Existing = new VL53L0X_Device(pi4j, busNum, existingAddress, traceLevel);
            vl53Existing.setNewAddress(gpioReset, address, console, existingAddress);
        } else {
            vl53Existing = new VL53L0X_Device(pi4j, busNum, address, traceLevel);

        }
        int x = 0;
        while (x == 0) {
            int tof = vl53Existing.range();
            console.print("TOF resulted in measurement :  " + tof + " mm  " + tof / 25.4 + " inches  \n");
            vl53Existing.sleepMS(1000, console);
        }

    }

}

