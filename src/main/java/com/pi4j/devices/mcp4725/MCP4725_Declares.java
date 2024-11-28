/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  MCP4725_Declares.java
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

package com.pi4j.devices.mcp4725;

public class MCP4725_Declares {
    public static final int _MCP4725_DEFAULT_ADDRESS = 0x62;

    public static final int _MCP4725_SET_EEPROM_SIZE = 0x06;
    public static final int _MCP4725_SET_FAST_SIZE = 0x04;
    public static final int _MCP4725_CHIP_READ_SIZE = 0x05;

    public static final byte _MCP4725_WRITE_CMD_FAST = 0b00000000;
    public static final byte _MCP4725_WRITE_CMD_DAC = 0b01000000;
    public static final byte _MCP4725_WRITE_CMD_DAC_EEPROM = 0b01100000;

    public static final byte _MCP4725_PD_MODE_NORMAL = 0b00000000;
    public static final byte _MCP4725_PD_MODE_INOPT = 0b00000110;

    public static final byte _MCP4725_DAC_PD0_MODE_MASK = 0b00000010;
    public static final byte _MCP4725_DAC_PD1_MODE_MASK = 0b00000100;

    public static final int _MCP4725_EEPROM_PD1_MODE_MASK = 0b10000000;
    public static final byte _MCP4725_EEPROM_PD0_MODE_MASK = 0b01000000;

    // Indicate EEPROM write/update status
    public static final byte _MCP4725_READ_CMD_RDY_BSY_MSK = (byte) 0b10000000;
    public static final byte _MCP4725_READ_CMD_IS_COMPLT = (byte) 0b10000000;
    public static final byte _MCP4725_READ_CMD_IS_NOT_COMPLT = (byte) 0b00000000;

    // POR device load EEPROM
    public static final byte _MCP4725_GEN_CALL_RESET_CMD = (byte) 0b00000110;

    // POR device   PD1 and PD0 set to 0 for normal operation
    public static final byte _MCP4725_GEN_CALL_WAKEUP_CMD = (byte) 0b00001001;


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
