package com.pi4j.examples.games.piman;

import com.pi4j.drivers.input.GameController;
import com.pi4j.examples.games.MiniGame;
import com.pi4j.examples.games.Sprite;

class Monster {
    private final static Sprite FRIGHTENED_1 = Sprites.MONSTER.withColors(0xff8888ff,0, 0xff8888ff, 0xff8888ff, 0xffffbbaa);
    private final static Sprite FRIGHTENED_2 = Sprites.MONSTER.withColors(0xffddddff,0, 0xffddddff, 0xffddddff, 0xffff0000);
    private final static Sprite RETURNING = Sprites.MONSTER.withColors(0, 0, 0, 0xff000000, 0xffffffff);

    static final float SPEED = 1f/20;

    PiMan game;
    Sprite sprite;
    float x;
    float y;
    GameController.Direction direction = GameController.Direction.NONE;
    Mode mode;
    int modeCountdown;
    int number;
    Sprite.Transformation transformation = Sprite.Transformation.NONE;

    Monster(PiMan game) {
        this.game = game;
        number = game.monsters.size();
        if (number == 0) {
            sprite = Sprites.MONSTER;
            x = 4;
            y = 2;
            mode = Mode.CHASE;
            modeCountdown = Integer.MAX_VALUE;
        } else {
            sprite = Sprites.MONSTER.withColors(0xff88ffff, 0, 0xff88ffff, 0xff000000, 0xffffffff);
            x = 3.5f;
            y = x;
            mode = Mode.HOUSE;
            modeCountdown = 800;
        }
    }

    public void step() {
        switch (mode) {
            case RETURN -> {
                if (Math.abs(3.5f - x) + Math.abs(3.5f - y) <= 2* speed()) {
                    x = 3.5f;
                    y = 3.5f;
                    mode = Mode.HOUSE;
                    modeCountdown = 300;
                } else {
                    direction = GameController.Direction.of((int) (100 * (3.5f - x)), (int) (100 * (3.5f - y)));}
            }
            case HOUSE -> direction = (modeCountdown % 20) < 10 ? GameController.Direction.NORTH : GameController.Direction.SOUTH;
            case LAUNCH -> {
                if (y < 2) {
                    y = 2;
                    direction = game.random.nextBoolean() ? GameController.Direction.EAST : GameController.Direction.WEST;
                    mode = Mode.CHASE;
                    modeCountdown = Integer.MAX_VALUE;
                }
            }
            default -> {
                if (x == (int) x && y == (int) y) {
                    direction = updateDirection();
                    if (direction == GameController.Direction.EAST) {
                        transformation = Sprite.Transformation.NONE;
                    } else if (direction == GameController.Direction.WEST) {
                        transformation = Sprite.Transformation.MIRROR_H;
                    }
                }
            }
        }
        sprite.erase(game, x, y, transformation);
        game.renderBackground(x, y);
        float speed = speed();
        x = MiniGame.addAndSnap(x, direction.getX() * speed);
        y = MiniGame.addAndSnap(y, direction.getY() * speed);

        Sprite modeSprite = switch (mode) {
            case SCARED -> (modeCountdown > 100 || modeCountdown / 20 % 2 == 0) ? FRIGHTENED_1 : FRIGHTENED_2;
            case RETURN -> RETURNING;
            default -> sprite;
        };
        modeSprite.render(game, x, y, transformation);

        if (Math.pow(x - game.playerX, 2) + Math.pow(y - game.playerY, 2) <= .7) {
            if (mode == Mode.SCARED) {
                mode = Mode.RETURN;
                game.addPoints(10);
                game.play("c8c8");
            } else if (mode != Mode.RETURN) {
                game.gameOver();
            }
        }

        if (--modeCountdown == 0) {
            switch (mode) {
                case HOUSE -> {
                    mode = Mode.LAUNCH;
                    direction = GameController.Direction.NORTH;
                    modeCountdown = Integer.MAX_VALUE;
                }
                case SCATTER, SCARED -> {
                    mode = Mode.CHASE;
                    modeCountdown = Integer.MAX_VALUE;
                }
            }
        }
    }

    float speed() {
        return switch(mode) {
            case RETURN -> 1.5f * SPEED;
            case HOUSE, LAUNCH -> SPEED / 4;
            case SCARED -> SPEED*2/3;
            default -> SPEED;
        };
    }

    GameController.Direction updateDirection() {
        if (mode == Mode.CHASE || mode == Mode.SCATTER) {
            float targetX;
            float targetY;
            if (mode == Mode.SCATTER) {
                targetX = number * 7;
                targetY = 0;
            } else {
                targetX = game.playerX;
                targetY = game.playerY;
            }
            float dx = targetX - x;
            float dy = targetY - y;
            GameController.Direction primary = dx > 0 ? GameController.Direction.EAST : GameController.Direction.WEST;
            GameController.Direction secondary  = dy > 0 ? GameController.Direction.SOUTH : GameController.Direction.NORTH;
            if (Math.abs(dy) > Math.abs(dx)) {
                GameController.Direction tmp = primary;
                primary = secondary;
                secondary = tmp;
            }
            if (primary != direction.opposite() && game.canMove(x, y, primary)) {
                return primary;
            }
            if (secondary != direction.opposite() && game.canMove(x, y, secondary)) {
                return secondary;
            }
        }
        while (true) {
            GameController.Direction newDirection = PiMan.DIRECTIONS[game.random.nextInt(PiMan.DIRECTIONS.length)];
            if (newDirection != direction.opposite()
                    && newDirection != GameController.Direction.NONE
                    && game.canMove(x, y, newDirection)) {
                return newDirection;
            }
        }
    }

    enum Mode {
        HOUSE, LAUNCH, CHASE, SCATTER, SCARED, RETURN;
    }
}
