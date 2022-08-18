/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  DAC8552.java
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

package com.pi4j.devices.dac8552;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DAC8552 {


    public DAC8552(SpiBus spiBus, int csPin, Console console, String traceLevel, Context pi4j) throws InterruptedException {
        super();
        this.spiBus = spiBus;
        this.console = console;
        this.traceLevel = traceLevel;
        this.pi4j = pi4j;
        this.csPinNum = csPin;
        System.setProperty("org.slf4j.simpleLogger.log." + DAC8552.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(DAC8552.class);
        this.init();

    }


    private void writeByte(byte toWrite) {
        this.logger.trace(">>> Enter writeByte  :" + String.format(" | %04x", toWrite));
        byte value[] = new byte[1];
        byte valueToWrite[] = new byte[1];
        valueToWrite[0] = toWrite;
        int bytesRead = this.spi.transfer(valueToWrite, 0, value, 0, 1);
        this.logger.trace("<<< Exit writeByte  ");
    }


    /**
     * Actual spi transaction writing to the DAC.  The DAC syns (CS) line is held low across
     * several spi operations, for this reason the Pi SPI CS pin cannot be used.
     *
     * @param channel
     * @param data
     */
    private void Write_DAC8532(int channel, int data) {
        this.logger.trace(">>> Enter Write_DAC8532  channel : " +  String.format(" | %04x",channel) + "  data  :" + String.format(" | %04x", data));

        this.csGpio.high();
        this.csGpio.low();
        this.writeByte((byte) (channel & 0xff));
        this.writeByte((byte) ((data  >> 8) & 0xff));
        this.writeByte((byte) (data & 0xff));
        this.csGpio.high();
        this.logger.trace("<<< Exit Write_DAC8532 ");
    }

    /******************************************************************************
     function:	Specify a channel output voltage value and vref
     parameter:
     Channel: Channel number,  channel_A and channel_B
     Voltage: Output voltage value,
     Vref -reference voltage (used to calculate register data value
     Info:
     ******************************************************************************/
    public boolean DAC8532_Out_Voltage(String channel, double voltage, double vref) {
        this.logger.trace(">>> Enter DAC8532_Out_Voltage   channel : " + channel + " voltage :" + String.format(" | %04f", voltage) + " vref :" + String.format(" | %04f", vref));
        boolean rval = false;
        int chnlNumber = mapChnl(channel);
        int temp = (int) (voltage * DAC8552_Declares.DAC_Value_MAX / vref);
        if ((temp <= DAC8552_Declares.DAC_Value_MAX) && (temp >= 0.0)) {
            Write_DAC8532(chnlNumber, temp);
            rval = true;
        } else {
            this.logger.error("Invalid Voltage :" + String.format(" | %04f", voltage));
            System.exit(301);
        }
        this.logger.trace("<<< Exit DAC8532_Out_Voltage  completed:  " + rval);
        return (rval);
    }

    /******************************************************************************
     function:	Specify a channel output voltage value
     parameter:
     Channel: Channel number,  channel_A and channel_B
     Voltage: Output voltage value,
     Info:
     ******************************************************************************/
    public boolean DAC8532_Out_Voltage(String channel, double voltage) {
        this.logger.trace(">>> Enter DAC8532_Out_Voltage   channel : " + channel + " voltage :" + String.format(" | %04f", voltage));
        boolean rval = this.DAC8532_Out_Voltage(channel, voltage, this.vref);
        this.logger.trace("<<< Exit DAC8532_Out_Voltage  completed:  " + rval);
        return (rval);
    }

    /**
     *
     * @param channel
     * @return  numeric  value for this channel, used as input data written  to the DAC
     */
    private int mapChnl(String channel) {
        this.logger.trace(">>> Enter mapChnl  : " + channel);
        int rval = 0x00;
        if (channel.contentEquals("channel_A")) {
            rval = DAC8552_Declares.channel_A;
        } else if (channel.contentEquals("channel_B")) {
            rval = DAC8552_Declares.channel_B;
        } else {
            this.logger.error("Invalid channel designation :" + channel);
            System.exit(300);
        }
        this.logger.trace("<<< Exit mapChnl  :"+ String.format(" | %04x", rval));
        return (rval);
    }

    private void init() throws InterruptedException {
        this.logger.trace(">>> Enter init");
        var spiConfig = Spi.newConfigBuilder(this.pi4j)
                .id("SPI" + this.spiBus + "_DAC8552")
                .name("D/A converter")
                .bus(this.spiBus)
                .chipSelect(SpiChipSelect.CS_0)   // not used
                // 30D400
                //     0b001100001101010000000000
                .flags(0b0000000000000011100001L)  // Ux CE not used, MM mode 1
                .baud(976563)
                .mode(SpiMode.MODE_1)
                .provider("pigpio-spi")
                .build();
        this.spi = this.pi4j.create(spiConfig);

        // required all configs
        var outputConfig2 = DigitalOutput.newConfigBuilder(pi4j)
                .id("CS_pin")
                .name("CS")
                .address(this.csPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            this.csGpio = pi4j.create(outputConfig2);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut DRDY failed");
            System.exit(202);
        }
        this.logger.trace("<<< Exit init");

    }


    private Spi spi;
    private Console console;
    private String traceLevel;
    private Logger logger;

    private double vref = DAC8552_Declares.DAC_VREF;
    private double voutput = 0;


    private DigitalOutput csGpio;
    private int csPinNum;

    private Context pi4j;

    private SpiBus spiBus;

}
