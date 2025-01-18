/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  GPIOChaser.java
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


/**
 * Sets all GPIO pins to OUT, and turns one GPIO on at a time, in an incrementing sequence.
 * @author Chuck Ritola
 *
 */
public class GPIOChaser {
    private final ADS125x ads;
    private int iterationIdx;

    public GPIOChaser(ADS125x ads) {
	super();
	this.ads = ads;
	for(int i = 0; i < ads.getNumGpioPins(); i++)
	    ads.setGpioDirOut(i);
    }//end constructor
    
    /**
     * Update the states to the ADS125x, print the states to the console, and increment
     * the chaser's internal state without yet updating it to the ADS125x.
     * 
     * @since Dec 15, 2024
     */
    public void updatePrintAndIterate() {
	updateStateToADS();
	printState();
	iterate();
    }

    /**
     * Increment the chaser's internal state without yet updating it to the ADS125x.
     * 
     * @since Dec 15, 2024
     */
    public void iterate() {
	iterationIdx++;
	iterationIdx %= ads.getNumGpioPins();
    }//end iterate()
    
    /**
     * Update the chaser's internal state to the ADS125x.
     * 
     * @since Dec 15, 2024
     */
    public void updateStateToADS() {
	for(int i = 0; i < ads.getNumGpioPins(); i++){
	    ads.setGpio(i, (iterationIdx == i));
	}
    }//end updateStateToADS()

    /**
     * Print the chaser's internal state to the console.
     * 
     * @since Dec 15, 2024
     */
    public void printState() {
	for(int i = 0; i < ads.getNumGpioPins(); i++)
	    System.out.println("GPIO "+i+": "+(iterationIdx == i?"HIGH":"LOW"));
	System.out.println();
    }//end printState()

}//end GPIOChaser
