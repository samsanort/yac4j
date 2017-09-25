package com.samsanort.yac4j.process;

import com.samsanort.yac4j.UrlEvaluator;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.model.Link;
import com.samsanort.yac4j.model.PageWithLinks;
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
    private UrlEvaluator urlEvaluator;
    private long delay;

    /**
     * @param fetchService
     * @param trackedUrls
     * @param processableContentQueue
     * @param urlEvaluator
     * @param delay
     */
    public Fetcher(
            FetchService fetchService,
            TrackedUrlContainer trackedUrls,
            Queue<ProcessableContent> processableContentQueue,
            UrlEvaluator urlEvaluator,
            long delay) {

        this.fetchService = fetchService;
        this.trackedUrls = trackedUrls;
        this.processableContentQueue = processableContentQueue;
        this.urlEvaluator = urlEvaluator;
        this.delay = delay;
    }

    @Override
    public void runCycle() {

        try {

            String url = this.trackedUrls.nextVisitableUrl();

            if (url != null) {

                visit(url);

                this.trackedUrls.addVisitedUrl(url);
            }

        } catch (Exception e) {
            logger.error("Fetcher cycle error.", e);
        }
    }

    private void visit(String url) {

        String content = this.fetchService.fetchURLContent(url);

        if (content != null && !content.isEmpty()) {

            extractLinks(url, content);

            if (urlEvaluator.isProcessable(url)) {
                this.processableContentQueue.enqueue(new ProcessableContent(url, content));
            }
        }
    }

    private void extractLinks(String url, String content) {

        PageWithLinks pageWithLinks = new PageWithLinks(url, content);
        logger.debug("Analysing page with {} links.", pageWithLinks.getLinkedUrls().size());

        for (Link linkedUrl : pageWithLinks.getLinkedUrls()) {

            if (shouldBeAddedIntoVisitableSet(linkedUrl.getAbsoluteUrl())) {

                this.trackedUrls.addVisitableUrl(linkedUrl.getAbsoluteUrl());
                logger.trace("URL {} added to the list of visitable URLs.", linkedUrl.getAbsoluteUrl());
            }
        }
    }

    private boolean shouldBeAddedIntoVisitableSet(String linkedUrl) {

        boolean visited = this.trackedUrls.isAlreadyVisited(linkedUrl);
        boolean scheduled = this.trackedUrls.isVisitAlreadyScheduled(linkedUrl);
        boolean visitable = this.urlEvaluator.isVisitable(linkedUrl);

        logger.trace("Is {} visitable? {}", linkedUrl, (visitable && (!visited && !scheduled)));

        return (visitable && (!visited && !scheduled));
    }

    @Override
    public long getDelay() {
        return this.delay;
    }
}
