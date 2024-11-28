/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  ReadProperties.java
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

package com.pi4j.devices.appConfig;

/**
 * <h1>ReadProperties</h1>
 * <p>
 * Single class that calls read_properties on the various
 * classes within the appConfig package
 * </p>
 */
public class ReadProperties {

    /**
     * CTOR
     */
    public ReadProperties() {
    }

    /**
     * Work   Print the values in all three properties.
     */
    public void work() {
        ChipNameMap cMap = new ChipNameMap();
        String chips = cMap.readProperties();

        GpioToApp gMap = new GpioToApp();
        String pins = gMap.readProperties();

        PiPinMap piMap = new PiPinMap();
        String pi = piMap.readProperties();

        System.out.println(" chip map : " + chips);

        //////////////////////////
        System.out.println(" pin map : " + pins);

        //////////////////////////
        System.out.println(" pi map : " + pi);


    }

    /**
     * main
     * <p>
     * Classes within  the appConfig package have their readProperties method called.
     * <p>
     * PreCond: None
     * </p>
     *
     * <p>
     * PostCond:  Each file read and printed to the screen.
     * </p>
     */
    public static void main(String[] args) {

        ReadProperties read = new ReadProperties();
        read.work();
    }

}
