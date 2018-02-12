package com.samsanort.yac4j.process;

import com.samsanort.yac4j.UrlEvaluator;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.model.PageWithLinks;
import com.samsanort.yac4j.model.ProcessableContent;
import com.samsanort.yac4j.service.FetchService;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

/**
 * Created by samu on 4/20/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class FetcherTest {

    private Fetcher testSubject;

    @Mock
    private FetchService mockedFetchService;

    @Mock
    private TrackedUrlContainer mockedTrackedUrlContainer;

    @Mock
    private Queue<ProcessableContent> mockedProcessableContentQueue;

    @Mock
    private UrlEvaluator mockedUrlEvaluator;

    private static final String ROOT_URL = "http://root";
    private static final String URL = "http://www.foo.bar";
    private static final String CONTENT = "<p> CONTENT </p>";

    @Before
    public void initTest() {

        testSubject =
                new Fetcher(
                        ROOT_URL,
                        mockedFetchService,
                        mockedTrackedUrlContainer,
                        mockedProcessableContentQueue,
                        mockedUrlEvaluator,
                        250);
    }

    @Test
    public void runCycle_containerHasVisitableUrl_urlContentIsFetchedAndVisitedUrlIsTracked() throws Exception {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrlContainer).addVisitedUrl(URL);
        verify(mockedFetchService).fetchURLContent(URL);
    }

    @Test
    public void runCycle_containerHasNoMoreVisitableUrls_nothingIsFetched() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedFetchService, times(0)).fetchURLContent(anyString());
    }

    @Test
    public void runCycle_fetchedContentIsNull_urlIsTracked() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrlContainer).addVisitedUrl(URL);
    }

    @Test
    public void runCycle_fetchedContentIsNull_noContentIsEnqueued() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedProcessableContentQueue, times(0)).enqueue(ArgumentMatchers.any(ProcessableContent.class));
    }

    @Test
    public void runCycle_fetchedContentIsFromNotProcessableUrl_nothingIsEnqueued() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(CONTENT);
        when(mockedUrlEvaluator.isProcessable(URL)).thenReturn(false);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedProcessableContentQueue, times(0)).enqueue(ArgumentMatchers.any(ProcessableContent.class));
    }

    @Test
    public void runCycle_fetchedContentIsProcessable_contentIsEnqueued() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(CONTENT);
        when(mockedUrlEvaluator.isProcessable(URL)).thenReturn(true);

        // When
        testSubject.runCycle();

        // Then
        ArgumentCaptor<ProcessableContent> argument = ArgumentCaptor.forClass(ProcessableContent.class);
        verify(mockedProcessableContentQueue).enqueue(argument.capture());
        assertThat(argument.getValue().getUrl(), is(equalTo(URL)));
        assertThat(argument.getValue().getContent(), is(equalTo(CONTENT)));
    }

    @Test
    public void runCycle_fetchedContentHasVisitableURLs_URLsAreAddedIntoTrackedUrlContainer() throws Exception {

        // Given
        String linkedUrl = aVisitableUrl();
        PageWithLinks page = aPageWithLinksThatWillBeFetched(linkedUrl);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrlContainer).addVisitableUrl(linkedUrl);
    }

    @Test
    public void runCycle_fetchedContentDoesNotHaveVisitableURLs_noURLIsRegisteredAsVisitable() {

        // Given
        aFetchablePageWithoutVisitableLinks();

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrlContainer, times(0)).addVisitableUrl(anyString());
    }

    @Test
    public void runCycle_fetchedContentHasURLAlreadyScheduledForVisiting_URLIsNotRegisteredAsVisitable() {

        // Given
        aFetchablePageWithLinkScheduledForVisiting();

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrlContainer, times(0)).addVisitableUrl(anyString());
    }

    @Test
    public void runCycle_fetchedContentHasURLAlreadyVisited_URLIsNotRegisteredAsVisitable() {

        // Given
        aFetchablePageWithLinkAlreadyVisited();

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrlContainer, times(0)).addVisitableUrl(anyString());
    }

    @Test
    public void runCycle_UncheckedException_finishesGracefully() throws Exception {

        // Given
        Exception captured = null;
        willThrow(new RuntimeException("Mocked")).given(mockedTrackedUrlContainer).nextVisitableUrl();

        // When
        try {
            testSubject.runCycle();

        } catch (Exception e) {
            captured = e;
        }

        // Then
        assertThat(captured, is(nullValue()));
    }

    private PageWithLinks aFetchablePageWithoutVisitableLinks() {

        return aPageWithLinksThatWillBeFetched(null);
    }

    private PageWithLinks aFetchablePageWithLinkAlreadyVisited() {

        String urlAlreadyVisited = aVisitableUrl();

        when(mockedTrackedUrlContainer.isAlreadyVisited(urlAlreadyVisited)).thenReturn(true);

        return aPageWithLinksThatWillBeFetched(urlAlreadyVisited);
    }

    private PageWithLinks aFetchablePageWithLinkScheduledForVisiting() {

        String urlWithVisitScheduled = aVisitableUrl();

        when(mockedTrackedUrlContainer.isVisitAlreadyScheduled(urlWithVisitScheduled)).thenReturn(true);

        return aPageWithLinksThatWillBeFetched(urlWithVisitScheduled);
    }

    private PageWithLinks aPageWithLinksThatWillBeFetched(String linkedUrl) {

        String html = (linkedUrl == null ? "<html></html>" : "<html><a href=\"" + linkedUrl + "\">link</a></html>");

        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(html);

        return new PageWithLinks(ROOT_URL, URL, html);
    }

    private String aVisitableUrl() {

        String visitableUrl = "http://"
                + new RandomStringGenerator.Builder().withinRange('a', 'z').build().generate(8);

        when(mockedUrlEvaluator.isVisitable(visitableUrl)).thenReturn(true);

        return visitableUrl;
    }

}
