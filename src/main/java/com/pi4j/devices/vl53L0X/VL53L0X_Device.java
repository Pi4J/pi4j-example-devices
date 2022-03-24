package com.pi4j.devices.vl53L0X;


/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  VL53L0X_Device.java
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
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VL53L0X_Device {


    public VL53L0X_Device(Context pi4j, int bus, int address, int timeout) {
        super();
        this.bus = bus;
        this.address = address;
        this.timeout = timeout;
        this.pi4j = pi4j;
        this.logger = LoggerFactory.getLogger(VL53L0X_Device.class);
        this.device = this.createI2cDevice(bus, address, timeout);
        this.init(bus, address, timeout);

    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    I2C createI2cDevice(int bus, int address, int timeout) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        this.logger.info("Enter: createI2cDevice VL53L0X_TOF" + id + "" + name);
        I2C rval = null;
        var i2cDeviceConfig = I2C.newConfigBuilder(this.pi4j)
                .bus(bus)
                .device(address)
                .id("VL53L0X_TOF  " + id + " " + name)
                .name(name)
                .provider("linuxfs-i2c")
                .build();
        rval = this.pi4j.create(i2cDeviceConfig);
        this.logger.info("Exit: createI2cDevice VL53L0X_TOF" + id + "" + name);
        return (rval);

    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void init(int bus, int address, int timeout) {
        this.logger.info("Enter:init   bus  " + bus + "  address " + address + " timeout  " + timeout);
        this._BUFFER = new byte[3];
        // Check identification registers for expected values.
        // From section 3.2 of the datasheet.
        this.test = this.readDevice8(0xC0);
        this.logger.info(" compare 0xEE : " + 0XEE + "  returned : " + this.test);

        this.logger.trace(" compare 0xEE : " + 0XEE + "  returned : " + this.test);
        if ((this.readDevice8(0xC0) != 0xEE) || (this.readDevice8(0xC1) != 0xAA) || (this.readDevice8(0xC2) != 0x10)) {
            System.exit(4);
        }
        // Initialize access to the sensor. This is based on the logic from:
        // https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
        // Set I2C standard mode.
        int[][] pair = {{0x88, 0x00}, {0x80, 0x01}, {0xFF, 0x01}, {0x00, 0x00}};
        for (int i = 0; i < pair.length; i++) {
            this.writeDevice8(pair[i][0], (byte) pair[i][1]);
        }
        this._stop_variable = (byte) this.readDevice8(0x91);

        int[][] pair2 = {{0x00, 0x01}, {0xFF, 0x00}, {0x80, 0x00}};
        for (int i = 0; i < pair2.length; i++) {
            this.writeDevice8(pair2[i][0], (byte) pair2[i][1]);
        }
        // disable SIGNAL_RATE_MSRC (bit 1) and SIGNAL_RATE_PRE_RANGE (bit 4)
        // # limit checks
        byte config_control = (byte) ((byte) this.readDevice8(Vl53L0X_Declares._MSRC_CONFIG_CONTROL) | 0x12);
        this.writeDevice8(Vl53L0X_Declares._MSRC_CONFIG_CONTROL, config_control);
        // set final range signal rate limit to 0.25 MCPS (million counts per
        // second)
        this.set_signal_rate_limit(0.25);
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_SEQUENCE_CONFIG, (byte) 0xFF);
        SpadData spaddata = this._get_spad_info();
        this.logger.trace("spaddata    : " + spaddata);
        // The SPAD map (RefGoodSpadMap) is read by
        int[] ref_spad_map = new int[7];
        ref_spad_map[0] = Vl53L0X_Declares._GLOBAL_CONFIG_SPAD_ENABLES_REF_0;
        this.writeDevice8(ref_spad_map[0]);
        for (int c = 1; c < ref_spad_map.length; c++) {
            ref_spad_map[c] = this.readDevice8();
            this.logger.trace("ref_spad_map[c]   :" + ref_spad_map[c]);
        }

        int[][] pair3 = {{0xFF, 0x01}, {Vl53L0X_Declares._DYNAMIC_SPAD_REF_EN_START_OFFSET, 0x00},
                {Vl53L0X_Declares._DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, 0x2C}, {0xFF, 0x00},
                {Vl53L0X_Declares._GLOBAL_CONFIG_REF_EN_START_SELECT, 0xB4}};
        for (int i = 0; i < pair3.length; i++) {
            this.writeDevice8(pair3[i][0], (byte) pair3[i][1]);
        }

        int first_spad_to_enable = 0;
        if (spaddata.is_aperture) {
            first_spad_to_enable = 12;
        }

        int spads_enabled = 0;
        for (int i = 0; i < 48; i++) {
            if ((i < first_spad_to_enable) || (spads_enabled == spaddata.count)) {
                // # This bit is lower than the first one that should be
                // enabled,
                // # or (reference_spad_count) bits have already been enabled,
                // so
                // # zero this bit.
                ref_spad_map[1 + (Math.floorDiv(i, 8))] &= ~(1 << (i % 8));
            } else if (((ref_spad_map[1 + (Math.floorDiv(i, 8))] >> (i % 8)) & 0x1) > 0) {
                spads_enabled += 1;
            }
        }

        for (int c = 1; c < ref_spad_map.length; c++) {
            this.writeDevice8(ref_spad_map[c]);
        }

        int[][] pair4 = {{0xFF, 0x01}, {0x00, 0x00}, {0xFF, 0x00}, {0x09, 0x00}, {0x10, 0x00},
                {0x11, 0x00}, {0x24, 0x01}, {0x25, 0xFF}, {0x75, 0x00}, {0xFF, 0x01}, {0x4E, 0x2C},
                {0x48, 0x00}, {0x30, 0x20}, {0xFF, 0x00}, {0x30, 0x09}, {0x54, 0x00}, {0x31, 0x04},
                {0x32, 0x03}, {0x40, 0x83}, {0x46, 0x25}, {0x60, 0x00}, {0x27, 0x00}, {0x50, 0x06},
                {0x51, 0x00}, {0x52, 0x96}, {0x56, 0x08}, {0x57, 0x30}, {0x61, 0x00}, {0x62, 0x00},
                {0x64, 0x00}, {0x65, 0x00}, {0x66, 0xA0}, {0xFF, 0x01}, {0x22, 0x32}, {0x47, 0x14},
                {0x49, 0xFF}, {0x4A, 0x00}, {0xFF, 0x00}, {0x7A, 0x0A}, {0x7B, 0x00}, {0x78, 0x21},
                {0xFF, 0x01}, {0x23, 0x34}, {0x42, 0x00}, {0x44, 0xFF}, {0x45, 0x26}, {0x46, 0x05},
                {0x40, 0x40}, {0x0E, 0x06}, {0x20, 0x1A}, {0x43, 0x40}, {0xFF, 0x00}, {0x34, 0x03},
                {0x35, 0x44}, {0xFF, 0x01}, {0x31, 0x04}, {0x4B, 0x09}, {0x4C, 0x05}, {0x4D, 0x04},
                {0xFF, 0x00}, {0x44, 0x00}, {0x45, 0x20}, {0x47, 0x08}, {0x48, 0x28}, {0x67, 0x00},
                {0x70, 0x04}, {0x71, 0x01}, {0x72, 0xFE}, {0x76, 0x00}, {0x77, 0x00}, {0xFF, 0x01},
                {0x0D, 0x01}, {0xFF, 0x00}, {0x80, 0x01}, {0x01, 0xF8}, {0xFF, 0x01}, {0x8E, 0x01},
                {0x00, 0x01}, {0xFF, 0x00}, {0x80, 0x00}};

        for (int i = 0; i < pair4.length; i++) {
            this.writeDevice8(pair4[i][0], (byte) pair4[i][1]);
        }

        this.writeDevice8(Vl53L0X_Declares._SYSTEM_INTERRUPT_CONFIG_GPIO, (byte) 0x04);
        int gpio_hv_mux_active_high = this.readDevice8(Vl53L0X_Declares._GPIO_HV_MUX_ACTIVE_HIGH);
        this.writeDevice8(Vl53L0X_Declares._GPIO_HV_MUX_ACTIVE_HIGH, (byte) (gpio_hv_mux_active_high & ~0x10)); // #
        // active
        // low
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_INTERRUPT_CLEAR, (byte) 0x01);
        this.measurement_timing_budget_us = this.get_measurement_timing_budget();
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_SEQUENCE_CONFIG, (byte) 0xE8);
        this.set_measurement_timing_budget(this.measurement_timing_budget_us);
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_SEQUENCE_CONFIG, (byte) 0x01);
        this.perform_single_ref_calibration(0x40);
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_SEQUENCE_CONFIG, (byte) 0x02);
        this.perform_single_ref_calibration(0x00);
        // # "restore the previous Sequence Config"
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_SEQUENCE_CONFIG, (byte) 0xE8);
        this.logger.info("Exit: init  " );

    }


    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    class StepEnable {
        public StepEnable(boolean tcc, boolean dss, boolean msrc, boolean pre_range, boolean final_range) {
            super();
            this.tcc = tcc;
            this.dss = dss;
            this.msrc = msrc;
            this.pre_range = pre_range;
            this.final_range = final_range;
        }

        boolean tcc;
        boolean dss;
        boolean msrc;
        boolean pre_range;
        boolean final_range;
    }


    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void setNewAddress(int gpioReset, int newAddress, Console console, int existingAddress) {
        this.logger.trace("Enter: setNewAddress  gpio  " + gpioReset + " new address  " + newAddress  + " exsting address "  + existingAddress  );
        this.resetChip(gpioReset, this.pi4j, 1000, true, DigitalState.HIGH, console);
        VL53L0X_Device vl53Temp = null;
        I2C tempI2c = null;
        //Use 0x29 POR value so we need to recreate I2C device with expected POR value
        // unless it was the existing address and he device already exists
        if (existingAddress != Vl53L0X_Declares._VL53L0X_DEFAULT_ADDRESS) {
            vl53Temp = new VL53L0X_Device(this.pi4j, this.bus, Vl53L0X_Declares._VL53L0X_DEFAULT_ADDRESS, this.timeout);
            vl53Temp.device.writeRegister(Vl53L0X_Declares._I2C_SLAVE_DEVICE_ADDRESS, newAddress);
        } else {  // use the existing device
            this.init(this.bus, Vl53L0X_Declares._VL53L0X_DEFAULT_ADDRESS, this.timeout);
            this.device.writeRegister(Vl53L0X_Declares._I2C_SLAVE_DEVICE_ADDRESS, newAddress);
            tempI2c = this.createI2cDevice(this.bus, newAddress, this.timeout);
        }
        // Now chip is reprogrammed with new address
        if (existingAddress != newAddress) {
            // now reset to the device with the new address
            this.device = tempI2c;
        }
        this.init(this.bus, newAddress, this.timeout);
        this.logger.trace("Exit: setNewAddress ");
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void resetChip(int resetGpio, Context pi4j, int delay, boolean bar, DigitalState initial, Console console) {
        this.logger.trace("Enter: resetChip  gpio " + resetGpio  + "  delay " + delay + " UnderBar "  + bar   + " initial state  " + initial);
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("resetPin")
                .name("Chip reset")
                .address(resetGpio)
                .shutdown(initial)
                .initial(initial)
                .provider("pigpio-digital-output");
        DigitalOutput resetPin = null;
        try {
            resetPin = pi4j.create(ledConfig);
        } catch (Exception e) {
            e.printStackTrace();
            console.println(String.format("reset_chip  %s", e.toString()), 600);
        }
        try {
            if (bar) {  // active low
                resetPin.low();
                this.sleepMS(delay, console);
                resetPin.high();
            } else {
                resetPin.high();
                this.sleepMS(delay, console);
                resetPin.low();
            }
            this.sleepMS(delay, console);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(101);
        }
        this.logger.trace("Exit: resetChip ");
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void sleepMS(int mSecs, Console console) {
        this.logger.trace("Enter: sleepMS   time  " + mSecs);
        try {
            Thread.sleep(mSecs, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            console.println("Sleep failed");
            System.exit(100);
        }
        this.logger.trace("Exit: sleepMS   ");
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    class StepTimeouts {
        public StepTimeouts(int msrc_dss_tcc_us, int pre_range_us, int final_range_us,
                            int final_range_vcsel_period_pclks, int pre_range_mclks) {
            super();
            this.msrc_dss_tcc_us = msrc_dss_tcc_us;
            this.pre_range_us = pre_range_us;
            this.final_range_us = final_range_us;
            this.final_range_vcsel_period_pclks = final_range_vcsel_period_pclks;
            this.pre_range_mclks = pre_range_mclks;
        }

        int msrc_dss_tcc_us;
        int pre_range_us;
        int final_range_us;
        int final_range_vcsel_period_pclks;
        int pre_range_mclks;
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private int get_measurement_timing_budget() {
        this.logger.trace("Enter: get_measurement_timing_budget");
        // The measurement timing budget in microseconds.
        int budget_us = 1910 + 960; // # Start overhead + end overhead.
        StepEnable stepE = this.get_sequence_step_enables();
        boolean tcc = stepE.tcc;
        boolean dss = stepE.dss;
        boolean msrc = stepE.msrc;
        boolean pre_range = stepE.pre_range;
        boolean final_range = stepE.final_range;

        StepTimeouts step_timeouts = this.get_sequence_step_timeouts(pre_range);
        int msrc_dss_tcc_us = step_timeouts.msrc_dss_tcc_us;
        int pre_range_us = step_timeouts.pre_range_us;
        int final_range_us = step_timeouts.final_range_us;
        int final_range_vcsel_period_pclks = step_timeouts.final_range_vcsel_period_pclks;

        if (tcc) {
            budget_us += msrc_dss_tcc_us + 590;
        }
        if (dss) {
            budget_us += 2 * (msrc_dss_tcc_us + 690);
        } else if (msrc) {
            budget_us += msrc_dss_tcc_us + 660;
        }
        if (pre_range) {
            budget_us += pre_range_us + 660;
        }
        if (final_range) {
            budget_us += final_range_us + 550;
        }
        this.measurement_timing_budget_us = budget_us;
        this.logger.trace("Exit: get_measurement_timing_budget     ms "  + budget_us);
        return (budget_us);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void set_measurement_timing_budget(int budget_us) {
        this.logger.trace("Enter: set_measurement_timing_budget   ms  " + budget_us);
        // assert budget_us >= 20000;
        int used_budget_us = 1320 + 960; // # Start (diff from get) + end
        // overhead
        StepEnable stepE = this.get_sequence_step_enables();
        boolean tcc = stepE.tcc;
        boolean dss = stepE.dss;
        boolean msrc = stepE.msrc;
        boolean pre_range = stepE.pre_range;
        boolean final_range = stepE.final_range;

        StepTimeouts stepT = this.get_sequence_step_timeouts(stepE.pre_range);
        int msrc_dss_tcc_us = stepT.final_range_us;
        int pre_range_us = stepT.final_range_vcsel_period_pclks;
        int xtra = stepT.msrc_dss_tcc_us;

        int final_range_vcsel_period_pclks = stepT.pre_range_mclks;
        int pre_range_mclks = stepT.pre_range_us;

        if (tcc) {
            used_budget_us += msrc_dss_tcc_us + 590;
        }
        if (dss) {
            used_budget_us += 2 * (msrc_dss_tcc_us + 690);
        } else if (msrc) {
            used_budget_us += msrc_dss_tcc_us + 660;
        }
        if (pre_range) {
            used_budget_us += pre_range_us + 660;
        }
        if (final_range) {
            used_budget_us += 550;
        }
        // # "Note that the final range timeout is determined by the timing
        // # budget and the sum of all other timeouts within the sequence.
        // # If there is no room for the final range timeout, then an error
        // # will be set. Otherwise the remaining time will be applied to
        // # the final range."
        if (used_budget_us > budget_us) {
            this.logger.error("Requested timeout too big.");
            System.exit(70);
        }
        int final_range_timeout_us = budget_us - used_budget_us;
        int final_range_timeout_mclks = this.get_timeout_microseconds_to_mclks(final_range_timeout_us,
                final_range_vcsel_period_pclks);
        if (pre_range) {
            final_range_timeout_mclks += pre_range_mclks;
        }
        this.writeDevice16(Vl53L0X_Declares._FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI,
                this.encode_timeout(final_range_timeout_mclks));

        this.measurement_timing_budget_us = budget_us;
        this.logger.trace("Exit: set_measurement_timing_budget  ");

    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private StepTimeouts get_sequence_step_timeouts(boolean pre_range) {
        this.logger.trace("Enter: get_sequence_step_timeouts  pre  " + pre_range);
        // # based on get_sequence_step_timeout() from ST API but modified by
        // # pololu here:
        // # https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
        int pre_range_vcsel_period_pclks = this.get_vcsel_pulse_period(Vl53L0X_Declares._VCSEL_PERIOD_PRE_RANGE);
        int msrc_dss_tcc_mclks = (this.readDevice8(Vl53L0X_Declares._MSRC_CONFIG_TIMEOUT_MACROP) + 1) & 0xFF;
        int msrc_dss_tcc_us = this.timeout_mclks_to_microseconds(msrc_dss_tcc_mclks, pre_range_vcsel_period_pclks);
        int pre_range_mclks = this
                .decode_timeout(this.readDevice16(Vl53L0X_Declares._PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI));
        int pre_range_us = this.timeout_mclks_to_microseconds(pre_range_mclks, pre_range_vcsel_period_pclks);
        int final_range_vcsel_period_pclks = this.get_vcsel_pulse_period(Vl53L0X_Declares._VCSEL_PERIOD_FINAL_RANGE);
        int final_range_mclks = this
                .decode_timeout(this.readDevice16(Vl53L0X_Declares._FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI));
        if (pre_range) {
            final_range_mclks -= pre_range_mclks;
        }
        int final_range_us = this.timeout_mclks_to_microseconds(final_range_mclks, final_range_vcsel_period_pclks);
        StepTimeouts rval = new StepTimeouts(msrc_dss_tcc_us, pre_range_us, final_range_us,
                final_range_vcsel_period_pclks, pre_range_mclks);
        this.logger.trace("Exit: get_sequence_step_timeouts  StepTimeouts  " + rval.toString());
        return (rval);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private int timeout_mclks_to_microseconds(int timeout_period_mclks, int vcsel_period_pclks) {
        this.logger.trace("Enter: timeout_mclks_to_microseconds  mclks   " + timeout_period_mclks + "  pclks " + vcsel_period_pclks);
        int macro_period_ns = Math.floorDiv(((2304 * (vcsel_period_pclks) * 1655) + 500), 1000);
        this.logger.trace("Exit: timeout_mclks_to_microseconds   " +  (timeout_period_mclks * macro_period_ns) + Math.floorDiv(Math.floorDiv(macro_period_ns, 2), 1000));
        return ((timeout_period_mclks * macro_period_ns) + Math.floorDiv(Math.floorDiv(macro_period_ns, 2), 1000));
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private int get_vcsel_pulse_period(int vcsel_period_type) {
        // # pylint: disable=no-else-return
        // # Disable should be removed when refactor can be tested
        this.logger.trace("Enter: get_vcsel_pulse_period  type " + vcsel_period_type);
        int val = 255;
        if (vcsel_period_type == Vl53L0X_Declares._VCSEL_PERIOD_PRE_RANGE) {
            val = this.readDevice8(Vl53L0X_Declares._PRE_RANGE_CONFIG_VCSEL_PERIOD);
            this.logger.trace("Exit: get_vcsel_pulse_period  IF leg " + ((((val) + 1) & 0xFF) << 1));
            return ((((val) + 1) & 0xFF) << 1);
        } else if (vcsel_period_type == Vl53L0X_Declares._VCSEL_PERIOD_FINAL_RANGE) {
            val = this.readDevice8(Vl53L0X_Declares._FINAL_RANGE_CONFIG_VCSEL_PERIOD);
            this.logger.trace("Exit: get_vcsel_pulse_period  ELSE leg " + ((((val) + 1) & 0xFF) << 1));
            return ((((val) + 1) & 0xFF) << 1);
        }
        this.logger.trace("Exit: get_vcsel_pulse_period   " + val);
        return (val);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private StepEnable get_sequence_step_enables() {
        // # based on VL53L0X_GetSequenceStepEnables() from ST API
        this.logger.trace("Enter: get_sequence_step_enables");
        int sequence_config = this.readDevice8(Vl53L0X_Declares._SYSTEM_SEQUENCE_CONFIG);
        // # pylint: disable=bad-whitespace
        boolean tcc = ((sequence_config >> 4) & 0x1) > 0;
        boolean dss = ((sequence_config >> 3) & 0x1) > 0;
        boolean msrc = ((sequence_config >> 2) & 0x1) > 0;
        boolean pre_range = ((sequence_config >> 6) & 0x1) > 0;
        boolean final_range = ((sequence_config >> 7) & 0x1) > 0;
        StepEnable rval = new StepEnable(tcc, dss, msrc, pre_range, final_range);
        this.logger.trace("Exit: get_sequence_step_enables  " + rval.toString()  );
        return (rval);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private long get_measurement_timing_budget_us() {
        this.logger.trace("Enter: get_measurement_timing_budget_us");
        int budget_us = 1910 + 960; // # Start overhead + end overhead.
        StepEnable stepE = this.get_sequence_step_enables();
        StepTimeouts step_timeouts = this.get_sequence_step_timeouts(stepE.pre_range);
        if (stepE.tcc) {
            budget_us += step_timeouts.msrc_dss_tcc_us + 590;
        }
        if (stepE.dss) {
            budget_us += 2 * (step_timeouts.msrc_dss_tcc_us + 690);
        } else if (stepE.msrc) {
            budget_us += step_timeouts.msrc_dss_tcc_us + 660;
        }
        if (stepE.pre_range) {
            budget_us += step_timeouts.pre_range_us + 660;
        }
        if (stepE.final_range) {
            budget_us += step_timeouts.final_range_us + 550;
        }
        this.measurement_timing_budget_us = budget_us;
        this.logger.trace("Exit: get_measurement_timing_budget_us   " + budget_us);

        return budget_us;
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void perform_single_ref_calibration(int vhv_init_byte) {
        // # based on VL53L0X_perform_single_ref_calibration() from ST API.
        this.logger.trace("Enter: perform_single_ref_calibration   byte  "  + vhv_init_byte);
        this.writeDevice8(Vl53L0X_Declares._SYSRANGE_START, (byte) (0x01 | vhv_init_byte & 0xFF));
        float start = System.nanoTime() / (float) 1000000;
        while ((this.readDevice8(Vl53L0X_Declares._RESULT_INTERRUPT_STATUS) & 0x07) == 0) {
            if ((this.io_timeout_s > 0) && (((System.nanoTime() / (float) 1000000) - start) >= this.io_timeout_s)) {
                this.logger.error("Timeout waiting for VL53L0X!");
                System.exit(30);
            }
        }
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_INTERRUPT_CLEAR, (byte) 0x01);
        this.writeDevice8(Vl53L0X_Declares._SYSRANGE_START, (byte) 0x00);
        this.logger.trace("Exit: perform_single_ref_calibration ");
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private int readDevice8() {
        this.logger.trace("Enter: readDevice8 " );
        int rval = 0;
        rval = ((this.device.read()) & 0xFF);
        this.logger.trace("Exit: readDevice8 "   + rval );
        return (rval);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int readDevice8(int address) {
        this.logger.trace("Enter: readDevice8 address  "  + address );
        int rval = 0;
        rval = (((byte) (this.device.readRegister(address))) & 0xFF);
        this.logger.trace("Exit: readDevice8 "   + rval );
        return (rval);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int readDevice16(int address) {
        this.logger.trace("Enter: readDevice16 address  "  + address );
        int rval = 0;
        rval = ((byte) this.device.readRegister(address) & 0xFF) << 8;
        rval = rval | ((byte) this.device.readRegister(address + 1) & 0xFF);
        this.logger.trace("Exit: readDevice16 "   + rval );
        return (rval);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private void writeDevice8(int address) {
        this.logger.trace("Execute: writeDevice8 address  "  + address );
        this.device.write((byte) address);
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void writeDevice8(int address, byte data) {
        this.logger.trace("Execute: writeDevice8 address  "  + address  + "  data  "  + data);
        this.device.writeRegister(address, data);

    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    void writeDevice16(int address, int data) {
        this.logger.trace("Execute: writeDevice16 address  "  + address  + "  data  "  + data);
        this.device.writeRegister(address, (byte) ((data & 0xff00) >> 8));
        this.device.writeRegister(address + 1, (byte) (data & 0xff));

    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private class SpadData {
        public SpadData(byte count, boolean is_aperture) {
            super();
            this.count = count;
            this.is_aperture = is_aperture;
        }

        byte count;
        boolean is_aperture;

    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    SpadData _get_spad_info() {
        // # Get reference SPAD count and type, returned as a 2-tuple of
        // # count and boolean is_aperture. Based on code from:
        // # https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
        this.logger.trace("Enter: _get_spad_info  " );
        int[][] pair = {{0x80, 0x01}, {0xFF, 0x01}, {0x00, 0x00}, {0xFF, 0x06}};
        for (int i = 0; i < pair.length; i++) {
            this.writeDevice8(pair[i][0], (byte) pair[i][1]);
        }

        this.writeDevice8(0x83, (byte) (this.readDevice8(0x83) | 0x04));

        int[][] pair2 = {{0xFF, 0x07}, {0x81, 0x01}, {0x80, 0x01}, {0x94, 0x6B}, {0x83, 0x00}};
        for (int i = 0; i < pair2.length; i++) {
            this.writeDevice8(pair2[i][0], (byte) pair2[i][1]);
        }

        float start = System.nanoTime() / (float) 1000000;
        while (this.readDevice8((byte) 0x83) == 0x00) {
            if ((this.io_timeout_s > 0) && (((System.nanoTime() / (float) 1000000) - start) >= this.io_timeout_s)) {
                this.logger.trace("Timeout waiting for VL53L0X!");
                System.exit(40);
            }
        }

        this.writeDevice8(0x83, (byte) 0x01);
        int tmp = this.readDevice8(0x92);
        byte count = (byte) (tmp & 0x7F);
        this.logger.trace("tmp : " + tmp + "   (tmp >> 7)" + (tmp >> 7));
        boolean is_aperture = (((tmp >> 7) & 0x01) == 1);

        int[][] pair3 = {{0x81, 0x00}, {0xFF, 0x06}};
        for (int i = 0; i < pair3.length; i++) {
            this.writeDevice8(pair3[i][0], (byte) pair3[i][1]);
        }

        int[][] pair4 = {{0xFF, 0x01}, {0x00, 0x01}, {0xFF, 0x00}, {0x80, 0x00}};
        for (int i = 0; i < pair4.length; i++) {
            this.writeDevice8(pair4[i][0], (byte) pair4[i][1]);
        }
        this.logger.trace("Exit: _get_spad_info  count  " +  count + "is_aperture  "   + is_aperture);

        return (new SpadData(count, is_aperture));
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int decode_timeout(int val) {
        this.logger.trace("Enter: decode_timeout   val  "  + val );
        int rval = 0;
        // # format: "(LSByte * 2^MSByte) + 1"
        rval = ((int) ((val & 0xFF) * Math.pow(2.0, ((val & 0xFF00) >> 8)) + 1)) & (0xff);
        this.logger.trace("Exit: decode_timeout   rval  "  + rval );
        return rval;
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int encode_timeout(int timeout_mclks) {
        this.logger.trace("Enter: encode_timeout   mclks  "  + timeout_mclks );
        // format: "(LSByte * 2^MSByte) + 1"
        timeout_mclks = timeout_mclks & 0xFFFF;
        int ls_byte = 0;
        int ms_byte = 0;
        if (timeout_mclks > 0) {
            ls_byte = timeout_mclks - 1;
            while (ls_byte > 255) {
                ls_byte >>= 1;
                ms_byte += 1;
            }
            this.logger.trace("Exit: encode_timeout   mclks  "  + (((ms_byte << 8) | (ls_byte & 0xFF)) & 0xFFFF) );
            return ((ms_byte << 8) | (ls_byte & 0xFF)) & 0xFFFF;
        }
        this.logger.trace("Exit: encode_timeout   mclks  "  +
  0 );        return 0;
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    int _timeout_mclks_to_microseconds(int timeout_period_mclks, int vcsel_period_pclks) {
        this.logger.trace("Enter: _timeout_mclks_to_microseconds  " );
        int macro_period_ns = Math.floorDiv(((2304 * (vcsel_period_pclks) * 1655) + 500), 1000);
        this.logger.trace("Exit: _timeout_mclks_to_microseconds  " + ((timeout_period_mclks * macro_period_ns) + (Math.floorDiv(Math.floorDiv(macro_period_ns, 2), 1000))));
        return ((timeout_period_mclks * macro_period_ns) + (Math.floorDiv(Math.floorDiv(macro_period_ns, 2), 1000)));
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    int get_timeout_microseconds_to_mclks(int timeout_period_us, int vcsel_period_pclks) {
        this.logger.trace("Enter: get_timeout_microseconds_to_mclks  " );
        int macro_period_ns = Math.floorDiv(((2304 * (vcsel_period_pclks) * 1655) + 500), 1000);
        this.logger.trace("Exit: get_timeout_microseconds_to_mclks  " + ((timeout_period_us * 1000) + Math.floorDiv(Math.floorDiv(macro_period_ns, 2), macro_period_ns)));
        return ((timeout_period_us * 1000) + Math.floorDiv(Math.floorDiv(macro_period_ns, 2), macro_period_ns));
    }

    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    int range() {
        int range_mm = 0;
        // """Perform a single reading of the range for an object in front of
        // the sensor and return the distance in millimeters.
        // """
        // # Adapted from readRangeSingleMillimeters &
        // # readRangeContinuousMillimeters in pololu code at:
        // # https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp

        this.logger.trace("Entered range()");
        int[][] pair = {{0x80, 0x01}, {0xFF, 0x01}, {0x00, 0x00}, {0x91, this._stop_variable}, {0x00, 0x01},
                {0xFF, 0x00}, {0x80, 0x00}, {Vl53L0X_Declares._SYSRANGE_START, 0x01}};
        for (int i = 0; i < pair.length; i++) {
            this.writeDevice8(pair[i][0], (byte) pair[i][1]);
        }

        float start = System.nanoTime() / (float) 1000000;
        while ((this.readDevice8(Vl53L0X_Declares._SYSRANGE_START) & 0x01) > 0) {
            if (((System.nanoTime() / (float) 1000000) > 0)
                    && (((System.nanoTime() / (float) 1000000) - start) >= this.io_timeout_s)) {
                this.logger.error("Timeout waiting for VL53L0X!");
                System.exit(60);
            }
        }
        start = System.nanoTime() / (float) 1000000;
        while ((this.readDevice8(Vl53L0X_Declares._RESULT_INTERRUPT_STATUS) & ((byte) 0x07)) == 0) {
            if ((this.io_timeout_s > 0) && (((System.nanoTime() / (float) 1000000) - start) >= this.io_timeout_s)) {
                this.logger.error("Timeout waiting for VL53L0X!");
                System.exit(61);
            }
        }
        // # assumptions: Linearity Corrective Gain is 1000 (default)
        // # fractional ranging is not enabled
        range_mm = this.readDevice16(Vl53L0X_Declares._RESULT_RANGE_STATUS + 10);
        this.writeDevice8(Vl53L0X_Declares._SYSTEM_INTERRUPT_CLEAR, (byte) 0x01);
        this.logger.trace("Exit range()  "  + range_mm);
        return (range_mm);
    }

    double get_signal_rate_limit() {
        // """The signal rate limit in mega counts per second."""
        this.logger.trace("Entered get_signal_rate_limit()");
        int val = this.readDevice16(Vl53L0X_Declares._FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT);
        // # Return value converted from 16-bit 9.7 fixed point to float.
        this.logger.trace("exit range() : "  + (val / (1 << 7)));
        return (val / (1 << 7));
    }

    void set_signal_rate_limit(double val) {
        // assert (val > 0) && (511.99 > val);
        // # Convert to 16-bit 9.7 fixed point value from a float.
        this.logger.trace("Entered set_signal_rate_limit()  val  "   +val);
        val = (val * (1 << 7));
        this.writeDevice16(Vl53L0X_Declares._FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, (int) Math.round(val));
        this.logger.trace("Exit set_signal_rate_limit()");
    }


    private final Context pi4j;
    private byte[] _BUFFER;
    private final int bus;
    private final int address;
    private final int timeout;
    private I2C device;
    private byte _stop_variable;
    private long io_timeout_s;
    private int measurement_timing_budget_us;
    private final Logger logger;

    private int test;

}


