package com.pi4j.examples.hat;

import com.pi4j.drivers.display.character.CharacterDisplay;
import com.pi4j.drivers.display.graphics.GraphicsCharacterDisplay;
import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sound.Note;
import com.pi4j.drivers.sound.SoundDriver;
import com.pi4j.examples.games.bricks.Bricks;
import com.pi4j.examples.games.snake.Snake;
import com.pi4j.examples.ui.ListView;
import com.pi4j.examples.ui.SensorView;

import java.util.List;

/** A generic demo for display hats */
public class DisplayHatDemo {

    private final GraphicsDisplay graphicsDisplay;
    private final CharacterDisplay characterDisplay;
    private final GameController controller;
    private final SoundDriver soundDriver;
    private final List<Sensor> sensors;

    public DisplayHatDemo(
        GraphicsDisplay graphicsDisplay,
        CharacterDisplay characterDisplay,
        GameController controller,
        SoundDriver soundDriver,
        List<Sensor> sensors
    ) {
        this.graphicsDisplay = graphicsDisplay;
        this.characterDisplay = characterDisplay != null ? characterDisplay
            : new GraphicsCharacterDisplay(graphicsDisplay);
        this.controller = controller;
        this.soundDriver = soundDriver;
        this.sensors = sensors;
    }

    public void run() {
        int resolution = Math.min(graphicsDisplay.getWidth(), graphicsDisplay.getHeight());

        ListView menu = new ListView(characterDisplay, controller);
        if (sensors != null && !sensors.isEmpty()) {
            menu.add("Sensors", () -> new SensorView(characterDisplay, controller)
                .addAll(sensors)
                .run());
        }
        menu.add("Snake", () -> new Snake(graphicsDisplay, controller).run());
        if (resolution >= 64) {
            menu.add("Bricks", () -> new Bricks(graphicsDisplay, controller).run());
        }
        if (soundDriver != null) {
            menu.add("Play Demo Sound", () -> {
                soundDriver.playNotes(103, null, Note.G4, 8, Note.G4, 8, Note.G4, 8, Note.DS4, 6, Note.AS4, 2, Note.G4, 8, Note.DS4, 6, Note.AS4, 2, Note.G4, 16);
            });
        }
        menu.add("Exit", ListView.EXIT_ACTION);
        menu.run();
    }


}
