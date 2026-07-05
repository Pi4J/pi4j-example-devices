

package com.pi4j.devices.is31Fl37Matrix;


import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.util.Console;
import org.slf4j.Logger;

/**
 * Controls the processing abd warning LEDs
 */
public class ControlLeds {


    private final Logger logger;
    private final Is31Fl37Matrix matrix;
    private final Context pi4j;
    private final Console console;
    private DigitalOutput redLED = null;    // considered the Warning LED
    private DigitalOutput greenLED = null;    // considered the normal processing LED


    public ControlLeds(Context pi4j, Console console, DigitalOutput red_led_pin, DigitalOutput green_led_pin, Is31Fl37Matrix matrix, Logger logger) {
        this.redLED = red_led_pin;
        this.greenLED = green_led_pin;
        this.matrix = matrix;
        this.pi4j = pi4j;
        this.console = console;
        this.logger = logger;

        this.init();
    }

    private boolean init() {

        return (true);
    }

    /**
     * Warning LED will begin flashing. Process LED will be off.   LED matrix will have all leds energized
     *
     * @param matrix Is31Fl37Matrix
     */
    public void flash_alarm_led(Is31Fl37Matrix matrix) {
        this.logger.trace("flash_alarm_led ");
        this.logger.error("WaitOnInterrupt failed");

        if (this.greenLED != null) {
            this.greenLED.low();
        }

        matrix.blink_write(0);
        for (int i = 0; i < 7; i++) {
            matrix.fill(7, (byte) 0, 0);
        }
        if (this.redLED != null) {
            while (true) {
                this.redLED.toggle();
                matrix.blink_write(0);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.exit(20); // logging disabled so use System call
                }
            }
        }

    }

    /**
     * @param active True, processLED on, Warning LED off, else processLED off, Warning LED on
     */
    public void toggle_led(boolean active) {
        this.logger.trace("toggle_led  <state> " + active);
        if ((this.greenLED != null) && (this.redLED != null)) {
            if (active) {
                this.greenLED.high();
                this.redLED.low();
            } else {
                this.greenLED.low();
                this.redLED.high();
            }
        }

        this.logger.trace("toggle_led");

    }

    /**
     * Monitor the matrix controller interrupt to indicate the matrix has completed
     * displaying the previously loaded matrix data.
     *
     * @return Class with success indicator and count of wait loops executed.
     */
    public InterruptDetails wait_for_interrupt() {
        this.logger.trace("wait_for_interrupt");

        InterruptDetails rval = matrix.waitIntjLoop();  // uses gpio addListener
        //  InterruptDetails rval = matrix.waitIntpLoop(); // uses polling of GPIO INTB
        // signals.

        this.logger.trace("wait_for_interrupt  <is_done> " + rval.getSuccessVal() + " counter :"
            + String.format("0x%02X", rval.getCounter()));
        return (rval);
    }


}
