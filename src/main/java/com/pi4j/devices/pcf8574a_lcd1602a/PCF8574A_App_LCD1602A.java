

package com.pi4j.devices.pcf8574a_lcd1602a;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.drivers.display.character.hd44780.Hd44780Driver;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.i2c.I2C;
import com.pi4j.util.Console;

public class PCF8574A_App_LCD1602A {

    private static final int DEFAULT_ADDRESS = 0x77;
    private static final int DEFAULT_BUS = 0x1;

    public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        boolean clearLCD = false;
        int shiftLeftCount = 0;
        int shiftRightCount = 0;
        String lineOne = "";
        int lineOneOffset = 0;
        String lineTwo = "";
        int lineTwoOffset = 0;

        int busNum = DEFAULT_BUS;
        int address = DEFAULT_ADDRESS;




        console.title("<-- The Pi4J V2 Project Extension  -->", "HD44780U_App");
        String helpString = " parms: HD44780U   -b hex value bus    -a hex value address -t trace \n  " +
            "  -line1 LcdString,-line1Offset offset ," +
            " -line2 LcdString, -line2Offset offset, -shiftL left shift -shiftR right shift -clearLCD  \n" ;

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
            } else if (o.contentEquals("-line1")) {
                String a = args[i + 1];
                lineOne = a;
                i++;
            } else if (o.contentEquals("-line1Offset")) {
                String a = args[i + 1];
                lineOneOffset = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-line2")) {
                String a = args[i + 1];
                lineTwo = a;
                i++;
            } else if (o.contentEquals("-line2Offset")) {
                String a = args[i + 1];
                lineTwoOffset = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-shiftL")) {
                String a = args[i + 1];
                shiftLeftCount = Integer.parseInt(a);
                i++;
            }else if (o.contentEquals("-shiftR")) {
                String a = args[i + 1];
                shiftRightCount = Integer.parseInt(a);
                i++;
            }  else if (o.contentEquals("-clearLCD")) {
                clearLCD = true;
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(43);
            }
        }


        console.println("----------------------------------------------------------");
        console.println("PI4J PROVIDERS");
        console.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        console.println("----------------------------------------------------------");

        I2C pcfDev = createI2cDevice(pi4j, busNum, address);

        Hd44780Driver pcfDrv =  Hd44780Driver.withPcf8574Connection(pcfDev, 16, 2 );

        pcfDrv.setBacklightEnabled(true);
        pcfDrv.clear();
        pcfDrv.setBlinkingEnabled(true);
        pcfDrv.setCursorEnabled(true);
        pcfDrv.clear ();

        if (lineOne.length() > 0) {
            pcfDrv.setCursorPosition(lineOneOffset, 0);
            pcfDrv.write(lineOne);
        }

        Thread.sleep(5000);

        if (lineTwo.length() > 0) {
            pcfDrv.setCursorPosition(lineTwoOffset, 1) ;
            pcfDrv.write(lineTwo);
        }

        Thread.sleep(5000);

        for (int s = 0 ; s < shiftRightCount ; s++) {
            pcfDrv.scrollRight();
        }
        Thread.sleep(5000);
        for (int s = 0 ; s < shiftLeftCount ; s++) {
            pcfDrv.scrollLeft();
        }
        Thread.sleep(5000);

        if (clearLCD) {
            pcfDrv.clear();
        }
        Thread.sleep(5000);

      }


    private static I2C createI2cDevice(Context pi4j, int bus, int address) {
        String id = String.format("0X%02x: ", bus);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(bus)
            .device(address)
            .id(id + " " + name)
            .name(name)
            .build();
        return  pi4j.create(i2cDeviceConfig);
    }

}
