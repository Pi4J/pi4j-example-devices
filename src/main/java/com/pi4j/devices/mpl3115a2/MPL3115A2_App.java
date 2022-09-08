/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  MPL3115A2_App.java
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

package com.pi4j.devices.mpl3115a2;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.LifecycleException;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class MPL3115A2_App {


    private static final int MPL_I2C_ADDRESS = MPL3115A2_Declares.DEFAULT_ADDRESS;
    private static final int MPL_I2C_BUS = MPL3115A2_Declares.DEFAULT_BUS;


    /**
     * See -h help text for usage
     *
     * Also, the -x reset chip.   Initial chip access may fail to retrieve data
     * and log an error message.  When the chip has completed reset normal data
     * retrieval will resume.
     *
     *
     *     At present supports retrieval Temperature Pressure and Altitude.
     *  A future commit will incorporate 'target' interrupt support.
     */
    public static void main(String[] args) throws Exception {


        int busNum = MPL3115A2_Declares.DEFAULT_BUS;
        int address = MPL3115A2_Declares.DEFAULT_ADDRESS;

        boolean doReset = false;
        int gpio_int_1_num = 0x42 ;
        int gpio_int_2_num = 0x42 ;
        float pw = 0;
        float tw = 0;
        float tg = 0;
        float pg = 0;

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

        /*var pi4j = Pi4J.newContextBuilder().add(
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
        console.print("startup  MPL3115A2_App ");
        console.print("==============================================================");


        String helpString = " parms: -b hex value bus    -a hex value address -int1 interrupt1 gpio," +
                " -int2 interrrupt2 gpio, -x do reset,  -t trace \n" +
                "  -PW pressure/alt window -PG pressure/alt limit   -TW temp window -TG temp limit  \n" +
                " \n trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\"  Default \"info\"";
        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-b")) { // bus
                String a = args[i + 1];
                busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            }else if (o.contentEquals("-TG")) { // temp limit
                String a = args[i + 1];
                i++;
                tg  = Float.parseFloat(a);
            }else if (o.contentEquals("-TW")) { // temp window
                String a = args[i + 1];
                i++;
                tw  = Float.parseFloat(a);
            }else if (o.contentEquals("-PG")) { // pressure limit
                String a = args[i + 1];
                i++;
                pg  = Float.parseFloat(a);
            }else if (o.contentEquals("-PW")) { // pressure window
                String a = args[i + 1];
                i++;
                pw  = Float.parseFloat(a);
            } else if (o.contentEquals("-int1")) {
                String a = args[i + 1];
                i++;
                gpio_int_1_num = Integer.parseInt(a);
            }else if (o.contentEquals("-int2")) {
                String a = args[i + 1];
                i++;
                gpio_int_2_num = Integer.parseInt(a);
            }else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
            }else if (o.contentEquals("-x")) {
                doReset = true;
            } else if (o.contentEquals("-t")) {
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


        var mplDev = new MPL3115A2(pi4j, console, busNum, address,gpio_int_1_num,gpio_int_2_num, traceLevel);
        console.println("  Dev I2C detail    " + mplDev.i2cDetail());
        console.println("  Setup ----------------------------------------------------------");


        if(doReset){
            mplDev.reset();
        }

        double pressure = mplDev.readPresurePa();
        console.println(" Pressure Pa = " + pressure);

        double pressureMb = mplDev.readPresureMb();
        console.println(" Pressure Mb = " + pressureMb);

        double temperature = mplDev.readTemperatureC();
        console.println(" Temperatue C = " + temperature);

        double temperatureF = mplDev.readTemperatureF();
        console.println(" Temperatue F = " + temperatureF);

        double altitude = mplDev.readAltimeterM();
        console.println(" Altitude Meters = " + altitude);

        double altitudeF = mplDev.readAltimeterF();
        console.println(" Altitude Feet = " + altitudeF);


        if(pg > 0){
            mplDev.set_P_PGT((long) pg);
        }
        if(pw > 0){
            mplDev.set_P_WND((long) pw);
        }
        if(tg > 0){
            mplDev.set_T_TGT((long) tg);
        }
        if(tw > 0){
            mplDev.set_T_WND((long) tw);
        }

        console.promptForExit();

        while (console.isRunning()) {
            // spin and allow the interrupt handlers to fire
            Thread.sleep(1000);
        }
        console.emptyLine();


        // Shutdown Pi4J
        pi4j.shutdown();
    }

}
