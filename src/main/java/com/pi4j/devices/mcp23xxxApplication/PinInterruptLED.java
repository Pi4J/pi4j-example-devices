/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  PinInterruptLED.java
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
import com.pi4j.devices.appConfig.AppConfigUtilities;
import com.pi4j.devices.base_util.ffdc.FfdcUtil;
import com.pi4j.devices.base_util.gpio.BaseGpioInOut;
import com.pi4j.devices.base_util.gpio.GpioPinCfgData;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalState;

import java.util.HashMap;

/**
 * PinInterruptLED
 * <p>
 * Instances of this class are associated with an PinInterruptActionIntf as
 * the instance to call for effecting some action.
 */

public class PinInterruptLED extends PinInterruptBase implements Mcp23xxxPinMonitorIntf {


    /**
     * CTOR
     *
     * @param pi4j        Context
     * @param pin         Pin on the  MCP23008 or MCP23017
     * @param ffdc        logging
     * @param mcpObj      MCP23008 or MCP23017 instance
     * @param pinDict     pin configuration disctionary
     * @param cfgU        utilities
     * @param priChipName Name of the MCP23008 or MCP23017 chip owning the 'pin'
     */
    public PinInterruptLED(Context pi4j, int pin, FfdcUtil ffdc, Mcp23xxxPinMonitorIntf mcpObj,
                           HashMap<Integer, GpioPinCfgData> pinDict, AppConfigUtilities cfgU, String priChipName) {
        super(pi4j, pin, ffdc, mcpObj, pinDict, cfgU, priChipName);

    }

    /**
     * changeLed
     *
     * <p>
     * Use the AppConfigUtilities to determine what action to
     * take when a specific pin causes an interrupt.
     * The actions may in fact effect others pins, depended upon the
     * details in the GpioToApp class.
     *
     * @param pinNumber effect pin
     * @param pinState  DigitalState
     */
    public void changeLed(int pinNumber, DigitalState pinState) {
        ffdc.ffdcMethodEntry("changeLed pin : " + pinNumber + " pin state :" + pinState + " this instance for pin " + this.pin);
        HashMap<String, String> pinDetails = this.cfgU.getPinMapDetails(this.priChipName, "pin" + this.pin);
        ffdc.ffdcDebugEntry("instance pin  " + this.pin + " priChipName " + this.priChipName + " chip details " + pinDetails);
        if ((pinNumber == this.pin)) {
            // chipMap attribute contains details on what to do
            if (pinDetails != null) {
                String action = pinDetails.get("action");
                ffdc.ffdcDebugEntry("action " + action);
                if (action.equals("hilow")) {
                    ffdc.ffdcDebugEntry("Hilow " + pinNumber);
                    // do the LED work
                    BaseGpioInOut gpio = new BaseGpioInOut(this.pi4j, this.ffdc, this.pinDict);
                    if (pinDetails.containsKey("gpioNumLED")) {
                        // what pin to drive
                        ffdc.ffdcDebugEntry("PinMap details contains Pi gpioNumLED " + pinDetails.get("gpioNumLED"));
                        // any
                        // mux
                        // in
                        // path
                        String ledGpio = pinDetails.get("gpioNumLED");
                        int pinNum = Character.getNumericValue(ledGpio.charAt(3)); // dionx
                        if (ledGpio.length() == 5) {  // dioxx
                            pinNum = pinNum * 10;
                            pinNum += Character.getNumericValue(ledGpio.charAt(4));
                        }
                        int intPin = pinNum;
                        gpio.createOutPin("Util usage", intPin, DigitalState.HIGH);
                        gpio.drivePinHigh(intPin);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        gpio.drivePinLow(intPin);
                    } else if (pinDetails.containsKey("pin")) {
                        ffdc.ffdcDebugEntry("PinMap details contains pin " + pinDetails.get("pin"));
                        String ledGpio = pinDetails.get("pin");
                        int pinNum = Character.getNumericValue(ledGpio.charAt(3)); // pinx
                        if (ledGpio.length() == 5) {  // pinxx
                            pinNum = pinNum * 10;
                            pinNum += Character.getNumericValue(ledGpio.charAt(4));
                        }
                        HashMap<String, String> target = this.cfgU.getPinMapDetails(this.priChipName,
                            pinDetails.get("pin"));
                        this.cfgU.enableGpioPath(pinDetails.get("pin"), this.priChipName); // enable
                        // any
                        // mux
                        // in
                        // path
                        HashMap<String, String> chipDetail = this.cfgU.getChipMapRec(target.get("chipName"));
                        this.controlChip(chipDetail, pinNum, action, pinState);
                    } else {
                        ffdc.ffdcConfigWarningEntry("Pin : " + pinNumber + " No valid target");
                    }
                } else if (action.equals("reflect")) {
                    ffdc.ffdcDebugEntry("Reflect  " + pinNumber);

                    // do the LED work
                    ffdc.ffdcDebugEntry("PinMap details contains Pi gpioNumLED " + pinDetails.get("gpioNumLED"));
                    if (pinDetails.containsKey("gpioNumLED")) {
                        // what pin to drive
                        String ledGpio = pinDetails.get("gpioNumLED");
                        BaseGpioInOut gpio = new BaseGpioInOut(this.pi4j, this.ffdc, this.pinDict);
                        int pinNum = Character.getNumericValue(ledGpio.charAt(3)); // diox
                        if (ledGpio.length() == 5) {  // dioxx
                            pinNum = pinNum * 10;
                            pinNum += Character.getNumericValue(ledGpio.charAt(4));
                        }
                        int intPin = pinNum;
                        gpio.createOutPin("Util usage", intPin, DigitalState.HIGH);

                        if (pinState == DigitalState.HIGH) {
                            gpio.drivePinHigh(intPin);
                        } else {
                            gpio.drivePinLow(intPin);
                        }
                    } else if (pinDetails.containsKey("pin")) {
                        String ledGpio = pinDetails.get("pin");
                        int pinNum = Character.getNumericValue(ledGpio.charAt(3)); // pinx
                        if (ledGpio.length() == 5) {  // pinxx
                            pinNum = pinNum * 10;  // mov first digit to tens column
                            pinNum += Character.getNumericValue(ledGpio.charAt(4)); // last digit is ones column
                        }
                        HashMap<String, String> target = this.cfgU.getPinMapDetails(this.priChipName,
                            pinDetails.get("pin"));
                        this.cfgU.enableGpioPath(pinDetails.get("pin"), this.priChipName); // enable
                        // any
                        // mux
                        // in
                        // path
                        HashMap<String, String> chipDetail = this.cfgU.getChipMapRec(target.get("chipName"));
                        this.controlChip(chipDetail, pinNum, action, pinState);

                    } else {
                        ffdc.ffdcConfigWarningEntry("Pin : " + pinNumber + " No valid target");
                    }
                } else {
                    ffdc.ffdcConfigWarningEntry("Pin : " + pinNumber + " No valid acton");
                }
            } else {
                ffdc.ffdcConfigWarningEntry("Pin : " + pinNumber + " not found in GpioToApp map");
            }
        } else {
            ffdc.ffdcDebugEntry("Not for thios event hanlder configured pin, NOP");
        }
        ffdc.ffdcMethodExit("changeLed");
    }

    private void controlChip(HashMap<String, String> chipDetail, int pinNum, String action, DigitalState pinState) {
        // TODO Auto-generated method stub
        String chipPCA = chipDetail.get("pca");
        if ((chipPCA.equals("23017")) || (chipPCA.equals("23008"))) {
            int busNumber = Integer.parseInt(chipDetail.get("busNum").substring(2), 16);
            int chipAddress = Integer.parseInt(chipDetail.get("address").substring(2), 16);
            if (action.equals("hilow")) {
                try {
                    this.mcpObj.drivePin(pinNum, true);
                    Thread.sleep(3000);
                    this.mcpObj.drivePin(pinNum, false);
                } catch (InterruptedException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals("reflect")) {
                try {
                    this.mcpObj.drivePin(pinNum, pinState == DigitalState.HIGH);
                } catch (InterruptedException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("ChipName not found in PinInterruptLed:ControlChip implementation : " + chipPCA);
        }

    }

}

