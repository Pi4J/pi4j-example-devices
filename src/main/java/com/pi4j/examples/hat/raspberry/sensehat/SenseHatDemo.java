package com.pi4j.examples.hat.raspberry.sensehat;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.raspberry.SenseHat;
import com.pi4j.examples.hat.DisplayHatDemo;

/**
 * Demo for the Sense hat. Renders a menu with options for showing sensor values and playing snake.
 */
public class SenseHatDemo {

    public static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        SenseHat senseHat = new SenseHat(pi4j);

        new DisplayHatDemo(senseHat.getDisplay(), senseHat.getController(), senseHat.getAllSensors()).run();

        pi4j.shutdown();
    }
}



