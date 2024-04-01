package com.pi4j.devices.mcp3008;

import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.spi.Spi;
import com.pi4j.io.spi.SpiBus;
import com.pi4j.io.spi.SpiChipSelect;
import com.pi4j.io.spi.SpiMode;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCP3008 {


    public MCP3008(Context pi4j, SpiBus spiBus, SpiChipSelect chipSelect, short pinCount, Console console, String traceLevel, double vref) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.chipSelect = chipSelect;
        this.spiBus = spiBus;
        this.pinCount = pinCount;
        this.traceLevel = traceLevel;
        this.vref = vref;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + MCP3008.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(MCP3008.class);
        this.init();

    }

    private void init(){
        var spiConfig = Spi.newConfigBuilder(pi4j)
                .id("SPI" + spiBus + " " + chipSelect)
                .name("A/D converter")
                .bus(spiBus)
                .chipSelect(chipSelect)
                .flags(0b0000000000000000000000L)
                .baud(Spi.DEFAULT_BAUD)
                .mode(SpiMode.MODE_0)
                .provider("linuxfsspi")
                .build();
        this.spi = this.pi4j.create(spiConfig);

    }
    public void displayProgramID() {
        // print program title/header
        this.logger.trace(">>> Enter displayProgramID");
        console.title("<-- The Pi4J Project -->", "SPI test program using MCP3008 AtoD Chip");
        this.logger.trace("<<< Exit displayProgramID");

    }

    public void displayMCP3008State(boolean allChannels, short channel) throws InterruptedException, IOException {
        this.channel = channel;
        this.doallChannels = allChannels;
        // allow for user to exit program using CTRL-C
        console.promptForExit();
        if (this.doallChannels) {
            this.logger.trace(">>> Enter displayMCP3008State  allChannels  : " + allChannels);
        } else {
            this.logger.trace(">>> Enter displayMCP3008State  channel  : " + this.channel);
        }


        // This SPI example is using the Pi4J SPI interface to communicate with
        // the SPI hardware interface connected to a MCP3004/MCP3008 AtoD Chip.
        //
        // Please make sure the SPI is enabled on your Raspberry Pi via the
        // raspi-config utility under the advanced menu option.
        //
        // see this blog post for additional details on SPI and WiringPi
        // http://wiringpi.com/reference/spi-library/
        //
        // see the link below for the data sheet on the MCP3004/MCP3008 chip:
        // http://ww1.microchip.com/downloads/en/DeviceDoc/21294E.pdf


        // continue running program until user exits using CTRL-C
        while (console.isRunning()) {
            read(this.doallChannels, this.channel);
            Thread.sleep(1000);
        }
        console.emptyLine();
        this.logger.trace("<<< Exit displayMCP3008State");
    }

    /**
     * Read data via SPI bus from MCP3008 chip.
     *
     * @throws IOException
     */
    public void read(boolean doallChannels, short thisChannel) throws IOException, InterruptedException {
        this.logger.trace(">>> Enter read ");
        if (doallChannels) {
            for (short channel = 0; channel < this.pinCount; channel++) {
                int conversion_value = getConversionValue(channel);
                this.logger.trace("Channel  :" + channel + "  value  :" + String.format(" | %04d", conversion_value)); // print
                Thread.sleep(500);
                // 4
                // digits
                // with
                // leading
                // zeros
            }
        } else {
            int conversion_value = getConversionValue(thisChannel);
            this.logger.trace("Channel  :" + channel + "  value  :" + String.format(" | %04d", conversion_value)); // print
        }
        this.logger.trace(" |\r");
        Thread.sleep(1000);
        this.logger.trace("<<< Exit read");

    }

    /**
     * Communicate to the ADC chip via SPI to get single-ended conversion value
     * for a specified channel.
     *
     * @param channel analog input channel on ADC chip
     * @return conversion value for specified analog input channel
     * @throws IOException
     */
    public int getConversionValue(short channel) throws IOException {
        this.logger.trace(">>> Enter getConversionValue  channel : " + channel);

        // create a data buffer and initialize a conversion request payload
        byte data[] = new byte[]{(byte) 0b00000001, // first byte, start bit
                (byte) (0b10000000 | (((channel & 7) << 4))), // second byte
                // transmitted
                // -> (SGL/DIF =
                // 1,
                // D2=D1=D0=0)
                (byte) 0b00000000 // third byte transmitted....don't care
        };

        // send conversion request to ADC chip via SPI channel
        //int bytesWritten = this.spi.write(data);
        byte value[] = new byte[3];
        int bytesRead = this.spi.transfer(data, 0, value, 0, 3);


        // calculate and return conversion value from result bytes
        int result = (value[1] << 8) & 0b1100000000; // merge value[1] & value[2]
        // to get 10-bit result
        result |= (value[2] & 0xff);
        this.logger.info("Channel : " + channel + "   Bytes read : " + bytesRead + "  Value : " + result);
        if(this.vref > 0){this.logger.info("A/D read input voltage : "+  ((result * this.vref )/1024 + " \n"));
        }
        this.logger.trace("<<< Exit getConversionValue ");

        return result;
    }

    // SPI device
    //  public SpiDevice spi;

    // ADC channel count
    public short pinCount; // MCP3004=4, MCP3008=8 move to chip config
    // file
    private boolean doallChannels = false;
    private short channel;
    private Spi spi;
    private Console console;
    private final String traceLevel;
    private final Logger logger;

    private final double vref;
    private final SpiChipSelect chipSelect;
    private final SpiBus spiBus;

    private Context pi4j;

}

