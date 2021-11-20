package ru.sfedu.model.entity;

import java.util.Objects;

public class Stock {
    private String secID;
    private String shortName;
    private String name;
    private boolean isTraded;
    private boolean type;

    Stock(){}

    public String getSecID() {
        return secID;
    }

    public void setSecID(String secID) {
        this.secID = secID;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isTraded() {
        return isTraded;
    }

    public void setTraded(boolean traded) {
        isTraded = traded;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
