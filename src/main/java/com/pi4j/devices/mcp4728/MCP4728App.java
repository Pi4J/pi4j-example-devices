

package com.pi4j.devices.mcp4728;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.da.mcp472x.Mcp4728Driver;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalInputConfigBuilder;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.util.Console;

/**
 *   Support MCP4728 DAC.
 *<p>
 *   Support the single channel functions of the chip. Example: you cannot set
 *   all four channels vref bit in a single call. Rather this would require
 *   four separate calls to the driver code.
 *</p>
 *   <p>
 *  *The MCP4728 ships with device address 0x60. To set other address the DAC config registers
 *  * must be updated. The update process requires the LDAC pin be toggled at a specific point
 *  * in the I2C command as it is clocked into the chip. This GPIO control within the I2C
 *  * traffic is not possible with Pi4j.  To accomplish this would require ordering the chip with
 *  * the address programmed, or use of an MCU.
 *  * </p>
 *  * @see <a href="https://ww1.microchip.com/downloads/aemDocuments/documents/OTH/ProductDocuments/DataSheets/22187E.pdf">MCP4728</a>
 *  *
 *
 */
public class MCP4728App {

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "NONE");

        Context pi4j = Pi4J.newAutoContext();

        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  MCP4728App ");
        console.print("==============================================================");



        int dataPinNum = -1;
        int busNum = 1;
        int address = MCP4728_Declares._MCP4728_DEFAULT_ADDRESS;
        int channel = 0;
        boolean doReset = false;
        int registerData = 0;
        boolean setOutputEEPROM = false;
        boolean setOutputFast = false;
        double vref = 0;
        float eepromVolt = 0;
        float fastVolt = 0;
        int onlyOne = 0;   // numerous parms a mutually exclusive
        int gainBit = 0;
        int vrefBit = 1;
        boolean setVrefBit = false;
        boolean setGainBit = false;
        boolean updateEEPROM = false;

        String helpString = " parms: -b 0x? hex value bus    -a 0x?? hex value address  -d  RDY GPIO number  \n " +
            "  -r  reset chip  -ue update EEPROM -rde  update DAC and EEPROM \n" +
            " -ev eeprom voltage  -fv fast voltage \n" +
            " -rdf DAC value update fast   -vdd decimal reference voltage\n " +
            " -ch channel A B C D  -sv vref 0 or 1  -sg gain 0 or 1" +
            "-rde -ev -fv -rdf -sv -sg mutually exclusive " ;

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
            } else if (o.contentEquals("-d")) {
                String a = args[i + 1];
                dataPinNum = Integer.parseInt(a);
                i++;
            }  else if (o.contentEquals("-sv")) {
                String a = args[i + 1];
                vrefBit  = Integer.parseInt(a);
                setVrefBit = true;
                if ( (vrefBit  <0) || (vrefBit > 1) ){
                    console.println("-vref must be in range 0..1");
                    System.exit(54);
                }
                i++;
                onlyOne ++;
            }  else if (o.contentEquals("-sg")) {
                String a = args[i + 1];
                gainBit  = Integer.parseInt(a);
                setGainBit = true;
                if ( (gainBit  <0) || (gainBit > 1) ){
                    console.println("-vref must be in range 0..1");
                    System.exit(55);
                }
                i++;
                onlyOne ++;
            } else if (o.contentEquals("-ch")) {
                String a = args[i + 1];
                i++;
                if (a.equalsIgnoreCase("A")) {
                    channel = 0;
                } else if (a.equalsIgnoreCase("B")) {
                    channel = 1;
                }  else if (a.equalsIgnoreCase("C")) {
                    channel = 2;
                }  else if (a.equalsIgnoreCase("D")) {
                    channel = 3;
                } else {
                    console.println("  -ch invalid ");
                    System.exit(53);
                }
            } else if (o.contentEquals("-vdd")) { // reference voltage
                String a = args[i + 1];
                i++;
                vref = Float.parseFloat(a);
            }  else if (o.contentEquals("-r")) {
                doReset = true;
            } else if (o.contentEquals("-ue")) {
                 updateEEPROM = true;
            } else if (o.contentEquals("-rde")) {
                String a = args[i + 1];
                i++;
                registerData = Integer.parseInt(a);
                setOutputEEPROM = true;
                if (registerData < 0 || registerData > 4095) {
                    console.println("-rde must be in range 0..4095");
                    System.exit(36);
                }
                onlyOne ++;
            } else if (o.contentEquals("-rdf")) {
                String a = args[i + 1];
                i++;
                setOutputFast = true;
                registerData = Integer.parseInt(a);
                if (registerData < 0 || registerData > 4095) {
                    console.println("-rdf must be in range 0..4095");
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

       if ( dataPinNum == -1 ){
           console.println(" Parameter -d, gpio BCM must be set.");
           console.println(helpString);
           System.exit(54);
       }
        if (onlyOne > 1 ) {
            console.println(" mutually exclusive parms used.");
            console.println(helpString);
            System.exit(43);
        }

        if ( (vref == 0) || (vref < 0) ){
            console.println("-vdd is zero or less than zero");
            System.exit(50);

        }
        if ( (eepromVolt > vref)  || (eepromVolt < 0) ) {
            console.println("-ev greater than -vdd, or less than zero");
            System.exit(51);

        }

        if ( (fastVolt > vref) || (fastVolt < 0) ) {
            console.println("-ef greater than -vdd, or less than zero");
            System.exit(52);

        }

        Mcp4728Driver dacChip ;
        I2C genCallDevice = null ;
        DigitalInput readyPin = createInputGPIO(dataPinNum, pi4j);
        

        I2C i2cDev = createI2cDevice("MCP4728",  busNum, address, I2CImplementation.DIRECT, pi4j) ;
        dacChip = new Mcp4728Driver( i2cDev, readyPin, vref);


        if (doReset) {
   			genCallDevice = createI2cDevice("GenCallReset", busNum,0x00, I2CImplementation.SMBUS, pi4j);
            dacChip.resetChip(genCallDevice);
        }

        console.println(dacChip.materializeDacDescription());

        if (setGainBit) {
            dacChip.setGain(channel,gainBit);
        }

        if (setVrefBit) {
            dacChip.setVref(channel,vrefBit);
        }

        if (setOutputEEPROM) {
            try {
                dacChip.setDigitalValueDACEEPROM(channel, registerData);
            } catch (Exception e) {
                console.println("Error occurred setting DAC output via register value failed. \n Exception "  + e.getMessage());
            }
        }
        if (setOutputFast) {
            dacChip.setDigitalValueDAC(channel, registerData);
        }

        if (eepromVolt > 0) {
            try {
                dacChip.setVoltage(channel, eepromVolt);
            } catch (Exception e) {
                console.println("Error occurred setting DAC output via target voltage failed. \n Exception "  + e.getMessage());
            }
        }


        if (fastVolt > 0) {
            dacChip.materializeDacRegs() ;
            dacChip.setVoltage(channel, fastVolt);
            dacChip.materializeDacRegs() ;
        }

        if (updateEEPROM) {
            dacChip.syncDACToEEPROM();
        }

        console.println(dacChip.materializeDacDescription());


        if (genCallDevice != null) {
            genCallDevice.close();
        }
        if (i2cDev != null) {
            i2cDev.close();
        }

                                                          
    }


    /**
     *
     * @param pinNumber GPIO number
     * @param pi4j      Context
     * @return
     */
    static DigitalInput createInputGPIO(int pinNumber,  Context pi4j){
        DigitalInputConfigBuilder inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
            .id("Data_In_" + pinNumber)
            .name("Data_In_" + pinNumber)
            .bcm(pinNumber)
            .pull(PullResistance.PULL_UP);
        return pi4j.create(inputConfig1);
    }

    /**
     *
     * @param chipType  String Part numer
     * @param bus       INT
     * @param address   INT
     * @param pi4j      Context
     * @return
     */
    static I2C createI2cDevice( String chipType, int bus, int address, I2CImplementation impType, Context pi4j) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(bus)
            .device(address)
            .id(chipType + id + " " + name)
            .name(name)
            .i2cImplementation(impType)
            .build();

        return pi4j.create(i2cDeviceConfig);


    }

}

