/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  AllInputPrinter.java
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

import java.text.DecimalFormat;

public class AllInputPrinter {
    private final ADS125x ads;
    private PrintingUnits printingUnits = PrintingUnits.NORMALIZED;
    
    public static enum PrintingUnits {
	TWOS_COMPLEMENT,
	NORMALIZED,
	VOLTS
    }
    
    AllInputPrinter(ADS125x ads) {
	this.ads = ads;
	
	configureADSXXXForAllInputs();
    }
    
    private void configureADSXXXForAllInputs() {
	for(int gpioPinIdx = 0; gpioPinIdx < ads.getNumGpioPins(); gpioPinIdx++)
	    ads.setGpioDirIn(gpioPinIdx);
    }
    
    /**
     * Print GPIO states and all single-sided AINx against AINCOM.
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    public void printADSXXXInputStates() throws InterruptedException {
	System.out.print("GPIO("+Integer.toBinaryString((ads.getGpioRaw() & 0xFF) + 0x100).substring(1)+")");
	for(int gpioPinIdx = 0; gpioPinIdx < ads.getNumGpioPins(); gpioPinIdx++)
	    System.out.print("    "+gpioPinIdx +":"+ (ads.isGpioHigh(gpioPinIdx)?"HIGH":"LOW ") );
	
	System.out.println();
	
	final DecimalFormat format = new DecimalFormat("0.000");
	for(int ainPinIdx = 0; ainPinIdx < ads.getNumAnalogPins(); ainPinIdx++) {
	    String voltageString = "N/A";
	    
		switch(getPrintingUnits()) {
		case TWOS_COMPLEMENT:
		    voltageString = ads.readAnalogOneSided(ainPinIdx)+"";
		    break;
		case NORMALIZED:
		    voltageString = format.format(ads.readAnalogOneSidedNormalized(ainPinIdx));
		    break;
		case VOLTS:
		    voltageString = format.format(ads.readAnalogOneSidedVolts(ainPinIdx))+"V";
		    break;
		default:
		}//end switch block getPrintingUnits()
	    System.out.println("AIN"+ainPinIdx+": "+voltageString+"                        ");
	}
	System.out.println();
	
    }//end printADSXXXInputState()

    /**
     * Query the units used for printing A/D converter readings
     * @return
     * @since Dec 2, 2024
     */
    public PrintingUnits getPrintingUnits() {
        return printingUnits;
    }

    /**
     * Set the units used for printing A/D converter readings.
     * @param printingUnits
     * @since Dec 2, 2024
     */
    public void setPrintingUnits(PrintingUnits printingUnits) {
        this.printingUnits = printingUnits;
    }
}//end AllInputPrinter
