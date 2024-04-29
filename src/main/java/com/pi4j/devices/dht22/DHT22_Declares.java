/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  DHT22_Declares.java
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

package com.pi4j.devices.dht22;

public class DHT22_Declares {


    // MCU/Pi, initiate sensor OP
    protected final static int BEGIN_READINGS_MILLS = 20;
    // these two signal the sensor is prepared to send data
    protected final static int PREPARE_DATA_LOW_PULSE_MICS = 80;
    protected final static int PREPARE_DATA_HIGH_PULSE_MICS = 80;

    // data signals '0 or '1' by pulse length
    protected final static int ZERO_PULSE_MICS = 27;  // zero bit
    protected final static int ONE_PULSE_MICS = 70;   //  one bit


    protected final static int TOTAL_NUM_BITS = 40;
    protected final static int RH_NUM_BITS = 16;
    protected final static int T_NUM_BITS = 16;
    protected final static int CKSUM_NUM_BITS = 8;


}
