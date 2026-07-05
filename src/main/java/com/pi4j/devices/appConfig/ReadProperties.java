

package com.pi4j.devices.appConfig;

/**
 * <h1>ReadProperties</h1>
 * <p>
 * Single class that calls read_properties on the various
 * classes within the appConfig package
 * </p>
 */
public class ReadProperties {

    /**
     * CTOR
     */
    public ReadProperties() {
    }

    /**
     * Work   Print the values in all three properties.
     */
    public void work() {
        ChipNameMap cMap = new ChipNameMap();
        String chips = cMap.readProperties();

        GpioToApp gMap = new GpioToApp();
        String pins = gMap.readProperties();

        PiPinMap piMap = new PiPinMap();
        String pi = piMap.readProperties();

        System.out.println(" chip map : " + chips);

        //////////////////////////
        System.out.println(" pin map : " + pins);

        //////////////////////////
        System.out.println(" pi map : " + pi);


    }

    /**
     * main
     * <p>
     * Classes within  the appConfig package have their readProperties method called.
     * <p>
     * PreCond: None
     * </p>
     *
     * <p>
     * PostCond:  Each file read and printed to the screen.
     * </p>
     */
    public static void main(String[] args) {

        ReadProperties read = new ReadProperties();
        read.work();
    }

}
