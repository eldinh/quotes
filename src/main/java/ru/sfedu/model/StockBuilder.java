package ru.sfedu.model;

public class StockBuilder {
    protected String ticker;
    protected String name;
    protected String shortName;
    protected String latName;
    protected double nominal;
    protected String nominalValue;
    protected String issueDate;
    protected String isin;
    protected long issueSize;
    protected SecurityHistory securityHistory;
    private Stock.StockType type;
    private double dividendSum;
    private double capitalization;

    public StockBuilder(){}

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

    public StockBuilder withType(Stock.StockType type) {
        this.type = type;
        return this;
    }

    public StockBuilder withDividendSum(double dividendSum) {
        this.dividendSum = dividendSum;
        return this;
    }

    public StockBuilder withSecurityHistory(SecurityHistory securityHistory){
        this.securityHistory = securityHistory;
        return this;
    }

    public StockBuilder withCapitalization(double capitalization) {
        this.capitalization = capitalization;
        return this;
    }

    public Stock build(){
        return new Stock(ticker, name, shortName, latName,
                nominal, nominalValue, issueDate, isin,
                issueSize,securityHistory , type, dividendSum, capitalization);
    }
}
