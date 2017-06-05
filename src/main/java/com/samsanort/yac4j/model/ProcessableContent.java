package com.samsanort.yac4j.model;

/**
 * Created by samu on 4/7/17.
 */
public class ProcessableContent {

    private String url;
    private String content;

    /**
     * @param url
     * @param content
     */
    public ProcessableContent(String url, String content) {

        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
