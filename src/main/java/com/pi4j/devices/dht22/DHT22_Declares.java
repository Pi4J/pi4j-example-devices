

package com.pi4j.devices.dht22;

public class DHT22_Declares {


    // MCU/Pi, initiate sensor OP
    protected final static int BEGIN_READINGS_MILLS = 20;
    // these two signal the sensor is prepared to send data
    protected final static int PREPARE_DATA_LOW_PULSE_MICS = 80;
    protected final static int PREPARE_DATA_HIGH_PULSE_MICS = 80;

    // data signals '0 or '1' by pulse length
    protected final static int ZERO_PULSE_MICS = 27;  // zero bit
    protected final static int ONE_PULSE_MICS = 70;   //  one bit


    protected final static int TOTAL_NUM_BITS = 40;
    protected final static int RH_NUM_BITS = 16;
    protected final static int T_NUM_BITS = 16;
    protected final static int CKSUM_NUM_BITS = 8;


}
