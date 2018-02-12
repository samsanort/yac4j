package com.samsanort.yac4j.model;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by samu on 4/9/17.
 */
public class PageWithLinksTest {

    private PageWithLinks testSubject;

    @Test
    public void ctor_contentHasLinks_getLinkedUrlsReturnsLinks() {

        // Given
        String rootUrl = "http://root.url";
        String url = "http://foo.bar";
        String link1 = "page1.html";
        String link2 = "http://www.something.com";
        String content = aContentWithLinkPlaceholders();
        content = content.replaceFirst("@1@", link1);
        content = content.replaceFirst("@2@", link2);

        // When
        testSubject = new PageWithLinks(rootUrl, url, content);
        List<Link> links = testSubject.getLinkedUrls();

        // Then
        assertThat(links.get(0).getAbsoluteUrl(), is(equalTo(url + "/" + link1)));
        assertThat(links.get(1).getAbsoluteUrl(), is(equalTo(link2)));
    }

    private String aContentWithLinkPlaceholders() {
        return "<html>"
                + "<a href=\"@1@\">Link 1</a>"
                + "<br/><p>whatever</p>"
                + "<a href=\"@2@\">Link 1</a>"
                + "</html>";
    }
}
