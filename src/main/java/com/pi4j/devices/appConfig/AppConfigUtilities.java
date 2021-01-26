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


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.pi4j.Pi4J;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.mcp23017.Mcp23017;
import com.pi4j.devices.mcp23008.Mcp23008;

import com.pi4j.context.Context;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxAppProcessParms;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.devices.mcp23xxxCommon.McpBase;
import com.pi4j.devices.mcp23xxxCommon.Mcp23xxxUtil;
import com.pi4j.devices.mcp23xxxCommon.McpConfigData;
import com.pi4j.devices.tca9548.Tca9548;
import com.pi4j.devices.tca9548.Tca9548ConfigData;
import com.pi4j.exception.Pi4JException;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.util.Console;
import com.pi4j.devices.base_util.mapUtil.MapUtil;


/**
 * <h1>AppConfigUtilities</h1>  Utilities for the  configuration of an I2C chip.
 * Provides details such as: The location of a chip, is it connected directly
 * to the Pi, or behind a mux/switch. If behind a mux/switch, id for that
 * mux/switch and what bus on the mux/switch.
 *
 * Also implements various methods that enable the path from the pi bus
 * the chip. So if the pin/chip that is required is in the configuration files
 * the path from the proper Pi I2C bus will be connected including any required
 * mux/switch being configured.
 *
 * Supports i2c access to the register(s). Individual methods support enable and
 * disabling individual buses. In addition a method support the performing a chip reset.
 * <p>
 * The code is written with use of the datasheet available on the WEB.
 * TCA9548A Low-Voltage 8-Channel I2C Switch with Reset datasheet (Rev. G)
 *
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
    public AppConfigUtilities(Context pi4j, FfdcUtil ffdc,  BaseGpioInOut gUtil, Console console) {
            LogManager lm = LogManager.getLogManager();
            this.busNumTemp = 0xff;
            this.addressTemp = 0xff;
            this.console = console;
            this.pi4j = pi4j;
            this.ffdc = ffdc;
            this.has_full_keyed_data = false;
            this.has_full_pin_keyed_data = false;
            this.mapUtils = new MapUtil( ffdc, gUtil);
            this.dioPinData = gUtil.pinDict;
            ChipNameMap cMap = new ChipNameMap();
            String chips = cMap.read_properties();
            this.chipMap = this.mapUtils.createFullMap(chips);

            GpioToApp gMap = new GpioToApp();
            String pins = gMap.read_properties();
            this.pinMap = this.mapUtils.createXtraFullMap(pins);

            PiPinMap pMap = new PiPinMap();
            String piPins = pMap.read_properties();
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
    private void init(){
            this.deviceMap = new HashMap<String, HashMap<String, Object>>();
        }


    /**
     * dumpDicts  Debug method to log configuration contents.
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     *
     *
     *                        <p>
     *                        PostCond:  If successful return true, else false.
     *                        </p>
     *                        <p>
     *                        Note: register contents for disable_bus_number are effected, all other register
     *                        contents are NOT modified.
     *                        </p>
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
     * @param chipName String name of target chip
     *                 <p>
     *                 Will search the chipMap for this entry
     *                 </p>
     * @return  Dictionary of details for the 'key' chipName
     */
    public HashMap<String, String> chipMapDetails(String chipName) {
        this.ffdc.ffdcMethodEntry("getMapDetails Chip   " + chipName);
        HashMap<String, String> details = this.chipMap.get(chipName);
        this.ffdc.ffdcDebugEntry("chipMapDetails :" + details);
        this.ffdc.ffdcMethodExit("getMapDetails rval  " + details);
        return(details);
    }


    /**
     * getPinMapDetails  Look up dictionary details for a particulare pin
     * on a particular chip chip
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     * @param chipName String name of target chip
     * @param pinName String name of target pin
     *                 <p>
     *                 Will search the chipMap for the target chip
     *                the search the  pinMap for the pin
     *                 </p>
     * @return  Dictionary of details for the 'key' pin
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
     * @return  Tca9548 instantiated
     */
    private Tca9548 buildTca9548() {
            this.ffdc.ffdcMethodEntry("buildTca9548 ");
            Tca9548 tcaObj = new Tca9548(this.pi4j,this.ffdc, this.busNumTemp, this.addressTemp, console);
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
     * @param target Dictionary entry defining the target.
     * @return  boolean to indicate success true, else false
     *
     */
    public boolean mapBus(HashMap<String, String> target) {
            this.ffdc.ffdcMethodEntry("mapBus target : " + target);
            boolean rval = false;
            if (this.chipMap.containsKey(target.get("chipName")) == false) {
                this.ffdc.ffdcConfigWarningEntry("chipMap missing key : " + target.get("chipName"));
                this.ffdc.ffdcErrorExit("",2006);
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
     * @param gpio  Pin number.
     * @param  chip  Chip containing the gpio
     * @return  boolean to indicate success true, else false
     *
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
     * @param  chip  Chip containing the gpio
     * @return  boolean to indicate success true, else false
     *
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
     * @param  chipDetails  Chip details for the required TCA9548
     * @return  Tca9548
     *
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
            if(this.deviceMap.containsKey(muxBus)) {
                busDetails = deviceMap.get(muxBus);
                if (busDetails.containsKey(muxAddr)) {
                    Object mux = busDetails.get(muxAddr);
                    if (mux.getClass() == Tca9548.class) {
                        rObj = (Tca9548) mux;
                        this.ffdc.ffdcDebugEntry("tcaObj already exist :" + rObj);
                    }
                }
            }
            if(rObj == null) {
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
     *  PreCond: AppConfigUtilities instance initialized.  See CTOR
     * @param  bus   bus number for the chip
     * @param  chip subject chip
     * @return  boolean   If successful true, else false
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
     * to that instance and call the method to disable bus disable_bus_num
     * <p>
     *  PreCond: AppConfigUtilities instance initialized.  See CTOR
     * @param disable_bus_num  Bus to disable
     * @param  bus   bus number for the chip
     * @param  chip subject chip
     * @return  boolean   If successful true, else false
     */
    public boolean disableBus(int disable_bus_num, int bus, String chip) {
            this.ffdc.ffdcMethodEntry("disableBus number  " + disable_bus_num + "   in chip  " + chip);
            boolean rval = false;
            HashMap<String, String> chipDetails = this.getChipMapRec(chip);
            if (chipDetails != null) {
                // switch on chipType
                this.mapBus(chipDetails);
                if (chipDetails.get("pca").equals("9548")) {
                    // switch on chipType
                    Tca9548 tcaObj = this.prime9548(chipDetails);
                    this.ffdc.ffdcMethodEntry("tcaObj     : " + tcaObj);
                    tcaObj.disableBus(disable_bus_num);
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
     * to that instance and call the method to enable bus enable_bus_num
     * <p>
     *  PreCond: AppConfigUtilities instance initialized.  See CTOR
     * @param enable_bus_num  Bus to enable
     * @param  bus   bus number for the chip
     * @param  chip subject chip
     * @return  boolean   If successful true, else false
     */
        public boolean enableBus(int enable_bus_num, int bus, String chip) {
            this.ffdc.ffdcMethodEntry("enableBus number  " + enable_bus_num + "   in chip  " + chip);
            boolean rval = false;
            HashMap<String, String> chipDetails = this.getChipMapRec(chip);
            if (chipDetails != null) {
                // switch on chipType
                this.mapBus(chipDetails);
                if (chipDetails.get("pca").equals("9548")) {
                    // switch on chipType
                    Tca9548 tcaObj = this.prime9548(chipDetails);
                    tcaObj.enableBus(enable_bus_num);
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
     *  PreCond: AppConfigUtilities instance initialized.  See CTOR
     * @param  chip subject chip
     * @return  chipeDetail dictionary
     */
    public HashMap<String, String> getChipMapRec(String chip) {
            this.ffdc.ffdcMethodEntry("getChipMapRec  " + chip);
            HashMap<String, String> rval = null;
            if (this.chipMap.containsKey(chip) == false) {
                this.ffdc.ffdcErrorExit("chipMap missing key : " + chip, 2020);
            } else {
                rval = this.chipMap.get(chip);
                // HashMap<String, String> chipD = this.getChipMapRec(chip);
            }
            this.ffdc.ffdcMethodExit("getChipMapRec " + rval);
            return (rval);
        }

    /**
     * usage    Display help text for using this program
     * <p>
     *  PreCond: AppConfigUtilities instance initialized.  See CTOR
     */
    public void usage() {
            console.println("options   -h 'help', -b bus, -a address, -z mainChip " + "-c chipName  -p  pinName   "
                    + "  -x reset-chip  -n reset GPIO -f ffdc_lvl -g gpiodict   -m mcp23xx_pin_onfig   -s log");
        }

    /**
     *   runCli Execute and display results of the i2cdetect CLI
     * <p>
     *  PreCond: AppConfigUtilities instance initialized.  See CTOR
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
            int line ;
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
        private int busNumTemp;
        private int addressTemp;
        private Console console;



        public static void main(String[] args) {
            // TODO Auto-generated method stub
            var console = new Console();
            Context pi4j = null;
            try {
                pi4j = Pi4J.newAutoContext();
            } catch (Pi4JException e) {
                e.printStackTrace();
            }

            FfdcUtil ffdc = new FfdcUtil(console, pi4j, 0, AppConfigUtilities.class);
            // tca9548 tcaObj = new tca9548();
            // tcaObj.logger = Logger.getLogger("tca9548");
            // tcaObj.ConfigData = new tca9548ConfigData(ffdc);
            // tcaObj
            HashMap<Integer, GpioPinCfgData> dioPinData = new HashMap<Integer, GpioPinCfgData>();
            // cfgU.ffdc = ffdc;

            // tcaObj.gpio = new BaseGpioInOut(ffdc, dioPinData,ffdc.logger);

            // tcaObj.ConfigData = new tca9548ConfigData(ffdc);
            BaseGpioInOut gUtil = new BaseGpioInOut(pi4j, ffdc, dioPinData);
            gUtil.dumpHashMap();

            MapUtil mapUtils = new MapUtil(ffdc, gUtil);
            AppConfigUtilities cfgU = new AppConfigUtilities(pi4j, ffdc,  gUtil, console);
            // later in this function, the device address and bus number will be properly set

            boolean returned = false;

            // HashMap<String, HashMap<String, HashMap<String, String>>> xMap =
            // cfgU.createXtraFullMap("testString");
            // console.print(xMap);

            int ffdcLvl = 0;
            boolean badParm = false;
            String badParmDetail = "";
            String mainChip = null;
            int mainBus = 0;
            for (int i = 0; i < args.length; i++) {
                String o = args[i];
                if (o.contentEquals("-f")) {
                    String a = args[i + 1];
                    i++;
                    ffdcLvl = Integer.parseInt(a);
                    cfgU.ffdc.setLevel(ffdcLvl);
                } else if (o.contentEquals("-h")) {
                    cfgU.usage();
                    System.exit(0);
                } else if (o.contentEquals("-z")) { // mainChip
                    mainChip = args[i + 1];

                    // >>>tcaObj.bus_num = Integer.parseInt(a.substring(2), 16);
                    i++;
                } else if (o.contentEquals("-c")) { // primarydevice name
                    String a = args[i + 1]; // chip name
                    i++;
                    cfgU.priChipName = a;
                    console.println("-c : " + cfgU.priChipName);
                    // display_main.address = Integer.parseInt(a, 16);
                } else if (o.contentEquals("-p")) { // primarydevice address
                    String a = args[i + 1]; // chip name/
                    i++;
                    cfgU.pinName = a;
                    console.println("-p : " + cfgU.pinName);

                    // display_main.address = Integer.parseInt(a, 16);
                } else if (o.contentEquals("-g")) { // pi GPIO pins
                    cfgU.has_full_keyed_data = true;
                    cfgU.full_keyed_data = args[i + 1];
                    i++;
                } else if (o.contentEquals("-m")) { // mcp23xx pins
                    cfgU.has_full_pin_keyed_data = true;
                    cfgU.full_pin_keyed_data = args[i + 1];
                    i++;
                }
            }

            HashMap<String, String> initialChipD = cfgU.getChipMapRec(mainChip);
            String initialChipBus = initialChipD.get("busNum");
            String initialChipAddr = initialChipD.get("address");
            mainBus = Integer.parseInt(initialChipBus.substring(2), 16);

            cfgU.addressTemp = Integer.parseInt(initialChipAddr.substring(2), 16);
            cfgU.busNumTemp = mainBus;

            HashMap<String, String> chipD = cfgU.getChipMapRec(cfgU.priChipName);
            String banked = chipD.get("banked");
            String chipBus = chipD.get("busNum");
            String chipAddr = chipD.get("address");

            boolean isBanked = false;
            if (banked.equalsIgnoreCase("y")) {
                isBanked = true;
            }
            int chipAddrInt = Integer.parseInt(chipAddr.substring(2), 16);
            McpConfigData mcpcfgD = new McpConfigData(cfgU.ffdc);
            Mcp23xxxParms parmsObj = Mcp23xxxAppProcessParms.processMain(pi4j, args, true, dioPinData, console);

            McpBase mcpObj = null;
            if (isBanked) {
                mcpObj = new Mcp23017(pi4j, parmsObj, cfgU.ffdc, dioPinData, console);
            } else {
                mcpObj = new Mcp23008(pi4j, parmsObj, cfgU.ffdc, dioPinData, console);
            }

            Mcp23xxxUtil mcpUtil = new Mcp23xxxUtil(pi4j, cfgU.ffdc, Integer.parseInt(initialChipBus.substring(2), 16),
                    chipAddrInt, mcpcfgD, mcpObj, console);
            if (cfgU.has_full_keyed_data) {
                cfgU.ffdc.ffdcDebugEntry("Start of has_full_keyed_data");
                HashMap<String, HashMap<String, String>> outerMap = cfgU.mapUtils.createFullMap(cfgU.full_keyed_data);
                mcpcfgD.replaceMap(outerMap);
                gUtil.createGpioInstance(mcpcfgD.getFullMap());
            }
            if (cfgU.has_full_pin_keyed_data) {
                cfgU.ffdc.ffdcDebugEntry("Start of has_full_pin_keyed_data");
                HashMap<String, HashMap<String, String>> pinMap = cfgU.mapUtils.createFullMap(cfgU.full_pin_keyed_data);
                mcpcfgD.replaceMap(pinMap);
            }
            /*
             * try { // TODO how to get first chip address details ????
             * tcaObj.i2cDevice = new I2cBase(tcaObj.bus_num, tcaObj.address,
             * cfgU.ffdc); cfgU.ffdc.ffdcDebugEntry(" i2c device ptr in tca obj  : " +
             * tcaObj.i2cDevice); } catch (InterruptedException e) { // TODO
             * Auto-generated catch block e.printStackTrace();
             * cfgU.ffdc.errorExit(2001); } catch (PlatformAlreadyAssignedException
             * e) { // TODO Auto-generated catch block cfgU.ffdc.errorExit(2001);
             * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
             * catch block cfgU.ffdc.errorExit(2001); e.printStackTrace(); } catch
             * (UnsupportedBusNumberException e) { // TODO Auto-generated catch
             * block cfgU.ffdc.errorExit(2001); e.printStackTrace(); }
             */
            cfgU.disableBus(0, mainBus, mainChip);
            cfgU.disableBus(1, mainBus, mainChip);
            cfgU.disableBus(2, mainBus, mainChip);
            cfgU.disableBus(3, mainBus, mainChip);
            cfgU.disableBus(4, mainBus, mainChip);
            cfgU.disableBus(5, mainBus, mainChip);
            cfgU.disableBus(6, mainBus, mainChip);
            cfgU.disableBus(7, mainBus, mainChip);
            cfgU.displayEnableReg(mainBus, mainChip);
            // see if target chip behind a mux. If so set up mux
            cfgU.ffdc.ffdcDebugEntry("Args to enable " + cfgU.pinName + "   " + cfgU.priChipName);
            returned = cfgU.enableGpioPath(cfgU.pinName, cfgU.priChipName); // path
            // to
            // mcp23xx

            cfgU.ffdc.ffdcDebugEntry("bus  " + chipAddr + "    distant device " + chipAddr + " should be visible");
            cfgU.runCli();
            cfgU.displayEnableReg(mainBus, mainChip);

            // above we mapped the path to this MCP chip so the process keyed
            // data can acces the chip.

            if (cfgU.has_full_pin_keyed_data) {
                cfgU.ffdc.ffdcDebugEntry("Process has_full_pin_keyed_data");
                mcpUtil.process_keyed_data(); // configure the target mcp23xx
            }                 // chip

            // test the configured pin
            mcpUtil.drive_pin(Integer.parseInt(cfgU.pinName.substring(3)), true);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drive_pin(Integer.parseInt(cfgU.pinName.substring(3)), false);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drive_pin(Integer.parseInt(cfgU.pinName.substring(3)), true);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drive_pin(Integer.parseInt(cfgU.pinName.substring(3)), false);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drive_pin(Integer.parseInt(cfgU.pinName.substring(3)), true);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drive_pin(Integer.parseInt(cfgU.pinName.substring(3)), false);


            cfgU.disableBus(0, mainBus, mainChip);
            cfgU.disableBus(1, mainBus, mainChip);
            cfgU.disableBus(2, mainBus, mainChip);
            cfgU.disableBus(3, mainBus, mainChip);
            cfgU.disableBus(4, mainBus, mainChip);
            cfgU.disableBus(5, mainBus, mainChip);
            cfgU.disableBus(6, mainBus, mainChip);
            cfgU.disableBus(7, mainBus, mainChip);

            // expect fail
            returned = cfgU.enableGpioPath("pin42", "23008#1");

            // no mapping
            console.println("bus Nope, Pi pin");

            returned = cfgU.enableGpioPath("dio12", "23008#1");

            // Windows
            console.println("bus 1");
            returned = cfgU.enableGpioPath("pin14", "23008#1");

            cfgU.runCli();

            cfgU.displayEnableReg(mainBus, mainChip);

            cfgU.disableBus(1, mainBus, mainChip);

            // pin15
            if (isBanked) {
                returned = cfgU.enableGpioPath("pin4", "23017#2");
                console.println("bus 3");
                cfgU.runCli();

                cfgU.displayEnableReg(mainBus, mainChip);

                cfgU.disableBus(3, mainBus, mainChip);
                //
                console.println("bus 4");
                returned = cfgU.enableGpioPath("pin15", "23017#2");
                cfgU.runCli();

                cfgU.displayEnableReg(mainBus, mainChip);

                cfgU.disableBus(4, mainBus, mainChip);
            }
            returned = cfgU.enableGpioPath(cfgU.pinName, cfgU.priChipName);
            console.println("Use in/out data pin : " + cfgU.pinName + "   chip   :" + cfgU.priChipName);
            console.println("bus dependent on input data");

            HashMap<String, String> chipD2 = cfgU.getChipMapRec(cfgU.priChipName);
            String banked2 = chipD2.get("banked");
            String chipBus2 = chipD2.get("busNum");
            String chipAddr2 = chipD2.get("address");

            boolean isBanked2 = false;
            if (banked2.equalsIgnoreCase("y")) {
                isBanked2 = true;
            }
            if (isBanked2) {
                console.println("Drive pin15 high");
                mcpUtil.drive_pin(15, true);
                gUtil.sleepMS(2000, cfgU.ffdc);
                mcpUtil.drive_pin(15, false);
            } else {
                console.println("Drive pin2 high");
                mcpUtil.drive_pin(2, true);
                gUtil.sleepMS(2000, cfgU.ffdc);
                mcpUtil.drive_pin(2, false);
            }
            gUtil.sleepMS(2000, cfgU.ffdc);

            console.println("Drive Gpio18 low");
            int p18 = 18;
            gUtil.drivePinHigh(p18);
            cfgU.runCli();
            gUtil.sleepMS(4000, cfgU.ffdc);

            console.println("Drive Gpio18 low");
            gUtil.drivePinLow(p18);

            cfgU.displayEnableReg(mainBus, mainChip);

            cfgU.disableBus(0, mainBus, mainChip);
            cfgU.disableBus(1, mainBus, mainChip);
            cfgU.disableBus(2, mainBus, mainChip);
            cfgU.disableBus(3, mainBus, mainChip);
            cfgU.disableBus(4, mainBus, mainChip);
            cfgU.disableBus(5, mainBus, mainChip);
            cfgU.disableBus(6, mainBus, mainChip);
            cfgU.disableBus(7, mainBus, mainChip);
            //

            if (isBanked == false) {
                boolean eChip = cfgU.enableChipPath("matrix#1");
                console.println("matrix#1  bus 7");
                cfgU.displayEnableReg(mainBus, mainChip);
                cfgU.runCli();
                cfgU.disableBus(7, mainBus, mainChip);
                cfgU.displayEnableReg(mainBus, mainChip);
                eChip = cfgU.enableChipPath("BMP#1");
                console.println("BMP#1  bus 7");
                cfgU.displayEnableReg(mainBus, mainChip);
                cfgU.runCli();
                cfgU.disableBus(7, mainBus, mainChip);
            }
            // use input details

            cfgU.runCli();

            System.exit(0);

        }

        HashMap<String, HashMap<String, HashMap<String, String>>> pinMap;
        HashMap<String, HashMap<String, String>> chipMap;
        HashMap<String, HashMap<String, String>> piPinMap;
        FfdcUtil ffdc;
        String priChipName;
        String pinName;
        boolean has_full_keyed_data;
        String full_keyed_data;
        boolean has_full_pin_keyed_data;
        String full_pin_keyed_data;
        public BaseGpioInOut gpio;
        public HashMap<Integer, GpioPinCfgData> dioPinData;
        public Tca9548ConfigData ConfigData;

        public MapUtil mapUtils;

        //      bUS0x?          address0x??   'someInstance
        HashMap<String, HashMap<String, Object>> deviceMap;


    }


