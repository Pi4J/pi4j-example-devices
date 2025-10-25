package com.pi4j.examples.games.snake;

import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.io.ListenableOnOffRead;
import com.pi4j.util.DeferredDelay;

import java.util.*;
import java.util.function.Consumer;

/**
 * A simple "Snake" game implementation for a 8x8 playing field.
 * <p>
 * Hold the center key for one second to exit.
 * <p>
 * This class doesn't include a main() method; please find an examples for wiring this up in the waveshare 14972
 * dispay hat demo.
 */
public class Snake {
    // 8x8 allows rendering on LED matrix displays
    private static final int AREA_SIZE = 8;
    private static final int INITIAL_STEP_TIME_MILLIS = 200;
    private static final int FADE_OUT_TIME = 2000;

    private final GraphicsDisplay display;
    private final DeferredDelay deferredDelay = new DeferredDelay();
    private final int x0;
    private final int y0;
    private final int scale;
    private final Random random = new Random();
    private final Map<ListenableOnOffRead<?>, Consumer<Boolean>> keys = new HashMap<>();

    private int fadeOut = 0;
    private int dx;
    private int dy;
    private int headX;
    private int headY;
    private int length;
    private int stepTimeMillis;

    private Entity[][] arena;
    private List<Segment> body = new ArrayList<>();
    private long longPressStart = Long.MAX_VALUE;

    public Snake(GraphicsDisplay display, GameController controller) {
        this.display = display;

        assignKey(controller.getKey(GameController.Key.UP), value -> processDirectionalKey(value, 0, -1));
        assignKey(controller.getKey(GameController.Key.DOWN), value -> processDirectionalKey(value, 0, 1));
        assignKey(controller.getKey(GameController.Key.LEFT), value -> processDirectionalKey(value, -1, 0));
        assignKey(controller.getKey(GameController.Key.RIGHT), value -> processDirectionalKey(value, 1, 0));
        assignKey(controller.getKey(GameController.Key.CENTER), value -> processCenterKey(value));

        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();

        scale = Math.min(displayHeight / AREA_SIZE, displayWidth / AREA_SIZE);
        x0 = (displayWidth - AREA_SIZE * scale) / 2;
        y0 = (displayHeight - AREA_SIZE * scale) / 2;
    }

    private void addFood() {
        while(true) {
            int x = random.nextInt(AREA_SIZE);
            int y = random.nextInt(AREA_SIZE);
            if (arena[x][y] == null) {
                setEntity(x, y, Entity.FOOD);
                break;
            }
        }
    }

    private void addSegment(int x, int y) {
        body.add(new Segment(x, y));
        if (body.size() > length) {
            Segment tail = body.removeFirst();
            setEntity(tail.x, tail.y, null);
        }
        setEntity(x, y, Entity.SNAKE);
    }

    private void assignKey(ListenableOnOffRead<?> key, Consumer<Boolean> consumer) {
        keys.put(key, key.addConsumer(consumer));
    }

    private void initialize() {
        arena = new Entity[AREA_SIZE][AREA_SIZE];
        display.fillRect(0, 0, display.getWidth(), display.getHeight(), 0);
        body.clear();
        length = 1;

        headX = AREA_SIZE / 2;
        headY = AREA_SIZE / 2;

        dx = 0;
        dy = 0;
        stepTimeMillis = INITIAL_STEP_TIME_MILLIS;

        addSegment(headX, headY);
        addFood();
    }

    private void processCenterKey(boolean pressed) {
        longPressStart = pressed ? System.currentTimeMillis() : Long.MAX_VALUE;
    }

    private void processDirectionalKey(boolean keyPressed, int dx, int dy) {
        if (keyPressed) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private void renderColor(int x, int y, int color) {
        display.fillRect(x0 + x * scale, y0 + y * scale, scale, scale, color);
    }

    public void run() {
        initialize();
        while (System.currentTimeMillis() - longPressStart < 1000) {
            deferredDelay.setDelayMillis(stepTimeMillis);
            step();
            deferredDelay.materializeDelay();
        }
        display.fillRect(0, 0, display.getWidth(), display.getHeight(), 0xff000000);
        for (Map.Entry<ListenableOnOffRead<?>, Consumer<Boolean>> entry : keys.entrySet()) {
            entry.getKey().removeConsumer(entry.getValue());
        }
    }

    private void setEntity(int x, int y, Entity entity) {
        arena[x][y] = entity;
        int color = switch(entity) {
            case null -> 0;
            case Entity.SNAKE -> 0x8888ff;  // green
            case Entity.FOOD -> 0xffff88;  // yellow
        };
        renderColor(x, y, color);
    }

    private void step() {
        if (fadeOut > 0) {
            for (Segment segment: body) {
                int normalized = Math.min((fadeOut * 255) / FADE_OUT_TIME, 255);
                int color = normalized | (normalized << 8) | (normalized << 16);
                renderColor(segment.x, segment.y, color);
            }
            fadeOut -= stepTimeMillis;
            if (fadeOut <= 0) {
                initialize();
            }
        } else {
            int newX = headX + dx;
            int newY = headY + dy;
            if (newX != headX || newY != headY) {
                if (newX < 0 || newY < 0 || newX >= AREA_SIZE || newY >= AREA_SIZE
                        || arena[newX][newY] == Entity.SNAKE) {
                    fadeOut = FADE_OUT_TIME;
                } else {
                    if (arena[newX][newY] == Entity.FOOD) {
                        length++;
                        if (stepTimeMillis > 100) {
                            stepTimeMillis -= 10;
                        }
                        addFood();
                    }
                    addSegment(newX, newY);
                    headX = newX;
                    headY = newY;
                }
            }
        }
        display.flush();
    }

    private static class Segment {
        private final int x;
        private final int y;
        public Segment(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private enum Entity {
        SNAKE, FOOD
    }
}
