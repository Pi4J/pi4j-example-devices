package com.pi4j.examples.ioexpander.input;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.expander.InputExpander;
import com.pi4j.drivers.io.expander.pcf8574.Pcf8574Driver;
import com.pi4j.io.ListenableOnOffRead;
import com.pi4j.io.gpio.digital.DigitalInputConfig;
import com.pi4j.io.i2c.I2CConfig;

public class JoystickExample {

    public static void main(String[] args) throws InterruptedException {
        Context context = Pi4J.newAutoContext();

        // The interrupt pin is connected to BCM 23
        ListenableOnOffRead<?> interruptPin = context.create(DigitalInputConfig.newBuilder(context).bcm(23));

        // The driver uses the interrupt pin to internally poll the chip and update all the pins
        InputExpander expander = Pcf8574Driver.createForInput(
            context.create(I2CConfig.newBuilder(context).bus(1).device(0x20)),
            interruptPin);

        // Render events as the keys are pressed and released.
        expander.getInput(0).addConsumer((value) -> System.out.println("Up Key " + !value));
        expander.getInput(1).addConsumer((value) -> System.out.println("Down Key " + !value));
        expander.getInput(2).addConsumer((value) -> System.out.println("Left Key " + !value));
        expander.getInput(3).addConsumer((value) -> System.out.println("Right Key " + !value));

        // The loop prints the current time every 10 seconds -- note that it does not poll the IO Expander.
        // The inputs
        while (true) {
            System.out.println("Waiting for input..." + System.currentTimeMillis() + " state: " + Integer.toBinaryString(expander.getInputStates()));
            Thread.sleep(10_000);
        }
    }
}
