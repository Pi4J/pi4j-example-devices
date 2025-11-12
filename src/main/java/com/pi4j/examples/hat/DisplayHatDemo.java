package com.pi4j.examples.hat;

import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.examples.games.bricks.Bricks;
import com.pi4j.examples.games.snake.Snake;
import com.pi4j.examples.ui.ListView;
import com.pi4j.examples.ui.SensorView;

import java.util.List;

/** A generic demo for display hats */
public class DisplayHatDemo {

    private final GraphicsDisplay display;
    private final GameController controller;
    private final List<Sensor> sensors;

    public DisplayHatDemo(
        GraphicsDisplay display,
        GameController controller,
        List<Sensor> sensors
    ) {
        this.display = display;
        this.controller = controller;
        this.sensors = sensors;
    }

    public void run() {
        int resolution = Math.min(display.getWidth(), display.getHeight());
        int scale = Math.max(1, resolution) / 64;

        ListView menu = new ListView(display, controller, scale);
        if (sensors != null && !sensors.isEmpty()) {
            menu.add("Sensors", () -> new SensorView(display, controller, scale)
                .addAll(sensors)
                .run());
        }
        menu.add("Snake", () -> new Snake(display, controller).run());
        if (resolution >= 64) {
            menu.add("Bricks", () -> new Bricks(display, controller).run());
        }
        menu.add("Exit", ListView.EXIT_ACTION);
        menu.run();
    }


}
