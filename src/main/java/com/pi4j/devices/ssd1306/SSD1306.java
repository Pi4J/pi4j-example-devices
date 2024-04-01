/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  SSD1306.java
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

package com.pi4j.devices.ssd1306;
/*

Co | D/C | 0 0 0 0 0 0

After the transmission of the slave address, either the control byte or the data byte may be sent across
the SDA. A control byte mainly consists of Co and D/C# bits following by six “0” ‘s.
a. If the Co bit is set as logic “0”, the transmission of the following information will contain
data bytes only.
b. The D/C# bit determines the next data byte is acted as a command or a data. If the D/C# bit is
set to logic “0”, it defines the following data byte as a command. If the D/C# bit is set to
logic “1”, it defines the following data byte as a data which will be stored at the GDDRAM.
The GDDRAM column address pointer will be increased by one automatically after each
data write.


Reset Circuit
When RES# input is LOW, the chip is initialized with the following status:
1. Display is OFF
2. 128 x 64 Display Mode
3. Normal segment and display data column address and row address mapping (SEG0 mapped to
address 00h and COM0 mapped to address 00h)
4. Shift register data clear in serial interface
5. Display start line is set at display RAM address 0
6. Column address counter is set at 0
7. Normal scan direction of the COM outputs
8. Contrast control register is set at 7Fh
9. Normal display mode (Equivalent to A4h command)


After VCC become stable, send command AFh for display ON.





 */


import com.pi4j.context.Context;
import com.pi4j.devices.bmp280.BMP280Declares;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSD1306 {

    /**
     * Constant <code>NAME="SSD1306"</code>
     */
    public static final String NAME = "SSD1306";
    /**
     * Constant <code>ID="SSD1306"</code>
     */
    public static final String ID = "SSD1306";


    // I2C Provider name and unique ID
    /**
     * Constant <code>I2C_PROVIDER_NAME="NAME +  I2C Provider"</code>
     */
    public static final String I2C_PROVIDER_NAME = NAME + " SSD1306 I2C Provider";
    /**
     * Constant <code>I2C_PROVIDER_ID="ID + -i2c"</code>
     */
    public static final String I2C_PROVIDER_ID = ID + "-i2c";


    private final Logger logger;
    private final String traceLevel;


    // local/internal I2C reference for communication with hardware chip
    protected I2C i2c = null;

    protected I2CConfig config = null;

    protected Context pi4j = null;

    protected Console console = null;

    protected int busNum = BMP280Declares.DEFAULT_BUS;
    protected int address = BMP280Declares.DEFAULT_ADDRESS;


    /**
     * @param console Context instance used accross application
     * @param bus     Pi bus
     * @param address Device address
     */

    /**
     * @param console    Context instance used accross application
     * @param bus        Pi bus
     * @param address    Device address
     * @param traceLevel for Logger
     */
    public SSD1306(Context pi4j, Console console, int bus, int address, String traceLevel) {
        super();
        this.pi4j = pi4j;
        this.address = address;
        this.busNum = bus;
        this.console = console;
        this.traceLevel = traceLevel;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + SSD1306.class.getName(), this.traceLevel);

        this.logger = LoggerFactory.getLogger(SSD1306.class);
        this.createI2cDevice(); // will set start this.i2c
        this.initialize();
    }


    /**
     * @param console Context instance used across application
     * @param bus     Pi bus
     * @param address Device address
     * @param logger  Instantiated Logger
     */
    public SSD1306(Context pi4j, Console console, int bus, int address, Logger logger) {
        super();
        this.pi4j = pi4j;
        this.address = address;
        this.busNum = bus;
        this.console = console;
        this.traceLevel = "info";  // we were passed the Logger to use
        this.logger = logger;
        this.createI2cDevice(); // will set start this.i2c
        this.initialize();
    }

    /**
     * @param device Set i2c state
     */
    public void setI2c(I2C device) {
        this.logger.info("Enter: setI2c  I2C device   " + device.toString());
        this.i2c = device;
        this.address = device.device();
        this.busNum = device.bus();
        this.logger.info("exit: setI2c  ");
    }

    /**
     * @return i2c state
     */
    public I2C getI2c() {
        this.logger.info("Enter: GetI2c ");
        this.logger.info("Exit: getI2c  I2C device   " + this.i2c);
        return (this.i2c);
    }


    /**
     * Use the state from the Sensor config object and the state pi4j to create
     * a BMP280 device instance
     */
    private void createI2cDevice() {
        this.logger.info("Enter:createI2cDevice   bus  " + this.busNum + "  address " + this.address);

        var address = this.address;
        var bus = this.busNum;

        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
                .bus(bus)
                .device(address)
                .id(id + " " + name)
                .name(name)
                .provider("linuxfsi2c")
                .build();
        this.config = i2cDeviceConfig;
        this.i2c = this.pi4j.create(i2cDeviceConfig);
        this.logger.info("Exit:createI2cDevice  ");
    }


    /**
     * @return string containing a description of the attached I2C path
     */
    public String i2cDetail() {
        this.logger.trace("enter: i2cDetail");
        this.logger.trace("exit: i2cDetail  " + (this.i2c.toString() + " bus : " + this.config.bus() + "  address : " + this.config.device()));
        return (this.i2c.toString() + " bus : " + this.config.bus() + "  address : " + this.config.device());
    }


    /**
     * @return The  device I2cConfig object
     */
    public I2CConfig config() {

        this.logger.trace("enter: config");
        this.logger.trace("exit: config  " + this.config.toString());
        return this.config;
    }


    public void sendCmd(byte cmd) {
        this.logger.info("Enter: sendCmd  : " + String.format("0x%08x ", cmd));
        byte[] cmdData = new byte[2];
        cmdData[0] = SSD1306_Defines.WITH_ONE_COMMAND;
        cmdData[1] = cmd;
        this.i2c.write(cmdData);
        this.logger.info("Exit: sendCmd");
    }

    public void sendCmdData(byte cmd, byte data) {
        this.logger.info("Enter: sendCmdData  CMD: " + String.format("0x%08x ", cmd) + "  Data : " + String.format("0x%08x", data));
        byte[] cmdData = new byte[3];
        cmdData[0] = SSD1306_Defines.WITH_ONE_COMMAND;
        cmdData[1] = cmd;
        cmdData[2] = data;
        this.i2c.write(cmdData);
        this.logger.info("Exit: sendCmdData");
    }


    private void sendData(byte[] dataArray, int arrayLength) {
        this.logger.info("Enter: sendData  ");
        byte[] cmdData = new byte[arrayLength + 1];
        cmdData[0] = SSD1306_Defines.WITH_DATA_ONLY;
        System.arraycopy(dataArray, 0, cmdData, 1, dataArray.length);
        this.i2c.write(cmdData);
        this.logger.info("Exit: sendData  ");
    }


    // Valid all address modes
    // Set starting offset within columns
    protected void setDisplayStartLine(byte line) {
        this.logger.info("Enter: setDisplayRAMStartLine   " + line + " Command value " + (SSD1306_Defines.COMMAND_SET_ADDRESS_LINE + line));
        if (line > 63) {
            this.logger.error("Invalid line, greater than 63");
            System.exit(500);
        }
        this.sendCmd((byte) (SSD1306_Defines.COMMAND_SET_ADDRESS_LINE | (byte) (line & 0x3F)));
        this.logger.info("Exit: setDisplayRAMStartLine");
    }


    // Starting column offset used to display DRAM
    // Appears as though the image data was moved up
    protected void setDisplayComOffset(byte offset) {
        this.logger.info("Enter: setDisplayComOffset   " + offset);
        if (offset > 63) {
            this.logger.error("Invalid line, greater than 63");
            System.exit(500);
        }
        byte[] cmdData = new byte[3];
        cmdData[0] = SSD1306_Defines.WITH_ONE_COMMAND;
        cmdData[1] = SSD1306_Defines.COMMAND_SET_DISPLAY_OFFSET;
        cmdData[2] = (byte) (offset & 0x3F);
        this.i2c.write(cmdData);

        this.logger.info("Exit: setDisplayComOffset");
    }

    protected void setMemoryAddressMode(byte mode) {
        this.logger.info("Enter: setMemoryAddressMode   " + mode);
        this.sendCmdData(SSD1306_Defines.COMMAND_SET_MEM_ADDRESS_MODE, mode);

        this.logger.info("Exit: setMemoryAddressMode");
    }

    // Set start and end column addresses (COL0 - COL127) for display
    // Valid in horizontal or vertical address mode
    protected void setColumnAddress(byte start, byte end) {
        this.logger.info("Enter: setColumnAddress   " + start + "  " + end);
        if ((start > 127) || (end > 127)) {
            this.logger.error("Invalid column, greater than 127");
            System.exit(502);
        }

        byte[] cmdData = new byte[4];
        cmdData[0] = SSD1306_Defines.WITH_ONE_COMMAND;
        cmdData[1] = SSD1306_Defines.COMMAND_SET_COLUMN_ADDRESS;
        cmdData[2] = (byte) (start & 0x7F);
        cmdData[3] = (byte) (end & 0x7F);
        this.i2c.write(cmdData);
        this.logger.info("Exit: setColumnAddress");
    }


    // Send a data buffer to GDDRAM
    protected void sendBuffer(byte[] buffer, int bufferLength) {
        this.logger.info("Enter: sendBuffer  ");
        if (buffer.length < bufferLength) {
            this.logger.error("SendBuffer length exceeds actual buffer size ");
            System.exit(501);
        }
        this.sendData(buffer, bufferLength);
        this.logger.info("Exit: sendBuffer  ");
    }

    private void initialize() {
        this.logger.info("Enter: initialize  ");
        // start of init steps
        this.sendCmd((byte) (SSD1306_Defines.COMMAND_DISPLAY_ON | SSD1306_Defines.DISABLE_DISPLAY));
        //Set MUX Ratio [$A8, $3F]
        this.sendCmdData((byte) (0xA8), (byte) (0x3f));

        //Set display offset [$D3, $00]
        this.sendCmdData((byte) (0xD3), (byte) (0x00));

        //Set segment re-map $A0 / $A1
        this.sendCmd((byte) (0xA0));

        //Set COM output scan direction $C0 / $C8
        this.sendCmd((byte) (0xC0));

        //Set COM pin hardware configuration [$DA, $12]  128x64  --  was 00
        this.sendCmdData((byte) (0xDA), (byte) (0x12));

        //Set contrast [$81, $7F]---  8f
        this.sendCmdData((byte) (0x81), (byte) (0x7f));

        //Set precharge [$D9, $22]
        this.sendCmdData((byte) (0xD9), (byte) (0x22));

        // voltage com detect $DB   20
        this.sendCmdData((byte) 0xDB, (byte) (0x20));


        //Set Oscillator frequency [$D5, $80]
        this.sendCmdData((byte) (0xD5), (byte) (0x80));


        //Enable charge pump [$8D, $14]
        this.sendCmdData((byte) (0x8D), (byte) (0x14));

        //Resume the display $A4
        this.sendCmd((byte) (0xA4));

        // normal display $A6/$A7(inverse)
        this.sendCmd((byte) (0xA6));

        //Turn the display on $AF
        this.sendCmd((byte) (SSD1306_Defines.COMMAND_DISPLAY_ON | SSD1306_Defines.ENABLE_DISPLAY));

        // completion of init steps
        this.logger.info("Exit: initialize  ");
    }

}
