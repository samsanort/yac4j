package com.samsanort.yac4j.model;

import java.util.regex.Pattern;

/**
 * Created by samu on 3/1/17.
 */
public class Link {

    private final static Pattern ABSOLUTE_LINK_PATTERN = Pattern.compile("^(?:(?:http[s]?://)|(?:file:////)|(?://))(?:[a-zA-Z0-9_-]+?\\.[a-zA-Z0-9_-]+?).*");

    private String absoluteUrl;

    /**
     * @param url
     * @param sourceUrl
     */
    public Link(String url, String sourceUrl) {

        if (ABSOLUTE_LINK_PATTERN.matcher(url).matches()) {

            absoluteUrl = url;

        } else {

            if (sourceUrl.endsWith("/") || url.startsWith("/")) {

                absoluteUrl = sourceUrl + url;

            } else {

                absoluteUrl = sourceUrl + "/" + url;
            }
        }
    }

    /**
     * @return
     */
    public String getAbsoluteUrl() {
        return this.absoluteUrl;
    }
}
