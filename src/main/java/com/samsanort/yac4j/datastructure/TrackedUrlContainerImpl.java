package com.samsanort.yac4j.datastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by samu on 3/4/17.
 */
public class TrackedUrlContainerImpl implements TrackedUrlContainer {

    private static final Logger logger = LoggerFactory.getLogger(TrackedUrlContainerImpl.class);

    private Set<String> visitableUrls = new HashSet();
    private Set<String> visitedUrls = new HashSet();

    private int maxVisits;

    private int dequeuedVisitableUrls = 0;

    /**
     * @param maxVisits
     * @param seeds
     */
    public TrackedUrlContainerImpl(int maxVisits, List<String> seeds) {

        this(maxVisits, seeds, new HashSet<String>(), new HashSet<String>());
    }

    TrackedUrlContainerImpl(int maxVisits, List<String> seeds, Set<String> visitableUrls, Set<String> visitedUrls) {

        this.maxVisits = maxVisits;
        this.visitableUrls = visitableUrls;
        this.visitedUrls = visitedUrls;

        if (seeds != null) {
            for (String url : seeds) {
                this.addVisitableUrl(url);
            }
        }
    }

    public synchronized void addVisitableUrl(String url) {

        if (this.maxVisits > 0) {

            if (maxDequeuesReached()) {

                logger.debug(
                        "Visitable site not added: {} visitable URLs already dequeued (limit = {})",
                        this.visitableUrls.size(),
                        this.maxVisits);

                return;
            }
        }

        this.visitableUrls.add(url);

        logger.debug(
                "Added new visitable site (registered: {}, dequeued: {}, limit: {}).",
                this.visitableUrls.size(),
                this.dequeuedVisitableUrls,
                this.maxVisits);
    }

    public synchronized void addVisitedUrl(String url) {

        this.visitedUrls.add(url);

        logger.trace(
                "Added new visited site (registered: {}, dequeued: {}, limit: {}).",
                this.visitedUrls.size(),
                this.dequeuedVisitableUrls,
                this.maxVisits);
    }

    public synchronized String nextVisitableUrl() {

        if (!this.visitableUrls.isEmpty()) {

            String url = this.visitableUrls.iterator().next();
            this.visitableUrls.remove(url);

            increaseNumOfDequeuedUrls();

            logger.trace(
                    "Returning URL {}, {} still available.",
                    url,
                    this.visitedUrls.size());

            return url;
        }

        return null;
    }

    public synchronized boolean isAlreadyVisited(String url) {

        return this.visitedUrls.contains(url);
    }

    public synchronized boolean isVisitAlreadyScheduled(String url) {

        return this.visitableUrls.contains(url);
    }

    public synchronized boolean willReturnVisitableUrl() {

        if (this.visitableUrls.isEmpty()) {

            logger.trace("No more visitable URLs.");
            return false;
        }

        return true;
    }

    boolean maxDequeuesReached() {
        return this.dequeuedVisitableUrls >= this.maxVisits;
    }

    void increaseNumOfDequeuedUrls() {
        this.dequeuedVisitableUrls++;
    }
}
