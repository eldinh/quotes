package ru.sfedu.builder;

import ru.sfedu.model.Stock;
import ru.sfedu.model.MarketType;

public class StockBuilder extends SecurityBuilder {
    private Stock.StockType type;
    private double dividendSum;
    private double capitalization;

    public StockBuilder(){}

    public StockBuilder(SecurityBuilder securityBuilder){
        this.ticker = securityBuilder.ticker;
        this.name = securityBuilder.name;
        this.shortName = securityBuilder.shortName;
        this.latName = securityBuilder.latName;
        this.nominal = securityBuilder.nominal;
        this.nominalValue = securityBuilder.nominalValue;
        this.issueDate = securityBuilder.issueDate;
        this.isin = securityBuilder.isin;
        this.issueSize = securityBuilder.issueSize;
        this.marketType = securityBuilder.marketType;
    }

    public StockBuilder withTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public StockBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public StockBuilder withShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public StockBuilder withLatName(String latName) {
        this.latName = latName;
        return this;
    }

    public StockBuilder withNominal(double nominal) {
        this.nominal = nominal;
        return this;
    }

    public StockBuilder withNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
        return this;
    }

    public StockBuilder withIssueDate(String issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public StockBuilder withIsin(String isin) {
        this.isin = isin;
        return this;
    }

    public StockBuilder withIssueSize(long issueSize) {
        this.issueSize = issueSize;
        return this;
    }

    public StockBuilder withMarketName(MarketType marketName) {
        this.marketType = marketName;
        return this;
    }

    public StockBuilder withType(Stock.StockType type) {
        this.type = type;
        return this;
    }

    public StockBuilder withDividendSum(double dividendSum) {
        this.dividendSum = dividendSum;
        return this;
    }

    public StockBuilder withCapitalization(double capitalization) {
        this.capitalization = capitalization;
        return this;
    }

    public Stock build(){
        return new Stock(ticker, name, shortName, latName,
                        nominal, nominalValue, issueDate, isin,
                        issueSize, marketType, type, dividendSum, capitalization);
    }
}
