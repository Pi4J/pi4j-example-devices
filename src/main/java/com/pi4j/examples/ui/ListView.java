package com.pi4j.examples.ui;

import com.pi4j.drivers.display.BitmapFont;
import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.io.ListenableOnOffRead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Renders an interactive scrollable list of items on the screen. Scales down to 8x8 matrix displays by scrolling
 * the selected item horizontally.
 * <p>
 * For a usage example, please refer to the SenseHat demo.
 */
public class ListView {
    public static final Runnable EXIT_ACTION = () -> {};

    private final GraphicsDisplay display;
    private final GameController controller;
    private final Object lock = new Object();
    private final List<Item> items = new ArrayList<>();
    private final int scale;
    private final BitmapFont font;
    private final int lineHeight;
    private final Map<ListenableOnOffRead<?>, Consumer<Boolean>> activeKeys = new HashMap<>();

    private boolean scroll;
    private int x0;
    private int line0;
    private int selectedIndex = 0;
    private Runnable triggeredAction = null;
    private boolean exit;

    public ListView(GraphicsDisplay display, GameController controller) {
        this(display, controller, 1);
    }

    public ListView(GraphicsDisplay display, GameController controller, int scale) {
        this.display = display;
        this.controller = controller;
        this.scale = scale;
        if (display.getHeight() > 50 * scale) {
            font = BitmapFont.get5x10Font();
        } else {
            font = BitmapFont.get5x8Font();
        }
        this.lineHeight = font.getCellHeight() * scale;
    }

    public ListView add(String text) {
        return add(text, null);
    }

    public ListView add(String text, Runnable action) {
        synchronized (lock) {
            items.add(new Item(text, action));
        }
        return this;
    }

    public ListView set(int index, String text) {
        synchronized (lock) {
            items.set(index, new Item(text, null));
        }
        return this;
    }

    public void run() {
        synchronized (lock) {
            while (!exit) {
                initialize();
                try {
                    while (!exit && triggeredAction == null) {
                        lock.wait(50);
                        if (scroll) {
                            if (--x0 < -(3 + items.get(selectedIndex).label.length()) * 6) {
                                x0 = 0;
                            }
                            render(selectedIndex);
                            display.flush();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                releaseKeys();
                if (triggeredAction == EXIT_ACTION) {
                    exit = true;
                } else if (triggeredAction != null) {
                    triggeredAction.run();
                }
                triggeredAction = null;
            }
            display.fillRect(0, 0, display.getWidth(), display.getHeight(), 0xff000000);
        }
    }

    public int size() {
        synchronized (lock) {
            return items.size();
        }
    }

    // Private helpers

    private void assignKeys(Consumer<Boolean> consumer, GameController.Key... keys) {
        synchronized (lock) {
            for (GameController.Key key : keys) {
                ListenableOnOffRead<?> lor = controller.getKey(key);
                if (lor != null) {
                    this.activeKeys.put(lor, lor.addConsumer(consumer));
                }
            }
        }
    }

    private void initialize() {
        synchronized (lock) {
            render();
            assignKeys(pressed -> moveCursor(pressed, -1), GameController.Key.UP);
            assignKeys(pressed -> moveCursor(pressed, 1), GameController.Key.DOWN);
            assignKeys(this::triggerKey,
                GameController.Key.RIGHT, GameController.Key.CENTER,
                GameController.Key.A, GameController.Key.SELECT, GameController.Key.KEY_1);
            if (!items.stream().anyMatch(item -> item.action == EXIT_ACTION)) {
                assignKeys(this::backKey, GameController.Key.B, GameController.Key.LEFT, GameController.Key.KEY_3);
            }
            select(selectedIndex);
        }
    }

    private void render() {
        synchronized (lock) {
            int maxLine = line0 + display.getHeight() / lineHeight + 1;
            for (int i = line0; i < maxLine; i++) {
                render(i);
            }
        }
    }

    private void moveCursor(boolean pressed, int direction) {
        if (pressed) {
            select(selectedIndex + direction);
        }
    }

    private void releaseKeys() {
        synchronized (lock) {
            for (Map.Entry<ListenableOnOffRead<?>, Consumer<Boolean>> entry : activeKeys.entrySet()) {
                entry.getKey().removeConsumer(entry.getValue());
            }
            activeKeys.clear();
        }
    }

    private void render(int index) {
        synchronized (lock) {
            boolean selected = selectedIndex == index;
            boolean invert = selected && display.getHeight() > scale * 8;
            int backgroundColor = invert ? 0xffffffff : 0xff000000;
            int foregroundColor = invert ? 0xff000000 : 0xffffffff;

            display.fillRect(0, lineHeight * (index - line0), display.getWidth(), lineHeight, backgroundColor);
            if (index < items.size()) {
                String text = items.get(index).label;
                int x = 0;
                if (selected && scroll) {
                    x = x0 * scale;
                    text += " - " + text;
                }
                display.renderText(x, lineHeight + (index - line0) * lineHeight, text, font, foregroundColor, scale, scale);
            }
        }
    }

    private void select(int index) {
        synchronized (lock) {
           selectedIndex = (items.size() + index) % items.size();
           line0 = Math.max(0, selectedIndex - (display.getHeight() - lineHeight) / lineHeight);
           scroll = items.get(selectedIndex).label.length() * 6 * scale > display.getWidth();
           x0 = 0;
           render();
        }
    }

    private void backKey(boolean pressed) {
        if (pressed) {
            exit = true;
        }
    }

    private void triggerKey(boolean pressed) {
        if (pressed) {
            synchronized (lock) {
                triggeredAction = items.get(selectedIndex).action;
                lock.notify();
            }
        }
    }

    static class Item {
        final String label;
        final Runnable action;

        Item(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }
}
