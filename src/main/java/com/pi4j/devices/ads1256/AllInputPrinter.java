

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
        for (int gpioPinIdx = 0; gpioPinIdx < ads.getNumGpioPins(); gpioPinIdx++)
            ads.setGpioDirIn(gpioPinIdx);
    }

    /**
     * Print GPIO states and all single-sided AINx against AINCOM.
     *
     * @throws InterruptedException
     * @since Dec 2, 2024
     */
    public void printADSXXXInputStates() throws InterruptedException {
        System.out.print("GPIO(" + Integer.toBinaryString((ads.getGpioRaw() & 0xFF) + 0x100).substring(1) + ")");
        for (int gpioPinIdx = 0; gpioPinIdx < ads.getNumGpioPins(); gpioPinIdx++)
            System.out.print("    " + gpioPinIdx + ":" + (ads.isGpioHigh(gpioPinIdx) ? "HIGH" : "LOW "));

        System.out.println();

        final DecimalFormat format = new DecimalFormat("0.000");
        for (int ainPinIdx = 0; ainPinIdx < ads.getNumAnalogPins(); ainPinIdx++) {
            String voltageString = "N/A";

            switch (getPrintingUnits()) {
                case TWOS_COMPLEMENT:
                    voltageString = ads.readAnalogOneSided(ainPinIdx) + "";
                    break;
                case NORMALIZED:
                    voltageString = format.format(ads.readAnalogOneSidedNormalized(ainPinIdx));
                    break;
                case VOLTS:
                    voltageString = format.format(ads.readAnalogOneSidedVolts(ainPinIdx)) + "V";
                    break;
                default:
            }//end switch block getPrintingUnits()
            System.out.println("AIN" + ainPinIdx + ": " + voltageString + "                        ");
        }
        System.out.println();

    }//end printADSXXXInputState()

    /**
     * Query the units used for printing A/D converter readings
     *
     * @return
     * @since Dec 2, 2024
     */
    public PrintingUnits getPrintingUnits() {
        return printingUnits;
    }

    /**
     * Set the units used for printing A/D converter readings.
     *
     * @param printingUnits
     * @since Dec 2, 2024
     */
    public void setPrintingUnits(PrintingUnits printingUnits) {
        this.printingUnits = printingUnits;
    }
}//end AllInputPrinter
