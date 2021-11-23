package ru.sfedu.model.entity;

import java.util.ArrayList;

public class Market {
    private ArrayList<Security> securityList;
    private String name;
    public Market(){}

    public ArrayList<Security> getSecurityList() {
        return securityList;
    }

    public void setSecurityList(ArrayList<Security> securityList) {
        this.securityList = securityList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
