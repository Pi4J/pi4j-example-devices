

package com.pi4j.devices.mcp4728;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.io.da.mcp472x.Mcp4728Driver;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CImplementation;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *   Support MCP4728 DAC.
 *<p>
 *   Support the single channel at a time functions of the chip. Example: you cannot set
 *   all four channels vref bit in a single call. Rather this would require
 *   four separate calls to the driver code.
 *</p>
 *   <p>
 *  The MCP4728 ships with device address 0x60. To set other address the DAC config registers
 *   must be updated. The update process requires the LDAC pin be toggled at a specific point
 *   in the I2C command as it is clocked into the chip. This GPIO control within the I2C
 *   traffic is not possible with Pi4j.  To accomplish this would require ordering the chip with
 *   the address programmed, or use of an MCU.
 *   </p>
 *   @see <a href="https://ww1.microchip.com/downloads/aemDocuments/documents/OTH/ProductDocuments/DataSheets/22187E.pdf">MCP4728</a>
 *
 *
 * The MCP4728 chip contains an EEPROM.  When Powered-on or RESET, the EEPROM contents are used to update
 * the DAC volatile registers, restoring the configured output voltages.  The program arguments that result
 * in the EEPROM being updated that code examples include the  statement  dacChip.setEepromEnabled(true);.
 * After this method all subsequent methods will ensure the EEPROM  is updated.  To stop this behavior the
 *  examples call dacChip.setEepromEnabled(false);
 *
 *  The example uses the letters A B C D to specify the channel. This conforms to the chips Datasheet documentation.
 *  Within the driver method signatures and code the channel is specified with an int.
 *
 */
public class MCP4728App {

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
        Logger logger = LoggerFactory.getLogger(MCP4728App.class);
        logger.trace(">>> Enter: init");

        Context pi4j = Pi4J.newAutoContext();

        final Console console = new Console();
        console.print("==============================================================");
        console.print("startup  MCP4728App ");
        console.print("==============================================================");



        int busNum = 1;
        int address = MCP4728_Declares._MCP4728_DEFAULT_ADDRESS;
        int channel = 0;
        boolean doReset = false;
        int registerData = 0;
         double vref = 0;
        float volts = 0;
        int onlyOne = 0;   // numerous parms a mutually exclusive
        int gainBit = 0;
        int vrefBit = 1;
        boolean setVrefBit = false;
        boolean setGainBit = false;
        boolean setOutputDigital = false;
        boolean setOutputVotage = false;
        boolean persistValues = false;



        String helpString = " parms: -b 0x? hex value bus    -a 0x?? hex value address   \n " +
            "  -r  reset chip    -ch channel A B C D    -d  digital value (0 ... 4095) \n" +
            "  -v  voltage  (0 ... vdd)   -pu  persist values true/false  -vdd decimal reference voltage \n" +
            " -sv vrefBit 0 or 1    -sg gainBit 0 or 1 \n" +
            "-d -v -sv -sg mutually exclusive  \n" ;
        ;
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
            }  else if (o.contentEquals("-sv")) {
                String a = args[i + 1];
                vrefBit  = Integer.parseInt(a);
                setVrefBit = true;
                if ( (gainBit  <0) || (gainBit > 1) ){
                    console.println("vrefBit must be in range 0..1");
                    System.exit(55);
                }
                i++;
                onlyOne ++;
            }  else if (o.contentEquals("-sg")) {
                String a = args[i + 1];
                gainBit  = Integer.parseInt(a);
                setGainBit = true;
                if ( (gainBit  <0) || (gainBit > 1) ){
                    console.println("gainBit must be in range 0..1");
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
            }else if (o.contentEquals("-pv")) {
                String a = args[i + 1];
                persistValues = Boolean.parseBoolean(a);
                i++;
             } else if (o.contentEquals("-d")) {
                String a = args[i + 1];
                i++;
                registerData = Integer.parseInt(a);
                setOutputDigital = true;
                if (registerData < 0 || registerData > 4095) {
                    console.println("-rde must be in range 0..4095");
                    System.exit(36);
                }
                onlyOne ++;
            }  else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(39);
            } else if (o.contentEquals("-v")) {  // eeprom volts
                String a = args[i + 1];
                i++;
                setOutputVotage = true;
                volts = Float.parseFloat(a);
                onlyOne ++;
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(42);
            }
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
        if ( (volts > vref)  || (volts < 0) ) {
            console.println("-v greater than -vdd, or less than zero");
            System.exit(51);

        }

        if ( (volts > vref) || (volts < 0) ) {
            console.println("-ef greater than -vdd, or less than zero");
            System.exit(52);

        }

        Mcp4728Driver dacChip ;
        I2C genCallDevice = null ;


        I2C i2cDev = createI2cDevice("MCP4728",  busNum, address, I2CImplementation.DIRECT, pi4j) ;
        dacChip = new Mcp4728Driver( i2cDev,  vref);


        if (doReset) {
   			genCallDevice = createI2cDevice("GenCallReset", busNum,0x00, I2CImplementation.SMBUS, pi4j);
            dacChip.resetChip(genCallDevice);
        }

        logger.info("\nBefore state : \n" + dacChip);

        dacChip.setEepromEnabled(persistValues);

        if (setGainBit) {
            dacChip.setGain(channel,gainBit);
        }

        if (setVrefBit) {
            dacChip.setVref(channel,vrefBit);
        }

        if (setOutputDigital) {
            dacChip.setDigitalValue(channel, registerData);
        }

        if (setOutputVotage) {
            dacChip.setVoltage(channel, volts);
        }



        logger.info("\nCompletion state : \n" + dacChip);


        if (genCallDevice != null) {
            genCallDevice.close();
        }
        if (i2cDev != null) {
            i2cDev.close();
        }

                                                          
    }



    /**
     *
     * @param chipType  String Part numer
     * @param bus       INT
     * @param address   INT
     * @param pi4j      Context
     * @return I2C device
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

