/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  AbstractADS125X.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2024 Pi4J
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

import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.context.Context;
import com.pi4j.devices.base_util.gpio.InputTransitionBarrier;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;

public abstract class AbstractADS125x implements ADS125x {
    private static final boolean DEBUG = false;

    /**
     * See ADS1256App.java help text to explain these parms
     *
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
     * @param sampleRate 
     * @throws InterruptedException
     */
    @SuppressWarnings("exports")
    public AbstractADS125x(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect, boolean reset, int drdyPin, int csPin, int rstPin, boolean crtRstGpio, int pdwnPin, boolean crtPdwnGpio, Console console, String traceLevel, double vref, DataRate sampleRate, boolean useBuffer) throws InterruptedException {
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
        //this.pdwnPinNum = pdwnPin;
        //this.crtPdwnGpio = crtPdwnGpio;
        this.traceLevel = traceLevel;
        this.sampleRate = sampleRate;
        this.useBuffer = useBuffer;

        this.vref = vref;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + AbstractADS125x.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(AbstractADS125x.class);
        this.init();
    }

    /**
     * Creates the SPI and requested GPIOs.
     * Do reset if so requested by app
     * Configure chip Gain and speed.
     *
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
            .provider("linuxfs-spi")
            .build();
        this.spi = this.pi4j.create(spiConfig);

        // required all configs
        var drdyConfig = DigitalInput.newConfigBuilder(pi4j)
            .id("DRDY_pin")
            .name("DRDY")
            .address(this.drdyPinNum)
            .debounce(500L)
            .provider("gpiod-digital-input"); //               .pull(PullResistance.PULL_UP)
        try {
            this.drdyGpio = pi4j.create(drdyConfig);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigIn DRDY failed");
            System.exit(201);
        }
        // required all configs
        var csConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id("CS_pin")
            .name("CS")
            .address(this.csPinNum)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH)
            .provider("gpiod-digital-output");
        try {
            this.csGpio = pi4j.create(csConfig);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut DRDY failed");
            System.exit(202);
        }
        // not always required, see README
        if (this.crtRstGpio) {
            var rstConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("RST_pin")
                .name("RESET")
                .address(this.rstPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("gpiod-digital-output");
            try {
                this.rstGpio = pi4j.create(rstConfig);
            } catch (Exception e) {
                e.printStackTrace();
                console.println("create DigOut RESET failed");
                System.exit(203);
            }
        } else {
            if(DEBUG) this.logger.trace("RESET Gpio not requested/created");
        }

        // not always required, see README
        /*if (this.crtPdwnGpio) {
            var pdwnConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("PDWN_pin")
                .name("PDWN")
                .address(this.pdwnPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("gpiod-digital-output");
            try {
                this.pdwnGpio = pi4j.create(pdwnConfig);
            } catch (Exception e) {
                e.printStackTrace();
                console.println("create DigOut PDWN failed");
                System.exit(203);
            }
        } else
            if(DEBUG) this.logger.trace("PDWN Gpio not requested/created");
        */
        if (this.resetChip)
            this.reset();
        
        //race condition: wait for drdy to settle
        this.writeCmd(ADS125x.STANDBY);
        sleepMS(250);
        drdyBarrier = new InputTransitionBarrier(drdyGpio);
        drdyBarrier.setThrowableHandler((t)->{t.printStackTrace(); System.exit(1);});
        drdyBarrier.setAnticipateTogglesConflictHandler((barrier, numAnticipated, expectedInitialState)->{
            drdyBarrier.anticipateToggles(!expectedInitialState,  numAnticipated+1);//TODO: Recursion limit?
            return true;
            });
	drdyBarrier.setExhaustedAnticipatedToggleHandler((barrier,state)->{
	    barrier.setNumAnticipatedToggles(barrier.getNumAnticipatedToggles()-1);
	    return true;});
        drdyBarrier.setAwaitToggleConflictHandler((barrier,state,state1)->{return false;});
        
        configADC(Gain.GAIN_1, sampleRate, true, useBuffer);
        
        drdyBarrier.anticipateToggles(false, 1);
        this.writeCmd(ADS125x.STANDBY);
    }//end init()
    
    /**
     * Validate chip ID = 3
     *
     * @return null if the chip ID is valid (0x03), else the received ID value
     * @throws InterruptedException
     */
    @Override
    public Integer validateChipID() throws InterruptedException {
        // print program title/header
        int id = 0;
        if(DEBUG) this.logger.trace(">>> Enter displayProgramID");
        console.title("<-- The Pi4J Project -->", "SPI test program using ADS1256 AtoD Chip");
        id = (readRegData(ADS125x.REG_STATUS) >> 4) & 0x0F;
        if (id != ADS125x.CHIP_ID) {
            if(DEBUG) logger.trace("Incorrect chip ID : " + id);
            return id;
        }
        if(DEBUG) this.logger.trace("<<< Exit displayProgramID  : " + id);
        return null;
    }//end validateChipID()

    /**
     * Get the currently-set VREF volt property used for calculating A/D voltage readings.
     * This property defaults to 2.5V if not set.
     * @return VREF in volts
     * @since Dec 2, 2024
     */
    @Override
    public double getVRefVolts() {
        return vref;
    }

    /**
     * Set the VREF property to be used in calculating A/D voltage readings.
     * This property defaults to 2.5V if not set.
     * @param vRefVolts
     * @since Dec 2, 2024
     */
    @Override
    public void setVRefVolts(double vRefVolts) {
        this.vref = vRefVolts;
    }
    
    @Override
    public float getDigitalFilterSettlingTimeMillis() {
	return dataRate.getSettlingTimeMS();
    }

    /**
     * Set chip Gain and speed
     *
     * @param gain
     * @param drate
     * @throws InterruptedException
     */
    @Override
    public void configADC(Gain gain, DataRate drate, boolean autoCalibrate, boolean useBuffer) throws InterruptedException {
	//When configured, DRDY will by HIGH then LOW.
	drdyBarrier.anticipateToggles(true, 1);
	this.pgaGain  = gain;
	this.dataRate = drate;
	this.autoCalibrate = autoCalibrate;
	// set ACAL bit
	workBuffer4[0] = (byte)((autoCalibrate?ADS125x.STATUS_ACAL:0x0) | (useBuffer?0x01:0x00)); //STATUS_REG
	workBuffer4[1] = (byte) (0b00000000 | (((0 & 0xf) << 4)) | 8);         // MUX_REG   initial: AIN0/AINCOM
	workBuffer4[2] = (byte)(pgaGain.ordinal()&0x07);       // ADCON_REG CLK/SENSOR off
	workBuffer4[3] = dataRate.asRegisterValue();
	// DRATE_REG
	this.csGpio.low();
	sleepMS(2);
	this.spi.write(ADS125x.WREG | 0);
	sleepMS(2);
	this.spi.write(0x03);  // writing 4 bytes data
	sleepMS(2);
	spi.write(workBuffer4);
	this.csGpio.high();
	sleepMS(1);
	try {drdyBarrier.awaitAnticipatedToggles(false,250);}
	catch(TimeoutException e) {}
    }//end configADC(...)

    /**
     * Debug usage, display state of DRDY InputGpio
     */
    @SuppressWarnings("unused")
    private void showDrdyState() {
        if(DEBUG) this.logger.trace("showDrdyState ");
        if (this.drdyGpio.state().isHigh()) {
            if(DEBUG) this.logger.trace("DRDY state HIGH");
        } else if (this.drdyGpio.state().isLow()) {
            if(DEBUG) this.logger.trace("DRDY state LOW");
        } else {
            if(DEBUG) this.logger.trace("DRDY state = " + this.drdyGpio.state());
        }
    }//end showDrdyState()
    
    /**
     * If the RESET Gpio was configured reset dhip via Gpio, else use commands
     *
     * @throws InterruptedException
     */
    public void reset() throws InterruptedException {
        if(DEBUG) this.logger.trace(">>> Enter reset");
        if (this.rstGpio != null) {
            this.rstGpio.high();
            sleepMS(200);
            Thread.sleep(200);
            this.rstGpio.low();
            Thread.sleep(200);
            this.rstGpio.high();
        } else {
            this.writeCmd(ADS125x.RESET);
            Thread.sleep(200);
        }
        if(DEBUG) this.logger.trace("<<< Exit reset");
    }

    private void writeCmd(int cmd) {
        if(DEBUG) this.logger.trace(">>> Enter writeCmd  cmd " + cmd);
        this.csGpio.low();
        sleepMS(1);
        this.spi.write(cmd);
        sleepMS(1);
        this.csGpio.high();
        if(DEBUG) this.logger.trace("<<< Exit writeCmd");
    }


    private void writeReg(int reg, int data) {
        if(DEBUG) this.logger.trace(">>> Enter writeReg  reg :  " + reg + " data " + String.format("0X%02x: ", data & 0xff));
        this.csGpio.low();
        sleepMS(1);
        workBuffer3[0] = (byte) (ADS125x.WREG | reg);
        workBuffer3[1] = 0x00;
        workBuffer3[2] = (byte) data;
        spi.write(workBuffer3);
        sleepMS(1);
        spi.write(ADS125x.STANDBY);
        sleepMS(1);
        this.csGpio.high();
        
        if(DEBUG) this.logger.trace("<<< Exit writeReg");
    }//end writeReg

    private byte readRegData(int reg) {
	try{drdyBarrier.awaitAnticipatedToggles(true, 250);}
	catch(InterruptedException | TimeoutException e) {}
        if(DEBUG) this.logger.trace(">>> Enter readReg register# : " + reg);
        byte rval = 42;
        this.csGpio.low();
        sleepMS(1);
        workBuffer2[0] = (byte) (ADS125x.RREG | reg);
        workBuffer2[1] = 0x00;
        this.spi.write(workBuffer2);
        sleepMS(1);
        rval = this.spi.readByte();
        sleepMS(1);
        spi.write(ADS125x.STANDBY);
        sleepMS(1);
        this.csGpio.high();
        sleepMS(1);  // let chip quiet
        if(DEBUG) this.logger.trace("<<< Exit readReg  : " + String.format("0X%02x: ", rval & 0xff));
        return (byte) (rval & 0xff);
    }
    
    private static void sleepMS(long ms) {
	try {Thread.sleep(ms);}
	catch(InterruptedException e) {}
    }

    /**
     * Name pairs pChannel and nChannel written into chips MUX register
     *
     * @param pChannel
     * @param nChannel
     * @throws IOException
     * @throws InterruptedException
     */
    private void mapMux(short pChannel, short nChannel) throws IOException, InterruptedException {
        if(DEBUG) this.logger.trace(">>> Enter mapMux  channel : " + pChannel + "/" + nChannel);
        this.writeReg(ADS125x.REG_MUX, (((pChannel & 0xf) << 4) | nChannel));
        if(DEBUG) this.logger.trace("<<< Exit mapMux ");
    }//end mapMux(...)
    
    /**
     * Get a one-sided analog reading against AINCOM
     * @param analogInputNumber The AINx pin number from which to read the analog value.
     * @return Analog value, non-normalized.
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    @Override
    public int readAnalogOneSided(int analogInputNumber) throws InterruptedException {
	if(analogInputNumber < 0 || analogInputNumber >= getNumAnalogPins())
	    throw new IllegalArgumentException();
	return readAnalogDifferential(analogInputNumber, 8);
    }
    
    /**
     * Get a one-sided analog reading against AINCOM. See datasheet pg. 23.
     * @param analogInputNumber The AINx pin number from which to read the analog value.
     * @return Analog value, normalized to the range [-1,1]
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    @Override
    public double readAnalogOneSidedNormalized(int analogInputNumber) throws InterruptedException {
	if(analogInputNumber < 0 || analogInputNumber >= getNumAnalogPins())
	    throw new IllegalArgumentException();
	return readAnalogDifferentialNormalized(analogInputNumber, 8);
    }
    
    /**
     * Get a one-sided analog reading against AINCOM in volts. See datasheet pg. 23.<br>
     * This assumes that the setVRef() property is set to its proper value, else 5V reference is used.
     * @param analogInputNumber The AINx pin number from which to read the analog value.
     * @return Analog value, normalized to the range [-1,1]
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    @Override
    public double readAnalogOneSidedVolts(int analogInputNumber) throws InterruptedException {
	if(analogInputNumber < 0 || analogInputNumber >= getNumAnalogPins())
	    throw new IllegalArgumentException("Analog input pin number out of range of [0,"+(getNumAnalogPins()-1)+"]. Got "+analogInputNumber);
	return readAnalogDifferentialVolts(analogInputNumber, 8);
    }
    
    @Override
    public int readAnalogDifferential(int analogPositiveInputNumber, int analogNegativeInputNumber) throws InterruptedException {
	return getConversionValue((short)analogPositiveInputNumber, (short)analogNegativeInputNumber);
    }
    
    @Override
    public double readAnalogDifferentialNormalized(int analogPositiveInputNumber, int analogNegativeInputNumber) throws InterruptedException {
	return read((short)analogPositiveInputNumber, (short)analogNegativeInputNumber) / (pgaGain.getGainScalar() * 8.38861E6);
    }
    
    @Override
    public double readAnalogDifferentialVolts(int analogPositiveInputNumber, int negativeInputNumber) throws InterruptedException {
	return (2*getVRefVolts()) * read((short)analogPositiveInputNumber, (short)negativeInputNumber) / (pgaGain.getGainScalar() * 8.38861E6);
    }
    
    
    /**
     * Read data via SPI bus from ADS1256 chip.
     *
     * @throws IOException
     */
    public double read(short pChannel, short nChannel) throws IOException, InterruptedException {
        if(DEBUG) this.logger.trace(">>> Enter read ");
        // see if chip ready for additional commands
        double adcValue = this.getConversionValue(pChannel, nChannel);

        if(DEBUG) this.logger.trace(" |\r");
        if(DEBUG) this.logger.trace("<<< Exit read");
        return adcValue;
    }//end read(...)

    /**
     * Communicate to the ADC chip via SPI to get balanced ADC value of specified channels
     * Issue SYNC and WAKEUP commands so chip will calculate the
     * digital value for pChannel nChannel
     *
     * @param pChannel analog input channel on ADC chip
     * @param nChannel analog input channel on ADC chip
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public int getConversionValue(short pChannel, short nChannel) throws IOException, InterruptedException {
        if(DEBUG) this.logger.trace(">>> Enter getConversionValue  channel : " + pChannel + "/" + nChannel);
        
        drdyBarrier.anticipateToggles(true, 1);//mapMux goes high->low for drdy
        this.mapMux(pChannel, nChannel);
        if(DEBUG)logger.debug("before getConversionValue.SYNC, drdyState="+drdyBarrier.getLastReceivedState());
        this.writeCmd(ADS125x.SYNC);
        if(DEBUG)logger.debug("before getConversionValue.WAKEUP, drdyState="+drdyBarrier.getLastReceivedState());
        this.writeCmd(ADS125x.WAKEUP);
        try {drdyBarrier.awaitAnticipatedToggles(false, 250);}
        catch(TimeoutException e) {}
        int adcValue = doRDATA();
        if(DEBUG)logger.debug("RDATA complete; enter STANDBY....");
        drdyBarrier.anticipateToggles(false, 1);
        this.writeCmd(ADS125x.STANDBY);
        try {drdyBarrier.awaitAnticipatedToggles(true, 250);}
        catch(TimeoutException e) {}
        
        if(DEBUG) {
            this.logger.debug("Channel  :" + pChannel + "/" + nChannel + "  value  :" + adcValue); //String.format(" | %06f", value)); // print
            if (this.vref > 0) {
        	this.logger.debug("A/D read input voltage : " + ((adcValue * getVRefVolts()) / 0x7fffff + " \n"));
            }
        }//end if(DEBUG)

        if(DEBUG) this.logger.trace("<<< Exit getConversionValue ");
        return adcValue;
    }//end getConversionValue(...)
    

    /**
     * Same as getConversionValue(byte, byte) but with built-in conversion from int parameters.
     * @param positivePin
     * @param negativePin
     * @return
     * @throws InterruptedException
     * @since Dec 18, 2024
     */
    public double getConversionValue(int positivePin, int negativePin) throws InterruptedException {
	return this.getConversionValue((byte)positivePin, (byte)negativePin);
    }

    /**
     * Retrieve RDATA from chip
     *
     * @return 24-bit RADATA value as int32
     * @throws InterruptedException
     */
    private int doRDATA() throws InterruptedException {
        if(DEBUG) this.logger.trace(">>> Enter doRDATA ");

        int adcValue = 0;
        
        sleepMS(1);

        this.csGpio.low();
        sleepMS(1);
        this.spi.write(ADS125x.RDATA);
        sleepMS(1);
        
        spi.read(workBuffer3);
        this.csGpio.high();
        adcValue = (workBuffer3[0] << 16) & 0x00FF0000;
        adcValue |= (workBuffer3[1] << 8) & 0x0000FF00;
        adcValue |= workBuffer3[2] & 0x000000FF;
        adcValue &= 0x00ffffff;
        //printf("%d  %d  %d \r\n",buf[0],buf[1],buf[2]);
        if (adcValue >= 0x800000) {
            adcValue -= 0x1000000; // negative value
        }
        if(DEBUG) this.logger.trace("<<< Exit doRDATA ");
        return adcValue;
    }//end doRDATA()
    
    /**
     * Get the gpio status byte from the I/O register, complete with direction
     * information on the more significant nibble.
     * @return Gpio I/O status byte
     * @since Dec 13, 2024
     */
    @Override
    public byte getGpioRaw() {
	return readRegData(ADS125x.REG_IO);
    }
    
    /**
     * Set the gpio status byte to the I/O register, complete with direction
     * information on the more significant nibble.
     * @since Dec 13, 2024
     */
    @Override
    public void setGpioRaw(byte regVal) {
	writeReg(ADS125x.REG_IO, regVal);
    }
    
    /**
     * Query whether the specified gpio is high
     * @param gpioPinNumber The GPIO pin number (not chip number) to query
     * @return Boolean representing the given gpio pin's logical state
     * @since Dec 13, 2024
     */
    @Override
    public boolean isGpioHigh(int gpioPinNumber) {
	if(DEBUG) this.logger.trace(">>> Enter isGpioHigh pin "+gpioPinNumber);
	if(gpioPinNumber >= getNumGpioPins() || gpioPinNumber < 0)
	    throw new IllegalArgumentException("GPIO pin number out of bounds. Must be in range [0,"+(getNumGpioPins()-1)+"]. Got "+gpioPinNumber);
	final byte regVal = getGpioRaw();
	if(DEBUG) this.logger.trace("<<< Exit isGpioHigh pin "+gpioPinNumber);
	return ADS125x.isGpioHigh(gpioPinNumber, regVal);
    }//end isGPIOHigh

    /**
     * Query the number of usable GPIO pins.
     * @return The Number of GPIO pins of this ADS125x chip.
     * @since Dec 15, 2024
     */
    @Override
    public abstract int getNumGpioPins();
    
    /**
     * Query the number of usable analog input pins
     * @return The number of analog input (AIN) pins of this ADS125x chip.
     * @since Dec 15, 2024
     */
    @Override
    public abstract int getNumAnalogPins();
    
    /**
     * Query if auto-calibrate was enabled when this ADS125x chip was initialized.
     * @return True if auto-calibrate was enabled, else false.
     * @since Dec 15, 2024
     */
    @Override
    public boolean isAutoCalibrateEnabled() {
        return autoCalibrate;
    }

    @SuppressWarnings("exports")
    public DigitalState readGpio(int gpioPinNumber) {
	if(DEBUG) this.logger.trace(">>> Enter readGpio pin  " + gpioPinNumber);
	if(gpioPinNumber >= getNumGpioPins() || gpioPinNumber < 0)
	    throw new IllegalArgumentException("GPIO pin number out of bounds. Must be in range [0,"+(getNumGpioPins()-1)+"]. Got "+gpioPinNumber);
        DigitalState pinState = DigitalState.UNKNOWN;
        byte regVal = readRegData(ADS125x.REG_IO);
        if (ADS125x.isPinInput(gpioPinNumber, regVal)) {
            pinState = this.getPinState(gpioPinNumber, regVal);
        }
        if(DEBUG) this.logger.trace("<<< Exit readGpio  State" + pinState);
        return pinState;
    }//end readGpio(...)

    /**
     * Specify the I/O direction of a given gpio pin to be OUTPUT
     * @param gpioPinNumber The pin number of the GPIO in question (D0, D1 ... Dx)
     * @since Dec 15, 2024
     */
    @Override
    public void setGpioDirOut(int gpioPinNumber) {
	if(gpioPinNumber >= getNumGpioPins() || gpioPinNumber < 0)
	    throw new IllegalArgumentException("GPIO pin number out of bounds. Must be in range [0,"+(getNumGpioPins()-1)+"]. Got "+gpioPinNumber);
        if(DEBUG) this.logger.trace(">>> Enter setGpioDirOut pin  " + gpioPinNumber);
        // if(DEBUG) this.logger.trace("ADCON"  + this.readRegData(ADS125x.REG_ADCON));
        byte regVal = this.readRegData(ADS125x.REG_IO);
        regVal &= ~(0x10 << gpioPinNumber) & 0xff;
        this.writeReg(ADS125x.REG_IO, regVal);
        if(DEBUG) this.logger.trace("<<< Exit setGpioDirOut");
    }//end setGpioDirOut(...)

    /**
     * Specify the I/O direction of a given gpio pin to be OUTPUT
     * @param gpioPinNumber The pin number of the GPIO in question (D0, D1 ... Dx)
     * @since Dec 15, 2024
     */
    @Override
    public void setGpioDirIn(int gpioPinNumber) {
	if(gpioPinNumber >= getNumGpioPins() || gpioPinNumber < 0)
	    throw new IllegalArgumentException("GPIO pin number out of bounds. Must be in range [0,"+(getNumGpioPins()-1)+"]. Got "+gpioPinNumber);
        if(DEBUG) this.logger.trace(">>> Enter setGpioDirIn pin  " + gpioPinNumber);
        byte regVal = this.readRegData(ADS125x.REG_IO);
        regVal |= (0x10 << gpioPinNumber) & 0xff;
        this.writeReg(ADS125x.REG_IO, regVal);
        if(DEBUG) this.logger.trace("<<< Exit setGpioDirIn  State");
    }

    private DigitalState getPinState(int pin, byte registerVal) {
        DigitalState pinState = DigitalState.UNKNOWN;
        if(DEBUG) this.logger.trace(">>> Enter isPinInput pin: " + pin);
        pinState = (registerVal & (0x01 << pin)) == 0?DigitalState.LOW:DigitalState.HIGH;
        if(DEBUG) this.logger.trace(" Exit isPinInput ");
        return pinState;
    }
    
    /**
     * Set the specified gpio pin
     * @param pin
     * @param newState
     * @since Dec 13, 2024
     */
    public void setGpio(int pin, boolean newState) {
	byte regVal = this.readRegData(ADS125x.REG_IO);
        if (ADS125x.isPinOutput(pin, regVal))
            this.setPinState(pin, newState?DigitalState.HIGH:DigitalState.LOW, regVal);
        else if(DEBUG) this.logger.trace("Pin " + pin + " not configured for output");
        
        if(DEBUG) this.logger.trace("<<< Exit setGpio ");
    }//end setGpio(...)

    /**
     * Set the specified gpio pin
     * @param pin The GPIO pin in question (D0, D1 ... Dx)
     * @param newState
     */
    public void setGpio(int pin, @SuppressWarnings("exports") DigitalState newState) {
        if(DEBUG) this.logger.trace(">>> Enter setGpio  pin " + pin + "  state : " + newState);
        byte regVal = this.readRegData(ADS125x.REG_IO);
        if (ADS125x.isPinOutput(pin, regVal)) {
            this.setPinState(pin, newState, regVal);
        } else {
            if(DEBUG) this.logger.trace("Pin " + pin + " not configured for output");
        }
        if(DEBUG) this.logger.trace("<<< Exit setGpio");
    }//end setGpio(...)

    private void setPinState(int pin, DigitalState newState, byte registerVal) {
        if(DEBUG) this.logger.trace(">>> Enter setPinState pin: " + pin + "  state: " + newState);
        if (newState.isHigh()) {
            registerVal |= (1 << pin);
        } else {
            registerVal &= ~(1 << pin);
        }
        this.writeReg(ADS125x.REG_IO, registerVal);
        if(DEBUG) this.logger.trace(" Exit setPinState   ");
    }
    
    // file
    private Spi spi;
    private final Console console;
    private final String traceLevel;
    private final Logger logger;
    
    private double vref = 2.5;
    private final SpiChipSelect chipSelect;
    private final SpiBus spiBus;

    private final Context pi4j;
    private boolean resetChip = false;

    private DigitalInput drdyGpio;
    private final int drdyPinNum;   // 17
    private DigitalOutput csGpio;
    private final int csPinNum;     //  22

    private DigitalOutput rstGpio;
    private final int rstPinNum;     //  18
    private boolean crtRstGpio = false;
    //private DigitalOutput pdwnGpio;
    private final DataRate sampleRate;

    //private final int pdwnPinNum;     //  27
    //private boolean crtPdwnGpio = false;
    private boolean autoCalibrate;
    private final boolean useBuffer;
    
    private Gain pgaGain;
    private DataRate dataRate;
    private final byte [] workBuffer2 = new byte[2];
    private final byte [] workBuffer3 = new byte[3];
    private final byte [] workBuffer4 = new byte[4];
    
    private InputTransitionBarrier drdyBarrier;

}//end ADS125X

