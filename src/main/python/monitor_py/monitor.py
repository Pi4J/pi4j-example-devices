#!/usr/bin/env python3

# monitor.py
# Monitors specified GPIO pins or all GPIO pins if none are specified.
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

# Store the last tick times for each GPIO
last_ticks = [None] * 32

# Callback instances for cleanup
callbacks = []

def gpio_callback(gpio, level, tick):
  """Callback function to handle GPIO state changes."""
  if last_ticks[gpio] is not None:
    diff = pigpio.tickDiff(last_ticks[gpio], tick)
    print(f"GPIO={gpio} Level={level} Time Diff={diff} μs")
  last_ticks[gpio] = tick

def initialize_gpio_monitor(pins):
  """Initialize monitoring for specified GPIO pins."""
  for pin in pins:
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

  try:
    print("Monitoring GPIO pins... Press Ctrl+C to stop.")
    while True:
      time.sleep(60)  # Keeps the script alive
  except KeyboardInterrupt:
    print("\nCleaning up and exiting...")
  finally:
    for callback in callbacks:
      callback.cancel()
    pi.stop()

if __name__ == "__main__":
  main()
