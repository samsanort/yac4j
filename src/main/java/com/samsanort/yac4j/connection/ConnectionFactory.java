package com.samsanort.yac4j.connection;

import java.io.IOException;

/**
 * Created by samu on 4/4/17.
 */
public interface ConnectionFactory {

    /**
     * @param strUrl
     * @return
     */
    Connection obtainConnection(String strUrl) throws IOException;
}
