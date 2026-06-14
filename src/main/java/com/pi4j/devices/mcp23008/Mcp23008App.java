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
import com.pi4j.io.ListenableOnOffRead;
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
    private static int mcpPinWIre = 4;
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
        boolean resetMCP = false;
        boolean drivePinHigh = false;
        boolean drivePin = false;
        boolean readPin = false;


        console.title("<-- The Pi4J V2 Project Extension  -->", "HD44780U_App");
        String helpString = " parms: HD44780U   -b hex value bus -a hex value address  \n  " +
            "  -r read-pin #  -sH set_pin_high #   -sL set_pin_low #  -x reset MCP \n";

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
                mcpPinR = Integer.parseInt(a);
                readPin = true;
                i++;
            } else if (o.contentEquals("-sH")) {
                String a = args[i + 1];
                drivePin = true;
                drivePinHigh = true;
                mcpPinW = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-sL")) {
                String a = args[i + 1];
                drivePin = true;
                drivePinHigh = false;
                mcpPinW = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-x")) {
                resetMCP = true;
            } else if (o.contentEquals("-int_pin")) {
                String a = args[i + 1];
                int_Pin = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-int_pin")) {
                String a = args[i + 1];
                int_Pin = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-rest_pin")) {
                String a = args[i + 1];
                reset_Pin = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-int_led")) {
                String a = args[i + 1];
                int_Led = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-test_pin")) {
                String a = args[i + 1];
                test_pin = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-do_reset")) {
                resetMCP = true;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(43);
            }
        }


        console.println("----------------------------------------------------------");
        console.println("PI4J PROVIDERS");
        console.println("----------------------------------------------------------");
     //   pi4j.providers().describe().print(System.out);
        console.println("----------------------------------------------------------");


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
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH);
        DigitalOutput gpioToPin = pi4j.create(testConfig);

        var testConfigIntr = DigitalOutput.newConfigBuilder(pi4j)
            .id("INTR_triggered")
            .name("INTR_triggered")
            .bcm(int_Led)
            .shutdown(DigitalState.HIGH)
            .initial(DigitalState.HIGH);
        DigitalOutput intr_happened = pi4j.create(testConfigIntr);


        var inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
            .id("Interrupt")
            .name("Interrupt")
            .bcm(int_Pin)
            .pull(PullResistance.PULL_DOWN);
        DigitalInput interruptPin = pi4j.create(inputConfig1);


        I2C mcpDev = createI2cDevice(pi4j, busNum, address);

        Mcp23008Driver mcpDriver = new Mcp23008Driver(mcpDev);

        interruptPin.addListener(new GpioListener(mcpDriver, console));
        // clear any outstanding interrupts
        mcpDriver.getInterruptCapture();


        if (resetMCP) {
            resetChip(resetPin);
        }


        // Configure the pins
        configureMCP(mcpDriver, console);

        console.print("Chip register configurations completed");


        console.println("LED off ??") ;
        waitMS(3000);

        if (drivePin) {
            drivePin(mcpDriver, mcpPinLed,  drivePinHigh);
        }

        console.println("LED on ??") ;
        waitMS(3000);


        mcpDriver.getInterruptCapture();

        console.println("Expect 0 Read Pin " + mcpPinR  +  "   value  "  + readOnePin(mcpDriver, mcpPinR)  ) ;

        drivePin(mcpDriver, mcpPinW, true );

        waitMS(3000);


       if (readPin) {
            console.println("Expect 1 Read Pin " + mcpPinR  +  "   value  "  + readOnePin(mcpDriver, mcpPinR)  ) ;
        }




        int whichPin =   mcpDriver.getInterruptCapture();
        String intcap =  String.format("%8s", Integer.toBinaryString(whichPin)).replace(" ","0");
        console.println("INTF register b" + intcap) ;


        pinInterrupted(mcpDriver, console);

        gpioToPin.high();   // gpio connected to MCP pin  mcpPinGpio

        console.println("Expect 1 Read Pin " + mcpPinGpio  +  "   value  "  + readOnePin(mcpDriver, mcpPinGpio)  ) ;



        console.println("move jumper between 3.3v and ground") ;
        waitMS(300000);



        // Shutdown Pi4J
        pi4j.shutdown();
    }

    /*      pin0 output  +         LED
            pin1 output            pin7
            pin3 input   pullup    gpio16
            pin4 Input   PullUp    jumper
            pin7 input   pullDown  pin1


            GPIO27 has a listener    Wired to INT pin
            */

    private static void configureMCP(Mcp23008Driver mcpDriver, Console console) {
        mcpDriver.setIoDirection(mcpPinW, ConfigurableIoExpander.Direction.OUTPUT);

        mcpDriver.setIoDirection(mcpPinLed, ConfigurableIoExpander.Direction.OUTPUT);

        mcpDriver.setIoDirection(mcpPinGpio, ConfigurableIoExpander.Direction.INPUT);

        mcpDriver.setPullupResistorConfiguration(mcpPinGpio, true );

        mcpDriver.setIoDirection(mcpPinWIre, ConfigurableIoExpander.Direction.INPUT);
        mcpDriver.setPullupResistorConfiguration(mcpPinWIre, true );

        // set interrupt enabled
        mcpDriver.setInterruptMode(4, Mcp23008Driver.InterruptMode.ON_0);


        mcpDriver.setIoDirection(mcpPinR, ConfigurableIoExpander.Direction.INPUT);
        mcpDriver.setPullupResistorConfiguration(7, false);


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

    private static int   readOnePin(Mcp23008Driver drv, int pin ) {
        return (drv.getInputState(pin) ? 1 : 0 );
    }

    public  static  int pinInterrupted(Mcp23008Driver drv , Console console){
        // Examine INTCAP for which pin caused the interrupt
       int rval = 0xff;
        int flags = drv.getInterruptCapture();
        String intcap =  String.format("%8s", Integer.toBinaryString(flags)).replace(" ","0");
        console.println("INTF register b" + intcap) ;

        for(int c = 0; c<8; c++){
            if ((flags & (1 << c) ) > 0){
                rval = c;
            }
        }
        return rval;
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
            console=consoleParm ;
            mcpDrv = drvr;
            console.println("Listener CTOR ") ;
           }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            if (event.state() == DigitalState.LOW) {
                console.println("onDigitalStateChange Pin went low");
            } else if (event.state() == DigitalState.HIGH) {
                console.println("onDigitalStateChange Pin went high");
            } else {
                System.out.println("Strange event state  " + event.state());
            }
            int whichPin = mcpDrv.getInterruptCapture();
           String intcap =  String.format("%8s", Integer.toBinaryString(whichPin)).replace(" ","0");
           console.println("INTF register b" + intcap) ;
        }
    }


}
