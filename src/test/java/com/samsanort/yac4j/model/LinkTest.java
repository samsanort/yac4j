package com.samsanort.yac4j.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by samu on 4/9/17.
 */
public class LinkTest {

    private Link testSubject;

    @Test
    public void ctor_urlIsAbsolute_getAbsoluteUrlReturnsUrl() {

        // Given
        String[] urls = {
                "file:////foo.txt",
                "http://www.github.com",
                "https://secure.whatever.org/somethinelse",
                "http://localhost:8080/index.html"
        };
        String sourceUrl = "http://fixed.source.url.edu";
        String rootUrl = "http://root.url.com";

        for (String url : urls) {

            // When
            testSubject = new Link(rootUrl, url, sourceUrl);

            // Then
            assertThat(testSubject.getAbsoluteUrl(), is(equalTo(url)));
        }
    }

    @Test
    public void ctor_urlIsRelativeNotStartingWithSlash_getAbsoluteUrlReturnsSourceUrlPlusUrl() {

        // Given
        String[] urls = {
                "first.html",
                "second",
                "third.txt",
                "fourth"
        };
        String[] sourceUrls = {
                "file:////foo",
                "http://www.github.com",
                "https://secure.whatever.org/somethinelse",
                "http://localhost:8080"
        };
        String rootUrl = "http://root.url.com";

        for (int i = 0; i < urls.length; i++) {

            // When
            testSubject = new Link(rootUrl, urls[i], sourceUrls[i]);

            // Then
            assertThat(testSubject.getAbsoluteUrl(), is(equalTo(sourceUrls[i] + "/" + urls[i])));
        }
    }

    @Test
    public void ctor_urlIsRelativeStartingWithSlash_getAbsoluteUrlReturnsRootUrlPlusUrl() {

        // Given
        String root = "http://root.url";
        String[] roots = {
                root,
                root + "/"
        };

        String[] sources = {
                "http://first.source",
                "https://second.source/url/"
        };

        String[] urls = {
                "/first.html",
                "/second",
        };

        for (int i = 0; i < urls.length; i++) {

            // When
            testSubject = new Link(roots[i], urls[i], sources[i]);

            // Then
            assertThat(testSubject.getAbsoluteUrl(), is(equalTo(root + urls[i])));
        }
    }
}
