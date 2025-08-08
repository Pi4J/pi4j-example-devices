/*
 *    * Copyright (C) 2012 - 2024 Pi4J
 *  * %%
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * -
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J :: EXTENSION
 *  FILENAME      :  ADS1256.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *  %%
 */

package com.pi4j.devices.basic_ads1256;



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

    enum ADS1256_GAIN {
        ADS1256_GAIN_1,               //= 0,	/* GAIN   1 */
        ADS1256_GAIN_2,           //	= 1,	/*GAIN   2 */
        ADS1256_GAIN_4,          //	= 2,	/*GAIN   4 */
        ADS1256_GAIN_8,          //	= 3,	/*GAIN   8 */
        ADS1256_GAIN_16,         //	= 4,	/* GAIN  16 */
        ADS1256_GAIN_32,           //	= 5,	/*GAIN    32 */
        ADS1256_GAIN_64,        //	= 6,	/*GAIN    64 */

    }

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
    }



static final int[] ADS1256_DRATE_E =
    {
        0xF0,		/*reset the default values  */
       0xE0,
       0xD0,
        0xC0,
        0xB0,
        0xA1,
 /*reset the default values  */
        0xE0,
        0xD0,
        0xC0,
        0xB0,
        0xA1,
        0x92,
        0x82,
        0x72,
        0x63,
        0x53,
        0x43,
        0x33,
        0x23,
        0x13,
        0x03
    };

/**
 *   See ADS1256App.java help text to explain these parms
 * @param pi4j
 * @param spiBus
 * @param chipSelect
 * @param reset
 * @param drdyPin
 * @param csPin
 * @param rstPin
 * @param crtRstGpio
 * @param pdwnPin
 * @param crtPdwnGpio
 * @param console
 * @param traceLevel
 * @param vref
 * @throws InterruptedException
 */
  public ADS1256(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect, boolean  reset,  int drdyPin, int csPin, int rstPin, boolean crtRstGpio, int pdwnPin,boolean crtPdwnGpio,  Console console, String traceLevel, double vref) throws InterruptedException {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.chipSelect = chipSelect;
        this.resetChip = reset;
        this.spiBus = spiBus;
        this.csPinNum = csPin;
        this.rstPinNum = rstPin;
        this.crtRstGpio = crtRstGpio;
        this.drdyPinNum = drdyPin;
        this.pdwnPinNum = pdwnPin;
        this.crtPdwnGpio = crtPdwnGpio;
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

    /**
     *  Creates the SPI and requested GPIOs.
     *  Do reset if so requested by app
     *  Configure chip Gain and speed.
     * @throws InterruptedException
     */
    private void init() throws InterruptedException {
        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + this.spiBus + " " + this.chipSelect)
            .name("A/D converter")
            .bus(this.spiBus)
            .chipSelect(this.chipSelect)
            .baud(976563) //Spi.DEFAULT_BAUD)
            .mode(SpiMode.MODE_1)
            .provider("linuxfs-spi")   //linuxfs-spi    SpiFFMProviderImpl
            .build();
        this.spi = this.pi4j.create(spiConfig);

        // required all configs
        var inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
            .id("DRDY_pin")
            .name("DRDY")
            .address(this.drdyPinNum)
            .provider("gpiod-digital-input"); //               .pull(PullResistance.PULL_UP)
        try {
            this.drdyGpio = pi4j.create(inputConfig1);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigIn DRDY failed");
            System.exit(201);
        }
        // required all configs
        var outputConfig2 = DigitalOutput.newConfigBuilder(pi4j)
            .id("CS_pin")
            .name("CS")
            .address(this.csPinNum)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH)
            .provider("gpiod-digital-output");
        try {
            this.csGpio = pi4j.create(outputConfig2);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut DRDY failed");
            System.exit(202);
        }
        // not always required, see README
        if(this.crtRstGpio) {
            var outputConfig3 = DigitalOutput.newConfigBuilder(pi4j)
                .id("RST_pin")
                .name("RESET")
                .address(this.rstPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("gpiod-digital-output");
            try {
                this.rstGpio = pi4j.create(outputConfig3);
            } catch (Exception e) {
                e.printStackTrace();
                console.println("create DigOut RESET failed");
                System.exit(203);
            }
        }else{
            this.logger.trace("RESET Gpio not requested/created");
        }

        // not always required, see README
        if(this.crtPdwnGpio) {
            var outputConfig4 = DigitalOutput.newConfigBuilder(pi4j)
                .id("PDWN_pin")
                .name("PDWN")
                .address(this.pdwnPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("gpiod-digital-output");
            try {
                this.pdwnGpio = pi4j.create(outputConfig4);
            } catch (Exception e) {
                e.printStackTrace();
                console.println("create DigOut PDWN failed");
                System.exit(203);
            }
        }else{
            this.logger.trace("PDWN Gpio not requested/created");
        }


        if(this.resetChip){
            this.doReset();
        }


        this.ADS1256_ConfigADC("ADS1256_GAIN_1", "ADS1256_10SPS");


    }

    /**
     * Validate chip ID = 3, if not program will exist
     * @return chip ID
     * @throws InterruptedException
     */
    public int validateChipID() throws InterruptedException {
        // print program title/header
        int id = 0;
        this.logger.trace(">>> Enter displayProgramID");
        console.title("<-- The Pi4J Project -->", "SPI test program using ADS1256 AtoD Chip");
        this.waitForDrdyLow();
        id = this.readRegData(ADS1256_Declares.REG_STATUS) >> 4;
        if (id != ADS1256_Declares.CHIP_ID){
            console.println("Incorrect chip ID : " + id);
            System.exit(301);
        }
        this.logger.trace("<<< Exit displayProgramID  : " + id);
        return(id);
    }


    /**
     *   Set chip Gain and speed
     * @param gain
     * @param drate
     * @throws InterruptedException
     */
    private void ADS1256_ConfigADC(String gain, String  drate) throws InterruptedException {
        this.waitForDrdyLow();
        byte status = this.readRegData(ADS1256_Declares.REG_STATUS);
        int buf[] = {0,0,0,0};
        // preserve DRDY bit state and set ACAL bit
        buf[0] = (status & ADS1256_Declares.STATUS_DRDY_MASK)|ADS1256_Declares.STATUS_ACAL;                               //STATUS_REG
        buf[1] =  (byte) (0b00000000 | (((0 & 0xf) << 4)) | 8);         // MUX_REG   initial: AIN0/AINCOM
        buf[2] = (0<<5) | (0<<3) | (this.mapGainString(gain)<<0);       // ADCON_REG CLK/SENSOR off
        buf[3] = this.mapDrateString(drate);                            // DRATE_REG
        this.csGpio.low();
        this.busyWaitMS(2);
        this.spi.write(ADS1256_Declares.WREG | 0);
        this.busyWaitMS(2);
        this.spi.write(0x03);  // writing 4 bytes data
        this.busyWaitMS(2);
        this.spi.write(buf[0]);
        this.busyWaitMS(2);
        this.spi.write(buf[1]);
        this.busyWaitMS(2);
        this.spi.write(buf[2]);
        this.busyWaitMS(2);
        this.spi.write(buf[3]);
        this.busyWaitMS(2);
        this.csGpio.high();
        this.busyWaitMS(1);
    }

    /**
     * Debug usage, display state of DRDY InputGpio
     */
    private void showDrdyState() {
        this.logger.trace("showDrdyState " );
        if (this.drdyGpio.state() == DigitalState.HIGH) {
            this.logger.trace("DRDY state HIGH");
        } else if (this.drdyGpio.state() == DigitalState.LOW) {
            this.logger.trace("DRDY state LOW");
        } else {
            this.logger.trace("DRDY state = " + this.drdyGpio.state());
        }
    }

    /**
     * If the RESET Gpio was configured reset dhip via Gpio, else use commands
     * @throws InterruptedException
     */
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
        this.logger.trace(">>> Enter writeCmd  cmd " + cmd);
        this.csGpio.low();
        this.busyWaitMS(2);
        this.spi.write(cmd);
        this.busyWaitMS(2);
        this.csGpio.high();
        this.logger.trace("<<< Exit writeCmd");
    }


    private void writeReg(int reg, int data) {
        this.logger.trace(">>> Enter writeReg  reg :  " + reg  + " data " +  String.format("0X%02x: ", data&0xff));
        this.csGpio.low();
        this.busyWaitMS(2);
        byte buf[] = {0,0,0};
        buf[0] = (byte) (ADS1256_Declares.WREG | reg);
        buf[1] =  0x00;
        buf[2] = (byte) data;
        this.spi.write(buf);

        this.busyWaitMS(2);
        this.csGpio.high();
        this.logger.trace("<<< Exit writeReg");
    }

    private byte readRegData(int reg) {
        this.logger.trace(">>> Enter readReg register# : "  + reg);
        byte rval = 42;
        this.csGpio.low();
        this.busyWaitMS(2);
        byte buf[] = {0,0,0};
        buf[0] = (byte) (ADS1256_Declares.RREG | reg);
        buf[1] =  0x00;
        this.spi.write(buf);
        this.busyWaitMS(2);
        rval = this.spi.readByte();
        this.busyWaitMS(2);
        this.csGpio.high();
        this.busyWaitMS(200);  // let chip quiet
        this.logger.trace("<<< Exit readReg  : " +  String.format("0X%02x: ", rval&0xff));
        return (byte) (rval & 0xff);
    }

    private static void busyWaitNano(long nanos) {
        long waitUntil = System.nanoTime() + (nanos);
        while (waitUntil > System.nanoTime()) {
            ;
        }
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

    /**
     * Name(s) supplied by app. These are mapped to integer values
     * @param name
     * @return
     */
    private short mapMuxString(String name) {
        MuxValue muxMap[] = MuxValue.values();
        int posPin = 0xff;
        for (MuxValue col : muxMap) {
            // Calling ordinal() to find index
            // of pin name
            if (col.toString().contentEquals(name)) {
                posPin = col.ordinal();
                this.logger.trace(" pname : " + name + "  No : " + posPin);
                break;
            }
        }
        return (short) (posPin & 0xff);
    }

    /**
     * Name pairs pChannel and nChannel written into chips MUX register
     * @param pChannel
     * @param nChannel
     * @throws IOException
     * @throws InterruptedException
     */
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


    /**
     * One-shot get the RDATA for ppName and pnName, then return the value
     * @param ppName
     * @param pnName
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public double getADS1256State(String ppName, String pnName) throws InterruptedException, IOException {

        short pChannel = this.mapMuxString(ppName);
        short nChannel = this.mapMuxString(pnName);
        // allow for user to exit program using CTRL-C
        this.logger.trace(">>> Enter getADS1256State  channel  : " + pChannel + "/" + nChannel);
        // continue running program until user exits using CTRL-C
        double rval =  read(pChannel, nChannel);
        this.logger.trace("<<< Exit getADS1256State: channel  :" + pChannel + "/" + nChannel + "  value  :" + rval);
        return(rval);
    }

    /**
     * Will repeatedly get the RDATA for ppName and pnName, until user ctrl-C
     * @param ppName
     * @param pnName
     * @throws InterruptedException
     * @throws IOException
     */
    public void displayADS1256State(String ppName, String pnName) throws InterruptedException, IOException {

        short pChannel = this.mapMuxString(ppName);
        short nChannel = this.mapMuxString(pnName);
        // allow for user to exit program using CTRL-C
        this.console.promptForExit();
        this.logger.trace(">>> Enter displayADS1256State  channel  : " + pChannel + "/" + nChannel);
        // continue running program until user exits using CTRL-C
        while (this.console.isRunning()) {
            read(pChannel, nChannel);
            Thread.sleep(1000);
        }
        console.emptyLine();
        this.logger.trace("<<< Exit displayADS1256State");
    }

    /**
     * Read data via SPI bus from ADS1256 chip.
     *
     * @throws IOException
     */
    public double read(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter read ");
        // see if chip ready for additional commands
        this.waitForDrdyLow();
        double conversion_value = this.getConversionValue(pChannel, nChannel);

        this.logger.trace(" |\r");
        this.logger.trace("<<< Exit read");
        return(conversion_value);
    }


    /**
     * Polling for controlled number of times testing for
     * DRDY Gpio being DigitalState.LOW, return true, else false
     * @return
     * @throws InterruptedException
     */
    private boolean waitForDrdyLow() throws InterruptedException {
        boolean rval = false;
        long i = 0;
        for(i=0;i<4000000;i++){
            if(this.drdyGpio.state()  == DigitalState.LOW) {
                rval = true;
                break;
            }else{
                this.logger.trace("Not desired State counter "  + i);
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
     * Issue SYNC and WAKEUP commands so chip will calculate the
     * digital value for pChannel nChannel
     *
     * @param pChannel analog input channel on ADC chip
     * @param nChannel analog input channel on ADC chip
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public int getConversionValue(short pChannel, short nChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter getConversionValue  channel : " + pChannel + "/" + nChannel);


        this.mapMux(pChannel, nChannel);
        this.busyWaitMS(2);

        this.writeCmd(ADS1256_Declares.SYNC);
        this.busyWaitMS(2);
        this.writeCmd(ADS1256_Declares.WAKEUP);
        this.busyWaitMS(2);
        int value = this.doRDATA();
        this.busyWaitMS(2);

        this.logger.info("Channel  :" + pChannel + "/" + nChannel + "  value  :" + value ); //String.format(" | %06f", value)); // print
        if (this.vref > 0) {
            this.logger.info("A/D read input voltage : " + ((value * this.vref) / 0x7fffff + " \n"));
        }

        this.logger.trace("<<< Exit getConversionValue ");
        return value;
    }

    /**
     * REtrieve RDATA from chip
     * @return
     * @throws InterruptedException
     */
    private int doRDATA() throws InterruptedException {
        this.logger.trace(">>> Enter doRDATA ");

        int read = 0;
        int buf[] = {0,0,0};

        this.waitForDrdyLow();
        this.busyWaitMS(1);

        this.csGpio.low();
        this.busyWaitMS(2);
        this.spi.write(ADS1256_Declares.RDATA);
        this.busyWaitMS(1);
        buf[0] = this.spi.readByte();
        buf[1] = this.spi.readByte();
        buf[2] = this.spi.readByte();
        this.csGpio.high();
        read = (buf[0] << 16) & 0x00FF0000;
        read |= (buf[1] << 8) & 0x0000FF00;
        read |= buf[2] & 0x000000FF;
        read &= 0x00ffffff;
        //printf("%d  %d  %d \r\n",buf[0],buf[1],buf[2]);
        if (read >= 0x800000) {
            read -=  0x1000000; // negative value
        }
        this.logger.trace("<<< Exit doRDATA ");
        return read;

    }


    private short mapDrateString(String name) {
        ADS1256_DRATE drateMap[] = ADS1256_DRATE.values();
        int posPin = 0xff;
        for (ADS1256_DRATE col : drateMap) {
            // Calling ordinal() to find index
            // of drate.
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
            // Calling ordinal() to find index
            // of Gain name.
            if (col.toString().contentEquals(name)) {
                posPin = col.ordinal();
                this.logger.trace(" gain : " + name + "  No : " + posPin);
                break;
            }
        }
        return (short) (posPin & 0xff);
    }

    public  DigitalState readGpio(int pin){
        this.logger.trace(">>> Enter readGpio pin  "  + pin);
        DigitalState rval = DigitalState.UNKNOWN;
        byte regVal = this.readRegData(ADS1256_Declares.REG_IO);
        if(this.isPinInput(pin, regVal)){
            rval = this.getPinState(pin, regVal);
        }
        this.logger.trace("<<< Exit readGpio  State"  + rval);
        return (rval);
    }
    public  boolean setGpioDirOut(int pin){
        this.logger.trace(">>> Enter setGpioDirOut pin  "  + pin);
        boolean rval = false;
        // this.logger.trace("ADCON"  + this.readRegData(ADS1256_Declares.REG_ADCON));
        byte regVal = this.readRegData(ADS1256_Declares.REG_IO);
        regVal &=  ~(0x10<< pin) & 0xff;
        this.writeReg(ADS1256_Declares.REG_IO, regVal);
        rval = true;
        this.logger.trace("<<< Exit setGpioDirOut  "  + rval);
        return (rval);
    }
    public  boolean setGpioDirIn(int pin){
        this.logger.trace(">>> Enter setGpioDirIn pin  "  + pin);
        boolean rval = false;
        byte regVal = this.readRegData(ADS1256_Declares.REG_IO);
        regVal |= (0x10<< pin) & 0xff;
        this.writeReg(ADS1256_Declares.REG_IO, regVal);
        this.logger.trace("<<< Exit setGpioDirIn  State"  + rval);
        rval = true;
        return (rval);
    }
    private DigitalState getPinState(int pin, byte registerVal){
        DigitalState rval = DigitalState.UNKNOWN;
        this.logger.trace(">>> Enter isPinInput pin: "  +pin);
        if((registerVal & (0x01 << pin)) == 0){
            rval = DigitalState.LOW;
        }else{
            rval = DigitalState.HIGH;
        }
        this.logger.trace(" Exit isPinInput ");
        return(rval);
    }
    public  boolean setGpio(int pin, DigitalState newState){
        this.logger.trace(">>> Enter setGpio  pin "  + pin + "  state : "+ newState);
        boolean rval = false;
        byte regVal = this.readRegData(ADS1256_Declares.REG_IO);
        if(this.isPinOutput(pin, regVal)){
            rval = this.setPinState(pin, newState, regVal);
        }else {
            this.logger.trace("Pin " + pin + " not configured for output");
        }
        this.logger.trace("<<< Exit setGpio "  + rval);
        return (rval);
    }
    private boolean setPinState(int pin, DigitalState newState, byte registerVal){
        boolean rval = false;
        this.logger.trace(">>> Enter setPinState pin: "  +pin  +  "  state: " + newState);
        if(newState == DigitalState.HIGH){
            registerVal |= (1<< pin);
            rval = true;
        }else{
            registerVal &= ~(1<< pin);
            rval = true;
        }
        this.writeReg(ADS1256_Declares.REG_IO, registerVal);
        this.logger.trace(" Exit setPinState   " + rval);
        return(rval);
    }
    private boolean isPinInput(int pin, byte registerVal){
        boolean rval = false;
        this.logger.trace(">>> Enter isPinInput pin: "  +pin);
        if((registerVal & (0x10 << pin)) >0){
            rval = true;
        }
        this.logger.trace(" Exit isPinInput "  + rval);
        return(rval);
    }
    private boolean isPinOutput(int pin, byte registerVal){
        boolean rval = false;
        this.logger.trace(">>> Enter isPinOutput pin: "  +pin);
        if((registerVal & (0x10 << pin)) == 0){
            rval = true;
        }
        this.logger.trace(" Exit isPinOutput "  + rval);
        return(rval);
    }
    // SPI device
    //  public SpiDevice spi;

    // file
    private Spi spi;
    private Console console;
    private final String traceLevel;
    private final Logger logger;

    private double vref = 2.5;
    private final SpiChipSelect chipSelect;
    private final SpiBus spiBus;

    private Context pi4j;
    private String ppName = "";
    private String pnName = "";
    private boolean resetChip = false;

    private DigitalInput drdyGpio;
    private int drdyPinNum;   // 17
    private DigitalOutput csGpio;
    private int csPinNum;     //  22

    private DigitalOutput rstGpio;
    private int rstPinNum;     //  18
    private boolean crtRstGpio = false;
    private DigitalOutput pdwnGpio;

    private int pdwnPinNum;     //  27
    private boolean crtPdwnGpio = false;
}