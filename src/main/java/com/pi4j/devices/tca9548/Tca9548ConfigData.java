

package com.pi4j.devices.tca9548;

import com.pi4j.devices.base_util.ffdc.FfdcUtil;

/**
 * Class contains chip specific values.Details found in the chip Data Sheet.
 * <p>
 *
 * @see <a href="https://www.ti.com/lit/ds/symlink/tca9548a.pdf">https://www.ti.com/lit/ds/symlink/tca9548a.pdf</a>
 */

public class Tca9548ConfigData {
    // register sets
    int[] channelBits = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
    int maxBusNumber = channelBits.length;
    FfdcUtil ffdc = null;

    public Tca9548ConfigData(FfdcUtil ffdc) {
        // TODO Auto-generated constructor stub
        this.ffdc = ffdc;
    }


    /**
     * @param busNum Simple int valu representing a bus.
     * @return mask value for the supplied busNum
     * <p>
     * Note: this method uses Software thinking in the bus number range from 0 -7. If you
     * prefer Hardware thinking change the code to accept 1 - 8 as valid bus numbers
     * </p>
     */
    public int getByteMask(int busNum) {
        // TODO Auto-generated method stub
        this.ffdc.ffdcMethodEntry("getByteMask  bus :  " + busNum);
        int rval = -1;
        this.ffdc.ffdcDebugEntry("getByteMask  bus number " + busNum);
        if (busNum >= maxBusNumber) {
            String detail = String.format("Parm bus_num value  : %s   exceeds maximum of :%s ", String.format("0x%02X: ", busNum), String.format("0x%02X ", (maxBusNumber - 1)));
            this.ffdc.ffdcErrorEntry(detail);
        } else {
            rval = ((this.channelBits[busNum]) & 0xFF);
        }
        String detail = String.format("getByteMask rval : %s  ", String.format("0x%02X: ", rval));
        this.ffdc.ffdcMethodExit(detail);
        return (rval);
    }


}
