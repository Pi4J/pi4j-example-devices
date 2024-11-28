/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  AppConfigUtilities.java
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

package com.pi4j.devices.appConfig;


import com.pi4j.context.Context;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.tca9548.Tca9548;
import com.pi4j.devices.tca9548.Tca9548ConfigData;
import com.pi4j.util.Console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.LogManager;


/**
 * <h1>AppConfigUtilities</h1>  Utilities for the  configuration of an I2C chip.
 * Provides details such as: The location of a chip, is it connected directly
 * to the Pi, or behind a mux/switch. If behind a mux/switch, id for that
 * mux/switch and what bus on the mux/switch.
 * <p>
 * Also implements various methods that enable the path from the pi bus
 * the chip. So if the pin/chip that is required is in the configuration files
 * the path from the proper Pi I2C bus will be connected including any required
 * mux/switch being configured.
 * <p>
 * Supports i2c access to the register(s). Individual methods support enable and
 * disabling individual buses. In addition a method support the performing a chip reset.
 * <p>
 * The code is written with use of the datasheet available on the WEB.
 * TCA9548A Low-Voltage 8-Channel I2C Switch with Reset datasheet (Rev. G)
 */

public class AppConfigUtilities {

    /**
     * CTOR.
     *
     * <p>
     * PreCond: AppConfigUtilities CTOR called with valid parameters
     * <ul>
     *     <li>Instantiated Context class
     *     <li> Instantiated FFDC class
     *     <li> BaseGpioInOut
     *     <li> Instantiated Console class
     * </ul>
     * <p>
     * PostCond:  Class methods are now accessable
     */
    public AppConfigUtilities(Context pi4j, FfdcUtil ffdc, BaseGpioInOut gUtil, Console console) {
        LogManager lm = LogManager.getLogManager();
        this.busNumTemp = 0xff;
        this.addressTemp = 0xff;
        this.console = console;
        this.pi4j = pi4j;
        this.ffdc = ffdc;
        this.hasFullKeyedData = false;
        this.hasFullPinKeyedData = false;
        this.mapUtils = new MapUtil(ffdc, gUtil);
        this.dioPinData = gUtil.pinDict;
        ChipNameMap cMap = new ChipNameMap();
        String chips = cMap.readProperties();
        this.chipMap = this.mapUtils.createFullMap(chips);

        GpioToApp gMap = new GpioToApp();
        String pins = gMap.readProperties();
        this.pinMap = this.mapUtils.createXtraFullMap(pins);

        PiPinMap pMap = new PiPinMap();
        String piPins = pMap.readProperties();
        this.piPinMap = this.mapUtils.createFullMap(piPins);


        this.init();

    }

    /**
     * init.
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * <p>
     * PostCond:  AppConfigUtilities state set
     */
    private void init() {
        this.deviceMap = new HashMap<String, HashMap<String, Object>>();
    }


    /**
     * dumpDicts  Debug method to log configuration contents.
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     *
     * <p>
     * PostCond:  If successful return true, else false.
     * </p>
     * <p>
     * Note: register contents for disableBusNumber are effected, all other register
     * contents are NOT modified.
     * </p>
     */
    public void dumpDicts() {
        ffdc.ffdcMethodEntry("dumpDicts");
        ffdc.ffdcDebugEntry("chipMap:  " + this.chipMap);
        ffdc.ffdcDebugEntry("pinMap:  " + this.pinMap);
        ffdc.ffdcDebugEntry("piPinMap:  " + this.piPinMap);
        ffdc.ffdcMethodExit("dumpDicts");
    }


    /**
     * chipMapDetails  Look up dictionary details for a chip
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param chipName String name of target chip
     *                 <p>
     *                 Will search the chipMap for this entry
     *                 </p>
     * @return Dictionary of details for the 'key' chipName
     */
    public HashMap<String, String> chipMapDetails(String chipName) {
        this.ffdc.ffdcMethodEntry("getMapDetails Chip   " + chipName);
        HashMap<String, String> details = this.chipMap.get(chipName);
        this.ffdc.ffdcDebugEntry("chipMapDetails :" + details);
        this.ffdc.ffdcMethodExit("getMapDetails rval  " + details);
        return (details);
    }


    /**
     * getPinMapDetails  Look up dictionary details for a particulare pin
     * on a particular chip chip
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param chipName String name of target chip
     * @param pinName  String name of target pin
     *                 <p>
     *                 Will search the chipMap for the target chip
     *                 the search the  pinMap for the pin
     *                 </p>
     * @return Dictionary of details for the 'key' pin
     */
    public HashMap<String, String> getPinMapDetails(String chipName, String pinName) {
        this.ffdc.ffdcMethodEntry("getPinMapDetails Chip   " + chipName + "  pin  " + pinName);
        HashMap<String, String> rval = null;
        if (this.pinMap.containsKey(chipName)) {
            HashMap<String, HashMap<String, String>> chipDetails = this.pinMap.get(chipName);
            if (chipDetails.containsKey(pinName)) {
                rval = chipDetails.get(pinName);
            }
        }
        this.ffdc.ffdcMethodExit("getPinMapDetails rval  " + rval);
        return (rval);
    }

    /**
     * buildTca9548 Create instance of Tca9548. Use class state.
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @return Tca9548 instantiated
     */
    private Tca9548 buildTca9548() {
        this.ffdc.ffdcMethodEntry("buildTca9548 ");
        Tca9548 tcaObj = new Tca9548(this.pi4j, this.ffdc, this.busNumTemp, this.addressTemp, console);
        this.ffdc.ffdcMethodExit("buildTca9548  :  " + tcaObj);

        return (tcaObj);
    }

    /**
     * mapBus  Using the param data, test if this chip is directly
     * connected to the Pi, or located behind a mux/switch.
     * If behind a mux/switch, configure the mux/switch path so the
     * target chip is accessable
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param target Dictionary entry defining the target.
     * @return boolean to indicate success true, else false
     */
    public boolean mapBus(HashMap<String, String> target) {
        this.ffdc.ffdcMethodEntry("mapBus target : " + target);
        boolean rval = false;
        if (!this.chipMap.containsKey(target.get("chipName"))) {
            this.ffdc.ffdcConfigWarningEntry("chipMap missing key : " + target.get("chipName"));
            this.ffdc.ffdcErrorExit("", 2006);
        } else {
            rval = true;
            HashMap<String, String> chipDetail = this.chipMap.get(target.get("chipName"));

            if (chipDetail.containsKey("behindMux")) {
                String muxDev = chipDetail.get("behindMux");
                HashMap<String, String> muxDetail = this.chipMap.get(muxDev);
                String muxBus = muxDetail.get("busNum");
                String muxAddr = muxDetail.get("address");
                String chipBus = chipDetail.get("busNum");
                int muxAddrI = Integer.parseInt(muxAddr.substring(2), 16);
                this.addressTemp = muxAddrI;
                int muxBusI = Integer.parseInt(muxBus.substring(2), 16);
                this.busNumTemp = muxBusI;
                this.ffdc.ffdcDebugEntry("muxDev : " + muxDev + " muxBus :" + muxBus + " chipBus :" + chipBus);
                if (muxDetail.get("pca").equals("9548")) {
                    // enable the bus
                    // I2cBase i2cDevice = null;

                    Tca9548 tcaObj = this.prime9548(chipDetail);
                    this.ffdc.ffdcDebugEntry("tcaObj : " + tcaObj);
                    tcaObj.enableBus(Integer.parseInt(chipBus.substring(2), 16));
                } else {
                    rval = false;
                    this.ffdc.ffdcConfigWarningEntry("BehindMux device not supported  " + muxDetail.get("pca"));
                }

            } else { // just talk to the bus.
                this.ffdc.ffdcDebugEntry("Not behind mux");
            }
        }
        this.ffdc.ffdcMethodExit("mapBus ");
        return (rval);
    }

    /**
     * enableGpioPath  Using the param data, get the data that describes
     * the chip. Ensure the bus associated with that chip is connected
     * to the Pi.
     * * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param gpio Pin number.
     * @param chip Chip containing the gpio
     * @return boolean to indicate success true, else false
     */
    public boolean enableGpioPath(String gpio, String chip) {
        this.ffdc.ffdcMethodEntry("enableGpioPath  gpio : " + gpio + "   chip :  " + chip);
        boolean rval = false;
        if (this.pinMap.containsKey(chip)) {
            HashMap<String, HashMap<String, String>> chipDetails = this.pinMap.get(chip);
            if (chipDetails.containsKey(gpio)) {
                HashMap<String, String> target = chipDetails.get(gpio);
                rval = this.mapBus(target);
            } else {
                this.ffdc.ffdcConfigWarningEntry(
                    " enableGpioPath  gpio : " + gpio + "   chip :  " + chip + "  not contained in pinMap entry");
            }
        }
        this.ffdc.ffdcMethodExit("enableGpioPath  gpio : " + gpio + "   chip :  " + chip + "   rval : " + rval);
        return (rval);
    }

    /**
     * enableChipPath  Using the param data, get the data that describes
     * the chip. Ensure the bus associated with that chip is connected
     * to the Pi.
     * * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param chip Chip containing the gpio
     * @return boolean to indicate success true, else false
     */
    public boolean enableChipPath(String chip) {
        this.ffdc.ffdcMethodEntry("enableChipPath chip : " + chip);
        boolean rval = false;
        HashMap<String, String> chipDetails = this.getChipMapRec(chip);
        if (chipDetails != null) {
            rval = this.mapBus(chipDetails);
        }
        this.ffdc.ffdcMethodExit("enableChipPath chip : " + chip);

        return (rval);
    }

    /**
     * prime9548  Using the param data, get the required device address
     * and bus number for the TCA9548.
     * Test if there already exists a chip at that address and bus. If
     * one already exists, ensure it is a TCA9548 and return the object,
     * else create a new TCA9548 instance. If a new instance is created
     * update the deviceMap.
     * * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param chipDetails Chip details for the required TCA9548
     * @return Tca9548
     */
    public Tca9548 prime9548(HashMap<String, String> chipDetails) {
        Tca9548 rObj = null;
        this.ffdc.ffdcMethodEntry("prime9548 with   " + chipDetails);

        String muxBus = chipDetails.get("busNum");
        String muxAddr = chipDetails.get("address");
        int muxAddrI = Integer.parseInt(muxAddr.substring(2), 16); // replace("0x",
        // "");
        int muxBusI = Integer.parseInt(muxBus.substring(2), 16); // .replace("0x",
        this.ffdc.ffdcDebugEntry("muxDev : " + muxAddr + " muxBus :" + muxBus);
        // enable the bus
        // I2cBase i2cDevice = null;
        // test if instance already created
        HashMap<String, Object> busDetails;
        if (this.deviceMap.containsKey(muxBus)) {
            busDetails = deviceMap.get(muxBus);
            if (busDetails.containsKey(muxAddr)) {
                Object mux = busDetails.get(muxAddr);
                if (mux.getClass() == Tca9548.class) {
                    rObj = (Tca9548) mux;
                    this.ffdc.ffdcDebugEntry("tcaObj already exist :" + rObj);
                }
            }
        }
        if (rObj == null) {
            this.ffdc.ffdcDebugEntry("tcaObj must be created");
            rObj = this.buildTca9548();
            // create entry into the devceMap entry
            HashMap<String, Object>
                innerMap = new HashMap<String, Object>();
            innerMap.put(muxAddr, rObj);
            this.deviceMap.put(muxBus, innerMap);

        }
        this.ffdc.ffdcDebugEntry("tcaObj : " + rObj);
        // "");
        this.ffdc.ffdcMethodExit("prime9548 with   " + chipDetails + " intp obj : " + rObj);
        return rObj;
    }

    /**
     * displayEnableReg   Using the param data bus and chip,  get the
     * chip details from the chipMap. If chip is a TCA9548, get a reference
     * to that instance and call the method to display the bus enablement details.
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param bus  bus number for the chip
     * @param chip subject chip
     * @return boolean   If successful true, else false
     */
    public boolean displayEnableReg(int bus, String chip) {
        this.ffdc.ffdcMethodEntry("displayEnableReg  in   " + bus + "/" + chip);
        boolean rval = false;
        HashMap<String, String> chipDetails = this.getChipMapRec(chip);
        // make sure the chip is mapped onto the pi bus
        if (chipDetails != null) {
            this.mapBus(chipDetails);
            if (chipDetails.get("pca").equals("9548")) {
                // switch on chipType
                Tca9548 tcaObj = this.prime9548(chipDetails);
                tcaObj.displayBusEnable();
                rval = true;
            }
        } else {
            this.ffdc.ffdcDebugEntry("No details for chip :  " + chip);
        }
        this.ffdc.ffdcMethodExit("displayEnableReg ");
        return (rval);
    }

    /**
     * disableBus    Using the param data bus and chip,  get the
     * chip details from the chipMap. If chip is a TCA9548, get a reference
     * to that instance and call the method to disable bus disableBusNum
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param disableBusNum Bus to disable
     * @param bus           bus number for the chip
     * @param chip          subject chip
     * @return boolean   If successful true, else false
     */
    public boolean disableBus(int disableBusNum, int bus, String chip) {
        this.ffdc.ffdcMethodEntry("disableBus number  " + disableBusNum + "   in chip  " + chip);
        boolean rval = false;
        HashMap<String, String> chipDetails = this.getChipMapRec(chip);
        if (chipDetails != null) {
            // switch on chipType
            this.mapBus(chipDetails);
            if (chipDetails.get("pca").equals("9548")) {
                // switch on chipType
                Tca9548 tcaObj = this.prime9548(chipDetails);
                this.ffdc.ffdcMethodEntry("tcaObj     : " + tcaObj);
                tcaObj.disableBus(disableBusNum);
                rval = true;
            }
        } else {
            this.ffdc.ffdcConfigWarningEntry("invalid bus/chip lookup " + bus + "/ " + chip);
        }
        this.ffdc.ffdcMethodExit("disableBus " + rval);
        return (rval);
    }

    /**
     * enableBus    Using the param data bus and chip,  get the
     * chip details from the chipMap. If chip is a TCA9548, get a reference
     * to that instance and call the method to enable bus enableBusNum
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param enableBusNum Bus to enable
     * @param bus          bus number for the chip
     * @param chip         subject chip
     * @return boolean   If successful true, else false
     */
    public boolean enableBus(int enableBusNum, int bus, String chip) {
        this.ffdc.ffdcMethodEntry("enableBus number  " + enableBusNum + "   in chip  " + chip);
        boolean rval = false;
        HashMap<String, String> chipDetails = this.getChipMapRec(chip);
        if (chipDetails != null) {
            // switch on chipType
            this.mapBus(chipDetails);
            if (chipDetails.get("pca").equals("9548")) {
                // switch on chipType
                Tca9548 tcaObj = this.prime9548(chipDetails);
                tcaObj.enableBus(enableBusNum);
                rval = true;
            }
        } else {
            this.ffdc.ffdcConfigWarningEntry("invalid bus/chip lookup " + bus + "/ " + chip);
        }
        this.ffdc.ffdcMethodExit("disableBus " + rval);
        return (rval);
    }

    /**
     * getChipMapRec    Using the param data chip,  get the
     * chip details from the chipMap.      * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     * @param chip subject chip
     * @return chipeDetail dictionary
     */
    public HashMap<String, String> getChipMapRec(String chip) {
        this.ffdc.ffdcMethodEntry("getChipMapRec  " + chip);
        HashMap<String, String> rval = null;
        if (!this.chipMap.containsKey(chip)) {
            this.ffdc.ffdcErrorExit("chipMap missing key : " + chip, 2020);
        } else {
            rval = this.chipMap.get(chip);
            // HashMap<String, String> chipD = this.getChipMapRec(chip);
        }
        this.ffdc.ffdcMethodExit("getChipMapRec " + rval);
        return (rval);
    }


    /**
     * runCli Execute and display results of the i2cdetect CLI
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     */
    public void runCli() {
        // build my command as a list of strings
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("i2cdetect", "-y", "1");
        Process p = null;
        try {
            p = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
            this.ffdc.ffdcErrorEntry("runCli failed");
        }

        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        int line;
        try {
            while ((line = br.read()) == -1) {
                ffdc.ffdcConfigWarningEntry(is.toString());
            }
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.ffdc.ffdcErrorEntry("runCli failed");
        }
    }


    private final Context pi4j;
    protected int busNumTemp;
    protected int addressTemp;
    private final Console console;
    HashMap<String, HashMap<String, HashMap<String, String>>> pinMap;
    HashMap<String, HashMap<String, String>> chipMap;
    HashMap<String, HashMap<String, String>> piPinMap;
    FfdcUtil ffdc;
    String priChipName;
    String pinName;
    boolean hasFullKeyedData;
    String fullKeyedData;
    boolean hasFullPinKeyedData;
    String fullPinKeyedData;
    public BaseGpioInOut gpio;
    public HashMap<Integer, GpioPinCfgData> dioPinData;
    public Tca9548ConfigData ConfigData;

    public MapUtil mapUtils;

    //      bUS0x?          address0x??   'someInstance
    HashMap<String, HashMap<String, Object>> deviceMap;


}


