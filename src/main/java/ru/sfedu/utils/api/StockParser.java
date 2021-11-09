package ru.sfedu.utils.api;

import java.net.URISyntaxException;

public interface StockParser {
    public String fetch() throws URISyntaxException, Exception;
}
