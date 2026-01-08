

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
#  * FILENAME      :  createInterrupt.py
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
#* Sole purpose is unit testing. Drive Pi4 GPIO
#*  high/low or low/high to simulate an interrupt line.
#* The duration before the GPIO toggle is configurable


# pin 20 m 10 MS interrupt d direction interrupt condition is High
# thonny IDE
#%Run createInterrupt.py -p 20 -m 10 -d HIGH 
#%FastDebug createInterrupt.py -p 20 -m 10 -d HIGH
# drive pin 18 low interrupt duration 10 MS
#python3 createInterrupt.py -p 18 -m 10 -d LOW
#




import gpiozero

import sys, getopt

import time
#from datetime import datetime, date, time




def main(argv):

    initialState = False
    interruptLevelLow = False
    milliSecDelay = 1000
    pin = 42
    typeActive = True
    try:
        opts, args = getopt.getopt(argv,"hp:o:m:d:",["pin=","ofile="])
    except getopt.GetoptError:
        print ('python3 createInterrupt.py -p 20 -m 10 -d HIGH')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '-h':
            print ('monitorGpio.py -p <pin> -m <delay>   -d <interrupt active HIGH/LOW')
            sys.exit()
        elif opt in ("-p", "--pin"):
            pin = int(arg)
        elif opt == '-m' :
            delay = int(arg)
            milliSecDelay = float(delay/1000)
            print("MilliSecond interrupt duration",  milliSecDelay*1000)
        elif opt == '-d':
            firstDirection = arg
            if (firstDirection=="HIGH") or (firstDirection=="LOW"):
                if(firstDirection=='LOW'):
                    initialState = True
                    interruptLevelLow = True
                    typeActive = False
                else:
                    initialState = False
                    interruptLevelLow = False
                    typeActive = True
            else:
                print("Error: invalid parm -d")
                quit()



    print('Drive  pin "', pin)
    if(interruptLevelLow):
        output = gpiozero.DigitalOutputDevice(pin, True, True) #typeActive, True)
        print("Interrupt LOW wait MS", + milliSecDelay*1000 )
        output.off()
        time.sleep(milliSecDelay)
        output.on()
        print("state restored")
    else:
        output = gpiozero.DigitalOutputDevice(pin, typeActive, False)
        print("Interrupt HIGH wait MS ",  milliSecDelay*1000 )
        output.on()
        time.sleep(milliSecDelay)
        output.off()
        print("state restored")
    output.close()



if __name__ == "__main__":
    main(sys.argv[1:])