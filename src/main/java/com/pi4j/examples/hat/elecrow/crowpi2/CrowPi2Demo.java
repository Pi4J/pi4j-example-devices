package com.pi4j.examples.hat.elecrow.crowpi2;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.elecrow.CrowPi2;
import com.pi4j.examples.hat.DisplayHatDemo;

public class CrowPi2Demo {

    public static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        CrowPi2 crowPi2 = new CrowPi2(pi4j);

        DisplayHatDemo demo = new DisplayHatDemo(
            crowPi2.getGraphicsDisplay(),
            crowPi2.getTextDisplay(),
            crowPi2.getGameController(),
            crowPi2.getSoundDriver(),
            null);

        demo.run();
    }

}
