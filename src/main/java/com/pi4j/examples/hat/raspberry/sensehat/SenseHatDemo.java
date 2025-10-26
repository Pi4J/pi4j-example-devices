package com.pi4j.examples.hat.raspberry.sensehat;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.raspberry.SenseHat;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.examples.games.snake.Snake;
import com.pi4j.examples.ui.ListView;
import com.pi4j.examples.ui.SensorView;

/**
 * Demo for the Sense hat. Renders a menu with options for showing sensor values and playing snake.
 */
public class SenseHatDemo {

    public static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        SenseHat senseHat = new SenseHat(pi4j);

        ListView menu = new ListView(senseHat.getDisplay(), senseHat.getController())
            .add("Sensors", () -> new SensorView(senseHat.getDisplay(), senseHat.getController(), 1)
                .addAll(senseHat.getAllSensors())
                .run())
            .add("Snake", () -> new Snake(senseHat.getDisplay(), senseHat.getController()).run());

        menu.run();

        pi4j.shutdown();
    }
}



