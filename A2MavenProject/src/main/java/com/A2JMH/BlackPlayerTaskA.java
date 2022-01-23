package com.A2JMH;

import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

// thread code for a singular black player
// mimic the white player, but for the black player's logic
public class BlackPlayerTaskA extends Thread {

    // private variables
    private final String name;
    private final ReentrantLock lock;
    private final Phaser ph;

    // constructor
    public BlackPlayerTaskA(String n, ReentrantLock r, Phaser phA) {
        name = n;
        lock = r;
        ph = phA;
        ph.register();
    }

    // run method
    @Override
    public void run() {

        lock.lock();
        try {

            boolean moved = false;
            while (!moved) {

                // i goes from 7 to 4
                outer:
                for (int i = BoardA.checkersGame.length - 1; i >= 4; i--) {
                    // j goes from 0 to 7
                    for (int j = 0; j < BoardA.checkersGame[0].length; j++) {
                        // if we can manipulate the piece
                        if (BoardA.checkersGame[i][j] == 2) {
                            // a 0 or 1 to see if this piece will be moved or not
                            int movesThisPiece = ThreadLocalRandom.current().nextInt(0, 2);
                            // we will move this one
                            if (movesThisPiece == 1) {
                                // booleans for movement privileges
                                boolean up = true, down = true, right = true, left = true;
                                if (i == 4 || BoardA.checkersGame[i - 1][j] != 0)
                                    up = false;
                                if (i == 7 || BoardA.checkersGame[i + 1][j] != 0)
                                    down = false;
                                if (j == 7 || BoardA.checkersGame[i][j + 1] != 0)
                                    right = false;
                                if (j == 0 || BoardA.checkersGame[i][j - 1] != 0)
                                    left = false;

                                // random value chosen to decide movement direction: 0-3 for the four directions
                                int dir = ThreadLocalRandom.current().nextInt(0, 4);

                                // movement checks that align the desired move direction with movement capability
                                if (dir == 0 && up) {
                                    // swap this piece for the empty space above
                                    int temp = BoardA.checkersGame[i][j];
                                    BoardA.checkersGame[i][j] = BoardA.checkersGame[i - 1][j];
                                    BoardA.checkersGame[i - 1][j] = temp;
                                    moved = true;
                                    break outer;
                                }
                                if (dir == 1 && down) {
                                    // swap this piece for the empty space below
                                    int temp = BoardA.checkersGame[i][j];
                                    BoardA.checkersGame[i][j] = BoardA.checkersGame[i + 1][j];
                                    BoardA.checkersGame[i + 1][j] = temp;
                                    moved = true;
                                    break outer;
                                }
                                if (dir == 2 && right) {
                                    // swap this piece for the empty space to the right
                                    int temp = BoardA.checkersGame[i][j];
                                    BoardA.checkersGame[i][j] = BoardA.checkersGame[i][j + 1];
                                    BoardA.checkersGame[i][j + 1] = temp;
                                    moved = true;
                                    break outer;
                                }
                                if (dir == 3 && left) {
                                    // swap this piece for the empty space to the left
                                    int temp = BoardA.checkersGame[i][j];
                                    BoardA.checkersGame[i][j] = BoardA.checkersGame[i][j - 1];
                                    BoardA.checkersGame[i][j - 1] = temp;
                                    moved = true;
                                    break outer;
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
            ph.arriveAndAwaitAdvance();
            ph.arriveAndDeregister();
        }
    }
}
