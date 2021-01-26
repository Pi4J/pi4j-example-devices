/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  PiPinMap.java
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
//  Map Pi3B and Pi4B gpio mapping

/**
 * <h1>PiPinMap</h1>
 *   <p>
 *       Defines each GPIO pins on the Pi BCM. Numbering follows the BCM numbering.
 *       Included detail action details when an interrupt pertains to this pin.
 *   </p>
 *   <p>
 *       The structure is defined and must be followed, or code updates be completed.
 *   </p>
 *   <p>
 *       The application must ensure the 'set_properties()' method has been called.
 *       See com.pi4j.devices.appConfig.SetProperties as an example.
 *   </p>
 *
 *
 */

public class PiPinMap {

    /**
     * set_properties
     * <p>
     * PreCond: PiPinMap instance initialized.
     *
     * <p>
     * PostCond:  File config_pi_pins.properties contains the class data pi_pin_map
     *
     * */
    public void set_properties() {
        Properties prop = new Properties();
        OutputStream output = null;
        String fName = "config_pi_pins.properties";
        this.createNewFile(fName);

        try {

            output = new FileOutputStream(fName);
            prop.setProperty("pi_pin_map",
                    "{{'dio13':{'appName':'output','action':'hilow','chipName':'pi4B'}},"
                            + "{'dio12':{'appName':'LED','action':'reflect','chipName':'pi4B'}},"
                            + "{'dio18':{'appName':'LED','action':'reflect','chipName':'pi4B'}},"
                            + "{'dio26':{'appName':'hilow','action':'reflect','chipName':'pi4B'}}}");

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
     * read_properties
     * <p>
     * PreCond: PiPinMap instance initialized.   set_properties was called.
     *
     *
     * <p>
     * PostCond:  File config_pi_pins.properties read.
     *
     * @return String containing value for key pi_pin_map
     *
     * */

    public String read_properties() {
        Properties prop = new Properties();
        InputStream input = null;
        String rval = "";
        ReadProperties readProp = new ReadProperties();
        try {
            input = new FileInputStream("config_pi_pins.properties");
            // load a properties file
            prop.load(input);
            // System.out.println(prop.getProperty("pi_pin_map"));
            rval = prop.getProperty("pi_pin_map");

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


    // Test only
    public static void main(String[] args) {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config_Pi_Pins.properties");

            // load a properties file
            System.out.println("\n\nconfig_Pi_Pins.properties");
            System.out.println(prop.getProperty("database"));
            // get the property value and print it out
            System.out.println(prop.getProperty("database"));
            System.out.println(prop.getProperty("dbuser"));
            System.out.println(prop.getProperty("dbpassword"));

            System.out.println(prop.getProperty("pi_pin_map"));
            System.out.println(prop.getProperty("pin_map"));
            System.out.println(prop.getProperty("chip_map"));

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

    }

}
