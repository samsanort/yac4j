package com.samsanort.yac4j;

/**
 * Created by samu on 2/26/17.
 */
public interface UrlEvaluator {

    /**
     * Should the URL be visited for further analysis and/or
     * content processing?
     *
     * @param url
     * @return
     */
    boolean isVisitable(String url);

    /**
     * Should the page be processed?
     *
     * @param url
     * @return
     */
    boolean isProcessable(String url);
}
