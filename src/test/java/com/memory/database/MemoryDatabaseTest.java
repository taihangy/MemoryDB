package com.memory.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yetaihang on 9/14/16.
 */
public class MemoryDatabaseTest {
    private MemoryDatabase<String, String> mdb;
    private static final int TIME_TO_LIVE = 10;
    private static final int CLEAN_UP_INTERVAL = 10;
    private static final int DB_CAPACITY = 20;

    @Before
    public void setUp() throws Exception {
        mdb = new MemoryDatabase<String, String>(TIME_TO_LIVE, CLEAN_UP_INTERVAL, DB_CAPACITY);
    }

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            mdb.cacheMap.set("testKey" + i, "testValue" + i);
        }
        System.out.println("After adding first ten items: \n" + mdb.cacheMap);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 10; i < 20; i++)
            mdb.cacheMap.set("testKey" + i, "testValue" + i);
        System.out.println("After adding second ten items: \n" + mdb.cacheMap);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(mdb.cacheMap);
    }

}