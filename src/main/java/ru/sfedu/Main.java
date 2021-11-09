package ru.sfedu;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import ru.sfedu.api.Moex;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static  void main(String[] args) throws Exception {
        Moex moex = new Moex();
        Moex m = moex.securities();
        String str = moex.securities().addParameter("group_by", "group")
                .addParameter("group_by_filter", "stock_shares")
                .addParameter("is_tranding", "1").addParameter("q", "RU000")
                .addParameter("start", "0").fetch();
        System.out.println(str);
    }
}
