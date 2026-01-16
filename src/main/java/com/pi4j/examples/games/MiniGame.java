package com.pi4j.examples.games;

import com.pi4j.drivers.display.BitmapFont;
import com.pi4j.drivers.display.graphics.Graphics;
import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.drivers.sound.SoundDriver;
import com.pi4j.util.DeferredDelay;

import java.util.concurrent.locks.LockSupport;

/** Abstract base class for games tailored towards small displays. */
public abstract class MiniGame {
    // Drivers and constructor params

    protected final GraphicsDisplay display;
    protected final GameController controller;
    protected final SoundDriver soundDriver;
    protected final String gameName;
    protected final Graphics graphics;
    protected final DeferredDelay delay = new DeferredDelay();
    protected int backgroundColor;
    protected int borderColor;

    // Sizing and scaling

    protected int cellSize;
    protected int scale;
    protected int scaledCellSize;
    protected int size;
    protected int x0;
    protected int y0;

    // Internals

    private GameController.Direction previousDirection = GameController.Direction.NONE;
    private int textX;
    private int textY;
    private String scrollText;
    private int scrollPos;
    private boolean soundEnabled;
    private boolean running = false;
    private boolean exit = false;
    private int score = 0;
    private int highScore = 0;

    /**
     * Adds the "delta" value to a value and returns the sum. If the distance to the next integer is less than
     * delta / 2, the returned value "snaps" to this integer.
     */
    public static float addAndSnap(float value, float delta) {
        float newValue = value + delta;
        float rounded = Math.round(newValue);
        return (Math.abs(newValue - rounded) <= Math.abs(delta)/2) ? rounded : newValue;
    }

    protected MiniGame(GraphicsDisplay display,
             GameController controller,
             SoundDriver soundDriver,
             String name,
             int backgroundColor,
             int borderColor) {
        this.display = display;
        this.controller = controller;
        this.soundDriver = soundDriver;
        this.gameName = name;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.soundEnabled = soundDriver != null;

        graphics = display.getGraphics();

        int minDim = Math.min(display.getWidth(), display.getHeight());
        int rawCellSize = minDim / 8;

        if (rawCellSize < 8) {
            cellSize = 1;
            scale = cellSize;
        } else {
            int remainder10 = rawCellSize % 10;
            int remainder8 = rawCellSize % 8;

            if (remainder10 <= remainder8) {
                cellSize = 10;
            } else {
                cellSize = 8;
            }
            scale = rawCellSize / cellSize;
        }
        scaledCellSize = scale * cellSize;
        size = 8 * scaledCellSize;

        x0 = (display.getWidth() - size) / 2;
        y0 = (display.getHeight() - size) / 2;

        System.out.println("cellSize: " + cellSize + " scale: " + scale + " x0: " + x0 + " y0: " + y0);

        renderTitleScreen();
    }

    public void addPoints(int points) {
        score += points;
    }

    protected void clearScreen() {
        graphics.setColor(borderColor);
        graphics.fillRect(0, 0, display.getWidth(), display.getHeight());
        graphics.setColor(backgroundColor);
        graphics.fillRect(x0, y0, size, size);
    }

    protected void clearScreen(int ms) {
        graphics.setColor(backgroundColor);
       
        for (int i = 0; i < size; i++) {
            graphics.drawRect(i + x0, i + y0, size - 2 * i, size - 2 * i);
            sleep(Math.max(1, ms / size));
        }
    }

    protected void println(String text) {
        if (cellSize >= 8) {
            graphics.renderText(textX, textY, text);
            textX = x0 + scaledCellSize / 2;
            textY += scaledCellSize;
        }
        scrollText +=  " -- " + text;
    }

    protected void renderTitleScreen() {
        clearScreen();
        scrollText = "";
        if (score > highScore) {
            highScore = score;
        }

        if (cellSize >= 8) {
            graphics.setTextScale(scale, scale);
            graphics.setColor(0xffffffff);
            graphics.setFont(cellSize == 10 ? BitmapFont.get5x10Font() : BitmapFont.get5x8Font());

            textX = (display.getWidth() - gameName.length() * 6 * scale) / 2;
            textY = scaledCellSize * 5 / 4;
        } else {
            scrollText = "";
        }
        println(gameName);

        textY += scaledCellSize / 4;

        println("Hi: " + highScore);
        println("Sc: " + score);
        println("> Start");
        println("< Exit");
        if (soundDriver != null) {
            println("^ Sound: " + (soundEnabled ? "On" : "Off"));
        }
        scrollText += scrollText;
    }

    protected void titleScreenStep() {
        GameController.Direction direction = controller.getDirection();
        if (direction == GameController.Direction.NONE) {
            switch (previousDirection) {
                case GameController.Direction.EAST -> {
                    clearScreen();
                    startGame();
                    running = true;
                }
                case GameController.Direction.WEST -> {
                    clearScreen();
                    exit = true;
                }
                case GameController.Direction.NORTH -> {
                    soundEnabled = !soundEnabled;
                    renderTitleScreen();
                }
            }
            previousDirection = GameController.Direction.NONE;
        } else if (previousDirection == GameController.Direction.NONE) {
            previousDirection = direction;
        }

        if (cellSize < 8) {
            graphics.setFont(BitmapFont.get5x8Font());
            graphics.setColor(backgroundColor);
            graphics.fillRect(0, 0, 1000, 64);
            graphics.setColor(0xffffffff);
            int len = graphics.renderText(scrollPos--/2, 8, scrollText);
            if (scrollPos < -len) {
                scrollPos = 0;
            }
        }
    }

    public void run() {
        while (!exit) {
            delay.setDelayMillis(17);
            if (running) {
                running = step();
                if (!running) {
                    renderTitleScreen();
                }
            } else {
                titleScreenStep();
            }
            display.flush();
            delay.materializeDelay();
        }
    }

    public void play(String mml) {
        if (soundDriver != null && soundEnabled) {
            soundDriver.playNotes(mml);
        }
    }

    public void sleep(int ms) {
        if (running) {
            LockSupport.parkNanos(ms * 1_000_000L);
        } else {
              try {
                Thread.sleep(ms);
              } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  throw new RuntimeException(e);
              }
        }
    }

    /** Implement the logic for starting a new game here. */
    protected abstract void startGame();

    /** Invoked every ~17ms. Implemenet the game logic and rendering here. */
    protected abstract boolean step();
}
