HC-SR04 Ultrasonic Sensor Example with Pi4J
===========================================

This project demonstrates how to use the HC-SR04 ultrasonic sensor with a Raspberry Pi via the Pi4J library to measure distances in centimeters. The application continuously measures and displays the distance from the sensor to an object every second.

Requirements
------------

-   Raspberry Pi (any model with GPIO support)
-   HC-SR04 Ultrasonic Sensor
-   Pi4J library (version compatible with your Pi and Java setup)
-   Java 11 or later

Wiring
------

1.  **TRIG** (trigger) pin on the HC-SR04 sensor is connected to GPIO 23 on the Raspberry Pi.
2.  **ECHO** (echo) pin on the HC-SR04 sensor is connected to GPIO 24 on the Raspberry Pi.
3.  **VCC** pin on the HC-SR04 sensor is connected to the 5V power pin on the Raspberry Pi.
4.  **GND** pin on the HC-SR04 sensor is connected to a ground (GND) pin on the Raspberry Pi.

**Note**: Some Raspberry Pi models use 3.3V GPIO pins, so if needed, use a voltage divider to lower the ECHO signal voltage.

Compile and Run
------
1. **Build the project:**
    ```bash
    mvn clean install
    ```
2. **Navigate to the distribution directory:**
    ```bash
    cd target/distribution
   ```
3. **Run the application with optional GPIO parameters:** 
    ```bash
    sudo ./runHCSR04.sh <TRIG_PIN> <ECHO_PIN>
    ```
#### Example Usage
 - **Using Default GPIO Pins (TRIG=23, ECHO=24):**
    ```bash
    sudo ./runHCSR04.sh
    ```
 - **Specifying GPIO Pins (e.g., TRIG=20, ECHO=21):**
    ```bash
    sudo ./runHCSR04.sh 20 21
    ```
 - **Getting Help Information:**
    ```bash
    sudo ./runHCSR04.sh -h
    ```
Code Overview
-------------

### HCSR04 Class

The `HCSR04` class configures the GPIO pins to interact with the sensor and provides a `measureDistance` method to calculate and return the distance to an object based on the duration of the ECHO pulse.

Key points:

-   **Trigger Signal**: A 10-microsecond pulse on the TRIG pin starts the measurement.
-   **Echo Measurement**: The class measures the duration of the ECHO pulse to calculate the distance.
-   **Distance Calculation**: Uses the speed of sound to convert pulse duration into centimeters.

### HCSR04_App Class

The `HCSR04_App` class is a simple application that initializes the Pi4J context and the `HCSR04` sensor instance. It enters a loop to repeatedly measure and print the distance to the console every second.

Example Output
--------------

When running the application, you should see output similar to the following:

```
Starting HC-SR04 distance measurement application...
Distance: 25.30 cm
Distance: 24.95 cm
Distance: 24.80 cm
...
```

If the object is out of range or no reading is detected, the output will indicate an invalid reading:

```
Out of range or invalid reading.
```