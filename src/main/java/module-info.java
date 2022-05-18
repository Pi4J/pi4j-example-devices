module com.pi4j.devices{

    // Pi4J MODULES
    requires com.pi4j;
    requires com.pi4j.plugin.pigpio;

    // SLF4J MODULES   LOG4J
    requires org.slf4j;
    requires org.slf4j.simple;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;


    requires java.logging;
    requires jdk.unsupported;
    requires com.pi4j.plugin.linuxfs;


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

    exports com.pi4j.devices.bmp280 ;
    exports com.pi4j.devices.is31Fl37Matrix;
    exports com.pi4j.devices.mcp3008;

    exports com.pi4j.devices.sn74hc595;
    exports com.pi4j.devices.mcp4725;

}