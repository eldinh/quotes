package ru.sfedu.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Moex {
    private List<String> path;
    private List<NameValuePair> params;
    private URIBuilder uri;
    private final Logger log = (Logger) LogManager.getLogger(Moex.class.getName());
    public Moex() {
        path = new ArrayList<>();
        params = new ArrayList<>();
    }
    protected Moex(List<String> path) {
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

    public Moex addParameter(String name, String value){
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    public Moex addParameter(String name, int value){
        params.add(new BasicNameValuePair(name, Integer.toString(value)));
        return this;
    }

    private List<String> pathWithFormat(){
        int len = path.size();
        if (len == 0)
            return new ArrayList<>(List.of(".json"));
        List<String> listWithFormat = new ArrayList<>(path);
        listWithFormat.set(len - 1, listWithFormat.get(len - 1) + ".json");
        return listWithFormat;
    }

    public String fetch() throws Exception {
        log.info("Fetch json");
        URIBuilder uri = new URIBuilder();
        uri.setScheme("https");
        uri.setHost("iss.moex.com/iss");
        uri.setPathSegments(pathWithFormat());

        uri.setParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet request = new HttpGet(uri.build());
        CloseableHttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();

        log.debug("Sending 'get' request to URL:  " + uri);

        log.debug("Response code: " + response.getStatusLine().getStatusCode());

        String result = "";
        if (entity != null) {
            // return it as a String
            result = EntityUtils.toString(entity);
        }
        
        return result;
    }
}
