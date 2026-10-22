package com.pi4j.devices.bmp280;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.examples.utils.commandhandler.DeviceCommandHandler;
import com.pi4j.examples.utils.commandhandler.IntegerParameter;
import com.pi4j.drivers.sensor.environment.bmx280.Bmx280Driver;
import com.pi4j.io.i2c.I2C;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Class to test and demonstrate the BMP280 device.
 */
public class BMP280I2cExample {

    private static final int DEFAULT_BUS = 0x1;
    private static final NumberFormat FORMAT = new DecimalFormat("0.###");

    /**
     * Sample application using BMP280 sensor chip.
     */
    static void main(String[] args) {
        System.out.println("BMP 280 I2C Example");
        System.out.println();

        Context pi4j = Pi4J.newAutoContext();

        DeviceCommandHandler<Bmx280Driver> commandHandler = new DeviceCommandHandler<>(settings ->
            new Bmx280Driver(pi4j.create(I2C.newConfigBuilder()
                .device((Integer) settings.get("a"))
                .bus((Integer) settings.get("b")))));

        commandHandler.configuration(
            "a",
            "Set the BMx280 I2C Device Address",
            new IntegerParameter(0, 127),
            Bmx280Driver.ADDRESS_BMP_280);

        commandHandler.configuration(
            "b",
            "Set the BMx280 I2C Bus",
            new IntegerParameter(0, 127),
            DEFAULT_BUS);

        commandHandler.action("r", "Read measurements (default)", device -> {
            Bmx280Driver.Measurement measurement = device.readMeasurement();

            System.out.println("Temperature °C: " + FORMAT.format(measurement.getTemperature()) );
            System.out.println("Temperature °F: " +  FORMAT.format(measurement.getTemperature()  * 1.8 + 32)  );
            System.out.println("Pressure Pa: " + FORMAT.format(measurement.getPressure()));
            System.out.println("Pressure mbar: " + FORMAT.format(measurement.getPressure() / 100));
            System.out.println("Pressure InHg: " + FORMAT.format(measurement.getPressure() / 3386));
            System.out.println("Humidity: " + FORMAT.format(measurement.getHumidity()) + " %");
        });

        commandHandler.setDefault("r");
        commandHandler.process(args);

        // Shutdown Pi4J
        pi4j.shutdown();
    }
}