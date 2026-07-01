

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
