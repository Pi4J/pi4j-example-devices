package com.pi4j.examples.ui;

import com.pi4j.drivers.display.character.CharacterDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sensor.SensorDescriptor;

import java.util.*;

/**
 * Renders a list of sensors using a list view. For a usage example, please refer to the SenseHat demo.
 */
public class SensorView {
    private final ListView listView;
    private final List<Sensor> sensors = new ArrayList<>();
    private final Timer timer = new Timer();

    private boolean running;

    private static String titleCase(String s) {
        boolean toUpper = true;
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '_' || c == ' ') {
                toUpper = true;
                sb.append(' ');
            } else if (toUpper) {
                sb.append(Character.toUpperCase(c));
                toUpper = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    public SensorView(
        CharacterDisplay display,
        GameController controller
    ) {
        this.listView = new ListView(display, controller);
    }

    public SensorView add(Sensor sensor) {
        sensors.add(sensor);
        for (SensorDescriptor.Value valueDescriptor : sensor.getDescriptor().getValues()) {
            listView.add(valueDescriptor.getKind().name());
        }
        return this;
    }

    public SensorView addAll(List<Sensor> sensors) {
        for (Sensor sensor : sensors) {
            add(sensor);
        }
        return this;
    }

    public void run() {
        running = true;
        scheduleUpdate();
        listView.run();
        running = false;
        timer.purge();
    }

    private void scheduleUpdate() {
        if (running) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    int listIndex = 0;
                    for (Sensor sensor : sensors) {
                        float[] values = new float[sensor.getDescriptor().getValues().size()];
                        sensor.readMeasurement(values);
                        for (SensorDescriptor.Value valueDescriptor : sensor.getDescriptor().getValues()) {
                            listView.set(listIndex, titleCase(valueDescriptor.getKind().name()) + ": " + Math.round(100 * values[valueDescriptor.getIndex()]) / 100f);
                            listIndex++;
                        }
                    }
                    scheduleUpdate();
                }
            }, 100);
        }
    }
}
