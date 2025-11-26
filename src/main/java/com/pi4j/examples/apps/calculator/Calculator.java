package com.pi4j.examples.apps.calculator;

import com.pi4j.drivers.display.character.CharacterDisplay;
import com.pi4j.drivers.input.KeyPad;

public class Calculator {
    private final CharacterDisplay display;
    private final KeyPad keyPad;

    private char lastKey = '\0';
    private char pendingOp = ' ';
    private long keyStart = 0;
    private double accumulator = 0;
    private long input = 0;
    private int divisor = 0;

    public Calculator(CharacterDisplay display, KeyPad keyPad) {
        this.display = display;
        this.keyPad = keyPad;
    }

    public void run() {
        display.clear();
        display.writeAt(0, 0, "Exit: Hold '='");
        renderInput();

        while (true) {
            char key = keyPad.getKey();
            if (key == '\0') {
                boolean longPress = System.currentTimeMillis() - keyStart > 1000;
                if (longPress && lastKey == '=') {
                    break;
                }
                if (lastKey >= '0' && lastKey <= '9') {
                    input = input * 10 + (lastKey - '0');
                    divisor = divisor * 10;
                    renderInput();
                } else if (lastKey == '#') {
                    divisor = 1;
                    renderInput();
                } else if ("+-*=/".indexOf(lastKey) != -1) {
                    commit(lastKey, longPress);
                }
            } else if (lastKey != key) {
                keyStart = System.currentTimeMillis();
            }
            lastKey = key;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }
    }

    private void commit(char nextOp, boolean longPress) {
        if (pendingOp != ' ' || nextOp == '=' || input != 0) {
            double value = input / Math.max(1.0, divisor);
            switch (pendingOp) {
                case '+' -> accumulator += value;
                case '-' -> accumulator -= value;
                case '/' -> accumulator /= value;
                case '*' -> accumulator *= value;
                default -> accumulator = value;
            }
            divisor = 0;
            input = 0;
        }
        display.clear();
        String s = String.valueOf(accumulator);
        display.writeAt(
            0,
            display.getHeight() - 2,
            " ".repeat(Math.max(0, display.getWidth() - s.length())) + s);
        pendingOp = nextOp == '=' ? ' ' : nextOp;
        renderInput();
    }

    private void renderInput() {
        String s = String.valueOf(input);
        if (divisor != 0) {
            int digits = String.valueOf(divisor).length();
            if (digits > s.length()) {
                s = "0".repeat(digits - s.length()) + s;
            }
            int cut = s.length() - digits + 1;
            s = s.substring(0, cut) + "." + s.substring(cut);
        }
        display.writeAt(
            0,
            display.getHeight() - 1,
            pendingOp + " ".repeat(Math.max(0, display.getWidth() - s.length() - 1)) + s);
    }
}
