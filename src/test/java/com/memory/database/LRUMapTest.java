package com.memory.database;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yetaihang on 9/14/16.
 */
public class LRUMapTest {
    private LRUMap<String, String> map1;
    @Before
    public void setUp() throws Exception {
        map1 = new LRUMap<String, String>(10);
        for (int i = 1; i <= 10; i++)
            map1.set("testKey" + i, "testValue" + i);
    }

    @Test
    public void testGetAndSet() throws Exception {
        System.out.println("After setting ten items:\n" + map1);
        assertEquals(10, map1.size());
        assertEquals("testValue10", map1.head.value);

        map1.set("testKey11", "testValue11");
        System.out.println("After setting test 11 item:\n" + map1);
        assertEquals(10, map1.size());
        assertEquals("testValue11", map1.head.value);
        assertEquals(null, map1.get("testKey1"));

        map1.set("testKey12", "testValue12");
        System.out.println("After setting test 12 item:\n" + map1);
        assertEquals(10, map1.size());
        assertEquals("testValue12", map1.head.value);
        assertEquals(null, map1.get("testKey1"));
        assertEquals(null, map1.get("testKey2"));

        assertEquals("testValue3", map1.get("testKey3"));
        assertEquals("testValue3", map1.head.value);

        assertEquals("testValue9", map1.get("testKey9"));
        assertEquals("testValue9", map1.head.value);
    }

    @Test
    public void testRemove() throws Exception {
        map1.remove("testKey1");
        System.out.println("After removing test 1 item:\n" + map1);
        assertEquals(null, map1.get("testKey1"));
        assertEquals("testValue2", map1.tail.value);
        assertEquals("testValue10", map1.head.value);

        map1.remove("testKey10");
        System.out.println("After removing test 10 item:\n" + map1);
        assertEquals(null, map1.get("testKey10"));
        assertEquals("testValue2", map1.tail.value);
        assertEquals("testValue9", map1.head.value);

        map1.remove("testKey5");
        System.out.println("After removing test 5 item:\n" + map1);
        assertEquals(null, map1.get("testKey5"));
        assertEquals("testValue2", map1.tail.value);
        assertEquals("testValue9", map1.head.value);

        Node<String, String> node = map1.head;
        map1.remove(node.key);
        System.out.println("After removing head item:\n" + map1);
        assertEquals(null, map1.get(node.key));
        assertEquals("testValue2", map1.tail.value);
        assertEquals("testValue8", map1.head.value);

        Node<String, String> node1 = map1.tail;
        map1.remove(node1.key);
        System.out.println("After removing tail item:\n" + map1);
        assertEquals(null, map1.get(node.key));
        assertEquals("testValue3", map1.tail.value);
        assertEquals("testValue8", map1.head.value);

        for (int i = 3; i < 8; i++) {
            map1.remove("testKey" + i);
        }
        assertEquals("testValue8", map1.tail.value);
        assertEquals("testValue8", map1.head.value);

        map1.remove("testKey8");
        assertEquals(null, map1.tail);
        assertEquals(null, map1.head);
    }



}