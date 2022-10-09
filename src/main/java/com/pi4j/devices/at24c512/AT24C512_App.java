/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  AT24C512_App.java
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

package com.pi4j.devices.at24c512;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.devices.bmp280.BMP280Declares;
import com.pi4j.devices.bmp280.BMP280Device;
import com.pi4j.exception.LifecycleException;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class AT24C512_App {


        /**
         * Sample application using AT24C512 SEEPROM.
         *
         * @param args an array of {@link java.lang.String} objects.
         *             Parms are not required. if 'any' parameter value is supplied,
         *             the example uses the create pattern for device instantiation,
         *             otherwise provider setup is used
         * @throws java.lang.Exception if any.
         */
        public static void main(String[] args) throws Exception {


        int busNum = 0x01;
        int address = 0x50;
        int numBytes = 1;
        long readReg = 0;
        boolean doRead = false;
        long writeReg = 0;
        long writeData;
        boolean doWrite = false;

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

    /*    var pi4j = Pi4J.newContextBuilder().add(
                LinuxFsI2CProvider.newInstance()).build();
*/
        Context pi4j = Pi4J.newAutoContext();

        // print installed providers
        System.out.println("----------------------------------------------------------");
        System.out.println("PI4J PROVIDERS");
        System.out.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");
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
        console.print("startup  AT24C512_App ");
        console.print("==============================================================");


        String helpString = " parms: -b hex value bus    -a hex value address  \n" +
                "-n numBytes -r readReg   -w writeReg -d  data -t trace\n" +
                "-r not permitted with args -d -w, either read data or write data \n " +
                " \n trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\"  Default \"info\"";
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
            }else if (o.contentEquals("-n")) { // device address
                String a = args[i + 1];
                i++;
                numBytes = Integer.parseInt(a.substring(2), 16);
            }else if (o.contentEquals("-r")) { // read reg
                String a = args[i + 1];
                i++;
                doRead = true;
                readReg = Long.parseLong(a.substring(2), 16);
            }else if (o.contentEquals("-w")) { // write reg
                String a = args[i + 1];
                i++;
                doWrite = true;
                writeReg = Long.parseLong(a.substring(2), 16);
            }else if (o.contentEquals("-d")) { // write reg
                String a = args[i + 1];
                i++;
                writeData = Long.parseLong(a.substring(2), 16);
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
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }

        if(doRead & doWrite){
            console.println("  !!! Invalid Parms, -r -w mutually exclusive");
            console.println(helpString);
            System.exit(43);

        }

        var seeDev = new AT24C512(pi4j, console, busNum, address, traceLevel);
        console.println("  Dev I2C detail    " +  seeDev.i2cDetail());
        console.println("  Setup ----------------------------------------------------------");


        console.println("  I2C detail : " + seeDev.i2cDetail());


        byte toWrite[] = {(byte) 0xDE, (byte) 0xAD, (byte) 0xBE, (byte) 0xEF};
        // TODO get users data to send to SEEPROM
            // TODO use -w
        if(doWrite) {
            int bWritten = seeDev.writeEEPROM(writeReg, toWrite.length, toWrite);
            console.println("Wrote " + bWritten + " bytes, expected : " + numBytes);
        }

        byte[] toRead;
        // TODO use -r
        if(doRead) {
                toRead = seeDev.readEEPROM(readReg, numBytes);
            }
        // Shutdown Pi4J
        pi4j.shutdown();
    }


}
