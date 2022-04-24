
Implementation of the  Is31fl3731 matrix controller.  
Uses a BMP280 and system time to alternately display the temperature and time.

The program has the tables to translate numbers to matrix commands, it does not
translate alphabetic characters.



   1. mvn clean package
   2. cd target/distribution
   3. Execute command to perform desired Matrix operation


sudo ./runIs31fl37Matrix.sh -b 0x01 -a 0x74 -bmpB 0x01 -bmpA 0x76 -g 24 -w 20 -i 128 -c 1 -l 1 -r 16 -z 12 -t info


sudo java --module-path . --module  com.pi4j.devices.multi/com.pi4j.devices.is31fl37Matrix.Is31fl37_matrix_app  -b 0x1 -a 0x74 <matrix> " +
" -bmpB 0x1 <BMP280 bus> -bmpA 0x76 BMP280 address  -g  GPIO processing LED      -w GPIO warning LED  " +
"-i intensity<128>  -c <repeat_count,0 infinite>  -l <displays>   -s log  -r resetGpio -z monitorGpio#  " +
"-t values : \"trace\", \"debug\", \"info\", \"warn\", \"error\" or \"off\"  Default \"info\"


GPIO pin

x      -g     ----------->  processing indicator LED

x      -w    ------------>  warning indicator LED


x      -r    ------------------------------------------->   Is31fl3731   SD
                                                                         --

x      -z    <-------------------------------------------   Is31fl3731   INTB

x        I2C  -b bus  -a address  -----------------------   Is31fl3731


x        I2C  -bmpB bus  -bmpA address  ------------------  BMP280



Debug utilities

Java Monitor input GPIO pin 18 configured as an active low interrupt.  
com.pi4j.devices.multi/com.pi4j.devices.is31Fl37Matrix.MonitorInterrupt -p 18 -d DOWN 


Java Create output GPIO pin 18.  Create interrupt by driving line low for 15 millisecond duration 
com.pi4j.devices.multi/com.pi4j.devices.is31Fl37Matrix.CreateInterrupt -p 18 -d LOW -m 15

Python3 to create interrupt of specific interval
python3 createInterrupt.py -p 18 -m 4 -d LOW

Python3 Program knows the expected is31Fl37Matrix interrupt flow, measure duration of successfully processed interrupts
python3 ./monitorGpio.py -p 25

python3 monitorGpio2.py -p 25

Pi-OS 64bit
Monitor programs implemented in python3 process interrupts of 1 and 2 milliseconds.

Monitor program implemented in java process interrupts of at least 10 milliseconds.

The python3 program monitorGpio2.py uses the GPIO.add_event_detect function callback to more closely 
operate same as the java monitor program's  event callback

In either python3 create interrupt  implementation the required interrupt duration is ten times greater in for 
the java monitor program than the python3 monitor program


sudo /usr/lib/jvm/java-1.11.0-openjdk-armhf/bin/java -agentlib:jdwp=transport=dt_socket,address=5009,suspend=y,server=y -javaagent:/home/pi/Tools/Intellij/idea-IC-203.8084.24/plugins/java/lib/rt/debugger-agent.jar -Dfile.encoding=UTF-8 -classpath /home/pi/Tools/Intellij/idea-IC-203.8084.24/lib/idea_rt.jar -p /home/pi/Pi4J_V2/Pi4J_V2_Devices/target/classes:/home/pi/.m2/repository/org/slf4j/slf4j-simple/2.0.0-alpha0/slf4j-simple-2.0.0-alpha0.jar:/home/pi/.m2/repository/org/slf4j/slf4j-api/2.0.0-alpha0/slf4j-api-2.0.0-alpha0.jar:/home/pi/.m2/repository/com/pi4j/pi4j-core/2.2.0-SNAPSHOT/pi4j-core-2.2.0-20220207.070217-12.jar:/home/pi/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar:/home/pi/.m2/repository/org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar:/home/pi/.m2/repository/org/apache/logging/log4j/log4j-slf4j-impl/2.17.1/log4j-slf4j-impl-2.17.1.jar:/home/pi/.m2/repository/com/pi4j/pi4j-plugin-raspberrypi/2.2.0-SNAPSHOT/pi4j-plugin-raspberrypi-2.2.0-20220207.070318-12.jar:/home/pi/.m2/repository/com/pi4j/pi4j-plugin-pigpio/2.2.0-SNAPSHOT/pi4j-plugin-pigpio-2.2.0-20220207.070311-12.jar:/home/pi/.m2/repository/com/pi4j/pi4j-library-pigpio/2.2.0-SNAPSHOT/pi4j-library-pigpio-2.2.0-20220207.070247-12.jar:/home/pi/.m2/repository/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar:/home/pi/.m2/repository/com/pi4j/pi4j-plugin-linuxfs/2.2.0-SNAPSHOT/pi4j-plugin-linuxfs-2.2.0-20220207.070324-12.jar:/home/pi/.m2/repository/com/jcraft/jsch/0.1.55/jsch-0.1.55.jar:/home/pi/.m2/repository/com/pi4j/pi4j-library-linuxfs/2.2.0-SNAPSHOT/pi4j-library-linuxfs-2.2.0-20220207.070255-12.jar -m com.pi4j.devices.multi/com.pi4j.devices.is31Fl37Matrix.MonitorInterrupt -p 18 -d DOWN

sudo /usr/lib/jvm/java-1.11.0-openjdk-armhf/bin/java -javaagent:/home/pi/Tools/Intellij/idea-IC-203.8084.24/lib/idea_rt.jar=39721:/home/pi/Tools/Intellij/idea-IC-203.8084.24/bin -Dfile.encoding=UTF-8 -p /home/pi/Pi4J_V2/Pi4J_V2_Devices/target/classes:/home/pi/.m2/repository/org/slf4j/slf4j-simple/2.0.0-alpha0/slf4j-simple-2.0.0-alpha0.jar:/home/pi/.m2/repository/org/slf4j/slf4j-api/2.0.0-alpha0/slf4j-api-2.0.0-alpha0.jar:/home/pi/.m2/repository/com/pi4j/pi4j-core/2.2.0-SNAPSHOT/pi4j-core-2.2.0-20220207.070217-12.jar:/home/pi/.m2/repository/org/apache/logging/log4j/log4j-api/2.17.1/log4j-api-2.17.1.jar:/home/pi/.m2/repository/org/apache/logging/log4j/log4j-core/2.17.1/log4j-core-2.17.1.jar:/home/pi/.m2/repository/org/apache/logging/log4j/log4j-slf4j-impl/2.17.1/log4j-slf4j-impl-2.17.1.jar:/home/pi/.m2/repository/com/pi4j/pi4j-plugin-raspberrypi/2.2.0-SNAPSHOT/pi4j-plugin-raspberrypi-2.2.0-20220207.070318-12.jar:/home/pi/.m2/repository/com/pi4j/pi4j-plugin-pigpio/2.2.0-SNAPSHOT/pi4j-plugin-pigpio-2.2.0-20220207.070311-12.jar:/home/pi/.m2/repository/com/pi4j/pi4j-library-pigpio/2.2.0-SNAPSHOT/pi4j-library-pigpio-2.2.0-20220207.070247-12.jar:/home/pi/.m2/repository/org/jetbrains/annotations/23.0.0/annotations-23.0.0.jar:/home/pi/.m2/repository/com/pi4j/pi4j-plugin-linuxfs/2.2.0-SNAPSHOT/pi4j-plugin-linuxfs-2.2.0-20220207.070324-12.jar:/home/pi/.m2/repository/com/jcraft/jsch/0.1.55/jsch-0.1.55.jar:/home/pi/.m2/repository/com/pi4j/pi4j-library-linuxfs/2.2.0-SNAPSHOT/pi4j-library-linuxfs-2.2.0-20220207.070255-12.jar -m com.pi4j.devices.multi/com.pi4j.devices.is31Fl37Matrix.MonitorInterrupt -p 18 -d DOWN
