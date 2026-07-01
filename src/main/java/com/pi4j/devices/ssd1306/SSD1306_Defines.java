

package com.pi4j.devices.ssd1306;

public class SSD1306_Defines {

    protected static final int SSD1306_I2C_ADDRESS = 0x3C;
    protected static final int SSD1306_I2C_BUS = 1;


    protected static final byte COMMAND_DISPLAY_ON = (byte) 0xAE;
    protected static final byte ENABLE_DISPLAY = (byte) 0x1;

    public static final byte DISABLE_DISPLAY = (byte) 0x00;

    //    0b10000000 = 0x80 = multiple commands
//   0b00000000 = 0x00 = one command
//   0b11000000 = 0xC0 = multiple data
//  0b01000000 = 0x40 = one data byte

    // CO and   D/C bits
    protected static final byte WITH_ONE_COMMAND = 0x00;
    protected static final byte WITH_MULTI_COMMAND = (byte) 0x80;
    protected static final byte WITH_DATA_ONLY = 0x40;
    protected static final byte WITH_MULTI_DATA = (byte) 0xC0;


    // COMMANDS
    protected static final byte COMMAND_SET_MEM_ADDRESS_MODE = 0x20;
    protected static final byte COMMAND_SET_MEM_ADDRESS_MODE_PAGE = 0x02;
    protected static final byte COMMAND_SET_MEM_ADDRESS_MODE_VERT = 0x01;
    protected static final byte COMMAND_SET_MEM_ADDRESS_MODE_HORZ = 0x00;


    protected static final byte COMMAND_SET_COLUMN_ADDRESS = 0x21;
    // Set page address
    protected static final byte COMMAND_SET_PAGE_ADDRESS = 0x22;

    protected static final byte COMMAND_SET_ADDRESS_LINE = 0x40;

    protected static final byte COMMAND_SET_DISPLAY_OFFSET = (byte) 0xD3;


}
