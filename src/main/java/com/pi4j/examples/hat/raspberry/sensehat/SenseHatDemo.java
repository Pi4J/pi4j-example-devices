package com.pi4j.examples.hat.raspberry.sensehat;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.hat.raspberry.SenseHat;
import com.pi4j.drivers.input.GameController;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sensor.SensorDescriptor;

import java.util.List;

/**
 * Demo for the Sense hat. Renders measurements for all available sensors in a scrolling line of text.
 * Use the joystick up/down movements to switch between sensors.
 */
public class SenseHatDemo {
    private final Context pi4j = Pi4J.newAutoContext();
    private final SenseHat senseHat = new SenseHat(pi4j);
    private final List<Sensor> sensors = senseHat.getAllSensors();
    private final MatrixScrollDisplay scrollDisplay = new MatrixScrollDisplay(senseHat.getDisplay());

    private int index = 0;

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

    void run() {
        selectIndex(0);

        senseHat.getController().getKey(GameController.Key.UP).addConsumer(value -> move(value, -1));
        senseHat.getController().getKey(GameController.Key.DOWN).addConsumer(value -> move(value, 1));

        while (true) {
            // Update the measured value for the current sensor every 10s.
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            selectIndex(index);
        }
    }

    void selectIndex(int index) {
        index = (index + sensors.size()) % sensors.size();
        if (index != this.index) {
            this.index = index;
            scrollDisplay.clear();
        }
        Sensor sensor = sensors.get(index);
        float[] values = new  float[sensor.getDescriptor().getValues().size()];
        sensor.readMeasurement(values);
        StringBuilder sb = new StringBuilder();
        for (SensorDescriptor.Value valueDescriptor: sensor.getDescriptor().getValues()) {
            float value = Math.round(values[valueDescriptor.getIndex()] * 10) / 10f;
            sb.append(titleCase(valueDescriptor.getKind().name()));
            sb.append(": ");
            sb.append(value);
            sb.append("  ");
        }
        scrollDisplay.setText(sb.toString());
    }


    void move(boolean pressed, int dir) {
        if (!pressed) {
            selectIndex(index + dir);
        }
    }

    public static void main(String[] args) {
        new SenseHatDemo().run();
    }
}



