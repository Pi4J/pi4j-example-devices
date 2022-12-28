package com.pi4j.devices.bmp280;

/*
 *
 *
 *  #%L
 *  **********************************************************************
 *  ORGANIZATION  :  Pi4J
 *  PROJECT       :  Pi4J ::  Providers
 *  FILENAME      :  BMP280I2cExample.java
 *
 *  This file is part of the Pi4J project. More information about
 *  this project can be found here:  https://pi4j.com/
 *  **********************************************************************
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU General Lesser Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/lgpl-3.0.html>.
 *  #L%
 *
 */


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.exception.LifecycleException;
import com.pi4j.plugin.linuxfs.provider.i2c.LinuxFsI2CProvider;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Class to test and demonstrate the BMP280 device.
 */
public class BMP280I2cExample {

    private static final int BMP280_I2C_ADDRESS = BMP280Declares.DEFAULT_ADDRESS;
    private static final int BMP280_I2C_BUS = BMP280Declares.DEFAULT_BUS;

    /**
     * Sample application using BMP280 sensor chip.
     *
     * @param args an array of {@link java.lang.String} objects.
     *             Parms are not required. if 'any' parameter value is supplied,
     *             the example uses the create pattern for device instantiation,
     *             otherwise provider setup is used
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {


        int busNum = BMP280Declares.DEFAULT_BUS;
        int address = BMP280Declares.DEFAULT_ADDRESS;

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

        var pi4j = Pi4J.newContextBuilder().add(
                LinuxFsI2CProvider.newInstance()).build();


       // Context pi4j =  Pi4J.newAutoContext();

        // print installed providers
        System.out.println("----------------------------------------------------------");
        System.out.println("PI4J PROVIDERS");
        System.out.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");
        // Prior to running methods, set up control-c handler
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Performing ctl-C shutdown");
                try {
                    pi4j.shutdown();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
                Thread.dumpStack();
                System.exit(2);
            }
        });


        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  BMP280I2cExample ");
        console.print("==============================================================");


        String helpString = " parms: -b hex value bus    -a hex value address  -t trace \n " +
                " \n trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\"  Default \"info\"";
        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-b")) { // bus
                String a = args[i + 1];
                busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-t")) { // device address
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(40);
                }
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }


        var bmpDev = new BMP280Device(pi4j, console, busNum, address, traceLevel);
        bmpDev.initSensor();
        console.println("  Dev I2C detail    " + bmpDev.i2cDetail());
        console.println("  Setup ----------------------------------------------------------");


        console.println("  I2C detail : " + bmpDev.i2cDetail());

        double reading1 = bmpDev.temperatureC();
        console.println(" Temperatue C = " + reading1);

        double reading2 = bmpDev.temperatureF();
        console.println(" Temperatue F = " + reading2);

        double press1 = bmpDev.pressurePa();
        console.println(" Pressure Pa = " + press1);

        double press2 = bmpDev.pressureIn();
        console.println(" Pressure InHg = " + press2);

        double press3 = bmpDev.pressureMb();
        console.println(" Pressure mb = " + press3);


        // Shutdown Pi4J
        pi4j.shutdown();
    }

}