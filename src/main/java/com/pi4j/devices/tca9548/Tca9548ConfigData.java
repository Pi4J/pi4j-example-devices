/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Tca9548ConfigData.java
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

import com.pi4j.devices.base_util.ffdc.*;

/**
 * Class contains chip specific values.Details found in the chip Data Sheet.
 * <p>
 *
 * @see <a href="https://www.ti.com/lit/ds/symlink/tca9548a.pdf">https://www.ti.com/lit/ds/symlink/tca9548a.pdf</a>
 */

public class Tca9548ConfigData {
    // register sets
    int[] channel_bits = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
    int maxBusNumber = channel_bits.length;
    FfdcUtil ffdc = null;

    public Tca9548ConfigData(FfdcUtil ffdc) {
        // TODO Auto-generated constructor stub
        this.ffdc = ffdc;
    }


    /**
     * @param bus_num Simple int valu representing a bus.
     * @return mask value for the supplied bus_num
     * <p>
     * Note: this method uses Software thinking in the bus number range from 0 -7. If you
     * prefer Hardware thinking change the code to accept 1 - 8 as valid bus numbers
     * </p>
     */
    public int getByteMask(int bus_num) {
        // TODO Auto-generated method stub
        this.ffdc.ffdcMethodEntry("getByteMask  bus :  " + bus_num);
        int rval = -1;
        this.ffdc.ffdcDebugEntry("getByteMask  bus number " + bus_num);
        if (bus_num >= maxBusNumber) {
            String detail = String.format("Parm bus_num value  : %s   exceeds maximum of :%s ", String.format("0x%02X: ", bus_num), String.format("0x%02X ", (maxBusNumber - 1)));
            this.ffdc.ffdcErrorEntry(detail);
        } else {
            rval = ((this.channel_bits[bus_num]) & 0xFF);
        }
        String detail = String.format("getByteMask rval : %s  ", String.format("0x%02X: ", rval));
        this.ffdc.ffdcMethodExit(detail);
        return (rval);
    }


}
