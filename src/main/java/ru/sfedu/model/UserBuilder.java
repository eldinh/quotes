package ru.sfedu.model;

import java.util.List;

public class UserBuilder {

    private String id;

    private String name;

    private List<Security> tickerList;

    private List<Action> actionHistory;

    public UserBuilder(){}

    public UserBuilder(String id){
        this.id = id;
    }

    public UserBuilder(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.tickerList = user.getSecurityList();
        this.actionHistory = user.getActionHistory();
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withTickerList(List<Security> tickerList) {
        this.tickerList = tickerList;
        return this;
    }

    public UserBuilder withActionHistory(List<Action> actionHistory) {
        this.actionHistory = actionHistory;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Security> getTickerList() {
        return tickerList;
    }

    public List<Action> getActionHistory() {
        return actionHistory;
    }

    public User build(){
        return new User(this);
    }
}
