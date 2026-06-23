/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23008App.java
 *  *   *
 *  *   * This file is part of the Pi4J project. More information about
 *  *   * this project can be found here:  https://pi4j.com/
 *  *   * **********************************************************************
 *    * %%
 *  *   * Copyright (C) 2012 - 2021 Pi4J
 *     * %%
 *    * Licensed under the Apache License, Version 2.0 (the "License");
 *    * you may not use this file except in compliance with the License.
 *    * You may obtain a copy of the License at
 *    *
 *    *      http://www.apache.org/licenses/LICENSE-2.0
 *    *
 *    * Unless required by applicable law or agreed to in writing, software
 *    * distributed under the License is distributed on an "AS IS" BASIS,
 *    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    * See the License for the specific language governing permissions and
 *    * limitations under the License.
 *    * #L%
 *  *
 *  *
 *
 *
 */

package com.pi4j.devices.mcp23008;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.expander.ConfigurableIoExpander;
import com.pi4j.drivers.io.expander.mcp23008.Mcp23008Driver;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;


public class Mcp23008AppArg {

    private static final int DEFAULT_ADDRESS = 0x27;
    private static final int DEFAULT_BUS = 0x1;

    /**
     * <p>
     * Invoke various methods on MCP23008 instance
     * </p>
     *
     * @param args user params
     * @throws Exception
     */
    static void main(String[] args) {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();

        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23008App");


        int busNum = DEFAULT_BUS;
        int address = DEFAULT_ADDRESS;
        int intr_gpio = 27;
        int reset_gpio = 13;
        boolean drivePinHigh = false;
        boolean drivePinL = false;
        boolean drivePinH = false;
        boolean doReset = false;
        boolean setInterrupt = false;
        boolean createInPin = false;
        boolean createOutPin = false;

        boolean polarity = false;

        int theRPin = 0;
        int theIntPin = 0;
        int theOutPin = 0;
        int theInPin = 0;
        int theDrivePinH = 0;
        int theDrivePinL = 0;

        boolean readPin = false;

        DigitalOutput resetPinGpio = null;
        DigitalInput interruptPinGpio = null;

        Mcp23008Driver.InterruptMode iMode = null;
        console.title("<-- The Pi4J V2 Project Extension  -->", "Mcp23008App");
        String helpString = " parms: MCP23008   -b hex value bus -a hex value address  \n  " +
            "  -r read_pin pin#  -dH drive_pin_high pin#   -dL drive_pin_low pin#  -do_reset \n" +
            " -cI create input pin # pull true/false       -cO create output pin # \n" +
            " -sIntr setInterrupt pin#  mode (OFF, ON_0,  ON_1, ON_CHANGE) \n " +
            "   -reset_gpio #    -intr_gpio #    ";
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
            } else if (o.contentEquals("-r")) {
                String a = args[i + 1];
                theRPin = Integer.parseInt(a);
                readPin = true;
                i++;
            } else if (o.contentEquals("-dH")) {
                String a = args[i + 1];
                drivePinH = true;
                drivePinHigh = true;
                theDrivePinH = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-dL")) {
                String a = args[i + 1];
                drivePinL = true;
                drivePinHigh = false;
                theDrivePinL = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-cI")) {
                String a = args[i + 1];
                createInPin = true;
                theInPin = Integer.parseInt(a);
                i++;
                a = args[i + 1];
                polarity = Boolean.parseBoolean(a);
                i++;
            } else if (o.contentEquals("-cO")) {
                String a = args[i + 1];
                createOutPin = true;
                theOutPin = Integer.parseInt(a);
                i++;
            }//  -sI setInterrupt pin#  mode (OFF, ON_0,  ON_1, ON_CHANGE)
            else if (o.contentEquals("-sIntr")) {
                String a = args[i + 1];
                setInterrupt = true;
                theIntPin = Integer.parseInt(a);
                i++;
                a = args[i + 1];
                iMode = Mcp23008Driver.InterruptMode.valueOf(a.toUpperCase());
                i++;
            }  else if (o.contentEquals("-intr_gpio")) {
                String a = args[i + 1];
                intr_gpio = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-reset_gpio")) {
                String a = args[i + 1];
                reset_gpio = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-do_reset")) {
                doReset = true;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(42);
            }
        }

        // create an I2C to the <MCP23008.
        I2C mcpDev = createI2cDevice(pi4j, busNum, address);
        // Create the Mcp23008Driver passing the MCP23008 I2C device
        Mcp23008Driver mcpDriver = new Mcp23008Driver(mcpDev);


        var resetConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id("reset_gpio")
            .name("reset_gpio")
            .bcm(reset_gpio)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH);
        resetPinGpio = pi4j.create(resetConfig);

            // MCP23008 pin 8, indicate interrupt from MCP23008

        var inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
            .id("Interrupt")
            .name("Interrupt")
            .bcm(intr_gpio)
            .pull(PullResistance.PULL_DOWN);
        interruptPinGpio = pi4j.create(inputConfig1);

        // The MCP23008 Interrupt pin is connected to GPIO intr_gpio
        interruptPinGpio.addListener(new GpioListener(mcpDriver, console));


        // reset the MCP23008 device. This will set all the MCP23008 registers to
        // their POR values. In a non-test environment you would avoid a reset
        // at it essentially erase any existing register configurations.
        if (doReset) {
            resetChip(resetPinGpio);
        }

        if (createInPin) {
            mcpDriver.setIoDirection(theInPin, ConfigurableIoExpander.Direction.INPUT);
            mcpDriver.setPullupResistorConfiguration(theInPin, polarity);
        }

        if (createOutPin) {
            mcpDriver.setIoDirection(theOutPin, ConfigurableIoExpander.Direction.OUTPUT);
        }
        if (setInterrupt) {
            mcpDriver.setInterruptMode(theIntPin, iMode);
            mcpDriver.getInterruptCapture(); // Clear out any interrupt indications
        }

        if (drivePinH) {
            // Drive pin 0 high
            drivePin(mcpDriver, theDrivePinH, drivePinHigh);
        }

        if (drivePinL) {
            // Drive pin 0 high
            drivePin(mcpDriver, theDrivePinL, drivePinHigh);
        }

        if (readPin) {
            console.println("Expect 1 Read Pin " + theRPin + "   value  " + readOnePin(mcpDriver, theRPin, console));
        }

        // Shutdown Pi4J
        pi4j.shutdown();
    }



    /*      pin0 output  +         LED
            pin1 output            pin7
            pin7 input   pullDown  pin1


            GPIO27 has a listener    Wired to INT pin
            Listener drive GPIO18 high, gpio connected to LED that will flash ON
            */



    private static I2C createI2cDevice(Context pi4j, int bus, int address) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(bus)
            .device(address)
            .id(id + " " + name)
            .name(name)
            .build();
        return pi4j.create(i2cDeviceConfig);
    }

    private static void drivePin(Mcp23008Driver drvr, int pin, boolean drivePinHigh) {

        drvr.setOutputState(pin, drivePinHigh);
    }

    private static int readOnePin(Mcp23008Driver drv, int pin, Console console) {

        boolean stateHigh = drv.getInputState(pin);
        console.println(" readOnePin : Pin " + pin + "  state HIGH ?  " + stateHigh);
        return (stateHigh ? 1 : 0);
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

        public GpioListener(Mcp23008Driver drvr, Console consoleParm) {
            console = consoleParm;
            mcpDrv = drvr;
            console.println("Listener CTOR ");
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.LOW) {
                console.println("onDigitalStateChange Pin went low");
            } else if (event.state() == DigitalState.HIGH) {
                console.println("onDigitalStateChange Pin went high");
                waitMS(3000);
            } else {
                System.out.println("Strange event state  " + event.state());
                waitMS(3000);
            }
        }
    }


}
