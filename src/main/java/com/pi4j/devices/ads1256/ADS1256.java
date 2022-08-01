/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  ADS1256.java
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

package com.pi4j.devices.ads1256;

import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADS1256 {


    enum MuxValue {
        AIN0,
        AIN1,
        AIN2,
        AIN3,
        AIN4,
        AIN5,
        AIN6,
        AIN7,
        AINCOM
    }

    enum ADS1256_GAIN
    {
        ADS1256_GAIN_1,   			   //= 0,	/* GAIN   1 */
                ADS1256_GAIN_2,		   //	= 1,	/*GAIN   2 */
                ADS1256_GAIN_4,		  //	= 2,	/*GAIN   4 */
                ADS1256_GAIN_8,		  //	= 3,	/*GAIN   8 */
                ADS1256_GAIN_16,		 //	= 4,	/* GAIN  16 */
                ADS1256_GAIN_32,		   //	= 5,	/*GAIN    32 */
                ADS1256_GAIN_64,		//	= 6,	/*GAIN    64 */
    };

    enum ADS1256_DRATE {
        ADS1256_30000SPS,
        ADS1256_15000SPS,
        ADS1256_7500SPS,
        ADS1256_3750SPS,
        ADS1256_2000SPS,
        ADS1256_1000SPS,
        ADS1256_500SPS,
        ADS1256_100SPS,
        ADS1256_60SPS,
        ADS1256_50SPS,
        ADS1256_30SPS,
        ADS1256_25SPS,
        ADS1256_15SPS,
        ADS1256_10SPS,
        ADS1256_5SPS,
        ADS1256_2d5SPS,
        ADS1256_DRATE_MAX
    };


    static final int[] ADS1256_DRATE_E =
    {
            (int) 0xF0,		/*reset the default values  */
            (int) 0xE0,
            (int) 0xD0,
            (int) 0xC0,
            (int) 0xB0,
            (int) 0xA1,
            (int) 0x92,
            (int) 0x82,
            (int) 0x72,
            (int) 0x63,
            (int) 0x53,
            (int) 0x43,
            (int) 0x33,
            (int) 0x20,
            (int) 0x13,
            (int) 0x03
    };

    public ADS1256(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect, int drdyPin, int csPin, int rstPin, int pdwnPin, String ppName, String pnName, Console console, String traceLevel, double vref) throws InterruptedException {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.chipSelect = chipSelect;
        this.spiBus = spiBus;
        this.csPinNum = csPin;
        this.rstPinNum = rstPin;
        this.drdyPinNum = drdyPin;
        this.pdwnPinNum = pdwnPin;
        this.traceLevel = traceLevel;
        this.ppName = ppName;
        this.pnName = pnName;

        this.vref = vref;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + ADS1256.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(ADS1256.class);
        this.init();

    }

    private void init() throws InterruptedException {
        var spiConfig = Spi.newConfigBuilder(pi4j)
                .id("SPI" + this.spiBus + " " + this.chipSelect)
                .name("A/D converter")
                .bus(this.spiBus)
                .chipSelect(this.chipSelect)
                .baud(Spi.DEFAULT_BAUD)
                .mode(SpiMode.MODE_0)
                .provider("pigpio-spi")
                .build();
        this.spi = this.pi4j.create(spiConfig);


        var inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
                .id("DRDY_pin")
                .name("DRDY")
                .address(this.drdyPinNum)
                .provider("pigpio-digital-input");
        try {
            this.drdyGpio = pi4j.create(inputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigIn DRDY failed");
            System.exit(201);
        }
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
        var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
                .id("RST_pin")
                .name("RESET")
                .address(this.rstPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            this.csGpio = pi4j.create(outputConfig3);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut RESET failed");
            System.exit(203);
        }

        var outputConfig4 = DigitalOutput.newConfigBuilder(pi4j)
                .id("PDWN_pin")
                .name("PDWN")
                .address(this.rstPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        try {
            this.pdwnGpio = pi4j.create(outputConfig4);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut PDWN failed");
            System.exit(203);
        }
     }

    public byte displayProgramID() throws InterruptedException {
        // print program title/header
        byte id = 0;
        this.logger.trace(">>> Enter displayProgramID");
        console.title("<-- The Pi4J Project -->", "SPI test program using ADS1256 AtoD Chip");
        this.waitForDrdyLow();
        id = this.readRegData(ADS1256_Declares.REG_STATUS);
        this.logger.trace("<<< Exit displayProgramID  : " + (id >> 4));
        return(id);
    }



    public void ADS1256_ConfigADC(String gain, String  drate) throws InterruptedException {
        this.waitForDrdyLow();
        int buf[] = {0,0,0,0};
        buf[0] = (0<<3) | (1<<2) | (0<<1);
        buf[1] = 0x08;
        buf[2] = (0<<5) | (0<<3) | (this.mapGainString(gain)<<0);
        buf[3] = this.mapDrateString(drate);
        this.csGpio.low();
        this.writeCmd(ADS1256_Declares.WREG | 0);
        this.spi.write(0x03);  // writing 4 bytes data
        this.spi.write(buf[0]);
        this.spi.write(buf[1]);
        this.spi.write(buf[2]);
        this.spi.write(buf[3]);
        this.csGpio.high();
        this.busyWaitMS(1);
    }
    private void showDrdyState(int num) {
        this.logger.trace("showDrdyState : " + num);
        if (this.drdyGpio.state() == DigitalState.HIGH) {
            this.logger.trace("DRDY state HIGH");
        } else if (this.drdyGpio.state() == DigitalState.LOW) {
            this.logger.trace("DRDY state LOW");
        } else {
            this.logger.trace("DRDY state = " + this.drdyGpio.state());
        }
    }

    public void doReset() throws InterruptedException {
        this.logger.trace(">>> Enter doReset");
        if(this.rstGpio != null) {
            this.rstGpio.high();
            this.busyWaitMS(200);
            this.rstGpio.low();
            this.busyWaitMS(200);
            this.rstGpio.high();
        }else{
            this.writeCmd(ADS1256_Declares.RESET);
            this.busyWaitMS(200);
        }
        this.logger.trace("<<< Exit doReset");
    }

    private void writeCmd(int cmd) {
        this.logger.trace(">>> Enter writeCmd");
        this.csGpio.low();
        this.busyWaitMS(2);
        this.spi.write(cmd);
        this.busyWaitMS(2);
        this.csGpio.high();
        this.logger.trace("<<< Exit writeCmd");
    }


    private void writeReg(int reg, int data) {
        this.logger.trace(">>> Enter writeReg");
        this.csGpio.low();
        this.busyWaitMS(2);
        this.spi.write(ADS1256_Declares.WREG | reg);
        this.spi.write(0x00);
        this.spi.write(data);
        this.busyWaitMS(2);
        this.csGpio.high();
        this.logger.trace("<<< Exit writeReg");
    }

    private byte readRegData(int reg) {
        this.logger.trace(">>> Enter readReg");
        byte rval = 0x42;
        this.csGpio.low();
        this.busyWaitMS(2);
        this.spi.write(ADS1256_Declares.RREG | reg);
        this.spi.write(0x00);
        this.busyWaitMS(1);
        rval = this.spi.readByte();
        this.busyWaitMS(2);
        this.csGpio.high();
        this.logger.trace("<<< Exit readReg  : " +  String.format("0X%02x: ", rval));
        return(rval);
    }



    private static void busyWaitMicros(long micros) {
        long waitUntil = System.nanoTime() + (micros * 1000);
        while (waitUntil > System.nanoTime()) {
            ;
        }
    }

    private static void busyWaitMS(long ms) {
        long waitUntil = System.nanoTime() + (ms * 1000000);
        while (waitUntil > System.nanoTime()) {
            ;
        }
    }


    private short mapMuxString(String name) {
        MuxValue muxMap[] = MuxValue.values();
        int posPin = 0xff;
        for (MuxValue col : muxMap) {
            System.out.println(col + " at index "
                    + col.ordinal());
            // Calling ordinal() to find index
            // of color.
            if (col.toString().contentEquals(name)) {
                posPin = col.ordinal();
                this.logger.trace(" pname : " + name + "  No : " + posPin);
                break;
            }
        }
        return (short) (posPin & 0xff);
    }

    private void mapMux(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter mapMux  channel : " + pChannel + "/" + nChannel);


        // create a data buffer and initialize a conversion request payload
        byte muxData[] = new byte[]{(byte) ADS1256_Declares.WREG | ADS1256_Declares.REG_MUX, // 5H/register offset
                (byte) (0b00000000), // number regs to write -1
                (byte) (0b00000000 | (((pChannel & 0xf) << 4)) | nChannel), // channel +, channel AINCOM
        };
        Thread.sleep(2000);
        byte muxValue[] = new byte[4];
        this.writeReg( ADS1256_Declares.REG_MUX,(((pChannel & 0xf) << 4) | nChannel) );
        this.logger.trace("<<< Exit mapMux ");

    }



    public void displayADS1256State(String ppName, String pnName) throws InterruptedException, IOException {

        short pChannel = this.mapMuxString(ppName);
        short nChannel = this.mapMuxString(pnName);
        // allow for user to exit program using CTRL-C
        console.promptForExit();
        this.logger.trace(">>> Enter displayADS1256State  channel  : " + pChannel + "/" + nChannel);
        // continue running program until user exits using CTRL-C
        while (console.isRunning()) {
            read(pChannel, nChannel);
            Thread.sleep(1000);
        }
        console.emptyLine();
        this.logger.trace("<<< Exit displayMCP3008State");
    }

    /**
     * Read data via SPI bus from ADS1256 chip.
     *
     * @throws IOException
     */
    public void read(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter read ");
        // see if chip ready for additional commands
        this.waitForDrdyLow();
        double conversion_value = getConversionValue(pChannel, nChannel);
        this.logger.trace(" |\r");
        this.logger.trace("<<< Exit read");
    }



    private boolean waitForDrdyLow() throws InterruptedException {
        boolean rval = false;
        long i = 0;
        for(i=0;i<4000000;i++){
            if(this.drdyGpio.state()  == DigitalState.LOW) {
                rval = true;
                break;
            }else{
                this.logger.trace("Not desired State");
            }
        }
        if(i >= 4000000){
            this.logger.trace("waitForDrdyLow Time Out ...\r\n");
        }
        //this.logger.trace(" waitForDrdyLow   " + rval);
        return (rval);
    }



    /**
     * Communicate to the ADC chip via SPI to get single-ended conversion value
     * for a specified channel.
     *
     * @param pChannel analog input channel on ADC chip
     * @param nChannel analog input channel on ADC chip
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public double getConversionValue(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter getConversionValue  channel : " + pChannel + "/" + nChannel);


        this.mapMux(pChannel, nChannel);

        this.writeCmd(ADS1256_Declares.SYNC);
        this.writeCmd(ADS1256_Declares.WAKEUP);
        double value = this.doRDATA();

         this.logger.trace("Channel  :" + pChannel + "/" + nChannel + "  value  :" + String.format(" | %06f", value)); // print
        if (this.vref > 0) {
            this.logger.info("A/D read input voltage : " + ((value * this.vref) / 0x40000 + " \n"));
        }


        this.logger.trace("<<< Exit getConversionValue ");
       return value;
    }

private double doRDATA() throws InterruptedException {
    this.logger.trace(">>> Enter doRDATA ");

    long read = 0;
    long buf[] = {0,0,0};

    this.waitForDrdyLow();
    this.busyWaitMS(1);

    this.csGpio.low();
    this.spi.write(ADS1256_Declares.RDATA);
    this.busyWaitMS(1);
    buf[0] = this.spi.readByte();
    buf[1] = this.spi.readByte();
    buf[2] = this.spi.readByte();
    this.csGpio.high();
    read = ((long)buf[0] << 16) & 0x00FF0000;
    read |= ((long)buf[1] << 8) & 0x0000FF00;  /* Pay attention to It is wrong   read |= (buf[1] << 8) */
    read |= (long)buf[2] & 0x000000FF;
    //printf("%d  %d  %d \r\n",buf[0],buf[1],buf[2]);
    if ((read & 0x800000) >0 ) {
        read &= 0xFF000000; // negative value
    }
    this.logger.trace("<<< Exit doRDATA ");
    return (double)read;

}


    private short mapDrateString(String name) {
        ADS1256_DRATE drateMap[] = ADS1256_DRATE.values();
        int posPin = 0xff;
        for (ADS1256_DRATE col : drateMap) {
            System.out.println(col + " at index "
                    + col.ordinal());
            // Calling ordinal() to find index
            // of color.
            if (col.toString().contentEquals(name)) {
                posPin = col.ordinal();
                this.logger.trace(" drate : " + name + "  No : " + ADS1256_DRATE_E[posPin]);
                break;
            }
        }
        return (short) ( ADS1256_DRATE_E[posPin] & 0xff);
    }

    private short mapGainString(String name) {
        ADS1256_GAIN drateMap[] = ADS1256_GAIN.values();
        int posPin = 0xff;
        for (ADS1256_GAIN col : drateMap) {
            System.out.println(col + " at index "
                    + col.ordinal());
            // Calling ordinal() to find index
            // of color.
            if (col.toString().contentEquals(name)) {
                posPin = col.ordinal();
                this.logger.trace(" gain : " + name + "  No : " + posPin);
                break;
            }
        }
        return (short) (posPin & 0xff);
    }

    // SPI device
    //  public SpiDevice spi;

    // ADC channel count
    public short pinCount; // ADS1256=8 move to chip config
    // file
    private Spi spi;
    private Console console;
    private final String traceLevel;
    private final Logger logger;

    private final double vref;
    private final SpiChipSelect chipSelect;
    private final SpiBus spiBus;

    private Context pi4j;
    private String ppName = "";
    private String pnName = "";

    private DigitalInput drdyGpio;
    private int drdyPinNum;   // 17
    private DigitalOutput csGpio;
    private int csPinNum;     //  22

    private DigitalOutput rstGpio;
    private int rstPinNum;     //  18

    private DigitalOutput pdwnGpio;

    private int pdwnPinNum;     //  27

}

