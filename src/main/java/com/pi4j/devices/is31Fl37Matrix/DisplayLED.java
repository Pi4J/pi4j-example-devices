package com.pi4j.devices.is31Fl37Matrix;/*
 *
 *
 *
 *      * #%L
 *      * **********************************************************************
 *      * ORGANIZATION  :  Pi4J
 *      * PROJECT       :  Pi4J :: EXTENSION
 *      * FILENAME      :  DisplayLED.java
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


import org.slf4j.Logger;

/**
 * Capable of translating ascii data of temperature or time-of-day into LED matrix frames
 */
public class DisplayLED {

 
    public DisplayLED(Logger logger) {
            super();
            this.logger = logger;
        }

        public void usage() {
            System.out.println("option f:func,b:bus,-a:address,-t bmp_address ,-n number,-x:axis,-y:axis,-o:ON");
            System.out.println("python display_led.py  -b 1 -a 0X74  -t 0x77 -o ON -t 12:34  -w STEADY -l 1");
            System.out.println("python display_led.py  -b 1 -a 0x74 -t 0x77 -x 0 -y 0 -o ON ");
            System.out.println("python display_led.py  -b 1 -a 0x74 -t 0x77 -n 1 -w BLINK or STEADY");
        }

        // pixels for 0 1 2 3 4 5 6 7 8 9 :
        // First element is the width in pixels of the displayed number
        int[] a;
        int[] xy0 = new int[] { 0, 0, 1, 0, 2, 0, 0, 1, 2, 1, 0, 2, 2, 2, 0, 3, 2, 3, 0, 4, 2, 4, 0, 5, 2, 5, 0, 6, 2, 6, 2,
                6, 1, 6 };
        int[] xy1 = new int[] { 0, 1, 1, 0, 1, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 6, 0, 6, 2, 6 };
        int[] xy2 = new int[] { 0, 1, 0, 2, 1, 0, 2, 0, 3, 1, 3, 2, 3, 3, 2, 4, 1, 5, 0, 6, 1, 6, 2, 6, 3, 6 };
        int[] xy3 = new int[] { 0, 1, 1, 0, 2, 0, 3, 1, 3, 2, 2, 3, 3, 4, 3, 5, 0, 5, 1, 6, 2, 6 };
        int[] xy4 = new int[] { 0, 0, 0, 1, 0, 2, 0, 3, 1, 3, 2, 3, 3, 0, 3, 1, 3, 2, 3, 3, 3, 4, 3, 5, 3, 6 };
        int[] xy5 = new int[] { 0, 0, 0, 1, 0, 2, 0, 6, 1, 0, 2, 0, 3, 0, 1, 2, 2, 3, 3, 4, 2, 5, 1, 6, 0, 6 };
        int[] xy6 = new int[] { 1, 0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 1, 3, 2, 3, 3, 4, 3, 5, 2, 6, 1, 6 };
        int[] xy7 = new int[] { 0, 0, 0, 1, 1, 0, 2, 0, 3, 0, 3, 1, 3, 2, 3, 3, 3, 4, 3, 5, 3, 6 };
        int[] xy8 = new int[] { 0, 1, 0, 2, 1, 0, 2, 0, 3, 1, 3, 2, 1, 3, 2, 3, 0, 4, 0, 5, 1, 6, 2, 6, 3, 4, 3, 5 };
        int[] xy9 = new int[] { 0, 1, 0, 2, 1, 0, 2, 0, 3, 1, 3, 2, 1, 3, 2, 3, 3, 3, 3, 4, 3, 5, 3, 6 };
        int[] xy10 = new int[] { 0, 2, 0, 4 };
        int[][] pixels = new int[][] { xy0, xy1, xy2, xy3, xy4, xy5, xy6, xy7, xy8, xy9, xy10 };

        int[] width = new int[] { 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 1 };

        // List<List<List<int>>> listOfListsOfLists =new
        // ArrayList<List<List<int>>>(1);
        // list [ list [list,list] ] ]

        /*
         * // 0 [ [
         * [3],[[0,0],[1,0],[2,0],[0,1],[2,1],[0,2],[2,2],[0,3],[2,3],[0,4],[2,4]
         * ,[0,5],[2,5],[0,6],[2,6],[2,6],[1,6]] ], [ // 1
         * [3],[[0,1],[1,0],[1,1],[1,2],[1,3],[1,4],[1,5],[1,6],[0,6],[2,6]] ], // 2
         * [
         * [4],[[0,1],[0,2],[1,0],[2,0],[3,1],[3,2],[3,3],[2,4],[1,5],[0,6],[1,6],[2
         * , 6],[3,6]] ] , // 3
         * [[4],[[0,1],[1,0],[2,0],[3,1],[3,2],[2,3],[3,4],[3,5],[0,5],[1,6],[2,6]]]
         * ,// 4
         * [[4],[[0,0],[0,1],[0,2],[0,3],[1,3],[2,3],[3,0],[3,1],[3,2],[3,3],[3,4
         * ],[3,5],[3,6]] ], [ // 5
         * [4],[[0,0],[0,1],[0,2],[0,6],[1,0],[2,0],[3,0],[1,2],[
         * 2,3],[3,4],[2,5],[1,6],[0,6]] ], [ // 6
         * [4],[[1,0],[0,1],[0,2],[0,3],[0,4],[0,5
         * ],[1,3],[2,3],[3,4],[3,5],[2,6],[1,6]] ], [ //7
         * [4],[[0,0],[0,1],[1,0],[2,0],[3,0],[3,1],[3,2],[3,3],[3,4],[3,5],[3,6]]
         * ], [ //8
         * [4],[[0,1],[0,2],[1,0],[2,0],[3,1],[3,2],[1,3],[2,3],[0,4],[0,5],[1,6
         * ],[2,6],[3,4],[3,5]] ], [ // 9
         * [4],[[0,1],[0,2],[1,0],[2,0],[3,1],[3,2],[1,3],[
         * 2,3],[3,3],[3,4],[3,5],[3,6]] ], // 10 [ [1],[[0,2],[0,4]] ], ]
         */
        private int space_between_symbols = 0;

    /**
     *   Translate 'number' to its LED frame data and load into matrix controller shifted by plus_x columns
     * @param number
     * @param digit_count
     * @param plus_x
     * @param display_dev
     * @param led_blink
     * @return  matrix column offset for next number
     */
        public int write_num(int number, int digit_count, int plus_x, Is31Fl37Matrix display_dev, int led_blink) {
            this.logger.trace("write_num");

            int[] the_list = pixels[number];
            // plus_x used to place items across matrix

            for (int i = 0; i < the_list.length; i += 2) {
                display_dev.pixel(plus_x + (the_list[i]), the_list[i + 1], 10, led_blink, 0);
                // Use digit_count and each digit is on a new frams,
                // or put them all on the same frame if last parm '0'
            }
            int next_displace = plus_x + (width[number] + space_between_symbols);
            this.logger.trace("write_num");

            return next_displace;

        }

    /**
     *   Based on input data, create the LED patterns (frame) to display that data.  Then pass that data to the
     *   matrix controller to perform the actual display
     * @param pin_monitor    to toggle LEDs
     * @param display_dev    matrix controller
     * @param display_num    temperature value
     * @param display_asc    time value
     * @param led_blink      intensity
     * @param loop_count     whether display value is shown more than once
     * @param time_mode      Whether creating frames of temperature info ot time-of-day info
     */
        public void create_led_pattern(ControlLeds pin_monitor, Is31Fl37Matrix display_dev, int display_num,
                                       char[] display_asc, int led_blink, Integer loop_count, boolean time_mode) {
            // if verbose:
            // print
            // "create_led_pattern:display in leds : time_mode %s, display_num %s
            // "%(time_mode,
            // display_num)
            this.logger.trace("create_led_pattern");
            this.logger.trace("parms :  display_num : " + String.format("0x%02X", display_num) + " display_ASC : "
                    + display_asc[0] + " blink : " + String.format("0x%02X", led_blink) + " loop_count : "
                    + String.format("0x%02X", loop_count) + " time_mode : " + time_mode);

            // # et LEDs to indicate starting matrix operation
            pin_monitor.toggle_led(true);

            char[] ascii_num; // = ''; // new char[5];
            String ascii_str;

            if (time_mode) {
                ascii_num = display_asc;
            } else {
                ascii_str = Integer.toString(display_num);
                ascii_num = ascii_str.toCharArray();
            }
            int num = 0;
            int offset = 0;
            int placement = 0; // # aded to x axis value in setting the pixels
            // # special format when displaying time
            if (time_mode) { // # we expect format HH:MM in string format
                num = Character.getNumericValue(display_asc[0]);
                placement = this.write_num(num, offset, placement, display_dev, led_blink);
                offset += 1;
                // #
                num = Character.getNumericValue(display_asc[1]);
                placement = this.write_num(num, offset, placement, display_dev, led_blink);
                offset += 1;
                // #
                num = 10; // # pixel array for base 10, has an eleventh entry for
                // the :
                display_dev.blink_write(540);
                placement = this.write_num(num, offset, placement, display_dev, 1); // #led_blink)
                // #
                // force
                // blinking
                offset += 1;
                // #
                num = Character.getNumericValue(display_asc[3]);
                placement = this.write_num(num, offset, placement, display_dev, led_blink);
                offset += 1;
                // #
                num = Character.getNumericValue(display_asc[4]);
                placement = this.write_num(num, offset, placement, display_dev, led_blink);
                offset += 1;
            }

            else {
                for (int i = 0; i < ascii_num.length; i++) {
                    num = Character.getNumericValue(ascii_num[i]);
                    placement = this.write_num(num, offset, placement, display_dev, led_blink);
                    offset += 1;
                }
            }

            int delay = 693;
            int frames = 0; // could use offset
            display_dev.autoplay(delay, loop_count, frames);// #offset
            // # wait for the autoplay to complete
            // #completed = pin_monitor.monitor_intr(verbose)
            // # call dbus interface

            // if verbose:
            // print "monitor_intr returned ", completed
            this.logger.trace("create_led_pattern");

        }


private Logger logger;
 
       
    }

    

