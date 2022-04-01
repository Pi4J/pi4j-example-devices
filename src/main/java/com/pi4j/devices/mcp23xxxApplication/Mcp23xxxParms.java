/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  Mcp23xxxParms.java
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

package com.pi4j.devices.mcp23xxxApplication;


import com.pi4j.context.Context;
import com.pi4j.util.Console;

/**
 * Mcp23xxxParms
 *
 * State only class, used to pass may variable to the various methods.
 */

public class Mcp23xxxParms {

    /**
     * CTOR
     * @param console Console object
     */
    public Mcp23xxxParms(Console console){
        super();
        this.console = console;

    }

    private final Console console;
    public String mainChip;
    public int ffdcControlLevel;
    public boolean dumpRegs;
    public boolean readPin;
    public boolean pinOn;
    public boolean setPin;
    public String fullKeyedData;
    public String fullPinKeyedData;
    public boolean hasFullKeyedData;
    public boolean hasFullPinKeyedData;

    public boolean hasIOCONKeyedData;
    public String IOCONKeyedData;

    public String priChipName;
    public String pinName;
    public int priChipBusNum;
    public int priChipAddress;

    public boolean doReset;
    public int busNum;
    public int address;

    public byte configInfo;
    public byte intfA;
    public byte intfB;
    public int gpioNum;
    public String offOn;
    public String upDown;
    public int intrptCount;
    public boolean tmpFileUse;
    public int gpioReset;
    public boolean banked;
    public boolean bankCapable;

    public Context pi4j;
    public boolean monitorIntrp;
    public int pin;


    // , -b bus, -a address
    public void usage() {
        System.out.println("options   -h 'help', -d drive-pin with -o ON/OFF, \n"
                + "-r read-pin , -k \"{'dir':'out','int_ena':'no'}\" (using -d or -r) \n"
                + "-m \"{'pin1':{'dir':'in','pull':'down','default':'0','do_compare':'yes','int_ena':'yes','act':'high'}}\"\n"
                + "-c primary chip     -p primary pin \n"
                + "-z  gpios config dict    -i interript monitor on/off -q mainChip \n"
                + "-g gpio,   -x reset-chip GPIO# -n resetPin -f ffdc_lvl  -y dumpRegs  \n"
                + "     ffdc_lvl 0 < TRACE 1 DEBUG < 2 INFO < 3 WARN < 4 ERROR < 5 FATAL < 6 OFF  \n");
    }

}
