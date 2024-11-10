/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  HCSR04.java
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

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;

import java.time.Duration;

/**
 * A class to interface with the HC-SR04 ultrasonic distance sensor, allowing
 * distance measurements by calculating the time taken for an ultrasonic pulse
 * to travel to an object and back.
 *
 * <p>This class uses the Pi4J library to control the GPIO pins of a Raspberry Pi.
 * It configures one pin as a trigger (output) and another as an echo (input) to
 * measure distance based on the duration of the echo pulse.
 */
public class HCSR04 {
    private static final double SPEED_OF_SOUND = 342.25; // Speed of sound in meters per second at 18°C
    private static final long TRIG_SIGNAL_DURATION_NANOS = 10_000; // Trigger duration of 10 microseconds
    private static final Duration MIN_ECHO_DURATION = Duration.ofNanos(150_000); // Minimum valid echo duration
    private static final Duration MAX_ECHO_DURATION = Duration.ofMillis(25); // Maximum valid echo duration

    private final DigitalOutput trigPin;
    private final DigitalInput echoPin;

    /**
     * Constructs an HCSR04 object with the specified GPIO pin numbers for trigger and echo.
     *
     * @param pi4jContext the Pi4J context used to initialize GPIO pins
     * @param trigPinNumber the GPIO pin number used for the trigger signal
     * @param echoPinNumber the GPIO pin number used for the echo signal
     */
    public HCSR04(Context pi4jContext, int trigPinNumber, int echoPinNumber) {
        var trigConfig = DigitalOutput.newConfigBuilder(pi4jContext)
            .id("trigSignal")
            .name("Trig signal for HC-SR04")
            .address(trigPinNumber)
            .shutdown(DigitalState.LOW)
            .initial(DigitalState.LOW);
        this.trigPin = pi4jContext.create(trigConfig);

        var echoConfig = DigitalInput.newConfigBuilder(pi4jContext)
            .id("echoSignal")
            .name("Echo signal for HC-SR04")
            .address(echoPinNumber)
            .pull(PullResistance.PULL_DOWN);
        this.echoPin = pi4jContext.create(echoConfig);
    }

    /**
     * Alternative constructor that accepts pre-configured DigitalOutput and DigitalInput pins.
     *
     * @param trigPin the DigitalOutput instance for the trigger signal
     * @param echoPin the DigitalInput instance for the echo signal
     */
    public HCSR04(DigitalOutput trigPin, DigitalInput echoPin) {
        this.trigPin = trigPin;
        this.echoPin = echoPin;
    }

    /**
     * Measures the distance to an object by sending an ultrasonic pulse and measuring the
     * time taken for the pulse to return. The result is returned in centimeters.
     *
     * <p>This method sends a 10-microsecond pulse on the trigger pin and then measures
     * the duration of the echo pulse on the echo pin. The distance is calculated based
     * on the duration of the echo pulse.
     *
     * @return the calculated distance to the object in centimeters, or -1 if the
     *         measurement is invalid (out of range).
     */
    public double measureDistance() {
        try {
            // Send TRIG signal for 10 microseconds
            trigPin.high();
            Thread.sleep(0, (int) TRIG_SIGNAL_DURATION_NANOS); // Sleep for 10 microseconds
            trigPin.low();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return -1; // Exit measurement if interrupted
        }

        // Measure echo pulse duration
        long echoStartTime = waitForEchoSignal(true);
        long echoEndTime = waitForEchoSignal(false);

        // Calculate pulse duration
        long pulseDuration = echoEndTime - echoStartTime;

        // Check if pulse duration is within the expected range
        if (pulseDuration >= MIN_ECHO_DURATION.toNanos() && pulseDuration <= MAX_ECHO_DURATION.toNanos()) {
            return calculateDistance(echoStartTime, echoEndTime);
        } else {
            return -1; // Return -1 for out-of-range or invalid readings
        }
    }

    /**
     * Waits for the echo signal to either go high or low, depending on the specified parameter.
     *
     * <p>This method implements a busy-wait with a timeout to prevent infinite waiting.
     *
     * @param high {@code true} to wait for the echo signal to go high, {@code false} to wait for it to go low
     * @return the time in nanoseconds when the echo signal changed, or the current time if timed out
     */
    private long waitForEchoSignal(boolean high) {
        long timeoutStart = System.nanoTime();
        while ((echoPin.isHigh() != high) && Duration.ofNanos(System.nanoTime() - timeoutStart).compareTo(MAX_ECHO_DURATION) < 0) {
            // Busy-wait until signal changes or timeout
        }
        return System.nanoTime();
    }

    /**
     * Calculates the distance to an object based on the duration of the echo pulse.
     *
     * <p>This method uses the speed of sound to convert the measured pulse duration
     * (from the HC-SR04 sensor) into a distance. The calculation is done in
     * centimeters and accounts for the round-trip of the signal (to the object and back).
     *
     * @param pulseStartTime the start time of the echo pulse in nanoseconds
     * @param pulseEndTime the end time of the echo pulse in nanoseconds
     * @return the calculated distance to the object in centimeters, or -1 if the
     *         pulse duration is outside expected bounds (handled in measureDistance).
     */
    private double calculateDistance(long pulseStartTime, long pulseEndTime) {
        long pulseDuration = pulseEndTime - pulseStartTime; // Duration in nanoseconds
        return pulseDuration * (SPEED_OF_SOUND / 10_000_000 / 2); // Directly in centimeters
    }
}
