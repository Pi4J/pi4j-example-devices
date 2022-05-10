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
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class SN74HC595App {

    public static void main(String[] args) {
        // TODO Auto-generated method stub


        int OEPinNum = 0xff;
        int STCPPinNum = 0xff;
        int SHCPPinNum = 0xff;
        int MRPinNum = 0xff;
        int DSPinNum = 0xff;
        DigitalOutput oeGpio = null;
        DigitalOutput stcpGpio = null;
        DigitalOutput shcpGpio = null;
        DigitalOutput mrGpio = null;
        DigitalOutput dsGpio = null;
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
        console.print("startup  SN74HC595App ");
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
                console.println("OE Pin  " + OEPinNum);
                i++;
            } else if (o.contentEquals("-ds")) {
                String a = args[i + 1];
                DSPinNum = Integer.parseInt(a);
                console.println("DS Pin  " + DSPinNum);
                i++;
            } else if (o.contentEquals("-st")) {
                String a = args[i + 1];
                STCPPinNum = Integer.parseInt(a);
                console.println("STCP Pin  " + STCPPinNum);
                i++;
            } else if (o.contentEquals("-sh")) {
                String a = args[i + 1];
                SHCPPinNum = Integer.parseInt(a);
                console.println("SHCP Pin  " + SHCPPinNum);
                i++;
            } else if (o.contentEquals("-mr")) {
                String a = args[i + 1];
                MRPinNum = Integer.parseInt(a);
                console.println("MR Pin  " + MRPinNum);
                i++;
            } else if (o.contentEquals("-rd")) {
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

        var outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
                .id("OE_pin")
                .name("Enable")
                .address(OEPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        try {
            oeGpio = pi4j.create(outputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut OE failed");
            System.exit(201);
        }
        var outputConfig2 = DigitalOutput.newConfigBuilder(pi4j)
                .id("STCP_pin")
                .name("STCP")
                .address(STCPPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            stcpGpio = pi4j.create(outputConfig2);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut STCP failed");
            System.exit(201);
        }
        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
                .id("SHCP_pin")
                .name("SHCP")
                .address(SHCPPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        try {
            shcpGpio = pi4j.create(outputConfig3);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut SHCP failed");
            System.exit(201);
        }
        var outputConfig4 = DigitalOutput.newConfigBuilder(pi4j)
                .id("MR_pin")
                .name("MR")
                .address(MRPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            mrGpio = pi4j.create(outputConfig4);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut MR failed");
            System.exit(201);
        }
        var outputConfig5 = DigitalOutput.newConfigBuilder(pi4j)
                .id("DS_pin")
                .name("DS")
                .address(DSPinNum)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        try {
            dsGpio = pi4j.create(outputConfig5);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut MR failed");
            System.exit(201);
        }

        var snChip = new SN74HC595(pi4j, console, dsGpio, oeGpio, stcpGpio, shcpGpio, mrGpio, registerData, traceLevel);
        snChip.updateSN74();
    }

}
