



#!/usr/bin/python3

#* This is coded specifically to the manner in which the IS31FL3731 chip
#* toggles the INTB pin.  Used in debugging the Pi OS missing short
#* duration level changes on a GPIO
#*        It Is NOT a generic monitor program.
#*
#* Sole purpose is unit testing.  Pi4 with Pi OS 64bit
#* missing short duration interrupt. This program allows
#* measuring the interrupt duration.

import gpiozero

import sys, getopt

import time
#from datetime import datetime, date, time


def high():
    print("Pin High")

def low():
    print("Pin Low")

def main(argv):
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
         pin = arg


    print('Monitor pin "', pin)

    btn = gpiozero.Button(pin, True)

    val = btn.value
    oldVal = 0
    print("Initially value = ", val)
    while True:
        btn.wait_for_press()
        val = btn.value
        print("Value = ", val)
        if val != oldVal:
            oldVal = val
            if val == 1:
                print("State became  = ", val)
                start_time = int(round(time.time() * 1000))
            else:
                print("State became  = ", val)
                end_time = int(round(time.time() * 1000))
                break
    print("out of while loop")
    time_diff = (end_time - start_time)
    print(" time diff ", time_diff)



if __name__ == "__main__":
    main(sys.argv[1:])