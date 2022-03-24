package com.pi4j.devices.vl53L0X;


 /*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  Vl53L0X_Declares.java
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

public class Vl53L0X_Declares {

        static int _SYSRANGE_START = 0x00;
        static int _SYSTEM_THRESH_HIGH = 0x0C;
        static int _SYSTEM_THRESH_LOW = 0x0E;
        static int _SYSTEM_SEQUENCE_CONFIG = 0x01;
        static int _SYSTEM_RANGE_CONFIG = 0x09;
        static int _SYSTEM_INTERMEASUREMENT_PERIOD = 0x04;
        static int _SYSTEM_INTERRUPT_CONFIG_GPIO = 0x0A;
        static int _GPIO_HV_MUX_ACTIVE_HIGH = 0x84;
        static int _SYSTEM_INTERRUPT_CLEAR = 0x0B;
        static int _RESULT_INTERRUPT_STATUS = 0x13;
        static int _RESULT_RANGE_STATUS = 0x14;
        static int _RESULT_CORE_AMBIENT_WINDOW_EVENTS_RTN = 0xBC;
        static int _RESULT_CORE_RANGING_TOTAL_EVENTS_RTN = 0xC0;
        static int _RESULT_CORE_AMBIENT_WINDOW_EVENTS_REF = 0xD0;
        static int _RESULT_CORE_RANGING_TOTAL_EVENTS_REF = 0xD4;
        static int _RESULT_PEAK_SIGNAL_RATE_REF = 0xB6;
        static int _ALGO_PART_TO_PART_RANGE_OFFSET_MM = 0x28;
        static int _I2C_SLAVE_DEVICE_ADDRESS = 0x8A;
        static int _MSRC_CONFIG_CONTROL = 0x60;
        static int _PRE_RANGE_CONFIG_MIN_SNR = 0x27;
        static int _PRE_RANGE_CONFIG_VALID_PHASE_LOW = 0x56;
        static int _PRE_RANGE_CONFIG_VALID_PHASE_HIGH = 0x57;
        static int _PRE_RANGE_MIN_COUNT_RATE_RTN_LIMIT = 0x64;
        static int _FINAL_RANGE_CONFIG_MIN_SNR = 0x67;
        static int _FINAL_RANGE_CONFIG_VALID_PHASE_LOW = 0x47;
        static int _FINAL_RANGE_CONFIG_VALID_PHASE_HIGH = 0x48;
        static int _FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT = 0x44;
        static int _PRE_RANGE_CONFIG_SIGMA_THRESH_HI = 0x61;
        static int _PRE_RANGE_CONFIG_SIGMA_THRESH_LO = 0x62;
        static int _PRE_RANGE_CONFIG_VCSEL_PERIOD = 0x50;
        static int _PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI = 0x51;
        static int _PRE_RANGE_CONFIG_TIMEOUT_MACROP_LO = 0x52;
        static int _SYSTEM_HISTOGRAM_BIN = 0x81;
        static int _HISTOGRAM_CONFIG_INITIAL_PHASE_SELECT = 0x33;
        static int _HISTOGRAM_CONFIG_READOUT_CTRL = 0x55;
        static int _FINAL_RANGE_CONFIG_VCSEL_PERIOD = 0x70;
        static int _FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI = 0x71;
        static int _FINAL_RANGE_CONFIG_TIMEOUT_MACROP_LO = 0x72;
        static int _CROSSTALK_COMPENSATION_PEAK_RATE_MCPS = 0x20;
        static int _MSRC_CONFIG_TIMEOUT_MACROP = 0x46;
        static int _SOFT_RESET_GO2_SOFT_RESET_N = 0xBF;
        static int _IDENTIFICATION_MODEL_ID = 0xC0;
        static int _IDENTIFICATION_REVISION_ID = 0xC2;
        static int _OSC_CALIBRATE_VAL = 0xF8;
        static int _GLOBAL_CONFIG_VCSEL_WIDTH = 0x32;
        static int _GLOBAL_CONFIG_SPAD_ENABLES_REF_0 = 0xB0;
        static int _GLOBAL_CONFIG_SPAD_ENABLES_REF_1 = 0xB1;
        static int _GLOBAL_CONFIG_SPAD_ENABLES_REF_2 = 0xB2;
        static int _GLOBAL_CONFIG_SPAD_ENABLES_REF_3 = 0xB3;
        static int _GLOBAL_CONFIG_SPAD_ENABLES_REF_4 = 0xB4;
        static int _GLOBAL_CONFIG_SPAD_ENABLES_REF_5 = 0xB5;
        static int _GLOBAL_CONFIG_REF_EN_START_SELECT = 0xB6;
        static int _DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD = 0x4E;
        static int _DYNAMIC_SPAD_REF_EN_START_OFFSET = 0x4F;
        static int _POWER_MANAGEMENT_GO1_POWER_FORCE = 0x80;
        static int _VHV_CONFIG_PAD_SCL_SDA__EXTSUP_HV = 0x89;
        static int _ALGO_PHASECAL_LIM = 0x30;
        static int _ALGO_PHASECAL_CONFIG_TIMEOUT = 0x30;
        static int _VCSEL_PERIOD_PRE_RANGE = 0;
        static int _VCSEL_PERIOD_FINAL_RANGE = 1;
        static int _VL53L0X_DEFAULT_ADDRESS  = 0x29;

    }

