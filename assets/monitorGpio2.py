
#    * #%L
#    * Copyright (C) 2012 - 2025 Pi4J
#   * %%
#  *
#  * Licensed under the Apache License, Version 2.0 (the "License");
#  * you may not use this file except in compliance with the License.
#  * You may obtain a copy of the License at
#  *
#  *      http://www.apache.org/licenses/LICENSE-2.0
#  *
#  * Unless required by applicable law or agreed to in writing, software
#  * distributed under the License is distributed on an "AS IS" BASIS,
#  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  * See the License for the specific language governing permissions and
#  * limitations under the License.
#  *
#  *-
#  * #%L
#  * **********************************************************************
#  * ORGANIZATION  :  Pi4J
#  * PROJECT       :  Pi4J :: EXTENSION
#  * FILENAME      :  monitorGpio2.py
#  *
#  * This file is part of the Pi4J project. More information about
#  * this project can be found here:  https://pi4j.com/
#  * **********************************************************************
#  * %%
#
#

#!/usr/bin/python3

#* This is coded specifically to the manner in which the IS31FL3731 chip
#* toggles the INTB pin.  Used in debugging the Pi OS missing short
#* duration level changes on a GPIO
#*        It Is NOT a generic monitor program.
#*
#* Sole purpose is unit testing.  Pi4 with Pi OS 64bit
#* missing short duration interrupt. This program allows
#* measuring the interrupt duration.


import RPi.GPIO as GPIO
import sys, getopt

import time
#from datetime import datetime, date, time




def changed(pin):
    print("Pin changed at Time : ",  float(round(time.time() * 1000)))

def main(argv):
    GPIO.setmode(GPIO.BCM)
    btn = 42
    obs_data = {'RA': "22:24:05.52" }
    pred_data = {'RA':"22:24:05.60"}

    print("!!!")
    try:
        opts, args = getopt.getopt(argv,"hp:o:",["pin=","ofile="])
    except getopt.GetoptError:
        print ('test.py -i <inputfile> -o <outputfile>')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print ('monitorGpio.py -p <pin>')
            sys.exit()
        elif opt in ("-p", "--pin"):
            pin = int(arg)


    print('Monitor pin "', pin)

    btn = GPIO.setup(pin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

    val = GPIO.input(pin)
    oldVal = 0
    start_time = 0
    end_time = 0
    print("Initially value = ", val)
    GPIO.add_event_detect(pin, GPIO.BOTH, callback=changed)

    milliSecDelay = float(10/1000)
    while(True):
        time.sleep(milliSecDelay)

    while False: #True:
        GPIO.wait_for_edge(pin, GPIO.BOTH)
        val = GPIO.input(pin)
        print("Value = ", val)
        if val != oldVal:
            oldVal = val
            if val == 1:
                print("<1>State became  = ", val)
                start_time = float(round(time.time() * 1000))
            else:
                print("<0>State became  = ", val)
                end_time = float(round(time.time() * 1000))
                break
    print("out of while loop")
    time_diff = (end_time - start_time)
    print(" time diff ", time_diff)



if __name__ == "__main__":
    main(sys.argv[1:])