package com.pi4j.examples.ioexpander.output;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.expander.ConfigurableIoExpander;
import com.pi4j.drivers.io.expander.OutputExpander;
import com.pi4j.drivers.io.expander.mcp23017.Mcp23017Driver;
import com.pi4j.drivers.io.expander.pcf8574.Pcf8574OutputDriver;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Delay;

public class Main {

    static void main() {
        // Set up Pi4J
        Context pi4j = Pi4J.newAutoContext();

        ConfigurableIoExpander outputExpander = new Mcp23017Driver(
            pi4j.create(I2CConfig.newBuilder(pi4j).bus(1).device(0x27)));

        outputExpander.setIoDirections(0xffff, ConfigurableIoExpander.Direction.OUTPUT);

        TrafficLight trafficLight = new TrafficLight(
            /* red */ outputExpander.getOutput(8),      // PB0
            /* yellow */ outputExpander.getOutput(9),   // PB1
            /* green */ outputExpander.getOutput(10));  // PB2

        Delay delay = new Delay();
        // Cycle through the 4 states forever.
        while (true) {
            trafficLight.setState(TrafficLight.State.RED);
            delay.setMillis(2000).materialize();
            trafficLight.setState(TrafficLight.State.RED_YELLOW);
            delay.setMillis(1000).materialize();
            trafficLight.setState(TrafficLight.State.GREEN);
            delay.setMillis(2000).materialize();
            trafficLight.setState(TrafficLight.State.YELLOW);
            delay.setMillis(1000).materialize();
        }
    }
}
