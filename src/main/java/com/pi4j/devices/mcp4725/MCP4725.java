/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  MCP4725.java
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

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCP4725 {


    /**
     * @param pi4j         instantiated Context
     * @param bus          pi bus number
     * @param address      devices address
     * @param registerData INT, 12 LSB bits are the DAC data
     * @param traceLevel   "trace", "debug", "info", "warn", "error" or "off"
     * @param vref
     */
    public MCP4725(Context pi4j, int bus, int address, int registerData, String traceLevel, double vref) {
        super();
        this.bus = bus;
        this.address = address;
        this.pi4j = pi4j;
        this.registerData = registerData;
        this.traceLevel = traceLevel;
        this.vref = vref;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + MCP4725.class.getName(), this.traceLevel);


        this.logger = LoggerFactory.getLogger(MCP4725.class);
        this.device = this.createI2cDevice(bus, address);
        this.init(bus, address);

    }

    /**
     * @param bus     Pi bus number
     * @param address device address
     * @return Instantiate I2C device
     */
    I2C createI2cDevice(int bus, int address) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        this.logger.trace("Enter: createI2cDevice MCP4725 DAC " + id + "" + name);
        I2C rval = null;
        var i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
                .bus(bus)
                .device(address)
                .id("MCP4725_DAC  " + id + " " + name)
                .name(name)
                .provider("linuxfs-i2c")
                .build();
        rval = this.pi4j.create(i2cDeviceConfig);
        this.logger.trace("Exit: createI2cDevice MCP4725 DAC " + id + "" + name);
        return (rval);

    }

    /**
     * @param bus     devices I2C Pi bus
     * @param address devices address
     *                Validate via registers that this is VL53L0X chip.
     *                Initialize the chip for TOF operations
     */
    void init(int bus, int address) {
        this.logger.trace("Enter:init   bus  " + bus + "  address " + address);
        this.logger.trace("Exit: init  ");

    }


    /**
     * Write reset command into chip
     * Result: chip POR and EEPROM loaded
     */
    void resetChip() {
        this.logger.trace("Enter: resetChip ");
        I2C genCallDevice = this.createI2cDevice(bus, 0x00);
        genCallDevice.write(MCP4725_Declares._MCP4725_GEN_CALL_RESET_CMD);
        this.sleepMS(4);
        this.logger.trace("Exit: resetChip ");
    }


    boolean setOutputEEPROM(int twelveBitData) {
        boolean rval = false;
        String binaryString = Integer.toBinaryString(this.registerData & 0xff);
        String withLeadingZeros = String.format("0b%12s", binaryString).replace(' ', '0');
        this.logger.trace(">>> Enter: setOutputEEPROM  data :  " + withLeadingZeros + "   " + this.registerData);
        if (this.chipIdle()) {
            byte data[] = new byte[MCP4725_Declares._MCP4725_SET_EEPROM_SIZE];
            data[0] = (byte) (data[0] | MCP4725_Declares._MCP4725_WRITE_CMD_DAC_EEPROM | MCP4725_Declares._MCP4725_PD_MODE_NORMAL);
            data[1] = (byte) ((this.registerData & 0x0ff0) >> 4);
            data[2] = (byte) ((this.registerData & 0x000f) << 4);
            data[3] = data[0];
            data[4] = data[1];
            data[5] = data[2];
            this.device.write(data);
            rval = true;
        } else {
            this.logger.info("setOutputEEPROM not possible, chip BSY  ");
        }
        this.logger.trace("<<< Exit: setOutputEEPROM  ");
        return (rval);
    }

    boolean setOutputFast(int twelveBitData) {
        boolean rval = false;
        String binaryString = Integer.toBinaryString(this.registerData & 0xff);
        String withLeadingZeros = String.format("0b%12s", binaryString).replace(' ', '0');
        this.logger.trace(">>> Enter: setOutputFast  data :  " + withLeadingZeros + "   " + this.registerData);
        if (this.chipIdle()) {
            byte data[] = new byte[MCP4725_Declares._MCP4725_SET_FAST_SIZE];
            data[0] = (byte) (data[0] | MCP4725_Declares._MCP4725_WRITE_CMD_FAST | MCP4725_Declares._MCP4725_PD_MODE_NORMAL);
            data[0] = (byte) (data[0] | (byte) ((this.registerData & 0x0f00) >> 8));
            data[1] = (byte) (this.registerData & 0x00ff);
            data[2] = data[0];
            data[3] = data[1];
            this.device.write(data);
            rval = true;
        } else {
            this.logger.info("setOutputFast not possible, chip BSY  ");
        }
        this.logger.trace("<<< Exit: setOutputFast  ");
        return (rval);
    }


    boolean chipIdle() {
        this.logger.trace(">>> Enter: chipIdle  ");
        boolean rval = false;
        int[] data;
        data = this.readBuffer(MCP4725_Declares._MCP4725_CHIP_READ_SIZE);
        if (MCP4725_Declares._MCP4725_CHIP_READ_SIZE == data.length) {
            if ((data[0] & MCP4725_Declares._MCP4725_READ_CMD_IS_COMPLT) > 0) {
                rval = true;
            }
        } else {
            this.logger.trace("read failed  rc =  " + data.length);
        }
        this.logger.trace("<<< Exit: chipIdle  ");
        return (rval);
    }

    void dumpChip() {
        this.logger.trace(">>> Enter: dumpChip ");
        int data[];
        // allow time for a possible EEPROM update to complete
        if (this.chipIdle() == false) {
            this.logger.trace("Wait for EEPROM to complete and test COMPLETED one more time ");
            this.sleepMS(55);
        }

        if (this.chipIdle()) {
            data = this.readBuffer(MCP4725_Declares._MCP4725_CHIP_READ_SIZE);
            String info = " RAW chip data  \n";
            for (int i = 0; i < data.length; i++){
                info = info + String.format("0x%08x ", data[i])  +"  ";
            }
            // possibly class was instantiated with registerData of 0. If so, we need to
            // prime this state for possible dump procedure
            if(this.registerData == 0 ){
                this.logger.trace("Prime registerData  state");
                this.registerData = ((data[1] & 0xff)<<4) + ((data[2]&0xf0) >> 4);
            }
            info = info + "\n";
            this.logger.info(info);
            this.prettyPrint(data, data.length);
        } else {
            this.logger.info("dumpChip not possible, chip BSY  ");
        }
        this.logger.trace("<<< Exit : dumpChip ");
    }

    void prettyPrint(int[] data, int bytesData) {
        this.logger.info(">>> Enter : prettyPrint bytesData  : " + bytesData);
        String outP = "";
        if (bytesData == MCP4725_Declares._MCP4725_CHIP_READ_SIZE) {
            outP = outP + "\n First byte DAC  \n";
            if ((data[0] & MCP4725_Declares._MCP4725_READ_CMD_RDY_BSY_MSK) > 0) {
                outP = outP + "     chip completed state \n ";
            } else {
                outP = outP + "     chip Incomplete state \n";
            }
            outP = outP + "     PD1: " + (data[0] & MCP4725_Declares._MCP4725_DAC_PD1_MODE_MASK)
             + "    PD0:  " + (data[0] & MCP4725_Declares._MCP4725_DAC_PD0_MODE_MASK) + " \n";

            String binaryString = Integer.toBinaryString(data[1] & 0xff);
            String withLeadingZeros = String.format("%8s", binaryString).replace(' ', '0');
            outP = outP + " Second byte DAC  D11...D4 :  "+ withLeadingZeros + " \n";

            String binaryString2 = Integer.toBinaryString((data[2] & 0xf0) >> 4);
            String withLeadingZeros2 = String.format("%4s", binaryString2).replace(' ', '0');
            outP = outP + " Third byte DAC D3...D0  :  " + withLeadingZeros2 + " \n";

            int a1 = ((data[2] & 0xf0) >> 4);
            int b1 =  ((data[1] & 0xff) << 4);
            int c1 = a1 + b1;
            outP = outP + "       12bit DAC :" +  String.format("%04x ",  c1)   + " \n";
            if(this.vref > 0){
                outP = outP +  "       Calculated DAC output voltage : "+  ((this.vref*this.registerData)/4096 + " \n");
            }

            outP = outP + " First byte EEPROM  \n";
            outP = outP + "     PD1: " + (data[3] & MCP4725_Declares._MCP4725_EEPROM_PD1_MODE_MASK)
                    + "    PD0:  " + (data[3] & MCP4725_Declares._MCP4725_EEPROM_PD0_MODE_MASK) + " \n";


            String binaryString3 = Integer.toBinaryString(data[3] & 0x0f);
            String withLeadingZeros3 = String.format("%4s", binaryString3).replace(' ', '0');
            outP = outP + "       Second byte EEPROM D11...D8  :  " + withLeadingZeros3 + " \n";
            String binaryString4 = Integer.toBinaryString(data[4] & 0xff);
            String withLeadingZeros4 = String.format("%8s", binaryString4).replace(' ', '0');
            outP = outP + "       Third byte EEPROM D7...D0  :  " + withLeadingZeros4 + " \n";
            int a = (data[4] & 0xff);
            int b = ((data[3] & 0x0f)<<8);
            int c = a + b;
            outP = outP + "       12bit EEPROM : " + String.format("%04x ",  c) + "\n" ;
            if(this.vref > 0){
                outP = outP +  "       Calculated EEPROM output voltage : "+  ((this.vref * c)/4096 + " \n");
            }

            this.logger.info(outP);
        }
        this.logger.info("<<< Exit : prettyPrint ");
    }




    /**
     * @param mSecs Time to sleep this thread
     */
    void sleepMS(long mSecs) {
        this.logger.trace("Enter: sleepMS   time  " + mSecs);
        try {
            Thread.sleep(mSecs, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.logger.error("Sleep failed");
            System.exit(100);
        }
        this.logger.trace("Exit: sleepMS   ");
    }


    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int[] readBuffer(int readLen) {
        this.logger.trace(">>> Enter: readBuffer ");
        int[] data = new int[readLen];
        byte[] readData = new byte[readLen];
        int rc = this.device.read(readData);
        for (int i = 0; i < readLen; i++) {
            data[i] = (readData[i] & 0xff);
        }
        this.logger.trace("<<< Exit: readBuffer ");
        return (data);
    }






    private final Context pi4j;
    private final int bus;
    private final int address;
    private I2C device;
    private final Logger logger;
    private final String traceLevel;

    private int registerData;

    private final double vref;

}

