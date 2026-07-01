

package com.pi4j.devices.appConfig;

/**
 * <h1>SetProperties</h1>
 * <p>
 * Single class that calls set_properties on the various classes within the appConfig package
 * </p>
 */
public class SetProperties {

    /**
     * main
     * <p>
     * Classes within  the appConfig package have their setProperties method called.
     * <p>
     * PreCond: None
     * </p>
     *
     * <p>
     * PostCond:  Each file created and data written.
     * </p>
     */
    public static void main(String[] args) {
        ChipNameMap cMap = new ChipNameMap();
        cMap.setProperties();

        GpioToApp gMap = new GpioToApp();
        gMap.setProperties();

        PiPinMap piMap = new PiPinMap();
        piMap.setProperties();

    }

}
