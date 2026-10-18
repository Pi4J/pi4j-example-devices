package com.pi4j.examples.sensor.display;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.display.character.CharacterDisplay;
import com.pi4j.drivers.display.character.hd44780.Hd44780Driver;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfigBuilder;

/** Application rendering auto-detected I2C sensors on a 4x20 LCD connected via a PCF8574. */
public class LcdSensorDisplayApp {
    private static final int I2C_BUS = 1;
    private static final int DISPLAY_I2C_ADDRESS = 0x27;
    private static final int DISPLAY_WIDTH = 20;
    private static final int DISPLAY_HEIGHT = 4;

    public static void main(String[] args) throws InterruptedException {
        Context pi4j = Pi4J.newAutoContext();
        I2C displayI2c = pi4j.create(I2CConfigBuilder.newInstance(pi4j).bus(I2C_BUS).device(DISPLAY_I2C_ADDRESS));
        CharacterDisplay characterDisplay = Hd44780Driver.withPcf8574Connection(displayI2c, DISPLAY_WIDTH, DISPLAY_HEIGHT);

        SensorDisplay sensorDisplay = new SensorDisplay(pi4j, I2C_BUS, characterDisplay);

        while (true) {
            sensorDisplay.readAndDisplayMeasurements();
            Thread.sleep(4_000);
        }
    }
}
