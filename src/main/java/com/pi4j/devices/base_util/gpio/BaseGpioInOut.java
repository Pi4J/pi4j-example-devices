/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  BaseGpioInOut.java
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

package com.pi4j.devices.base_util.gpio;


        import java.util.HashMap;
        import java.util.Iterator;
        import java.util.Map;
        import java.util.Set;
        import com.pi4j.devices.base_util.ffdc.FfdcUtil;
        import com.pi4j.io.exception.IOException;
        import com.pi4j.io.gpio.digital.DigitalInput;
        import com.pi4j.io.gpio.digital.DigitalOutput;
        import com.pi4j.io.gpio.digital.DigitalState;

        import static java.util.concurrent.TimeUnit.MICROSECONDS;
        import com.pi4j.context.Context;
        import com.pi4j.io.gpio.digital.PullResistance;

/**
 *  BaseGpioInOut
 *  <p>
 *      Utility class to create and access Gpios on the Pi.
 *  </p>
 */
public class BaseGpioInOut  implements GpioBasics {
    /**
     * BaseGpioInOut CTOR
     * <p>
     * PreCond: BaseGpioInOut CTOR called with valid parameters
     * <ul>
     *     <li>Instantiated Context class
     *     <li> Instantiated FFDC class
     *     <li> HashMap GpioPin to CfgData (Gpio Device)
     * </ul>
     * <p>
     * PostCond:  Class methods are now accessable
     */
    public BaseGpioInOut(Context pi4j,FfdcUtil ffdc, HashMap<Integer, GpioPinCfgData> dioPinData) {
        this.ffdc = ffdc;
       this.pinDict = dioPinData;
        this.ffdc.ffdcDebugEntry("\n\n\nBaseGpiIO CTOR \n\n\n");
        this.pi4j = pi4j;
        // Thread.dumpStack();
        this.initPin();

    }

    /**
     * Placeholder to add initialization code
     *
     */
    private boolean initPin() {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::initPin");
       
        this.ffdc.ffdcMethodExit("BaseGpioInOut::initPin " + true);
        return (true);
    }



    /**
     * pinExists
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict return true, else false
     *      </p>
     */
    public boolean pinExists(Integer pin) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::pinExists pin " + pin);
        this.dumpHashMap();
        boolean rtn = this.pinDict.containsKey(pin);
        this.ffdc.ffdcMethodExit("BaseGpioInOut::pinExists pin " + pin + "  " + rtn);
        // System.out.println("pinExist " + rtn);
        return (rtn);
    }

    /**
     * pinIsOutput
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict is output return true, else false
     *      </p>
     */
    public boolean pinIsOutput(Integer pin) {
        boolean rtn = false;
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::pinIsOutput pin " + pin);
        if (this.pinExists(pin)) {
            GpioPinCfgData pData = this.pinDict.get(pin);
            if (pData.direction == GpioPinCfgData.Direction.out) {
                rtn = true;
            }
        }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::pinIsOutput pin " + pin + " " + rtn);
        // System.out.println("pinIsOutput " + rtn);
        return (rtn);
    }

    /**
     * Debug usage, dump pinDict
     */
    public void dumpHashMap() {
        // Getting an iterator
        Iterator hmIterator = this.pinDict.entrySet().iterator();

        // Iterate through the hashmap
        // and add some bonus marks for every student
        this.ffdc.ffdcDebugEntry("HashMap pin data   this.pinDict : " + this.pinDict + "\n\n");
        Iterator<Map.Entry<Integer, GpioPinCfgData>> child = this.pinDict.entrySet().iterator();
        while (child.hasNext()) {
            Map.Entry childPair = child.next();
            this.ffdc.ffdcDebugEntry("childPair.getKey() :   " + childPair.getKey() + " childPair.getValue()  :  "
                    + childPair.getValue().toString());

        }

    }

    /**
     * pinIsInput
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict is input return true, else false
     *      </p>
     */
    public boolean pinIsInput(Integer pin) {
        boolean rtn = false;
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::pinIsInput pin " + pin);
        if (this.pinExists(pin)) {
            GpioPinCfgData pData = this.pinDict.get(pin);
            if (pData.direction == GpioPinCfgData.Direction.in) {
                rtn = true;
            }
        }
        // System.out.println("pinIsInput " + rtn);
        this.ffdc.ffdcMethodExit("BaseGpioInOut::pinIsInput pin " + pin + " " + rtn);
        return (rtn);
    }

    /**
     * getCfgData
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     *
     *     <p>
     *      PostCond:  If pin in pinDict, data returned
     *      </p>
     * @return GpioPinCfgData
     */
    public GpioPinCfgData getCfgData(Integer pin) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::getCfgData pin " + pin);
        GpioPinCfgData pData = null;
        if (this.pinExists(pin)) {
            pData = this.pinDict.get(pin);
        }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::getCfgData pin " + pin + " " + pData);

        return (pData);
    }

    /**
     * addPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param pin
     * @param data   GpioPinCfgData associated data
     *
     *     <p>
     *      PostCond:  If pin in pinDict, data returned
     *      </p>
     */
    public void addPin(Integer pin, GpioPinCfgData data) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::addPin pin " + pin + "  data" + data);
        this.dumpHashMap();
        this.pinDict.put(pin, data);
        this.dumpHashMap();
        this.ffdc.ffdcMethodExit("BaseGpioInOut::addPin pin " + pin);

    }

    /**
     * createInPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param name config id
     * @param number  Pin number,config address
     * @param pullup  config pull resistance
     *     <p>
     *      PostCond:  Pin device created, and  pin in pinDict
     *      </p>
     * @return if successful true else false
     */
    public boolean createInPin(String name, Integer number, PullResistance pullup) {
        boolean success = false;
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::createInPin  " + name + "pin " + number + "  up/dwn " + pullup);
        if (this.pinExists(number)) {
            this.ffdc.ffdcDebugEntry(" Pin  " + number + " already in map : " + this.getCfgData(number));
        } else {
            this.ffdc.ffdcDebugEntry("create inpin  " + name);
            var ledConfig = DigitalInput.newConfigBuilder(this.pi4j)
                    .id(name)
                    .name(name)
                    .address(number)
                    .pull(pullup)
                    .provider("pigpio-digital-input");
            DigitalInput input = null;
            try {
                input = this.pi4j.create(ledConfig);
            } catch (Exception e) {
                e.printStackTrace();
                this.ffdc.ffdcErrorEntry("create DigIn failed");
                this.ffdc.ffdcErrorExit("create DigIn failed", 200);
            }

            GpioPinCfgData pData = new GpioPinCfgData(number, GpioPinCfgData.Direction.in, null, input);
            this.addPin(number, pData);
            success = true;
            this.ffdc.ffdcDebugEntry("pData :" + pData);
        }
        this.dumpHashMap();
        this.ffdc.ffdcMethodExit("BaseGpioInOut::createInPin success " + success);

        return (success);

    }


    /**
     * createOutPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param name config id
     * @param number  Pin number,config address
     * @param initialValue  config initial
     *     <p>
     *      PostCond:  Pin device created, and  pin in pinDict
     *      </p>
     * @return if successful true else false
     */
     public boolean createOutPin(String name, Integer number, DigitalState initialValue) {
        boolean success = false;
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::createOutPin  " + name + " pin " + number + "  state " + initialValue);
        this.dumpHashMap();
        if (this.pinExists(number)) {
            this.ffdc.ffdcDebugEntry(" Pin  " + number + " already in map : " + this.getCfgData(number));
        } else {
            System.out.println("create outpin  " + name);
                    var ledConfig = DigitalOutput.newConfigBuilder(this.pi4j)
                    .id(name)
                    .name(name)
                    .address(number)
                    .shutdown(initialValue)
                    .initial(initialValue)
                    .provider("pigpio-digital-output");
            DigitalOutput output = null;
            try {
                output = this.pi4j.create(ledConfig);
            } catch (Exception e) {
                e.printStackTrace();
                this.ffdc.ffdcErrorEntry("create DigOut failed");
                this.ffdc.ffdcErrorExit("create DigOut failed", 200);
            }
           GpioPinCfgData pData = new GpioPinCfgData(number, GpioPinCfgData.Direction.out, output, null);
            this.addPin(number, pData);
            success = true;
            this.ffdc.ffdcDebugEntry("pData :" + pData);
        }
        this.dumpHashMap();
        this.ffdc.ffdcMethodExit("BaseGpioInOut::createOutPin success " + success);
        return (success);
    }

    /**
     * drivePinHigh
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number  Pin number
     *     <p>
     *      PostCond:  Pin driven high
     *      </p>
     */
    public void drivePinHigh(Integer number) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::drivePinHigh pin " + number);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.high();
            } catch (com.pi4j.io.exception.IOException e) {
                e.printStackTrace();
            }
        } else {
            this.ffdc.ffdcConfigWarningEntry("Invalid usage for pin direction");
            this.ffdc.ffdcErrorExit("Invalid pin direction",301);
        }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::drivePinHigh");
    }

    /**
     * drivePinLow
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number Pin number
     *     <p>
     *      PostCond:  Pin driven low
     *      </p>
     */
    public void drivePinLow(Integer number) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::drivePinLow pin " + number);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.low();
            } catch (com.pi4j.io.exception.IOException e) {
                e.printStackTrace();
            }
        } else {
            this.ffdc.ffdcErrorExit("Invalid usage for pin direction", 302);
        }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::drivePinLow");
    }

    /**
     * togglePin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number Pin number
     *     <p>
     *      PostCond:  Pin toggled
     *      </p>
     */
    public void togglePin(Integer number) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::togglePin pin " + number);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.toggle();
            } catch (com.pi4j.io.exception.IOException e) {
                e.printStackTrace();
            }
        } else {
            this.ffdc.ffdcErrorExit("Invalid usage for pin direction", 303);
            }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::togglePin");
    }

    /**
     * pulse
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number  Pin number
     * @parm count  pulse count
     *     <p>
     *      PostCond:  Pin pulsed
     *      </p>
     */
    public void pulse(Integer number, long count) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::pulse pin " + number + " count" + count);
        if (pinIsOutput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            try {
                pData.output.pulse(number, MICROSECONDS );
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.ffdc.ffdcErrorExit("Invalid usage for pin direction", 305);
        }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::pulse");
    }

    /**
     * readPin
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param number Pin number
     *     <p>
     *      PostCond:  Pin read if exists, else return  DigitalState.UNKNOWN
     *         </p>
     * @return  DigitalState
     */
    public DigitalState readPin(Integer number) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::readPin pin " + number);
        DigitalState rtnVal = DigitalState.UNKNOWN;
        if (pinIsInput(number)) {
            GpioPinCfgData pData = this.getCfgData(number);
            rtnVal = pData.input.state();
        } else {
            this.ffdc.ffdcErrorExit("Invalid usage for pin direction", 306);
        }
        this.ffdc.ffdcMethodExit("BaseGpioInOut::readPin  data + rtnVal");
        return (rtnVal);
    }

    /**
     * createGpioInstance
     * <p>
     * PreCond: BaseGpioInOut instance initialized.  See CTOR
     *
     * @param  pinCfgMap  Dictionary describing Gpio pins
     *   "{{'gpio24':{'name':'RedLED','dir':'out','initial':'high'}},
     *     {'gpio20':{'name':'sensor','dir':'in','pull':up'}}}"
     *
     *     <p>
     *      PostCond:  Pins created
     *         </p>
     * @return  if successful true else false
     */
    public boolean createGpioInstance(HashMap<String, HashMap<String, String>> pinCfgMap) {
        this.ffdc.ffdcMethodEntry("BaseGpioInOut::createGpioInstance");
        boolean rval =  false;
        HashMap<String, HashMap<String, String>> outerMap = pinCfgMap;
        Set outerSet = outerMap.entrySet();
        Iterator<Map.Entry<String, Map<String, String>>> outerIterator = outerSet.iterator();
        while (outerIterator.hasNext()) {
            Map.Entry<String, Map<String, String>> mentry = (Map.Entry) outerIterator.next();
            System.out.println("mentry  " + mentry);
            String pinName = mentry.getKey();
            if (pinName.startsWith("gpio") == false) {
                this.ffdc.ffdcErrorExit("illegal name prefix :" + pinName , 1001);
            }
            int pinNumber = Integer.parseInt(pinName.substring(4));
            int pin = pinNumber;

            // iterate inner map
            Map<String, String> innerMap = mentry.getValue();

            String dir = innerMap.get("dir");
            String name = innerMap.get("name");

            if (dir.equals("in")) {
                String pull = innerMap.get("pull");
                if (pull.equals("up")) {
                    rval =this.createInPin(name, pin, PullResistance.PULL_UP);
                } else {
                    rval = this.createInPin(name, pin, PullResistance.PULL_DOWN);
                }
            } else { // out direction
                String initial = innerMap.get("initial");
                if (initial.equals("high")) {
                    rval =this.createOutPin(name, pin,DigitalState.HIGH);
                } else {
                    rval = this.createOutPin(name, pin, DigitalState.LOW);
                }
            }
        }
        this.dumpHashMap();
        this.ffdc.ffdcMethodExit("BaseGpioInOut::createGpioInstance");
        return (rval);
    }



    public HashMap<Integer, GpioPinCfgData> pinDict;

    FfdcUtil ffdc;
    Context pi4j;

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // TESTING
    // Use single instance
  /*  public static void main(String[] args)
            throws IOException, InterruptedException, {
        System.out.println("test gpios out");
        LogManager lm = LogManager.getLogManager();
        Logger logger;
        logger = Logger.getLogger("LoggingExample1");

        FfdcControls ffdc = new FfdcControls(14, logger, true, "BaseGpioInOut");
        HashMap<Pin, PinCfgData> dioPinData = new HashMap<Pin, PinCfgData>();

        BaseGpioInOut gpio = new BaseGpioInOut(ffdc, dioPinData, logger);
        gpio.createOutPin("red Led", RaspiBcmPin.GPIO_24, PinState.LOW);
        System.out.println("high");
        gpio.drivePinHigh(RaspiBcmPin.GPIO_24);

        Thread.sleep(3000);
        System.out.println("low");
        gpio.drivePinLow(RaspiBcmPin.GPIO_24);

        Thread.sleep(3000);
        System.out.println("toggle");
        gpio.togglePin(RaspiBcmPin.GPIO_24);

        Thread.sleep(3000);
        System.out.println("toggle");
        gpio.togglePin(RaspiBcmPin.GPIO_24);

        Thread.sleep(3000);
        System.out.println("pulse");
        gpio.pulse(RaspiBcmPin.GPIO_24, 1000);

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        System.out.println("start input pin GPIO20");
        gpio.createInPin("pin22", RaspiBcmPin.GPIO_20, PinPullResistance.PULL_UP);
        System.out.println("pin 22 state " + gpio.readPin(RaspiBcmPin.GPIO_20));

        // gpio.getCfgData(RaspiBcmPin.GPIO_22).input
        // .addListener(new GpioUsageExampleListener() // GpioListener
        gpio.getCfgData(RaspiBcmPin.GPIO_20).input.addListener(new GpioListener());
        gpio.getCfgData(RaspiBcmPin.GPIO_20).input
                .addTrigger(new GpioSyncStateTrigger(gpio.getCfgData(RaspiBcmPin.GPIO_24).output));
        System.out.println(" 20 Listener = " + gpio.getCfgData(RaspiBcmPin.GPIO_20).input.getListeners());

        System.out.println("let matrix ground pin 22, trigger listener");
        int c = 0;
        while (true) {
            Thread.sleep(8000);
            System.out.println("Inside loop count " + c);
            System.out.println("pin 20 state " + gpio.readPin(RaspiBcmPin.GPIO_20));
            System.out.println("pin 20 state " + gpio.readPin(RaspiBcmPin.GPIO_20));
            c++;
            if (c > 20) {
                break;
            }
        }
        Thread.sleep(8000);

        System.out.println("Done");
        // gpio.pulse(1000, true); // set second argument to 'true' use a
        // blocking call

        // Raspi BcmPin
        // System.out.println("Temperature :" + readings[0] + " C");
        // System.out.println("Pressure :" + readings[1] + " mbar");
    }
*/
    // TESTING
    // Use multiple instances
    /*
     * public static void main(String[] args) throws IOException,
     * InterruptedException, PlatformAlreadyAssignedException,
     * UnsupportedBusNumberException { System.out.println("test gpios out");
     * GpioOut gpio_out = new GpioOut("red Led", RaspiBcmPin.GPIO_24,
     * PinState.LOW); System.out.println("high"); gpio_out.drivePinHigh();
     *
     * Thread.sleep(3000); System.out.println("low"); gpio_out.drivePinLow();
     *
     * Thread.sleep(3000); System.out.println("toggle"); gpio_out.togglePin();
     *
     * Thread.sleep(3000); System.out.println("toggle"); gpio_out.togglePin();
     *
     * Thread.sleep(3000); System.out.println("pulse"); gpio_out.pulse(1000);
     *
     * System.out.println("start input pin GPIO22"); GpioIn gpio_in = new
     * GpioIn("pin22", RaspiBcmPin.GPIO_22, PinPullResistance.PULL_UP);
     * System.out.println("pin 22 state " + gpio_in.readPin());
     * System.out.println("ground pin 22"); Thread.sleep(8000);
     * System.out.println("After grounding pin 22 state " + gpio_in.readPin());
     *
     * System.out.println("Remove pin 22"); Thread.sleep(8000);
     * System.out.println("After lifting ground of pin 22 state " +
     * gpio_in.readPin());
     *
     * System.out.println("Done"); // gpio.pulse(1000, true); // set second
     * argument to 'true' use a // blocking call
     *
     * // Raspi BcmPin // System.out.println("Temperature   :" + readings[0] +
     * " C"); // System.out.println("Pressure      :" + readings[1] + " mbar");
     * } }
     */

    /*public static class GpioUsageExampleListener  {   //implements GpioPinListenerDigital
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            // display pin state on console
            //System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        }
    }*/
}
