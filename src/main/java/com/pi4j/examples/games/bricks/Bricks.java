package com.pi4j.examples.games.bricks;

import com.pi4j.drivers.display.graphics.GraphicsDisplay;
import com.pi4j.drivers.input.GameController;
import com.pi4j.io.ListenableOnOffRead;
import com.pi4j.util.DeferredDelay;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Bricks {

    private static final int FIELD_SIZE = 64;
    private static final int BALL_SIZE = 4;
    private static final int PADDLE_WIDTH = 16;
    private static final int PADDLE_HEIGHT = 4;
    private static final int PADDLE_Y = FIELD_SIZE - PADDLE_HEIGHT * 3 / 2;

    private final GraphicsDisplay display;
    private final GameController controller;
    private final DeferredDelay delay = new DeferredDelay();
    private final Map<ListenableOnOffRead<?>, Consumer<Boolean>> keys = new HashMap<>();

    private final int scale;
    private final int x0;
    private final int y0;
    private final Map<ListenableOnOffRead<?>, Consumer<Boolean>> activeKeys = new HashMap<>();

    private float ballX;
    private int ballY;
    private float ballDx;
    private int ballDy;

    private int paddleX;
    private int paddleDx;
    private boolean exit;
    private boolean gameOver;
    private int stepTime = 25;

    boolean[][] bricks = new boolean[4][3];
    int remainingBrickCount;

    public Bricks(GraphicsDisplay display, GameController controller) {
        this.display = display;
        this.controller = controller;
        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();

        assignKeys(on -> setMovement(on, -2), GameController.Key.LEFT, GameController.Key.LT);
        assignKeys(on -> setMovement(on, 2), GameController.Key.RIGHT, GameController.Key.RT);
        assignKeys(on -> actionKey(on), GameController.Key.CENTER, GameController.Key.A, GameController.Key.START);

        scale = Math.min(displayHeight / FIELD_SIZE, displayWidth / FIELD_SIZE);
        x0 = (displayWidth - FIELD_SIZE * scale) / 2;
        y0 = (displayHeight - FIELD_SIZE * scale) / 2;

        resetBall();
        initializeBricks();
    }

    private void assignKeys(Consumer<Boolean> consumer, GameController.Key... keys) {
        for (GameController.Key key : keys) {
            ListenableOnOffRead<?> lor = controller.getKey(key);
            if (lor != null) {
                this.activeKeys.put(lor, lor.addConsumer(consumer));
            }
        }
    }

    private void actionKey(boolean on) {
        if (!on && gameOver) {
            exit = true;
        } else if (on && ballDy == 0) {
            ballDy = -1;
            ballDx = 0.1f;
        }
    }

    private void initializeBricks() {
        display.fillRect(0, 0, display.getWidth(), display.getHeight(), 0);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                setBrick(i, j, true);
            }
        }
        paddleX = (FIELD_SIZE - PADDLE_WIDTH) / 2;
        remainingBrickCount = 4 * 3;
    }

    private void moveBall() {
        if (gameOver) {

        } else if (ballDy == 0) {
            ballX = paddleX + (PADDLE_WIDTH-BALL_SIZE) / 2;
        } else {
            ballX += ballDx;
            ballY += ballDy;

            if (ballX < 0 || ballX >= FIELD_SIZE - BALL_SIZE) {
                // Side reflection
                ballDx = -ballDx;
                ballX += ballDx;
            }

            if (ballY < 0) {
                // Top reflection
                ballDy = -ballDy;
                ballY += ballDy;
            } else if (ballY + BALL_SIZE > PADDLE_Y) {
                // paddle or miss
                int paddleCenter = paddleX + PADDLE_WIDTH / 2;
                float paddleCenterDistance = ballX + BALL_SIZE / 2f - paddleCenter;
                float absolutePaddleCenterDistance = Math.abs(paddleCenterDistance);

                if (absolutePaddleCenterDistance > (PADDLE_WIDTH + BALL_SIZE) / 2f) {
                    gameOver = true;
                    paddleDx = 0;
                } else {
                    ballDy = -ballDy;
                    ballY += ballDy;

                    ballDx += 2 * paddleCenterDistance / PADDLE_WIDTH;
                    ballDx = Math.max(-1.2f, Math.min(ballDx, 1.2f));

                    if (remainingBrickCount == 0) {
                        initializeBricks();
                        if (stepTime > 10) {
                            stepTime--;
                        }
                    }
                }
            } else {
                int row = ballY / 8;
                if (row >= 1 && row <= 3) {
                    int i = (int) (ballX / 16);
                    int j = row - 1;
                    if (bricks[i][j]) {
                        setBrick(i, j, false);
                        ballDy = -ballDy;
                        ballY += ballDy;
                        remainingBrickCount--;
                    }
                }
            }
        }
    }

    private void resetBall() {
        ballDx = 0;
        ballDy = 0;
        ballY = PADDLE_Y - BALL_SIZE;
        ballX = paddleX + (8-BALL_SIZE) / 2;
    }


    private void releaseKeys() {
        for (Map.Entry<ListenableOnOffRead<?>, Consumer<Boolean>> entry : activeKeys.entrySet()) {
            entry.getKey().removeConsumer(entry.getValue());
        }
        activeKeys.clear();
    }

    private void renderPaddle(boolean on) {
        display.fillRect(x0 + paddleX * scale, y0 + PADDLE_Y * scale,  PADDLE_WIDTH * scale, PADDLE_HEIGHT * scale, on ? 0x888888 : 0);
    }

    private void renderBall(boolean on) {
        display.fillRect(Math.round(x0 + ballX * scale), y0 + ballY * scale, BALL_SIZE * scale , BALL_SIZE * scale, on ? 0x888888 : 0);
    }


    public void run() {
        while (!exit) {
            delay.setDelayMillis(stepTime);
            renderPaddle(false);
            paddleX = Math.max(0, Math.min(paddleX + paddleDx, FIELD_SIZE - PADDLE_WIDTH));
            renderPaddle(true);

            renderBall(false);
            moveBall();
            renderBall(true);

            delay.materializeDelay();
        }
        releaseKeys();
    }

    private void setMovement(boolean on, int dx) {
        if (gameOver) {
            if (on) {
                exit = true;
            }
        } else {
            paddleDx = on ? dx : 0;
        }
    }

    private void setBrick(int x, int y, boolean alive) {
        bricks[x][y] = alive;
        display.fillRect(
            x0 + 16 * x * scale + 1,
            y0 + 8 * (y + 1) * scale + 1,
            14 * scale,
            6 * scale,
            alive ? (y == 0 ? 0xff0000 : y == 1 ? 0xff00 : 0xff) : 0);
    }
}
