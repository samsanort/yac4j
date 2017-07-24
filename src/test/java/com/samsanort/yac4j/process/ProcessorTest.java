package com.samsanort.yac4j.process;

import com.samsanort.yac4j.PageProcessor;
import com.samsanort.yac4j.UrlEvaluator;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.model.ProcessableContent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by samu on 5/28/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProcessorTest {

    private Processor testSubject;

    @Mock
    private Queue<ProcessableContent> mockedProcessableContentQueue;
    @Mock
    private TrackedUrlContainer mockedTrackedUrls;
    @Mock
    private PageProcessor mockedPageProcessor;
    @Mock
    private UrlEvaluator mockedUrlEvaluator;

    @Before
    public void initTest() {

        this.testSubject = new Processor(
                mockedProcessableContentQueue,
                mockedTrackedUrls,
                mockedPageProcessor,
                mockedUrlEvaluator,
                2000);
    }

    @Test
    public void runCycle_queueReturnsNull_noLinkIsRegisteredNorProcessed() {

        // Given
        when(mockedProcessableContentQueue.dequeue()).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrls, times(0)).addVisitableUrl(anyString());
        verify(mockedPageProcessor, times(0)).process(anyString());
    }

    @Test
    public void runCycle_dequeuedContentIsProcessable_contentIsSentToPageProcessor() {

        // Given
        ProcessableContent dequeued = aProcessableContentWithVisitableLinks();
        when(mockedProcessableContentQueue.dequeue()).thenReturn(dequeued);
        when(mockedUrlEvaluator.isProcessable(dequeued.getUrl())).thenReturn(true);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedPageProcessor).process(dequeued.getContent());
    }

    @Test
    public void runCycle_dequeuedContentIsNotProcessable_contentIsNotSentToProcessor() throws Exception {

        // Given
        ProcessableContent dequeued = aProcessableContentWithVisitableLinks();
        when(mockedProcessableContentQueue.dequeue()).thenReturn(dequeued);
        when(mockedUrlEvaluator.isProcessable(dequeued.getUrl())).thenReturn(false);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedPageProcessor, times(0)).process(dequeued.getContent());
    }


    @Test
    public void runCycle_dequeuedContentDoesNotHaveVisitableLinks_noLinkIsRegisteredAsVisitable() {

        // Given
        ProcessableContent dequeued = aProcessableContentWithoutVisitableLinks();
        when(mockedProcessableContentQueue.dequeue()).thenReturn(dequeued);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrls, times(0)).addVisitableUrl(anyString());
    }

    @Test
    public void runCycle_dequeuedContentHasLinkAlreadyScheduledForVisiting_noLinkIsRegisteredAsVisitable() {

        // Given
        ProcessableContent dequeued = aProcessableContentWithLinkScheduledForVisiting();
        when(mockedProcessableContentQueue.dequeue()).thenReturn(dequeued);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrls, times(0)).addVisitableUrl(anyString());
    }

    @Test
    public void runCycle_dequeuedContentHasLinkAlreadyVisited_noLinkIsRegisteredAsVisitable() {

        // Given
        ProcessableContent dequeued = aProcessableContentWithLinkAlreadyVisited();
        when(mockedProcessableContentQueue.dequeue()).thenReturn(dequeued);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedTrackedUrls, times(0)).addVisitableUrl(anyString());
    }

    private ProcessableContent aProcessableContentWithoutVisitableLinks() {

        return new ProcessableContent(
                "www.no-visitable-links.com",
                "<html></html>");
    }

    private ProcessableContent aProcessableContentWithLinkAlreadyVisited() {

        String urlAlreadyVisited = aVisitableUrl();

        when(mockedTrackedUrls.isAlreadyVisited(urlAlreadyVisited)).thenReturn(true);

        return new ProcessableContent(
                "www.has-links.com",
                "<html><a href=\"" + urlAlreadyVisited + "\">link</a></html>");
    }

    private ProcessableContent aProcessableContentWithLinkScheduledForVisiting() {

        String urlWithVisitScheduled = aVisitableUrl();

        when(mockedTrackedUrls.isVisitAlreadyScheduled(urlWithVisitScheduled)).thenReturn(true);

        return new ProcessableContent(
                "www.has-links.com",
                "<html><a href=\"" + urlWithVisitScheduled + "\">link</a></html>");
    }

    private ProcessableContent aProcessableContentWithVisitableLinks() {

        return new ProcessableContent(
                "www.has-links.com",
                "<html><a href=\"" + aVisitableUrl() + "\">link</a></html>");
    }

    private String aVisitableUrl() {

        String url = "http://foo.bar/visitable";

        when(mockedUrlEvaluator.isVisitable(url)).thenReturn(true);

        return url;
    }
}