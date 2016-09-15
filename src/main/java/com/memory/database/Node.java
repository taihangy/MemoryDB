package com.memory.database;

/**
 * Created by yetaihang on 9/14/16.
 */
public class Node<K extends Comparable<K>, V> {
    protected Node prev;
    protected Node next;
    protected K key;
    protected V value;
    protected long lastAccessed;
    protected Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.lastAccessed = System.currentTimeMillis();
    }

    public String toString() {
        return "[" + key.toString() + ", " + value.toString() + ", " + lastAccessed + " ]";
    }
}
