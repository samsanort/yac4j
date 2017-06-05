package com.samsanort.yac4j.datastructure;

/**
 * Created by samu on 4/7/17.
 */
public interface Queue<T> {

    /**
     * @param t
     */
    void enqueue(T t);

    /**
     * @return
     */
    T dequeue();

    /**
     * @return
     */
    boolean isEmpty();
}
