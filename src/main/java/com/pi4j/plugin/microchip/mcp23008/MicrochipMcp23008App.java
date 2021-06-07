/*
 *
 *  *
 *  * -
 *  *   * #%L
 *  *   * **********************************************************************
 *  *   * ORGANIZATION  :  Pi4J
 *  *   * PROJECT       :  Pi4J :: EXTENSION
 *  *   * FILENAME      :  MicrochipMcp23008App.java
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

package com.pi4j.plugin.microchip.mcp23008;

import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.MicrochipMcp23008DigitalInputProvider;
import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.MicrochipMcp23008DigitalOutputProvider;
import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.MicrochipMcp23008DigitalInput;
import com.pi4j.plugin.microchip.mcp23008.provider.gpio.digital.MicrochipMcp23008DigitalOutput;

import com.pi4j.provider.Providers;
import com.pi4j.util.Console;
import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import com.pi4j.io.gpio.digital.*;



public class MicrochipMcp23008App {

    public static void main(String[] args) throws Exception {
        var console = new Console();
        console.title("<-- The Pi4J V2 Project Extension  -->", "MicrochipMcp23008App");
/*
        var contextBuilder  = Pi4J.newContextBuilder()
                .add(MicrochipMcp23008DigitalOutputProvider.newInstance())
                .add(MicrochipMcp23008DigitalInputProvider.newInstance());

        // Initialize Pi4J with a custom context
        var pi4j = contextBuilder.build();


   */
        // Initialize Pi4J with an auto context
        var pi4j = Pi4J.newAutoContext();


        // create custom INPUT and OUTPUT providers for the Microchip Mcp23008
        DigitalInputProvider dip = pi4j.provider(MicrochipMcp23008DigitalInputProvider.ID);
        DigitalOutputProvider dop = pi4j.provider(MicrochipMcp23008DigitalOutputProvider.ID);

        // create INPUTS (various supported methods)
        DigitalInput touchSensor1 = dip.create(1);
        DigitalInput touchSensor2 = dip.create(2);
        DigitalInput touchSensor3 = dip.create(3);
        DigitalInput touchSensor4 = pi4j.provider(MicrochipMcp23008DigitalInputProvider.class).create(4);
      //  MicrochipMcp23008DigitalInput touchSensor5 = pi4j.din().create(5);
        MicrochipMcp23008DigitalInput touchSensor5 = pi4j.provider(MicrochipMcp23008DigitalInputProvider.class).create(5);
        touchSensor5.address();
        touchSensor4.state();
        touchSensor5.state();

/*
This code includes two changes in the I2C to sync with core updates in the
latest pi4j_V2 build.

In my testing I modified the POM file to pull a unique named Core jar from
my workspaces, I commented out
I created class to have my own CONFIG for an input pin. Only
added a single attribute, IPOL.
This statement fails. The IOType resolves to DIGITAL_INPUT and the proper
  provider is not used. I updated IOType.java and tried
changing provider alias.  I was stumped on how the class gets the 'type'
attribute set for use in this IOType matching.

Also, Does InputMCP23xxxProviders  require every possible signature be added,
once all the pin attributes are added the parm combinations will be great.


[main] INFO com.pi4j.platform.impl.DefaultRuntimePlatforms - adding platform to managed platform map [id=raspberrypi; name=RaspberryPi Platform; priority=5; class=com.pi4j.plugin.raspberrypi.platform.RaspberryPiPlatform]
Exception in thread "main" com.pi4j.provider.exception.ProviderIOTypeException: Pi4J provider IO type mismatch for [microchip-mcp23008-digital-input(DIGITAL_INPUT)]; provider instance is not of IO type [MCP23XXX_INPUT]
	at com.pi4j@2.0-SNAPSHOT/com.pi4j.provider.Providers.get(Providers.java:257)
	at com.pi4j@2.0-SNAPSHOT/com.pi4j.context.Context.create(Context.java:316)
	at com.pi4j@2.0-SNAPSHOT/com.pi4j.internal.IOCreator.create(IOCreator.java:58)
	at com.pi4j@2.0-SNAPSHOT/com.pi4j.internal.IOCreator.create(IOCreator.java:122)
	at com.pi4j.devices.multi@0.0.1/com.pi4j.plugin.microchip.mcp23008.MicrochipMcp23008App.main(MicrochipMcp23008App.java:97)


 */
        DigitalMCP23xxxInput touchSensor6 = (DigitalMCP23xxxInput) pi4j.create(DigitalMCP23xxxInputConfig.newBuilder(pi4j)
                .provider(MicrochipMcp23008DigitalInputProvider.ID)
                .address(6).ipol(true).build());

        console.println("IPOL set   " + touchSensor6.ipol());
        touchSensor6.isLow();
        // create LEDs (various supported methods)
        DigitalOutput led1 = dop.create(1);
        DigitalOutput led2 = dop.create(2);
        DigitalOutput led3 = dop.create(3);
        DigitalOutput led4 = pi4j.provider(MicrochipMcp23008DigitalOutputProvider.class).create(4);
        //MicrochipMcp23008DigitalOutput led5 = pi4j.dout().create(5);
       // Integer integer = 22;
       // MicrochipMcp23008DigitalOutput led5 = pi4j.provider(MicrochipMcp23008DigitalOutputProvider.class).create(2);


        DigitalOutput led6 = pi4j.create(DigitalOutputConfig.newBuilder(pi4j)
                .provider(MicrochipMcp23008DigitalOutputProvider.ID)
                .address(6).build());
        led4.high();
        led4.isOn();
        led4.on();
        led6.high();
        led6.off();

        // print which providers were detected in classpath
        pi4j.providers().digitalOutput().describe().print(System.out);
        pi4j.providers().digitalInput().describe().print(System.out);
        pi4j.registry().describe().print(System.out);


        // Let's print out to the console the detected and loaded
        // providers that Pi4J detected when it was initialized.
        Providers providers = pi4j.providers();
        console.box("Pi4J PROVIDERS");
        console.println();
        providers.describe().print(System.out);
        console.println();
        // shutdown Pi4J context now
        pi4j.shutdown();
        // Initialize Pi
    }
}
