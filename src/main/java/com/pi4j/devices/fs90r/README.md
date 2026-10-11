

##Device application to operate a Fs90r stepping servo.  This servo can rotate 360 degree continuously. 
The PWM signal sent to the servo device set the direction and RPM of rotation.



 Driver for a Fs90r servo motor.
 The Fs90r defines a specific PWM interface and exactly how the PWN signal
 controls the servo

 https://www.pololu.com/product/2820


   These devices do not have a customary DataSheet.  Most servo devices provide limited specifications
 for required pulse width, they just say 1 - 2 millisecond.

 The FS90 R 360 defines a specific PWM interface and exactly how the PWN signal
 controls the servo. There are many different manufacturers of the FS90 servo.
 The pulse width required for setting the servo at 0 degrees MAX Clockwise (CW) or 180 degrees
 MAX CounterClockWise (CCW) varies by manufacturer. The driver by default uses the pulse width timing as
 (low)1000 - (high)2000 microseconds. The user of this example application can change these defaults
 when first invoking the program.

 If the servo does not rotate to the expected RPM,Example to rotate faster CW  reduce the -low
  argument, to rotate CCW faster increase the -high argument. The -low -high change should be balanced or 90 degree will not properly align.
 Meaning if you reduce the -low by 200, you should increase the -high by 200, or visa-versa.




 https://www.pololu.com/product/2820



Fs90r               Pi
Grey/Brown          Ground
Red                 5V
Yellow              GPIO18  config.sys contains dtoverlay=pwm-2chan



1. ./mvnw clean package
2. cd target/distribution
3. ./runFs90r.sh   args

-h      help
-c      channel   (default 2)   set the channel, depending on config.txt 1 2 3 4  
-cw     clockwise S M F
-ccw    counter-clockwise S M F
-low    double pulse width for 0 degree microseconds
-high   double pulse width for 180 degree microseconds
-q      quit      servo will stop


Application running, awaiting input of 0-180 degree
./runFs90r.sh

Application running  CCW, then awaiting input of 0-180 degree
./runFs90r.sh 160



Set output shaft Fast ClockWise, then enter 90 for stop
./runFs90r.sh   -cw F     
         90


Set output shaft Slow CounterClockWise, then enter 0 for MAX soeed
./runFs90r.sh   -ccw S     
      0

My Fs90r appears faster rotation wih these values for the -low and -high limmits
./runFs90r.sh  -low 550 -high 2450
