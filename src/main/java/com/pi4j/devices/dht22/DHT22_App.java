

package com.pi4j.devices.dht22;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.util.Console;

public class DHT22_App {

    public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        int dataPinNum = 0xff;
        String traceLevel = "info";
        console.title("<-- The Pi4J V2 Project Extension  -->", "DHT22_App");
        String helpString = " parms:  -d  data GPIO number  -t trace " +
            " \n -t  trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
            " or \"off\"  Default \"info\"";


        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-d")) {
                String a = args[i + 1];
                dataPinNum = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-t")) {
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
        DHT22 sensor = new DHT22(pi4j, console, dataPinNum, traceLevel);
        sensor.readAndDisplayData();

        // console.waitForExit();

        pi4j.shutdown();
    }
}

