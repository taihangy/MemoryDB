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
    private static final int DB_CAPACITY = 50;

    @Before
    public void setUp() throws Exception {
        mdb = new MemoryDatabase<String, String>(TIME_TO_LIVE, CLEAN_UP_INTERVAL, DB_CAPACITY);
        for (int i = 0; i < 50; i++)
            mdb.set("k" + i, "v" + i);
        System.out.println("After setting up: " + mdb);
    }

    @Test
    public void testSet() {
        System.out.println("=========================================");
        System.out.println("Set test start...");
        for (int i = 0; i < 50; i++) {
            mdb.set("k" + i, "v" + i);
            assertTrue(mdb.cacheMap.head.getKey().equals("k" + i));
            assertTrue(mdb.cacheMap.head.getValue().equals("v" + i));
        }
    }

    @Test
    public void testGet() {
        System.out.println("=========================================");
        System.out.println("Get test start...");
        for (int i = 0; i < 50; i++) {
            mdb.get("k" + i);
            assertTrue(mdb.cacheMap.head.getKey().equals("k" + i));
            assertTrue(mdb.cacheMap.head.getValue().equals("v" + i));
        }
        for (int i = 50; i < 60; i++) {
            mdb.get("k" + i);
            assertTrue(mdb.cacheMap.head.getKey().equals("k" + 49));
            assertTrue(mdb.cacheMap.head.getValue().equals("v" + 49));
        }
    }

    @Test
    public void testRemove() {
        System.out.println("=========================================");
        System.out.println("Remove test start...");
        for (int i = 0; i < 49; i++) {
            mdb.remove("k" + i);
            assertTrue(mdb.cacheMap.tail.getKey().equals("k" + (i + 1)));
            assertTrue(mdb.cacheMap.tail.getValue().equals("v" + (i + 1)));
        }
    }

    @Test
    public void testMultiAndExec() {
        System.out.println("=========================================");
        System.out.println("Multi & Exec test start...");

        mdb.multi();
        mdb.set("k5", "v5");
        mdb.get("k2");
        mdb.remove("k4");
        mdb.exec();
        assertTrue(mdb.cacheMap.head.getValue().equals("v2"));
        assertTrue(mdb.cacheMap.head.next.getValue().equals("v5"));
        assertTrue(mdb.cacheMap.map.get("k4") == null);
//        System.out.println("After transaction set k5 v5; get k2; remove k4: " + mdb);

        mdb.set("k6", "v6");
        System.out.println("After set k6 v6: " + mdb);
    }

    @Test
    public void testMultiAndRollBack() {
        System.out.println("=========================================");
        System.out.println("Multi & Rollback test start...");

        mdb.multi();
        mdb.set("k5", "v5");
        mdb.get("k2");
        mdb.remove("k4");
        mdb.rollBack();
        assertTrue(mdb.cacheMap.head.getValue().equals("v49"));
        assertTrue(mdb.cacheMap.head.next.getValue().equals("v48"));
        System.out.println("After transaction set k5 v5; get k2; remove k4: " + mdb);

        for (int i = 0; i < 50; i++) {
            mdb.set("k" + i, "v" + i);
            assertTrue(mdb.cacheMap.head.getKey().equals("k" + i));
            assertTrue(mdb.cacheMap.head.getValue().equals("v" + i));
        }
    }
}