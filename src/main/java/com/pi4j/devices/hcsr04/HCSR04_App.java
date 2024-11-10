/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  HCSR04_App.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2024 Pi4J
 *      * %%
 *     *
 *     * Licensed under the Apache License, Version 2.0 (the "License");
 *     * you may not use this file except in compliance with the License.
 *     * You may obtain a copy of the License at
 *     *
 *     *      http://www.apache.org/licenses/LICENSE-2.0
 *     *
 *     * Unless required by applicable law or agreed to in writing, software
 *     * distributed under the License is distributed on an "AS IS" BASIS,
 *     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     * See the License for the specific language governing permissions and
 *     * limitations under the License.
 *     * #L%
 *     *
 *
 *
 *
 */

package com.pi4j.devices.hcsr04;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.util.Console;

/**
 * A simple application to test the HC-SR04 ultrasonic sensor by repeatedly measuring
 * and printing the distance to the console.
 *
 * <p>This application uses the Pi4J library to interact with the GPIO pins on
 * a Raspberry Pi, and it outputs distance measurements in centimeters every second.
 */
public class HCSR04_App {

    // Default GPIO pins for the HC-SR04 sensor
    private static final int DEFAULT_TRIG = 23;
    private static final int DEFAULT_ECHO = 24;

    /**
     * Main method to initialize the Pi4J context, configure the HC-SR04 sensor, and
     * continuously measure and display the distance.
     *
     * @param args command line arguments for GPIO pins or help
     * @throws Exception if an error occurs during measurement
     */
    public static void main(String[] args) throws Exception {
        // Create Pi4J console wrapper/helper
        final var console = new Console();

        // Check for help parameter
        if (args.length > 0 && args[0].equals("-h")) {
            printUsage(console);
            System.exit(0);
        }

        // Parse GPIO pins from command-line arguments if provided
        int trigPin = DEFAULT_TRIG;
        int echoPin = DEFAULT_ECHO;

        try {
            if (args.length > 0) {
                trigPin = Integer.parseInt(args[0]);
            }
            if (args.length > 1) {
                echoPin = Integer.parseInt(args[1]);
            }
        } catch (NumberFormatException e) {
            console.println("Invalid GPIO pin format. Please enter pins as integers.");
            printUsage(console);
            System.exit(1);
        }

        // Initialize Pi4J context
        Context pi4j = Pi4J.newAutoContext();

        // Initialize the HCSR04 sensor with specified GPIO pins
        HCSR04 hcsr04 = new HCSR04(pi4j, trigPin, echoPin);

        // Register a shutdown hook to release resources when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pi4j.shutdown();
            console.println("Application has been stopped. Pi4J resources released.");
        }));

        console.println("Starting HC-SR04 distance measurement application...");
        console.println("Using TRIG pin: " + trigPin + ", ECHO pin: " + echoPin);

        // Continuous loop to measure and display distance
        while (true) {
            try {
                double distance = hcsr04.measureDistance();
                if (distance >= 0) {
                    console.println(String.format("Distance: %.2f cm", distance));
                } else {
                    console.println("Out of range or invalid reading.");
                }
            } catch (Exception e) {
                console.println("Error during measurement: " + e.getMessage());
            }

            // Wait for 1 second before the next measurement
            Thread.sleep(1000);
        }
    }

    /**
     * Prints usage information for the program.
     *
     * @param console the console object for output
     */
    private static void printUsage(Console console) {
        console.println("Usage: java HCSR04_App [TRIG_PIN] [ECHO_PIN]");
        console.println("       java HCSR04_App -h");
        console.println("Options:");
        console.println("  [TRIG_PIN] [ECHO_PIN]  GPIO pins for TRIG and ECHO (default: 23 and 24)");
        console.println("  -h                     Show this help message");
    }
}
