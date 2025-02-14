/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  ADS1256.java
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

package com.pi4j.devices.ads1256;

import com.pi4j.context.Context;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.util.Console;

public final class ADS1256 extends AbstractADS125x {

    @SuppressWarnings("exports")
    public ADS1256(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect,
                   boolean reset, int drdyPin, int csPin, int rstPin,
                   boolean crtRstGpio, int pdwnPin, boolean crtPdwnGpio,
                   Console console, String traceLevel, double vref, DataRate sampleRate, boolean useBuffer)
        throws InterruptedException {
        super(pi4j, spiBus, chipSelect, reset, drdyPin, csPin, rstPin, crtRstGpio,
            pdwnPin, crtPdwnGpio, console, traceLevel, vref, sampleRate, useBuffer);
    }

    @Override
    public int getNumGpioPins() {
        return 4;
    }
    @Override
    public int getNumAnalogPins() {
        return 8;
    }
}//end ADS1256

