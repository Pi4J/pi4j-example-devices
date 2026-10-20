package com.pi4j.devices.commandhandler;

public abstract class ParameterSpecification<PT> {
    abstract PT process(String value);
}
