

package com.pi4j.devices.is31Fl37Matrix;

public class InterruptDetails {
    private final boolean rval;
    private final int counter;

    public InterruptDetails() {
        this.counter = 0;
        this.rval = false;

    }

    public InterruptDetails(boolean rval, int counter) {
        this.counter = counter;
        this.rval = rval;

    }

    public boolean getSuccessVal() {
        return (this.rval);
    }

    public int getCounter() {
        return (this.counter);
    }

}

