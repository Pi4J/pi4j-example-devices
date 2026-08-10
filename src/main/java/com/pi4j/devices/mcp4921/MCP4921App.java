package com.pi4j.devices.mcp4921;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.da.mcp492x.Mcp4921;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;


public class MCP4921App {

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        Context pi4j = Pi4J.newAutoContext();


        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  MCP4921App ");
        console.print("==============================================================");

        double vref = 0;

        short chipSelect = 0;
        SpiBus spiBus = SpiBus.BUS_0;

        double vout = 0.0;
        int twelveBit = 0;
        boolean setTwelveBit = false;
        boolean setVout = false;
        boolean buffered = false;
        boolean ga2x = true;
        boolean shdn = true;
        int onlyOne = 0;   // numerous parms a mutually exclusive

        console.title("<-- The Pi4J V2 Project Extension  -->", "MCP4921App");
        String helpString = " parms:  -c HEX value chip select     -s HEX value SPI bus #  -vref float reference voltage  \n " +
            "-tb twelveBit  -vout float Vout  -shdn boolean true active  -b boolean buffered  -ga boolean gain 1x (default) \n" +
            "-tb and -vout mutually exclusive";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
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
                if (twelveBit > 4096) {
                    console.println("-tb cannot exceed 4096");
                    System.exit(40);
                }
                onlyOne ++;
            } else if (o.contentEquals("-ga")) { // pin
                String a = args[i + 1];
                i++;
                ga2x = Boolean.parseBoolean(a);
            } else if (o.contentEquals("-b")) { // pin
                String a = args[i + 1];
                i++;
                buffered = Boolean.parseBoolean(a);
            } else if (o.contentEquals("-shdn")) {
                String a = args[i + 1];
                i++;
                shdn = Boolean.parseBoolean(a);
            } else if (o.contentEquals("-vout")) { // reference voltage
                String a = args[i + 1];
                i++;
                setVout = true;
                vout = Float.parseFloat(a);
                onlyOne ++;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(42);
            }
        }

        if (onlyOne > 1 ) {
            console.println(" mutually exclusive parms used.");
            console.println(helpString);
            System.exit(43);
        }

        if ((setVout) && (vref == 0.0) ) {
            console.println("  -vref is zero  ");
            System.exit(43);
        }

        if (setVout && setTwelveBit) {
            console.println("  -vout and -tb both provided, illegal combination ");
            System.exit(44);
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
        Mcp4921 mcpDrv = new Mcp4921(spi);


        if (setVout) {
            mcpDrv.writeTwelvePerVoltage(vout, vref, buffered, ga2x, shdn);
        } else {
            mcpDrv.writeTwelve(twelveBit, buffered, ga2x, shdn);
        }


        if (spi != null) {
            spi.close();
        }

    }


}

