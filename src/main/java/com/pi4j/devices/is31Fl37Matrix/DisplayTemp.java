

package com.pi4j.devices.is31Fl37Matrix;

import com.pi4j.context.Context;
import com.pi4j.drivers.sensor.environment.bmx280.Bmx280Driver;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.util.Console;
import org.slf4j.Logger;

import java.text.DecimalFormat;
import java.time.LocalTime;

/**
 * Class will obtain the temperature from the BMP280 and display it through the matrix controller.
 * Then obtain the system time-of-day and display that through the matrix controller.
 */
public class DisplayTemp {

    private final Is31Fl37Matrix display;
    private final Logger logger;
    private final Context pi4j;
    private final Console console;
    private I2C i2c;
    private Bmx280Driver bmx280Driver;
    private static final int ID_VALUE_MSK_BME = 0x60;

    /* This class includes the state -  private BMP280Device bmpDev;    This is required as Pi4j_V2
     * does not permit repeated instantiations of an I2C device using the same Pi i2c Bus and address. So
     * this class creates the BMP280 device once and reuses that same instance on subsequent invocation.
     */

    public DisplayTemp(Context pi4j, Console console, Is31Fl37Matrix display, Logger logger) {
        this.display = display;
        this.logger = logger;
        this.pi4j = pi4j;
        this.console = console;
        this.bmx280Driver = null;

    }

    /**
     * Note, the bmp_bus and bmp_address supplied on the initial call to this method will continue to be used
     * on subsequent calls regardless of values supplied.
     */
    public void process_bmp_data(ControlLeds pin_monitor, int led_blink,
                                 Integer loop_count, Integer bmp_bus, Integer bmp_address, byte sensorID) {
        int display_temp_num = 42;
        int display_humidity_num = 00;
        double[] readings = new double[2];
        // bmp = read_BMP180.ReadBmp180()
        this.logger.trace("process_bmp_data");
        this.logger.trace("parms :  blink : " + String.format("0x%02X", led_blink) + " loop_count : "
            + String.format("0x%02X", loop_count) + " bmp180 address : " + String.format("0x%02X", bmp_address));

    if (sensorID == ID_VALUE_MSK_BME) {
            if (this.bmx280Driver == null) {
                I2CConfig i2cConfig = I2C.newConfigBuilder(pi4j)
                    .id("BMP280")
                    .bus(bmp_bus)
                    .device(bmp_address)
                    .build();

                // Read values 10 times
                this.i2c = pi4j.create(i2cConfig);
                bmx280Driver = new Bmx280Driver(this.i2c);
            }
            DecimalFormat df = new DecimalFormat("0.###");

            console.println("**************************************");
            console.println("Reading values,");

            Bmx280Driver.Measurement measurement = bmx280Driver.readMeasurement();

            console.println("Temperature: " + df.format(measurement.getTemperature()) + " °C");
            console.println("Temperature: " + df.format(measurement.getTemperature() * 1.8 + 32) + " °F ");

            console.println("Pressure: " + df.format(measurement.getPressure()) + " Pa");
            // 1 Pa = 0.00001 bar or 1 bar = 100,000 Pa
            console.println("Pressure: " + df.format(measurement.getPressure() / 100_000) + " bar");
            // 1 Pa = 0.0000098692316931 atmosphere (standard) and 1 atm = 101.325 kPa
            console.println("Pressure: " + df.format(measurement.getPressure() / 101_325) + " atm");

            console.println("Humidity: " + df.format(measurement.getHumidity()) + " %");

            readings[0] = Double.parseDouble(df.format(measurement.getTemperature()));
            readings[1] = Double.parseDouble(df.format(measurement.getHumidity()));

        } else {
        System.out.println("Incorrect I2C sensor installed/ or none found");
         System.exit(42);
     }
        double temp = readings[0];
        double humidity = readings[1];
        display_humidity_num = (int) humidity;


        boolean time_mode = false;
        display_temp_num = (int) ((temp * 1.8) + 32);
        // if verbose:
        // print "Display fahrenheit temp " ,display_temp_num
        char[] display_asc = new char[5];
        DisplayLED disp_worker = new DisplayLED(this.logger);
        disp_worker.create_led_pattern_temp(pin_monitor, this.display, display_temp_num, led_blink, loop_count);
        // clear the matrix
        for (int c = 0; c < 8; c++) {
            this.display.fill(0, (byte) 0, 0);
        }
        disp_worker.create_led_pattern_humidity(pin_monitor, this.display, display_humidity_num, led_blink, loop_count);
        // clear the matrix
        for (int c = 0; c < 8; c++) {
            this.display.fill(0, (byte) 0, 0);
        }


        this.logger.trace("process_bmp_data");

    }
    /**
     * Use the state from the Sensor config object and the state pi4j to create
     * a BMP280 device instance
     */
    private static I2C createI2cDevice(Context pi4j, int busNum, int address) {

        String id = String.format("0X%02x: ", busNum);
        String name = String.format("0X%02x: ", address);
        var i2cDeviceConfig = I2C.newConfigBuilder(pi4j)
            .bus(busNum)
            .device(address)
            .id(id + " " + name)
            .name(name)
            .build();
        return  pi4j.create(i2cDeviceConfig);
    }
    public void show_time(ControlLeds pin_monitor, int led_blink,
                          Integer loop_count) {
        int display_num = 42; // not used this method
        boolean time_mode = true;
        // # obtain a viewable version of the time and display the time
        LocalTime thisSec;
        this.logger.trace("show_time");
        this.logger.trace("parms :   blink : " + String.format("0x%02X", led_blink) + " loop_count : "
            + String.format("0x%02X", loop_count));

        thisSec = LocalTime.now();
        String string_asc = String.format("%1$02d:%2$02d", thisSec.getHour(), thisSec.getMinute());
        char[] display_asc = string_asc.toCharArray();
        DisplayLED disp_worker = new DisplayLED(this.logger);
        disp_worker.create_led_pattern_time(pin_monitor, this.display, display_asc, led_blink, loop_count);
        // clear the matrix
        for (int c = 0; c < 8; c++) {
            this.display.fill(0, (byte) 0, 0);
        }


        this.logger.trace("show_time");

    }

}
