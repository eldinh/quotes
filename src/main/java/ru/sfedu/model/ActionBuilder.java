package ru.sfedu.model;


public class ActionBuilder {
    private String id;
    private String date;
    private ActionType action;
    private String userID;
    private Security security;


    public ActionBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ActionBuilder withDate(String date) {
        this.date = date;
        return this;
    }

    public ActionBuilder withAction(ActionType action) {
        this.action = action;
        return this;
    }

    public ActionBuilder withUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public ActionBuilder withSecurity(Security security) {
        this.security = security;
        return this;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Action build(){
        return new Action(this);
    }
}
