package com.pi4j.examples.sensor.display;

import com.pi4j.context.Context;
import com.pi4j.drivers.display.character.CharacterDisplay;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sensor.SensorDescriptor;
import com.pi4j.drivers.sensor.SensorDetector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Sensor display helper class, bundling a list of sensors and a character display.
 * It's able to aggregates sensor readings and display them on the given display.
 */
public class SensorDisplay {
    private static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

    private final CharacterDisplay display;
    private final List<Sensor> sensorList;

    public SensorDisplay(List<Sensor> sensorList, CharacterDisplay display) {
        this.display = display;
        this.sensorList = sensorList;
    }

    public SensorDisplay(Context context, int bus, CharacterDisplay display) {
        this(SensorDetector.detectI2cSensors(context, bus), display);
    }

    /**
     * Reads measurements and returns them in a nested map grouped by type and sensor.
     * This (somewhat complex) format allows simple aggregation of sensor readings of the same type from
     * different sensors.
     */
    public Map<SensorDescriptor.Kind, Map<Sensor, Double>> readMeasurements() {
        Map<SensorDescriptor.Kind, Map<Sensor, Double>> results = new TreeMap<>();
        for (Sensor sensor: sensorList) {
            SensorDescriptor descriptor = sensor.getDescriptor();
            List<SensorDescriptor.Value> valueDescriptorList = descriptor.getValues();
            double[] readings = new double[valueDescriptorList.size()];
            sensor.readMeasurement(readings);
            for (SensorDescriptor.Value valueDescriptor: valueDescriptorList) {
                results.putIfAbsent(valueDescriptor.getKind(), new LinkedHashMap<>());
                Map<Sensor, Double> valueMap = results.get(valueDescriptor.getKind());
                valueMap.put(sensor, readings[valueDescriptor.getIndex()]);
            }
        }
        return results;
    }

    /**
     * Renders the given measurements on the display, averaging all values of the same type.
     */
    public void displayMeasurements(Map<SensorDescriptor.Kind, Map<Sensor, Double>> measurements) {
        int row = 0;
        for (Map.Entry<SensorDescriptor.Kind, Map<Sensor, Double>> entry: measurements.entrySet()) {
            double average = entry.getValue().values().stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .getAsDouble();

            String formatted = formatReading(entry.getKey(), average, display.getWidth());
            display.writeAt(0, row++, formatted);
        }
    }

    /**
     * Convenience method combining reading and rendering sensor data.
     */
    public Map<SensorDescriptor.Kind, Map<Sensor, Double>> readAndDisplayMeasurements() {
        Map<SensorDescriptor.Kind, Map<Sensor, Double>> measurements = readMeasurements();
        displayMeasurements(measurements);
        return measurements;
    }

    // Helpers

    /**
     * Formats a sensor reading to a given number of columns.
     */
    public static String formatReading(SensorDescriptor.Kind kind, double value, int columns) {
        String renderedValue = DECIMAL_FORMAT.format(value);
        String renderedLabel = Character.toUpperCase(kind.toString().charAt(0)) + kind.toString().substring(1);

        int available = columns - renderedValue.length() - 6;
        if (renderedLabel.length() > available) {
            renderedLabel = renderedLabel.substring(0, available - 1) + ".";
        }
        return String.format("%-" + (available + 1) + "s %s %-3s", (renderedLabel + ":"), renderedValue, kind.measurementUnit);
    }

 }
