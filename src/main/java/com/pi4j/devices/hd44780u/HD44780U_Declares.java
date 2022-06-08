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

package com.pi4j.devices.hd44780u;

import com.pi4j.io.gpio.digital.DigitalState;

public class HD44780U_Declares {
    static final int  clearDispCMD = 0b0000000001;


    static final int  returnHomeCMD = 0b0000000010;

    static final int  entryModeCMD = 0b0000000100;
    static final int  entryModeIncCMD = 0b0000000010;
    static final int  entryModeNoIncCMD = 0b0000000000;
    static final int  entryModeShiftCMD = 0b0000000001;


    static final int  dispCMD = 0b0000001000;
    static final int  dispOnBit = 0b0000000100;
    static final int  dispOffBit = 0b0000000000;
    static final int  dispOnCMD = 0b0000000000;
    static final int  dispCrsOnBit = 0b0000000010;
    static final int  dispCrsOffBit = 0b0000000000;
    static final int  dispBlnkOnBit = 0b0000000001;
    static final int  dispBlnkOffBit = 0b0000000000;

   //                                             sr
   //                                             cl
    static final int  cursorCMD         = 0b0000010000;
    static final int  cusorLeftBit      = 0b0000000000;
    static final int  cusorRghtBit      = 0b0000000100;
    static final int  displayLeftBit    = 0b0000001000;
    static final int  displayRightBit   = 0b0000001100;

    static final int  funcSetCMD = 0b0000100000;
    static final int  func4BitsBit = 0b0000000000;
    static final int  func8BitsBit = 0b0000010000;
    static final int  func5x8OneBit = 0b0000000000;
    static final int  func5x10OneBit = 0b0000000100;
    static final int  func5x8TwoBit = 0b0000001000;

    //  Not normally used                aaaaaa
    static final int  setCGRAMCMD = 0b0001000000;
    //                                  aaaaaaa
    static final int  setDDRAMCMD = 0b0010000000;


    static final int  readBsfFlagCMD = 0b0100000000;



     // RS GPIO and RW GPIO. Internal command
    static final DigitalState RsInternalIr = DigitalState.LOW;
    static final DigitalState RwInternalIr = DigitalState.LOW;

    // RS GPIO and RW GPIO. Busy Flag  command
    static final DigitalState RsInternalBf = DigitalState.LOW;
    static final DigitalState RwInternalBf = DigitalState.HIGH;




    // RS GPIO and RW GPIO.  Write  commands
    static final DigitalState RsInternalWrtState = DigitalState.HIGH;
    static final DigitalState RwInternalWrtState = DigitalState.LOW;
    // RS GPIO and RW GPIO.  Read  commands
    static final DigitalState RsInternalRdState = DigitalState.HIGH;
    static final DigitalState RwInternalRdState = DigitalState.LOW;

    // E GPIO    enable display/read/write of DR
    static final DigitalState enableOpState = DigitalState.HIGH;
    static final DigitalState disableOpState = DigitalState.LOW;

    // nano seconds
    // write operations
    static final int preWrtEnableCycleDelay = 500;   // 400
    static final int enableWrtCycleDuration = 200;  // 150
    static final int postWrtEnableCycleDelay = 500;   // 400

    static final int addressWrtSetupDuration = 50; // 30
    static final int postAddressWrtSetupDelay = 90;   // 60

    static final int dataWrtSetupDuration = 90;   // 60
    static final int postDataWrtDelay = 20;   // 10

    // Read operations
    static final int preRdEnableCycleDelay = 500;   // 400
    static final int enableRdCycleDuration = 200;  // 150
    static final int postRdEnableCycleDelay = 500;   // 400

    static final int addressRdSetupDuration = 50; // 30
    static final int postAddressRdSetupDelay = 20;   // 10

    static final int dataRdSetupDuration = 240;   // 100
    static final int postDataRdDelay = 10;   // 5
    public static int enableWidthDuration = 450;
}
