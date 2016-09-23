package com.memory.database;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yetaihang on 9/14/16.
 */
public class MemoryDatabase<K extends Comparable<K>, V> implements MemoryDBTransaction {
    protected static Timer cleanUpTimer;
    protected int timeToLive;
    protected int cleanUpInterval;
    protected int capacity;
    protected LRUMap<K, V> cacheMap;
    protected List<Task<K, V>> taskQueue;
    protected List<V> resultQueue;
    protected Thread executionTread;

    private Enum state;

    public MemoryDatabase(int timeToLive, int cleanUpInterval, int capacity) {
        this.timeToLive = timeToLive * 1000;
        this.cleanUpInterval = cleanUpInterval * 1000;
        this.capacity = capacity;
        this.state = MemoryDatabaseStates.NORMAL;
        this.taskQueue = new ArrayList<Task<K, V>>();
        this.resultQueue = new ArrayList<V>();
        cacheMap = new LRUMap<K, V>(capacity);
        cleanUpTimer = new Timer();
        cleanUpTimer.schedule(new CleanUpTimerTask(), this.timeToLive, this.cleanUpInterval);
        executionTread = new Thread(new ExecutionThread<K, V>(taskQueue, resultQueue, cacheMap));
        executionTread.start();
    }

    public V get(K key) {
        Task<K, V> task = new Task<K, V>(new Node<K, V>(key, null), "get", state);
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
        V value = null;
        synchronized (resultQueue) {
            while (resultQueue.isEmpty() && state.compareTo(MemoryDatabaseStates.NORMAL) == 0) {
                try {
                    resultQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (state.compareTo(MemoryDatabaseStates.NORMAL) == 0) {
                value = resultQueue.get(resultQueue.size() - 1);
                resultQueue.clear();
            }
        }
        return value;
    }

    public void set(K key, V value) {
        Task<K, V> task = new Task<K, V>(new Node<K, V>(key, value), "set", state);
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
        synchronized (resultQueue) {
            while (resultQueue.isEmpty() && state.compareTo(MemoryDatabaseStates.NORMAL) == 0) {
                try {
                    resultQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (state.compareTo(MemoryDatabaseStates.NORMAL) == 0)
                resultQueue.clear();
        }
    }

    public void remove(K key) {
        Task<K, V> task = new Task<K, V>(new Node<K, V>(key, null), "remove", state);
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
        synchronized (resultQueue) {
            while (resultQueue.isEmpty() && state.compareTo(MemoryDatabaseStates.NORMAL) == 0) {
                try {
                    resultQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (state.compareTo(MemoryDatabaseStates.NORMAL) == 0)
                resultQueue.clear();
        }
    }

    public void multi() {
        if (!taskQueue.isEmpty() && taskQueue.get(0).getOp().equalsIgnoreCase("multi")) {
            System.out.println("Please use set, find, remove, exec or rollback command.");
            return;
        }
        System.out.println("Start transaction...");
        state = MemoryDatabaseStates.TRANSACTION_START;
        Task<K, V> task = new Task<K, V>(new Node<K, V>(null, null), "multi", state);
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
    }

    public void exec() {
        if (taskQueue.isEmpty() || !taskQueue.get(0).getOp().equalsIgnoreCase("multi")) {
            System.out.println("Please use multi before exec.");
            return;
        }
        state = MemoryDatabaseStates.TRANSACTION_EXECUTE;
        Task<K, V> task = new Task<K, V>(new Node<K, V>(null, null), "exec", state);
        List<String> taskPrintList = null;
        synchronized (taskQueue) {
            if (taskQueue.size() == 1 && taskQueue.get(0).getOp().equalsIgnoreCase("multi")) {
                taskQueue.clear();
                return;
            }
            System.out.println("Start execution...");
            taskQueue.add(task);
            taskPrintList = taskQueue.stream().filter(t -> !t.getOp().equalsIgnoreCase("multi") && !t.getOp().equalsIgnoreCase("exec"))
                    .map(Task::toString).collect(Collectors.toList());
            taskQueue.notifyAll();
        }
        synchronized (resultQueue) {
            while (resultQueue.isEmpty()) {
                try {
                    resultQueue.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // print out results after executing transaction
            for (int i = 0; i < resultQueue.size(); i++) {
                System.out.println("> After exec, result for task " + taskPrintList.get(i) + ": " + resultQueue.get(i));
            }
            //TODO: we can choose to print here
            resultQueue.clear();
        }
        state = MemoryDatabaseStates.NORMAL;
    }

    public void rollBack() {
        if (taskQueue.isEmpty() || !taskQueue.get(0).getOp().equalsIgnoreCase("multi")) {
            System.out.println("Please use multi before rollback.");
            return;
        }
        System.out.println("Start rollback...");
        state = MemoryDatabaseStates.TRANSACTION_ROLL_BACK;
        Task<K, V> task = new Task<K, V>(new Node<K, V>(null, null), "rollback", state);
        synchronized (taskQueue) {
            taskQueue.add(task);
            taskQueue.notifyAll();
        }
        synchronized (resultQueue) {
            resultQueue.clear();
        }
        state = MemoryDatabaseStates.NORMAL;
    }

    public class ExecutionThread<K extends Comparable<K>, V> implements Runnable {
        private List<Task<K, V>> taskQueue;
        private List<V> resultQueue;
        private LRUMap<K, V> cacheMap;

        public ExecutionThread(List<Task<K, V>> taskQueue, List<V> resultQueue, LRUMap<K, V> cacheMap) {
            this.taskQueue = taskQueue;
            this.resultQueue = resultQueue;
            this.cacheMap = cacheMap;
        }

        public void run() {
//            System.out.println("> Start checking task queue...");
            while (true) {
                synchronized (taskQueue) {
//                    System.out.println("> Size of task queue: " + taskQueue.size());

                    while (taskQueue.isEmpty() ||
                            taskQueue.get(taskQueue.size() - 1).getState().compareTo(MemoryDatabaseStates.TRANSACTION_START) == 0) {
//                        if (!taskQueue.isEmpty())
//                            System.out.println("> Is the latest task still in a transaction? "
//                                    + (taskQueue.get(0).getState().compareTo(MemoryDatabaseStates.TRANSACTION_START) == 0));
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!taskQueue.isEmpty() &&
                            taskQueue.get(taskQueue.size() - 1).getState().compareTo(MemoryDatabaseStates.TRANSACTION_EXECUTE) == 0) {
//                        System.out.println("> Start executing task queue...");
                        execute();
                    }
                    else if (!taskQueue.isEmpty() &&
                            taskQueue.get(taskQueue.size() - 1).getState().compareTo(MemoryDatabaseStates.TRANSACTION_ROLL_BACK) == 0) {
//                        System.out.println("> Start roll back all the task in the task queue...");
                    }
                    else if (!taskQueue.isEmpty() &&
                            taskQueue.get(taskQueue.size() - 1).getState().compareTo(MemoryDatabaseStates.NORMAL) == 0){
//                        System.out.println("> Start execute one task...");
                        execute();
                    }
                    taskQueue.clear();
                }
            }
        }

        private void execute() {
            synchronized (resultQueue) {
                for (int i = 0; i < taskQueue.size(); i++) {
                    Task<K, V> task = taskQueue.get(i);
//                    System.out.println("> Task " + i + " in taskQueue: " + task);
                    String op = task.getOp();
                    if (op.equalsIgnoreCase("multi")) continue;
                    Enum state = task.getState();
                    if (state.compareTo(MemoryDatabaseStates.NORMAL) == 0) {
                        V value = executeOneTask(task);
                        resultQueue.add(value);
                        resultQueue.notifyAll();
                        return;
                    }
                    else if (state.compareTo(MemoryDatabaseStates.TRANSACTION_START) == 0) {
                        V value = executeOneTask(task);
                        resultQueue.add(value);
                    }
                    else if (state.compareTo(MemoryDatabaseStates.TRANSACTION_EXECUTE) == 0) {
                        resultQueue.notifyAll();
                        return;
                    }
                }
            }
        }

        private V executeOneTask(Task<K, V> task) {
            K key = task.getNode().getKey();
            String op = task.getOp();
            V value = null;
            if (op.equalsIgnoreCase("set")) {
                value = task.getNode().getValue();
                cacheMap.set(key, value);
            } else if (op.equalsIgnoreCase("get")) {
                value = cacheMap.get(key);
            } else if (op.equalsIgnoreCase("remove")) {
                cacheMap.remove(key);
            }
            return value;
        }

    }

    public class CleanUpTimerTask extends TimerTask {
        @Override
        public void run() {
//            System.out.println("Start clean up task...");
            long cleanUpCount = cleanUp();
//            System.out.println("End clean up task, cleaned " + cleanUpCount +" items...");
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

    public String toString() {
        return cacheMap.toString();
    }
}
