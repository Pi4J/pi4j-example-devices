

package com.pi4j.devices.mcp4725;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.da.mcp472x.Mcp4725Driver;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;


public class MCP4725App {

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        Context pi4j = Pi4J.newAutoContext();




        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  MCP4725App ");
        console.print("==============================================================");


        int busNum = 1;
        int address = MCP4725_Declares._MCP4725_DEFAULT_ADDRESS;
         boolean doReset = false;
        int registerData = 0;
        boolean setOutputEEPROM = false;
        boolean setOutputFast = false;
        double vref = 0;
        float eepromVolt = 0;
        float fastVolt = 0;
        int onlyOne = 0;   // numerous parms a mutually exclusive

        String helpString = " parms: -b 0x? hex value bus    -a 0x?? hex value address    \n " +
            "  -r  reset chip   -rde  update DAC and EEPROM \n" +
            " -ev eeprom voltage  -fv fast voltage \n" +
            " -rdf DAC value update fast   -vref decimal reference voltage\n " +
            "-rde -ev -fv -rdf mutually exclusive" ;

           for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-b")) { // bus
                String a = args[i + 1];
                busNum = Integer.parseInt(a.substring(2), 16);
                i++;
            } else if (o.contentEquals("-a")) { // device address
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
            } else if (o.contentEquals("-vref")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
            }  else if (o.contentEquals("-r")) {
                doReset = true;
            } else if (o.contentEquals("-rde")) {
                String a = args[i + 1];
                i++;
                registerData = Integer.parseInt(a);
                setOutputEEPROM = true;
if (registerData < 0 || registerData > 4095) {
    console.println("-rde must be in range 0..4095");
    System.exit(36);
}
                }
                onlyOne ++;
            } else if (o.contentEquals("-rdf")) {
                String a = args[i + 1];
                i++;
                setOutputFast = true;
                registerData = Integer.parseInt(a);
                if (registerData > 4095) {
                    console.println("-rdf cannot exceed 4095");
                    System.exit(37);
                }
                onlyOne ++;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else if (o.contentEquals("-ev")) {  // eeprom volts
                String a = args[i + 1];
                i++;
                eepromVolt = Float.parseFloat(a);
                onlyOne ++;
            } else if (o.contentEquals("-fv")) { // fast volts
                String a = args[i + 1];
                i++;
                fastVolt = Float.parseFloat(a);
                onlyOne ++;
            } else {
                console.println("  !!! Invalid Parm " + args);
                console.println(helpString);
                System.exit(42);
            }
        }

        if (onlyOne > 1 ) {
            console.println(" mutually exclusive parms used.");
            console.println(helpString);
            System.exit(43);
        }

        if (vref == 0) {
            console.println("-vref is zero");
            System.exit(50);

        }
        if (eepromVolt > vref) {
            console.println("-ev greater than -vref");
            System.exit(51);

        }

        if (fastVolt > vref) {
            console.println("-ef greater than -vref");
            System.exit(51);

        }

        Mcp4725Driver dacChip ;
        I2C genCallDevice = null ;
        I2C i2cDev = createI2cDevice("MCP4725",  busNum, address, pi4j) ;
        dacChip = new Mcp4725Driver( i2cDev, vref);

        if (doReset) {
			genCallDevice = createI2cDevice("GenCallReset", busNum,0x00, pi4j);
            dacChip.resetChip(genCallDevice);
        }

        if (setOutputEEPROM) {
            dacChip.setEepromEnabled(true);
            dacChip.setDigitalValue(registerData);
            dacChip.setEepromEnabled(false);
        }
        if (setOutputFast) {
            dacChip.setDigitalValue(registerData);
        }

        if (eepromVolt > 0) {
            dacChip.setEepromEnabled(true);
            dacChip.setVoltage(0, eepromVolt);
            dacChip.setEepromEnabled(false);
        }


        if (fastVolt > 0) {
            dacChip.setVoltage(fastVolt);
        }


        if (genCallDevice != null) {
            genCallDevice.close();
        }
        if (i2cDev != null) {
            i2cDev.close();
        }


    }

    static I2C createI2cDevice( String chipType, int bus, int address, Context pi4j) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(bus)
            .device(address)
            .id("" + chipType + id + " " + name)
            .name(name)
            .build();

        return pi4j.create(i2cDeviceConfig);


    }

}

