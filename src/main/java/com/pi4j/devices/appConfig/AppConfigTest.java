package com.pi4j.devices.appConfig;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.devices.base_util.mapUtil.MapUtil;
import com.pi4j.devices.mcp23008.Mcp23008;
import com.pi4j.devices.mcp23017.Mcp23017;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxAppProcessParms;
import com.pi4j.devices.mcp23xxxApplication.Mcp23xxxParms;
import com.pi4j.devices.mcp23xxxCommon.Mcp23xxxUtil;
import com.pi4j.devices.mcp23xxxCommon.McpBase;
import com.pi4j.devices.mcp23xxxCommon.McpConfigData;
import com.pi4j.exception.Pi4JException;
import com.pi4j.util.Console;

import java.util.HashMap;

/**
 * Testcase
 */
public class AppConfigTest extends AppConfigUtilities {

    /**
     * CTOR.
     *
     * <p>
     * PreCond: AppConfigTest CTOR called with valid parameters
     * <ul>
     *     <li>Instantiated Context class
     *     <li> Instantiated FFDC class
     *     <li> BaseGpioInOut
     *     <li> Instantiated Console class
     * </ul>
     * <p>
     * PostCond:  Class methods are now accessable
     */
    public AppConfigTest(Context pi4j, FfdcUtil ffdc, BaseGpioInOut gUtil, Console console) {
        super(pi4j, ffdc, gUtil, console);
    }


    /**
     * usage    Display help text for using this program
     * <p>
     * PreCond: AppConfigUtilities instance initialized.  See CTOR
     */
    public void usage() {
        System.out.println("options   -h 'help', -b bus, -a address, -z mainChip " + "-c chipName  -p  pinName   "
            + "  -x reset-chip  -n reset GPIO -f ffdcLvl -g gpiodict   -m mcp23xx_pin_onfig   -s log");
    }


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
        AppConfigTest cfgU = new AppConfigTest(pi4j, ffdc, gUtil, console);
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

                // >>>tcaObj.busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-c")) { // primarydevice name
                String a = args[i + 1]; // chip name
                i++;
                cfgU.priChipName = a;
                console.println("-c : " + cfgU.priChipName);
                // displayMain.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-p")) { // primarydevice address
                String a = args[i + 1]; // chip name/
                i++;
                cfgU.pinName = a;
                console.println("-p : " + cfgU.pinName);

                // displayMain.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-g")) { // pi GPIO pins
                cfgU.hasFullKeyedData = true;
                cfgU.fullKeyedData = args[i + 1];
                i++;
            } else if (o.contentEquals("-m")) { // mcp23xx pins
                cfgU.hasFullPinKeyedData = true;
                cfgU.fullPinKeyedData = args[i + 1];
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

        boolean isBanked = banked.equalsIgnoreCase("y");
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
        if (cfgU.hasFullKeyedData) {
            cfgU.ffdc.ffdcDebugEntry("Start of hasFullKeyedData");
            HashMap<String, HashMap<String, String>> outerMap = cfgU.mapUtils.createFullMap(cfgU.fullKeyedData);
            mcpcfgD.replaceMap(outerMap);
            gUtil.createGpioInstance(mcpcfgD.getFullMap());
        }
        if (cfgU.hasFullPinKeyedData) {
            cfgU.ffdc.ffdcDebugEntry("Start of hasFullPinKeyedData");
            HashMap<String, HashMap<String, String>> pinMap = cfgU.mapUtils.createFullMap(cfgU.fullPinKeyedData);
            mcpcfgD.replaceMap(pinMap);
        }
        /*
         * try { // TODO how to get first chip address details ????
         * tcaObj.i2cDevice = new I2cBase(tcaObj.busNum, tcaObj.address,
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

        if (cfgU.hasFullPinKeyedData) {
            cfgU.ffdc.ffdcDebugEntry("Process hasFullPinKeyedData");
            mcpUtil.processKeyedData(); // configure the target mcp23xx
        }                 // chip

        // test the configured pin
        mcpUtil.drivePin(Integer.parseInt(cfgU.pinName.substring(3)), true);
        gUtil.sleepMS(2000, cfgU.ffdc);
        mcpUtil.drivePin(Integer.parseInt(cfgU.pinName.substring(3)), false);
        gUtil.sleepMS(2000, cfgU.ffdc);
        mcpUtil.drivePin(Integer.parseInt(cfgU.pinName.substring(3)), true);
        gUtil.sleepMS(2000, cfgU.ffdc);
        mcpUtil.drivePin(Integer.parseInt(cfgU.pinName.substring(3)), false);
        gUtil.sleepMS(2000, cfgU.ffdc);
        mcpUtil.drivePin(Integer.parseInt(cfgU.pinName.substring(3)), true);
        gUtil.sleepMS(2000, cfgU.ffdc);
        mcpUtil.drivePin(Integer.parseInt(cfgU.pinName.substring(3)), false);


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

        boolean isBanked2 = banked2.equalsIgnoreCase("y");
        if (isBanked2) {
            console.println("Drive pin15 high");
            mcpUtil.drivePin(15, true);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drivePin(15, false);
        } else {
            console.println("Drive pin2 high");
            mcpUtil.drivePin(2, true);
            gUtil.sleepMS(2000, cfgU.ffdc);
            mcpUtil.drivePin(2, false);
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

        if (!isBanked) {
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

}
