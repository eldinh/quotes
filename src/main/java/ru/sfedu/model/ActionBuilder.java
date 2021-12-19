package ru.sfedu.model;


public class ActionBuilder {
    private String date;
    private ActionType action;
    private String userID;
    private Security security;


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

    public Action build(){
        return new Action(date, action, userID, security);
    }
}
