/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  CP2102N_App.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2022 Pi4J
 *      * %%
 *     *
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
 *     *
 *
 *
 *
 */

package com.pi4j.devices.cp2102n;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.devices.vl53L0X.VL53L0X_Device;
import com.pi4j.devices.vl53L0X.Vl53L0X_Declares;
import com.pi4j.exception.LifecycleException;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class CP2102N_App {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Context pi4j = Pi4J.newAutoContext();

/*        var pi4j = Pi4J.newContextBuilder().add(
                LinuxFsI2CProvider.newInstance()).build();
*/
        // Prior to running methods, set up control-c handler
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Performing ctl-C shutdown");
                try {
                    pi4j.shutdown();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
                Thread.dumpStack();
                System.exit(2);
            }
        });


        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  VL53L0X_App ");
        console.print("==============================================================");


        // if user reset the Pi MUX to enable more I2C bus', they can pass that bus number as parm -b
        boolean doReset = false;
        int gpioReset = 0;

        String helpString = " parms:  -t trace   \n " +
                "  -r  reset integer value GPIO \n " +
                "    trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
                " or \"off\"  Default \"info\"";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if  (o.contentEquals("-t")) { // device address
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(40);
                }
            }  else if (o.contentEquals("-r")) {
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
        if (doReset ) {
            // TODO
            //use gpioReset
            System.exit(43);

        }
        var serialLine = new CP2102N(pi4j, traceLevel);
        serialLine.sendData("at\r");


    }

}
