# Using IOExpanders in Pi4j with Pi4j drivers

IOExpanders are typically I2C-based chips that provide additional GPI ports. Typical applications are:

- Providing a lot of GPIO ports as a distance -- using only the 4 I2C wires (and potentially) an
  interrupt wire. Here, it also helps that I2C is a bus, so if controlled devices are on a 
  single "path", only the 4 wires for the bus are required, not a separate connection to each of them.

- Driving devices that use parallel communication -- such as many displays, for instance the 
  ubiquitous 16x2 LCD displays.

In Pi4J drivers, the common functionality of IOExpanders is modelled in the `InputExpander` interface 
for input pins and the `OutputExpander` interface for output pins; IOExpanders with configurable
pins should implement the ConfigurableIoExpander interface, which combines these and provides
a method to configure the desired direction.

These interfaces expose the pins via the `OnOffWrite` and `ListenableOnOffRead` interfaces, which
basically represent pure logical state of `DigitalOutput` and `DigitalInput` -- and are 
implemented by these.

The reason for using the base interface is that `DigitalInput` and `DigitalOutput` provide some
details (such as debounce settings) that are commonly not available for expanders. In general,
it also seems like a safer design not to expose such settings to components that shouldn't
mess with them.

So if you want to make sure your application works seamlessly with IOExpanders, please make
sure to always use these interfaces instead of `DigitalOutput` or `DigitalInput` directly.

For more details about using IOExpanders with Pi4J, please refer to our output and input
examples:

- [Traffic Lights](output/): Using IO Expanders for more output options 
- [Joystick Example](input/): Using IO Expanders for more input options







