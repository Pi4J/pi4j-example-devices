/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  ADS1256_Declares.java
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

package com.pi4j.devices.ads1256;

public class ADS1256_Declares {


    protected final static int WAKEUP           = 0b00000000;   // wakeup/exist standby
    protected final static int RDATA            = 0b00000001;   // read conversion data
    protected final static int RDDATC           = 0b00000011;  // read conv data continuously
    protected final static int SDATAC           = 0b00001111;   // stop read continuously
    protected final static int RREG             = 0b00010000; //  read register(s)
    protected final static int WREG             = 0b01010000; // write register(s)
    protected final static int SELFCAL          = 0b11110000; // offset/gain self calibration
    protected final static int SELFOCAL         = 0b11110001&0xff; // offset self calibration
    protected final static int SELFGCAL         = 0b11110010;  // gain self calibration
    protected final static int SYSOCAL          = 0b11110011; //system offset calibration
    protected final static int SYSGCAL          = 0b11110100;  // system gain calibration
    protected final static int SYNC             = 0b11111100;  // sync A/D converstion
    protected final static int STANDBY          = 0b11111101;  // begin standby
    protected final static int RESET            = 0b11111110; // reset chip
    protected final static int WAKEUPb          = 0b11111111;   // wakeup/exist standby


    protected final static int REG_STATUS        = 0x00;
    protected final static int REG_MUX           = 0x01;
    protected final static int REG_ADCON         = 0x02;
    protected final static int REG_DRATE         = 0x03;
    protected final static int REG_IO            = 0x04;
    protected final static int REG_OFC0          = 0x05;
    protected final static int REG_OFC1          = 0x06;
    protected final static int REG_OFC2          = 0x07;
    protected final static int REG_FSC0          = 0x08;
    protected final static int REG_FSC1          = 0x09;
    protected final static int REG_FSC2          = 0x0A;


}
