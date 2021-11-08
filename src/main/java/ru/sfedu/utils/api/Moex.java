package ru.sfedu.utils.api;

public class Moex implements StockParser {
    private String fragment;

    public Moex()  {
    }
    public Moex(String fragment) {
        this.fragment = fragment;
    }
    public String getFragment() {
        return fragment;
    }
    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public Moex history(){
        return new Moex(fragment + "/history");
    }

    public Moex securities(){
        return new Moex(fragment + "/securities");
    }

    public Moex securities(String security){
        return new Moex(fragment + "/securities/" + security);
    }

    public Moex engines(String engine){
        return new Moex(fragment + "/engine/" + engine);
    }

    public Moex markets(){
        return new Moex(fragment + "/markets");
    }

    public Moex markets(String market){
        return new Moex(fragment + "/markets/" + market);
    }

    public Moex turnovers(){
        return new Moex(fragment + "/turnovers");
    }

    public Moex turnovers(String param){
        return new Moex(fragment + "/turnovers/" + param);
    }

    public Moex boards(){
        return new Moex(fragment + "/boards");
    }

    public Moex boards(String board){
        return new Moex(fragment + "/boards/" + board);
    }

    public Moex boardgroups(){
        return new Moex(fragment + "/boardgroups");
    }

    public Moex boardgroups(String boardgroup){
        return new Moex(fragment + "/boardgroups/" + boardgroup);
    }

    public Moex sessions(){
        return new Moex(fragment + "/sessions");
    }

    public Moex sessions(String session){
        return new Moex(fragment + "/sessions/" + session);
    }

    public Moex trades(){
        return new Moex(fragment + "/trades");
    }

    @Override
    public String fetch() {
        return null;
    }
}
