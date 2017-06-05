package com.samsanort.yac4j.service;

import com.samsanort.yac4j.connection.Connection;
import com.samsanort.yac4j.connection.ConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Created by samu on 5/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class FetchServiceImplTest {

    private FetchServiceImpl testSubject;

    @Mock
    private ConnectionFactory mockedConnectionFactory;

    @Mock
    private Connection mockedConnection;

    @Before
    public void init() {

        this.testSubject = new FetchServiceImpl(this.mockedConnectionFactory);
    }

    @Test
    public void fetchURLContent_connectionFactoryReturnsValidConnection_returnsContent() throws Exception {

        // Given

        String aUrl = "http://any.url";
        String expectedContent = "expected";

        when(mockedConnection.readAll()).thenReturn(expectedContent);
        when(mockedConnectionFactory.obtainConnection(aUrl)).thenReturn(mockedConnection);

        // When
        String fetchedContent = testSubject.fetchURLContent(aUrl);

        // Then
        assertThat(fetchedContent, is(equalTo(expectedContent)));
    }

    @Test
    public void fetchURLContent_connectionThrowsIOException_returnsNull() throws Exception {

        // Given

        String aUrl = "http://any.url";

        doThrow(new IOException("mocked")).when(mockedConnection).readAll();
        when(mockedConnectionFactory.obtainConnection(aUrl)).thenReturn(mockedConnection);

        // When
        String fetchedContent = testSubject.fetchURLContent(aUrl);

        // Then
        assertThat(fetchedContent, is(equalTo(null)));
    }
}
