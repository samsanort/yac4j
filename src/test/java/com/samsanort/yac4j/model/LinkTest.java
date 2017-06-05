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
                "https://secure.whatever.org/somethinelse"
        };
        String sourceUrl = "http://fixed.url.edu";

        for (String url : urls) {

            // When
            testSubject = new Link(url, sourceUrl);

            // Then
            assertThat(testSubject.getAbsoluteUrl(), is(equalTo(url)));
        }
    }

    @Test
    public void ctor_urlIsRelative_getAbsoluteUrlReturnsSourceUrlPlusUrl() {

        // Given
        String[] urls = {
                "first.html",
                "second",
                "third.txt"
        };
        String[] sourceUrls = {
                "file:////foo",
                "http://www.github.com",
                "https://secure.whatever.org/somethinelse"
        };

        for (int i = 0; i < urls.length; i++) {

            // When
            testSubject = new Link(urls[i], sourceUrls[i]);

            // Then
            assertThat(testSubject.getAbsoluteUrl(), is(equalTo(sourceUrls[i] + "/" + urls[i])));
        }
    }

}
