package com.samsanort.yac4j.process;

import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.model.ProcessableContent;
import com.samsanort.yac4j.service.FetchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
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
    private Queue<ProcessableContent> mockedQueue;

    private static final String URL = "http://www.foo.bar";
    private static final String CONTENT = "<p> CONTENT </p>";

    @Before
    public void initTest() {
        testSubject = new Fetcher(mockedFetchService, mockedTrackedUrlContainer, mockedQueue, 250);
    }

    @Test
    public void runCycle_existsUrlPointingToContent_contentIsEnqueuedAndUrlIsTracked() {

        // Given

        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(CONTENT);

        // When
        testSubject.runCycle();

        // Then

        ArgumentCaptor<ProcessableContent> argument = ArgumentCaptor.forClass(ProcessableContent.class);
        verify(mockedQueue).enqueue(argument.capture());
        assertThat(argument.getValue().getUrl(), is(equalTo(URL)));
        assertThat(argument.getValue().getContent(), is(equalTo(CONTENT)));

        verify(mockedTrackedUrlContainer).addVisitedUrl(URL);
    }

    @Test
    public void runCycle_trackedUrlContainerReturnsNull_nothingIsFetchedNorEnqueued() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedQueue, times(0)).enqueue(ArgumentMatchers.any(ProcessableContent.class));
        verify(mockedTrackedUrlContainer, times(0)).addVisitedUrl(anyString());
    }

    @Test
    public void runCycle_fetcherServiceReturnsNull_nothingIsEnqueued() {

        // Given
        when(mockedTrackedUrlContainer.nextVisitableUrl()).thenReturn(URL);
        when(mockedFetchService.fetchURLContent(URL)).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedQueue, times(0)).enqueue(ArgumentMatchers.any(ProcessableContent.class));
        verify(mockedTrackedUrlContainer).addVisitedUrl(URL);
    }

}
