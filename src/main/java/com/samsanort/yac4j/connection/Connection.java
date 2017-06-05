package com.samsanort.yac4j.connection;

import java.io.IOException;

/**
 * Created by samu on 5/9/17.
 */
public interface Connection {

    /**
     * @return
     * @throws IOException
     */
    String readAll() throws IOException;
}
