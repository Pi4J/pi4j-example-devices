package com.pi4j.devices.is31Fl37Matrix;/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  ControlLeds.java
 *      *
 *      * This file is part of the Pi4J project. More information about
 *      * this project can be found here:  https://pi4j.com/
 *      * **********************************************************************
 *     * %%
 *     * Copyright (C) 2012 - 2022 Pi4J
 *     * %%
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
 *   *
 *
 *
 */

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.util.Console;
import org.slf4j.Logger;

public class ControlLeds {


    private final Logger logger;


        public ControlLeds(Context pi4j, Console console, DigitalOutput red_led_pin, DigitalOutput green_led_pin, Is31Fl37Matrix matrix, Logger logger  ) {
            this.redLED = red_led_pin;
            this.greenLED = green_led_pin;
            this.matrix = matrix;
                   this.pi4j = pi4j;
            this.console = console;
            this.logger = logger;

            this.init();
        }

        private boolean init() {
            // this.gpio = new BaseGpioInOut(this.ffdc, this.dioPinData,
            // this.ffdc.logger);


           /* this..gpio.getCfgData(RaspiBcmPin.GPIO_22).input .addListener(new
             GpioInterruptListener());
            */
            return (true);
        }

        public void flash_alarm_led(boolean enable, Is31Fl37Matrix matrix) {
            this.logger.trace("flash_alarm_led  <enable> " + enable);
            this.logger.error("WaitOnInterrupt failed");

            if(this.greenLED != null) {
                this.greenLED.low();
            }

            matrix.blink_write(0);
            for (int i = 0; i < 7; i++) {
                matrix.fill(7, (byte) 0, 0);
            }
            if(this.redLED != null) {
                while (true) {
                    this.redLED.toggle();
                    matrix.blink_write(0);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.exit(20); // logging disabled so use System call
                    }
                }
            }

        }

        public void toggle_led(boolean active) {
            this.logger.trace("toggle_led  <state> " + active);
            if ((this.greenLED != null) && (this.redLED != null)) {
                if (active) {
                    this.greenLED.high();
                    this.redLED.low();
                } else {
                    this.greenLED.low();
                    this.redLED.high();
                }
            }

             this.logger.trace("toggle_led");

        }

        private boolean waitIntpLoop() {
            boolean is_done = false;
            int counter = 0;
            int MAX = 26000;
            this.logger.trace("waitIntpLoop");

            while (true) {
              /*  if (this.gpio.readPin(RaspiBcmPin.GPIO_22) == PinState.LOW) {
                    is_done = true;
                    this.ffdc.addFfdcEntry("GPIO 22 is LOW");
                    break;
                }
*/
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                counter++;
                if (counter > MAX) {
                    this.logger.error("wait_for_interupt: failed, exceeded MAX" + MAX);
                    break;
                }
            }
            this.logger.trace(
                    "waitIntpLoop <completion> " + is_done + " counter " + String.format("0x%02X", counter));

            return (is_done);
        }

      /*  public boolean waitIntrpJython() {
            this.logger.trace("waitIntrpJython");

            boolean answer = false;
            waitIntrpHook intpObj = new waitIntrpHook();
            answer = intpObj.jython_call_waitIntrp();
            this.logger.trace("waitIntrpJython  <answer > " + answer);

            return (answer);
        }
*/
        // Used....
        public InterruptDetails wait_for_interrupt() {
            this.logger.trace("wait_for_interrupt");

            InterruptDetails rval = matrix.waitIntjLoop();

            // polling loop
            // is_done = waitIntpLoop();
            // skip jython
            // is_done = waitIntrpJython();
            this.logger.trace("wait_for_interrupt  <is_done> " + rval.getSuccessVal() + " counter :"
                    + String.format("0x%02X", rval.getCounter()));
            return (rval);
        }

       int red_led_pin;
        int green_led_pin;
        boolean pin22_low_seen = false;
        Is31Fl37Matrix matrix;
    private final Context pi4j;
    private final Console console;

    DigitalOutput redLED = null;
    DigitalOutput greenLED = null;


    }
