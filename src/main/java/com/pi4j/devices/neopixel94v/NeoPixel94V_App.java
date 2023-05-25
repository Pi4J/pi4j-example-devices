/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  NeoPixel94V_App.java
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

package com.pi4j.devices.neopixel94v;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.LifecycleException;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.Scanner;

public class NeoPixel94V_App {


    public static void main(String[] args) throws Exception {
        var console = new Console();
        Context pi4j =  Pi4J.newAutoContext();

        console.title("<-- The Pi4J V2 Project Extension  -->", "NeoPixel94V");


        int pixels = 8;

        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Performing ctl-C shutdown");
                try {
                    pi4j.shutdown();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
                // Thread.dumpStack();
                System.exit(2);
            }
        });
    final NeoPixel94V ledStrip = new NeoPixel94V(pi4j, console, pixels, 0.5, 21, "trace");

    //set them all off, so nothing is shining
        System.out.println("All Off");
        ledStrip.allOff();

        ledStrip.waitForInput();


        System.out.println("setting the LEDs to RED");
        ledStrip.setStripColor(ledStrip.pixel.RED);
        ledStrip.render();
        ledStrip.sleep(3000,0);

        ledStrip.waitForInput();

        System.out.println("setting the first led to green");
        ledStrip.setPixelColor(0, ledStrip.pixel.GREEN);
        ledStrip.render();
        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();


        System.out.println("setting the LEDs to Light Blue");
        ledStrip.setStripColor(ledStrip.pixel.LIGHT_BLUE);
        ledStrip.render();
         ledStrip.sleep(3000,0);

        ledStrip.waitForInput();


        System.out.println("setting the first led to Purple");
        ledStrip.setPixelColor(0, ledStrip.pixel.PURPLE);
        ledStrip.render();
        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();


        System.out.println("All Off");
        ledStrip.allOff();

        ledStrip.waitForInput();

        System.out.println("setting the brightness to full and just show the first led as White");
        ledStrip.setBrightness(1);
        ledStrip.setPixelColor(0, ledStrip.pixel.WHITE);
        ledStrip.render();

        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();


        // test assumes there are at least 3 pixels
        System.out.println("All Off");
        ledStrip.allOff();

        ledStrip.waitForInput();

        System.out.println("setting the brightness to full and just show the fourth led as Red");
        ledStrip.setBrightness(1);
        ledStrip.setPixelColor(3, ledStrip.pixel.RED);
        ledStrip.render();
        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();



        // test assumes there are at least 3 pixels
        System.out.println("All Off");
        ledStrip.allOff();

        ledStrip.waitForInput();

        System.out.println("setting the brightness to full and just show the sixth led as orange");
        ledStrip.setBrightness(1);
        ledStrip.setPixelColor(5, ledStrip.pixel.ORANGE);
        ledStrip.render();
        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();



        // test assumes there are at least 3 pixels
        System.out.println("All Off");
        ledStrip.allOff();

        ledStrip.waitForInput();

        System.out.println("setting the brightness to full and just show the seventh led as  yellow");
        ledStrip.setBrightness(1);
        ledStrip.setPixelColor(6, ledStrip.pixel.YELLOW);
        ledStrip.render();
        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();


        // test assumes there are at least 3 pixels
        System.out.println("All Off");
        ledStrip.allOff();

        ledStrip.waitForInput();

        System.out.println("setting the brightness to full and just show the fifth led as Green");
        ledStrip.setBrightness(1);
        ledStrip.setPixelColor(4, ledStrip.pixel.GREEN);
        ledStrip.render();
        ledStrip.sleep(3000, 0);

        ledStrip.waitForInput();


        //finishing and closing
        ledStrip.close();
        System.out.println("closing the app");
        System.out.println("Color "+ ledStrip.getPixelColor(0));

        System.out.println("LED strip app done.");
}
}
