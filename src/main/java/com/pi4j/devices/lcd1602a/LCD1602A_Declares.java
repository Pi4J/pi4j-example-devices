

package com.pi4j.devices.lcd1602a;

public class LCD1602A_Declares {
    public static final int clearDispCMD = 0b0000000001;


    public static final int returnHomeCMD = 0b0000000010;

    public static final int entryModeCMD = 0b0000000100;
    public static final int entryModeIncCMD = 0b0000000010;
    public static final int entryModeNoIncCMD = 0b0000000000;
    public static final int entryModeShiftCMD = 0b0000000001;


    public static final int dispCMD = 0b0000001000;
    public static final int dispOnBit = 0b0000000100;
    public static final int dispOffBit = 0b0000000000;
    public static final int dispOnCMD = 0b0000000000;
    public static final int dispCrsOnBit = 0b0000000010;
    public static final int dispCrsOffBit = 0b0000000000;
    public static final int dispBlnkOnBit = 0b0000000001;
    public static final int dispBlnkOffBit = 0b0000000000;

    //                                             sr
    //                                             cl
    public static final int cursorCMD = 0b0000010000;
    public static final int cusorLeftBit = 0b0000000000;
    public static final int cusorRghtBit = 0b0000000100;
    public static final int displayLeftBit = 0b0000001000;
    public static final int displayRightBit = 0b0000001100;

    public static final int funcSetCMD = 0b0000100000;
    public static final int func4BitsBit = 0b0000000000;
    public static final int func8BitsBit = 0b0000010000;
    public static final int func5x8OneBit = 0b0000000000;
    public static final int func5x10OneBit = 0b0000000100;
    public static final int func5x8TwoBit = 0b0000001000;

    //  Not normally used                aaaaaa
    public static final int setCGRAMCMD = 0b0001000000;
    //                                  aaaaaaa
    public static final int setDDRAMCMD = 0b0010000000;


    public static final int readBsfFlagCMD = 0b0100000000;


    // nano seconds
    // write operations
    public static final int preWrtEnableCycleDelay = 500;   // 400
    public static final int enableWrtCycleDuration = 200;  // 150
    public static final int postWrtEnableCycleDelay = 500;   // 400

    public static final int addressWrtSetupDuration = 50; // 30
    public static final int postAddressWrtSetupDelay = 90;   // 60

    public static final int dataWrtSetupDuration = 90;   // 60
    public static final int postDataWrtDelay = 60;   // 10

    // Read operations
    public static final int preRdEnableCycleDelay = 500;   // 400
    public static final int enableRdCycleDuration = 200;  // 150
    public static final int postResetEnableCycleDelay = 100;
    public static final int addressRdSetupDuration = 50; // 30
    public static final int preAddressWrtSetupDelay = 20;   // 10

    public static final int dataRdSetupDuration = 240;   // 100
    public static final int postDataRdDelay = 10;   // 5
    public static int enableWidthDuration = 450;
}
