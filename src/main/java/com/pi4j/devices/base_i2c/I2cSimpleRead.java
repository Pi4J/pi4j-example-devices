/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  I2cSimpleRead.java
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

package com.pi4j.devices.base_i2c;


import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.Pi4J;
import com.pi4j.io.exception.IOReadException;
import com.pi4j.util.Console;
import com.pi4j.context.*;

import java.io.IOException;

/**
 * I2cSimpleRead class.Capable of accessing an i2c device to read and display num_bytes of data in
 * a pretty print format
 */

public class I2cSimpleRead extends BasicI2cDevice {

    /**
     * I2cSimpleRead device CTOR
     * <p>
     * PreCond: I2cSimpleRead CTOR called with valid parameters
     * <ul>
     *     <li> Instantiated FFDC class
     *     <li> Number of existing/functional Pi i2c bus
     *     <li> Address of i2c device connected to the bus identified by bus_num
     *     <li> Instantiated Console class
     *     <li>Instantiated Context class
     *     <li> number of bytes to read and display
     * </ul>
     * <p>
     * PostCond:  Class Read method are now accessable
     */
    public I2cSimpleRead(Context pi4j, FfdcUtil ffdc, int busNum, int address, int numBytes, Console console) {
        super(pi4j, ffdc, busNum, address, console);
        this.numBytes = numBytes;

    }


    int numBytes = 0;

    /**
     * dumpRegs,dump/display num_bytes of an i2c device.
     * Each read access first sets the control registers to ensure what specific register is read
     * <p>
     * PreCond: I2cSimpleRead instance initialized.  See CTOR
     *
     * <p>
     * PostCond:  Register contents displayed
     *
     * @throws IOException, IOReadException
     */
    public void dumpRegs() throws IOException, IOReadException { // Here we will create I/O interfaces for a (GPIO) digital output
        // and input pin. Since no specific 'provider' is defined, Pi4J will
        // use the default `DigitalOutputProvider` for the current default platform.
        this.ffdc.ffdcMethodEntry(this.getMethodName());

        var details = "     0   1   2   3   4   5   6   7   8   9   a   b   c   d   e   f \n";
        details = details + String.format("%02x: ", 0);
        for (int i = 0; i < this.numBytes; i++) {
            byte data = this.readRegisterByte(i);
            details = details + String.format("%02x ", data) + " ";
            if ((i > 0) && ((i + 1) % 16) == 0) {
                details = details + "\n";
                details = details + String.format("%02x: ", i + 1);
            }
        }

        console.println(details);
        this.ffdc.ffdcMethodExit(this.getMethodName());
    }

    /**
     * Description of parms passed to class main
     *
     * <p>
     * PostCond: Details printed on console
     */
    protected void usage() {
        System.out.println(
                "options   -h 'help', -b bus, -a address," + "-n  number of bytes  -f ffdc_lvl -s sysCfg  " +
                        "1 DEBUG < 2 INFO < 3 WARN < 4 ERROR < 5 FATAL < 6 OFF   ");
    }

    /**
     * I2cSimpleRead, main entry point
     * <p>
     * See method usage() for details
     *
     * <p>
     * PostCond:  Register contents displayed
     */
    public static void main(String[] args) throws Exception {
        var console = new Console();

        // Print program title/header
        console.title("<-- The Pi4J V2 Project Extension -->", "Minimal Example I2c ");

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

        int ffdcControlLevel = 0;
        int busNum = 0x1;
        int address = 0x29;
        int numBytes = 0x10;

        boolean setFfdcLvl = false;
        boolean showCfg = false;
        boolean showUsage = false;

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
                busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-n")) { // bus
                String a = args[i + 1];
                numBytes = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
                // display_main.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-s")) {
                showCfg = true;
            } else if (o.contentEquals("-x")) {
                // dummy entry. The bash function expects program name and  total ??parms. If you pass
                // fewer you need to add -x entries to maintain six parms
            } else {
                showUsage = true;
            }
        }


        FfdcUtil ffdc = new FfdcUtil(console, pi4j, ffdcControlLevel, I2cSimpleRead.class);
        var simpClass = new I2cSimpleRead(pi4j, ffdc, busNum, address, numBytes, console);

        if (showUsage) {
            simpClass.usage();
            System.exit(0);
        }

        // ------------------------------------------------------------
        // Output Pi4J Context information
        // ------------------------------------------------------------
        if (showCfg) {
            ffdc.printLoadedPlatforms();
            ffdc.printDefaultPlatform();
            ffdc.printProviders();
            ffdc.printRegistry();
        }


        if (setFfdcLvl) {
            ffdc.setLevel(ffdcControlLevel);
        }

        simpClass.dumpRegs();


        // Shutdown Pi4J
        pi4j.shutdown();

    }
}
