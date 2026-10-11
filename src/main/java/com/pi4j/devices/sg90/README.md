#  SG90  servo motor

https://www.scribd.com/document/420327552/Servo
https://www.friendlywire.com/projects/ne555-servo-safe/SG90-datasheet.pdf

These devices do not have a customary DataSheet.  Most servo devices provide limited specifications 
for required pulse width, they just say 1 - 2 millisecond.

The SG90 defines a specific PWM interface and exactly how the PWN signal
controls the servo. There are many different manufacturers of the SG90 servo. 
The pulse width required for setting the servo at 0 degrees or 180 degrees varies by 
manufacturer. The driver by default uses the pulse width timing as 
(low)1000 - (high)2000 microseconds. The user of this example application can change these defaults when first 
invoking the program.

If the servo does not rotate to the desired position, Example: to rotate closer to the 0 degree 
point reduce the -low argument, to rotate closer to the 180 degree point increase the
-high argument. The -low -high change should be balanced or 90 degree will not properly align.
Meaning if you reduce the -low by 200, you should increase the -high by 200, or visa-versa. 



 Connections between the SG90 servo and the Raspberry Pi
  SG90              Pi
Grey/Brown          Ground
Red                 5V
Yellow              GPIO18  config.sys contains dtoverlay=pwm-2chan



1. ./mvnw clean package
2. cd target/distribution
3. ./runSg90.sh   args

-c      channel   (default 2)   set the channel, depending on config.txt 1 2 3 4  
-low    double pulse width for 0 degree microseconds
-high   double pulse width for 180 degree microseconds
-q      quit
-h      help

./runSg90.sh begins execution of the application, you then enter the desired servo degree of rotation.
Then continue entering the requested degree value, or any key will exist the program.

Set ouput shaft at 120 degree
./runSg90.sh   
         120     

Driver defaults to 0 degree requires a 1000 micro second pulse, 180 degree requires a 2000 microsecond pulse
If your servo requires different pulse widths

Set low high pulse widths, then set output shaft at 120 degree
./runSg90.sh  -low 600 -high 2400 
     120

My Sg90 aligns correctly with these values
./runSg90.sh  -low 550 -high 2450

If your configuration uses a channel other than 2
./runSg90.sh -c 1 
    120     
