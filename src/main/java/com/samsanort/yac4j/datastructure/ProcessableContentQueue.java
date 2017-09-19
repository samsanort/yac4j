package com.samsanort.yac4j.datastructure;

import com.samsanort.yac4j.model.ProcessableContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

public class ProcessableContentQueue implements Queue<ProcessableContent> {

    private static final Logger logger = LoggerFactory.getLogger(ProcessableContentQueue.class);

    private LinkedBlockingQueue<ProcessableContent> queue_ = new LinkedBlockingQueue<>();

    @Override
    public void enqueue(ProcessableContent processableContent) {

        this.queue_.add(processableContent);

        logger.trace("New content queued, queue size is {}", queue_.size());
    }

    @Override
    public ProcessableContent dequeue() {

        ProcessableContent retVal = this.queue_.poll();

        if(retVal != null) {
            logger.trace("Content dequeued, queue size is {}", queue_.size());
        }

        return retVal;
    }

    @Override
    public boolean isEmpty() {

        return this.queue_.isEmpty();
    }
}
