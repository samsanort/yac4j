package com.samsanort.yac4j.datastructure;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by samu on 9/25/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class EphimeralTrackedUrlContainerTest {

    private EphimeralTrackedUrlContainer testSubject;

    @Mock
    private Set<String> visitableUrls;

    @Mock
    private Set<String> visitedUrls;

    @Before
    public void init() {

        this.testSubject =
                new EphimeralTrackedUrlContainer(2, null, visitableUrls, visitedUrls);
    }

    @Test
    public void addVisitableUrl_noMaxVisits_addsVisitableUrl() throws Exception {

        // Given
        String urlToAdd = "url-1";

        // When
        testSubject.addVisitableUrl(urlToAdd);

        // Then
        verify(visitableUrls).add(urlToAdd);
    }

    @Test
    public void addVisitableUrl_maxVisitsNotReached_addsVisitableUrl() throws Exception {

        // Given
        willReturn(1).given(visitableUrls).size();
        String url = "url";

        // When
        testSubject.addVisitableUrl(url);

        // Then
        verify(visitableUrls).add(url);
    }

    @Test
    public void addVisitableUrl_maxVisitsReached_doesNotAddVisitableUrl() throws Exception {

        // Given
        EphimeralTrackedUrlContainer spiedTestSubject = Mockito.spy(testSubject);
        willReturn(true).given(spiedTestSubject).maxDequeuesReached();
        String urlToNotAdd = "url";

        // When
        spiedTestSubject.addVisitableUrl(urlToNotAdd);

        // Then
        verify(visitableUrls, times(0)).add(urlToNotAdd);
    }

    @Test
    public void nextVisitableUrl_hasVisitableUrls_returnsNextVistableUrl() throws Exception {

        // Given
        String expectedUrl = "url";
        Iterator<String> mockedIterator = Mockito.mock(Iterator.class);
        willReturn(expectedUrl).given(mockedIterator).next();
        willReturn(mockedIterator).given(visitableUrls).iterator();

        // When
        String nextVisitableUrl = testSubject.nextVisitableUrl();

        // Then
        assertThat(nextVisitableUrl, is(equalTo(expectedUrl)));
    }

    @Test
    public void nextVisitableUrl_noMoreVisitableUrls_returnsNull() throws Exception {

        // Given
        Iterator<String> mockedIterator = Mockito.mock(Iterator.class);
        willReturn(null).given(mockedIterator).next();
        willReturn(mockedIterator).given(visitableUrls).iterator();

        // When
        String nextVisitableUrl = testSubject.nextVisitableUrl();

        // Then
        assertThat(nextVisitableUrl, is(nullValue()));
    }

    @Test
    public void isAlreadyVisited_urlInVisitedUrls_returnsTrue() throws Exception {

        // Given
        String visitedUrl = "visited";
        willReturn(true).given(visitedUrls).contains(visitedUrl);

        // When
        boolean visited = testSubject.isAlreadyVisited(visitedUrl);

        // Then
        assertThat(visited, is(true));
    }

    @Test
    public void isAlreadyVisited_urlNotInVisitedUrls_returnsFalse() throws Exception {

        // Given
        String visitedUrl = "visited";
        willReturn(false).given(visitedUrls).contains(visitedUrl);

        // When
        boolean visited = testSubject.isAlreadyVisited(visitedUrl);

        // Then
        assertThat(visited, is(false));
    }

    @Test
    public void isVisitAlreadyScheduled_urlInVisitableUrls_returnsTrue() throws Exception {

        // Given
        String visitabledUrl = "visitable";
        willReturn(true).given(visitableUrls).contains(visitabledUrl);

        // When
        boolean scheduled = testSubject.isVisitAlreadyScheduled(visitabledUrl);

        // Then
        assertThat(scheduled, is(true));
    }

    @Test
    public void isVisitAlreadyScheduled_urlNotInVisitableUrls_returnsFalse() throws Exception {

        // Given
        String visitabledUrl = "visitable";
        willReturn(false).given(visitableUrls).contains(visitabledUrl);

        // When
        boolean scheduled = testSubject.isVisitAlreadyScheduled(visitabledUrl);

        // Then
        assertThat(scheduled, is(false));
    }

    @Test
    public void willReturnVisitableUrl_visitableUrlsIsNotEmpty_returnsTrue() throws Exception {

        // Given
        willReturn(false).given(visitableUrls).isEmpty();

        // When
        boolean willReturnUrl = testSubject.willReturnVisitableUrl();

        // Then
        assertThat(willReturnUrl, is(true));
    }

    @Test
    public void willReturnVisitableUrl_visitableUrlsIsEmpty_returnsFalse() throws Exception {

        // Given
        willReturn(true).given(visitableUrls).isEmpty();

        // When
        boolean willReturnUrl = testSubject.willReturnVisitableUrl();

        // Then
        assertThat(willReturnUrl, is(false));
    }

}