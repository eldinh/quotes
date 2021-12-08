package ru.sfedu.builder;

import ru.sfedu.entity.Stock;
import ru.sfedu.model.MarketName;

public class StockBuilder implements IStockBuilder , ISecurityBuilder {
    private String ticker;
    private String name;
    private String shortName;
    private String latName;
    private double nominal;
    private String nominalValue;
    private String issueDate;
    private String isin;
    private long issueSize;
    private MarketName marketName;  // тип биржи
    private Stock.StockType type;
    private double dividendSum;
    private double capitalization;

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setLatName(String latName) {
        this.latName = latName;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public void setNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public void setIssueSize(long issueSize) {
        this.issueSize = issueSize;
    }

    public void setMarketName(MarketName marketName) {
        this.marketName = marketName;
    }

    public void setType(Stock.StockType type) {
        this.type = type;
    }

    public void setDividendSum(double dividendSum) {
        this.dividendSum = dividendSum;
    }

    public void setCapitalization(double capitalization) {
        this.capitalization = capitalization;
    }

    public Stock getResult(){
        return new Stock(ticker, name, shortName, latName,
                        nominal, nominalValue, issueDate, isin,
                        issueSize, marketName, type, dividendSum, capitalization);
    }
}
