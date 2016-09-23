package com.memory.database;


/**
 * Created by yetaihang on 9/16/16.
 */
public interface MemoryDBTransaction {

    void multi();

    void exec();

    void rollBack();
}
