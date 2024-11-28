/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  ChipNameMap.java
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

import java.io.*;
import java.util.Properties;

/**
 * <h1>ChipNameMap</h1>
 * <p>
 * Defines each chip in the system.  Characteristics like Pi bus number,
 * Device address, Speed, if behind a mux/switch....
 * These details are used by the Application configuration code.
 * </p>
 * <p>
 * The structure is defined and must be followed, or code updates be completed.
 * </p>
 * <p>
 * The application must ensure the 'set_properties()' method has been called.
 * See com.pi4j.devices.appConfig.SetProperties as an example.
 * </p>
 */
public class ChipNameMap {
    /**
     * setProperties
     * <p>
     * PreCond: ChipNameMap instance initialized.
     *
     * <p>
     * PostCond:  File config_chipMap.properties contains the class data chip_map
     */

    public void setProperties() {
        Properties prop = new Properties();
        OutputStream output = null;
        String fName = "config_chipMap.properties";
        this.createNewFile(fName);

        try {

            output = new FileOutputStream(fName);

            prop.setProperty("chip_map",
                "{ {'23008#1':{'busNum':'0x0','address':'0x20','banked':'n','behindMux':'9548#1','pca':'23008','chipName':'23008#1'}},"
                    + "{'23017#1':{'busNum':0x1','address':'0x22','banked':'n','behindMux':'9548#1','pca':'23017','chipName':'23017#1'}},"
                    + "{'23017#2':{'busNum':0x6','address':'0x22','banked':'n','behindMux':'9548#1','pca':'23017','chipName':'23017#1'}},"
                    + "{'23017#3':{'busNum':'0X4','banked':'y','address':'0x20','behindMux':'9548#3','pca':'23017','chipName':'23017#3'}},"
                    + "{'9548#1':{'busType':'i2c','busNum':'0X1','address':'0x70','pca':'9548','chipName':'9548#1'}},"
                    + "{'9548#2':{'busType':'i2c','busNum':'0X1','address':'0x70','pca':'9548','chipName':'9548#2'}},"
                    + "{'9548#3':{'busType':'i2c','busNum':'0X1','address':'0x76','pca':'9548','chipName':'9548#3'}},"
                    + "{'3008#2':{'busType':'spi','channel':'0X0','speed':'1000000','mode':'0','pca':'3008','chipName':'3008#2'}},"
                    + "{'matrix#1':{'busNum':0x7','address':'0x74','behindMux':'9548#1','pca':'i31fl3731','chipName':'matrix#1'}},"
                    + "{'BMP#1':{'busNum':0x7','address':'0x77','behindMux':'9548#1','pca':'BMP180','chipName':'BMP#1'}},"
                    + "{'pi3B':{'busType':'i2c','busNum':'X','pca':'ARM','chipName':'pi3B'}},"
                    + " {'pi4B':{'busNum':'XXXX','pca':'ARM','chipName':'pi4B'}}}");
            // MODE_0(0), MODE_1(1), MODE_2(2), MODE_3(3);
            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void createNewFile(String fName) {

        try {
            PrintWriter writer = null;
            File f = new File(fName);
            if (f.exists() && !f.isDirectory()) {
                System.out.println("file exists ");
                writer = new PrintWriter(new FileWriter(fName));
            } else {
                System.out.println("file does not exist");
                writer = new PrintWriter(fName);
            }
            writer.close();

        } catch (Exception e) {
            // Print out the exception that occurred
            System.out.println("Unable to create new file   " + e.getMessage());
        }
    }

    /**
     * readProperties
     * <p>
     * PreCond: ChipNameMap instance initialized.   set_properties was called.
     *
     *
     * <p>
     * PostCond:  File config_chipMap.properties read.
     *
     * @return String containing value for key chip_map
     */
    public String readProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        String rval = "";
        ReadProperties readProp = new ReadProperties();
        try {
            input = new FileInputStream("config_chipMap.properties");
            // load a properties file
            prop.load(input);
            // System.out.println(prop.getProperty("chip_map"));

            rval = prop.getProperty("chip_map");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return (rval);

    }

}

