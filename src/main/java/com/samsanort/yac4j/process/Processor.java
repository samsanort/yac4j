package com.samsanort.yac4j.process;

import com.samsanort.yac4j.PageProcessor;
import com.samsanort.yac4j.UrlEvaluator;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.model.Link;
import com.samsanort.yac4j.model.PageWithLinks;
import com.samsanort.yac4j.model.ProcessableContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by samu on 4/6/17.
 */
public class Processor implements Cycle {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private Queue<ProcessableContent> processableContentQueue;
    private TrackedUrlContainer trackedUrls;
    private PageProcessor pageProcessor;
    private UrlEvaluator urlEvaluator;
    private long delay;

    public Processor(
            Queue<ProcessableContent> processableContentQueue,
            TrackedUrlContainer trackedUrls,
            PageProcessor pageProcessor,
            UrlEvaluator urlEvaluator,
            long delay) {

        this.processableContentQueue = processableContentQueue;
        this.trackedUrls = trackedUrls;
        this.pageProcessor = pageProcessor;
        this.urlEvaluator = urlEvaluator;
        this.delay = delay;
    }

    @Override
    public void runCycle() {

        ProcessableContent processableContent = this.processableContentQueue.dequeue();

        if (processableContent != null) {

            extractLinks(processableContent);
            processContent(processableContent);
        }
    }

    private void extractLinks(ProcessableContent processableContent) {

        PageWithLinks pageWithLinks = new PageWithLinks(processableContent.getUrl(), processableContent.getContent());
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

    private void processContent(ProcessableContent processableContent) {

        this.pageProcessor.process(processableContent.getContent());
    }

    @Override
    public long getDelay() {
        return this.delay;
    }
}
