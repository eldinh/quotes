package ru.sfedu.utils.api;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Moex implements StockParser {
    private List<String> path;
    private List<NameValuePair> params;
    private URIBuilder uri;
    public Moex() {
        path = new ArrayList<>();
        params = new ArrayList<>();
    }
    public Moex(List<String> path) {
        this();

        this.path = path;
    }

    public Moex addPath(String str){
        ArrayList<String> list = new ArrayList<>(path);
        list.add(str);
        return new Moex(list);
    }

    public Moex history(){
        return addPath("history");
    }

    public Moex securities(){
        return addPath("securities");
    }

    public Moex securities(String security){
        return securities().addPath(security);
    }

    public Moex engines(){
        return addPath("engines");
    }

    public Moex engines(String engine){
        return engines().addPath(engine);
    }

    public Moex markets(){
        return addPath("markets");
    }

    public Moex markets(String market){
        return markets().addPath(market);
    }

    public Moex turnovers(){
        return addPath("turnovers");
    }

    public Moex turnovers(String param){
        return turnovers().addPath(param);
    }

    public Moex boards(){
        return addPath("boards");
    }

    public Moex boards(String board){
        return boards().addPath(board);
    }

    public Moex boardgroups(){
        return addPath("boardgroups");
    }

    public Moex boardgroups(String boardgroup){
        return boardgroups().addPath(boardgroup);
    }

    public Moex sessions(){
        return addPath("sessions");
    }

    public Moex sessions(String session){
        return sessions().addPath(session);
    }

    public Moex trades(){
        return addPath("trades");
    }

    public void addParameter(String name, String value){
        params.add(new BasicNameValuePair(name, value));
    }

    @Override
    public String fetch() throws Exception {
        URIBuilder uri = new URIBuilder("https://iss.moex.com/iss");
        uri.setScheme("https");
        uri.setHost("iss.moex.com/iss");
        uri.setPathSegments(path);
        uri.setParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet request = new HttpGet(uri.build());
        CloseableHttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();

        System.out.println("Sending 'get' request to URL:  " + uri);

        System.out.println("Response code: " + response.getStatusLine().getStatusCode());

        String result = "";
        if (entity != null) {
            // return it as a String
            result = EntityUtils.toString(entity);
            System.out.println(result);
        }
        
        return result;
    }
}
