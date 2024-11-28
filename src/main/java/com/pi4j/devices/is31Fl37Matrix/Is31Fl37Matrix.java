package com.pi4j.devices.is31Fl37Matrix;

/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  Is371fl37Matrix.java
 *      *
 *      * This file is part of the Pi4J project. More information about
 *      * this project can be found here:  https://pi4j.com/
 *      * **********************************************************************
 *     * %%
 *     * Copyright (C) 2012 - 2022 Pi4J
 *     * %%
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
 *   *
 *
 *
 */


import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeEvent;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.io.i2c.I2C;
import org.slf4j.Logger;

public class Is31Fl37Matrix {

    private final int width = 16;
    private final int height = 9;
    private final int _MODE_REGISTER = 0x00;
    private final int _FRAME_REGISTER = 0x01;
    private final int _AUTOPLAY1_REGISTER = 0x02;
    private final int _AUTOPLAY2_REGISTER = 0x03;
    private final int _BLINK_REGISTER = 0x05;
    private final int _AUDIOSYNC_REGISTER = 0x06;
    private final int _FRAME_STATE_REGISTER = 0x07;
    private final int _BREATH1_REGISTER = 0x08;
    private final int _BREATH2_REGISTER = 0x09;
    private final int _SHUTDOWN_REGISTER = 0x0a;
    private final int _GAIN_REGISTER = 0x0b;
    private final int _ADC_REGISTER = 0x0c;

    private final int _CONFIG_BANK = 0x0b;
    private final int _BANK_ADDRESS = 0xfd;

    private final int _PICTURE_MODE = 0x00;
    private final int _AUTOPLAY_MODE = 0x08;
    private final int _AUDIOPLAY_MODE = 0x18;

    private final int _ENABLE_OFFSET = 0x00;
    private final int _BLINK_OFFSET = 0x12;
    private final int _COLOR_OFFSET = 0x24;

    private int _frame = 0;

    private boolean intrp_occured = false;
    private final Logger logger;

    public Is31Fl37Matrix(Integer bus, Integer address, Integer bmp_address, Logger logger,
                          DigitalInput interruptPin, Context pi4j) {
        this.bus_num = bus;
        this.address = address;
        this.bmp_address = bmp_address;
        this.logger = logger;
        this.input = interruptPin;
        this.pi4j = pi4j;

        this.device = this.createI2cDevice(this.bus_num, this.address);
        this.init();
        this.reset();

    }

    /**
     * @param bus     Pi bus number
     * @param address device address
     * @return Instantiate I2C device
     */
    private I2C createI2cDevice(int bus, int address) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        this.logger.trace("Enter: createI2cDevice VL53L0X_TOF" + id + name);
        I2C rval = null;
        var i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
            .bus(bus)
            .device(address)
            .id("VL53L0X_TOF  " + id + " " + name)
            .name(name)
            .provider("linuxfs-i2c")
            .build();
        rval = this.pi4j.create(i2cDeviceConfig);
        this.logger.trace("Exit: createI2cDevice VL53L0X_TOF" + id + name);
        return (rval);

    }


    private void reset() {
        this.logger.trace("reset");

        this.sleep(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(100);
        }
        // #utime.sleep_us(10)
        this.sleep(false);
        this.logger.trace("reset");

    }

    private void sleep(boolean value) {
        this.logger.trace("sleep  value : " + value);

        int data;
        if (value) {
            data = 0;
        } else {
            data = 1;
        }
        this._register_write(this._CONFIG_BANK, this._SHUTDOWN_REGISTER, data);// is
        // there
        // a
        // time
        // constraint
        this.logger.trace("sleep"); // ???
    }

    private boolean init() {

        this.logger.info("init");
        this._mode(this._AUTOPLAY_MODE); // FRAME MODE);
        this.frame_write(0, true);
        for (int frame = 0; frame < 8; frame++) {
            this.fill(0, (byte) 0, frame);
            for (int c = 0; c < 18; c++) {
                this._register_write(frame, this._ENABLE_OFFSET + c, 0xff);
            }
        }
        // this.gpio = new BaseGpioInOut(this.ffdc, this.dioPinData,
        // this.ffdc.logger);
        // todo get interrupt pin number from args
        // GpioUtil gpioUtil = new GpioUtil(this.ffdc, this.dioPinData,
        // this.ffdc.logger);


        this.input.addListener(new MatrixGpioListener(this));
        this.logger.info("Added listener");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
         * this.gpio.getCfgData(RaspiBcmPin.GPIO_20).input .addTrigger(new
         * GpioSyncStateTrigger(
         * this.gpio.getCfgData(RaspiBcmPin.GPIO_24).output));
         */
        this.audio_sync(0);
        this.logger.info("init");

        return (true);
    }

    /**
     * Call from the Listener instance when the interrupt occurs.
     */
    public void intrp_happened() {
        this.logger.info("intrp_happened");

        this.intrp_occured = true;
        // System.out.println("intrp_happened");
        this.logger.info("intrp_happened");
    }

    /**
     * Uses the Pi GPIO event listener pattern. When the interrupt occurs the listener class will set
     * this.intrp_occured   true
     *
     * @return Class containing completion success and loop count waiting for interrupt.
     */
    public InterruptDetails waitIntjLoop() {
        this.logger.info("waitIntjLoop");

        boolean is_done = false;
        int counter = 0;
        int MAX = 2000;
        while (true) {
            if (this.intrp_occured) {
                is_done = true;
                this.intrp_occured = false;
                // ystem.out.println("GPIO 20 is LOW");
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            counter++;
            if (counter > MAX) {
                this.logger.error("waitIntjLoop: failed, exceeded MAX" + MAX);
                this.dumpRegs();
                break;
            }
        }
        this.logger.info(
            "waitIntjLoop <is_done>  " + is_done + " counter : " + String.format("0x%02X", counter));
        return (new InterruptDetails(is_done, counter));
    }

    /**
     * Ti avaoid a Pi4J_V2 GPIO interrupt bug, maually check the interrupt GPIO to determine if
     * the matrix controller signalled frame display completion via the INTB line
     *
     * @return Class containing completion success and loop count waiting for interrupt.
     */
    public InterruptDetails waitIntpLoop() {
        boolean is_done = false;
        int counter = 0;
        int MAX = 26000;
        this.logger.trace("waitIntpLoop");

        while (true) {
            if (this.input.isLow()) {
                is_done = true;
                this.logger.info(this.input.getDescription() + " is LOW");
                break;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            counter++;
            if (counter > MAX) {
                this.logger.error("wait_for_interupt: failed, exceeded MAX" + MAX);
                break;
            }
        }
        this.logger.trace(
            "waitIntpLoop <completion> " + is_done + " counter " + String.format("0x%02X", counter));

        return (new InterruptDetails(is_done, counter));
    }

    public void dumpRegs() {
        this.logger.info("dumpregs");
        String completeMess = "";
        for (int i = 0; i < 8; i++) {
            completeMess = completeMess + "\n" + " Frame : " + i + "\n";
            this.frame_write(i, true);
            byte[] data = this._register_read(this._CONFIG_BANK, this._MODE_REGISTER);
            completeMess = completeMess + this.dumpBits(0, data);
            data = this._register_read(this._CONFIG_BANK, this._FRAME_REGISTER);
            completeMess = completeMess + this.dumpBits(1, data);
            data = this._register_read(this._CONFIG_BANK, this._AUTOPLAY1_REGISTER);
            completeMess = completeMess + this.dumpBits(2, data);
            data = this._register_read(this._CONFIG_BANK, this._AUTOPLAY2_REGISTER);
            completeMess = completeMess + this.dumpBits(3, data);
            data = this._register_read(this._CONFIG_BANK, this._BLINK_REGISTER);
            completeMess = completeMess + this.dumpBits(4, data);
            data = this._register_read(this._CONFIG_BANK, this._AUDIOSYNC_REGISTER);
            completeMess = completeMess + this.dumpBits(5, data);
            data = this._register_read(this._CONFIG_BANK, this._FRAME_STATE_REGISTER);
            completeMess = completeMess + this.dumpBits(6, data);
            data = this._register_read(this._CONFIG_BANK, this._BREATH1_REGISTER);
            completeMess = completeMess + this.dumpBits(7, data);
            data = this._register_read(this._CONFIG_BANK, this._BREATH2_REGISTER);
            completeMess = completeMess + this.dumpBits(8, data);
            data = this._register_read(this._CONFIG_BANK, this._SHUTDOWN_REGISTER);
            completeMess = completeMess + this.dumpBits(9, data);
            data = this._register_read(this._CONFIG_BANK, this._GAIN_REGISTER);
            completeMess = completeMess + this.dumpBits(10, data);
            data = this._register_read(this._CONFIG_BANK, this._ADC_REGISTER);
            completeMess = completeMess + this.dumpBits(11, data);
        }
        this.logger.info(completeMess);

        this.logger.info("dumpregs");
    }

    public String dumpBits(int regOffset, byte[] reg) {
        String wholeMess = "";

        String[] reg_name = {"_MODE_REGISTER  ", "_FRAME_REGISTER   ", "_AUTOPLAY1_REGISTER", "_AUTOPLAY2_REGISTER ",
            "_BLINK_REGISTER ", "_AUDIOSYNC_REGISTER  ", "_FRAME_STATE_REGISTER", "_BREATH1_REGISTER   ",
            "_BREATH2_REGISTER   ", "_SHUTDOWN_REGISTER ", "_GAIN_REGISTER   ", "_ADC_REGISTER   "};

        String[][] pin_name = {{"---    ---    ---    Mode   Mode   >>>    Frame  <<<"},
            {"---    ---    ---    ---    ---    >>>    Frame  <<<   "},
            {"---    >>>    Loops  <<<    ---    >>>    Frames <<<     "},
            {"---    ---    >>>>      Frame Delay X 23ms       <<<<     "},
            {"---    ---    IC     ---    Blink  > Time X 0.27s <    "},
            {"---    ---    ---    ---    ---    ---    ---    Enable "},
            {"---    ---    ---    INT    ---    > Current Frame <     "},
            {"---    > Fade out time <    ---    > Fade in time  <     "},
            {"---    ---    ---    Ena    ---    ---   ---     ET     "},
            {"---    ---    ---    ---    ---    ---    ---    on/normal"},
            {"---    ---    ---    Mode   Ena    >>>    AGS    <<<     "},
            {">>>           Audio Sample Rate                  <<<     "},};
        wholeMess = wholeMess + "Reg " + reg_name[regOffset] + "    " + String.format("0x%02X", reg[0]);

        byte val = reg[0];
        for (int i = 0; i < 1 /* pin_name.length */; i++) {

            // System.out.println("pin7 pin6 pin5 pin4 pin3 pin2 pin1 pin0");
            wholeMess = wholeMess + "\n" + pin_name[regOffset][0];
            wholeMess = wholeMess + "\n" + " " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "      "
                + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n";

        }
        return (wholeMess);
    }

    private void audio_sync(int value) {
        this.logger.trace("audio_sync  <value> :" + String.format("0x%02X", value));

        this._register_write(this._CONFIG_BANK, this._AUDIOSYNC_REGISTER, value);
        this.logger.trace("audio_sync");
    }

    private void frame_write(int frame, boolean show) {
        this.logger.trace("frame_write  <frame> :" + String.format("0x%02X", frame) + " show " + show);

        if ((frame < 0) || (frame > 7)) {
            this.logger.error("Frame out of range");
            System.exit(66);
        }
        this._frame = frame;
        if (show) {
            this._register_write(this._CONFIG_BANK, this._FRAME_REGISTER, frame);
        }
    }

    private int frame_read() {
        this.logger.trace("frame_read");
        this.logger.trace("frame_read " + String.format("0x%02X", this._frame));

        return (this._frame);
    }

    private void _mode(int mode) {
        this.logger.trace("_mode  <mode> :" + String.format("0x%02X", mode));

        this._register_write(this._CONFIG_BANK, this._MODE_REGISTER, mode);
        this.logger.trace("_mode");

    }

    private int getInt(byte[] data, int index) {
        // # return two bytes from data as a signed 16-bit valu
        this.logger.trace(
            "getInt  <data> :" + String.format("0x%02X", data) + " index : " + String.format("0x%02X", index));
        this.logger.trace(
            "get short  " + String.format("0x%02X", data[index]) + " " + String.format("0x%02X", data[index + 1]));
        this.logger.trace(
            "return   " + (((data[index] << 16)) + ((data[index + 1] << 8)) + ((data[index + 2]) & 0xff)));

        this.logger.trace("getInt : " + String.format("0x%02X",
            ((data[index] << 16)) + ((data[index + 1] << 8)) + ((data[index + 2]) & 0xff)));
        return ((data[index] << 16)) + ((data[index + 1] << 8)) + ((data[index + 2]) & 0xff);
    }

    private int blink_read() {
        this.logger.trace("blink_read");
        int rtn = 0;

        byte[] val = this._register_read(this._CONFIG_BANK, (this._BLINK_REGISTER & 0x07));
        rtn = this.getInt(val, 0) * 270; // ??? size todo
        this.logger.trace("blink_read <rtn> :" + String.format("0x%02X", rtn));

        return (rtn);
    }

    int blink_write(int rate) {
        this.logger.trace("blink_write <rate> :" + String.format("0x%02X", rate));
        int rtn = 0;
        if (rate == 0) {
            this._register_write(this._CONFIG_BANK, this._BLINK_REGISTER, 0x00);
            return (rtn);
        }
        rate = rate / 270; // = 270
        this._register_write(this._CONFIG_BANK, this._BLINK_REGISTER, rate & 0x07 | 0x08);
        this.logger.trace("blink_write <rtn> :" + String.format("0x%02X", rtn));
        return (rtn);
    }

    public byte _bank_read() {
        this.logger.trace("_bank_read ");
        byte[] data = new byte[1];
        int rc = this.device.read(data, this._BANK_ADDRESS, 1);
        this.logger.trace("_bank_read <data> :" + String.format("0x%02X", data[0]));
        return (data[0]);
    }

    private int _bank_write(int bank) {
        this.logger.trace("_bank_write <bank> :" + String.format("0x%02X", bank));
        byte[] data = new byte[1];
        data[0] = (byte) bank;
        this.device.writeRegister(this._BANK_ADDRESS, data[0]);

        this.logger.trace("_bank_write");
        return (0);
    }

    void fill(int color, byte blink, int frame) {
        this.logger.trace("fill <color> :" + String.format("0x%02X", color) + " blink "
            + String.format("0x%02X", blink) + " frame " + String.format("0x%02X", frame));
        if (frame == 42) {
            frame = this._frame;
        }
        this._bank_write(frame);
        if (color != 42) { // is not None:
            if ((color > -1) && (color < 256)) { // not 0 <= color <= 255:
                byte[] data = new byte[24];
                for (int i = 0; i < 24; i++) {
                    data[i] = (byte) color; // / size
                }
                for (int row = 0; row < 6; row++) {// in range(6):
                    this.device.writeRegister(this._COLOR_OFFSET + row * 24, data);
                    // offset, data) self.i2c.write_i2c_block_data(self.address,
                    // self._COLOR_OFFSET + row * 24, data)
                }
            } else {
                this.logger.error("Color out of range");
            }
        }
        if (blink == 0) { // is not None:
            int data = (blink * 0xff);
            for (int col = 0; col < 18; col++) { // in range(18):
                this._register_write(frame, this._BLINK_OFFSET + col, data);
            }
        }
        this.logger.trace("fill");
    }

    private int _pixel_addr(int x, int y) {
        this.logger.trace(
            "_pixel_addr <x> " + String.format("0x%02X", x) + " <y> " + String.format("0x%02X", y));
        this.logger.trace("_pixel_addr <addr> : " + String.format("0x%02X", (x + y * 16)));
        return (x + y * 16);
    }

    int pixel(int x, int y, int color, int blink, int frame) {
        // System.out.println("pixels ");
        this.logger.trace("pixel <x> " + String.format("0x%02X", x) + " <y> " + String.format("0x%02X", y)
            + " color " + String.format("0x%02X", color) + " blink " + String.format("0x%02X", blink) + " frame "
            + String.format("0x%02X", frame));

        int rtn = 0;
        if ((x < 0) || (x > this.width)) {
            this.logger.error(" X is too big");
            return (rtn);
        }
        if ((y < 0) || (y > this.height)) {
            this.logger.error(" Y is too big");
            return (rtn);
        }

        int pixel = this._pixel_addr(x, y);
        // ??????? TODO how to port from python
        // if ((color == 0) && (blink == 1)) {
        // return (int) (this._register_read(this._frame, pixel)[0]);
        // }
        if (frame == 0) {
            frame = this._frame;
        }
        if (color > 0) {
            if (color > 255) {
                this.logger.error("Color out of range");
                System.exit(35);
            }
            this._register_write(frame, this._COLOR_OFFSET + pixel, color);
        }
        int addr = pixel / 8;
        int bit = pixel % 8;
        byte[] data = this._register_read(frame, this._BLINK_OFFSET + addr);
        int bits = data[0];
        if (blink == 1) {
            bits |= 1 << bit;
        } else {
            bits &= ~(1 << bit);
        }
        this._register_write(frame, this._BLINK_OFFSET + addr, bits);
        this.logger.trace("pixel  <rtn> :" + String.format("0x%02X", rtn));
        return (rtn);
    }

    private byte[] _register_read(int bank, int register) {
        this.logger.trace("_register_read <bank> : " + String.format("0x%02X", bank) + " register "
            + String.format("0x%02X", register));
        // System.out.println("reg read " + bank + " " + register);
        this._bank_write(bank);
        byte[] rtn = new byte[1];
        int bytesRead = this.device.readRegister(register, rtn, 1);
        // i2c.read_i2c_block_data(self.address,
        // register, 1)[0]);
        /*
         * for (int i = 0; i < rtn.length; i++) { System.out.println(" byte " +
         * i + " value " + rtn[i]); }
         */
        this.logger.trace("_register_read  <rtn> :" + String.format("0x%02X", rtn[0]));
        return (rtn);

    }

    private void _register_write(int bank, int register, int value) {
        this.logger.trace("_register_write <bank> : " + String.format("0x%02X", bank) + " register "
            + String.format("0x%02X", register) + " value " + String.format("0x%02X", value));

        // System.out.println("reg write " + bank + " " + register + " " +
        // value);
        this._bank_write(bank);
        byte[] data = new byte[1];
        data[0] = (byte) value; // ?? size
        this.device.writeRegister(register, data[0]);
        this.logger.trace("_register_write");
    }

    public byte[] _mode_read(int mode) {
        this.logger.trace("_mode_read <mode> " + String.format("0x%02X", mode));
        byte[] rval = this._register_read(this._CONFIG_BANK, this._MODE_REGISTER);
        this.logger.trace("_mode_read <return> " + String.format("0x%02X", rval[0]));
        return (rval);
    }

    private void _mode_write(int mode) {
        this.logger.trace("_mode_write <mode> " + String.format("0x%02X", mode));
        this._register_write(this._CONFIG_BANK, this._MODE_REGISTER, mode);
        this.logger.trace("_mode_write ");
    }

    void autoplay(int delay, int loops, int frames) {
        this.logger.trace("autoplay  <delay> " + String.format("0x%02X", delay) + " loops "
            + String.format("0x%02X", loops) + " frames" + String.format("0x%02X", frames));
        if (delay == 0) {
            this._mode_write(this._PICTURE_MODE);
            this.logger.trace("delay zero, picture mode");
            this.logger.trace("autoplay ");
            return;
        }
        // System.out.println("loop" + loops);
        delay = delay / 11; // //= 11
        if ((loops < 0) || (loops > 7)) {
            this.logger.error("Loops out of range");
            System.exit(36);
        }
        if ((frames < 0) || (frames > 7)) {
            this.logger.error("Frames out of range");
            System.exit(37);
        }
        if ((delay < 0) || (delay > 63)) {
            this.logger.error("Delay out of range");
            System.exit(38);
        }
        this._mode_write((this._AUTOPLAY_MODE | this._frame));
        this._register_write(this._CONFIG_BANK, this._AUTOPLAY1_REGISTER, loops << 4 | frames);
        this._register_write(this._CONFIG_BANK, this._AUTOPLAY2_REGISTER, delay);
        this.logger.trace("autoplay ");

    }

    private final int address;
    private final int bmp_address;
    private final int bus_num;
    private final I2C device;
    private final DigitalInput input;

    private final Context pi4j;

    private class MatrixGpioListener implements DigitalStateChangeListener {

        public MatrixGpioListener(Is31Fl37Matrix chip) {
            this.chip = chip;
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
                // System.out.println("Pin went low");

                this.chip.intrp_happened();
            }
        }

        private final Is31Fl37Matrix chip;
    }


}
