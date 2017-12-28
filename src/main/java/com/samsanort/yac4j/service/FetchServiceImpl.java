package com.samsanort.yac4j.service;

import com.samsanort.yac4j.connection.Connection;
import com.samsanort.yac4j.connection.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by samu on 5/6/17.
 */
public class FetchServiceImpl implements FetchService {

    private static final Logger logger = LoggerFactory.getLogger(FetchServiceImpl.class);

    private ConnectionFactory connectionFactory;

    /**
     * @param connectionFactory
     */
    public FetchServiceImpl(ConnectionFactory connectionFactory) {

        this.connectionFactory = connectionFactory;
    }

    @Override
    public String fetchURLContent(String pageUrl) {

        logger.trace("Fetching URL {}", pageUrl);

        try {

            Connection conn = this.connectionFactory.obtainConnection(pageUrl);
            return conn.readAll();

        } catch (IOException ioe) {
            logger.warn("Error fetching URL {}: {}", pageUrl, ioe.getMessage());
            return null;
        }
    }
}
