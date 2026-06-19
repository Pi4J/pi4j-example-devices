package com.pi4j.examples.ioexpander.output;

import com.pi4j.io.OnOffWrite;

public class TrafficLight {
    // OnOffWrite is a simple interface implemented by DigitalOutput that abstracts from some physical aspects
    private final OnOffWrite red;
    private final OnOffWrite yellow;
    private final OnOffWrite green;

    public TrafficLight(OnOffWrite red, OnOffWrite yellow, OnOffWrite green) {
        this.red = red;
        this.yellow = yellow;
        this.green = green;
    }

    public void setState(State state) {
        red.setState(state == State.RED || state == State.RED_YELLOW);
        yellow.setState(state == State.YELLOW || state == State.RED_YELLOW);
        green.setState(state == State.GREEN);
    }

    enum State {
        RED, RED_YELLOW, GREEN, YELLOW, ERROR;
    }
}