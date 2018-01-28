package com.samsanort.yac4j;

import java.util.List;

public interface UrlRepository {

    List<String> getAllVisitable();

    List<String> getAllVisited();

    void addVisitable(String url);

    void addVisited(String url);

    void deleteVisitable(String url);
}
