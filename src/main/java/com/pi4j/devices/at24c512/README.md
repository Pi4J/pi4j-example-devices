
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

Specific READ
Read 2 bytes starting page/address 0x342
sudo ./runAT24C512.sh -b 0x01  -b 0x01 -a 0x50 -n 0x2 -r 0x342 -t trace


Specific WRITE
Write 2 byte '0x1234 at page/address 0x324
sudo ./runAT24C512.sh -b 0x01  -b 0x01 -a 0x50 -n 0x2 -w 0x0342 -d 0x1234 -t trace



Specific READ
Read 1 byte starting page/address 0x342
sudo ./runAT24C512.sh -b 0x01  -b 0x01 -a 0x50 -n 0x1 -r 0x342 -t trace


Specific from current page/address
Read 1 byte starting page/address 0x343
sudo ./runAT24C512.sh -b 0x01  -b 0x01 -a 0x50 -n 0x1 -rr  -t trace



 
parms: -b hex value bus -a hex value address -t trace -n numbytes -r readReg   -w writeReg -d data  -rr read current
-r not permitted with args -d -w, either read data or write data
trace values : "trace", "debug", "info", "warn", "error" or "off"  Default "info"

sudo ./runAT24C512.sh -b 0x01  -b 0x01 -a 0x50 -n 0x11 -w 0x112 -d 0x1122334455667788990011223344556677  -t trace