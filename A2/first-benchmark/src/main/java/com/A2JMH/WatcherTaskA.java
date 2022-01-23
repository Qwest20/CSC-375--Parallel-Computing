package com.A2JMH;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

// thread code for a Watcher
// a Watcher thread will observe a random space on the board and make note of the space's status
public class WatcherTaskA extends Thread {

    // private variables
    private final String name;
    private final ReentrantLock lock;

    // constructor
    public WatcherTaskA(String n, ReentrantLock r) {
        name = n;
        lock = r;
    }

    // run method
    @Override
    public void run() {
        lock.lock();
        try {
            int v = ThreadLocalRandom.current().nextInt(0, 8);
            int h = ThreadLocalRandom.current().nextInt(0, 8);
            int spaceStatus = BoardA.checkersGame[v][h];
	    // Space status key: 0 = empty, 1 = white, 2 = black
            if (spaceStatus !=0) {
                String color = "black";
                if (spaceStatus == 1)
                    color = "white";
            }
        } finally {
            lock.unlock();
        }
    }
}
