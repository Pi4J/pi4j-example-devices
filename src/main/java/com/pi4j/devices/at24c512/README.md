
# NOT READY FOR USE.  Pi4J limitation prevents this completion....



# Pi4J_V2-AT24C512 SEEPROM


https://www.alldatasheet.com/datasheet-pdf/pdf/56068/ATMEL/AT24C512.html

The example program has the assumption the BMP280 is connected to Pi bus 1, and the device is factory configured to
operate as device address 0x50. If these assumptions are not possible, the bus and device address value can be passed as
parms. Or modify the AT24C512_App program to use your specific bus and device address.

The program uses org.slf4j.simple Class SimpleLogger as suggested by the Pi4j_V2 documentation. Consult that logger
class documentation to understand its various logging options.

1. mvn clean package
2. cd target/distribution
3. sudo ./runAT24C512.sh

Use of different bus or device address, use the applicable values in the following command.
sudo ./runAT24C512.sh -b 0x01 -a 0x50   -w 0x00  -d 0xDEADBEEF


Will create the runAT24C512 device to create a runAT24C512 instance Call the read and
write methods

sudo ./runAT24C512.sh -b 0x01 -a 0x50  -n 4  -w 0x00  -d 0xDEADBEEF


sudo ./runAT24C512.sh -b 0x01 -a 0x50   -r 0x00  -n 0x16
 
parms: -b hex value bus -a hex value address -t trace -n numbytes -r readReg   -w writeReg -d data 
-r not permitted with args -d -w, either read data or write data
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "info"

