package ru.sfedu;

import org.apache.http.client.utils.URIBuilder;

import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        URIBuilder uri = new URIBuilder("/");
        uri.setFragment("/he");
        uri.setFragment("hheee");
        uri.setFragment("qqqq");
        System.out.println(uri);

    }
}
