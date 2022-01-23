package com.A2JMH;

// GAME EXPLANATION:
// The game setup is built around a standard 8x8 checkerboard, occupied symbolically by interger status values
// A 0 indicates a space with NO piece on it. A 1 indicates a space with a WHITE piece on it. A 2 indicates a space with a BLACK piece on it.
// Threads as clients are represented as either "Watchers" (readers) or "Players" (writers), with there only ever being 2 players in the game at a time.
// Watchers will randomly observe one space in the 8x8 board and make note of the status of that space. Tests in JMH will differ in the quantity of readers and the desired locking mechanism.
// Players will randomly move one of their checker pieces in a singular up down left or right fassion on each invocation of the benchmark method AFTER the watchers have observed the board already. The phaser in the benchmark.java file ensures that the players will alternate turns after the watchers do their thing.


// Shared data structure class
public class BoardA {
    // 0 is empty, 1 is white, 2 is black
    // this initialization mimics a standard checkers setup
    public static int[][] checkersGame = {
            {0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {2, 0, 2, 0, 2, 0, 2, 0},
            {0, 2, 0, 2, 0, 2, 0, 2},
            {2, 0, 2, 0, 2, 0, 2, 0}
    };
}
