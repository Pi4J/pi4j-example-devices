package com.pi4j.examples.sensor.homeassistant;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sensor.SensorDescriptor;
import com.pi4j.drivers.sensor.SensorDetector;

import java.io.*;
import java.util.*;

public class SensorServer {
    private static final int I2C_BUS = 1;
    private static final int HTTP_PORT = 8147;

    private final List<Sensor> sensorList;
    private final TinyWebServer webServer = new TinyWebServer(HTTP_PORT);
    private final String address = webServer.getUrl();

    SensorServer(int port, List<Sensor> sensorList) throws IOException {
        this.sensorList = sensorList;
        webServer.addRequestHandler("config", "text/vnd.yaml", this::renderConfig);
        webServer.addRequestHandler("measurements", "application/json", this::renderMeasurements);
        webServer.addRequestHandler("", "text/plain", this::renderHelp);
    }

    void run() {
        webServer.run();
    }

    void renderMeasurements(PrintWriter out) {
        boolean first = true;
        out.print("{");
        Map<String, Integer> names = new HashMap<>();
        for (Sensor sensor : sensorList) {
            SensorDescriptor descriptor = sensor.getDescriptor();
            List<SensorDescriptor.Value> valueDescriptors = descriptor.getValues();
            double[] measurements = new double[valueDescriptors.size()];
            sensor.readMeasurement(measurements);
            for (SensorDescriptor.Value valueDescriptor : valueDescriptors) {
                out.print(first ? "\n  " : ",\n  ");
                first = false;
                out.print('"');
                out.print(getUniqueName(names, valueDescriptor));
                out.print("\": ");
                out.print(measurements[valueDescriptor.getIndex()]);
            }
        }
        out.print("\n}\n");
    }

    void renderConfig(PrintWriter out) {
        // https://www.home-assistant.io/integrations/sensor.rest/
        out.println("# Add this snippet to the 'rest:' section in your HomeAssistant config.yaml:");
        out.println("# rest:");
        out.println("  - resource: \"" + address +"/measurements\"");
        out.println("    sensor:\n");
        Map<String, Integer> names = new HashMap<>();
        for (Sensor sensor : sensorList) {
            SensorDescriptor descriptor = sensor.getDescriptor();
            for (SensorDescriptor.Value valueDescriptor : descriptor.getValues()) {
                String name = getUniqueName(names, valueDescriptor);
                String deviceClass = getDeviceClass(valueDescriptor.getKind());
                if (deviceClass != null) {
                    out.println("      - name: \"" + name + '"');
                    out.println("        value_template: \"{{ value_json['" + name + "'] }}\"");
                    out.println("        device_class: " + deviceClass);
                    out.println("        unit_of_measurement: \"" + valueDescriptor.getKind().measurementUnit + '"');
                }
            }
        }
    }

    void renderHelp(PrintWriter out) {
        out.println("Pi4J I2C Autodetect Sensor Server");
        out.println();
        out.println("HomeAssistant YAML");
        out.println("configuration snippet: " + address + "/config");
        out.println("Measurements:          " + address + "/measurements\n");
        out.println("This help text:        " + address);
    }

    static String getDeviceClass(SensorDescriptor.Kind kind) {
        return switch(kind) {
            case LIGHT, LIGHT_BLUE, LIGHT_RED, LIGHT_GREEN -> "illuminance";
            case CO2, DISTANCE, HUMIDITY, PRESSURE, TEMPERATURE -> kind.toString();
            default -> null;
        };
    }

    static String getUniqueName(Map<String, Integer> names, SensorDescriptor.Value valueDescriptor) {
        String name = valueDescriptor.getKind().toString().toLowerCase(Locale.ROOT);
        int suffix = names.getOrDefault(name, 1);
        names.put(name, suffix + 1);
        return suffix > 1 ? name + "_" + suffix : name;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Context pi4j = Pi4J.newAutoContext();
        List<Sensor> sensorList = SensorDetector.detectI2cSensors(pi4j, I2C_BUS);
        if (sensorList.isEmpty()) {
            throw new IllegalStateException("No sensors detected. Server not started.");
        }
        System.out.println("Detected sensors: " + sensorList.stream().map(s -> s.getClass().getSimpleName()).toList());
        System.out.println("Starting server on port " + HTTP_PORT);
        System.out.println();
        SensorServer server = new SensorServer(HTTP_PORT, sensorList);

        server.renderHelp(new PrintWriter(System.out, true));

        server.run();
    }
}
