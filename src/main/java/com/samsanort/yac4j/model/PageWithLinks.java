package com.samsanort.yac4j.model;

import java.util.ArrayList;
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
    private List<Link> linkedUrls = new ArrayList();

    /**
     * @param htmlContent
     */
    public PageWithLinks(String url, String htmlContent) {

        this.url = url;
        this.htmlContent = htmlContent;
        extractLinkedUrls();
    }

    public List<Link> getLinkedUrls() {
        return this.linkedUrls;
    }

    private void extractLinkedUrls() {

        Matcher matcher = LINK_PATTERN.matcher(this.htmlContent);

        while (matcher.find()) {

            linkedUrls.add(
                    new Link(matcher.group(1), url));
        }
    }
}
