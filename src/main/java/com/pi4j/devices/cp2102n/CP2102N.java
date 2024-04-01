/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  CP2102N.java
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

package com.pi4j.devices.cp2102n;

import com.pi4j.context.Context;
import com.pi4j.devices.vl53L0X.VL53L0X_Device;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.StopBits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CP2102N {
    public CP2102N(Context pi4j, String traceLevel) {
        super();
        this.pi4j = pi4j;
        this.traceLevel = traceLevel;
        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + CP2102N.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(CP2102N.class);
        this.device = this.createSerialDevice();
        this.init();

    }


    Serial createSerialDevice() {
        String id = String.format("CP2102N");
        String name = String.format("Serial-USB");
        this.logger.info("Enter: createSerialDevice CP2102N" + id + "" + name);
        Serial rval = null;
        rval =pi4j.create(Serial.newConfigBuilder(pi4j)
                .use_9600_N81()
                .dataBits_8()
                .parity(Parity.NONE) //or ODD or EVEN
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
                .id(id)
                .name(name)
                .device("/dev/ttyAMA0")
                .provider("linuxfsserial")
                .build());

        this.logger.info("Exit: createSerialDevice CP2102N" + id + "" + name);
        return (rval);
    }

    void init() {
        this.logger.info("Enter:init ");
        this.device.open();
        this.logger.info("Exit: init  ");

    }

    public void sendData(String s) {
        this.logger.info("Enter:sendData  data : " + s);
        if(this.device.isOpen() == false){
            this.logger.error("Serial device not open");
            System.exit(43);
        }
        byte[] command = s.getBytes();
        for (int c = 0; c < command.length; c++) {
            this.logger.trace("Uart send byte "  + c  + "  :" +String.format("%02X ", command[c]));
        }
        this.logger.trace(command.toString());
        this.device.write(command);
        this.logger.info("Exit:sendData ");
    }



    private final Context pi4j;
    private final Logger logger;
    private Serial device;
    private final String traceLevel;


}
