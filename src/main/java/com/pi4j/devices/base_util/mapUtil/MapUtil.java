/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  MapUtil.java
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

package com.pi4j.devices.base_util.mapUtil;

import com.pi4j.io.gpio.digital.DigitalState;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioBasics;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.io.gpio.digital.PullResistance;


/**
 * <h1>MapUtils</h1>  Utilities for creating the maps used in application
 * configuration.
 */

public class MapUtil {

    /**
     * CTOR.
     *
     * <p>
     * PreCond: AppMapUtils CTOR called with valid parameters
     * <ul>
     *     <li> Instantiated FFDC class
     *     <li> BaseGpioInOut
     * </ul>
     * <p>
     *     The methods accept different dictionaries, utimately returning
     *     a completed dictionary for general usage.
     * </p>
     * <p>
     * PostCond:  Class methods are now accessable
     */
    public MapUtil(FfdcUtil ffdc, BaseGpioInOut baseUtil) {
        super();
        this.ffdc = ffdc;
        this.baseUtil = baseUtil;
    }


    /**
     * createMap
     *  <p>
     * PreCond: AppMapUtils instance initialized.  See CTOR
     * @param pin number for the string data
     * @param dataStr   String to converted to a dictionary
     * @return  Dictionary of details for the 'pin'
     *
     */
    public HashMap<String, HashMap<String, String>> createMap(int pin, String dataStr) {
        this.ffdc.ffdcMethodEntry("mcpConfigure::createMap");
        // System.out.println(" dataStr : " + dataStr);
        HashMap<String, HashMap<String, String>> outerMap;
        HashMap<String, String> innerMap;
        outerMap = new HashMap<String, HashMap<String, String>>();
        innerMap = new HashMap<String, String>();
        String result = new String();
        // remove {}
        result = dataStr.replace("{", "");
        result = result.replace("}", "");
        // remove spaces
        result = result.trim();
        // remove '
        result = result.replace("'", "");
        // split on ,
        result = result.trim();

        String arrayDict[] = result.split(",", 0);
        for (String temp1 : arrayDict) {
            String key = temp1.substring(0, temp1.indexOf(":"));
            String val = temp1.substring(temp1.indexOf(":") + 1);
            innerMap.put(key, val);
            outerMap.put("pin" + pin, innerMap);
        }
        this.ffdc.ffdcMethodExit("createMap");
        return (outerMap);
    }

    /**
     * createXtraFullMap
     *  <p>
     * PreCond: AppMapUtils instance initialized.  See CTOR
     * @param fullDataStr   String to converted to a dictionary
     * @return  Dictionary of details for the 'pin'
     *
     */
    public HashMap<String, HashMap<String, HashMap<String, String>>> createXtraFullMap(String fullDataStr) {

        this.ffdc.ffdcMethodEntry("createXtraFullMap");
        // map< Key CARDNAME << value map pin < map k/v details>>>
        HashMap<String, HashMap<String, HashMap<String, String>>> rMap;
        rMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();
        HashMap<String, HashMap<String, String>> internalDetailMap;
        internalDetailMap = new HashMap<String, HashMap<String, String>>();
        String result = new String();
        result = fullDataStr.trim();
        // remove '
        result = result.replace("'", "");
        // remove spaces
        result = result.replace(" ", "");
        // "{{'pin0':{'dir':'out','int_ena':'no'}},
        // {'pin5':{'dir':'out','int_ena':'no'}}}
        int offset = 1;
        // int cardIdStart = result.indexOf("{{", offset);
        // int cardIdEnd = result.indexOf(":" , offset) -1 ;
        // String cardName = result.substring( cardIdStart, cardIdEnd);
        String rawText = new String();

        // int valueEnd = result.indexOf("}}}", cardIdStart); // end of this
        // cards
        int veryEnd = result.indexOf("}}}}}"); // end of this cards

        while (offset < veryEnd) { // cheapo
            int cardIdStart = result.indexOf("{", offset) + 1;
            int cardIdEnd = result.indexOf(":", offset);
            int valueEnd = result.indexOf("}}}}", cardIdStart) + 4; // end of
            // this
            // cards
            String cardName = result.substring(cardIdStart, cardIdEnd);

            rawText = result.substring(cardIdEnd + 1, valueEnd);
            // result = result.substring(valueEnd, veryEnd);
            offset = valueEnd;

            internalDetailMap = this.createFullMap(rawText);
            rMap.put(cardName, internalDetailMap);
        }
        this.ffdc.ffdcMethodExit("createXtraFullMap");
        return (rMap);

    }

    /**
     * createFullMap
     *  <p>
     * PreCond: AppMapUtils instance initialized.  See CTOR
     * @param fullDataStr   String to converted to a dictionary
     * @return  Dictionary of details for the 'pin'
     *
     */
    public HashMap<String, HashMap<String, String>> createFullMap(String fullDataStr) {
        // public void createFullMap(String FullDataStr) {
        this.ffdc.ffdcMethodEntry("mcpConfigure::createFullMap");
        HashMap<String, HashMap<String, String>> outerMap;
        HashMap<String, String> innerMap;
        HashMap<String, HashMap<String, String>> tempMap;
        outerMap = new HashMap<String, HashMap<String, String>>();
        innerMap = new HashMap<String, String>();
        String result = new String();
        result = fullDataStr.trim();
        // remove '
        result = result.replace("'", "");
        // remove spaces
        result = result.replace(" ", "");
        // "{{'pin0':{'dir':'out','int_ena':'no'}},
        // {'pin5':{'dir':'out','int_ena':'no'}}}
        int keyStart = result.indexOf("{{");
        int valueEnd = result.indexOf("}}}", keyStart); // this value is the
        // last valid } (value
        // end)
        if ((keyStart == -1) || (valueEnd == -1)) {
            System.out.println("Invalid Dictionary format");
            System.exit(401);
        }
        int valueStart = 0;
        int pinNum = 0xff;
        int endOfValue = 0;
        String majorKey = "";
        while (true) {
            String prepend = "pin";
            valueStart = result.substring(keyStart, keyStart + 8).indexOf("pin");
            if (valueStart == -1) {
                valueStart = result.substring(keyStart, keyStart + 8).indexOf("dio");
                prepend = "dio";
            }
            if (valueStart == -1) { // word pin or dio not found
                // get chars from keyStart to the :
                majorKey = result.substring(keyStart + 2, result.indexOf(":", keyStart));
                valueStart = keyStart + 2;
            } else {
                valueStart = keyStart + 2;
                pinNum = Character.getNumericValue(result.charAt(valueStart + 3));
                if (Character.isDigit(result.charAt(valueStart + 4))) {
                    pinNum = pinNum * 10;
                    pinNum += Character.getNumericValue(result.charAt(valueStart + 4));
                }
                majorKey = prepend + pinNum;
            }

            String value = new String();
            endOfValue = ((result.indexOf("}", valueStart)));
            // starting at the inner brace, get all K/V's and the closing brace
            value = result.substring(result.indexOf("{", valueStart), (endOfValue + 1));

            keyStart = result.indexOf("{", endOfValue) - 1; // prime offset to
            // next
            // entry

            // call method to update maps
            tempMap = this.createMap(majorKey, value);
            // System.out.println("tempMap : " + tempMap);
            outerMap.put(majorKey, tempMap.get(majorKey));
            // System.out.println("outerMap : " + outerMap);

            if (endOfValue == valueEnd) {
                break;
            }
        }
        this.ffdc.ffdcMethodExit("mcpConfigure::createFullMap");
        return (outerMap);

    }

    /**
     * createMap
     *  <p>
     *      Create HashMap using the two strings supplied as params
     *          pin5  {'dir':'out','int_ena':'no'}
     *
     *      </p>
     *      <p>
     * PreCond: AppMapUtils instance initialized.  See CTOR
     *      </p>
     * @param majorKey   String to converted to a dictionary key
     * @param  dataStr    String to convert to a HashMap value
     * @return  Dictionary of details for the  majorkey
     *
     */
   public HashMap<String, HashMap<String, String>> createMap(String majorKey, String dataStr) {
        this.ffdc.ffdcMethodEntry("mcpConfigure::createMap");
        // System.out.println(" dataStr : " + dataStr);
        HashMap<String, HashMap<String, String>> outerMap;
        HashMap<String, String> innerMap;
        outerMap = new HashMap<String, HashMap<String, String>>();
        innerMap = new HashMap<String, String>();
        String result = new String();
        // remove {}
        result = dataStr.replace("{", "");
        result = result.replace("}", "");
        // remove spaces
        result = result.trim();
        // remove '
        result = result.replace("'", "");
        // split on ,
        result = result.trim();

        String arrayDict[] = result.split(",", 0);
        for (String temp1 : arrayDict) {
            String key = temp1.substring(0, temp1.indexOf(":"));
            String val = temp1.substring(temp1.indexOf(":") + 1);
            innerMap.put(key, val);
            outerMap.put(majorKey, innerMap);
        }
        this.ffdc.ffdcMethodExit("mcpConfigure::createMap");
        return (outerMap);
    }


    BaseGpioInOut baseUtil;
    FfdcUtil ffdc;

}
