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
     * @param console Context instance used accross application
     * @param bus     Pi bus
     * @param address Device address
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
                .provider("linuxfs-i2c")
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



    public void sendCmd(byte cmd){
        this.logger.info("Enter: sendCmd  : "+  String.format("0x%08x ",cmd));
        byte[] cmdData = new byte[2];
        cmdData[0] = SSD1306_Defines.WITH_ONE_COMMAND;
        cmdData[1] = cmd;
        this.i2c.write(cmdData);
        this.logger.info("Exit: sendCmd");
    }
    public void sendCmdOneData(byte cmd, byte data){
        this.logger.info("Enter: sendCmdData  CMD: "+  String.format("0x%08x ",cmd) + "  Data : " +  String.format("0x%08x",data));
        byte[] cmdData = new byte[3];
        cmdData[0] = SSD1306_Defines.WITH_ONE_DATA; //WITH_MULTI_COMMAND;   -----
        cmdData[1] = cmd;
        cmdData[2] = data;
        //this.i2c.write(cmdData);

        this.i2c.write(cmdData[0], cmdData[1]);
        this.i2c.write(cmdData[0], cmdData[2]);

        this.logger.info("Exit: sendCmdData");
    }

    public void sendCmdMultiData(byte[] dataArray){
        byte[] cmdData = new byte[dataArray.length + 1];
        cmdData[0] = SSD1306_Defines.WITH_MULTI_COMMAND;
        System.arraycopy(dataArray, 0, cmdData,1 , dataArray.length);
        this.i2c.write(cmdData);
    }

    public void sendMultiData(byte[] dataArray){
        byte[] cmdData = new byte[dataArray.length + 1];
        cmdData[0] = SSD1306_Defines.WITH_MULTI_DATA;
        System.arraycopy(dataArray, 0, cmdData,1 , dataArray.length);
        this.i2c.write(cmdData);
    }



    // Set start and end column addresses (COL0 - COL127)
    protected void setColumnAddress(byte start, byte end)
    {
        this.logger.info("Enter: setColumnAddress   "   + start  + "  " + end);
        byte[] cmdData = new byte[3];
        cmdData[0] = SSD1306_Defines.COMMAND_COLUMN_ADDRESS;
        cmdData[1] = start;
        cmdData[2] = end;
        sendCmdMultiData( cmdData);
        this.logger.info("Exit: setColumnAddress");
    }

    // ...

    // Set the start and end pages (PAGE0-PAGE7)
    protected void setPageAddress(byte start, byte end)
    {
        this.logger.info("Enter: setPageAddress   "   + start  + "  " + end);
        byte[] cmdData = new byte[3];
        cmdData[0] = SSD1306_Defines.COMMAND_SET_PAGE_ADDRESS;
        cmdData[1] = start;
        cmdData[2] = end;
        sendCmdMultiData(  cmdData);
        this.logger.info("Exit: setPageAddress   ");
    }

    // Send a data buffer GDDRAM
    protected void sendBuffer(byte[] buffer, long length)
    {
        this.logger.info("Enter: sendBuffer  ");
        this.i2c.writeRegister(SSD1306_Defines.WITH_MULTI_DATA,buffer);   // TODO  which command
        this.logger.info("Exit: sendBuffer  ");
    }

    private void initialize() {
        this.logger.info("Enter: initialize  ");
        // start of init steps
        this.sendCmd((byte) (SSD1306_Defines.COMMAND_DISPLAY_ON | SSD1306_Defines.DISABLE_DISPLAY));

      // memory mode horz
        this.sendCmdOneData((byte) (0x20), (byte) (0x00));

        //Set COM output scan direction $C0 / $C8 ......
        this.sendCmd((byte) (0xC0));

        //Set start line [$40]
        this.sendCmd((byte) (0x40));

        // missing ???  A1
        this.sendCmd((byte) (0xA1));

        //  missing ??? C0   ----
        this.sendCmd((byte) (0xC0));
        //??? DA  02    -----
        this.sendCmdOneData((byte) (0xDA), (byte) (0x02));


        // missing ??  81 AF
          //Set contrast [$81, $7F]
        this.sendCmdOneData((byte) (0x81), (byte) (0x8f));

        // Normal display   A6    ??? A7   ---+++
        this.sendCmd((byte) (0xA6));

        // Set MUX Ratio [$A8, $3F]
        this.sendCmdOneData((byte) (0xA8), (byte) (0x3F));

        //Set display offset [$D3, $00]
        this.sendCmdOneData((byte) (0xD3), (byte) (0x00));

        //Set Oscillator frequency [$D5, $80]
        // SETDISPLAYCLOCKDIV, 0x80,
        this.sendCmdOneData((byte) (0xD5), (byte) (0x80));

        //Set pre-charge [$D9, $22]
        this.sendCmdOneData((byte) (0xD9), (byte) (0x22));

        //Set divide ratio[$D5, $12]   ??? 80  --+++
        this.sendCmdOneData((byte) (0xD5), (byte) (0x12));

       /// SSD1306_SETVCOMDETECT, 0x20,     ??? only DB   ----   ++++
        this.sendCmdOneData((byte) (0xDB), (byte) (0x20));

        //Enable charge pump [$8D, $14]
        this.sendCmdOneData((byte) (0x8D), (byte) (0x14));

         //SSD1306_DISPLAYALLON_RESUME
        //Resume the display $A4
        this.sendCmd((byte) (0xA4));



      //Turn the display on $AF
        this.sendCmd((byte) (SSD1306_Defines.COMMAND_DISPLAY_ON | SSD1306_Defines.ENABLE_DISPLAY));

        // completion of init steps
        this.logger.info("Exit: initialize  ");
    }
}
