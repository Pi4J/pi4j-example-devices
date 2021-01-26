/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  GpioBasics.java
 *  *   *
 *  *   * This file is part of the Pi4J project. More information about
 *  *   * this project can be found here:  https://pi4j.com/
 *  *   * **********************************************************************
 *    * %%
 *  *   * Copyright (C) 2012 - 2021 Pi4J
 *     * %%
 *    * Licensed under the Apache License, Version 2.0 (the "License");
 *    * you may not use this file except in compliance with the License.
 *    * You may obtain a copy of the License at
 *    *
 *    *      http://www.apache.org/licenses/LICENSE-2.0
 *    *
 *    * Unless required by applicable law or agreed to in writing, software
 *    * distributed under the License is distributed on an "AS IS" BASIS,
 *    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    * See the License for the specific language governing permissions and
 *    * limitations under the License.
 *    * #L%
 *  *
 *  *
 *
 *
 */

package com.pi4j.devices.base_util.gpio;

import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.context.Context;

/**
 * GpioBasics is a worker class intended to contain utilities required by various classes
 */
public interface GpioBasics {

    /**
     * sleepMs uses the Thread class to pause execution of the calling thread.
     *
     * @param mSecs millsecs to wait.
     */
    default void sleepMS(int mSecs, FfdcUtil ffdc) {
        try {
            Thread.sleep(mSecs, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            ffdc.ffdcErrorEntry("Sleep failed");
        }
    }

    /**
     * reset_chip
     * <p>
     *     This method does not use the Pin Dictionary, It creates the
     *     DigitalOutput instance as needed.
     * </p>
     * <p>
     * PreCond: GpioBasics instance initialized.  See CTOR
     *
     * @param  resetGpio Gpio pin number to use
     * @param pi4j Context
     * @param  delay   time to wait between driving resetGpio
     * @param bar  if bar true, the chip reset pin is BAR, reset condition is LOW
     * @param ffdc  for logging information
     *     <p>
     *      PostCond:  Pin driven and restored to initial state
     *      </p>
     */
    default void reset_chip(int resetGpio, Context pi4j, int delay, boolean bar, FfdcUtil ffdc) {
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("resetPin")
                .name("Chip reset")
                .address(resetGpio)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        DigitalOutput resetPin = null;
        try {
            resetPin = pi4j.create(ledConfig);
        } catch (Exception e) {
            e.printStackTrace();
            ffdc.ffdcErrorExit(String.format("reset_chip  %s" ,e.toString()), 600);
        }
        try {
            if(bar) {  // active low
                resetPin.low();
                this.sleepMS(delay, ffdc);
                resetPin.high();
            }else{
                resetPin.high();
                this.sleepMS(delay, ffdc);
                resetPin.low();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
      }


}
