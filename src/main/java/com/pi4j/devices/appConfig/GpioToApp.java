/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  GpioToApp.java
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
 * <h1>GpioToApp</h1>
 * <p>
 * Defines each chip, defines pins available on that chip.
 * Included detail, mux/switch name if located behind a mux/chip,
 * action details when an interrupt pertains to this pin.
 * *   </p>
 * <p>
 * The structure is defined and must be followed, or code updates be completed.
 * </p>
 * <p>
 * The application must ensure the 'set_properties()' method has been called.
 * See com.pi4j.devices.appConfig.SetProperties as an example.
 * </p>
 */

public class GpioToApp {


    /**
     * setProperties
     * <p>
     * PreCond: GpioToApp instance initialized.
     *
     * <p>
     * PostCond:  File config_pin_map.properties contains the class data pin_map
     */
    public void setProperties() {
        Properties prop = new Properties();
        OutputStream output = null;
        String fName = "config_pin_map.properties";
        this.createNewFile(fName);

        try {
            output = new FileOutputStream(fName);
            prop.setProperty("pin_map",
                "{ {'23008#1':{{'pin7':{'appName':'input','action':'hilow','chipName':'23008#1','gpioNumLED':'dio12'}},"
                    + "{'pin14':{'appName':'input','action':'reflect','chipName':'23008#1','pin':'pin15','pinChip':'23008#1'}},"
                    + "{'pin15':{'appName':'LED','action':'reflect','chipName':'23008#1'}},"
                    + "{'pin2':{'appName':'LED','action':'reflect','chipName':'23008#1'}},"
                    + "{'pin0':{'appName':'LED','action':'reflect','chipName':'23008#1'}},"
                    + "{'pin3':{'appName':'input','action':'reflect','chipName':'23008#1','pin':'pin0','pinChip':'23008#1'}},"
                    + "{'pin4':{'appName':'input','action':'reflect','chipName':'23008#1','pin':'pin0','pinChip':'23008#1'}} } },"
                    + "{'23017#1':{{'pin7':{'appName':'input','action':'hilow','chipName':'23017#1','gpioNumLED':'dio12'}},"
                    + "{'pin15':{'appName':'input','action':'reflect','chipName':'23017#2','pin':'pin0','pinChip':'23017#1'}},"
                    + "{'pin4':{'appName':'input','action':'hilow','chipName':'23017#1','pin':'pin14','pinChip':'23017#1'}},"
                    + "{'pin14':{'appName':'LED','action':'reflect','chipName':'23017#1'}},"
                    + "{'pin0':{'appName':'LED','action':'reflect','chipName':'23017#1'}},"
                    + "{'pin15':{'appName':'LED','action':'reflect','chipName':'23017#1'}},"
                    + "{'pin15':{'appName':'input','action':'reflect','chipName':'23017#1','pin':'pin0','pinChip':'23017#1'}},"
                    + "{'pin2':{'appName':'LED','action':'reflect','chipName':'23017#1'}}  } }, "
                    + "{'23017#2':{{'pin7':{'appName':'input','action':'hilow','chipName':'23017#2','gpioNumLED':'dio12'}},"
                    + "{'pin14':{'appName':'input','action':'reflect','chipName':'23017#2','pin':'pin15','pinChip':'23017#2'}},"
                    + "{'pin9':{'appName':'output','action':'reflect','chipName':'23017#2'}},"
                    + "{'pin4':{'appName':'LED','action':'reflect','chipName':'23017#2'}},"
                    + "{'pin0':{'appName':'LED','action':'reflect','chipName':'23017#2'}},"
                    + "{'pin15':{'appName':'LED','action':'reflect','chipName':'23017#2'}},"
                    + "{'pin2':{'appName':'LED','action':'reflect','chipName':'23017#2'}}  } }, "
                    + "{'23017#3':{{'pin15':{'appName':'LED','action':'reflect','chipName':'23017#3'}} }} }");

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
     * PreCond: GpioToApp instance initialized.   set_properties was called.
     *
     *
     * <p>
     * PostCond:  File config_pin_map.properties read.
     *
     * @return String containing value for key pin_map
     */
    public String readProperties() {
        Properties prop = new Properties();
        InputStream input = null;
        String rval = "";
        ReadProperties readProp = new ReadProperties();
        try {
            input = new FileInputStream("config_pin_map.properties");
            // load a properties file
            prop.load(input);
            // System.out.println(prop.getProperty("pin_map"));
            rval = prop.getProperty("pin_map");

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


