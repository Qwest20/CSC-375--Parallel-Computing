package com.A2JMH;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// WatchPLay Lock class that we establish for ourselves
public class WatchPlayLock {

    final ReentrantLock lock = new ReentrantLock();
    final Condition watchable = lock.newCondition();
    final Condition playable = lock.newCondition();
    int watchers, players, waitingWatchers, waitingPlayers;

    void lockWatch() {
        lock.lock();
        try {
            while (players != 0) {
                ++waitingWatchers;
                watchable.awaitUninterruptibly();
                --waitingWatchers;
            }
            ++watchers;
        } finally {
            lock.unlock();
        }
    }

    void lockPlay() {
        lock.lock();
        try {
            // in our version of the ReadWriteLock, we don't require that only one writer can go at a time.
            // We allow multiple since we only have 2 writers (players) who can't affect one another (see BoardA.java for an explanation on this)
            while (watchers != 0) {
                ++waitingPlayers;
                playable.awaitUninterruptibly();
                --waitingPlayers;
            }
            players++;
        } finally {
            lock.unlock();
        }
    }

    void unlockWatch() {
        lock.lock();
        try {
            if (--watchers == 0) {
                if (waitingPlayers != 0) {
                    playable.signalAll();
                } else {
                    watchable.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    void unlockPlay() {
        lock.lock();
        try {
            if (--players <= 1) {
                if (waitingWatchers != 0) {
                    watchable.signalAll();
                } else {
                    playable.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
