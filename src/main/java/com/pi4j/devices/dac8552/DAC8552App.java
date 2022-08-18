/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  DAC8552App.java
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

package com.pi4j.devices.dac8552;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.util.Console;

public class DAC8552App {

    public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
       int csPin = 0;
        double vref = 0.0;
        double voutput = 0.0;
        boolean hasVref = false;
        SpiBus spiBus = SpiBus.BUS_0;
        String channel = "";
        console.title("<-- The Pi4J V2 Project Extension  -->", "DAC8552App");
        String helpString = " parms:   -cs chip select GPIO -s HEX value SPI #  -t  trace  \n" +
                "-chn  channel, -vout voltage out  -vref reference voltage\n" +
                " Tracevalues : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
                " or \"off\"  Default \"info\"";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                hasVref = true;
                vref = Float.parseFloat(a);
            } else if (o.contentEquals("-vout")) { // reference voltage
                String a = args[i + 1];
                i++;
                voutput = Float.parseFloat(a);
            } else if (o.contentEquals("-cs")) { // device address
                String a = args[i + 1];
                i++;
                csPin = Integer.parseInt(a);
            } else if (o.contentEquals("-s")) {
                String a = args[i + 1];
                spiBus = SpiBus.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            } else if (o.contentEquals("-t")) { // device address
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(41);
                }
            } else if (o.contentEquals("-chn")) { // pin positive
                String a = args[i + 1];
                i++;
                channel = a;
                if (a.contentEquals("channel_A") | a.contentEquals("channel_B")) {
                } else {
                    console.println("-chn invalid  : " + channel);
                    System.exit(42);
                }
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(44);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(45);
            }
        }


        short pinCount = 8;
        console.println("----------------------------------------------------------");
        console.println("PI4J PROVIDERS");
        console.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");

        DAC8552 spiCls = new DAC8552(spiBus, csPin, console, traceLevel, pi4j);

        if (hasVref) {
            spiCls.DAC8532_Out_Voltage(channel, voutput, vref);
        } else {
            spiCls.DAC8532_Out_Voltage(channel, voutput);

        }

    }

}
