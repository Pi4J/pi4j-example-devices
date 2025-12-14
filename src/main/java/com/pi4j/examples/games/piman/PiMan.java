package com.pi4j.examples.games.piman;

import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.drivers.sound.SoundDriver;
import com.pi4j.examples.games.MiniGame;
import com.pi4j.examples.games.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PiMan extends MiniGame {
    static final float EPSILON = 0.01f;

    final Random random = new Random();

    static final float SPEED = 1f/15;

    static final GameController.Direction[] DIRECTIONS = GameController.Direction.values();

    List<Monster> monsters = new ArrayList<>();

    float playerX = 3;
    float playerY = 5;

    private GameController.Direction direction = GameController.Direction.NONE;
    private GameController.Direction preselected = GameController.Direction.NONE;
    private boolean gameOver = false;
    private int remaining;
    private Sprite playerSprite = Sprites.PLAYER;
    private Sprite.Transformation playerTransform = Sprite.Transformation.NONE;
    private int levelTime = 0;
    int level = 0;

    private static final String MAZE =
            "....*..." +
            ".AEC AC." +
            "...  ..." +
            "AC.IJ.AC" +
            "...LK..." +
            ".B....B." +
            ".MC.AEP." +
            "... ....";

    private StringBuilder maze = new StringBuilder(MAZE);


    public PiMan(GraphicsDisplay display,
          GameController controller,
          SoundDriver soundDriver) {
        super(display, controller, soundDriver, "PiMan", 0xff000000, 0xffffffff);
    }

    @Override
    public void startGame() {
        gameOver = false;
        level = 0;
        startLevel();
    }

    void startLevel() {
        clearScreen();
        maze = new StringBuilder(MAZE);
        monsters.clear();

        playerX = 3;
        playerY = 7;
        preselected = GameController.Direction.NONE;
        direction = GameController.Direction.NONE;

        monsters.add(new Monster(this));
        monsters.add(new Monster(this));

        remaining = 0;
        for (char c : MAZE.toCharArray()) {
            if (c == '.' || c == '*') {
                remaining++;
            }
        }
        levelTime = 0;
    }

    void flashBackground(int count) {
        for (int i = 0; i < count; i++) {
            renderBackground(true);
            sleep(400);
            // play(">c");
            renderBackground(false);
            sleep(400);
        }

    }

    void renderBackground(boolean highlight) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                renderBackground(x, y, highlight);
            }
        }
    }

    void renderBackground(float x, float y) {
        for (int by = (int) y; by <= (int) (y + 0.99f); by++) {
            for (int bx = (int) x; bx <= (int) (x + 0.99f); bx++) {
                renderBackground(bx, by, false);
            }
        }
    }

    void renderBackground(int x, int y, boolean highlight) {
        char c = mazeAt(x, y);
        switch (c) {
            case ' ' -> {}
            case '.' -> {
                graphics.setColor(scaledCellSize == 1 ? 0xff333300 : 0xffffffff);
                int size = Math.max(1, (scaledCellSize + 5) / 6);
                int offset = (scaledCellSize - size) / 2;
                graphics.fillRect(x0 + x * scaledCellSize + offset, y0 + y * scaledCellSize + offset, size, size);
            }
            case '*' -> {
                graphics.setColor(scaledCellSize == 1 ? 0xff666600 : 0xffffffff);
                int size = Math.max(1, (scaledCellSize + 1) / 2);
                int offset = (scaledCellSize - size) / 2;
                graphics.fillRect(x0 + x * scaledCellSize + offset, y0 + y * scaledCellSize + offset, size, size);
            }
            default -> {
                Sprite.Transformation transform = Sprite.Transformation.values()[(c - 'A') % 4];
                (highlight ? Sprites.WHITE_WALLS : Sprites.WALLS)[(c - 'A') / 4].render(this, x, y, transform);
            }
        }
    }

    public boolean step() {
        GameController.Direction pressed = controller.getDirection();
        if ((pressed.getX() != 0) != (pressed.getY() != 0)) {
            preselected = pressed;
        }

        if (canMove(playerX, playerY, preselected)) {
            direction = preselected;
            switch (direction) {
                case WEST -> playerTransform = Sprite.Transformation.MIRROR_H;
                case EAST -> playerTransform = Sprite.Transformation.NONE;
            }
        }

        if (canMove(playerX, playerY, direction)) {
            playerSprite.erase(this, playerX, playerY, playerTransform);
            renderBackground(playerX, playerY);
            playerX = addAndSnap(playerX, direction.getX() * SPEED);
            playerY = addAndSnap(playerY, direction.getY() * SPEED);

            if (playerX == (int) playerX && playerY == (int) playerY) {
                char c = mazeAt((int) playerX, (int) playerY);
                if (c == '.' || c == '*') {
                    maze.setCharAt((int) (playerY * 8 + playerX), ' ');
                    addPoints(1);
                    remaining--;
                    if (c == '*') {
                        if (remaining != 0) {
                            play("c8e16");
                        }
                        for (Monster monster : monsters) {
                            if (monster.mode != Monster.Mode.HOUSE && monster.mode != Monster.Mode.LAUNCH) {
                                monster.mode = Monster.Mode.SCARED;
                                monster.modeCountdown = 1000;
                            }
                        }
                    } else if (remaining != 0) {
                        play("<c16");
                    }
                }
            }
        }
        playerSprite.render(this, playerX, playerY, playerTransform);

        if (remaining == 0) {
            nextLevel();
        } else {

            for (Monster monster : monsters) {
                monster.step();
            }

            if (levelTime++ == 0) {
                flashBackground(3);
            }
        }
        return !gameOver;
    }

    boolean canMove(float x, float y, GameController.Direction direction) {
        if ((direction.getX() == 0) == (direction.getY() == 0)) {
            return false;
        }

        if (direction.getX() != 0 && y != (int) y || direction.getY() != 0 && x != (int) x) {
            return false;
        }

        float tx = x;
        float ty = y;
        switch (direction) {
            case NORTH ->  ty = ty - EPSILON;
            case SOUTH -> ty = ty + 1;
            case WEST -> tx = tx - EPSILON;
            case EAST -> tx = tx + 1;
        }

        int txi = (int) (tx + 1) - 1;
        int tyi = (int) (ty + 1) - 1;

        return mazeAt(txi, tyi) < '0';
    }


    char mazeAt(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            return '0';
        }
        return maze.charAt(y * 8 + x);
    }

    void gameOver() {
        play("<edc");
        gameOver = true;
        for (int i = 1; i < 7; i++) {
            sleep(300);
            Sprites.PLAYER.render(this, playerX, playerY, Sprite.Transformation.values()[i%4]);
        }

        clearScreen(1000);
    }

    void nextLevel() {
        play("cde");
       level++;
        clearScreen(2000);
       startLevel();
    }
}
