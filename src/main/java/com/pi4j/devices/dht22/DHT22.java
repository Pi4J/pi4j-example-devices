/*
 *
 *
 *     *
 *     * -
 *     * #%L
 *     * **********************************************************************
 *     * ORGANIZATION  :  Pi4J
 *     * PROJECT       :  Pi4J :: EXTENSION
 *     * FILENAME      :  DHT22.java
 *     *
 *     * This file is part of the Pi4J project. More information about
 *     * this project can be found here:  https://pi4j.com/
 *     * **********************************************************************
 *     * %%
 *     *   * Copyright (C) 2012 - 2022 Pi4J
 *      * %%
 *     *
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
 *     *
 *
 *
 *
 */

package com.pi4j.devices.dht22;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import com.pi4j.util.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


 /**
 * If the commented use of the listener DataInGpioListener, this would
 * be a more normal implementation. However, the time to idle the gpio from output operation and
 * re-init the gpio as an input with a listener takes too long to complete
 * and DHT22  signals are lost and the device attempt to send data fails.
 *
 * So, for the time being  a simple polling implementation is used.
 */


public class DHT22 {

    private final Console console;
    private final Context pi4j;
    private DigitalOutput oeGpio = null;
    private int dataPinNum = 0xff;
   private final String traceLevel;
    private Logger logger;
    private DigitalOutput dataOut = null;
    private DigitalInput dataIn = null;

    private DigitalOutputConfigBuilder outputConfig1;
    private DigitalInputConfigBuilder inputConfig1;

    private DHT22.DataInGpioListener listener;


   long timeElapsed;
    boolean data_bits_started = false;

    long dataBits = 0;
    int bitCounter = 0;
    long endInstant;

    boolean awaitingHigh;


    public DHT22(Context pi4j, Console console, int dataPinNum,  String traceLevel){
        super();
        this.console = console;
        this.pi4j = pi4j;
        this.dataPinNum = dataPinNum;
        this.traceLevel = traceLevel;
        this.awaitingHigh = true;
        this.init();
    }

    private void init(){
        System.setProperty("org.slf4j.simpleLogger.log." + DHT22.class.getName(), this.traceLevel);
        this.logger = LoggerFactory.getLogger(DHT22.class);
        this.logger.trace(">>> Enter: init");

        this.logger.trace("Data Pin  " + this.dataPinNum);
     //   this.listener = new DHT22.DataInGpioListener();

        this.outputConfig1 = DigitalOutput.newConfigBuilder(pi4j)
                .id("Data_Out")
                .name("Data_Out")
                .address(this.dataPinNum)
                .shutdown(DigitalState.HIGH)
                .initial(DigitalState.HIGH)
                .provider("linuxfs-digital-output");


        this.inputConfig1 = DigitalInput.newConfigBuilder(pi4j)
                .id("Data_In")
                .name("Data_In")
                .address(this.dataPinNum)
                .pull(PullResistance.OFF)
                .provider("linuxfs-digital-input");



        this.logger.trace("<<< Exit: init");
    }


    // Various logging routines commented out as their cose creates errors in reading the DHT22 waveform
    private void createOutputPin(){
     //  this.logger.trace(">>> Enter: createOutputPin");
        if(this.dataOut == null) {
            this.dataOut = pi4j.create(this.outputConfig1);
        }else{
            this.dataOut.initialize(this.pi4j);
        }
     //   this.logger.trace("<<< Exit: createOutputPin");
    }

    private void createInputPin(){
     //   this.logger.trace(">>> Enter: createInputPin");
        if(this.dataIn == null) {
            this.dataIn = pi4j.create(this.inputConfig1);
        }else{
            this.dataIn.initialize(this.pi4j);
        }
    /*    if(this.listener == null){
            this.listener =  new DHT22.DataInGpioListener();
        }
        this.dataIn.addListener(this.listener);
      */
     //   this.logger.trace("<<< Exit: createInputPin");

    }

    private void idleOutputPin(){
    //    this.logger.trace(">>> Enter: idleOutputPin");
        this.dataOut.shutdown(this.pi4j);
  //      this.logger.trace("<<< Exit: idleOutputPin");
    }

    private void idleInputPin(){
      //  this.logger.trace(">>> Enter: idleInputPin");
        if(this.dataIn !=null) {
         //   this.dataIn.removeListener(this.listener);
            this.dataIn.shutdown(this.pi4j);
        }
     //   this.logger.trace("<<< Exit: idleInputPin");
    }


    public void readAndDisplayData(){
        {
            double temperature, humidity;

            double [] res;
            for (int i=0; i<DHT22_Declares.TOTAL_NUM_BITS; i++)
                if ((res = read()) != null)
                {
                    temperature = res[0];
                    humidity = res[1];
                    String sign = "";
                    if(((long)temperature & 0x8000) > 0){
                        sign = "-";
                    }
                    System.out.println("\n    RH : " + humidity + "  T : " + sign +  ((temperature* 1.8) + 32) +"\n");
                    break;
                }
                else try {Thread.sleep(300);} catch (Exception e) {}
        }

          // this.readAndDisplayDataLL();

    }

    private double [] read()
    {
        this.logger.trace(">>> Enter: read");
        this.createOutputPin();
        this.dataOut.state(DigitalState.LOW);
        long now = System.nanoTime();
        while (System.nanoTime()-now < 2000000);
        this.dataOut.state(DigitalState.HIGH);
        this.idleOutputPin();

        this.createInputPin();
        now = System.nanoTime();
        DigitalState state = this.dataIn.state();
        long val = 0, lastHi = now;
        int read = 0;
        while (System.nanoTime()-now < 10000000)
        {
            DigitalState next = this.dataIn.state();
           // this.logger.trace("Pin's state was :" + next);
            if (state != next)
            {
                if (next == DigitalState.HIGH)
                    lastHi = System.nanoTime();
                else
                {
                    val = (val << 1);
                    read++;
                    if ((System.nanoTime()-lastHi)/1000 > 48)
                        val++;//  long duration high, signifies a 1
                }
                state = next;
            }
        }
        this.idleInputPin();
        double rval[] = null;
        double temperature = 0.0;
        double humidity = 0.0;
        //should be 40 but the first few bits are often missed and often equal 0
        //But enough read to see if the checksum validate we read all pertinent bit;
        if (read >= 38)
        {
            int hi = (int)((val & 0xff00000000L) >> 32), hd = (int)((val & 0xff000000L) >> 24),
                    ti = (int)((val & 0xff0000) >> 16), td = (int)((val & 0xff00) >> 8),
                    cs = (int)(val & 0xff);
            //checksum validation
            if (cs == ((hi+hd+ti+td) & 0xff))
            {
                temperature = ((((ti & 0x7f) << 8)+td)/10.)*((ti & 0x80) != 0 ? -1 : 1); // check if sign bit set (neg) multi by -1
                humidity =  ((hi << 8)+hd)/10;
                rval =  new double [] {temperature, humidity};

                this.logger.trace("Decoded values   T: "+ temperature +  "/" + ((temperature* 1.8) + 32) + "   RH : " + humidity );
            }else{
                this.logger.trace("Checksum failed val  : "  +val); //will return null and read() will be called again
           }
        }
       this.logger.trace("<<< Exit");
        return rval;
    }

    /**
     * If the commented use of the listener DataInGpioListener, this would
     * be a more normal implementation. However, the time to idle the gpio from output operation and
     * re-init the gpio as an input with a listener takes too long to complete
     * and DHT22  signals are lost and the device attempt to send data fails.
     *
     * So, for the time being  a simple polling implementation is used.
     */
    private void readAndDisplayDataLL() {
        this.logger.trace(">>> Enter: readAndDisplayDataLL");
          long now = System.nanoTime();
           this.idleInputPin();  // possibly exists from previous OP
            this.createOutputPin();
            this.dataOut.low();
            while (System.nanoTime() - now < 2000000) {
                ;
            }
            this.dataOut.high();
            this.idleOutputPin();
            this.createInputPin();

            this.logger.trace("<<< Exit: readAndDisplayDataLL");

    }
    private void process_timeCalc() {
        long durationMics = this.timeElapsed / 1000;
       // this.logger.trace(" >>> Enter: process_timeCalc  duration MICs " + durationMics  + " bit counter : " +  this.bitCounter);
        if ((durationMics > DHT22_Declares.ZERO_PULSE_MICS - 5) && (durationMics < DHT22_Declares.ZERO_PULSE_MICS + 5)) {
            // zero bit
          //  this.logger.trace("zero bit ");
            this.dataBits = (this.dataBits << 1) | 0;
            this.bitCounter++;
        } else if ((durationMics > DHT22_Declares.ONE_PULSE_MICS - 5) && (durationMics < DHT22_Declares.ONE_PULSE_MICS + 5)) {
            // one bit
          //  this.logger.trace("one bit ");
            this.dataBits = (this.dataBits << 1) | 1;
            this.bitCounter++;
        }
      //  this.logger.trace("this.bitCounter " + this.bitCounter);

        if(this.bitCounter >= DHT22_Declares.TOTAL_NUM_BITS){
            this.data_bits_started = false;
            // display the data
            //TODO validate cksum
            long rh = (this.dataBits >> (DHT22_Declares.T_NUM_BITS + DHT22_Declares.CKSUM_NUM_BITS))/10;
            long t = ((this.dataBits >> DHT22_Declares.CKSUM_NUM_BITS)  & 0xffff)/10;
            // ready for next calculation
            this.data_bits_started = true;
            this.bitCounter = 0;
            this.dataBits = 0;
            String sign = "";
            if((t&0x80) > 0){
                sign = "-";
            }
            this.logger.trace("RH = " + rh + "   t = : " + sign + ((t * 1.8) + 32));
        }

       // this.logger.trace(" <<< Exit: process_timeCalc ");
    }

    protected void sleepTimeMilliS(int milliSec) {
        TimeUnit tu = TimeUnit.MILLISECONDS;
        try {
            tu.sleep(milliSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /* Listener class        */

    private static class DataInGpioListener implements DigitalStateChangeListener {

        Instant startInstant;
        Duration timeElapsed;
        boolean data_bits_started = false;

        long dataBits = 0;
        int bitCounter = 0;

        public DataInGpioListener() {
            System.out.println("DataInGpioListener ctor");
           /* Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("DataInGpioListener: Performing ctl-C shutdown");
                    // Thread.dumpStack();
                }
            });
            */
        }

        @Override
        public void onDigitalStateChange(DigitalStateChangeEvent event) {
            System.out.println(">>> Enter: onDigitalStateChange");
            this.startInstant = Instant.now(); //  init Duration because first event is Low,
            // this is in prep to begin sending high----low transition to signify 0 or 1
            if (event.state() == DigitalState.HIGH) {
                this.startInstant = Instant.now();
                System.out.println("onDigitalStateChange Pin went High");
            } else if (event.state() == DigitalState.LOW) {
                Instant endInstant = Instant.now();
                System.out.println("onDigitalStateChange Pin went Low");
                this.timeElapsed = Duration.between(startInstant, endInstant);
                System.out.println("timeElapsed time MicS " + timeElapsed.getNano() / 1000);
                this.process_timeCalc();
            } else {
                System.out.println("Strange event state  " + event.state());
            }
            System.out.println("<<< Exit: onDigitalStateChange");
        }

        private void process_timeCalc() {
            long durationMics = this.timeElapsed.getNano() / 1000;
            System.out.println(" >>> Enter: process_timeCalc  duration MICs " + durationMics  + " bit counter : " +  this.bitCounter);
                if ((durationMics > DHT22_Declares.ZERO_PULSE_MICS - 5) && (durationMics < DHT22_Declares.ZERO_PULSE_MICS + 5)) {
                    // zero bit
                    System.out.println("zero bit ");
                    this.dataBits = (this.dataBits << 1) | 0;
                } else if ((durationMics > DHT22_Declares.ONE_PULSE_MICS - 5) && (durationMics < DHT22_Declares.ONE_PULSE_MICS + 5)) {
                    // one bit
                    System.out.println("one bit ");
                    this.dataBits = (this.dataBits << 1) | 1;
                }
                this.bitCounter++;
                System.out.println("this.bitCounter " + this.bitCounter);

                if(this.bitCounter >= DHT22_Declares.TOTAL_NUM_BITS){
                    this.data_bits_started = false;
                    // display the data
                    //TODO validate cksum
                    long rh = (this.dataBits >> (DHT22_Declares.T_NUM_BITS + DHT22_Declares.CKSUM_NUM_BITS))/10;
                    long t = ((this.dataBits >> DHT22_Declares.CKSUM_NUM_BITS)  & 0xffff)/10;
                    // ready for next calculation
                    this.data_bits_started = true;
                    this.bitCounter = 0;
                    this.dataBits = 0;
                    System.out.println("RH = " + rh + "   t = : " + ((t * 1.8) + 32));
                }

            System.out.println(" <<< Exit: process_timeCalc ");
        }


   }

}

