package com.samsanort.yac4j.process;

import com.samsanort.yac4j.PageProcessor;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.model.ProcessableContent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willThrow;
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
    private PageProcessor mockedPageProcessor;

    @Before
    public void initTest() {

        this.testSubject = new Processor(
                mockedProcessableContentQueue,
                mockedPageProcessor,
                2000);
    }

    @Test
    public void runCycle_queueReturnsNull_noLinkIsProcessed() {

        // Given
        when(mockedProcessableContentQueue.dequeue()).thenReturn(null);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedPageProcessor, times(0)).process(anyString());
    }

    @Test
    public void runCycle_contentIsDequeued_contentIsSentToPageProcessor() {

        // Given
        ProcessableContent dequeued = aProcessableContentWithVisitableLinks();
        when(mockedProcessableContentQueue.dequeue()).thenReturn(dequeued);

        // When
        testSubject.runCycle();

        // Then
        verify(mockedPageProcessor).process(dequeued.getContent());
    }

    @Test
    public void runCycle_UncheckedException_finishesGracefully() throws Exception {

        // Given
        Exception captured = null;
        willThrow(new RuntimeException("Mocked")).given(mockedProcessableContentQueue).dequeue();

        // When
        try {
            testSubject.runCycle();

        } catch (Exception e) {
            captured = e;
        }

        // Then
        assertThat(captured, is(nullValue()));
    }


    private ProcessableContent aProcessableContentWithVisitableLinks() {

        return new ProcessableContent(
                "www.has-links.com",
                "<html><a href=\"http://foo.bar/visitable\">link</a></html>");
    }
}