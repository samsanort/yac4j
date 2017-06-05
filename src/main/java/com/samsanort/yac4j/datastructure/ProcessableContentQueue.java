package com.samsanort.yac4j.datastructure;

import com.samsanort.yac4j.model.ProcessableContent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by samu on 4/7/17.
 */
public class ProcessableContentQueue implements Queue<ProcessableContent> {

    private LinkedBlockingQueue<ProcessableContent> queue_;

    @Override
    public void enqueue(ProcessableContent processableContent) {
        this.queue_.add(processableContent);
    }

    @Override
    public ProcessableContent dequeue() {
        return this.queue_.poll();
    }

    @Override
    public boolean isEmpty() {
        return this.queue_.isEmpty();
    }
}
