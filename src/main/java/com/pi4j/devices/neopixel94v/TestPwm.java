/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  TestPwm.java
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
import com.pi4j.*;
import com.pi4j.io.pwm.*;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.library.pigpio.PiGpio;

public class TestPwm {
    private static Pwm pwm = null;
    private static Context pi4j;

    public static void main(String[] args) throws Exception
    {
        pi4j = Pi4J.newAutoContext();
        initPiGpio();
        initGPIOCM4();
        pwm.on(50,1);
        while (true){
            // Timeout here?
        }
    }

    private static void initPiGpio()
    {
        var pigpio =  PiGpio.newNativeInstance();
        pigpio.gpioCfgClock(2, 1, 0);
      //  pigpio.initialize();
    }

    private static void initGPIOCM4()
    {
        var configPwm = Pwm.newConfigBuilder(pi4j)
                .address(13)
                .pwmType(PwmType.HARDWARE)
                .provider("pigpio-pwm")
                .initial(0)
                .shutdown(0)
                .build();
        try {
            pwm = pi4j.create(configPwm);
        } catch (Exception e) {
        }
    }

}
