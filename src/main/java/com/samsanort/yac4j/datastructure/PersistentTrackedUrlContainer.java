package com.samsanort.yac4j.datastructure;

import com.samsanort.yac4j.UrlRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersistentTrackedUrlContainer implements TrackedUrlContainer {

    private UrlRepository urlRepository;

    private Set<String> visitableUrls = new HashSet<>();
    private Set<String> visitedUrls = new HashSet<>();

    public PersistentTrackedUrlContainer(List<String> seeds, UrlRepository urlRepository){

        this(seeds, urlRepository, null, null);
    }

    PersistentTrackedUrlContainer(
            List<String> seeds,
            UrlRepository urlRepository,
            Set<String> visitableUrls,
            Set<String> visitedUrls){

        this.visitableUrls = visitableUrls!=null ?visitableUrls :new HashSet<>();
        this.visitedUrls = visitedUrls!=null ?visitedUrls :new HashSet<>();

        this.urlRepository = urlRepository;

        initCache(seeds);
    }

    private void initCache(List<String> seeds) {

        this.visitableUrls.addAll(this.urlRepository.getAllVisitable());
        this.visitedUrls.addAll(this.urlRepository.getAllVisited());

        seeds.forEach(this::addVisitableUrl);
    }

    @Override
    public synchronized void addVisitableUrl(String url) {

        this.visitableUrls.add(url);
        this.urlRepository.addVisitable(url);
    }

    @Override
    public synchronized void addVisitedUrl(String url) {

        this.visitedUrls.add(url);
        this.urlRepository.addVisited(url);
    }

    @Override
    public synchronized String nextVisitableUrl() {

        String url = this.visitableUrls.iterator().next();

        this.visitableUrls.remove(url);
        this.urlRepository.deleteVisitable(url);

        return url;
    }

    @Override
    public synchronized boolean isAlreadyVisited(String url) {

        return this.visitedUrls.contains(url);
    }

    @Override
    public synchronized boolean isVisitAlreadyScheduled(String url) {
        return this.visitableUrls.contains(url);
    }

    @Override
    public synchronized boolean willReturnVisitableUrl() {

        return !(this.visitableUrls.isEmpty());
    }
}
