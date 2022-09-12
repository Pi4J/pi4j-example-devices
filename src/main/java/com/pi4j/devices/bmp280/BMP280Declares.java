package com.pi4j.devices.bmp280;/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  BMP280Declares.java
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

/**
 * Register address, values and masks
 */
public class BMP280Declares {

    /**
     * Constant <code>NAME="BMP.I2C_PROVIDER_NAME"</code>
     */
    String NAME = BMP280Device.I2C_PROVIDER_NAME;
    /**
     * Constant <code>ID="BMP.I2C_PROVIDER_ID"</code>
     */
    String ID = BMP280Device.I2C_PROVIDER_ID;

    public static final int DEFAULT_ADDRESS = 0x77;

    public static final int DEFAULT_BUS = 0x1;


    /*  Begin device register definitions.        */
    static int temp_xlsb = 0xFC;
    static int temp_lsb = 0xFB;
    static int temp_msb = 0xFA;
    static int press_xlsb = 0xF9;
    static int press_lsb = 0xF8;
    static int press_msb = 0xF7;
    static int config = 0xF5;
    static int ctrl_meas = 0xF4;
    static int status = 0xF3;
    static int reset = 0xE0;
    static int chipId = 0xD0;


    // errata register definitions
    static int reg_dig_t1 = 0x88;
    static int reg_dig_t2 = 0x8A;
    static int reg_dig_t3 = 0x8C;

    static int reg_dig_p1 = 0x8E;
    static int reg_dig_p2 = 0x90;
    static int reg_dig_p3 = 0x92;
    static int reg_dig_p4 = 0x94;
    static int reg_dig_p5 = 0x96;
    static int reg_dig_p6 = 0x98;
    static int reg_dig_p7 = 0x9A;
    static int reg_dig_p8 = 0x9C;
    static int reg_dig_p9 = 0x9E;

    // register contents
    static int reset_cmd = 0xB6;  // written to reset

    // Pertaining to 0xF3 status register
    static int stat_measure = 0x08;  // set, conversion running
    static int stat_update = 0x01;  // set, NVM being copied

    // Pertaining to 0xF4 ctrl_meas register
    static int tempOverSampleMsk = 0xE0;  // mask bits 5,6,7
    static int presOverSampleMsk = 0x1C;  // mask bits 2,3,4
    static int pwrModeMsk = 0x03;  // mask bits 0,1


    // Pertaining to 0xF5 config register
    static int inactDurationMsk = 0xE0;  // mask bits 5,6,7
    static int iirFltMsk = 0x1C;  // mask bits 2,3,4
    static int enableSpiMsk = 0x01;  // mask bits 0

    // Pertaining to 0xF7 0xF8 0xF9 press  register
    static int pressMsbMsk = 0xFF;  // mask bits 0 - 7
    static int pressLsbMsk = 0xFF;  // mask bits 0 - 7
    static int pressXlsbMsk = 0x0F;  // mask bits 0 - 3

    // Pertaining to 0xFA 0xFB 0xFC temp  register
    static int tempMsbMsk = 0xFF;  // mask bits 0 - 7
    static int tempLsbMsk = 0xFF;  // mask bits 0 - 7
    static int tempXlsbMsk = 0x0F;  // mask bits 0 - 3
    static int idValueMsk = 0x58;   // expected chpId value

    // For the control reg 0xf4
    static int ctl_forced = 0x01;
    static int ctl_tempSamp1 = 0x20;   // oversample *1
    static int ctl_pressSamp1 = 0x04;   // oversample *1


}
