package ru.sfedu;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        URIBuilder uri = new URIBuilder();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("start","0"));
        List<String> path = new ArrayList<>();

        path.add("index");
        path.set(path.size() - 1, path.get(path.size() - 1) + ".json");

        uri.setScheme("https");
        uri.setHost("iss.moex.com/iss");
        uri.setPathSegments(path);
        System.out.println(uri);
        System.out.println(uri);
        uri.addParameters(params);
        System.out.println(uri.build());

    }
}
