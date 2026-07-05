package com.pi4j.devices.bmp280;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.sensor.environment.bmx280.Bmx280Driver;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;

import java.text.DecimalFormat;


public class BMP280SpiExample {


    /**
     * Sample application using BMP280 sensor chip.
     *
     * @param args an array of {@link java.lang.String} objects.
     *             Parms are not required. if 'any' parameter value is supplied,
     *             the example uses the create pattern for device instantiation,
     *             otherwise provider setup is used
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception
    {

        int chipSelect = 0;
        SpiBus spiBus = SpiBus.BUS_0;
        Context pi4j = Pi4J.newAutoContext();


        // ------------------------------------------------------------
        // Initialize the Pi4J Runtime Context
        // ------------------------------------------------------------
        // Before you can use Pi4J you must initialize a new runtime
        // context.
        //
        // The 'Pi4J' static class includes a few helper context
        // creators for the most common use cases.  The 'newAutoContext()'
        // method will automatically load all available Pi4J
        // extensions found in the application's classpath which
        // may include 'Platforms' and 'I/O Providers'


        // print installed providers
        System.out.println("----------------------------------------------------------");
        System.out.println("PI4J PROVIDERS");
        System.out.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");


        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  BMP280SpiExample ");
        console.print("==============================================================");


        String helpString = " parms:  NONE      \n " ;
         for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }

        Spi spiDev = createSPIDevice(pi4j, spiBus, chipSelect);

        Bmx280Driver bmpDev = new Bmx280Driver(spiDev);



        bmpDev.reset();

        DecimalFormat df = new DecimalFormat("0.###");

        Bmx280Driver.Measurement measurement = bmpDev.readMeasurement();


        console.println("  Setup ----------------------------------------------------------");

        console.println(" Temperature C = " + df.format(measurement.getTemperature()) );

        console.println(" Temperature F = " +  df.format(measurement.getTemperature()  * 1.8 + 32)  );

        console.println(" Pressure Pa = " + df.format(measurement.getPressure()));

        console.println(" Pressure mbar = " + df.format(measurement.getPressure() / 100));

        console.println(" Pressure InHg = " + df.format(measurement.getPressure() / 3386));

        console.println("  Dev Spi detail    SpiBus : " + spiBus + "  chipSelect : " + chipSelect);

        // Shutdown Pi4J
        pi4j.shutdown();
    }

/**
 * Use the state from the Sensor config object and the state pi4j to create
 * a BMP280 device instance
 */
private static  Spi createSPIDevice(Context pi4j, SpiBus spiBus, int channel) {
    var spiConfig = Spi.newConfigBuilder(pi4j)
        .id("SPI" + spiBus + "_BMP280")
        .name("D/A converter")
        .bus(spiBus)
        .channel(channel)
        //1 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
        //b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
        // .flags(0b0000000000000000100000L)  // MODE0, ux GPIO not used for chip select
        .baud(Spi.DEFAULT_BAUD)    // Max 10MHz
        .mode(SpiMode.MODE_0)
        .build();
    return  pi4j.create(spiConfig);
}
}