package com.memory.database;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yetaihang on 9/14/16.
 */
public class MemoryDatabase<K extends Comparable<K>, V> {
    protected static Timer cleanUpTimer;
    protected int timeToLive;
    protected int cleanUpInterval;
    protected int capacity;
    protected LRUMap<K, V> cacheMap;

    public MemoryDatabase(int timeToLive, int cleanUpInterval, int capacity) {
        this.timeToLive = timeToLive * 1000;
        this.cleanUpInterval = cleanUpInterval * 1000;
        this.capacity = capacity;
        cacheMap = new LRUMap<K, V>(capacity);
        cleanUpTimer = new Timer();

        cleanUpTimer.schedule(new CleanUpTimerTask(), this.timeToLive, this.cleanUpInterval);
    }

    public V get(K key) {
        return cacheMap.get(key);
    }

    public void set(K key, V value) {
        cacheMap.set(key, value);
    }

    public class CleanUpTimerTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Start clean up task...");
            long cleanUpCount = cleanUp();
            System.out.println("End clean up task, cleaned " + cleanUpCount +" items...");
        }

        private long cleanUp() {
            if (cacheMap.size() < capacity / 2) return 0;
            long now = System.currentTimeMillis();
            long cleanUpCount = 0l;

            System.out.println("Current time: " + now);
            synchronized (cacheMap) {
                Node<K, V> node = cacheMap.tail;
                while (node != null && node.lastAccessed + timeToLive < now ) {
                    System.out.println("node is dead for " + (now - node.lastAccessed - timeToLive) + " ms.");
                    cacheMap.removeTail();
                    cleanUpCount++;
                    node = node.prev;
                }
            }
            return cleanUpCount;
        }
    }



}
