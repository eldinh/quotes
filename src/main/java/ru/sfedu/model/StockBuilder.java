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
    private StockType type;
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

    public StockBuilder withType(StockType type) {
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

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLatName() {
        return latName;
    }

    public double getNominal() {
        return nominal;
    }

    public String getNominalValue() {
        return nominalValue;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getIsin() {
        return isin;
    }

    public long getIssueSize() {
        return issueSize;
    }

    public SecurityHistory getSecurityHistory() {
        return securityHistory;
    }

    public StockType getType() {
        return type;
    }

    public double getDividendSum() {
        return dividendSum;
    }

    public double getCapitalization() {
        return capitalization;
    }

    public Stock build(){
        return new Stock(this);
    }
}
