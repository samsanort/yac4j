package com.samsanort.yac4j.model;

/**
 * Created by samu on 3/1/17.
 */
public class Link {

    private String absoluteUrl;

    /**
     * @param url
     * @param sourceUrl
     */
    public Link(String rootUrl, String url, String sourceUrl) {

        if (isAbsolute(url)) {

            absoluteUrl = url;

        } else {

            if(url.startsWith("/")) {

                if(rootUrl.endsWith("/")) {
                    absoluteUrl = rootUrl + url.substring(1);

                } else {
                    absoluteUrl = rootUrl + url;
                }

            } else {

                if (sourceUrl.endsWith("/") || url.startsWith("/")) {

                    absoluteUrl = sourceUrl + url;

                } else {

                    absoluteUrl = sourceUrl + "/" + url;
                }
            }
        }
    }

    private boolean isAbsolute(String url) {

        if (url.startsWith("http://") || url.startsWith("https://"))
            return true;

        if (url.startsWith("file:////"))
            return true;

        return false;
    }

    /**
     * @return
     */
    public String getAbsoluteUrl() {
        return this.absoluteUrl;
    }
}
