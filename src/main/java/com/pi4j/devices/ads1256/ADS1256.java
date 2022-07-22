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


    public ADS1256(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect, String ppName, String pnName, short pinCount, Console console, String traceLevel, double vref) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.chipSelect = chipSelect;
        this.spiBus = spiBus;
        this.pinCount = pinCount;
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

    private void init() {
        var spiConfig = Spi.newConfigBuilder(pi4j)
                .id("SPI" + spiBus + " " + chipSelect)
                .name("A/D converter")
                .bus(spiBus)
                .chipSelect(chipSelect)
                .baud(Spi.DEFAULT_BAUD)
                .mode(SpiMode.MODE_0)
                .provider("pigpio-spi")
                .build();
        this.spi = this.pi4j.create(spiConfig);


    }

    public void displayProgramID() {
        // print program title/header
        this.logger.trace(">>> Enter displayProgramID");
        console.title("<-- The Pi4J Project -->", "SPI test program using ADS1256 AtoD Chip");
        this.logger.trace("<<< Exit displayProgramID");

    }


    public void doReset() throws InterruptedException {
        this.logger.trace(">>> Enter doReset");
        // create a data buffer and initialize a conversion request payload
        byte resetData[] = new byte[]{(byte) ADS1256_Declares.RESET
        };
        byte resetValue[] = new byte[1];
        int bytesRead = this.spi.transfer(resetData, 0, resetValue, 0, 1);
        Thread.sleep(1);
        this.logger.trace("Exit doReset");
    }


    private boolean isDrdyLow() {
        boolean rval = false;
        this.logger.trace(">>> Enter isDrdyLow");
        // create a data buffer and initialize a conversion request payload
        byte statusData[] = new byte[]{(byte) ADS1256_Declares.RREG | (0b00000001), // 5H/register offset
                (byte) (0b00000000), // number regs to read -1
        };

        byte statusValue[] = new byte[1];
        int bytesRead = this.spi.transfer(statusData, 0, statusValue, 0, 2);
        int flag = statusValue[0] & 0b00000001;
        if (flag == 0) {
            rval = true;
        }
        this.logger.trace("<<< Exit isDrdyLow : " + rval);
        return (rval);
    }

    private short mapString(String pName) {

    MuxValue muxMap[] = MuxValue.values();
    int posPin = 0xff;
        for(
    MuxValue col :muxMap)

    {
        System.out.println(col + " at index "
                + col.ordinal());
        // Calling ordinal() to find index
        // of color.
        if (col.toString().contentEquals(pName)) {
            posPin = col.ordinal();
            this.logger.trace(" pname : " + pName + "  No : " + posPin);
        }
    }
        return(short) (posPin & 0xff);
    }

    public void displayADS1256State(String ppName, String pnName) throws InterruptedException, IOException {

        short pChannel  = this.mapString(ppName);
        short nChannel  = this.mapString(pnName);
        // allow for user to exit program using CTRL-C
        console.promptForExit();
       this.logger.trace(">>> Enter displayADS1256State  channel  : " + pChannel+"/" + nChannel);
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
        if(this.isDrdyLow()) {
                int conversion_value = getConversionValue(pChannel, nChannel);
                this.logger.trace("Channel  :" + pChannel+"/" + nChannel + "  value  :" + String.format(" | %04d", conversion_value)); // print
        }else{
            this.logger.info("Read not possible per DRDY state");
        }
        this.logger.trace(" |\r");
        Thread.sleep(1000);
        this.logger.trace("<<< Exit read");

    }

    public int sendStandby() throws IOException, InterruptedException {
        this.logger.trace(">>> Enter sendStanby  ");
        // create a data buffer and initialize a conversion request payload
        byte stbyData[] = new byte[]{(byte) ADS1256_Declares.STANDBY
        };

        byte stbyValue[] = new byte[1];
        int bytesRead = this.spi.transfer(stbyData, 0, stbyValue, 0, 1);
        Thread.sleep(1);

        this.logger.trace(" <<< Exit sendStanby bytesRead  " + bytesRead);
        return(bytesRead);
   }

    public int sendWakeup() throws IOException, InterruptedException {
        this.logger.trace(">>> Enter sendWakeup  ");
        // create a data buffer and initialize a conversion request payload
        byte wakeData[] = new byte[]{(byte) ADS1256_Declares.WAKEUP
        };

        byte wakeValue[] = new byte[1];
        int bytesRead = this.spi.transfer(wakeData, 0, wakeValue, 0, 1);
        Thread.sleep(1);

        this.logger.trace(" <<< Exit sendWakeup bytesRead  " + bytesRead);
        return(bytesRead);
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
    public int getConversionValue(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter getConversionValue  channel : " + pChannel+"/" + nChannel);


        // create a data buffer and initialize a conversion request payload
        byte muxData[] = new byte[]{(byte) ADS1256_Declares.WREG| (0b00000001), // 5H/register offset
                (byte) (0b00000000 ), // number regs to write -1
                (byte) (0b00000000 | (((pChannel & 7) << 4))|nChannel ), // channel +, channel AINCOM
        };

        byte muxValue[] = new byte[4];
        int bytesRead = this.spi.transfer(muxData, 0, muxValue, 0, 4);

        this.sendStandby();

        this.sendWakeup();


        // wait until DRDY is low


        for(int c = 0; c < 20; c++){
            if(this.isDrdyLow()){
                break;
            }else {
                Thread.sleep(10);
            }
        }
        // create a data buffer and initialize a conversion request payload
        byte readData[] = new byte[]{(byte) ADS1256_Declares.RDATA,   // read data command
        };

        byte conversionValue[] = new byte[3];
        int convBytesRead = this.spi.transfer(readData, 0, conversionValue, 0, 1);



        // See page 23
        // calculate and return conversion value from result bytes
        int result = (conversionValue[0] << 16) | (conversionValue[1] << 8)  | (conversionValue[2] & 0xff);
        this.logger.info("Channel : " + pChannel+"/" + nChannel + "   Bytes read : " + bytesRead + "  Value : " + result);
        if (this.vref > 0) {
            this.logger.info("A/D read input voltage : " + ((result * this.vref) / 0x40000 + " \n"));
        }
        this.logger.trace("<<< Exit getConversionValue ");

        return result;
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
    private String ppName="";
    private String pnName="";






}

