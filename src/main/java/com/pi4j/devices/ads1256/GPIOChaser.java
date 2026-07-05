

package com.pi4j.devices.ads1256;


/**
 * Sets all GPIO pins to OUT, and turns one GPIO on at a time, in an incrementing sequence.
 *
 * @author Chuck Ritola
 *
 */
public class GPIOChaser {
    private final ADS125x ads;
    private int iterationIdx;

    public GPIOChaser(ADS125x ads) {
        super();
        this.ads = ads;
        for (int i = 0; i < ads.getNumGpioPins(); i++)
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
        for (int i = 0; i < ads.getNumGpioPins(); i++) {
            ads.setGpio(i, (iterationIdx == i));
        }
    }//end updateStateToADS()

    /**
     * Print the chaser's internal state to the console.
     *
     * @since Dec 15, 2024
     */
    public void printState() {
        for (int i = 0; i < ads.getNumGpioPins(); i++)
            System.out.println("GPIO " + i + ": " + (iterationIdx == i ? "HIGH" : "LOW"));
        System.out.println();
    }//end printState()

}//end GPIOChaser
