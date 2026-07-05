

package com.pi4j.devices.mcp23017_lcd1602a;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.util.Console;

public class MCP23017_LCD1602A_App {

    public static final int DEFAULT_ADDRESS = 0x77;

    public static final int DEFAULT_BUS = 0x1;

    public static void main(String[] args) throws InterruptedException, IOException {
        var console = new Console();
        Context pi4j = Pi4J.newAutoContext();
        boolean clearLCD = false;
        int rsPinNum = 0xff;
        int enPinNum = 0xff;
        int shiftLeftCount = 0;
        String lineOne = "";
        int lineOneOffset = 0;
        String lineTwo = "";
        int lineTwoOffset = 0;

        int busNum = DEFAULT_BUS;
        int address = DEFAULT_ADDRESS;

        // params for shift register, HD44780U_interface
        int OEPinNum = 0xff;
        int STCPPinNum = 0xff;
        int SHCPPinNum = 0xff;
        int MRPinNum = 0xff;
        int DSPinNum = 0xff;
        int resetPin = 0xff;
        boolean doReset = false;

        byte registerData = 0;


        console.title("<-- The Pi4J V2 Project Extension  -->", "HD44780U_App");
        String helpString = " parms: HD44780U   -b hex value bus    -a hex value address -t trace \n  " +
            " -x resetPin -r doReset -line1 LcdString,-line1Offset offset ," +
            " -line2 LcdString, -line2Offset offset, -shiftL left shift -clearLCD  \n" +
            "-t  trace values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" \n " +
            " or \"off\"  Default \"info\" \n";

        String traceLevel = "info";
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
            } else if (o.contentEquals("-x")) {
                String a = args[i + 1];
                resetPin = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-r")) {
                doReset = true;
            } else if (o.contentEquals("-shiftL")) {
                String a = args[i + 1];
                shiftLeftCount = Integer.parseInt(a);
                i++;
            } else if (o.contentEquals("-clearLCD")) {
                clearLCD = true;
            } else if (o.contentEquals("-t")) {
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(41);
                }
            } else if (o.contentEquals("-h")) {
                console.println(helpString);
                System.exit(41);
            } else {
                console.println("  !!! Invalid Parm " + o);
                console.println(helpString);
                System.exit(43);
            }
        }


        short pinCount = 8;
        console.println("----------------------------------------------------------");
        console.println("PI4J PROVIDERS");
        console.println("----------------------------------------------------------");
        pi4j.providers().describe().print(System.out);
        System.out.println("----------------------------------------------------------");

        MCP23017_LCD1602A dispObj = new MCP23017_LCD1602A(pi4j, console, resetPin, busNum, address, traceLevel);

        if (doReset) {
            dispObj.resetChip();
        }


        if (lineOne.length() > 0) {
            dispObj.sendStringLineX(lineOne, 1, lineOneOffset);
        }


        if (lineTwo.length() > 0) {
            dispObj.sendStringLineX(lineTwo, 2, lineTwoOffset);
        }

        Thread.sleep(5000);

        dispObj.shiftLeft(shiftLeftCount);
        Thread.sleep(5000);

        if (clearLCD) {
            dispObj.clearDisplay();
        }
        Thread.sleep(5000);
        //dispObj.sendStringLineX("HelloWorld" , 1, 5);

    }


}
