/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Tca9548.java
 *  *   *
 *  *   * This file is part of the Pi4J project. More information about
 *  *   * this project can be found here:  https://pi4j.com/
 *  *   * **********************************************************************
 *    * %%
 *  *   * Copyright (C) 2012 - 2021 Pi4J
 *     * %%
 *    * Licensed under the Apache License, Version 2.0 (the "License");
 *    * you may not use this file except in compliance with the License.
 *    * You may obtain a copy of the License at
 *    *
 *    *      http://www.apache.org/licenses/LICENSE-2.0
 *    *
 *    * Unless required by applicable law or agreed to in writing, software
 *    * distributed under the License is distributed on an "AS IS" BASIS,
 *    * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    * See the License for the specific language governing permissions and
 *    * limitations under the License.
 *    * #L%
 *  *
 *  *
 *
 *
 */

package com.pi4j.devices.tca9548;


import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;
import com.pi4j.devices.base_i2c.BasicI2cDevice;
import com.pi4j.devices.base_util.gpio.GpioBasics;


/**
 * <h1>Tca9548</h1> Supports i2c access to the register(s). Individual methods support enable and
 * disabling individual buses. In addition a method support the performing a chip reset.
 * <p>
 * The code is written with use of the datasheet available on the WEB.
 * TCA9548A Low-Voltage 8-Channel I2C Switch with Reset datasheet (Rev. G)
 *
 * @see <a href="https://www.ti.com/lit/ds/symlink/tca9548a.pdf">https://www.ti.com/lit/ds/symlink/tca9548a.pdf</a>
 */

public class Tca9548 extends BasicI2cDevice implements GpioBasics {


    Tca9548ConfigData cfgData = null;

    /**
     * CTOR.
     *
     * <p>
     * PreCond: Tca9548 CTOR called with valid parameters
     * <ul>
     *     <li>Instantiated Context class
     *     <li> Instantiated FFDC class
     *     <li> Number of existing/functional Pi i2c bus
     *     <li> Address of i2c device connected to the bus identified by busNum
     *     <li> Instantiated Console class
     * </ul>
     * <p>
     * PostCond:  Class methods are now accessable
     */
    public Tca9548(Context pi4j, FfdcUtil ffdc, int busNum, int address, Console console) {
        super(pi4j, ffdc, busNum, address, console);
        init();
    }

    /**
     * init.
     * <p>
     * PreCond: Tca9548 instance initialized.  See CTOR
     *
     * <p>
     * PostCond:  Tca9548ConfigData state cfgData set
     */
    private void init() {
        this.cfgData = new Tca9548ConfigData(this.ffdc);
    }


    /**
     * disableBus.Disable path of bus 'disableBusNum' through switch.
     * <p>
     * PreCond: Tca9548 instance initialized.  See CTOR
     *
     * @param disableBusNum Bus to disable
     *                        <p>
     *                        PostCond:  If successful return true, else false.
     *                        </p>
     *                        <p>
     *                        Note: register contents for disableBusNumber are effected, all other register
     *                        contents are NOT modified.
     *                        </p>
     */
    public boolean disableBus(int disableBusNum) {
        // TODO Auto-generated method stub
        boolean rval = true;
        byte reg;
        reg = 0;
        this.ffdc.ffdcMethodEntry(this.getMethodName() + "  bus number: " + disableBusNum);

        try {
            reg =  this.readByte();
        } catch (java.io.IOException e) {
            this.ffdc.ffdcErrorEntry("disableBus readByte failed");
            rval = false;
        }

        int busBits = this.cfgData.getByteMask(disableBusNum);
        if (busBits == -1) {
            this.ffdc.ffdcConfigWarningEntry("bus number not mapped to a byte mask");
            rval = false;
        }

        if (rval) {
          String regAstr = "";
            int val = busBits;
            regAstr = regAstr.concat("busBits \n");
            regAstr = regAstr.concat("      " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "     "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                    + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
            this.ffdc.ffdcDebugEntry(regAstr);

            val = ~busBits;
            regAstr = regAstr.concat("busBits \n");
            regAstr = regAstr.concat("      " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "     "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                    + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
            this.ffdc.ffdcDebugEntry(regAstr);
            reg = (byte) (reg & (~busBits));
            reg = (byte) this.write(reg);
            rval = this.examineReturnWrite(reg);
        }
        this.ffdc.ffdcMethodExit((this.getMethodName() + " rval :" + rval));
        return (rval);
    }

    /**
     * enableBus.Enable path of bus 'enableBusNum' through switch.
     * <p>
     * PreCond: Tca9548 instance initialized.  See CTOR
     *
     * @param enableBusNum Bus to enable
     *
     *                       <p>
     *                       PostCond:  If successful return true, else false.
     *                       </p>
     *                       <p>
     *                       Note: register contents for enableBusNumber are effected, all other register
     *                       contents are NOT modified.
     *                       </p>
     */
    public boolean enableBus(int enableBusNum) {
        byte reg = 0;
        int rcode = 0;
        boolean rval = true;
        this.ffdc.ffdcMethodEntry(this.getMethodName() + "  bus number: " + enableBusNum);

        try {
            reg = this.readByte();
        } catch (java.io.IOException e) {
            rval = false;
        }

        int busBits = this.cfgData.getByteMask(enableBusNum);
        if (busBits == -1) {
            this.ffdc.ffdcConfigWarningEntry("bus number not mapped to a byte mask");
            rval = false;
        }

        if(rval){
            reg = (byte) (reg | busBits);
            rcode = this.write(reg);
            rval = this.examineReturnWrite(rcode);
         }
        this.ffdc.ffdcMethodExit((this.getMethodName() + " rval :" + rval));
        return (rval);
    }

    /**
     * displayBusEnable.Pretty print bus enablement through switch.
     * <p>
     * PreCond: Tca9548 instance initialized.  See CTOR
     *
     * <p>
     * PostCond:  Register contents displayed
     */
    public void displayBusEnable() {

        byte reg = (byte) 0xff;
        byte val;
        boolean worked = true;

        this.ffdc.ffdcMethodEntry(this.getMethodName());
        String pinName = "      Bus7   Bus6  Bus5   Bus4   Bus3   Bus2   Bus1   Bus0";

        try {
            reg = this.readByte();
        } catch (java.io.IOException e) {
            worked = false;
        }

        if (worked) {
            String regAstr = "";

            val = reg;
            regAstr = regAstr.concat("tca9548 bus enable register  \n");
            regAstr = regAstr.concat(pinName + "\n");
            regAstr = regAstr.concat("      " + ((val & 0x80) >> 7) + "      " + ((val & 0x40) >> 6) + "     "
                    + ((val & 0x20) >> 5) + "      " + ((val & 0x10) >> 4) + "      " + ((val & 0x08) >> 3) + "      "
                    + ((val & 0x04) >> 2) + "      " + ((val & 0x02) >> 1) + "      " + ((val & 0x01)) + "\n");
            this.ffdc.ffdcDebugEntry(regAstr);
            System.out.println(regAstr);
        }
        this.ffdc.ffdcMethodExit(this.getMethodName());

    }

    /**
     * resetChip.Drive chip reset pin low to effect a reset, restore pin to high to enable chip
     * operation.
     *
     * <p>
     * PreCond: Tca9548 instance initialized.  See CTOR
     *
     * @param resetGpio pin number.  Uses the Broadcom pin numbering
     *
     *                  <p>
     *                  PostCond:  Chip reset.
    */
    public void resetChip(int resetGpio) {
        this.ffdc.ffdcMethodEntry(this.getMethodName() + "  GPIO" + resetGpio);
        var ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("restPin")
                .name("tca95448 reset")
                .address(resetGpio)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("pigpio-digital-output");
        DigitalOutput resetPin = null;
        try {
            resetPin = pi4j.create(ledConfig);
        } catch (Exception e) {
            e.printStackTrace();
            this.ffdc.ffdcErrorEntry("create DigOut failed");
            this.ffdc.ffdcErrorExit("create DigOut failed", 200);
        }
        try {
            resetPin.low();
            this.sleepMS(1500, this.ffdc);
            resetPin.high();
        } catch (IOException e) {
            e.printStackTrace();
            this.ffdc.ffdcErrorEntry("drive DigOut failed");
            this.ffdc.ffdcErrorExit("drive DigOut failed", 201);
        }
        this.ffdc.ffdcMethodExit(this.getMethodName());
    }



}
