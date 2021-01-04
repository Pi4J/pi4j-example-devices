/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  SampleTca9548App.java
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

package com.pi4j.devices.tca9548;

import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.LifecycleException;
import com.pi4j.util.Console;

import com.pi4j.devices.tca9548.Tca9548;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * SampleTca9548App
 * <p>
 * Simple application example of the Tca9548 i2c switch.
 *
 * <p>
 * See code comments
 */


public class SampleTca9548App {

    /**
     * Description of parms passed to class main
     *
     * <p>
     * PostCond: Details printed on console
     */
    static private void usage() {
        System.out.println(
                "options   -h 'help', -b bus, -a address," + "-n number of bytes  -s sysCfg  " +
                        " -f ffdc_lvl :1 DEBUG < 2 INFO < 3 WARN < 4 ERROR < 5 FATAL < 6 OFF   " +
                        "   -e busToEnabled  -d busToDisabled -l displayEnableReg  -r resetChip GPIO# \n ");
    }

    /**
     * SampleTca9548App, main entry point
     * <p>
     * See method usage() for details
     * Example :sudo java -classpath ???   tca9548.SampleTca9548App -b 0x01 -a 0x70  -f 1   -l  -e 0x7 -r 0x06
     * Parms: -b  pi_i2c_bus_1  -x device_address  -e enable_bus_7  -r 0x06 (reset chip using GPIO_6 -l (display_register)
     *
     *
     * <p>
     * PostCond:  Requested actions completed
     */
    public static void main(String[] args) throws Exception {
        var console = new Console();

        // Print program title/header
        console.title("<-- The Pi4J V2 Project Extension  -->", "SampleTca9548App  TCA9548");

        // ************************************************************

        // ------------------------------------------------------------
        // Initialize the Pi4J Runtime Context
        // ------------------------------------------------------------
        // Before you can use Pi4J you must initialize a new runtime
        // context.
        //
        // The 'Pi4J' static class includes a few helper context
        // creators for the most common use cases.  The 'newAutoContext()'
        // method will automatically load all available Pi4J
        // extensions found in the application's classpath which
        // may include 'Platforms' and 'I/O Providers'
        Context pi4j = Pi4J.newAutoContext();

        // declare and initialize attributes (state) used within the app
        int ffdcControlLevel = 0;
        int bus_num = 0x1;
        int address = 0x29;
        boolean displayRegs = false;
        boolean enableBus = false;
        boolean disableBus = false;

        boolean showUsage = false;
        boolean setFfdcLvl = false;
        boolean showCfg = false;
        boolean resetChip = false;
        int resetGpio = 0xff;
        int muxBusNumber = 0;

        // Walk through all parms passed to the program.  |
        // The parms
        //   1) Set application state.
        //   2) Request help, display program parms and exit
        //   3) Invalid, forcing usage display and exit.
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-f")) {
                String a = args[i + 1];
                i++;
                setFfdcLvl = true;
                ffdcControlLevel = Integer.parseInt(a);
            } else if (o.contentEquals("-h")) {
                showUsage = true;
            } else if (o.contentEquals("-b")) { // bus
                String a = args[i + 1];
                bus_num = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-r")) { // gpio numberdevice address
                String a = args[i + 1];
                i++;
                resetGpio = Integer.parseInt(a.substring(2), 16);
                resetChip = true;
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-s")) {
                showCfg = true;
            } else if (o.contentEquals("-e")) { // enable bus
                String a = args[i + 1];
                i++;
                enableBus = true;
                muxBusNumber = Integer.parseInt(a.substring(2), 16);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-d")) { // device address
                String a = args[i + 1];
                i++;
                disableBus = true;
                muxBusNumber = Integer.parseInt(a.substring(2), 16);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-l")) { // device address
                // display_main.address = Integer.parseInt(a, 16);
                displayRegs = true;
            } else {
                showUsage = true;
            }
        }


        // Now use the class state to instantiate the FFDC utility class and the Tca9548 class
        FfdcUtil ffdc = new FfdcUtil(console, pi4j, ffdcControlLevel, Tca9548.class);
        var tcaMux = new Tca9548(pi4j, ffdc, bus_num, address, console);

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

        // Based upon parms the user supplied, call Tca9548 methods
        if (showUsage) {
            SampleTca9548App.usage();
            System.exit(0);
        }
        // Display pi4j_V2 configuration details
        if (showCfg) {
            ffdc.printLoadedPlatforms();
            ffdc.printDefaultPlatform();
            ffdc.printProviders();
            ffdc.printRegistry();
        }

        //  Set controls for the log4j logging
        if (setFfdcLvl) {
            if (ffdc.setLevel(ffdcControlLevel) == false) {
                console.println("FFDC level invalid :" + ffdcControlLevel);
                // recovery code needed
            }
        }
        // This will drive the GPIO connected to the Tca9548 reset pin  parm -r
        if (resetChip) {
            tcaMux.resetChip(resetGpio);
        }
        // enable a bus
        if (enableBus) {
            if (tcaMux.enableBus(muxBusNumber) == false) {
                console.println("enable bus failed. -e was :" + muxBusNumber);
                //  Recovery code to use correct bus number ? Maybe error exit
            }
        }
        // disable a bus
        if (disableBus) {
            if (tcaMux.disableBus(muxBusNumber) == false) {
                console.println("enable bus failed. -e was :" + muxBusNumber);
                //  Recovery code to use correct bus number ? Maybe error exit
            }
        }
        //Display (prettyPrint) the Tca9548 bus configuration register
        if (displayRegs) {
            tcaMux.displayBusEnable();
        }

        ffdc.ffdcFlushShutdown(); // push all logs to the file

        // Shutdown Pi4J
        pi4j.shutdown();

    }
}
