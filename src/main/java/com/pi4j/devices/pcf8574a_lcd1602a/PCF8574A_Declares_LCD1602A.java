/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  PCF8574A_Declares.java
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

package com.pi4j.devices.pcf8574a_lcd1602a;

public class PCF8574A_Declares_LCD1602A {

    protected  static final int pcf8574A_OutputValid = 4;
    protected  static final int pcf8574A_InputHold = 4;


    protected  static final int data_bits_mask          = 0b11110000;
    protected  static final int RS_bit_mask             = 0b00000001;


    protected  static final int E_bit_mask              = 0b00000100;    // E P2
    protected  static final int E_bit_mask_off          = 0b11111011;

    protected static final int E_low                    = 0b00000000;   // P2 off
    protected static final int E_high                   = 0b00000100;    // P2 on

    protected static final int backlight_on            = 0b00001000;    // P2 on
    protected static final int backlight_off           = 0b00000000;    // P2 on



    protected  static final int RS_bit_mask_off         = 0b11111110;    // RS P0
    protected static final int RS_low  = 0b00000000;      // P0 off
    protected static final int RS_high = 0b00000001;    // P0 on


    protected  static final int RW_bit_mask_off         = 0b11111101;    // RW P1
    protected static final int RW_low                   = 0b00000000;      // P1 off
    protected static final int RW_high                  = 0b00000010;    // P1 on


}
