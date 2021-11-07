package ru.sfedu.utils;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class Parser {

    public static void main(String[] args) throws Exception {

        Parser http = new Parser();

        System.out.println("Testing 1 - Send Http GET request");
        http.sendGet();
    }


        private void sendGet() throws  Exception {

            String url = "https://iss.moex.com/iss/history/engines/stock/markets/shares/securities/SBER.json?from=2013-11-21" ;

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            System.out.println("Sending 'get' request to URL:  " + url);
            System.out.println("Response code: " + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(entity.getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line).append("\n");
            }

            System.out.println(result);





    }

}
