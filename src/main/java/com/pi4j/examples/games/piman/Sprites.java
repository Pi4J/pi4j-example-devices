package com.pi4j.examples.games.piman;

import com.pi4j.examples.games.Sprite;

import java.util.Arrays;

public class Sprites {
    private static final int WALL_PIXEL_COLOR = 0xff0000ff;
    private static final int[] WALL_PALLETTE = new int[]{0, 0xff8888ff};

    static final Sprite PLAYER =new Sprite( 0xffffff00, 9, new long[] {
            0x011111110L,
            0x112212211L,
            0x123323311L,
            0x123223221L,
            0x112212211L,
            0x111111111L,
            0x442222244L,
            0x444444444L,
            0x044444440L
    }, new int[]{0, 0xffffff00, 0xff000000, 0xffffffff, 0xff8888ff});


    static final Sprite[] WALLS = new Sprite[] {

            new Sprite(WALL_PIXEL_COLOR, 10,
                    new long[] {
                            0x0000000000L,
                            0x0000000000L,
                            0x0001111111L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0001111111L,
                            0x0000000000L,
                            0x0000000000L,
                    }, WALL_PALLETTE),
            new Sprite(WALL_PIXEL_COLOR, 10,
                    new long[] {
                            0x0000000000L,
                            0x0000000000L,
                            0x1111111111L,
                            0x0000000000L,
                            0x0000000000L,
                            0x0000000000L,
                            0x0000000000L,
                            0x1111111111L,
                            0x0000000000L,
                            0x0000000000L,
                    }, WALL_PALLETTE),
            new Sprite(WALL_PIXEL_COLOR, 10,
                    new long[] {
                            0x0000000000L,
                            0x0000000000L,
                            0x0001111111L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                    },  WALL_PALLETTE),
            new Sprite(WALL_PIXEL_COLOR, 10,
                    new long[] {
                            0x0010000100L,
                            0x0010000100L,
                            0x0010000011L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0010000000L,
                            0x0001111111L,
                            0x0000000000L,
                            0x0000000000L,
                    },  WALL_PALLETTE),
    };


    static Sprite[] WHITE_WALLS = Arrays.stream(WALLS).map(s -> s.withColors(0xffffffff, new int[] {0, 0xffffffff})).toArray(Sprite[]::new);

    static Sprite MONSTER = new Sprite( 0xffff8888, 10, new long[] {
                0x0000000000L,
                0x1010101010L,
                0x0111111100L,
                0x0112212210L,
                0x1123323321L,
                0x0123223221L,
                0x0012212211L,
                0x0111111110L,
                0x0001001010L,
                0x0010010000L
        },  new int[]{0, 0xffff8888, 0xff000000, 0xffffffff});
}
