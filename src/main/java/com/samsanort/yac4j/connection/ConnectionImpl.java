package com.samsanort.yac4j.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by samu on 5/9/17.
 */
public class ConnectionImpl implements Connection {

    private Proxy proxy;
    private String url;
    private String userAgent;

    /**
     * @param url
     * @param proxy
     */
    public ConnectionImpl(String url, Proxy proxy, String userAgent) {

        this.url = url;
        this.proxy = proxy;
        this.userAgent = userAgent;
    }

    @Override
    public String readAll() throws IOException {

        URLConnection con = this.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        StringBuilder strBld = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            strBld.append(line).append("\n");
        }

        return strBld.toString();
    }

    private URLConnection openConnection() throws IOException {

        URL url = new URL(this.url);

        URLConnection conn;

        if (this.proxy != null) {
            conn = url.openConnection(this.proxy);

        } else {
            conn = url.openConnection(Proxy.NO_PROXY);
        }

        conn.setRequestProperty("User-Agent", userAgent);

        return conn;
    }
}
