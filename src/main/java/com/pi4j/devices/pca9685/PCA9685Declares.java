/*
 *    * Copyright (C) 2012 - 2025 Pi4J
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
 *  FILENAME      :  PCA9685Declares.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */

package com.pi4j.devices.pca9685;

public class PCA9685Declares {


    static final int defaultClockSpeed = 25000000; //25630710;


    public static final int  MODE1_ADDR     = 0x00;
    public static final int  MODE2_ADDR     = 0x01;
    public static final byte MODE1_SUBADDR1 = 0x08;
    public static final byte MODE1_SUBADDR2 = 0x04;
    public static final byte MODE1_SUBADDR3 = 0x02;


    public static final int RESET_ADDR = 0x06;
    public static final int CONFIG_ADDRESS = 0x70;



    public static final int SUBADR1 = 0x02; // bus1 subaddr 0xE2 bit0 rsrvd
    public static final int SUBADR2 = 0x03; // bus1 subaddr 0xE4 bit0 rsrvd
    public static final int SUBADR3 = 0x04; // bus1 subaddr 0xE2 bit0 rsrvd

    public static final int ALLCALLADR = 0x05; // bus1  0XE0 bit0 rsrvd
    public static final int LED0_ON_L = 0x06;
    public static final int LED0_ON_H = 0x07;
    public static final int LED0_OFF_L = 0x08;
    public static final int LED0_OFF_H = 0x09;

    public static final int LED1_ON_L = 0x0A;
    public static final int LED1_ON_H = 0x0B;
    public static final int LED1_OFF_L = 0x0C;
    public static final int LED1_OFF_H = 0x0D;

    public static final int LED2_ON_L = 0x0E;
    public static final int LED2_ON_H = 0x0F;
    public static final int LED2_OFF_L = 0x10;
    public static final int LED2_OFF_H = 0x11;

    public static final int LED3_ON_L = 0x12;
    public static final int LED3_ON_H = 0x13;
    public static final int LED3_OFF_L = 0x14;
    public static final int LED3_OFF_H = 0x15;

    public static final int LED4_ON_L = 0x16;
    public static final int LED4_ON_H = 0x17;
    public static final int LED4_OFF_L = 0x18;
    public static final int LED4_OFF_H = 0x19;

    public static final int LED5_ON_L = 0x1A;
    public static final int LED5_ON_H = 0x1B;
    public static final int LED5_OFF_L = 0x1C;
    public static final int LED5_OFF_H = 0x1D;

    public static final int LED6_ON_L = 0x1E;
    public static final int LED6_ON_H = 0x1F;
    public static final int LED6_OFF_L = 0x20;
    public static final int LED6_OFF_H = 0x21;

    public static final int LED7_ON_L = 0x22;
    public static final int LED7_ON_H = 0x23;
    public static final int LED7_OFF_L = 0x24;
    public static final int LED7_OFF_H = 0x25;

    public static final int LED8_ON_L = 0x26;
    public static final int LED8_ON_H = 0x27;
    public static final int LED8_OFF_L = 0x28;
    public static final int LED8_OFF_H = 0x29;

    public static final int LED9_ON_L = 0x2A;
    public static final int LED9_ON_H = 0x2B;
    public static final int LED9_OFF_L = 0x2C;
    public static final int LED9_OFF_H = 0x2D;

    public static final int LED10_ON_L = 0x2E;
    public static final int LED10_ON_H = 0x2F;
    public static final int LED10_OFF_L = 0x30;
    public static final int LED10_OFF_H = 0x31;

    public static final int LED11_ON_L = 0x32;
    public static final int LED11_ON_H = 0x33;
    public static final int LED11_OFF_L = 0x34;
    public static final int LED11_OFF_H = 0x35;

    public static final int LED12_ON_L = 0x36;
    public static final int LED12_ON_H = 0x37;
    public static final int LED12_OFF_L = 0x38;
    public static final int LED12_OFF_H = 0x39;

    public static final int LED13_ON_L = 0x3A;
    public static final int LED13_ON_H = 0x3B;
    public static final int LED13_OFF_L = 0x3C;
    public static final int LED13_OFF_H = 0x3D;

    public static final int LED14_ON_L = 0x3E;
    public static final int LED14_ON_H = 0x3F;
    public static final int LED14_OFF_L = 0x40;
    public static final int LED14_OFF_H = 0x41;

    public static final int LED15_ON_L = 0x42;
    public static final int LED15_ON_H = 0x43;
    public static final int LED15_OFF_L = 0x44;
    public static final int LED15_OFF_H = 0x45;

    public static final int ALL_LED_ON_L = 0xFA;
    public static final int ALL_LED_ON_H = 0xFB;

    public static final int ALL_LED_OFFL = 0xFC;
    public static final int ALL_LED_OFF_H = 0xFD;

    public static final int PRE_SCALE = 0xFE;
    public static final int TestMode = 0xFF;


    // MODE1
    public static final int ALLCALL_NO_RSP = 0x00;
    public static final int ALLCALL_RSP = 0x01;

    public static final int I2C_SUB3NO_RSP = 0x00;
    public static final int I2C_SUB3_RSP = 0x02;

    public static final int I2C_SUB2_NO_RSP = 0x00;
    public static final int I2C_SUB2_RSP = 0x04;

    public static final int I2C_SUB1_NO_RSP = 0x00;
    public static final int I2C_SUB1_RSP = 0x08;

    public static final int SLEEP_NORMAL = 0x00;
    public static final int SLEEP_LOW_PWR = 0x10;

    public static final int AUTO_INC_DISA = 0x00;
    public static final int AUTO_INC_ENAB = 0x20;

    public static final int CLCK_INTERN = 0x00;
    public static final int CLCK_EXTERN = 0x40;

    public static final int RESTART_DISA = 0x00;
    public static final int RESTART_ENAB = 0x80;


    // MODE2                                                   c ccxx                    ccccccccccc
    public static final int INVRT_YES = 0x10;
    public static final int OCH_ON_ACK = 0x08; // else ON STOP
    public static final int OUTDRV_TTM = 0x04; // LEDs totem pole. ,else open drain
    public static final int OUTNE = 0x02; //
    //   00*When OE = 1 (output drivers not enabled), LEDn = 0.
    //   01When OE = 1 (output drivers not enabled):
    //     LEDn = 1 when OUTDRV = 1
    //    LEDn = high-impedance when OUTDRV = 0 (same as OUTNE[1:0] = 10)
    //    1X  When OE = 1 (output drivers not enabled), LEDn = high-impedance.
    //  [1]See Section 7.7 “Using the PCA9685 with and without external drivers”
    //                               for more details. Normal LEDs can be driven directly in eit


}