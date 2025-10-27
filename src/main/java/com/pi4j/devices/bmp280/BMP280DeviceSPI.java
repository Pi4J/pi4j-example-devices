/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  BMP280DeviceSPI.java
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

package com.pi4j.devices.bmp280;

import com.pi4j.context.Context;
import com.pi4j.io.spi.*;
import com.pi4j.util.Console;
import org.slf4j.LoggerFactory;


/*
SPI operates mode0 or mode1
Register address use the MSB to indicate read (1) or write (0)
Access register 0x7f
    Read access transfer 0xf7
    Write access transfer 0x77

 Write 2 bytes to register 0xF7
 Send  0x77  byte1   0x77 byte2

 Read two bytes from register 0xf7
 Send 0xf7  rcv byte1  rcv byte2


 */
public class BMP280DeviceSPI extends BMP280Device {


    SpiBus spiBus;

    SpiChipSelect chipSlct;

    // local/internal SPI reference for communication with hardware chip
    Spi spi = null;

     // SPI Provider name and unique ID
    /**
     * Constant <code>SPI_PROVIDER_NAME="NAME +  SPI Provider"</code>
     */
    public final String SPI_PROVIDER_NAME = NAME + " BMP280 SPI Provider";
    /**
     * Constant <code>SPI_PROVIDER_ID="ID + -spi"</code>
     */
    public final String SPI_PROVIDER_ID = ID + "-spi";


    public BMP280DeviceSPI(Context pi4j, Console console, SpiBus spiBus, SpiChipSelect chipSlct,  String traceLevel) {
        super(pi4j, console, traceLevel);
        this.pi4j = pi4j;
        this.spiBus = spiBus;
        this.chipSlct = chipSlct;
        this.console = console;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + BMP280DeviceSPI.class.getName(), traceLevel);

        this.logger = LoggerFactory.getLogger(BMP280DeviceSPI.class);
        this.init();
    }

    private void init() {
        this.logger.info(">>> Enter:init ");
        this.createSPIDevice();
        this.logger.info("<<<Exit:init ");
    }

    /**
     * Use the state from the Sensor config object and the state pi4j to create
     * a BMP280 device instance
     */
    private void createSPIDevice() {
        this.logger.info(">>> Enter:createSPIDevice   bus  " + this.spiBus);
        var spiConfig = Spi.newConfigBuilder(this.pi4j)
            .id("SPI" + this.spiBus + "_BMP280")
            .name("D/A converter")
            .bus(this.spiBus)
            .chipSelect(this.chipSlct)
            //1 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
            //b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
            // .flags(0b0000000000000000100000L)  // MODE0, ux GPIO not used for chip select
            .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
            .mode(SpiMode.MODE_0)
            .provider("linuxfs-spi")
            .build();
        this.spi = this.pi4j.create(spiConfig);
        this.logger.info("Exit:createSPIDevice  ");
    }


    /**
     * @param register
     * @return 8bit value read from register
     */
    public int readRegister(int register) {
        this.logger.trace(">>> Enter readRegister   : " + String.format("0X%02x: ", register));
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        byte[] value = new byte[1];
        this.spi.registerRead(data, data.length, value, value.length);
        this.logger.trace("<<< Exit readRegister   : " + String.format("0X%02x: ", value[0]));
        return value[0]; //rval);
    }

    /**
     * @param register register address
     * @param buffer   Buffer to return read data
     * @return count     number bytes read or fail -1
     */
    public int readRegister(int register, byte[] buffer) {
        this.logger.trace(">>> Enter readRegister   : " + String.format("0X%02x: ", register));
        byte[] data = new byte[]{(byte) (0b10000000 | register)};
        this.spi.registerRead(data, data.length, buffer, buffer.length);
        this.logger.trace("<<< Exit readRegister   : " + String.format("0X%02x: ", buffer[0]) + String.format("0X%02x: ", buffer[0]));
        return buffer.length;
    }


    /**
     * @param register register
     * @param data     byte to write
     * @return bytes written, else -1
     */
    public int writeRegister(int register, int data) {
        this.logger.trace(">>> Enter writeRegister   : " + String.format("0X%02x: ", register));
        int rval = 0;
        int byteswritten = -1;
        byte[] buffer = new byte[]{(byte) (0b01111111 & register),
            (byte) data
        };
        byte[] dummy = new byte[2];
        // send read request to BMP chip via SPI channel
        byteswritten = this.spi.write(buffer);

        this.logger.trace("<<< Exit writeRegister wrote : " + byteswritten);
        return (rval);
    }
}

