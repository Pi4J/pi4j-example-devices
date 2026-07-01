

package com.pi4j.devices.ads1256;

import com.pi4j.context.Context;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.util.Console;

public final class ADS1256 extends AbstractADS125x {

    @SuppressWarnings("exports")
    public ADS1256(Context pi4j, SpiBus spiBus, int chipSelect,
                   boolean reset, int drdyPin, int csPin, int rstPin,
                   boolean crtRstGpio, int pdwnPin, boolean crtPdwnGpio,
                   Console console, String traceLevel, double vref, DataRate sampleRate, boolean useBuffer)
        throws InterruptedException {
        super(pi4j, spiBus, chipSelect, reset, drdyPin, csPin, rstPin, crtRstGpio,
            pdwnPin, crtPdwnGpio, console, traceLevel, vref, sampleRate, useBuffer);
    }

    @Override
    public int getNumGpioPins() {
        return 4;
    }

    @Override
    public int getNumAnalogPins() {
        return 8;
    }
}//end ADS1256

