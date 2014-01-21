package net.javaci.mobile.bomberman.core.models;

import java.util.Random;

public class LabyrinthModel {
    public static final int NUM_COLS = 21;
    public static final int NUM_ROWS = 13;
    public static final int INITIAL_BRICK_COUNT = 60;
    public static byte EMPTY = 0;
    public static byte WALL = 1;
    public static byte BRICK = 2;
    private byte[][] grid = new byte[NUM_COLS][NUM_ROWS];

    public LabyrinthModel() {
        for (int i = 0; i < NUM_COLS; i++) {
            for (int j = 0; j < NUM_ROWS; j++) {
                if (i == 0 || (i == NUM_COLS - 1) || j == 0 || j == (NUM_ROWS - 1)) {
                    grid[i][j] = WALL;
                } else if (i % 2 == 0 && j % 2 == 0) {
                    grid[i][j] = WALL;
                }
            }
        }
        generateBricks();
    }

    private void generateBricks() {
        int numBricks = 0;
        Random random = new Random(System.currentTimeMillis());
        while (numBricks < INITIAL_BRICK_COUNT) {
            int x = random.nextInt(NUM_COLS - 1) + 1;
            int y = random.nextInt(NUM_ROWS - 1) + 1;
            if (grid[x][y] == EMPTY) {
                if ( ! ((x == 1 && y == 1) || (x == 1 && y == 2) || (x == 2 && y == 1)) &&
                     ! ((x == 19 && y == 1) || (x == 19 && y == 2) || (x == 18 && y == 1)) &&
                     ! ((x == 1 && y == 10) || (x == 1 && y == 11) || (x == 2 && y == 11)) &&
                     ! ((x == 19 && y == 10) || (x == 19 && y == 11) || (x == 18 && y == 11))) {
                    grid[x][y] = BRICK;
                    numBricks++;
                }
            }
        }
    }

    public byte[][] getGrid() {
        return grid;
    }
}
