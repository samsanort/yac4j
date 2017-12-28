package com.samsanort.yac4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samu on 2/26/17.
 */
public class CrawlerConfig {

    private ArrayList<String> seeds = new ArrayList();
    private int maxFetches = -1;
    private long visitDelay = 500L;
    private String proxyAddress;
    private int proxyPort = UNASSIGNED;
    private int workers = 1;
    private String userAgent = "yac4j";

    public static final int UNASSIGNED = -1;

    /**
     * @param siteUrl
     */
    public CrawlerConfig(String siteUrl) {

        this.addSeed(siteUrl);
    }

    /**
     * @param url
     */
    public void addSeed(String url) {
        this.seeds.add(url);
    }

    /**
     * @return
     */
    public long getVisitDelay() {
        return visitDelay;
    }

    /**
     * @param visitDelay
     */
    public void setVisitDelay(long visitDelay) {
        this.visitDelay = visitDelay;
    }

    /**
     * @return
     */
    public int getMaxFetches() {
        return maxFetches;
    }

    /**
     * @param maxFetches
     */
    public void setMaxFetches(int maxFetches) {
        this.maxFetches = maxFetches;
    }

    /**
     * @return
     */
    public List<String> getSeeds() {
        return seeds;
    }

    /**
     * @return
     */
    public String getProxyAddress() {
        return proxyAddress;
    }

    /**
     * @param proxyAddress
     */
    public void setProxyAddress(String proxyAddress) {
        this.proxyAddress = proxyAddress;
    }

    /**
     * @return
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return
     */
    public int getWorkers() {
        return workers;
    }

    /**
     * @param workers
     */
    public void setWorkers(int workers) {
        this.workers = workers;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
