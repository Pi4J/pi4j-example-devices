package com.pi4j.examples.hat.raspberry.sensehat;

import com.pi4j.drivers.display.BitmapFont;
import com.pi4j.drivers.display.graphics.GraphicsDisplay;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility for scrolling a line of text in a small display.
 */
public class MatrixScrollDisplay {

    private final GraphicsDisplay display;
    private final Timer timer = new Timer();
    private final Object lock = new Object();
    private String text;
    private int pos = 0;
    private boolean active = false;

    public MatrixScrollDisplay(GraphicsDisplay graphicsDisplay) {
        this.display = graphicsDisplay;
    }

    void clear() {
        synchronized (lock) {
            setText("");
            pos = 0;
        }
    }

    void setText(String text) {
        synchronized (lock) {
            this.text = text;
            if (!active) {
                advance();
            }
        }
    }

    void advance() {
        synchronized (lock) {
            display.fillRect(0, 0, display.getWidth(), display.getHeight(), 0);
            int width = display.renderText(pos, 8, text, BitmapFont.get5x8Font(BitmapFont.Option.PROPORTIONAL), 0xffffffff);
            display.flush();
            if (--pos < -width - 8) {
                pos = 8;
            }
            active = !text.isEmpty();
            if (active) {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        advance();
                    }
                }, 80);
            }
        }
    }
}
