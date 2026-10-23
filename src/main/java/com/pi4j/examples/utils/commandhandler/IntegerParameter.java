package com.pi4j.examples.utils.commandhandler;

public class IntegerParameter extends ParameterSpecification<Integer> {
    private final int min;
    private final int max;

    public IntegerParameter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    Integer process(String text) {
        int parsed = Integer.decode(text);
        if (parsed < min || parsed > max) {
            throw new IllegalArgumentException("Value " + text);
        }
        return parsed;
    }

    public String toString() {
        return "[" + min + ".." + max + "] / [0x" + Integer.toHexString(min) + "..0x" + Integer.toHexString(max) + "]";
    }
}
