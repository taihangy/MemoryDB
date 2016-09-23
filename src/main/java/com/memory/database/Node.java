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

    public Node getPrev() {
        return prev;
    }

    public Node getNext() {
        return next;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    protected Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.lastAccessed = System.currentTimeMillis();
    }

    protected long setLastAccessed() {
        lastAccessed = System.currentTimeMillis();
        return lastAccessed;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(key == null ? "NULL" : key.toString()).append(" ")
                        .append(value == null ? "NULL" : value.toString())
                        .append(" ]");
        return sb.toString();
    }
}
