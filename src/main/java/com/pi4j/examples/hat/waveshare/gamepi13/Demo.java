package com.pi4j.examples.hat.waveshare.gamepi13;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.waveshare.GamePi13;
import com.pi4j.examples.hat.DisplayHatDemo;

/**
 * A simple demo running a "Snake" game. Exit by pressing the select key.
 */
public class Demo {

    static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        GamePi13 hat = new GamePi13(pi4j);

        new DisplayHatDemo(
            hat.getDisplay(),
            null,
            hat.getController(),
            /*hat.getSoundDriver()*/ null,
            null).run();

        hat.close();
        pi4j.shutdown();
    }

}
