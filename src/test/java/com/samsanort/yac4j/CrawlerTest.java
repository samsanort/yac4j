package com.samsanort.yac4j;


import com.samsanort.yac4j.connection.ConnectionFactory;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

/**
 * Created by samu on 2/26/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    private Crawler testSubject;

    @Mock
    private UrlEvaluator mockedUrlEvaluator;

    @Mock
    private PageProcessor mockedPageProcessor;

    @Mock
    private TrackedUrlContainer mockedTrackedUrlContainer;

    @Mock
    private ConnectionFactory mockedConnectionFactory;

    // ---[ C'tor ]----------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void construct_nullUrlEvaluator_throwsIllegalArgumentException() {

        // When
        new Crawler(aCrawlerConfig(), null, aPageProcessorFactory());
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_nullPageProcessor_throwsIllegalArgumentException() {

        // When
        new Crawler(aCrawlerConfig(), anUrlEvaluatorFactory(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_nullConfig_throwsIllegalArgumentException() {

        // When
        new Crawler(null, anUrlEvaluatorFactory(), aPageProcessorFactory());
    }

    // ---[ start ]----------------------------------------------------------

    @Test(expected = IllegalStateException.class)
    public void start_alreadyRunning_throwsIllegalStateException() {

        // Given

        testSubject = new Crawler(aCrawlerConfig(), anUrlEvaluatorFactory(), aPageProcessorFactory());

        when(mockedTrackedUrlContainer.willReturnVisitableUrl()).thenReturn(true);
        testSubject.setTrackedUrlContainer(mockedTrackedUrlContainer);
        testSubject.setConnectionFactory(mockedConnectionFactory);
        testSubject.setTrackedUrlContainer(mockedTrackedUrlContainer);

        testSubject.start();

        // When

        testSubject.start();
    }

    @Test
    public void start_notStartedYet_starts() {

        // Given
        testSubject = new Crawler(aCrawlerConfig(), anUrlEvaluatorFactory(), aPageProcessorFactory());
        when(mockedTrackedUrlContainer.willReturnVisitableUrl()).thenReturn(true);
        testSubject.setTrackedUrlContainer(mockedTrackedUrlContainer);

        // When
        testSubject.start();

        // Then
        assertThat(testSubject.isCrawling(), is(equalTo(true)));
    }

    // ---[ stop ]-----------------------------------------------------------

    @Test
    public void stop_running_stops() {

        // Given
        testSubject = new Crawler(aCrawlerConfig(), anUrlEvaluatorFactory(), aPageProcessorFactory());
        when(mockedTrackedUrlContainer.willReturnVisitableUrl()).thenReturn(true);
        testSubject.setTrackedUrlContainer(mockedTrackedUrlContainer);
        testSubject.start();
        assertThat(testSubject.isCrawling(), is(equalTo(true)));

        // When
        testSubject.stop();

        // Then
        assertThat(testSubject.isCrawling(), is(equalTo(false)));
    }

    @Test
    public void stop_notRunning_doesNothing() {

        // Given
        testSubject = new Crawler(aCrawlerConfig(), anUrlEvaluatorFactory(), aPageProcessorFactory());

        // When
        testSubject.stop();

        // Then
        assertThat(testSubject.isCrawling(), is(equalTo(false)));
    }


    // ----------------------------------------------------------------------

    private CrawlerConfig aCrawlerConfig() {
        CrawlerConfig config = new CrawlerConfig("foobar.com");
        return config;
    }

    private UrlEvaluatorFactory anUrlEvaluatorFactory() {
        return new StubbedUrlEvaluatorFactory(mockedUrlEvaluator);
    }

    private PageProcessorFactory aPageProcessorFactory() {
        return new StubbedPageProcessorFactory(mockedPageProcessor);
    }

    // ----------------------------------------------------------------------

    private class StubbedUrlEvaluatorFactory implements UrlEvaluatorFactory {
        private UrlEvaluator instance;

        StubbedUrlEvaluatorFactory(UrlEvaluator instance) {
            this.instance = instance;
        }

        @Override
        public UrlEvaluator createInstance() {
            return instance;
        }
    }

    private class StubbedPageProcessorFactory implements PageProcessorFactory {
        private PageProcessor instance;

        StubbedPageProcessorFactory(PageProcessor instance) {
            this.instance = instance;
        }

        @Override
        public PageProcessor createInstance() {
            return instance;
        }
    }
}
