package com.pi4j.devices.mcp3008;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.util.Console;

public class MCP3008App {
    public MCP3008App() {
        super();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        boolean doAll = true;
        short pinNumber = 0;

        double vref = 0;

        SpiChipSelect chipSelect = SpiChipSelect.CS_1;
        SpiBus spiBus = SpiBus.BUS_0;

        console.title("<-- The Pi4J V2 Project Extension  -->", "MCP3008App");
        String helpString = " parms: -p HEX value pinToRead  <if not supplied all pins read  " +
            "  -c HEX value chip select   -vref decimal reference voltage " +
            "-s HEX value SPI #  -t  trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
            " or \"off\"  Default \"info\"";

        String traceLevel = "info";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-p")) { // pin
                String a = args[i + 1];
                pinNumber = Short.parseShort(a.substring(2), 16);
                doAll = false;
                i++;
                if (pinNumber > 7) {
                    console.println("  !!! Invalid Pin " + pinNumber);
                    console.println(helpString);
                    System.exit(40);
                }
            } else if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
            } else if (o.contentEquals("-c")) { // pin
                String a = args[i + 1];
                chipSelect = SpiChipSelect.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            } else if (o.contentEquals("-s")) { // pin
                String a = args[i + 1];
                spiBus = SpiBus.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
            } else if (o.contentEquals("-t")) { // device address
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(41);
                }
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(43);
            }
        }


        short pinCount = 8;
        console.println("----------------------------------------------------------");
        console.println("PI4J PROVIDERS");
        console.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");

        MCP3008 spiCls = new MCP3008(pi4j, spiBus, chipSelect, pinCount, console, traceLevel, vref);

        spiCls.displayProgramID();
        spiCls.displayMCP3008State(doAll, pinNumber);

    }

}
