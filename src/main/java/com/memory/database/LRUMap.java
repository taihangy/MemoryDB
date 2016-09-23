package com.memory.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by yetaihang on 9/14/16.
 */
public class LRUMap<K extends Comparable<K>, V> {
    protected final int capacity;
    protected int curCount;
    protected Node<K, V> head;
    protected Node<K, V> tail;
    protected Map<K, Node<K, V>> map;

    protected LRUMap(int capacity) {
        this.capacity = capacity;
        curCount = 0;
        map = new HashMap<K, Node<K, V>>();
    }

    protected V get(K key) {
        if (!map.containsKey(key)) return null;
        Node<K, V> node = map.get(key);
        move(node);
        return node.value;
    }

    protected synchronized void move(Node<K, V> node) {
        if(node == null || node == head) return;
        if(head == null || tail == null) {
            head = node;
            tail = node;
            return;
        }
        if(node.prev != null) node.prev.next = node.next;
        if(node.next != null) node.next.prev = node.prev;
        if(node == tail) tail = tail.prev;
        node.next = head;
        node.prev = null;
        head.prev = node;
        head = node;

        node.setLastAccessed();
    }

    protected void set(K key, V value) {
        Node<K, V> node = map.get(key);
        setNode(node, key, value);
    }

    private synchronized void setNode(Node<K, V> node, K key, V value) {
        if (node != null) {
            node.value = value;
        } else {
            node = new Node<K, V>(key, value);
            if (curCount < capacity) {
                curCount++;
            }
            else if(tail != null) {
                map.remove(tail.key);
                removeTail();
            }
        }
        move(node);
        map.put(key, node);
    }

    protected void remove(K key) {
        Node<K, V> node = map.get(key);
        removeNode(node);
    }

    private synchronized void removeNode(Node<K, V> node) {
        if (node == null) return;

        if (node == head)
            head = node.next;
        if (node == tail)
            tail = node.prev;
        if (node.prev != null)
            node.prev.next = node.next;
        if (node.next != null)
            node.next.prev = node.prev;

        node.next = null;
        node.prev = null;

        map.remove(node.key);
    }


    protected synchronized int size() {
        return map.size();
    }

    protected synchronized boolean isEmpty() {
        return map.size() == 0;
    }

    protected synchronized void removeTail() {
        if (tail == null) return;
        if(tail.prev != null) {
            tail.prev.next = null;
        } else {
            head = null;
        }
        tail = tail.prev;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder().append("{ ");
        Node<K, V> dummy = head;
        while (dummy != null) {
            sb.append(" ").append(dummy);
            dummy = dummy.next;
        }
        return sb.append(" }").toString();
    }
}
