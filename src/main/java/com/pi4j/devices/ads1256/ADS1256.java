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


    public ADS1256(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect, int drdyPin, int csPin,int rstPin, String ppName, String pnName,  Console console, String traceLevel, double vref) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.chipSelect = chipSelect;
        this.spiBus = spiBus;
        this.csPinNum = csPin;
        this.rstPinNum = rstPin;
        this.drdyPinNum = drdyPin;
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
                .id("SPI" + this.spiBus + " " + this.chipSelect)
                .name("A/D converter")
                .bus(this.spiBus)
                .chipSelect(this.chipSelect)
                // .baud(100000) //Spi.DEFAULT_BAUD)
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
    }

    public void displayProgramID() throws InterruptedException {
        // print program title/header
        this.logger.trace(">>> Enter displayProgramID");
        console.title("<-- The Pi4J Project -->", "SPI test program using ADS1256 AtoD Chip");
        this.csGpio.low();
        busyWaitMicros(1000000);
        this.waitForDrdyLow();
        this.spi.write(ADS1256_Declares.WREG| ADS1256_Declares.REG_STATUS);
        this.spi.write(0);
        busyWaitMicros(1000000);  // 1 ms
        int rtn = this.spi.read();
        this.csGpio.high();
        busyWaitMicros(1000000);
        this.logger.trace("<<< Exit displayProgramID  : "  + (rtn>>4));

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
        this.rstGpio.high();
        busyWaitMS(200);
        this.rstGpio.low();
        busyWaitMS(200);
        this.rstGpio.high();

      /*  // create a data buffer and initialize a conversion request payload
        byte resetData[] = new byte[]{(byte) ADS1256_Declares.RESET
        };
        byte resetValue[] = new byte[1];
        this.csGpio.low();
        busyWaitMicros(1000);
        this.spi.write(resetData, 1);
        busyWaitMicros(1000);
        this.csGpio.high();
        busyWaitMicros(1000);
        */
        this.logger.trace("Exit doReset");
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

    private boolean isDrdyLow() throws InterruptedException {
        boolean rval = false;
        this.logger.trace(">>> Enter isDrdyLow");
        //      this.showDrdyState(1);
        // create a data buffer and initialize a conversion request payload
        byte statusData[] = new byte[]{(byte) ADS1256_Declares.RREG | ADS1256_Declares.REG_STATUS, // 5H/register offset
                (byte) (0b00000000), // number regs to read -1
        };

        byte statusValue[] = new byte[3];
        this.csGpio.low();

        this.spi.write(statusData, 2);
        // delay 50 * tclkin(.00000013)     13 ns  * 50  = 6.50  ms
        this.busyWaitMicros(200);  // 7
        Thread.sleep(2000);
        this.spi.write(0);
        this.busyWaitMicros(200); // 200


        byte conversionValue[] = new byte[3];
        statusValue[0] = this.spi.readByte();

        int bytesRead = 42;
        //int bytesRead =  this.spi.transfer(statusValue,1);
        //todo   int bytesRead = this.spi.read(statusValue, 1);
        this.csGpio.high();

        this.logger.trace("Status read   : " + bytesRead + " data of value :" + statusValue[0]);


        //
        // int flag = statusValue[0] & 0b00000001;


        if (this.drdyGpio.state() == DigitalState.HIGH) {
            this.logger.trace("DRDY state HIGH");
        } else if (this.drdyGpio.state() == DigitalState.LOW) {
            this.logger.trace("DRDY state LOW");
            rval = true;
        } else {
            this.logger.trace("DRDY state = " + this.drdyGpio.state());
        }

        this.logger.trace("<<< Exit isDrdyLow : " + rval);
        return (rval);
    }

    private short mapString(String pName) {
        MuxValue muxMap[] = MuxValue.values();
        int posPin = 0xff;
        for (MuxValue col : muxMap) {
            System.out.println(col + " at index "
                    + col.ordinal());
            // Calling ordinal() to find index
            // of color.
            if (col.toString().contentEquals(pName)) {
                posPin = col.ordinal();
                this.logger.trace(" pname : " + pName + "  No : " + posPin);
            }
        }
        return (short) (posPin & 0xff);
    }

    public void displayADS1256State(String ppName, String pnName) throws InterruptedException, IOException {

        short pChannel = this.mapString(ppName);
        short nChannel = this.mapString(pnName);
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
        if (this.isDrdyLow()) {
            int conversion_value = getConversionValue(pChannel, nChannel);
        } else {
            this.logger.info("Read not possible per DRDY state");
        }
        this.logger.trace(" |\r");
        this.logger.trace("<<< Exit read");
    }

    public int sendStandby() throws IOException, InterruptedException {
        this.logger.trace(">>> Enter sendStanby  ");
        // create a data buffer and initialize a conversion request payload
        byte stbyData[] = new byte[]{(byte) ADS1256_Declares.STANDBY
        };

        byte stbyValue[] = new byte[1];
        this.csGpio.low();

        int bytesWritten = this.spi.write(stbyData, 1);
        Thread.sleep(1000);
        this.csGpio.high();
        this.logger.trace(" <<< Exit sendStandby wrote  " + bytesWritten);
        return (bytesWritten);
    }

    public int sendWakeup() throws IOException, InterruptedException {
        this.logger.trace(">>> Enter sendWakeup  ");
        // create a data buffer and initialize a conversion request payload
        byte wakeData[] = new byte[]{(byte) ADS1256_Declares.WAKEUP
        };

        byte wakeValue[] = new byte[1];
        this.csGpio.low();

        int bytesWritten = this.spi.write(wakeData, 1);
        //       this.busyWaitMicros(200);  // 7
        this.csGpio.high();
        this.logger.trace(" <<< Exit sendWakeup wrote  " + bytesWritten);
        return (bytesWritten);
    }


    public int sendSync() throws IOException, InterruptedException {
        this.logger.trace(">>> Enter sendSync  ");
        // create a data buffer and initialize a conversion request payload
        byte syncData[] = new byte[]{(byte) ADS1256_Declares.SYNC
        };

        byte wakeValue[] = new byte[1];
        this.csGpio.low();

        int bytesWritten = this.spi.write(syncData, 1);
        this.busyWaitMicros(2000);  // 7
        this.csGpio.high();
        this.logger.trace(" <<< Exit sendSync wrote  " + bytesWritten);
        return (bytesWritten);
    }

    private boolean waitForDrdyLow() throws InterruptedException {
        boolean rval = false;
        for (int c = 0; c < 20; c++) {
            if (this.isDrdyLow()) {
                rval = true;
                break;
            } else {
                Thread.sleep(100);
            }
        }
        this.logger.trace(" waitForDrdyLow   " + rval);
        return (rval);
    }

    private boolean waitForDrdyHigh() throws InterruptedException {
        boolean rval = false;
        for (int c = 0; c < 20; c++) {
            if (this.isDrdyLow() == false) {
                rval = true;
                break;
            } else {
                Thread.sleep(100);
            }
        }
        this.logger.trace(" waitForDrdyHigh   " + rval);
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
    public int getConversionValue(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter getConversionValue  channel : " + pChannel+"/" + nChannel);


        // create a data buffer and initialize a conversion request payload
        byte muxData[] = new byte[]{(byte) ADS1256_Declares.WREG| ADS1256_Declares.REG_MUX, // 5H/register offset
                (byte) (0b00000000 ), // number regs to write -1
                (byte) (0b00000000 | (((pChannel & 0xf) << 4))|nChannel ), // channel +, channel AINCOM
        };
        Thread.sleep(2000);
        byte muxValue[] = new byte[4];
        this.csGpio.low();

        int bytesWritten = this.spi.write(muxData[0]);
        bytesWritten = this.spi.write(muxData[1]);
        bytesWritten = this.spi.write(muxData[2]);
        this.csGpio.high();

        this.showDrdyState(3);

        this.sendSync();

        this.showDrdyState(4);

        this.waitForDrdyHigh();
        this.sendWakeup();
        // create a data buffer and initialize a conversion request payload
        byte readData[] = new byte[]{(byte) ADS1256_Declares.RDATA,   // read data command
        };



        this.showDrdyState(5);
        this.csGpio.low();

        this.waitForDrdyLow();

        this.busyWaitMicros(2000); // 200

         this.spi.write(readData[0]);

        this.busyWaitMicros(2000); // 200



        byte conversionValue[] = new byte[3];
        conversionValue[0] = this.spi.readByte();
        conversionValue[1] = this.spi.readByte();
        conversionValue[2] = this.spi.readByte();

        //        int convBytesRead = this.spi.read(conversionValue, 3);
        this.showDrdyState(6);

        // See page 23
        // calculate and return conversion value from result bytes

        int result = (conversionValue[0] << 16) | (conversionValue[1] << 8)  | (conversionValue[2] & 0xff);
        this.logger.trace("Channel  :" + pChannel+"/" + nChannel + "  value  :" + String.format(" | %06x", result)); // print
        if (this.vref > 0) {
            this.logger.info("A/D read input voltage : " + ((result * this.vref) / 0x40000 + " \n"));
        }
        this.csGpio.high();

        this.logger.trace("<<< Exit getConversionValue ");
        this.sendStandby();

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

    private DigitalInput drdyGpio;
    private int drdyPinNum;   // 17
    private DigitalOutput csGpio;
    private int csPinNum;     //  22

    private DigitalOutput rstGpio;
    private int rstPinNum;     //  18




}

