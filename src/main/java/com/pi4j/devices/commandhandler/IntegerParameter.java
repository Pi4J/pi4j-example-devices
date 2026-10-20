package com.pi4j.devices.commandhandler;

public class IntegerParameter extends ParameterSpecification<Integer> {
    private final int min;
    private final int max;

    public IntegerParameter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    Integer process(String text) {
        int parsed = Integer.parseInt(text);
        if (parsed < min || parsed > max) {
            throw new IllegalArgumentException("Value " + text);
        }
        return parsed;
    }

    public String toString() {
        return "[" + min + ".." + max + "]";
    }
}
