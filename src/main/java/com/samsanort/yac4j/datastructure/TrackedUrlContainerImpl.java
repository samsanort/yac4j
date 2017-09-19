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

    /**
     * @param maxVisits
     */
    public TrackedUrlContainerImpl(int maxVisits) {
        this.maxVisits = maxVisits;
    }

    /**
     * @param maxVisits
     * @param seeds
     */
    public TrackedUrlContainerImpl(int maxVisits, List<String> seeds) {

        this(maxVisits);
        for (String url : seeds) {
            this.addVisitableUrl(url);
        }
    }

    public synchronized void addVisitableUrl(String url) {

        this.visitableUrls.add(url);

        logger.trace(
                "Added new visitable site, {} registered (limit = {})",
                this.visitableUrls.size(),
                this.maxVisits);
    }

    public synchronized void addVisitedUrl(String url) {

        this.visitedUrls.add(url);

        logger.trace(
                "Added new visited site, {} registered (limit = {})",
                this.visitedUrls.size(),
                this.maxVisits);
    }

    public synchronized String nextVisitableUrl() {

        if (willReturnVisitableUrl()) {

            String url = this.visitableUrls.iterator().next();
            this.visitableUrls.remove(url);

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

        if (this.visitedUrls.size() >= this.maxVisits) {

            logger.trace(
                    "Size of visited URLs ({}) exceeds max allowed ({}).",
                    this.visitedUrls.size(),
                    this.maxVisits);

            return false;
        }

        return true;
    }
}
