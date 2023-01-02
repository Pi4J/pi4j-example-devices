/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  SSD1306_Example.java
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

package com.pi4j.devices.ssd1306;

import com.pi4j.Pi4J;
import com.pi4j.exception.LifecycleException;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;
import java.util.Arrays;
import com.pi4j.devices.ssd1306.SSD1306_Defines;

public class SSD1306_Example {

    /**
     * Sample application using SSD1306 sensor chip.
     *
     * @param args an array of {@link java.lang.String} objects.
     *             Parms are not required. if 'any' parameter value is supplied,
     *             the example uses the create pattern for device instantiation,
     *             otherwise provider setup is used
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {


        int busNum = SSD1306_Defines.SSD1306_I2C_ADDRESS;
        int address = SSD1306_Defines.SSD1306_I2C_BUS;

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

        var pi4j = Pi4J.newContextBuilder().add(
                LinuxFsI2CProvider.newInstance()).build();


        // Context pi4j =  Pi4J.newAutoContext();

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
        console.print("startup  SSD1306 I2c Example ");
        console.print("==============================================================");


        String helpString = " parms: -b hex value bus    -a hex value address  -t trace \n " +
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


        var ssdDev = new SSD1306(pi4j, console, busNum, address, traceLevel);
        console.println("  Dev I2C detail    " + ssdDev.i2cDetail());
        console.println("  Setup ----------------------------------------------------------");


        console.println("  I2C detail : " + ssdDev.i2cDetail());

        console.println(" Enable Display " );



  /*      // display off
        ssdDev.sendCmd((byte) (0xAE));
         // 1 pixel on
        ssdDev.sendCmd((byte) (0xA6));
        ssdDev.sendCmdData((byte) 0x20, (byte) 0x00);
        ssdDev.sendCmd((byte) 0x40);
        ssdDev.sendCmd((byte) 0xA0);
        ssdDev.sendCmd((byte) 0xC0);
        ssdDev.sendCmdData((byte) 0xA8, (byte) 0x3F);
        ssdDev.sendCmdData((byte) 0xD3, (byte) 0x00);
        ssdDev.sendCmdData((byte) 0xD5, (byte) 0x80);
        ssdDev.sendCmdData((byte) 0xD9, (byte) 0x22);
        ssdDev.sendCmdData((byte) 0xDA, (byte) 0x12);
        ssdDev.sendCmdData((byte) 0xDB, (byte) 0x20);
        ssdDev.sendCmdData((byte) 0x8D, (byte) 0x14);
*/




       // Display RAM page buffer
       // 128 columns (each bit in each byte is a row pixel)
        byte[] page_buffer = new byte[128];

        byte value = (byte) 0xff;
         // Fix the page buffer with 1s (all pixels on)
        Arrays.fill(page_buffer,0,page_buffer.length-1, value);

        //Set start line [$40]
        ssdDev.sendCmd((byte) (0x40));
        // Set the column bounds to the full width of the display
         // This also set the current column to 0
        ssdDev.setColumnAddress((byte) 0, (byte) 127);
        // Set the page bounds to all pages
        ssdDev.setPageAddress((byte) 0, (byte) 7);

        // Send the buffer to the display
        ssdDev.sendBuffer(page_buffer, page_buffer.length);

        // Shutdown Pi4J
        pi4j.shutdown();
    }

}
