package com.samsanort.yac4j.datastructure;

/**
 * Created by samu on 4/4/17.
 */
public interface TrackedUrlContainer {

    /**
     * @param url
     */
    void addVisitableUrl(String url);

    /**
     * @param url
     */
    void addVisitedUrl(String url);

    /**
     * @return
     */
    String nextVisitableUrl();

    /**
     * @param url
     * @return
     */
    boolean isAlreadyVisited(String url);

    /**
     * @param url
     * @return
     */
    boolean isVisitAlreadyScheduled(String url);

    /**
     * @return
     */
    boolean willReturnVisitableUrl();
}
