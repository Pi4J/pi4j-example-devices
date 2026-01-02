package com.pi4j.examples.server;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.sensor.Sensor;
import com.pi4j.drivers.sensor.SensorDescriptor;
import com.pi4j.drivers.sensor.SensorDetector;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class SensorServer {
    private static final int BUS = 1;
    private static final int PORT = 8147;

    private final ServerSocket serverSocket;
    private final List<Sensor> sensorList;
    private final String address;

    SensorServer(int port, List<Sensor> sensorList) throws IOException {
        serverSocket = new ServerSocket(port);
        this.sensorList = sensorList;
        try (
            Socket socket = new Socket();
        ) {
            // Get our own address, see https://stackoverflow.com/a/41822127/1401879
            socket.connect(new InetSocketAddress("google.com", 80));
            address = "http:/" + socket.getLocalAddress() + ":" + port;
        }
    }

    void run() {
        while(true) {
            try(Socket socket = serverSocket.accept()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String request = in.readLine();
                System.out.println(new Date() + ": Request from " + socket.getRemoteSocketAddress() + ": " + request);
                while (true) {
                    String line = in.readLine();
                    if (line == null || line.isEmpty()) {
                        break;
                    }
                }
                Writer writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                if (request == null || !request.startsWith("GET /")) {
                    writer.write("HTTP/1.0 501 Error\r\n\r\n");
                } else {
                    int end = request.lastIndexOf(' ');
                    String path = request.substring(5, end);
                    StringBuilder builder = new StringBuilder();
                    String contentType = switch (path) {
                        case "" -> renderHelp(builder);
                        case "measurements" -> renderData(builder);
                        case "config" -> renderConfig(builder);
                        default -> null;
                    };

                    if (contentType == null) {
                        writer.write("HTTP/1.0 404 Not Found\r\n\r\n");
                    } else {
                        writer.write("HTTP/1.0 200 OK\r\n");
                        writer.write("Content-Type: " + contentType + "; charset=utf-8\r\n\r\n");
                        writer.write(builder.toString());
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String renderData(StringBuilder sb) {
        boolean first = true;
        sb.append("{");
        Map<String, Integer> names = new HashMap<>();
        for (Sensor sensor : sensorList) {
            SensorDescriptor descriptor = sensor.getDescriptor();
            List<SensorDescriptor.Value> valueDescriptors = descriptor.getValues();
            float[] measurements = new float[valueDescriptors.size()];
            sensor.readMeasurement(measurements);
            for (SensorDescriptor.Value valueDescriptor : valueDescriptors) {
                sb.append(first ? "\n  " : ",\n  ");
                first = false;
                sb.append('"');
                sb.append(getUniqueName(names, valueDescriptor));
                sb.append("\": ");
                sb.append(measurements[valueDescriptor.getIndex()]);
            }
        }
        sb.append("\n}\n");
        return "application/json";
    }

    String renderConfig(StringBuilder sb) {
        // https://www.home-assistant.io/integrations/sensor.rest/
        sb.append("# Add this snippet to the 'rest:' section in your HomeAssistant config.yaml:\n");
        sb.append("# rest:\n");
        sb.append("  - resource: \"").append(address).append("/measurements\"\n");
        sb.append("    sensor:\n");
        Map<String, Integer> names = new HashMap<>();
        for (Sensor sensor : sensorList) {
            SensorDescriptor descriptor = sensor.getDescriptor();
            List<SensorDescriptor.Value> valueDescriptors = descriptor.getValues();
            float[] measurements = new float[valueDescriptors.size()];
            sensor.readMeasurement(measurements);
            for (SensorDescriptor.Value valueDescriptor : valueDescriptors) {
                String name = getUniqueName(names, valueDescriptor);
                String deviceClass = getDeviceClass(valueDescriptor.getKind());
                if (deviceClass != null) {
                    sb.append("      - name: \"").append(name).append("\"\n");
                    sb.append("        value_template: \"{{ value_json['").append(name).append("'] }}\"\n");
                    sb.append("        device_class: ").append(deviceClass).append("\n");
                    sb.append("        unit_of_measurement: \"").append(getUnit(valueDescriptor.getKind())).append("\"\n");
                }
            }
        }
        return "text/vnd.yaml";
    }

    String renderHelp(StringBuilder sb) {
        sb.append("Pi4J I2C Autodetect Sensor Server\n\n");

        sb.append("HomeAssistant YAML\n");
        sb.append("configuration snippet: ").append(address).append("/config\n");
        sb.append("Measurements:          ").append(address).append("/measurements\n");
        sb.append("This help text:        ").append(address).append("\n");

        return "text/plain";
    }

    static String getDeviceClass(SensorDescriptor.Kind kind) {
        return switch(kind) {
            case LIGHT, LIGHT_BLUE, LIGHT_RED, LIGHT_GREEN -> "illuminance";
            case CO2, DISTANCE, HUMIDITY, PRESSURE, TEMPERATURE -> kind.name().toLowerCase(Locale.ROOT);
            default -> null;
        };
    }

    static String getUnit(SensorDescriptor.Kind kind) {
        return switch (kind) {
            case ACCELERATION_X, ACCELERATION_Y, ACCELERATION_Z -> "m/s²";
            case ANGULAR_VELOCITY_X, ANGULAR_VELOCITY_Y, ANGULAR_VELOCITY_Z -> "°/s";
            case CO2 -> "ppm";
            case DISTANCE -> "m";
            case HUMIDITY -> "%";
            case LIGHT, LIGHT_BLUE, LIGHT_RED, LIGHT_GREEN -> "lx";
            case MAGNETIC_FIELD_X, MAGNETIC_FIELD_Y, MAGNETIC_FIELD_Z -> "G";
            case PRESSURE -> "hPa";
            case TEMPERATURE -> "°C";
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
        List<Sensor> sensorList = SensorDetector.detectI2cSensors(pi4j, BUS);
        System.out.println("Detected sensors: " + sensorList.stream().map(s -> s.getClass().getSimpleName()).toList());
        System.out.println("Starting server on port " + PORT);
        System.out.println();
        SensorServer server = new SensorServer(PORT, sensorList);

        StringBuilder sb = new StringBuilder();
        server.renderHelp(sb);
        System.out.println(sb);

        server.run();
    }
}
