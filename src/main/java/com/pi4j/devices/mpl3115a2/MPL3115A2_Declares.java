/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  MPL3115A2_Declares.java
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

package com.pi4j.devices.mpl3115a2;

import com.pi4j.devices.bmp280.BMP280Declares;

public class MPL3115A2_Declares {



    protected final static int  DEFAULT_ADDRESS   = 0x60;
    protected final static int  DEFAULT_BUS       = 0x01;


    protected final static int GPIO_3_IS_IN     = 0b10000000; //


    protected final static int GPIO_0_MASK      = 0b00000001; //

    protected final static int CTL1_ALT_ALTIM           = 0b10000000;  // OR set to altimeter
    protected final static int CTL1_ALT_PRESS_MASK      = 0b01111111;  // & clear, set to Press
    protected final static int CTL1_SBYB_ACT            = 0b00000001;  // OR set to active
    protected final static int CTL1_SBYB_STBY_MASK      = 0b11111110;  // & Clear, set to standby
    protected final static int CTL1_SBYB_SFT_RESET      = 0b00000100;  // Sofware reset
    protected final static int CTL1_OVR_SAMPL_MAX       = 0b00111000;  // ration 128

    protected final static int CTL3_PP_OD1_DRAIN        = 0b00010000;  // open drain
    protected final static int CTL3_PP_OD2_DRAIN        = 0b00000001;  // open drain


    protected final static int CTL4_INT_EN_DRDY         = 0b10000000;  // interrupt enabled
    protected final static int CTL4_INT_EN_PW           = 0b00100000;  // interrupt enabled pressure win
    protected final static int CTL4_INT_EN_TW           = 0b00010000;  // interrupt enabled  temp win
    protected final static int CTL4_INT_EN_PTH          = 0b00001000;  // interrupt enabled press limit
    protected final static int CTL4_INT_EN_TTH          = 0b00000100;  // interrupt enabled temp limit

    protected final static int CTL5_INT_CFG_PW          = 0b00100000;  // interrupt PW thru int1
    protected final static int CTL5_INT_CFG_TW          = 0b00010000;  // interrupt TW thru int1
    protected final static int CTL5_INT_CFG_PTH         = 0b00001000;  // interrupt PTH thru int1
    protected final static int CTL5_INT_CFG_TTH         = 0b00000100;  // interrupt TTH thru int1

    protected final static int PT_DATA_CFG_EVNT_ENBL    = 0b00000100;  // enable events
    protected final static int PT_DATA_CFG_EVNT_PA      = 0b00000010;  // for Alt and Press
    protected final static int PT_DATA_CFG_EVNT_T       = 0b00000001; // for Temp


    protected final static int  REG_INT_SOURCE_DRDY     = 0b10000000; // Data ready to read
    protected final static int  REG_INT_SOURCE_PW       = 0b00100000; // pressure/alti limit-window
    protected final static int  REG_INT_SOURCE_TW       = 0b00010000; // temp limit-window


    protected final static int  WHO_AM_I                = 0xC4; // expected chip ID




    protected final static int REG_STATUS       = 0x00;
    protected final static int REG_P_MSB        = 0x01;  // b12-19
    protected final static int REG_P_CSB        = 0x02;   // b4-11
    protected final static int REG_P_LSB        = 0x03;  // b0-3
    protected final static int REG_T_MSB        = 0x04;  // b4-11
    protected final static int REG_T_LSB        = 0x05;   // b0-3
    protected final static int REG_DR_STATUS    = 0x06;
    protected final static int REG_P_DELTA_MSB  = 0x07;  //b12-19
    protected final static int REG_P_DELTA_CSB  = 0x08;   // b4-11
    protected final static int REG_P_DELTA_LSB  = 0x09;    // b0-3;
    protected final static int REG_T_DELTA_MSB  = 0x0A;  //b4-11
    protected final static int REG_T_DELTA_LSB  = 0x0B;    // b0-3;
    protected final static int REG_WHO_AM_I     = 0x0C;   // ID
    protected final static int REG_F_STATUS     = 0x0D;   // fifo status
    protected final static int REG_F_DATA       = 0x0E;  // fifo 8 bit access
    protected final static int REG_F_SETUP      = 0x0F;  // fifo setup
    protected final static int REG_TIME_DLY     = 0x10;  // since fifo overflow
    protected final static int REG_SYSMOD       = 0x11;   // current system mode
    protected final static int REG_INT_SOURCE   = 0x12;  // interrupt status
    protected final static int REG_PT_DATA_CFG  = 0x13;   // dta event flag
    protected final static int REG_BAR_IN_MSB   = 0x14;  // b8-15 input for ALT calc
    protected final static int REG_BAR_IN_LSB   = 0x15;  // b0-7nput for ALT calc
    protected final static int REG_P_TGT_MSB    = 0x16;  // b8-15 input for ALT calc
    protected final static int REG_P_TGT_LSB    = 0x17;  // b0-7nput for ALT calc
    protected final static int REG_T_TGT        = 0x18;   // temp tgt
    protected final static int REG_P_WND_MSB    = 0x19;   // b8-15
    protected final static int REG_P_WND_LSB    = 0x1A;   // b0-7
    protected final static int REG_T_WND        = 0x1B;   // temp window value
    protected final static int REG_P_MIN_MSB    = 0x1C;   // b12-19
    protected final static int REG_P_MIN_CSB    = 0x1D;   // b4-11
    protected final static int REG_P_MIN_LSB    = 0x1E;   // b0-3
    protected final static int REG_T_MIN_MSB    = 0x1F;  // b8-15
    protected final static int REG_T_MIN_LSB    = 0x20;  // b0-7
    protected final static int REG_P_MAX_MSB    = 0x21;   // b12-19
    protected final static int REG_P_MAX_CSB    = 0x22;   // b4-11
    protected final static int REG_P_MAX_LSB    = 0x23;   // b0-3
    protected final static int REG_T_MAX_MSB    = 0x24;  // b8-15
    protected final static int REG_T_MAX_LSB    = 0x25;  // b0-7
    protected final static int REG_CTRL1        = 0x26;
    protected final static int REG_CTRL2        = 0x27;
    protected final static int REG_CTRL3        = 0x28;
    protected final static int REG_CTRL4        = 0x29;
    protected final static int REG_CTRL5        = 0x2A;
    protected final static int REG_OFF_P        = 0x2B;
    protected final static int REG_OFF_T        = 0x2C;
    protected final static int REG_OFF_H        = 0x2D;

// twenty bit data
    //          7  6  5  4  3  2  1  0
    // b19-12   x  x  x  x  x  x  x  x
    // b11-4    x  x  x  x  x  x  x  x
    // b3-0     x  x  x  x  -  -  -  -

// sixteen bit data
    //          7  6  5  4  3  2  1  0
    // b15-8    x  x  x  x  x  x  x  x
    // b7-0     x  x  x  x  x  x  x  x



// twelve bit data
    //          7  6  5  4  3  2  1  0
    // b11-4    x  x  x  x  x  x  x  x
    // b3-0     x  x  x  x  -  -  -  -




}
