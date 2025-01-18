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

import java.util.Arrays;

/**
 * Wraps an ADS125x object for measurement error mitigation by performing an odd number of analog or GPIO reads (3 unless specified)
 * For analog values, the median value is returned. For digital values, result is determined by majority vote.
 * @author Chuck Ritola
 *
 */
public class VotingADS125x implements ADS125x {
    private final ADS125x delegate;
    private final int numIterations;
    
    public VotingADS125x(ADS125x delegate) {
	this(delegate, 3);
    } 
    
    public VotingADS125x(ADS125x delegate, int readIterations) {
	super();
	if(readIterations % 2 != 1)
	    throw new IllegalArgumentException("readIterations must be odd number. Got "+readIterations);
	this.delegate = delegate;
	this.numIterations = readIterations;
    }

    public Integer validateChipID() throws InterruptedException {
	return delegate.validateChipID();
    }

    public double getVRefVolts() {
	return delegate.getVRefVolts();
    }

    public void setVRefVolts(double vRefVolts) {
	delegate.setVRefVolts(vRefVolts);
    }

    public float getDigitalFilterSettlingTimeMillis() {
	return delegate.getDigitalFilterSettlingTimeMillis();
    }

    public void configADC(Gain gain, DataRate drate,
	    boolean autoCalibrate, boolean useBuffer)
	    throws InterruptedException {
	delegate.configADC(gain, drate, autoCalibrate, useBuffer);
    }

    public int readAnalogOneSided(int analogInputNumber)
	    throws InterruptedException {
	final int numIterations = this.numIterations;
	final ADS125x delegate = this.delegate;
	int [] values = new int[numIterations];
	for(int i = 0 ; i < numIterations; i++)
	    values[i] = delegate.readAnalogOneSided(analogInputNumber);
	Arrays.sort(values);
	return values[numIterations/2];
    }

    public double readAnalogOneSidedNormalized(int analogInputNumber)
	    throws InterruptedException {
	final int numIterations = this.numIterations;
	double [] values = new double[numIterations];
	final ADS125x delegate = this.delegate;
	for(int i = 0 ; i < numIterations; i++)
	    values[i] = delegate.readAnalogOneSidedNormalized(analogInputNumber);
	Arrays.sort(values);
	return values[numIterations/2];
    }

    public double readAnalogOneSidedVolts(int analogInputNumber)
	    throws InterruptedException {
	final int numIterations = this.numIterations;
	double [] values = new double[numIterations];
	final ADS125x delegate = this.delegate;
	for(int i = 0 ; i < numIterations; i++)
	    values[i] = delegate.readAnalogOneSidedVolts(analogInputNumber);
	Arrays.sort(values);
	return values[numIterations/2];
    }

    public int readAnalogDifferential(int analogPositiveInputNumber,
	    int analogNegativeInputNumber) throws InterruptedException {
	final int numIterations = this.numIterations;
	final ADS125x delegate = this.delegate;
	int [] values = new int[numIterations];
	for(int i = 0 ; i < numIterations; i++)
	    values[i] = delegate.readAnalogDifferential(analogPositiveInputNumber, analogNegativeInputNumber);
	Arrays.sort(values);
	return values[numIterations/2];
    }

    public double readAnalogDifferentialNormalized(
	    int analogPositiveInputNumber, int analogNegativeInputNumber)
	    throws InterruptedException {
	final int numIterations = this.numIterations;
	final ADS125x delegate = this.delegate;
	double [] values = new double[numIterations];
	for(int i = 0 ; i < numIterations; i++)
	    values[i] = delegate.readAnalogDifferential(analogPositiveInputNumber, analogNegativeInputNumber);
	Arrays.sort(values);
	return values[numIterations/2];
    }

    public double readAnalogDifferentialVolts(int analogPositiveInputNumber,
	    int analogNegativeInputNumber) throws InterruptedException {
	final int numIterations = this.numIterations;
	final ADS125x delegate = this.delegate;
	double [] values = new double[numIterations];
	for(int i = 0 ; i < numIterations; i++)
	    values[i] = delegate.readAnalogDifferentialVolts(analogPositiveInputNumber, analogNegativeInputNumber);
	Arrays.sort(values);
	return values[numIterations/2];
    }

    public byte getGpioRaw() {
	final int numIterations = this.numIterations;
	final ADS125x delegate = this.delegate;
	final double [] values = new double[8];
	for(int i = 0 ; i < numIterations; i++) {
	    byte raw = delegate.getGpioRaw();
	    for(int j = 0 ; j < 8; j++)
		values[j] += ((raw >> j)&0x01);
	}
	byte result = 0x0;
	for(int j = 0 ; j < 8; j++) {
	    result += (((int)Math.rint(values[j] / (double)numIterations)) << j)&0xFF;
	}
	return result;
    }

    public boolean isGpioHigh(int gpioPinNumber) {
	final int numIterations = this.numIterations;
	final ADS125x delegate = this.delegate;
	double value = 0;
	for(int i = 0 ; i < numIterations; i++)
	    value += delegate.isGpioHigh(gpioPinNumber)?1:0;
	return (int)Math.rint(value / (double)numIterations) == 1;
    }

    public int getNumGpioPins() {
	return delegate.getNumGpioPins();
    }

    public int getNumAnalogPins() {
	return delegate.getNumAnalogPins();
    }

    public boolean isAutoCalibrateEnabled() {
	return delegate.isAutoCalibrateEnabled();
    }

    public void setGpioDirOut(int gpioPinNumber) {
	delegate.setGpioDirOut(gpioPinNumber);
    }

    public void setGpioDirIn(int gpioPinNumber) {
	delegate.setGpioDirIn(gpioPinNumber);
    }

    @Override
    public void setGpio(int pin, boolean newState) {
	delegate.setGpio(pin, newState);
    }

    @Override
    public void setGpioRaw(byte regVal) {
	delegate.setGpioRaw(regVal);
    }
    
}//end VotingADS125x
