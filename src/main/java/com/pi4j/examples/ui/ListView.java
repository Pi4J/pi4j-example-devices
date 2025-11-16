package com.pi4j.examples.ui;

import com.pi4j.drivers.display.BitmapFont;
import com.pi4j.drivers.display.character.CharacterDisplay;
import com.pi4j.drivers.display.graphics.GraphicsCharacterDisplay;
import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.io.ListenableOnOffRead;
import com.pi4j.io.OnOffRead;

import java.util.*;
import java.util.function.Consumer;

/**
 * Renders an interactive scrollable list of items on the screen. Scales down to 8x8 matrix displays by scrolling
 * the selected item horizontally.
 * <p>
 * For a usage example, please refer to the SenseHat demo.
 */
public class ListView {
    public static final Runnable EXIT_ACTION = () -> {};

    private final CharacterDisplay display;
    private final GameController controller;
    private final List<Item> items = new ArrayList<>();

    private final Map<ListenableOnOffRead<?>, Consumer<Boolean>> activeKeys = new HashMap<>();

    private boolean scroll;
    private int x0;
    private int line0;
    private int selectedIndex = 0;
    private Runnable triggeredAction = null;
    private boolean exit;
    private boolean keyEnabled;

    public ListView(CharacterDisplay display, GameController controller) {
        this.display = display;
        this.controller = controller;
    }

    public ListView(GraphicsDisplay display, GameController controller, int scale) {
        BitmapFont font = display.getHeight() > 50 * scale ? BitmapFont.get5x10Font() : BitmapFont.get5x8Font();
        this(new GraphicsCharacterDisplay(display, font, 0xffffffff, 0xff000000, scale),
            controller);
    }

    public ListView add(String text) {
        return add(text, null);
    }

    public ListView add(String text, Runnable action) {
        items.add(new Item(text, action));
        return this;
    }

    public ListView set(int index, String text) {
        items.set(index, new Item(text, null));
        return this;
    }

    public void run() {
        render();
        while (!exit) {
                try {
                    while (!exit) {
                        Thread.sleep(50);
                        GameController.Direction direction = controller.getDirection();
                        if (!keyEnabled) {
                            keyEnabled = direction == GameController.Direction.NONE;
                        } else if (direction == GameController.Direction.NORTH) {
                            select(selectedIndex - 1);
                            keyEnabled = false;
                        } else if (direction == GameController.Direction.SOUTH) {
                            select(selectedIndex + 1);
                            keyEnabled = false;
                        } else if (direction == GameController.Direction.EAST
                              || anyPressed(GameController.Key.CENTER, GameController.Key.START, GameController.Key.A)) {
                            Runnable action = items.get(selectedIndex).action;
                            if (action == EXIT_ACTION) {
                                exit = true;
                            } else {
                                action.run();
                                render();
                            }
                        } else if (direction == GameController.Direction.WEST) {
                                keyEnabled = false;
                        }

                        if (scroll) {
                            if (--x0 < -(3 + items.get(selectedIndex).label.length()) * 6) {
                                x0 = 0;
                            }
                            render(selectedIndex);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                if (triggeredAction == EXIT_ACTION) {
                    exit = true;
                } else if (triggeredAction != null) {
                    triggeredAction.run();
                }
                triggeredAction = null;
        }
        display.clear();
    }

    public int size() {
        return items.size();
    }

    // Private helpers

    private boolean anyPressed(GameController.Key... keys) {
        for (GameController.Key key : keys) {
            OnOffRead<?> onOff = controller.getKey(key);
            if (onOff != null && onOff.isOn()) {
                return true;
            }
        }
        return false;
    }

    private void render() {
        display.clear();
        int maxLine = line0 + display.getHeight();
        for (int i = line0; i <= maxLine; i++) {
            render(i);
        }
    }

    private void render(int index) {
        boolean selected = selectedIndex == index;
        boolean invert = selected && display.getHeight() > 1;

        if (index < items.size()) {
            String text = items.get(index).label;
            int x = 0;
            if (selected && scroll) {
                x = x0;
                text += " - " + text;
            } else {
                text += " ".repeat(display.getWidth());
            }
            display.writeAt(x, index - line0, text,
                invert ? EnumSet.of(CharacterDisplay.Attribute.INVERSE) : EnumSet.noneOf(CharacterDisplay.Attribute.class));
        }
    }

    private void select(int index) {
           selectedIndex = (items.size() + index) % items.size();
           line0 = Math.max(0, selectedIndex - display.getHeight());
           scroll = items.get(selectedIndex).label.length() > display.getWidth();
           x0 = 0;
           render();
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
