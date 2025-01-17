/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  ADS125x.java
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

public interface ADS125x {
    
    public enum MuxValue {
        AIN0,
        AIN1,
        AIN2,
        AIN3,
        AIN4,
        AIN5,
        AIN6,
        AIN7,
        AINCOM;
	
	public short channelNumber() {
	    return (short)(ordinal() & 0xff);
	}
    }//end MuxValue

    public enum Gain {
        GAIN_1(1),               //= 0,	/* GAIN   1 */
        GAIN_2(2),           //	= 1,	/*GAIN   2 */
        GAIN_4(4),          //	= 2,	/*GAIN   4 */
        GAIN_8(8),          //	= 3,	/*GAIN   8 */
        GAIN_16(16),         //	= 4,	/* GAIN  16 */
        GAIN_32(32),           //	= 5,	/*GAIN    32 */
        GAIN_64(64);      //	= 6,	/*GAIN    64 */
        
        private Gain(int gainScalar) {
            this.gainScalar = gainScalar;
        };
        
        private final int gainScalar;
        public int getGainScalar() {return gainScalar;}
    }//end GAIN

    public enum DataRate {
        SPS_30000(30_000,0xF0,.21f),
        SPS_15000(15_000,0xE0,.25f),
        SPS_7500(7500,0xD0,.31f),
        SPS_3750(3750,0xC0,.44f),
        SPS_2000(2000,0xB0,.68f),
        SPS_1000(1000,0xA1,1.18f),
        SPS_500(500,0x92,2.18f),
        SPS_100(100,0x82,10.18f),
        SPS_60(60,0x72,16.84f),
        SPS_50(50,0x63,20.18f),
        SPS_30(30,0x53,33.51f),
        SPS_25(25,0x43,40.18f),
        SPS_15(15,0x33,66.84f),
        SPS_10(10,0x23,100.18f),
        SPS_5(5,0x13,200.18f),
        SPS_2d5(2.5f,0x03,400.18f),
        DRATE_MAX(0,0x0,0f);
        
        private DataRate(float dataRateHertz, int registerValue, float settlingTimeMS) {
            this.registerAddress = (byte)(registerValue&0xFF);
            this.dataRateSPS = dataRateHertz;
            this.settlingTimeMS = settlingTimeMS;
        }
        
        private final byte registerAddress;
        private final float dataRateSPS, settlingTimeMS;
	public byte asRegisterValue() {return registerAddress;}
	public float getDataRateSPS() {return dataRateSPS;}
	public float getSettlingTimeMS() {return settlingTimeMS;}
    }//end DRATE


    static final int[] DRATE_E =
        {
            0xF0,        /*reset the default values  */
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


    public final static int WAKEUP = 0b00000000;   // wakeup/exist standby
    public final static int RDATA = 0b00000001;   // read conversion data
    public final static int RDDATC = 0b00000011;  // read conv data continuously
    public final static int SDATAC = 0b00001111;   // stop read continuously
    public final static int RREG = 0b00010000; //  read register(s)
    public final static int WREG = 0b01010000; // write register(s)
    public final static int SELFCAL = 0b11110000; // offset/gain self calibration
    public final static int SELFOCAL = 0b11110001; // offset self calibration
    public final static int SELFGCAL = 0b11110010;  // gain self calibration
    public final static int SYSOCAL = 0b11110011; //system offset calibration
    public final static int SYSGCAL = 0b11110100;  // system gain calibration
    public final static int SYNC = 0b11111100;  // sync A/D converstion
    public final static int STANDBY = 0b11111101;  // begin standby
    public final static int RESET = 0b11111110; // reset chip
    public final static int WAKEUPb = 0b11111111;   // wakeup/exist standby


    public final static int GPIO_0_IS_IN = 0b00010000; // cfg for input
    public final static int GPIO_1_IS_IN = 0b00100000; // cfg for input
    public final static int GPIO_2_IS_IN = 0b01000000; // cfg for input
    public final static int GPIO_3_IS_IN = 0b10000000; // cfg for input


    public final static int GPIO_0_MASK = 0b00000001; // bit mask
    public final static int GPIO_1_MASK = 0b00000010; // bit mask
    public final static int GPIO_2_MASK = 0b00000100; // bit mask
    public final static int GPIO_3_MASK = 0b00001000; // bit mask


    public final static int STATUS_ACAL = 0b00000100;  // enable auto calibrate
    public final static int STATUS_DRDY_MASK = 0b00000001;  // Mask off DRDY status bit
    public final static int REG_STATUS = 0x00;
    public final static int REG_MUX = 0x01;
    public final static int REG_ADCON = 0x02;
    public final static int REG_DRATE = 0x03;
    public final static int REG_IO = 0x04;
    public final static int REG_OFC0 = 0x05;
    public final static int REG_OFC1 = 0x06;
    public final static int REG_OFC2 = 0x07;
    public final static int REG_FSC0 = 0x08;
    public final static int REG_FSC1 = 0x09;
    public final static int REG_FSC2 = 0x0A;


    public final static int CHIP_ID = 0x03;
    
    /**
     * Helper method to determine if a specified GPIO pin is high in the specified
     * GPIO register byte value.
     * @param gpioPinNumber
     * @param regVal
     * @return true if the GPIO pin is HIGH, else false.
     * @since Dec 18, 2024
     */
    public static boolean isGpioHigh(int gpioPinNumber, byte regVal) {
	if(isPinInput(gpioPinNumber, regVal))
	    return (regVal & (0x01 << gpioPinNumber)) != 0;
	else
	    throw new IllegalStateException("Attempted to read GPIO pin "+gpioPinNumber+", which is set as output.");
    }//end isGpioHigh()
    
    /**
     * Helper method to determine if a specified GPIO pin is high in the specified
     * GPIO register byte value.
     * @param gpioPinNumber The PGIO input number (not the chip pin number)
     * @param regVal Register value byte obtained from something like readRaw(...)
     * @return true if pin is input, else false
     * @since Dec 18, 2024
     */
    public static boolean isPinInput(int gpioPinNumber, byte regVal) {
        boolean pinIsInput = false;
        pinIsInput = (regVal & (0x10 << gpioPinNumber)) > 0;
        return pinIsInput;
    }

    /**
     * Helper method to determine if a specified GPIO pin is high in the specified
     * GPIO register byte value.
     * @param gpioPinNumber The PGIO input number (not the chip pin number)
     * @param regVal Register value byte obtained from something like readRaw(...)
     * @return true if pin is output, else false
     * @since Dec 18, 2024
     */
    public static boolean isPinOutput(int gpioPinNumber, byte regVal) {
        boolean pinIsOutput = false;
        pinIsOutput = (regVal & (0x10 << gpioPinNumber)) == 0;
        return (pinIsOutput);
    }

    /**
     * Validate chip ID = 3
     *
     * @return null if the chip ID is valid (0x03), else the received ID value
     * @throws InterruptedException
     */
    Integer validateChipID() throws InterruptedException;

    /**
     * Get the currently-set VREF volt property used for calculating A/D voltage readings.
     * This property defaults to 2.5V if not set.
     * @return VREF in volts
     * @since Dec 2, 2024
     */
    double getVRefVolts();

    /**
     * Set the VREF property to be used in calculating A/D voltage readings.
     * This property defaults to 2.5V if not set.
     * @param vRefVolts
     * @since Dec 2, 2024
     */
    void setVRefVolts(double vRefVolts);

    /**
     * Query "time required for a step change
	on the analog inputs to propagate through the filter)" in milliseconds.
     * @return
     * @since Dec 18, 2024
     */
    float getDigitalFilterSettlingTimeMillis();

    /**
     * Set chip Gain and speed
     *
     * @param gain
     * @param drate
     * @throws InterruptedException
     */
    void configADC(Gain gain, DataRate drate,
	    boolean autoCalibrate, boolean useBuffer)
	    throws InterruptedException;//end configADC(...)

    /**
     * Get a one-sided analog reading against AINCOM
     * @param analogInputNumber The AINx pin number from which to read the analog value.
     * @return Analog value, non-normalized.
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    int readAnalogOneSided(int analogInputNumber) throws InterruptedException;

    /**
     * Get a one-sided analog reading against AINCOM. See datasheet pg. 23.
     * @param analogInputNumber The AINx pin number from which to read the analog value.
     * @return Analog value, normalized to the range [-1,1]
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    double readAnalogOneSidedNormalized(int analogInputNumber)
	    throws InterruptedException;

    /**
     * Get a one-sided analog reading against AINCOM in volts. See datasheet pg. 23.<br>
     * This assumes that the setVRef() property is set to its proper value, else 5V reference is used.
     * @param analogInputNumber The AINx pin number from which to read the analog value.
     * @return Analog value, normalized to the range [-1,1]
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    double readAnalogOneSidedVolts(int analogInputNumber)
	    throws InterruptedException;

    int readAnalogDifferential(int analogPositiveInputNumber,
	    int analogNegativeInputNumber) throws InterruptedException;

    double readAnalogDifferentialNormalized(int analogPositiveInputNumber,
	    int analogNegativeInputNumber) throws InterruptedException;

    double readAnalogDifferentialVolts(int analogPositiveInputNumber,
	    int negativeInputNumber) throws InterruptedException;

    /**
     * Set the specified gpio pin
     * @param pin
     * @param newState
     * @since Dec 13, 2024
     */
    public void setGpio(int pin, boolean newState);
    
    /**
     * Set the gpio status byte to the I/O register, complete with direction
     * information on the more significant nibble.
     * @since Dec 13, 2024
     */
    public void setGpioRaw(byte regVal);
    
    /**
     * Get the gpio status byte from the I/O register, complete with direction
     * information on the more significant nibble.
     * @return Gpio I/O status byte
     * @since Dec 13, 2024
     */
    byte getGpioRaw();

    /**
     * Query whether the specified gpio is high
     * @param gpioPinNumber The GPIO pin number (not chip number) to query
     * @return Boolean representing the given gpio pin's logical state
     * @since Dec 13, 2024
     */
    boolean isGpioHigh(int gpioPinNumber);//end isGPIOHigh

    /**
     * Query the number of usable GPIO pins.
     * @return The Number of GPIO pins of this ADS125x chip.
     * @since Dec 15, 2024
     */
    int getNumGpioPins();

    /**
     * Query the number of usable analog input pins
     * @return The number of analog input (AIN) pins of this ADS125x chip.
     * @since Dec 15, 2024
     */
    int getNumAnalogPins();

    /**
     * Query if auto-calibrate was enabled when this ADS125x chip was initialized.
     * @return True if auto-calibrate was enabled, else false.
     * @since Dec 15, 2024
     */
    boolean isAutoCalibrateEnabled();

    /**
     * Specify the I/O direction of a given gpio pin to be OUTPUT
     * @param gpioPinNumber The pin number of the GPIO in question (D0, D1 ... Dx)
     * @since Dec 15, 2024
     */
    void setGpioDirOut(int gpioPinNumber);//end setGpioDirOut(...)

    /**
     * Specify the I/O direction of a given gpio pin to be OUTPUT
     * @param gpioPinNumber The pin number of the GPIO in question (D0, D1 ... Dx)
     * @since Dec 15, 2024
     */
    void setGpioDirIn(int gpioPinNumber);

}//end ADS125x