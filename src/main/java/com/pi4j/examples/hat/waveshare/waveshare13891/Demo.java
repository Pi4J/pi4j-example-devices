package com.pi4j.examples.hat.waveshare.waveshare13891;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.waveshare.Waveshare13891;
import com.pi4j.examples.hat.DisplayHatDemo;

/**
 * A small demo allowing the user to play two mini games.
 */
public class Demo {

    static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        Waveshare13891 hat = new Waveshare13891(pi4j);

        new DisplayHatDemo(
            hat.getDisplay(),
            null,
            hat.getController(),
            null,
            null,
            null).run();

        hat.getDisplay().close();
        hat.getController().close();
        pi4j.shutdown();
    }

}
