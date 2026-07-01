

package com.pi4j.devices.lcd1602a;



import com.pi4j.context.Context;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public abstract class LCD1602A {


    protected Console console = null;
    protected final Context pi4j;

    protected String traceLevel = "";
    protected Logger logger;
    protected boolean clearDisplay = false;


    protected LCD1602A(Context pi4j, Console console, boolean clearIt, String traceLevel) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.clearDisplay = clearIt;
        this.traceLevel = traceLevel;

    }

    protected LCD1602A(Context pi4j, Console console, boolean clearIt) {
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.clearDisplay = clearIt;
        this.traceLevel = "info";
        this.logger = LoggerFactory.getLogger(LCD1602A.class);


    }


    protected void init() {
        this.logger.trace(">>> Enter: init");

        this.logger.trace("<<< Exit: init  device  ");
    }

    public void shiftLeft(int places) {
        this.logger.trace(">>> Enter: shiftLeft  : " + places);
        for (int i = 0; i < places; i++) {
            this.sendCommand(LCD1602A_Declares.cursorCMD | LCD1602A_Declares.displayLeftBit);
        }
        this.logger.trace("<<< Exit: shiftLeft  ");

    }

    public void clearDisplay() {
        this.logger.trace(">>> Enter: clearDisplay   ");

        this.sendCommand(LCD1602A_Declares.clearDispCMD);
        this.sendCommand(LCD1602A_Declares.returnHomeCMD);
        this.logger.trace("<<< Exit: clearDisplay   ");
    }

    /**
     * @param str    What to display
     * @param line   line of display, 1,2...
     * @param offset offset within the line
     */
    public void sendStringLineX(String str, int line, int offset) {
        this.logger.trace(">>> Enter: sendStringLineOne   : " + str + "    line : " + line + "  Offset  : " + offset);
        char[] chars = str.toCharArray();

        this.sendCommand(LCD1602A_Declares.setDDRAMCMD | (0x40 * (line - 1)) | offset);
        for (int i = 0; i < chars.length; i++) {
            this.sendChar(chars[i]);
        }
        this.logger.trace("<<<  Exit: sendStringLineOne  ");
    }


    protected boolean lcdAvailable() {
        int c = 0;
        this.logger.trace(">>> Enter: lcdAvailable  ");
        this.sleepTimeMilliS(10);
        boolean rval = this.isBfLow();
        while (!rval) {
            this.logger.info("\n\n\n !!!!!   BF was busy  \n\n");
            this.sleepTimeMilliS(400);
            c++;
            if (c > 10) {
                this.logger.info(" LCD remained busy state ");
                console.println(" LCD remained busy state ");
                System.exit(100);
            }
            rval = this.isBfLow();
        }
        this.logger.trace("<<< Exit: lcdAvailable  : " + rval);
        return (rval);
    }

    protected void sleepTimeNanoS(int nanoSec) {
        TimeUnit tu = TimeUnit.NANOSECONDS;
        try {
            tu.sleep(nanoSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    protected void sleepTimeMilliS(int milliSec) {
        TimeUnit tu = TimeUnit.MILLISECONDS;
        try {
            tu.sleep(milliSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sleepTimeMicroS(int microSec) {
        TimeUnit tu = TimeUnit.MICROSECONDS;
        try {
            tu.sleep(microSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    // Specific to SubClass
    protected void sendChar(char c) {
        this.logger.trace(">>> Enter: sendChar   : " + c);
        this.logger.trace("<<<  Exit: sendChar  ");
    }

    protected void sendChar(byte c) {
        this.logger.trace(">>> Enter: sendChar   : " + c);
        this.logger.trace("<<<  Exit: sendChar  ");
    }

    // do required gpio->LCD_input dance before and after actual LCD pin update
    protected void sendCommand(int cmd) {
        this.logger.trace(">>> Enter: sendCommand   ");
        this.logger.trace("<<< Exit: sendCommand   ");
    }


    /**
     * Value of 0 indicates the device is not performing internal
     * operations and will accept commands
     * Not possible with the Pi GPIOs. Later if the D0_D7 interface is on an
     * MCP230xx, this maybe possible.  For present time, use timing values
     * documented in datasheet
     *
     * @return bit value of DB7
     */
    protected boolean isBfLow() {
        this.logger.trace(">>> Enter: isBfLow   ");
        this.logger.trace("<<< Exit: isBfLow   ");
        return (true);
    }


    protected void pulseEnable() {
        this.logger.trace(">>> Enter: pulseEnable   ");
        this.logger.trace("<<< Exit: pulseEnable   ");
    }


    protected void pulseEnable(byte b) {
        this.logger.trace(">>> Enter: pulseEnable   ");
        this.logger.trace("<<< Exit: pulseEnable   ");
    }

}
