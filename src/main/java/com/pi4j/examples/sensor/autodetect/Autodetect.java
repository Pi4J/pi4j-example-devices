package com.pi4j.examples.sensor.autodetect;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sensor.SensorDescriptor;
import com.pi4j.drivers.sensor.SensorDetector;

import java.util.List;

/**
 * Scans I2C bus 1 for available sensors and prints their measurements.
 */
public class Autodetect {

    private static final int BUS = 1;

    public static void main(String[] args) {

        Context pi4j = Pi4J.newAutoContext();

        List<Sensor> sensorList = SensorDetector.detectI2cSensors(pi4j, BUS);

        if (sensorList.isEmpty()) {
            System.out.println("No sensors found on I2C bus " + BUS);
        }
        for (Sensor sensor : sensorList) {
            System.out.println("Sensor: " + sensor.getClass().getSimpleName());

            List<SensorDescriptor.Value> valueDescriptors = sensor.getDescriptor().getValues();

            float[] values = new float[valueDescriptors.size()];
            sensor.readMeasurement(values);

            for (SensorDescriptor.Value valueDescriptor : valueDescriptors) {
                System.out.println(" - " + valueDescriptor.getKind() + ": " + values[valueDescriptor.getIndex()]);
            }

            // Shut the sensor down.
            sensor.close();
        }

        pi4j.shutdown();
    }
}
