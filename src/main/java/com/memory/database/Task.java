package com.memory.database;

/**
 * Created by yetaihang on 9/16/16.
 */
public class Task<K extends Comparable<K>, V> {
    Node<K, V> node;
    String op;
    Enum state;

    public Task(Node<K, V> node, String op, Enum state) {
        this.node = node;
        this.op = op;
        this.state = state;
    }

    public Node<K, V> getNode() {
        return node;
    }

    public void setNode(Node<K, V> node) {
        this.node = node;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Enum getState() {
        return state;
    }

    public void setState(Enum state) {
        this.state = state;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ").append(node).append("/").append(op).append("/").append(state.name()).append(" }");
        return sb.toString();
    }
}
