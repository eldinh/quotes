package ru.sfedu.builder;

import ru.sfedu.model.MarketType;
import ru.sfedu.model.Security;
import ru.sfedu.model.SecurityHistory;

import java.util.List;

public class SecurityBuilder {
    protected String ticker;
    protected String name;
    protected String shortName;
    protected String latName;
    protected double nominal;
    protected String nominalValue;
    protected String issueDate;
    protected String isin;
    protected long issueSize;
    protected MarketType marketType;  // тип биржи
    protected String matDate; // Дата погашения
    protected SecurityHistory securityHistory;


    public SecurityBuilder withTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }


    public SecurityBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public SecurityBuilder withShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }


    public SecurityBuilder withLatName(String latName) {
        this.latName = latName;
        return this;
    }


    public SecurityBuilder withNominal(double nominal) {
        this.nominal = nominal;
        return this;
    }


    public SecurityBuilder withNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
        return this;
    }


    public SecurityBuilder withIssueDate(String issueDate) {
        this.issueDate = issueDate;
        return this;
    }


    public SecurityBuilder withIsin(String isin) {
        this.isin = isin;
        return this;
    }


    public SecurityBuilder withIssueSize(long issueSize) {
        this.issueSize = issueSize;
        return this;
    }


    public SecurityBuilder withMarketType(MarketType marketType) {
        this.marketType = marketType;
        return this;
    }
    public SecurityBuilder withSecurityHistory(SecurityHistory securityHistory) {
        this.securityHistory = securityHistory;
        return this;
    }

    public Security build(){
        return new Security(ticker, name, shortName, latName,
                nominal, nominalValue, issueDate, isin,
                issueSize, marketType, securityHistory);
    }




}
