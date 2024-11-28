package com.pi4j.devices.is31Fl37Matrix;

import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.util.Console;

public class CreateInterrupt {


    /**
     * <p>Constructor for CreateInterrupt.</p>
     * <p>
     * Sole purpose is unit testing.  Pi4 with Pi OS 64bit
     * missing short duration interrupt. This program allows
     * controlling the interrupt duration.
     */
    public CreateInterrupt() {
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    public static void main(String[] args) throws Exception {

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate stdin/stdout code)
        final var console = new Console();

        // print program title/header
        console.title("<-- The Pi4J Project -->", "Generate interrupt through a  GPIO");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // Initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        var pi4j = Pi4J.newAutoContext();
        int interruptPinNum = 43;
        String firstDirection = "";
        int countLoops = 120;
        DigitalOutput interruptPin = null;
        int milliSecDelay = 42;
        DigitalState initialState = DigitalState.UNKNOWN;
        boolean interruptLevelLow = true;  // if true, interrupt condition requires drive.low level, else drive.high level
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-p")) {
                String a = args[i + 1];
                interruptPinNum = Integer.parseInt(a);
                console.println("interruptPin  " + interruptPinNum);
                i++;
            } else if (o.contentEquals("-c")) {
                String a = args[i + 1];
                countLoops = Integer.parseInt(a);
                console.println("Process  " + countLoops + " half second loops");
                i++;
            } else if (o.contentEquals("-m")) {
                String a = args[i + 1];
                milliSecDelay = Integer.parseInt(a);
                console.println("MilliSecond  " + countLoops + " interrupt duration");
                i++;
            } else if (o.contentEquals("-d")) { // mainChip
                firstDirection = args[i + 1];
                if (firstDirection.contentEquals("HIGH") | firstDirection.contentEquals("LOW")) {
                    i++;
                    if (firstDirection.contentEquals("LOW")) {
                        initialState = DigitalState.HIGH;
                        interruptLevelLow = true;
                    } else {
                        initialState = DigitalState.LOW;
                        interruptLevelLow = false;
                    }
                } else {
                    console.println("Parms -p pin to interrupt, -c count half second loops, -m MS interrupt duration, -d first direction interrupt <HIGH, LOW>");
                }
            } else {
                console.println("INVALID Parms -p pin to monitor interrupt, -c count half second loops,  -m MS interrupt duration, -d first direction interrupt <HIGH, LOW>");

            }
        }


        var interruptConfig = DigitalOutput.newConfigBuilder(pi4j)
            .id("Interrupt")
            .name("Interrupt")
            .address(interruptPinNum)
            .shutdown(initialState)
            .initial(initialState)
            .provider("gpiod-digital-output");
        try {
            interruptPin = pi4j.create(interruptConfig);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut interrupt line failed");
            System.exit(202);
        }
        interruptPin.addListener(new MonitorInterrupt.MatrixGpioListener());


        console.println();
        console.println("CHANGE OUTPUT STATES VIA I/O HARDWARE AND CHANGE EVENTS WILL BE PRINTED BELOW:");

        /*for(int c = 0; c < countLoops; c++){
            Thread.sleep(500);
        }*/
        // wait (block) for user to exit program using CTRL-C
        if (interruptLevelLow) {
            console.println("Interrupt LOW wait " + milliSecDelay + "MS, then HIGH");
            interruptPin.low();
            Thread.sleep(milliSecDelay);
            interruptPin.high();
        } else {
            console.println("Interrupt HIGH wait " + milliSecDelay + "MS, then LOW");
            interruptPin.high();
            Thread.sleep(milliSecDelay);
            interruptPin.low();
        }
        console.waitForExit();

        // shutdown Pi4J
        console.println("ATTEMPTING TO SHUTDOWN/TERMINATE THIS PROGRAM");
        pi4j.shutdown();
    }

    public static class MatrixGpioListener implements DigitalStateChangeListener {

        public MatrixGpioListener() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("MatrixGpioListener: Performing ctl-C shutdown");
                    // Thread.dumpStack();
                }
            });
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            // display pin state on console
            // system.out.println(" Matrix -->Utility : GPIO PIN STATE CHANGE: "
            // + event.getPin() + " = " + event.getState());

            if (event.state() == DigitalState.LOW) {
                System.out.println("onDigitalStateChange Pin went low");
            }
            if (event.state() == DigitalState.HIGH) {
                System.out.println("onDigitalStateChange Pin went high");
            }

        }
    }
}
