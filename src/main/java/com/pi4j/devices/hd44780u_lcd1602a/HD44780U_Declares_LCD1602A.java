/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  HD44780U_Declares.java
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

package com.pi4j.devices.hd44780u_lcd1602a;

import com.pi4j.io.gpio.digital.DigitalState;

public class HD44780U_Declares_LCD1602A {


    // RS GPIO and RW GPIO. Internal command
    public static final DigitalState RsInternalIr = DigitalState.LOW;
    public static final DigitalState RwInternalIr = DigitalState.LOW;

    // RS GPIO and RW GPIO. Busy Flag  command
    public static final DigitalState RsInternalBf = DigitalState.LOW;
    public static final DigitalState RwInternalBf = DigitalState.HIGH;


    // RS GPIO and RW GPIO.  Write  commands
    public static final DigitalState RsInternalWrtState = DigitalState.HIGH;
    public static final DigitalState RwInternalWrtState = DigitalState.LOW;
    // RS GPIO and RW GPIO.  Read  commands
    public static final DigitalState RsInternalRdState = DigitalState.HIGH;
    public static final DigitalState RwInternalRdState = DigitalState.LOW;

    // E GPIO    enable display/read/write of DR
    public static final DigitalState enableOpState = DigitalState.HIGH;
    public static final DigitalState disableOpState = DigitalState.LOW;


}
