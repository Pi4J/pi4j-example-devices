#!/usr/bin/env python3

# monitor_table_format.py
# Monitors specified GPIO pins or all GPIO pins if none are specified.
# Displays the current state of each pin in a tabular format, updating the state in place.
#
# Required Libraries:
# This script requires the `pigpio` library to interface with GPIO.
# Install it via pip:
#   pip install pigpio
#
# Additionally, ensure the `pigpiod` daemon is running before executing this script:
#   sudo pigpiod
#
# Usage:
#   python3 monitor.py            # Monitor all GPIO pins
#   python3 monitor.py 23 24 25   # Monitor only GPIO pins 23, 24, and 25

import sys
import time
import pigpio

# Dictionary to store the state of each GPIO pin
pin_states = {}

def gpio_callback(gpio, level, tick):
    """Callback function to handle GPIO state changes."""
    pin_states[gpio] = level  # Update the current state of the GPIO pin
    display_pin_states()      # Update the display with the current states

def display_pin_states():
    """Prints only the states of all monitored GPIO pins in a single line."""
    # Generate states line
    states_line = "STATE " + " | ".join(str(pin_states[pin]).rjust(2) for pin in sorted(pin_states.keys()))
    print(f"\r{states_line}", end="")  # Update in place on the same line

def initialize_gpio_monitor(pins):
    """Initialize monitoring for specified GPIO pins."""
    for pin in pins:
        # Set initial state as unknown (e.g., `0` for not yet read)
        pin_states[pin] = 0
        # Create a callback for each pin to monitor
        callbacks.append(pi.callback(pin, pigpio.EITHER_EDGE, gpio_callback))

def main():
    global pi
    pi = pigpio.pi()

    if not pi.connected:
        print("Error: Could not connect to pigpio daemon.")
        sys.exit(1)

    # GPIO pins to monitor
    pins = range(32) if len(sys.argv) == 1 else map(int, sys.argv[1:])
    
    # Initialize monitoring on specified GPIO pins
    initialize_gpio_monitor(pins)

    # Display the header message, GPIO pins header, and initial state line
    print("Monitoring GPIO pins... Press Ctrl+C to stop.")
    sorted_pins = sorted(pin_states.keys())
    gpio_line = "GPIO  " + " | ".join(str(pin).rjust(2) for pin in sorted_pins)
    print(gpio_line)

    try:
        while True:
            # Refresh display every second (can be adjusted as needed)
            display_pin_states()
            time.sleep(1)
    except KeyboardInterrupt:
        print("\nCleaning up and exiting...")
    finally:
        for callback in callbacks:
            callback.cancel()
        pi.stop()

# List of callback instances for cleanup
callbacks = []

if __name__ == "__main__":
    main()
