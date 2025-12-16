package com.pi4j.examples.games;

public class Sprite {
    private int color;
    private int[] bitMap;
    private int bitOffset;
    private int bitStride;
    private int[] palette;
    private int width;
    private int height;

    public Sprite(int color, int width, int[] bitmap, int[] palette) {
        this.color = color;
        this.width = width;
        this.bitMap = bitmap;
        this.bitStride = 32;
        this.bitOffset = 32 - width * 4;
        this.height = bitmap.length * 32 / bitStride;
        this.palette = palette;
    }

    public Sprite(int color, int width, long[] bitmap, int[] palette) {
        this.color = color;
        this.width = width;
        this.bitMap = long2intArray(bitmap);
        this.bitStride = 64;
        this.bitOffset = 64 - width * 4;
        this.height = this.bitMap.length * 32 / bitStride;
        this.palette = palette;
    }

    public Sprite withColors(int color, int... palette) {
        Sprite result = new Sprite(color, this.width, bitMap, palette);
        result.bitStride = bitStride;
        result.bitOffset = bitOffset;
        result.height = height;
        return result;
    }

    public void render(MiniGame game, float x, float y) {
        render(game, x, y, Transformation.NONE);
    }

    public void fill(MiniGame game, float x, float y, Transformation transformation, int color) {
        game.graphics.setColor(color);
        int w;
        int h;
        if (game.cellSize < 8) {
            w = 1;
            h = 1;
        } else if (transformation == Transformation.ROTATE_90 || transformation == Transformation.ROTATE_270) {
            w = height;
            h = width;
        } else {
            w = width;
            h = height;
        }
        game.graphics.fillRect(
                game.x0 + (int) (x * game.scaledCellSize + (game.scaledCellSize - w * game.scale) / 2),
                game.y0 + (int) (y * game.scaledCellSize + (game.scaledCellSize - h * game.scale) / 2),
                w * game.scale,
                h * game.scale);
    }

    public void render(MiniGame game, float x, float y, Transformation transformation) {
        if (game.cellSize < 8) {
            fill(game, x, y, transformation, color);
            return;
        }
        int stride;
        int increment;
        int offset;
        int w;
        int h;
        switch (transformation) {
            case ROTATE_90 -> {
                offset = bitStride * (height - 1) + bitOffset;
                increment = -bitStride;
                stride = 4;
                w = height;
                h = width;
            }
            case ROTATE_180 -> {
                offset = bitStride * height - 4;
                increment = -4;
                stride = -bitStride;
                w = width;
                h = height;
            }
            case ROTATE_270 -> {
                offset = bitStride - 4;
                increment = bitStride;
                stride = -4;
                w = height;
                h = width;
            }
            case MIRROR_H -> {
                offset = bitStride - 4;
                increment = -4;
                stride = bitStride;
                w = width;
                h = height;
            }
            case MIRROR_V -> {
                offset = bitStride * (height - 1) + bitOffset;
                increment = 4;
                stride = -bitStride;
                w = width;
                h = height;
            }
            default ->  {
                offset = bitOffset;
                increment = 4;
                stride = bitStride;
                w = width;
                h = height;
            }
        }

        game.graphics.setProcessAlpha(true);
        game.graphics.drawIndexed(
                game.x0 + (int) (x * game.scaledCellSize + (game.scaledCellSize - w * game.scale) / 2),
                    game.y0 + (int) (y * game.scaledCellSize + (game.scaledCellSize - h * game.scale) / 2),
                    w * game.scale,
                    h * game.scale,
                    bitMap,
                    4,
                    palette,
                    offset,
                    increment,
                    stride,
                    game.scale,
                    game.scale);
    }

    public void erase(MiniGame game, float x, float y, Transformation transformation) {
        fill(game, x, y, transformation, game.backgroundColor);
    }

    public static int[] long2intArray(long[] data) {
        int[] result = new int[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            result[2*i] = (int) (data[i] >>> 32);
            result[2*i+1] = (int) data[i];
        }
        return result;
    }

    public enum Transformation {
        NONE,
        ROTATE_90,
        ROTATE_180,
        ROTATE_270,
        MIRROR_H,
        MIRROR_V,
    }
}
