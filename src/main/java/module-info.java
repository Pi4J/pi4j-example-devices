module com.pi4j.devices{

    // Pi4J MODULES
    requires com.pi4j;
  //  requires com.pi4j.plugin.pigpio;
  //  requires com.pi4j.library.pigpio;

    // SLF4J MODULES   LOG4J
    requires org.slf4j;
    requires org.slf4j.simple;

   // requires org.apache.logging.log4j;
   // requires org.apache.logging.log4j.core;


    requires java.logging;
    requires jdk.unsupported;
    requires com.pi4j.plugin.linuxfs;
    requires java.desktop;
    requires com.pi4j.plugin.gpiod;
   // requires com.pi4j.library.kernel;
    //requires com.pi4j.plugin.rp1spi;

    uses com.pi4j.extension.Extension;
    uses com.pi4j.provider.Provider;

    // allow access to these classes
    exports com.pi4j.devices.tca9548;
    exports com.pi4j.devices.mcp23008;
    exports com.pi4j.devices.mcp23017;
    exports com.pi4j.devices.mcp23xxxApplication;
    exports com.pi4j.devices.appConfig;
    exports com.pi4j.devices.base_i2c;
    // new TOF chip
    exports com.pi4j.devices.vl53L0X;

    exports com.pi4j.devices.bmp280;
    exports com.pi4j.devices.bme280;
    exports com.pi4j.devices.is31Fl37Matrix;
    exports com.pi4j.devices.mcp3008;

    exports com.pi4j.devices.sn74hc595;
    exports com.pi4j.devices.mcp4725;
    exports com.pi4j.devices.hd44780u_lcd1602a;
    exports com.pi4j.devices.pca9685;
    exports com.pi4j.devices.pcf8574a_lcd1602a;
    exports com.pi4j.devices.mcp23017_lcd1602a;
    exports com.pi4j.devices.dht22;
    exports com.pi4j.devices.hcsr04;
    exports com.pi4j.devices.ads1256;
    exports com.pi4j.devices.basic_ads1256;
    exports com.pi4j.devices.dac8552;
    exports com.pi4j.devices.mpl3115a2;
    exports com.pi4j.devices.at24c512;
    exports com.pi4j.devices.cp2102n;
    exports com.pi4j.devices.neopixel94v;

    }
