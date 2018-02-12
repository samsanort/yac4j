package com.samsanort.yac4j.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by samu on 2/26/17.
 */
public class PageWithLinks {

    private final static Pattern LINK_PATTERN = Pattern.compile("href=['\"](.+?)['\"]");

    private String htmlContent;
    private String url;
    private String rootUrl;
    private List<Link> linkedUrls;

    /**
     * @param htmlContent
     */
    public PageWithLinks(String rootUrl, String url, String htmlContent) {

        this.rootUrl = rootUrl;
        this.url = url;
        this.htmlContent = htmlContent;
    }

    public List<Link> getLinkedUrls() {

        if(this.linkedUrls == null) {
            extractLinkedUrls();
        }

        return Collections.unmodifiableList(
                this.linkedUrls);
    }

    private void extractLinkedUrls() {

        this.linkedUrls = new ArrayList();

        Matcher matcher = LINK_PATTERN.matcher(this.htmlContent);

        while (matcher.find()) {

            linkedUrls.add(
                    new Link(rootUrl, matcher.group(1), url));
        }
    }

    public String getUrl() {
        return url;
    }
}
