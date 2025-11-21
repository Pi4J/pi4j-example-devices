package com.pi4j.examples.hat.waveshare.waveshare14972;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.waveshare.Waveshare14972;
import com.pi4j.examples.hat.DisplayHatDemo;

/**
 * A simple demo running a "Snake" game. Exit by holding the center button for one second.
 */
public class Demo {

    static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        Waveshare14972 hat = new Waveshare14972(pi4j);

        new DisplayHatDemo(
            hat.getDisplay(),
            null,
            hat.getController(),
            null,
            null).run();

        hat.getDisplay().close();
        hat.getController().close();
        pi4j.shutdown();
    }

}
