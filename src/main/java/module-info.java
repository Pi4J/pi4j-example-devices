module com.pi4j.devices.multi{

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

    // allow access to classes in the following namespaces for Pi4J annotation processing
    opens com.pi4j.devices.tca9548 to com.pi4j;
    opens com.pi4j.devices.mcp23008 to com.pi4j;
    opens com.pi4j.devices.mcp23017 to com.pi4j;
    opens com.pi4j.devices.mcp23xxxApplication to com.pi4j;
    opens com.pi4j.devices.appConfig to com.pi4j;
    opens com.pi4j.devices.base_i2c to com.pi4j;
    // new TOF chip
    opens com.pi4j.devices.vl53L0X to com.pi4j;
}