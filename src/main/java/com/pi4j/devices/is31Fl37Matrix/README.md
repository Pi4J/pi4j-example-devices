
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


Python3 Program knows the expected is31Fl37Matrix interrupt flow, measure duration of successfully processed interrupts
python3 ./monitorGpio.py -p 25