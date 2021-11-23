package ru.sfedu.model.entity;

import java.util.Objects;

public class Stock extends Security {
    protected double dividendSum;
    protected double capitalization;
    Stock(){};

    public double getDividendSum() {
        return dividendSum;
    }

    public void setDividendSum(double dividendSum) {
        this.dividendSum = dividendSum;
    }

    public double getCapitalization() {
        return capitalization;
    }

    public void setCapitalization(double capitalization) {
        this.capitalization = capitalization;
    }
}
