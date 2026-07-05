

package com.pi4j.devices.base_util.gpio;


import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;

/**
 * GpioPinCfgData
 * <p>
 * Definitions used by the gpio package
 * </p>
 */

public class GpioPinCfgData {


    public enum Direction {
        in, out, none
    }


    public GpioPinCfgData(int number, Direction direction, DigitalOutput output, DigitalInput input) {

        this.number = number;
        this.output = output;
        this.input = input;
        this.direction = direction;
    }


    Direction direction;
    public int number;
    public DigitalOutput output;
    public DigitalInput input;
}





