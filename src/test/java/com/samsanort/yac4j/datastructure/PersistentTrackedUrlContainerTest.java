package com.samsanort.yac4j.datastructure;

import com.samsanort.yac4j.UrlRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PersistentTrackedUrlContainerTest {

    private PersistentTrackedUrlContainer testSubject;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private Set<String> visitableUrlsCache;

    @Mock
    private Set<String> visitedUrlsCache;

    @Before
    public void initTest() {

        this.testSubject =
                new PersistentTrackedUrlContainer(
                        new ArrayList<>(),
                        this.urlRepository,
                        this.visitableUrlsCache,
                        this.visitedUrlsCache);
    }

    @Test
    public void constructor_loadsVisitableAndVisitedUrls() {

        // Given
        List<String> initialVisitableUrls = new ArrayList<>();
        initialVisitableUrls.add("url-1");
        List<String> initialVisitedUrls = new ArrayList<>();
        initialVisitedUrls.add("url-2");

        willReturn(initialVisitableUrls).given(this.urlRepository).getAllVisitable();
        willReturn(initialVisitedUrls).given(this.urlRepository).getAllVisited();

        // When
        this.testSubject =
                new PersistentTrackedUrlContainer(
                        new ArrayList<>(),
                        this.urlRepository,
                        this.visitableUrlsCache,
                        this.visitedUrlsCache);

        // Then
        verify(this.urlRepository, times(2)).getAllVisitable();
        verify(this.urlRepository, times(2)).getAllVisited();
        verify(this.visitableUrlsCache).addAll(initialVisitableUrls);
        verify(this.visitedUrlsCache).addAll(initialVisitedUrls);
    }

    @Test
    public void addVisitableUrl_addsVisitableUrl() {

        // Given
        String urlToAdd = "url-1";

        // When
        testSubject.addVisitableUrl(urlToAdd);

        // Then
        verify(visitableUrlsCache).add(urlToAdd);
        verify(urlRepository).addVisitable(urlToAdd);
    }

    @Test
    public void addVisitedUrl_addsVisitedUrl() {

        // Given
        String urlToAdd = "url-1";

        // When
        testSubject.addVisitedUrl(urlToAdd);

        // Then
        verify(visitedUrlsCache).add(urlToAdd);
        verify(urlRepository).addVisited(urlToAdd);
    }

    @Test
    public void nextVisitableUrl_hasVisitableUrls_returnsNextVistableUrl() {

        // Given
        String expectedUrl = "url";
        Iterator<String> mockedIterator = Mockito.mock(Iterator.class);
        willReturn(expectedUrl).given(mockedIterator).next();
        willReturn(mockedIterator).given(visitableUrlsCache).iterator();

        // When
        String nextVisitableUrl = testSubject.nextVisitableUrl();

        // Then
        assertThat(nextVisitableUrl, is(equalTo(expectedUrl)));
    }

    @Test
    public void nextVisitableUrl_noMoreVisitableUrls_returnsNull() {

        // Given
        Iterator<String> mockedIterator = Mockito.mock(Iterator.class);
        willReturn(null).given(mockedIterator).next();
        willReturn(mockedIterator).given(visitableUrlsCache).iterator();

        // When
        String nextVisitableUrl = testSubject.nextVisitableUrl();

        // Then
        assertThat(nextVisitableUrl, is(nullValue()));
    }

    @Test
    public void isAlreadyVisited_urlInVisitedUrls_returnsTrue() {

        // Given
        String visitedUrl = "visited";
        willReturn(true).given(visitedUrlsCache).contains(visitedUrl);

        // When
        boolean visited = testSubject.isAlreadyVisited(visitedUrl);

        // Then
        assertThat(visited, is(true));
    }

    @Test
    public void isAlreadyVisited_urlNotInVisitedUrls_returnsFalse() {

        // Given
        String visitedUrl = "visited";
        willReturn(false).given(visitedUrlsCache).contains(visitedUrl);

        // When
        boolean visited = testSubject.isAlreadyVisited(visitedUrl);

        // Then
        assertThat(visited, is(false));
    }

    @Test
    public void isVisitAlreadyScheduled_urlInVisitableUrls_returnsTrue() {

        // Given
        String visitabledUrl = "visitable";
        willReturn(true).given(visitableUrlsCache).contains(visitabledUrl);

        // When
        boolean scheduled = testSubject.isVisitAlreadyScheduled(visitabledUrl);

        // Then
        assertThat(scheduled, is(true));
    }

    @Test
    public void isVisitAlreadyScheduled_urlNotInVisitableUrls_returnsFalse() {

        // Given
        String visitabledUrl = "visitable";
        willReturn(false).given(visitableUrlsCache).contains(visitabledUrl);

        // When
        boolean scheduled = testSubject.isVisitAlreadyScheduled(visitabledUrl);

        // Then
        assertThat(scheduled, is(false));
    }

    @Test
    public void willReturnVisitableUrl_visitableUrlsIsNotEmpty_returnsTrue() {

        // Given
        willReturn(false).given(visitableUrlsCache).isEmpty();

        // When
        boolean willReturnUrl = testSubject.willReturnVisitableUrl();

        // Then
        assertThat(willReturnUrl, is(true));
    }

    @Test
    public void willReturnVisitableUrl_visitableUrlsIsEmpty_returnsFalse() {

        // Given
        willReturn(true).given(visitableUrlsCache).isEmpty();

        // When
        boolean willReturnUrl = testSubject.willReturnVisitableUrl();

        // Then
        assertThat(willReturnUrl, is(false));
    }

}