

package com.pi4j.devices.mcp3008;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.ad.mcp300x.Mcp3008Driver;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;

public class MCP3008App {
    public MCP3008App() {
        super();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "TRACE");
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        boolean doAll = true;
        short pinNumber = 0;

        double vref = 0;

        short chipSelect = 0;
        SpiBus spiBus = SpiBus.BUS_0;

        console.title("<-- The Pi4J V2 Project Extension  -->", "MCP3008App");
        String helpString = " parms: -p HEX value pinToRead (0-7)  <if not supplied all pins read  " +
            "  -c HEX value chip select   -vref float reference voltage " +
            "-s HEX value SPI # ";

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
                chipSelect = Short.parseShort(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-s")) { // pin
                String a = args[i + 1];
                spiBus = SpiBus.getByNumber(Short.parseShort(a.substring(2), 16));
                i++;
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

//        MCP3008 spiCls = new MCP3008(pi4j, spiBus, chipSelect, pinCount, console, traceLevel, vref);
        var spiConfig = Spi.newConfigBuilder(pi4j)
            .id("SPI" + spiBus + " " + chipSelect)
            .name("A/D converter")
            .bus(spiBus)
            .channel((int)chipSelect)
            .baud(Spi.DEFAULT_BAUD)
            .mode(SpiMode.MODE_0)
            .build();
        Spi spi = pi4j.create(spiConfig);
        Mcp3008Driver mcpDrv = new com.pi4j.drivers.io.ad.mcp300x.Mcp3008Driver(spi) ;

        int val;
        if(doAll){
            for ( short c = 0; c < 8 ; c++){
                val = mcpDrv.readChannel(c);
                getConversionValue(c, val, vref, console);
                Thread.sleep(150      );
            }
        }else {
            val = mcpDrv.readChannel(pinNumber);
            getConversionValue(pinNumber, val, vref, console);
        }


    }

    /**
     * Communicate to the ADC chip via SPI to get single-ended conversion value
     * for a specified channel.
     * @param channel to process
     * @param  val  obtained from chip
     * @param  vref   reference voltage
     * @param console
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public static double getConversionValue(short channel, int val, double vref, Console console) throws IOException {

        double rval = 0.0;
        if (vref > 0) {
            rval = ((val * vref) / 1024 );
            console.println("Channel : " + channel  + " A/D read input voltage : " + rval + " \n");
        }else{
            rval = val;
            console.println("Channel : " + channel + " read : " + rval + " \n");
        }
        return rval ;
    }
}
