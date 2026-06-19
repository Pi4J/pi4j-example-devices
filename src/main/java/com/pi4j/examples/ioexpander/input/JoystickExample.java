package com.pi4j.examples.ioexpander.input;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.expander.ConfigurableIoExpander;
import com.pi4j.drivers.io.expander.mcp23017.Mcp23017Driver;
import com.pi4j.io.ListenableOnOffRead;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.i2c.I2CConfig;

public class JoystickExample {

    public static void main(String[] args) throws InterruptedException {
        Context context = Pi4J.newAutoContext();

        // The interrupt pin is connected to BCM 17
        ListenableOnOffRead<?> interruptPin = context.create(DigitalInputConfig.newBuilder(context).bcm(17));

        // The driver uses the interrupt pin to internally poll the chip and update all the pins
        Mcp23017Driver expander = new Mcp23017Driver(context.create(I2CConfig.newBuilder(context).bus(1).device(0x27)), interruptPin);

        // Configure everything as input
        expander.setIoDirections(0xffff, ConfigurableIoExpander.Direction.INPUT);

        // Pull up all the pins
        expander.setPullupResistorConfigurations(0xffff);

        // Swap polarity, so we see pressed as "true" and released as "false"
        expander.setInputPolarities(0xffff);

        // Render events as the keys are pressed and released.
        expander.getInput(15).addConsumer((value) -> System.out.println("Up Key " + value));
        expander.getInput(14).addConsumer((value) -> System.out.println("Down Key " + value));
        expander.getInput(13).addConsumer((value) -> System.out.println("Left Key " + value));
        expander.getInput(12).addConsumer((value) -> System.out.println("Right Key " + value));

        // The loop prints the current time every 10 seconds -- note that it does not poll the IO Expander.
        // The inputs
        while (true) {
            System.out.println("Waiting for input..." + System.currentTimeMillis());
            Thread.sleep(10000);
        }
    }
}
