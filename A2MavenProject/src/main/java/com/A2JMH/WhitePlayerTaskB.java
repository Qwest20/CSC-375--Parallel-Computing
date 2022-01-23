package com.A2JMH;

import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

// thread code for a singular white player
public class WhitePlayerTaskB extends Thread {

    // private variables
    private final String name;
    private final WatchPlayLock wPL;
    private final Phaser ph;

    // constructor
    public WhitePlayerTaskB(String n, WatchPlayLock w, Phaser phB) {
        name = n;
        wPL = w;
        ph = phB;
        ph.register();
    }

    // run method
    @Override
    public void run() {
        wPL.lockPlay();
        try {

            boolean moved = false;
            while (!moved) {
                // i goes from 0 to 3
                outer:
                for (int i = 0; i < 4; i++) {
                    // j goes from 0 to 7
                    for (int j = 0; j < BoardB.checkersGame[0].length; j++) {
                        // we can manipulate the piece
                        if (BoardB.checkersGame[i][j] == 1) {
                            // a 0 or 1 to see if this piece will be moved or not
                            int movesThisPiece = ThreadLocalRandom.current().nextInt(0, 2);
                            // we will move this one
                            if (movesThisPiece == 1) {
                                // booleans for movement privileges
                                boolean up = true, down = true, right = true, left = true;
                                if (i == 0 || BoardB.checkersGame[i - 1][j] != 0)
                                    up = false;
                                if (i == 3 || BoardB.checkersGame[i + 1][j] != 0)
                                    down = false;
                                if (j == 7 || BoardB.checkersGame[i][j + 1] != 0)
                                    right = false;
                                if (j == 0 || BoardB.checkersGame[i][j - 1] != 0)
                                    left = false;

                                // random value chosen to decide movement direction: 0-3 for the four directions
                                int dir = ThreadLocalRandom.current().nextInt(0, 4);

                                // movement checks that align the desired move direction with movement capability
                                if (dir == 0 && up) {
                                    // swap this piece for the empty space above
                                    int temp = BoardB.checkersGame[i][j];
                                    BoardB.checkersGame[i][j] = BoardB.checkersGame[i - 1][j];
                                    BoardB.checkersGame[i - 1][j] = temp;
                                    moved = true;
                                    break outer;
                                }
                                if (dir == 1 && down) {
                                    // swap this piece for the empty space below
                                    int temp = BoardB.checkersGame[i][j];
                                    BoardB.checkersGame[i][j] = BoardB.checkersGame[i + 1][j];
                                    BoardB.checkersGame[i + 1][j] = temp;
                                    moved = true;
                                    break outer;
                                }
                                if (dir == 2 && right) {
                                    // swap this piece for the empty space to the right
                                    int temp = BoardB.checkersGame[i][j];
                                    BoardB.checkersGame[i][j] = BoardB.checkersGame[i][j + 1];
                                    BoardB.checkersGame[i][j + 1] = temp;
                                    moved = true;
                                    break outer;
                                }
                                if (dir == 3 && left) {
                                    // swap this piece for the empty space to the left
                                    int temp = BoardB.checkersGame[i][j];
                                    BoardB.checkersGame[i][j] = BoardB.checkersGame[i][j - 1];
                                    BoardB.checkersGame[i][j - 1] = temp;
                                    moved = true;
                                    break outer;
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            wPL.unlockPlay();
            ph.arriveAndAwaitAdvance();
            ph.arriveAndDeregister();
        }
    }
}
