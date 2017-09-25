package com.samsanort.yac4j.process;

import com.samsanort.yac4j.PageProcessor;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.model.ProcessableContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by samu on 4/6/17.
 */
public class Processor implements Cycle {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private Queue<ProcessableContent> processableContentQueue;
    private PageProcessor pageProcessor;
    private long delay;

    public Processor(
            Queue<ProcessableContent> processableContentQueue,
            PageProcessor pageProcessor,
            long delay) {

        this.processableContentQueue = processableContentQueue;
        this.pageProcessor = pageProcessor;
        this.delay = delay;
    }

    @Override
    public void runCycle() {

        try {

            ProcessableContent processableContent = this.processableContentQueue.dequeue();

            if (processableContent != null) {

                this.pageProcessor.process(processableContent.getContent());
            }

        } catch (Exception e) {
            logger.error("Processor cycle error.", e);
        }
    }

    @Override
    public long getDelay() {
        return this.delay;
    }
}
