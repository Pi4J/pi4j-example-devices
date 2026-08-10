/*
 *  /*
 *  *
 *
 */

package com.pi4j.devices.mcp4922;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.da.mcp492x.Mcp4922;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;


public class MCP4922App {

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        Context pi4j = Pi4J.newAutoContext();


        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  MCP4922App ");
        console.print("==============================================================");

        double vrefA = 0;
        double vrefB = 0;

        short chipSelect = 0;
        SpiBus spiBus = SpiBus.BUS_0;

        double voutA = 0.0;
        double voutB = 0.0;
        int twelveBit = 0;
        boolean setTwelveBit = false;
        boolean setVoutA = false;
        boolean setVoutB = false;
        boolean buffered = false;
        boolean ga2x = false;
        boolean shdn = true;
        boolean AB = false;    // mcp4922 only
        int onlyOne = 0;   // numerous parms a mutually exclusive


        //   shdn   ab
        console.title("<-- The Pi4J V2 Project Extension  -->", "MCP4922App");
        String helpString = " parms:  -c HEX value chip select     -s HEX value SPI bus #  -vrefA float reference voltage A \n" +
            " -vrefB float reference voltage B    -voutA float VoutA    -voutB float VoutB  \n " +
            "-tb twelveBit  -shdn true active -AB A or B   -b true buffered  -ga2x boolean gain true 2x (1x default) \n" +
            " -tb and -voutA  -voutB  mutually exclusive";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-vrefA")) { // reference voltage
                String a = args[i + 1];
                i++;
                vrefA = Float.parseFloat(a);
            } else if (o.contentEquals("-vrefB")) { // reference voltage
                String a = args[i + 1];
                i++;
                vrefB = Float.parseFloat(a);
            } else if (o.contentEquals("-c")) { // pin
                String a = args[i + 1];
                chipSelect = Short.parseShort(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-s")) { // pin
                String a = args[i + 1];
                spiBus = SpiBus.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            } else if (o.contentEquals("-tb")) {
                String a = args[i + 1];
                i++;
                setTwelveBit = true;
                twelveBit = Integer.parseInt(a);
                if (twelveBit > 4095) {
                    console.println("-tb cannot exceed 4095");
                    System.exit(40);
                }
                onlyOne ++;
            } else if (o.contentEquals("-ga2x")) { // pin
                String a = args[i + 1];
                i++;
                ga2x =    Boolean.parseBoolean(a);
            } else if (o.contentEquals("-b")) { // pin
                String a = args[i + 1];
                i++;
                buffered =   Boolean.parseBoolean(a);
            } else if (o.contentEquals("-shdn")) {
                String a = args[i + 1];
                i++;
                shdn =  Boolean.parseBoolean(a);
            } else if (o.contentEquals("-AB")) {
                String a = args[i + 1];
                i++;
                if (a.equalsIgnoreCase("A")) {
                    AB = false;
                } else if (a.equalsIgnoreCase("B")) {
                    AB = true;
                } else {
                    console.println("  -AB invalid ");
                    System.exit(41);
                }
            } else if (o.contentEquals("-voutA")) { // reference voltage
                String a = args[i + 1];
                i++;
                setVoutA = true;
                voutA = Float.parseFloat(a);
                onlyOne ++;
            } else if (o.contentEquals("-voutB")) { // reference voltage
                String a = args[i + 1];
                i++;
                setVoutB = true;
                voutB = Float.parseFloat(a);
                onlyOne ++;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(42);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(43);
            }
        }

        if (onlyOne > 1 ) {
            console.println(" mutually exclusive parms used.");
            console.println(helpString);
            System.exit(44);
        }

        if ((setVoutA) && (vrefA == 0.0) ) {
            console.println("  -vrefA is zero  ");
            System.exit(45);
        }
        if ((setVoutB) && (vrefB == 0.0) ) {
            console.println("  -vrefB is zero  ");
            System.exit(46);
        }

        if ( (setVoutA || setVoutB) && setTwelveBit) {
            console.println("  -voutA or -voutB, and -tb provided, illegal combination ");
            System.exit(47);
        }

        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + spiBus + " " + chipSelect)
            .name("D-to-A converter")
            .bus(spiBus)
            .channel((int) chipSelect)
            .baud(Spi.DEFAULT_BAUD)
            .mode(SpiMode.MODE_0)
            .build();
        Spi spi = pi4j.create(spiConfig);

        //                  12 data bits
        //  A/B BUF GA SHDN D11 D10 D9 D8 D7 D6 D5 D4 D3 D2 D1 D0
        Mcp4922 mcpDrv = new Mcp4922(spi);

        if (setVoutA) {
            mcpDrv.writeTwelvePerVoltage(voutA, vrefA, false, buffered, ga2x, shdn);
        } else if (setVoutB) {
            mcpDrv.writeTwelvePerVoltage(voutB, vrefB, true, buffered, ga2x, shdn);
        } else {
            mcpDrv.writeTwelve(twelveBit, AB, buffered, ga2x, shdn);
        }


        if (spi != null) {
            spi.close();
        }

    }


}
