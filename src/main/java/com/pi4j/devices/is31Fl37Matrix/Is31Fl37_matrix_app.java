package com.pi4j.devices.is31Fl37Matrix;/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  Is31fl37_matrix_app.java
 *      *
 *      * This file is part of the Pi4J project. More information about
 *      * this project can be found here:  https://pi4j.com/
 *      * **********************************************************************
 *     * %%
 *     * Copyright (C) 2012 - 2022 Pi4J
 *     * %%
 *     * Licensed under the Apache License, Version 2.0 (the "License");
 *     * you may not use this file except in compliance with the License.
 *     * You may obtain a copy of the License at
 *     *
 *     *      http://www.apache.org/licenses/LICENSE-2.0
 *     *
 *     * Unless required by applicable law or agreed to in writing, software
 *     * distributed under the License is distributed on an "AS IS" BASIS,
 *     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     * See the License for the specific language governing permissions and
 *     * limitations under the License.
 *     * #L%
 *   *
 *
 *
 */

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//import com.pi4j.io.gpio.Pin;




public class Is31Fl37_matrix_app {

    private  DigitalOutput processGPIO;
    private  DigitalOutput warnGPIO;
    private Context pi4j = null;
    private int address;
    private int bmp_address;
    private int bus_num;
    private int bmp_bus;
    private boolean verbose = false;
    private int loop_count = 0;
    private int repeat_count;
    private int led_blink = 0;
    private String traceLevel;
    //mapUtils mapUtils;
    private Logger logger = null;
    private DigitalOutput resetPin = null;
    private DigitalInput monitorPin = null;
    private Console console;

    //GpioUtil gpioUtil;

  

    public Is31Fl37_matrix_app() {
        System.out.println(" default ctor");
    }

    public Is31Fl37_matrix_app(Context pi4j, Console console, int bus, int address, int bmpBus, int bmp_address, int loop_count, int repeat_count, int led_blink,
                               String traceLevel, DigitalOutput resetPin, DigitalInput monitorPin, DigitalOutput warnGpio, DigitalOutput processGpio) {
        super();
        this.bus_num = bus;
        this.address = address;
        this.bmp_address = bmp_address;
        this.loop_count = loop_count;
        this.repeat_count = repeat_count;
        this.led_blink = led_blink;
        this.traceLevel = traceLevel;
        this.resetPin = resetPin;
        this.monitorPin = monitorPin;
        this.pi4j = pi4j;
        this.console = console;
        this.bmp_bus = bmpBus;
        this.warnGPIO = warnGpio;
        this.processGPIO = processGpio;

        // "trace", "debug", "info", "warn", "error" or "off"). If not specified, defaults to "info"
        //  must fully qualify logger as others exist and the slf4 code will use the first it
        //  encounters if using the defaultLogLevel
        System.setProperty("org.slf4j.simpleLogger.log." + Is31Fl37_matrix_app.class.getName(), this.traceLevel);

        this.logger = LoggerFactory.getLogger(Is31Fl37_matrix_app.class);

        // this.print_state();
    }

    public void print_state() {
        /*
         * System.out.println(" print_state()   address: " + this.address +
         * " bus_num: " + this.bus_num + " traceLevel: " +
         * this.traceLevel + " loop_count: " + this.loop_count +
         * " repeat_count: " + this.repeat_count + " led_blink: " +
         * this.led_blink);
         */
        this.logger.info(" print_state()   Matrix address: " + String.format("0x%02X", this.address) + " Matrix bus_num: "
                + this.bus_num +   "  BMP address: " + String.format("0x%02X", this.bmp_address) + " BMP bus_num: "
                + this.bmp_bus +" traceLevel: " + this.traceLevel + " loop_count: " + this.loop_count
                + " repeat_count: " + this.repeat_count + " led_blink: " + this.led_blink);

    }

    public void clearGpioCfg() {
       // final GpioController gpio = GpioFactory.getInstance();
       // gpio.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Logger logger = Logger.getLogger("ibm.simics.ProcessTable");
        /*
         * Properties props = new java.util.Properties(); FileInputStream fis =
         * new FileInputStream("table_wire.properties");
         *
         * props.load(fis);
         */
        int address = 0xff;
        int bmp_address = 0xff;
        int bus_num = 0xff;
        int bmp_bus = 0xff;
        int loop_count = 0;
        int repeat_count = 0;
        int led_blink = 0;
        String traceLevel = "info";
        boolean tmpFileUse = false;
        boolean parmsOk = true;
        int resetPinNum = 0xff;
        int monitorPinNum = 0xff;
        int processGPIO = 0xff;
        int warnGPIO = 0xff;
        boolean createWarnPin = false;
        boolean createProcessPin = false;
        DigitalOutput processPin = null;
        DigitalOutput warnPin = null;
        DigitalOutput resetPin = null;
        DigitalInput monitorPin = null;
        boolean eChip = false;
        String mainChip = null;
        boolean doReset = false;

  
        final Console console = new Console();
        Context pi4j = Pi4J.newAutoContext();



        console.println("Is31fl37_matrix_app entered   :  ");
        String badParm = "";
        for (int i = 0; i < args.length; i++) {
            String o = args[i];
            if (o.contentEquals("-t")) {
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(40);
                }
            } else if (o.contentEquals("-h")) {
                console.println(
                        " java --module-path . --module  com.pi4j.devices.multi/com.pi4j.devices.is31fl37Matrix.Is31fl37_matrix_app  -b 0x1 -a 0x74 <matrix> " +
                                " -bmpB 0x1 <BMP280 bus> -bmpA 0x76 BMP280 address  -g  GPIO processing LED      -w GPIO warning LED  " +
                                "-i intensity -c <repeat_count,0 infinite>  -l <displays>   -s log  -r resetGpio -z monitorGpio#  " +
                                "-t values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\"  Default \"info\""  );
                System.exit(0);
            } else if (o.contentEquals("-r")) {
                String a = args[i + 1];
                resetPinNum = Integer.parseInt(a);
                console.println("resetPin  " + resetPinNum);
                doReset = true;
                i++;
            } else if (o.contentEquals("-z")) {
                String a = args[i + 1];
                monitorPinNum = Integer.parseInt(a);
                console.println("monitorPin  " + monitorPinNum);
                i++;
            }  else if (o.contentEquals("-b")) {
                String a = args[i + 1];
                bus_num = Integer.parseInt(a.substring(2), 16);
                i++;
            }  else if (o.contentEquals("-bmpB")) {
                String a = args[i + 1];
                bmp_bus = Integer.parseInt(a.substring(2), 16);
                i++;
            }else if (o.contentEquals("-l")) {
                String a = args[i + 1];
                i++;
                int num = Integer.parseInt(a);
                if (num > 7) {
                    console.println("Too many loops, MAX of 7");
                    badParm = "Too many loops, MAX of 7";
                    parmsOk = false;
                } else {
                    loop_count = Integer.parseInt(a);
                }
            } else if (o.contentEquals("-i")) {
                String a = args[i + 1];
                i++;
                int num = Integer.parseInt(a);
                if (num > 810) {
                    console.println("Intensity too high, MAX of 810");
                    badParm = "Intensity too high, MAX of 810";
                    parmsOk = false;
                } else {
                    led_blink = Integer.parseInt(a);
                }
            } else if (o.contentEquals("-a")) {
                String a = args[i + 1];
                i++;
                address = Integer.parseInt(a.substring(2), 16);
                // display_app.address = Integer.parseInt(a, 16);
            }else if (o.contentEquals("-bmpA")) {
                String a = args[i + 1];
                i++;
                bmp_address = Integer.parseInt(a.substring(2), 16);
                // display_app.address = Integer.parseInt(a, 16);
            } else if (o.contentEquals("-t")) {
                String a = args[i + 1];
                i++;
                traceLevel = a;
                if (a.contentEquals("trace") | a.contentEquals("debug") | a.contentEquals("info") | a.contentEquals("warn") | a.contentEquals("error") | a.contentEquals("off")) {
                    console.println("Changing trace level to : " + traceLevel);
                } else {
                    console.println("Changing trace level invalid  : " + traceLevel);
                    System.exit(41);
                }
              } else if (o.contentEquals("-c")) {
                String a = args[i + 1];
                i++;
                repeat_count = Integer.parseInt(a.substring(0));
            } else if (o.contentEquals("-s")) {
                tmpFileUse = true;

            } else if (o.contentEquals("-y")) { // mainChip
                mainChip = args[i + 1];

                // >>>tcaObj.bus_num = Integer.parseInt(a.substring(2), 16);
                i++;
            }else if (o.contentEquals("-g")) {
                String a = args[i + 1];
                createProcessPin = true;
                processGPIO = Integer.parseInt(a);
                console.println("processGPIO Pin  " + processGPIO);
                i++;
            } else if (o.contentEquals("-w")) {
                String a = args[i + 1];
                createWarnPin = true;
                warnGPIO = Integer.parseInt(a);
                console.println("warnLED pin  " + warnGPIO);
                i++;
            }  else {
                badParm = o;
                parmsOk = false;
            }
        }




        // create the required GPIOs
        var ledConfigIntr = DigitalInput.newConfigBuilder(pi4j)
                .id("MatrixInterrupt")
                .name("MatrixInterrupt")
                .address(monitorPinNum)
                .pull(PullResistance.PULL_UP)
                .debounce(4000L)
                .provider("linuxfs-digital-input");
        try {
            monitorPin = pi4j.create(ledConfigIntr);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigitalIn failed");
            System.exit(200);
        }
        var ledConfigReset = DigitalOutput.newConfigBuilder(pi4j)
                .id("MatrixReset")
                .name("MatrixReset")
                .address(resetPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("linuxfs-digital-output");
        try {
            resetPin = pi4j.create(ledConfigReset);
        } catch (Exception e) {
            e.printStackTrace();
            console.println("create DigOut reset failed");
            System.exit(201);
        }

        if(createProcessPin) {
            var ledConfigGreen = DigitalOutput.newConfigBuilder(pi4j)
                    .id("ProcessLED")
                    .name("ProcessLED")
                    .address(processGPIO)
                    .shutdown(DigitalState.LOW)
                    .initial(DigitalState.LOW)
                    .provider("linuxfs-digital-output");
            try {
                processPin = pi4j.create(ledConfigGreen);
            } catch (Exception e) {
                e.printStackTrace();
                console.println("create DigOut Process LED failed");
                System.exit(201);
            }
        }

        if(createWarnPin) {
            var ledConfigRed = DigitalOutput.newConfigBuilder(pi4j)
                    .id("WarnLED")
                    .name("WarnLED")
                    .address(warnGPIO)
                    .shutdown(DigitalState.LOW)
                    .initial(DigitalState.LOW)
                    .provider("linuxfs-digital-output");
            try {
                warnPin = pi4j.create(ledConfigRed);
            } catch (Exception e) {
                e.printStackTrace();
                console.println("create DigOut Warn LED failed");
                System.exit(202);
            }
        }

        if(doReset){
            console.println("Do chip reset");
            resetPin.low();
            Thread.sleep(600);
            resetPin.high();
        }
        Is31Fl37_matrix_app display_app = new Is31Fl37_matrix_app(pi4j,console, bus_num, address, bmp_bus, bmp_address, loop_count,
                repeat_count, led_blink, traceLevel, resetPin,  monitorPin, warnPin, processPin);


        if (parmsOk == false) {
            console.println("Invalid parm : " + badParm);
            console.println(
                    " java --module-path . --module  com.pi4j.devices.multi/com.pi4j.devices.is31fl37Matrix.Is31fl37_matrix_app  -b 0x1 -a 0x74 <matrix> " +
                            " -bmpB 0x1 <BMP280 bus> -bmpA 0x76 BMP280 address  -g  GPIO processing LED      -w GPIO warning LED  " +
                            "-i intensity -c <repeat_count,0 infinite>  -l <displays>   -s log  -r resetGpio -z monitorGpio#  " +
                            "-t values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\"  Default \"info\""  );
            System.exit(0x42);
        }

        console.println(" Address : " + Integer.toHexString(address));

        console.println("Arg processing completed...");


        try {
            Is31Fl37Matrix matrix = new Is31Fl37Matrix(display_app.bus_num, display_app.address,
                    display_app.bmp_address, display_app.logger, display_app.monitorPin, display_app.pi4j);
            DisplayTemp display = new DisplayTemp(display_app.pi4j, display_app.console, matrix, display_app.logger);
            ControlLeds pin_monitor = new ControlLeds(display_app.pi4j, display_app.console,display_app.warnGPIO, display_app.processGPIO,  matrix, display_app.logger);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            int c = 0;

            // if leg displays temperature/time count times then exists.
            if (display_app.repeat_count > 0) {
                for (int i = 0; i < display_app.repeat_count; i++) {
                    // future // eChip = cfgU.enableChipPath(mainChip);
                    // cfgU.displayEnableReg(bus_num, mainChip);
                    // cfgU.runCli();

                    display.process_bmp_data(pin_monitor,
                            display_app.led_blink, display_app.loop_count, display_app.bmp_bus, display_app.bmp_address);
                    // Thread.sleep(2000);

                    // clear the matrix
                    for (c = 0; c < 7; c++) {
                        matrix.fill(0, (byte) 0, 0);
                    }
                    // future //eChip = cfgU.enableChipPath(mainChip);
                    display.show_time(pin_monitor, display_app.led_blink,
                            display_app.loop_count);
                   // clear the matrix
                    for (c = 0; c < 8; c++) {
                        matrix.fill(0, (byte) 0, 0);
                    }
                }
                matrix.blink_write(0);
                // clear the matrix
                for (c = 0; c < 8; c++) {
                    matrix.fill(0, (byte) 0, 0);
                }
             } else {  // else leg continuously displays temperature/time
                while (true) {
                    // future // eChip = cfgU.enableChipPath(mainChip);
                    display.process_bmp_data(pin_monitor,
                            display_app.led_blink, display_app.loop_count, display_app.bmp_bus, display_app.bmp_address);
                    Thread.sleep(2000);
                    // clear the matrix
                    for (c = 0; c < 8; c++) {
                        matrix.fill(0, (byte) 0, 0);
                    }
                    // future  //eChip = cfgU.enableChipPath(mainChip);
                    display.show_time(pin_monitor, display_app.led_blink,
                            display_app.loop_count);
                    Thread.sleep(2000);
                    // clear the matrix
                    for (c = 0; c < 8; c++) {
                        matrix.fill(0, (byte) 0, 0);
                    }
                }
                /*
                 * display.blink(0); // THE FOLLOWING NEEDS A CTL-C HANDLER for
                 * (int i; i < 7; i++) { display.fill(1, false, 0); }
                 */
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        display_app.logger.info("program ending normal");
        display_app.clearGpioCfg();
        System.exit(0);

    }

}