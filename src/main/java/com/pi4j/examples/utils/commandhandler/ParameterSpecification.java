package com.pi4j.examples.utils.commandhandler;

public abstract class ParameterSpecification<PT> {
    abstract PT process(String value);
}
