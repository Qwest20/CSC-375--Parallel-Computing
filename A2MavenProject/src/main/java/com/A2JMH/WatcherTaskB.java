package com.A2JMH;

import java.util.concurrent.ThreadLocalRandom;

// a watcher thread will observe a random space on the board
public class WatcherTaskB extends Thread {

    // private variables
    private final String name;
    private final WatchPlayLock wPL;

    // constructor
    public WatcherTaskB(String n, WatchPlayLock w) {
        name = n;
        wPL = w;
    }

    // run method
    @Override
    public void run() {
        wPL.lockWatch();
        try {
            int v = ThreadLocalRandom.current().nextInt(0, 8);
            int h = ThreadLocalRandom.current().nextInt(0, 8);
            int spaceStatus = BoardB.checkersGame[v][h];
            if (spaceStatus !=0) {
                String color = "black";
                if (spaceStatus == 1)
                    color = "white";
            }
        } finally {
            wPL.unlockWatch();
        }
    }
}
