package com.samsanort.yac4j;

import com.samsanort.yac4j.connection.ConnectionFactory;
import com.samsanort.yac4j.connection.ConnectionFactoryImpl;
import com.samsanort.yac4j.datastructure.ProcessableContentQueue;
import com.samsanort.yac4j.datastructure.Queue;
import com.samsanort.yac4j.datastructure.TrackedUrlContainer;
import com.samsanort.yac4j.datastructure.TrackedUrlContainerImpl;
import com.samsanort.yac4j.model.ProcessableContent;
import com.samsanort.yac4j.process.CycleRunner;
import com.samsanort.yac4j.process.Fetcher;
import com.samsanort.yac4j.process.Processor;
import com.samsanort.yac4j.service.FetchServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by samsanort@gmail.com
 */
public class Crawler {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    // mandatorily provided
    private CrawlerConfig config;
    private UrlEvaluatorFactory urlEvaluatorFactory;
    private PageProcessorFactory pageProcessorFactory;

    // optionally providable
    private TrackedUrlContainer trackedUrlContainer;
    private ConnectionFactory connectionFactory;
    private Queue<ProcessableContent> processableContentQueue;

    // internal
    private CycleRunner[] cycleRunners_;

    /**
     * @param config
     * @param urlEvaluatorFactory
     * @param pageProcessorFactory
     */
    public Crawler(
            CrawlerConfig config,
            UrlEvaluatorFactory urlEvaluatorFactory,
            PageProcessorFactory pageProcessorFactory) {

        this.validateCtorArgs(config, urlEvaluatorFactory, pageProcessorFactory);

        this.config = config;
        this.urlEvaluatorFactory = urlEvaluatorFactory;
        this.pageProcessorFactory = pageProcessorFactory;
    }

    /**
     *
     */
    public void start() {

        if (isCrawling()) {
            throw new IllegalStateException("This crawler is already crawling.");
        }

        try {
            initCrawler();
            startCrawling();

        } catch (InterruptedException ie) {
            logger.info("Crawler interrupted by user.");
            stopCrawling();
        }
    }

    /**
     *
     */
    public void stop() {

        logger.info("Crawler stop requested by user.");
        stopCrawling();
    }

    /**
     * @return
     */
    public boolean isCrawling() {

        if (this.cycleRunners_ == null)
            return false;

        if (this.trackedUrlContainer.willReturnVisitableUrl())
            return true;

        if (!this.processableContentQueue.isEmpty())
            return true;

        for (CycleRunner cycleRunner : this.cycleRunners_) {
            if (cycleRunner.getState().equals(CycleRunner.State.WORKING)) {
                return true;
            }
        }

        return false;
    }

    private void validateCtorArgs(
            CrawlerConfig config,
            UrlEvaluatorFactory urlEvaluatorFactory,
            PageProcessorFactory pageProcessorFactory) {

        failIfNull(urlEvaluatorFactory, "UrlEvaluator argument cannot be null.");
        failIfNull(pageProcessorFactory, "PageProcessor argument cannot be null.");
        failIfNull(config, "CrawlerConfig argument cannot be null.");
    }

    private void failIfNull(Object object, String failureMessage) {

        if (object == null) {
            throw new IllegalArgumentException(failureMessage);
        }
    }

    private void initCrawler() {

        if (this.processableContentQueue == null) {
            this.processableContentQueue = new ProcessableContentQueue();
        }

        if (this.connectionFactory == null) {
            this.connectionFactory = buildConnectionFactory();
        }

        if (this.trackedUrlContainer == null) {
            this.trackedUrlContainer = new TrackedUrlContainerImpl(config.getMaxFetches(), config.getSeeds());
        }

        this.cycleRunners_ = new CycleRunner[config.getWorkers() + 1];
    }

    private ConnectionFactory buildConnectionFactory() {

        if (this.config.getProxyAddress() != null && this.config.getProxyPort() != CrawlerConfig.UNASSIGNED) {
            return new ConnectionFactoryImpl(config.getProxyAddress(), config.getProxyPort());

        } else {
            return new ConnectionFactoryImpl();
        }
    }

    private void startCrawling() throws InterruptedException {

        for (int i = 0; i < this.cycleRunners_.length - 1; i++) {

            this.cycleRunners_[i] = createFetcherCycleRunner();
            new Thread(this.cycleRunners_[i]).start();
            Thread.sleep(this.config.getVisitDelay());
        }

        this.cycleRunners_[this.cycleRunners_.length - 1] = createProcessorCycleRunner();
        new Thread(this.cycleRunners_[this.cycleRunners_.length - 1]).start();

        Thread.sleep(10000);
    }

    private CycleRunner createFetcherCycleRunner() {

        return new CycleRunner(
                new Fetcher(
                        new FetchServiceImpl(this.connectionFactory),
                        this.trackedUrlContainer,
                        this.processableContentQueue,
                        this.urlEvaluatorFactory.createInstance(),
                        this.config.getVisitDelay()));
    }

    private CycleRunner createProcessorCycleRunner() {

        return new CycleRunner(
                new Processor(
                        this.processableContentQueue,
                        this.pageProcessorFactory.createInstance(),
                        this.config.getVisitDelay()));
    }

    private void stopCrawling() {

        if (this.cycleRunners_ != null) {

            for (CycleRunner runner : this.cycleRunners_) {
                runner.kill();
            }

            this.cycleRunners_ = null;
        }
    }

    /**
     * @param trackedUrlContainer
     */
    public void setTrackedUrlContainer(TrackedUrlContainer trackedUrlContainer) {
        this.trackedUrlContainer = trackedUrlContainer;
    }

    /**
     * @param connectionFactory
     */
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * @param queue
     */
    public void setProcessableContentQueue(Queue<ProcessableContent> queue) {
        this.processableContentQueue = queue;
    }
}
