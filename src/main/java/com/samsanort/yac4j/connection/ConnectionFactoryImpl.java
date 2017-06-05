package com.samsanort.yac4j.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

/**
 * Created by samu on 2/28/17.
 */
public class ConnectionFactoryImpl implements ConnectionFactory {

    private Proxy proxy;

    /**
     * C'tor for obtaining connections through a proxy
     *
     * @param proxyAddress
     * @param proxyPort
     */
    public ConnectionFactoryImpl(String proxyAddress, int proxyPort) {

        SocketAddress addr = new InetSocketAddress(proxyAddress, proxyPort);
        this.proxy = new Proxy(Proxy.Type.HTTP, addr);
    }

    /**
     * C'tor for use no proxy
     */
    public ConnectionFactoryImpl() {
    }

    /**
     * @param strUrl
     * @return
     */
    public Connection obtainConnection(String strUrl) throws IOException {

        if (this.proxy != null) {
            return new ConnectionImpl(strUrl, this.proxy);

        } else {
            return new ConnectionImpl(strUrl, Proxy.NO_PROXY);
        }
    }
}
