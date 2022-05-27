/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  MCP4725_App.java
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

package com.pi4j.devices.mcp4725;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.devices.mcp4725.MCP4725;
import com.pi4j.devices.mcp4725.MCP4725_Declares;
import com.pi4j.exception.LifecycleException;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

public class MCP4725_App {

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
        console.print("startup  MCP4725_App ");
        console.print("==============================================================");


        int busNum = 1;
        int address = MCP4725_Declares._MCP4725_DEFAULT_ADDRESS;
        boolean existingSet = false;
        boolean doReset = false;
        int registerData = 0;
        boolean setOutputEEPROM = false;
        boolean setOutputFast = false;
        boolean dumpChip = false;
        double vref = 0;

        String helpString = " parms: -b 0x? hex value bus    -a 0x?? hex value address  -t trace   \n " +
                "  -r  reset chip  -d dumpChipData  -rde 0x???? update DAC and EEPROM  " +
                " -rdf 0x????update DAC fast   -vref decimal reference voltage\n " +
                "    trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
                " or \"off\"  Default \"info\"";

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
            } else if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
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
            }  else if (o.contentEquals("-r")) {
                doReset = true;
            }  else if (o.contentEquals("-d")) {
                dumpChip = true;
            }  else if (o.contentEquals("-rde")) {
                String a = args[i + 1];
                i++;
                registerData =  (Integer.parseInt(a.substring(2), 16) );
                setOutputEEPROM = true;
                if(registerData > 0x0fff){
                    console.println("-rde cannot exceed 0x0fff");
                    System.exit(36);
                }
           }  else if (o.contentEquals("-rdf")) {
                String a = args[i + 1];
                i++;
                setOutputFast = true;
                registerData =  (Integer.parseInt(a.substring(2), 16) );
                if(registerData > 0x0fff){
                    console.println("-rdf cannot exceed 0x0fff");
                    System.exit(37);
                }
            }else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }




        MCP4725 dacChip = null;
        dacChip = new MCP4725(pi4j, busNum, address, registerData, traceLevel, vref);
        if (doReset) {
            dacChip.resetChip();
        }

        if(setOutputEEPROM){
            boolean worked = dacChip.setOutputEEPROM(registerData);
            if(worked == false){
                console.println("setOutputEEPROM failed");
            }
        }
        if(setOutputFast){
            boolean worked = dacChip.setOutputFast(registerData);
            if(worked == false){
                console.println("setOutputFast failed");
            }
        }

        if(dumpChip){
            dacChip.dumpChip();
        }
    }

}
