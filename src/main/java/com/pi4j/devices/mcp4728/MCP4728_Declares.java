

package com.pi4j.devices.mcp4728;



public class MCP4728_Declares {
    public static final int _MCP4728_DEFAULT_ADDRESS = 0x60;

    public static final int _MCP4728_SET_EEPROM_SIZE = 0x06;
    public static final int _MCP4728_SET_FAST_SIZE = 0x04;
    public static final int _MCP4728_CHIP_READ_SIZE = 0x05;

    // Table 5.1
    public static final byte _MCP4728_WRITE_CMD_FAST = 0b00000000;
    public static final byte _MCP4728_WRITE_CMD_MULTI_DAC_EEPROM      = 0b01000000;
    public static final byte _MCP4728_WRITE_CMD_SNGL_DAC_EEPROM     = 0b01011000;

    public static final byte _MCP4728_WRITE_CMD_I2C_ADDR = 0b01100000;


    public static final byte _MCP4728_WRITE_CMD_SEQ_DAC_EEPROM      = 0b01010000;

    public static final byte _MCP4728_WRITE_CMD_VREF_SLCT_DAC     = (byte) 0b10000000;
    public static final byte _MCP4728_WRITE_CMD_GAIN_SLCT_DAC     = (byte) 0b11000000;
    public static final byte _MCP4728_WRITE_CMD_PD_BITS_DAC       = (byte) 0b10100000;







    public static final byte _MCP4728_PD_MODE_NORMAL = 0b00000000;
    public static final byte _MCP4728_PD_MODE_INOPT = 0b00000110;

    public static final byte _MCP4728_DAC_PD0_MODE_MASK = 0b00000010;
    public static final byte _MCP4728_DAC_PD1_MODE_MASK = 0b00000100;

    public static final int _MCP4728_EEPROM_PD1_MODE_MASK = 0b10000000;
    public static final byte _MCP4728_EEPROM_PD0_MODE_MASK = 0b01000000;

    // Indicate EEPROM write/update status
    public static final byte _MCP4728_READ_CMD_RDY_BSY_MSK = (byte) 0b10000000;
    public static final byte _MCP4728_READ_CMD_IS_COMPLT = (byte) 0b10000000;
    public static final byte _MCP4728_READ_CMD_IS_NOT_COMPLT = (byte) 0b00000000;

    // POR device load EEPROM
    public static final byte _MCP4728_GEN_CALL_RESET_CMD = (byte) 0b00000110;

    // POR device   PD1 and PD0 set to 0 for normal operation
    public static final byte _MCP4728_GEN_CALL_WAKEUP_CMD = (byte) 0b00001001;


    // Write
    //  C2=0  C1=1 C0=0 DAC only
    //  C2=0  C1=1 C0=1 DAC and EEPROM
    //  byte1                  byte2                            byte3                       byte4
    // address r/w       C2 C1 C0 x x PD1 PD0 X           D11 D10 D9 D8 D7 D6 D5 D4      D3 D2 D1 D0 X X X X

    // Read   DAC
    //  byte1                  byte2                            byte3                         byte4
    // address r/w       RDY/BSY C1 C0 x x PD1 PD0 X      D11 D10 D9 D8 D7 D6 D5 D4      D3 D2 D1 D0 X X X X

    //   EEPROM
    //    byte5                                    byte6
    //  X PD1 PD0 X D11 D10 D9 D8      D7 D6 D5 D4 D3 D2 D1 D0

}
