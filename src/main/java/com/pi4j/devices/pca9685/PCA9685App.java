/*
 *    * Copyright (C) 2012 - 2024 Pi4J
 *  * %%
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * -
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: EXTENSION
 *  FILENAME      :  Pca9685App.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */

package com.pi4j.devices.pca9685;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.util.Console;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;


public class PCA9685App {
    private static final Console console = new Console(); // Pi4J Logger helper

    private static final int I2C_BUS = 0x01;
    private static final int I2C_ADDRESS = 0x72; // When connecting SDO to GND = 0x76

    public static void main(String[] args) throws Exception {

        var pi4j = Pi4J.newAutoContext();
        PCA9685 pca = null;
        int address = I2C_ADDRESS;
        int bus = I2C_BUS;
        int pin = 0;
        int addr1 = 0;
        int addr2 = 0;
        int addr3 = 0;

        boolean enablePCA = false;

        String traceLevel = "info";

        String helpString = " parms:  -a hex value address, -b bus, -p OE pin, -h help \n" +
            "  -x reset  -s1 addr1   -s2 addr2   -s3 addr3  -q quit -e E enable/ D disable  -t trace";
        boolean loop = true;
        Scanner scanner = new Scanner(System.in);

        while (loop) {
            boolean addr1_present = false;
            boolean addr2_present = false;
            boolean addr3_present = false;

            boolean quit = false;
            boolean reset = false;

            for (int i = 0; i < args.length; i++) {
                String o = args[i];
                if (o.contentEquals("-a")) {
                    String a = args[i + 1];
                    i++;
                    address = Integer.parseInt(a.substring(2), 16);
                } else if (o.contentEquals("-b")) {
                    String a = args[i + 1];
                    i++;
                    bus = Integer.parseInt(a.substring(2), 16);
                } else if (o.contentEquals("-p")) {
                    String a = args[i + 1];
                    i++;
                    pin = Integer.parseInt(a.substring(2), 16);
                } else if (o.contentEquals("-e")) {
                    String a = (args[i + 1]).toUpperCase();
                    i++;
                    if (a.contains("E")) {
                        enablePCA = true;
                    } else if (a.contains("D")) {
                        enablePCA = false;
                    } else {
                        console.println("  !!! Invalid Parm " + args);
                        console.println(helpString);
                        System.exit(43);
                    }

                } else if (o.contentEquals("-s1")) {
                    String a = args[i + 1];
                    i++;
                    addr1 = Integer.parseInt(a.substring(2), 16);
                    addr1_present = true;
                } else if (o.contentEquals("-s2")) {
                    String a = args[i + 1];
                    i++;
                    addr2 = Integer.parseInt(a.substring(2), 16);
                    addr2_present = true;
                } else if (o.contentEquals("-s3")) {
                    String a = args[i + 1];
                    i++;
                    addr3 = Integer.parseInt(a.substring(2), 16);
                    addr3_present = true;
                } else if (o.contentEquals("-x")) {
                    reset = true;
                } else if (o.contentEquals("-q")) {
                    quit = true;
                } else if (o.contentEquals("-h")) {
                    console.println(helpString);
                    System.exit(39);
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
                } else {
                    console.println("  !!! Invalid Parm " + args);
                    console.println(helpString);
                    System.exit(42);
                }
            }
            if (pca == null) {
                pca = new PCA9685(console, bus, address, pin, pi4j, traceLevel);
            }
            if (quit) {
                endPgm(pi4j);
            }
            // The reset should be the first device code to run.
            // Reset the chip before setting any configuration
            if (reset) {
                detectI2C("i2cdetect -y 1");
                pca.reset();
                detectI2C("i2cdetect -y 1");
            }
            detectI2C("i2cdetect -y 1");

            if (addr1_present) {
                pca.setSubAddr1(0x70, addr1);
            }

            if (addr2_present) {
                pca.setSubAddr2(0x70, addr2);
            }

            if (addr3_present) {
                pca.setSubAddr3(0x70, addr3);
            }

            pca.enablePCA(enablePCA);

            detectI2C("i2cdetect -y 1");

            System.out.println(helpString);
            String name = scanner.nextLine();
            // todo  -x not handled correctly !!!!
            if (!name.contains(" ")) {
                args = name.split("\r");
            } else {
                args = name.split(" ");
            }
        } // loop end

        endPgm(pi4j);
    }

    private static void endPgm(Context pi4j) {
        pi4j.shutdown();
        detectI2C("i2cdetect -y 1");
        console.println("**************************************");
        console.println("Finished");
        System.exit(0);
    }

    private static void detectI2C(String cmd) {

        try {
            Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
