package com.samsanort.yac4j.process;

import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.model.ProcessableContent;
import com.samsanort.yac4j.service.FetchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by samu on 4/6/17.
 */
public class Fetcher implements Cycle {

    private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

    private FetchService fetchService;
    private TrackedUrlContainer trackedUrls;
    private Queue<ProcessableContent> processableContentQueue;
    private long delay;

    /**
     * @param fetchService
     * @param trackedUrls
     * @param processableContentQueue
     * @param delay
     */
    public Fetcher(
            FetchService fetchService,
            TrackedUrlContainer trackedUrls,
            Queue<ProcessableContent> processableContentQueue,
            long delay) {

        this.fetchService = fetchService;
        this.trackedUrls = trackedUrls;
        this.processableContentQueue = processableContentQueue;
        this.delay = delay;
    }

    @Override
    public void runCycle() {

        String url = this.trackedUrls.nextVisitableUrl();

        if (url != null) {

            String content = this.fetchService.fetchURLContent(url);

            if (content != null && !content.isEmpty()) {
                this.processableContentQueue.enqueue(new ProcessableContent(url, content));
            }

            this.trackedUrls.addVisitedUrl(url);
        }
    }

    @Override
    public long getDelay() {
        return this.delay;
    }
}
