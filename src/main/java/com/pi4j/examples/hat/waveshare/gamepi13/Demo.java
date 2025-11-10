package com.pi4j.examples.hat.waveshare.gamepi13;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.hat.waveshare.GamePi13;
import com.pi4j.drivers.input.GameController;

/**
 * A simple demo running a "Snake" game. Exit by pressing the select key.
 */
public class Demo {

    static void main(String[] args) {
        Context pi4j = Pi4J.newAutoContext();
        GamePi13 hat = new GamePi13(pi4j);
        GraphicsDisplay display = hat.getDisplay();
        GameController controller = hat.getController();

        new com.pi4j.examples.games.snake.Snake(display, controller).run();

        display.close();
        controller.close();
        pi4j.shutdown();
    }

}
