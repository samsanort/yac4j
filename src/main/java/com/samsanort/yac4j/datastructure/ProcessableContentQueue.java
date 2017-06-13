package com.samsanort.yac4j.datastructure;

import com.samsanort.yac4j.model.ProcessableContent;

import java.util.concurrent.LinkedBlockingQueue;

public class ProcessableContentQueue implements Queue<ProcessableContent> {

    private LinkedBlockingQueue<ProcessableContent> queue_ = new LinkedBlockingQueue<>();

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
