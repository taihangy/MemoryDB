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
        for (int i = 0; i < 5; i++)
            mdb.set("k" + i, "v" + i);
        System.out.println("After setting up: " + mdb);
    }
//
//    @Test
//    public void testSet() {
//        System.out.println("=========================================");
//        System.out.println("Set test start...");
//        mdb.set("k1", "v1");
//        System.out.println("After reset k1, v1: " + mdb);
//
//        mdb.set("k4", "v4");
//        System.out.println("After reset k4, v4: " + mdb);
//
//        mdb.set("k6", "v6");
//        System.out.println("After reset k6, v6: " + mdb);
//    }
//
//    @Test
//    public void testGet() {
//        System.out.println("=========================================");
//        System.out.println("Get test start...");
//        mdb.get("k2");
//        System.out.println("After getting k2: " + mdb);
//
//        mdb.get("k4");
//        System.out.println("After getting k4: " + mdb);
//
//        mdb.get("k6");
//        System.out.println("After getting k6: " + mdb);
//    }
//
//    @Test
//    public void testRemove() {
//        System.out.println("=========================================");
//        System.out.println("Remove test start...");
//        mdb.remove("k1");
//        System.out.println("After getting k1: " + mdb);
//
//        mdb.remove("k3");
//        System.out.println("After getting k3: " + mdb);
//
//        mdb.remove("k4");
//        System.out.println("After getting k4: " + mdb);
//
//        mdb.remove("k6");
//        System.out.println("After getting k6: " + mdb);
//    }

    @Test
    public void testMultiAndExec() {
        System.out.println("=========================================");
        System.out.println("Multi & Exec test start...");

        mdb.multi();
        mdb.set("k5", "v5");
        mdb.get("k2");
        mdb.remove("k4");
        mdb.exec();
        System.out.println("After transaction set k5 v5; get k2; remove k4: " + mdb);

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
        System.out.println("After transaction set k5 v5; get k2; remove k4: " + mdb);

        mdb.set("k6", "v6");
        System.out.println("After set k6 v6: " + mdb);
    }
}