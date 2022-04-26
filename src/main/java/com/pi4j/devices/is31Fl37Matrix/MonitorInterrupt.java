package com.pi4j.devices.is31Fl37Matrix;



import com.pi4j.Pi4J;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.util.Console;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

/**
 * <p>MonitorInterrupt class.</p>
 * This is coded specifically to the manner in which the IS31FL3731 chip
 * toggles the INTB pin.  Used in debugging the Pi OS missing short
 * duration level changes on a GPIO
 *        It Is NOT a generic monitor program.
 *
 * Sole purpose is unit testing.  Pi4 with Pi OS 64bit
 * missing short duration interrupt. This program allows
 * measuring the interrupt duration.
 *   is31Fl37Matrix.MonitorInterrupt -p 25 -d DOWN
 */
public class MonitorInterrupt {


    /**
     * <p>Constructor for MonitorInterrupt.</p>
     *
     */
    public MonitorInterrupt() {
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
        console.title("<-- The Pi4J Project -->", "Monitor Matrix interrupt GPIO");

        // allow for user to exit program using CTRL-C
        console.promptForExit();

        // Initialize Pi4J with an auto context
        // An auto context includes AUTO-DETECT BINDINGS enabled
        // which will load all detected Pi4J extension libraries
        // (Platforms and Providers) in the class path
        var pi4j = Pi4J.newAutoContext();
        int monitorPinNum = 43;
        String pullDirection = "";
        int countLoops = 120;
        DigitalInput monitorPin = null;

        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-p")) {
                String a = args[i + 1];
                monitorPinNum = Integer.parseInt(a);
                console.println("monitorPin  " + monitorPinNum);
                i++;
            } else if (o.contentEquals("-cccccc")) {
                String a = args[i + 1];
                countLoops = Integer.parseInt(a);
                console.println("Process  " + countLoops + " half second loops");
                i++;
            } else if (o.contentEquals("-d")) { // mainChip
                pullDirection = args[i + 1];
                if (pullDirection.contentEquals("UP") | pullDirection.contentEquals("DOWN") | pullDirection.contentEquals("OFF")) {
                    i++;
                } else {
                    console.println("Parms -p pin to monitor interrupt, -d direction of pull <UP DOWN OFF>");
                }
            } else {
                console.println("INVALID Parms -p pin to monitor interrupt, -d direction of pull <UP DOWN OFF>");

            }
        }

        // create a properties map with ".address" and ".shutdown" properties for the digital output configuration
        Properties properties = new Properties();
        properties.put("id", "GPIO monitor");
        properties.put("address", monitorPinNum);
        properties.put("pull", pullDirection);
        properties.put("name", "GPIO-MONITOR");

        // create a digital input instance using the default digital input provider
        // we will use the PULL_DOWN argument to set the pin pull-down resistance on this GPIO pin
        var config = DigitalInput.newConfigBuilder(pi4j)
                .load(properties)
                .build();

        var input = pi4j.din().create(config);

        var ledConfigIntr = DigitalInput.newConfigBuilder(pi4j)
                .id("MatrixInterrupt")
                .name("MatrixInterrupt")
                .address(monitorPinNum)
                .pull(PullResistance.PULL_UP)
                .provider("pigpio-digital-input");
        try {
            monitorPin = pi4j.create(ledConfigIntr);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigitalIn failed");
            System.exit(200);
        }

        monitorPin.addListener(new MatrixGpioListener());


        // lets read the digital output state
        console.print("DIGITAL INPUT [");
        console.print(input);
        console.print("] STATE IS [");
        console.println(input.state() + "]");

        console.print("DIGITAL INPUT [");
        console.print(input);
        console.print("] PULL RESISTANCE IS [");
        console.println(input.pull() + "]");

        console.println();
        console.println("CHANGE INPUT STATES VIA I/O HARDWARE AND CHANGE EVENTS WILL BE PRINTED BELOW:");

        /*for(int c = 0; c < countLoops; c++){
            Thread.sleep(500);
        }*/
        // wait (block) for user to exit program using CTRL-C
        console.waitForExit();
        monitorPin.shutdown(pi4j);
        // shutdown Pi4J
        console.println("ATTEMPTING TO SHUTDOWN/TERMINATE THIS PROGRAM");
        pi4j.shutdown();
    }

    public static class MatrixGpioListener implements DigitalStateChangeListener {

        Instant startInstant;
        Instant endInstant;
          public MatrixGpioListener() {
           /* Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("MatrixGpioListener: Performing ctl-C shutdown");
                    // Thread.dumpStack();
                }
            });
            */
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            // display pin state on console
            // system.out.println(" Matrix -->Utility : GPIO PIN STATE CHANGE: "
            // + event.getPin() + " = " + event.getState());
            long start = 0;
            long end = 0;
            if (event.state() == DigitalState.LOW) {
                start = System.currentTimeMillis();
                startInstant = Instant.now();
                System.out.println("onDigitalStateChange Pin went low");
            }else if (event.state() == DigitalState.HIGH) {
                end = System.currentTimeMillis();
                Instant endInstant = Instant.now();
                System.out.println("onDigitalStateChange Pin went high");
                long elapsedTime = end - start;
                System.out.println("Elapsed time MS " + elapsedTime);
                Duration timeElapsed = Duration.between(startInstant, endInstant);
                System.out.println("timeElapsed time MS " + timeElapsed.toMillis());
            }else{
                System.out.println("Strange event state  " + event.state());
            }
        }
    }
}

