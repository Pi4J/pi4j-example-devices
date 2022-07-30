package com.pi4j.devices.ads1256;
/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  ADS1256App.java
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



import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.util.Console;

public class ADS1256App {

     public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        double vref = 0;
        String ppName = "AINCOM";
        String pnName = "AINCOM";

        int drdyPin = 0;
        int csPin = 0;
        int rsrtPin = 0;
        boolean resetChip = false;
        SpiChipSelect chipSelect = SpiChipSelect.CS_0;
        SpiBus spiBus = SpiBus.BUS_0;

        console.title("<-- The Pi4J V2 Project Extension  -->", "MCP3008App");
        String helpString = " parms:  -vref decimal reference voltage  \n" +
                "-rst resetPin   -cs  chipSelectPin   -drdy  drdyPin   \n" +
                "  -pp -pn  AIN0 AIN1 AIN2 AIN3 AIN4 AIN5 AIN6 AIN7 AINCOM   -x reset \n" +
                "-s HEX value SPI #  -t  trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
                " or \"off\"  Default \"info\""; //   -c HEX value chip select

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
            } else if (o.contentEquals("-rst")) { // device address
                String a = args[i + 1];
                i++;
                rsrtPin = Integer.parseInt(a);
            } else if (o.contentEquals("-cs")) { // device address
                String a = args[i + 1];
                i++;
                csPin = Integer.parseInt(a);
            }  else if (o.contentEquals("-drdy")) { // device address
                String a = args[i + 1];
                i++;
                drdyPin = Integer.parseInt(a);
            } else if (o.contentEquals("-cNotUsed")) {
                String a = args[i + 1];
                chipSelect = SpiChipSelect.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            }else if (o.contentEquals("-x")) {
                 resetChip = true;
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
            }  else if (o.contentEquals("-pp")) { // pin positive
                String a = args[i + 1];
                i++;
                ppName =  a;
                if (a.contentEquals("AIN0") | a.contentEquals("AIN1") | a.contentEquals("AIN2") | a.contentEquals("AIN3") | a.contentEquals("AIN4") | a.contentEquals("AIN5") | a.contentEquals("AIN6") | a.contentEquals("AIN7") | a.contentEquals("AINCOM")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("-pp invalid  : " + traceLevel);
                    System.exit(42);
                }
            } else if (o.contentEquals("-pn")) { // pin positive
                String a = args[i + 1];
                i++;
                pnName =  a;
                if (a.contentEquals("AIN0") | a.contentEquals("AIN1") | a.contentEquals("AIN2") | a.contentEquals("AIN3") | a.contentEquals("AIN4") | a.contentEquals("AIN5") | a.contentEquals("AIN6") | a.contentEquals("AIN7") | a.contentEquals("AINCOM")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("-pn invalid  : " + traceLevel);
                    System.exit(42);
                }
            }else if (o.contentEquals("-h")) {
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

        ADS1256 spiCls = new ADS1256(pi4j, spiBus, chipSelect, drdyPin, csPin, rsrtPin,  ppName, pnName,  console, traceLevel, vref);


        if(resetChip){
            spiCls.doReset();
        }

        spiCls.displayProgramID();

         spiCls.displayProgramID();
         spiCls.displayProgramID();
         spiCls.displayProgramID();
         spiCls.displayProgramID();
         spiCls.displayProgramID();


        spiCls.displayADS1256State(ppName, pnName);

    }

}