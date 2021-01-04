/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  McpConfigData.java
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

package com.pi4j.devices.mcp23xxxCommon;


import com.pi4j.devices.base_util.ffdc.FfdcUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Contains enum/constants used by the MCP23008 and MCP23017 class
 */

public class McpConfigData {


    /**
     * CTOR
      * @param ffdc  logginf
     */
    public McpConfigData(FfdcUtil ffdc) {

        this.ffdc = ffdc;
        this.init();
    }

    /**
     * Place holder
     * creates HashMap for unit testing
     */
    public void init() {

        this.outerMap = new HashMap<String, HashMap<String, String>>();

    }



    // # Offset into data from getAddrMapFirst8(); and getAddrMapSecond8() .
    // This is the register address based on
    // PCA and whether banked-23017

    int _IODIR = 0x00;
    int _IPOL = 0x01;
    int _GPINTEN = 0x02;
    int _DEFVAL = 0x03;
    int _INTCON = 0x04;
    int _IOCON = 0x05;
    int _GPPU = 0x06;
    int _INTF = 0x07;
    int _INTCAP = 0x08;
    int _GPIO = 0x09;
    int _OLAT = 0x0A;

    int _IODIRB = 0x00;
    int _IPOLB = 0x01;
    int _GPINTENB = 0x02;
    int _DEFVALB = 0x03;
    int _INTCONB = 0x04;
    int _IOCONB = 0x05;
    int _GPPUB = 0x06;
    int _INTFB = 0x07;
    int _INTCAPB = 0x08;
    int _GPIOB = 0x09;
    int _OLATB = 0x0A;


    /**
     * Unit testing
      * @return    HashMap
     */
    public HashMap<String, HashMap<String, String>> getFullMap() {
        this.ffdc.ffdcMethodEntry("getFullMap");
        this.ffdc.ffdcMethodExit("map : " + this.outerMap);

        return (this.outerMap);
    }

    /**
     * Unit testing
     * @param newMap
     */
    public void replaceMap(HashMap<String, HashMap<String, String>> newMap) {
        this.ffdc.ffdcMethodEntry("replaceFullMap");
        this.ffdc.ffdcMethodExit("map : " + newMap);
        this.outerMap = newMap;
    }


    /**
     * Unit testing.  Dump and pretty print HashMap
      */
    public void DumpGpiosConfig() {
        this.ffdc.ffdcMethodEntry("mcpConfigure::DumpGpiosConfig (program details)");
        HashMap<String, HashMap<String, String>> outerMap = this.getFullMap();
        Set outerSet = outerMap.entrySet();
        Iterator<Map.Entry<String, Map<String, String>>> outerIterator = outerSet.iterator();
        while (outerIterator.hasNext()) {
            Map.Entry<String, Map<String, String>> mentry = (Map.Entry) outerIterator.next();
            this.ffdc.ffdcDebugEntry("key is: " + mentry.getKey() );
            // System.out.println(mentry.getValue());
            // iterate inner map
            HashMap<String, String> innerMap = new HashMap<String, String>();
            Iterator<Map.Entry<String, String>> child = (mentry.getValue()).entrySet().iterator();
            while (child.hasNext()) {
                Map.Entry childPair = child.next();
                this.ffdc.ffdcDebugEntry("childPair:   " + childPair.getKey() + " ->  "  + childPair.getValue());
            }
        }
        this.ffdc.ffdcMethodExit("DumpGpiosConfig");
    }

    HashMap<String, HashMap<String, String>> outerMap;
    FfdcUtil ffdc;



}
