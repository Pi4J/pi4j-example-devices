

package com.pi4j.devices.mcp23017_lcd1602a;

public class MCP23017_LCD1602A_Declares {

    protected static final int E_bit_mask = 0b10000000000;    // E P2
    protected static final int E_bit_mask_off = 0b01111111111;

    protected static final int EN_MCP_PinNum = 10;
    protected static final int E_low = 0b00000000000;   // P2 off
    protected static final int E_high = 0b10000000000;    // P2 on


    protected static final int RS_MCP_PinNum = 8;

    protected static final int RS_bit_mask_off = 0b11011111111;    // RS P0
    protected static final int RS_low = 0b00000000000;      // P0 off
    protected static final int RS_high = 0b00100000000;    // P0 on


    protected static final int RW_MCP_PinNum = 9;

    protected static final int RW_bit_mask_off = 0b10111111111;    // RW P1
    protected static final int RW_low = 0b00000000000;      // P1 off
    protected static final int RW_high = 0b01000000000;    // P1 on

    protected static final int _IODIR = 0x00;
    protected static final int _IPOL = 0x01;
    protected static final int _GPINTEN = 0x02;
    protected static final int _DEFVAL = 0x03;
    protected static final int _INTCON = 0x04;
    protected static final int _IOCON = 0x05;
    protected static final int _GPPU = 0x06;
    protected static final int _INTF = 0x07;
    protected static final int _INTCAP = 0x08;
    protected static final int _GPIO = 0x09;
    protected static final int _OLAT = 0x0A;

    protected static final int _IODIRB = 0x00;
    protected static final int _IPOLB = 0x01;
    protected static final int _GPINTENB = 0x02;
    protected static final int _DEFVALB = 0x03;
    protected static final int _INTCONB = 0x04;
    protected static final int _IOCONB = 0x05;
    protected static final int _GPPUB = 0x06;
    protected static final int _INTFB = 0x07;
    protected static final int _INTCAPB = 0x08;
    protected static final int _GPIOB = 0x09;
    protected static final int _OLATB = 0x0A;


}
