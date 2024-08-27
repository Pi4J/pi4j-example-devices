/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  RotaryEncoderId5880I2C.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2022 Pi4J
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
package com.pi4j.devices.rotary_encoder;

import com.pi4j.Pi4J;
import com.pi4j.exception.LifecycleException;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import com.pi4j.util.Console;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import static java.util.Arrays.fill;


public class RotaryEncoderId5880I2C {


    private static final Console console = new Console(); // Pi4J Logger helper

    private static final int I2C_BUS = 0x01;
    private static final int I2C_ADDRESS = 0x77; // When connecting SDO to GND = 0x76

    public static void main(String[] args) throws Exception {

        var pi4j = Pi4J.newAutoContext();
        int address = 0x77;
        int pin = 42;
        int position = 0;
        Signal.handle(new Signal("INT"), new SignalHandler() {
            public void handle(Signal sig) {
                System.out.println("Performing ctl-C shutdown");
                try {
                    pi4j.shutdown();
                } catch (LifecycleException e) {
                    e.printStackTrace();
                }
                // Thread.dumpStack();
                System.exit(2);
            }
        });

        String helpString = " parms:  -a hex address -i  hex interrupt  GPIO number,  -p hex position, -h help \n ";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-i")) { // interrupt pin GPIO number
                String a = args[i + 1];
                i++;
                pin = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-p")) { // encoder position
                String a = args[i + 1];
                i++;
                position = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }
        // Initialize I2C
        console.println("Initializing the encoder via I2C");
        I2CProvider i2CProvider = pi4j.provider("linuxfs-i2c");
        I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
            .id("RotartyEncoder")
            .bus(I2C_BUS)
            .device(address)
            .build();
        var inputCfg = DigitalInput.newConfigBuilder(pi4j)
            .address(pin)
            .pull(PullResistance.PULL_UP)
            .provider("gpiod-digital-input");
        DigitalInput intrp = pi4j.create(inputCfg);

        I2C rotary = i2CProvider.create(i2cConfig);

        // validate HW revision

        // config the encoder to signal interrupt low when button pushed ot knob turned
        setPosition(rotary, position);
        int pos = position(rotary);
        console.println("Position is :  " + pos);

        // setup a digital output listener to listen for any state changes on the digital output
        intrp.addListener(new RotaryEncoderId5880I2C.DataInGpioListener(rotary));
        String result = intrp.state().toString();
        console.println(" Read state : " + result);

        enable_encoder_interrupt(rotary);

        console.println(" Wait for Ctrl C ");
        console.waitForExit();
        pi4j.shutdown();

        console.println("**************************************");
        console.println("Finished");
    }


    private static class DataInGpioListener implements DigitalStateChangeListener {


        I2C encoder;
        int present_position;

        public DataInGpioListener(I2C rotary) {
            console.println("DataInGpioListener ctor");
            this.encoder = rotary;
            this.present_position = position(this.encoder);
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            console.println(">>> Enter: onDigitalStateChange");
            // this is in prep to begin sending high----low transition to signify 0 or 1
            if (event.state() == DigitalState.HIGH) {
                console.println("onDigitalStateChange Pin went High  NOP");
            } else if (event.state() == DigitalState.LOW) {
                console.println("onDigitalStateChange Pin went Low, Read position");
                int pos = position(this.encoder);
                String direction = "clocklwise";
                if (pos < this.present_position) {
                    direction = "CounterClockwise";
                }
                this.present_position = pos;
                console.println("Position is :  " + pos + " rotation :" + direction);
            } else {
                console.println("Strange event state  " + event.state());
            }
            console.println("<<< Exit: onDigitalStateChange");
        }
    }

    private static int position(I2C rotary) {
        int pos = encoder_position(rotary);
        return pos;
    }

    private static boolean setPosition(I2C rotary, int value) {
        set_encoder_position(rotary, value);

        return true;
    }

    private static int encoder_position(I2C rotary) {
        byte[] buf = new byte[4];
        fill(buf, (byte) 0);
        byte[] data = new byte[2];
        data[0] = (byte) EncoderDeclares.base_addr;
        data[1] = (byte) RotaryEncoderId5880I2C.EncoderDeclares.encoder_pos;
        rotary.write(data);
        long delay = 8;
        try {
            Thread.sleep(8);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        rotary.read(buf);
        int pos = buf[0] << 12 | buf[1] << 8 | buf[2] << 4 | buf[3];
        return pos;
    }

    private static boolean set_encoder_position(I2C rotary, int pos) {
        byte[] data = new byte[6];
        data[0] = (byte) EncoderDeclares.base_addr;
        data[1] = (byte) EncoderDeclares.encoder_pos;

        data[5] = (byte) (pos & 0xff);
        data[4] = (byte) ((pos >> 8) & 0xff);
        data[3] = (byte) ((pos >> 16) & 0xff);
        data[2] = (byte) ((pos >> 24) & 0xff);
        rotary.write(data);
        return true;
    }



    private static boolean enable_encoder_interrupt(I2C rotary) {
        byte[] data = new byte[3];
        data[0] = (byte) EncoderDeclares.base_addr;
        data[1] = (byte) EncoderDeclares.intrp_set;
        data[2] = 0x01;
        rotary.write(data);
        return true;
    }

    private static boolean disable_encoder_interrupt(I2C rotary) {
        byte[] data = new byte[3];
        data[0] = (byte) EncoderDeclares.base_addr;
        data[1] = (byte) EncoderDeclares.intrp_clr;
        data[2] = 0x01;
        rotary.write(data);
        return true;

    }


    private static class EncoderDeclares {
        /*  Begin device register definitions.         */
        static int base_addr = 0x11;
        static int encoder_pos = 0x30;
        static int encoder_delta = 0x40;
        static int intrp_set = 0x10;
        static int intrp_clr = 0x20;
    }
}
