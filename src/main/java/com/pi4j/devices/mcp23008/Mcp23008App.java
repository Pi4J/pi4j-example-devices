

package com.pi4j.devices.mcp23008;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.expander.ConfigurableIoExpander;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;
import com.pi4j.drivers.io.expander.mcp23008.Mcp23008Driver;



public class Mcp23008App  {

    private static final int DEFAULT_ADDRESS = 0x27;
    private static final int DEFAULT_BUS = 0x1;
    private static int mcpPinW = 1;
    private static int mcpPinR = 7;
    private static int mcpPinGpio = 3;
    private static int mcpPinLed = 0;


    /**
     * <p>
     * Invoke various methods on MCP23008 instance
     * </p>
     *
     * @param args user params
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();

        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23008App");


        int busNum = DEFAULT_BUS;
        int address = DEFAULT_ADDRESS;
        int int_Pin = 27;
        int reset_Pin = 13;
        int test_pin = 16;
        int int_Led = 18;

        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23008App");
        String helpString = " parms: HD44780U   -b hex value bus -a hex value address ";

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
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(43);
            }
        }


        var resetConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id("Reset_Pin")
            .name("Reset_Pin")
            .bcm(reset_Pin)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH);
        DigitalOutput resetPin = pi4j.create(resetConfig);


        var testConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id("GpioToPin")
            .name("GpioToPin")
            .bcm(test_pin)
            .shutdown(DigitalState.LOW)
            .initial(DigitalState.LOW);
        DigitalOutput gpioToPin = pi4j.create(testConfig);

        var testConfigIntr = DigitalOutput.newConfigBuilder(pi4j)
            .id("INTR_triggered")
            .name("INTR_triggered")
            .bcm(int_Led)
            .shutdown(DigitalState.LOW)
            .initial(DigitalState.LOW);
        DigitalOutput intr_happened = pi4j.create(testConfigIntr);


        var inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
            .id("Interrupt")
            .name("Interrupt")
            .bcm(int_Pin)
            .pull(PullResistance.PULL_DOWN);
        DigitalInput interruptPin = pi4j.create(inputConfig1);

    // create an I2C to the <CP23008.
        I2C mcpDev = createI2cDevice(pi4j, busNum, address);
    // Create the Mcp23008Driver passing the MCP23008 I2C device
        Mcp23008Driver mcpDriver = new Mcp23008Driver(mcpDev);

        // The MCP23008 Interrupt pin is connected to GPIO 27
        interruptPin.addListener(new GpioListener(mcpDriver, console, intr_happened));

        // reset the MCP23008 device. This will set all the MCP23008 registers to
        // their POR values. In a non-test environment you would avoid a reset
        // at it essentially erase any existing register configurations.
       resetChip(resetPin);


        // Configure the pins. Sets the MCP23008 registers to define pins
        // direction, polarity interrupt characteristics etc.
        configureMCP(mcpDriver, console, mcpDev);

        console.print("Chip register configurations completed");

        // LED connected to pin 0
        console.println("LED off ??") ;
        waitMS(3000);
        // Drive pin 0 high
        drivePin(mcpDriver, mcpPinLed,  true);

         // LED should now be ON
        console.println("LED ON ??    value  "  + readOnePin(mcpDriver, mcpPinLed, console)) ;
        waitMS(3000);

        // output pin1 is connected to input pin 7
        console.println("Expect 0 Read Pin " + mcpPinR  +  "   value  "  + readOnePin(mcpDriver, mcpPinR, console)  ) ;
        // Drive output pin 1 HIGH
        console.println("Drive HIGH Pin " + mcpPinR);
        drivePin(mcpDriver, mcpPinW, true );

        waitMS(3000);

        // Input pin 7 should npw be HIGH
        console.println("Expect 1 Read Pin " + mcpPinR  +  "   value  "  + readOnePin(mcpDriver, mcpPinR, console)  ) ;


        // set interrupt enabled on MCP23008 pin 3 connected to GPIO 16
        // Selected mod will create interrupt on any pin change.  See the
        // MCP23008 datasheet for explanation of interrupt conditions.
        mcpDriver.setInterruptMode(mcpPinGpio, Mcp23008Driver.InterruptMode.ON_0);
        mcpDriver.getInterruptCapture(); // Clear out any interrupt indications

        console.println("Expect 0 Read Pin " + mcpPinGpio  +  "   value  "  + readOnePin(mcpDriver, mcpPinGpio, console)  ) ;

        console.println("Expect console message pertaining to onDigitalStateChange when gpioToPin set High \n " +
            " Also GPIO18 driven high " );
        gpioToPin.high();   // gpio connected to MCP pin  mcpPinGpio
        console.println("Expect 1 Read Pin " + mcpPinGpio  +  "   value  "  + readOnePin(mcpDriver, mcpPinGpio, console)  ) ;
        waitMS(3000);
        gpioToPin.low();
        console.println("Expect console message pertaining to onDigitalStateChange when gpioToPin set Low" );
        waitMS(3000);

        // Shutdown Pi4J
        pi4j.shutdown();
    }



    /*      pin0 output  +         LED
            pin1 output            pin7
            pin3 input   pullup    gpio16
            pin7 input   pullDown  pin1


            GPIO27 has a listener    Wired to INT pin
            Listener drive GPIO18 high, gpio connected to LED that will flash ON
            */

    private static void configureMCP(Mcp23008Driver mcpDriver, Console console, I2C debug) {

        mcpDriver.setIoDirection(mcpPinW, ConfigurableIoExpander.Direction.OUTPUT);

        mcpDriver.setIoDirection(mcpPinLed, ConfigurableIoExpander.Direction.OUTPUT);

        mcpDriver.setIoDirection(mcpPinGpio, ConfigurableIoExpander.Direction.INPUT);

        mcpDriver.setPullupResistorConfiguration(mcpPinGpio, true );

        mcpDriver.setIoDirection(mcpPinR, ConfigurableIoExpander.Direction.INPUT);

        mcpDriver.setPullupResistorConfiguration(mcpPinR, false);


    }


    private static I2C createI2cDevice(Context pi4j, int bus, int address) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(bus)
            .device(address)
            .id(id + " " + name)
            .name(name)
            .build();
        return  pi4j.create(i2cDeviceConfig);
    }

    private static void    drivePin(Mcp23008Driver drvr, int pin , boolean drivePinHigh) {

        drvr.setOutputState(pin,drivePinHigh);
     }

    private static int   readOnePin(Mcp23008Driver drv, int pin , Console console) {

        boolean stateHigh = drv.getInputState(pin);
        console.println(" readOnePin : Pin " + pin + "  state HIGH ?  " +stateHigh);
        return (stateHigh ? 1 : 0 );
    }


    public static void resetChip(DigitalOutput pin) {
        pin.low();
        waitMS(20);
        pin.high();
        waitMS(20); // allow time for reinit
    }


    public static void waitMS(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }
    }

    public static class GpioListener implements DigitalStateChangeListener {

       Console console;
       Mcp23008Driver mcpDrv;
        DigitalOutput intrIndicate;
        public GpioListener(Mcp23008Driver drvr, Console consoleParm, DigitalOutput outPin) {
            console=consoleParm ;
            mcpDrv = drvr;
            intrIndicate = outPin;
            console.println("Listener CTOR ") ;
           }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.LOW) {
                console.println("onDigitalStateChange Pin went low");
            } else if (event.state() == DigitalState.HIGH) {
                console.println("onDigitalStateChange Pin went high");
                waitMS(3000);
                intrIndicate.high();
            } else {
                System.out.println("Strange event state  " + event.state());
                waitMS(3000);
            }
        }
    }


}
