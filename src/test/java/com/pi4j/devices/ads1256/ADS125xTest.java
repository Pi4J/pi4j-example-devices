package com.pi4j.devices.ads1256;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the static utility methods in the ADS125x interface.
 *
 * These tests validate the bit manipulation logic used to determine GPIO pin states
 * and directions without requiring actual hardware.
 */
@DisplayName("ADS125x Static Utility Methods Tests")
class ADS125xTest {

    @Test
    @DisplayName("isPinOutput should return true when pin direction bit is 0 (output)")
    void testIsPinOutput_WhenPinIsOutput() {
        // GPIO pin 0 configured as output (bit 4 = 0)
        byte regVal = (byte) 0b00000000;
        assertTrue(ADS125x.isPinOutput(0, regVal));

        // GPIO pin 1 configured as output (bit 5 = 0)
        regVal = (byte) 0b00000000;
        assertTrue(ADS125x.isPinOutput(1, regVal));

        // GPIO pin 2 configured as output (bit 6 = 0)
        regVal = (byte) 0b00000000;
        assertTrue(ADS125x.isPinOutput(2, regVal));

        // GPIO pin 3 configured as output (bit 7 = 0)
        regVal = (byte) 0b00000000;
        assertTrue(ADS125x.isPinOutput(3, regVal));
    }

    @Test
    @DisplayName("isPinOutput should return false when pin direction bit is 1 (input)")
    void testIsPinOutput_WhenPinIsInput() {
        // GPIO pin 0 configured as input (bit 4 = 1)
        byte regVal = (byte) 0b00010000;
        assertFalse(ADS125x.isPinOutput(0, regVal));

        // GPIO pin 1 configured as input (bit 5 = 1)
        regVal = (byte) 0b00100000;
        assertFalse(ADS125x.isPinOutput(1, regVal));

        // GPIO pin 2 configured as input (bit 6 = 1)
        regVal = (byte) 0b01000000;
        assertFalse(ADS125x.isPinOutput(2, regVal));

        // GPIO pin 3 configured as input (bit 7 = 1)
        regVal = (byte) 0b10000000;
        assertFalse(ADS125x.isPinOutput(3, regVal));
    }

    @Test
    @DisplayName("isPinInput should return true when pin direction bit is 1 (input)")
    void testIsPinInput_WhenPinIsInput() {
        // GPIO pin 0 configured as input (bit 4 = 1)
        byte regVal = (byte) 0b00010000;
        assertTrue(ADS125x.isPinInput(0, regVal));

        // GPIO pin 1 configured as input (bit 5 = 1)
        regVal = (byte) 0b00100000;
        assertTrue(ADS125x.isPinInput(1, regVal));

        // GPIO pin 2 configured as input (bit 6 = 1)
        regVal = (byte) 0b01000000;
        assertTrue(ADS125x.isPinInput(2, regVal));

        // GPIO pin 3 configured as input (bit 7 = 1)
        regVal = (byte) 0b10000000;
        assertTrue(ADS125x.isPinInput(3, regVal));
    }

    @Test
    @DisplayName("isPinInput should return false when pin direction bit is 0 (output)")
    void testIsPinInput_WhenPinIsOutput() {
        // All pins configured as output (bits 4-7 = 0)
        byte regVal = (byte) 0b00000000;
        assertFalse(ADS125x.isPinInput(0, regVal));
        assertFalse(ADS125x.isPinInput(1, regVal));
        assertFalse(ADS125x.isPinInput(2, regVal));
        assertFalse(ADS125x.isPinInput(3, regVal));
    }

    @Test
    @DisplayName("isGpioHigh should return true when input pin state bit is 1")
    void testIsGpioHigh_WhenInputPinIsHigh() {
        // GPIO pin 0: configured as input (bit 4 = 1) and state is high (bit 0 = 1)
        byte regVal = (byte) 0b00010001;
        assertTrue(ADS125x.isGpioHigh(0, regVal));

        // GPIO pin 1: configured as input (bit 5 = 1) and state is high (bit 1 = 1)
        regVal = (byte) 0b00100010;
        assertTrue(ADS125x.isGpioHigh(1, regVal));

        // GPIO pin 2: configured as input (bit 6 = 1) and state is high (bit 2 = 1)
        regVal = (byte) 0b01000100;
        assertTrue(ADS125x.isGpioHigh(2, regVal));

        // GPIO pin 3: configured as input (bit 7 = 1) and state is high (bit 3 = 1)
        regVal = (byte) 0b10001000;
        assertTrue(ADS125x.isGpioHigh(3, regVal));
    }

    @Test
    @DisplayName("isGpioHigh should return false when input pin state bit is 0")
    void testIsGpioHigh_WhenInputPinIsLow() {
        // GPIO pin 0: configured as input (bit 4 = 1) but state is low (bit 0 = 0)
        byte regVal = (byte) 0b00010000;
        assertFalse(ADS125x.isGpioHigh(0, regVal));

        // GPIO pin 1: configured as input (bit 5 = 1) but state is low (bit 1 = 0)
        regVal = (byte) 0b00100000;
        assertFalse(ADS125x.isGpioHigh(1, regVal));

        // GPIO pin 2: configured as input (bit 6 = 1) but state is low (bit 2 = 0)
        regVal = (byte) 0b01000000;
        assertFalse(ADS125x.isGpioHigh(2, regVal));

        // GPIO pin 3: configured as input (bit 7 = 1) but state is low (bit 3 = 0)
        regVal = (byte) 0b10000000;
        assertFalse(ADS125x.isGpioHigh(3, regVal));
    }

    @Test
    @DisplayName("isGpioHigh should throw IllegalStateException when pin is configured as output")
    void testIsGpioHigh_ThrowsExceptionWhenPinIsOutput() {
        // GPIO pins configured as output (bits 4-7 = 0)
        byte regVal = (byte) 0b00000000;

        IllegalStateException exception0 = assertThrows(IllegalStateException.class, () -> {
            ADS125x.isGpioHigh(0, regVal);
        });
        assertEquals("Attempted to read GPIO pin 0, which is set as output.", exception0.getMessage());

        IllegalStateException exception1 = assertThrows(IllegalStateException.class, () -> {
            ADS125x.isGpioHigh(1, regVal);
        });
        assertEquals("Attempted to read GPIO pin 1, which is set as output.", exception1.getMessage());

        IllegalStateException exception2 = assertThrows(IllegalStateException.class, () -> {
            ADS125x.isGpioHigh(2, regVal);
        });
        assertEquals("Attempted to read GPIO pin 2, which is set as output.", exception2.getMessage());

        IllegalStateException exception3 = assertThrows(IllegalStateException.class, () -> {
            ADS125x.isGpioHigh(3, regVal);
        });
        assertEquals("Attempted to read GPIO pin 3, which is set as output.", exception3.getMessage());
    }

    @Test
    @DisplayName("Mixed pin configurations should work correctly")
    void testMixedPinConfigurations() {
        // Mixed configuration: pins 0,2 as output, pins 1,3 as input
        // Pin 1 is high, pin 3 is low
        byte regVal = (byte) 0b10100010; // Binary: bits 7,5 = input direction, bit 1 = high state

        // Test direction detection
        assertTrue(ADS125x.isPinOutput(0, regVal));   // Pin 0 is output
        assertTrue(ADS125x.isPinInput(1, regVal));    // Pin 1 is input
        assertTrue(ADS125x.isPinOutput(2, regVal));   // Pin 2 is output
        assertTrue(ADS125x.isPinInput(3, regVal));    // Pin 3 is input

        // Test state reading for input pins only
        assertTrue(ADS125x.isGpioHigh(1, regVal));    // Pin 1 is high
        assertFalse(ADS125x.isGpioHigh(3, regVal));   // Pin 3 is low

        // Test that reading output pins throws exception
        assertThrows(IllegalStateException.class, () -> ADS125x.isGpioHigh(0, regVal));
        assertThrows(IllegalStateException.class, () -> ADS125x.isGpioHigh(2, regVal));
    }

    @Test
    @DisplayName("Edge case: All pins configured as input with various states")
    void testAllPinsInput_VariousStates() {
        // All pins input, with alternating high/low states
        byte regVal = (byte) 0b11110101; // Pins 0,2 high; pins 1,3 low

        // All should be input
        assertTrue(ADS125x.isPinInput(0, regVal));
        assertTrue(ADS125x.isPinInput(1, regVal));
        assertTrue(ADS125x.isPinInput(2, regVal));
        assertTrue(ADS125x.isPinInput(3, regVal));

        // None should be output
        assertFalse(ADS125x.isPinOutput(0, regVal));
        assertFalse(ADS125x.isPinOutput(1, regVal));
        assertFalse(ADS125x.isPinOutput(2, regVal));
        assertFalse(ADS125x.isPinOutput(3, regVal));

        // Test states
        assertTrue(ADS125x.isGpioHigh(0, regVal));   // Pin 0 is high
        assertFalse(ADS125x.isGpioHigh(1, regVal));  // Pin 1 is low
        assertTrue(ADS125x.isGpioHigh(2, regVal));   // Pin 2 is high
        assertFalse(ADS125x.isGpioHigh(3, regVal));  // Pin 3 is low
    }

    @Test
    @DisplayName("Edge case: All pins configured as output")
    void testAllPinsOutput() {
        // All pins configured as output
        byte regVal = (byte) 0b00001111; // Direction bits all 0 (output), state bits all 1

        // All should be output
        assertTrue(ADS125x.isPinOutput(0, regVal));
        assertTrue(ADS125x.isPinOutput(1, regVal));
        assertTrue(ADS125x.isPinOutput(2, regVal));
        assertTrue(ADS125x.isPinOutput(3, regVal));

        // None should be input
        assertFalse(ADS125x.isPinInput(0, regVal));
        assertFalse(ADS125x.isPinInput(1, regVal));
        assertFalse(ADS125x.isPinInput(2, regVal));
        assertFalse(ADS125x.isPinInput(3, regVal));

        // Reading any pin should throw exception
        assertThrows(IllegalStateException.class, () -> ADS125x.isGpioHigh(0, regVal));
        assertThrows(IllegalStateException.class, () -> ADS125x.isGpioHigh(1, regVal));
        assertThrows(IllegalStateException.class, () -> ADS125x.isGpioHigh(2, regVal));
        assertThrows(IllegalStateException.class, () -> ADS125x.isGpioHigh(3, regVal));
    }
}