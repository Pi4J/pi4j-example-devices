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
 *     *   * Copyright (C) 2012 - 2024 Pi4J
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
import com.pi4j.devices.ads1256.ADS125x.DataRate;
import com.pi4j.devices.ads1256.ADS125x.MuxValue;
import com.pi4j.devices.ads1256.AllInputPrinter.PrintingUnits;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.util.Console;

public class ADS1256App {
    public static enum PinTrackingConflictHandler {
        printAndExit,
        printAndRecover,
        silentAndRecover
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();

        double vref = 2.5;
        String ppName = "AINCOM";
        String pnName = "AINCOM";

        boolean crtRestart = false;
        boolean crtPdwn = false;
        int drdyPin = 17;
        int csPin = 22;
        int rsrtPin = 18;
        int pdwnPin = 27;
        boolean resetChip = false;
        SpiChipSelect chipSelect = SpiChipSelect.CS_0;
        SpiBus spiBus = SpiBus.BUS_0;
        DigitalState newState = DigitalState.UNKNOWN;
        boolean setPinState = false;
        boolean readPin = false;
        boolean dumpInputStates = false;
        boolean dumpRepeatedly = false;
        boolean runChaser = false;
        int chaseIntervalMS = 2000;
        boolean useBuffer = false;
        DataRate sampleRate = DataRate.SPS_10;
        boolean useVoting = false;

        int gpiopPin = 42;

        console.title("<-- The Pi4J V2 Project Extension  -->", "ADS1256App");
        String helpString = " parms:  -vref decimal reference voltage  \n" +
            "-rst resetPin   -cs  chipSelectPin   -drdy  drdyPin  -pdwn syn/pwrdPin \n" +
            "  -pp -pn  AIN0 AIN1 AIN2 AIN3 AIN4 AIN5 AIN6 AIN7 AINCOM   -x reset \n" +
            "  -p gpio pin number -rp read \"-p\"   -sp set state \"-p\"  HIGH/LOW \n" +
            "   -s HEX value SPI #  -t  trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
            "  or \"off\"  Default \"info\"\n" +
            "  -di dump all input states as quickly as possible. -dr Dump repeatedly at 1/sec. \n" +
            "  -chase INTERVAL_MS turn one GPIO on at a time, from 0 upward and repeat\n" +
            "  -sps sample rate/sec: 2.5, 5, 10, 15, 25, 30, 50, 60, 100, 500 (default 10SPS)\n" +
            "  -buf Enable analog input buffer (default off) -vo Use 3-way voting/median";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
            } else if (o.contentEquals("-p")) { // pin number
                String a = args[i + 1];
                i++;
                gpiopPin = Integer.parseInt(a);
                if (gpiopPin > 3) {
                    console.println("Invalid GPIO pin number : " + gpiopPin);
                    System.exit(45);
                }
            } else if (o.contentEquals("-rp")) { // read -p
                readPin = true;
            } else if (o.contentEquals("-sp")) { // set state of -p
                String a = args[i + 1];
                a = a.toUpperCase();
                i++;
                if (a.contentEquals("HIGH")) {
                    newState = DigitalState.HIGH;
                    setPinState = true;
                } else if (a.contentEquals("LOW")) {
                    newState = DigitalState.LOW;
                    setPinState = true;
                } else {
                    console.println("Attempted to set invalid pin state: " + a);
                    System.exit(44);
                }
            } else if (o.contentEquals("-rst")) { // device address
                String a = args[i + 1];
                i++;
                rsrtPin = Integer.parseInt(a);
                crtRestart = true;
            } else if (o.contentEquals("-cs")) { // device address
                String a = args[i + 1];
                i++;
                csPin = Integer.parseInt(a);
            } else if (o.contentEquals("-drdy")) { // device address
                String a = args[i + 1];
                i++;
                drdyPin = Integer.parseInt(a);
            } else if (o.contentEquals("-pdwn")) { // device address
                String a = args[i + 1];
                i++;
                pdwnPin = Integer.parseInt(a);
                crtPdwn = true;
            } else if (o.contentEquals("-cNotUsed")) {
                String a = args[i + 1];
                chipSelect = SpiChipSelect.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            } else if (o.contentEquals("-x")) {
                resetChip = true;
            } else if (o.contentEquals("-s")) {
                String a = args[i + 1];
                spiBus = SpiBus.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            } else if (o.contentEquals("-t")) { // trace level
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(41);
                }
            } else if (o.contentEquals("-pp")) { // pin positive
                String a = args[i + 1];
                i++;
                ppName = a;
                if (a.contentEquals("AIN0") | a.contentEquals("AIN1") | a.contentEquals("AIN2") | a.contentEquals("AIN3") | a.contentEquals("AIN4") | a.contentEquals("AIN5") | a.contentEquals("AIN6") | a.contentEquals("AIN7") | a.contentEquals("AINCOM")) {
                } else {
                    console.println("-pp invalid  : " + ppName);
                    System.exit(42);
                }
            } else if (o.contentEquals("-pn")) { // pin negative
                String a = args[i + 1];
                i++;
                pnName = a;
                if (a.contentEquals("AIN0") | a.contentEquals("AIN1") | a.contentEquals("AIN2") | a.contentEquals("AIN3") | a.contentEquals("AIN4") | a.contentEquals("AIN5") | a.contentEquals("AIN6") | a.contentEquals("AIN7") | a.contentEquals("AINCOM")) {
                } else {
                    console.println("-pn invalid  : " + pnName);
                    System.exit(43);
                }
            } else if (o.contentEquals("-chase")) {
                runChaser = true;
                try {
                    chaseIntervalMS = Integer.parseInt(args[i + 1]);
                    i++;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                }//Assume interval not supplied
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(44);
            } else if (o.contentEquals("-di")) {
                dumpInputStates = true;
            } else if (o.contentEquals("-buf")) {
                useBuffer = true;
            } else if (o.contentEquals("-dr")) {
                dumpRepeatedly = true;
            } else if (o.contentEquals("-vo")) {
                useVoting = true;
            } else if (o.contentEquals("-sps")) { // samples per second
                String a = args[i + 1];
                i++;
                a = ("SPS_" + a.replace("d", ".")).strip();
                try {
                    sampleRate = DataRate.valueOf(a);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error parsing SPS argument:");
                    e.printStackTrace();
                    System.exit(44);
                }
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(45);
            }//end default conditional branch
        }//end for(i)

        console.println("----------------------------------------------------------");
        console.println("PI4J PROVIDERS");
        console.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");

        ADS125x ads = new ADS1256(pi4j, spiBus, chipSelect, resetChip, drdyPin, csPin, rsrtPin, crtRestart, pdwnPin, crtPdwn, console, traceLevel, vref, sampleRate, useBuffer);
        if (useVoting)
            ads = new VotingADS125x(ads, 3);

        Thread.sleep(100);//Let settle before talking
        Integer chipID = ads.validateChipID();
        if (chipID != null) {
            System.out.println("Invalid chip ID: " + chipID);
            System.exit(301);
        }

        if (readPin) {
            ads.setGpioDirIn(gpiopPin);
            console.println(" pin " + gpiopPin + "  state : " + (ads.isGpioHigh(gpiopPin) ? "HIGH" : "LOW"));
            System.exit(0);
        } else if (setPinState) {
            ads.setGpioDirOut(gpiopPin);
            ads.setGpio(gpiopPin, newState.isHigh());
            System.exit(0);
        } else if (dumpInputStates) {
            final AllInputPrinter ip = new AllInputPrinter(ads);
            ip.setPrintingUnits(PrintingUnits.VOLTS);
            System.out.print("\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println("Press ENTER to end");
            try {
                while (System.in.available() == 0 || System.in.read() != '\n') {
                    System.out.print("\033[10A");//Go back up 10 lines to rewrite the output
                    ip.printADSXXXInputStates();
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else if (runChaser) {
            final GPIOChaser chaser = new GPIOChaser(ads);
            System.out.println("Press ENTER to end");
            try {
                while (System.in.available() == 0 || System.in.read() != '\n') {
                    chaser.updatePrintAndIterate();
                    Thread.sleep(chaseIntervalMS);
                }//end while(!enter)
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else {
            System.out.println("Press ENTER to end");
            int rtn = ads.readAnalogDifferential(MuxValue.valueOf(ppName).ordinal(), MuxValue.valueOf(pnName).ordinal());
            System.out.println("readAnalogDifferential() returned : channel  :" + ppName + "/" + pnName + "  value  :" + rtn);
            try {
                while (dumpRepeatedly & (System.in.available() == 0 || System.in.read() != '\n')) {
                    System.out.print("\033[1A");//Go back up 1 lines to rewite the output
                    System.out.print("\033[1A");//Go back up 1 line to rewite the output
                    rtn = ads.readAnalogDifferential(MuxValue.valueOf(ppName).ordinal(), MuxValue.valueOf(pnName).ordinal());
                    System.out.println("readAnalogDifferential() returned : channel  :" + ppName + "/" + pnName + "  value  :" + rtn);
                    Thread.sleep(1000);
                }//end while(!enter)
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }//end default conditional branch
    }//end main()
}