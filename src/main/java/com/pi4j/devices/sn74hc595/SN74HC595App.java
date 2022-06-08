/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  SN74HC595App.java
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

package com.pi4j.devices.sn74hc595;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.LifecycleException;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;


/**
 * Simple application to accumulate the prams to create a SN74HC595.  Primarily
 * this information is which Pi GPIOs will be used to control the SN74HC595.
 * <p>
 * This app creates the Pi DigitalOutput objects and they are passed to the SN74HC595
 */

public class SN74HC595App {

    public static void main(String[] args) {
        // TODO Auto-generated method stub


        int OEPinNum = 0xff;
        int STCPPinNum = 0xff;
        int SHCPPinNum = 0xff;
        int MRPinNum = 0xff;
        int DSPinNum = 0xff;

        byte registerData = 0;


        Context pi4j = Pi4J.newAutoContext();


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
        console.print("startup  SN74HC595App  ");
        console.print("==============================================================");


        String helpString = " parms:   -t trace   -ds HEX DS gpio\n" +
                " -oe OE gpio,  -st STCP gpio,  -sh SHCP gpio, -mr MR gpio -rd HEX  registerData  \n" +
                "    trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
                " or \"off\"  Default \"info\"";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-oe")) {
                String a = args[i + 1];
                OEPinNum = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-ds")) {
                String a = args[i + 1];
                DSPinNum = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-st")) {
                String a = args[i + 1];
                STCPPinNum = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-sh")) {
                String a = args[i + 1];
                SHCPPinNum = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-mr")) {
                String a = args[i + 1];
                MRPinNum = Integer.parseInt(a);
                i++;
            }  else if (o.contentEquals("-rd")) {
                String a = args[i + 1];
                i++;
                registerData = (byte) (Integer.parseInt(a.substring(2), 16) & 0xff);
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


        var snChip = new SN74HC595(pi4j, console, DSPinNum, OEPinNum, STCPPinNum, SHCPPinNum, MRPinNum, registerData, traceLevel);

        snChip.updateSN74();
        console.println("Wait 5 seconds, then turn on all outputs.");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        snChip.sendCommand(0xff);
    }

}
