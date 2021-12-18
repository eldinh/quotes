
package ru.sfedu.model;


public class BondBuilder {
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
    private double coupon;
    private int dayToRedemption;
    private String matDate;
    private Bond.BondType type;

    public BondBuilder(){}

    public BondBuilder withTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }


    public BondBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public BondBuilder withShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }


    public BondBuilder withLatName(String latName) {
        this.latName = latName;
        return this;
    }


    public BondBuilder withNominal(double nominal) {
        this.nominal = nominal;
        return this;
    }


    public BondBuilder withNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
        return this;
    }


    public BondBuilder withIssueDate(String issueDate) {
        this.issueDate = issueDate;
        return this;
    }


    public BondBuilder withIsin(String isin) {
        this.isin = isin;
        return this;
    }


    public BondBuilder withIssueSize(long issueSize) {
        this.issueSize = issueSize;
        return this;
    }


    public BondBuilder withMatDate(String matDate) {
        this.matDate = matDate;
        return this;
    }

    public BondBuilder withCoupon(Double coupon) {
        this.coupon = coupon;
        return this;
    }

    public BondBuilder withDayToRedemption(Integer dayToRedemption) {
        this.dayToRedemption = dayToRedemption;
        return this;
    }


    public BondBuilder withType(Bond.BondType type) {
        this.type = type;
        return this;
    }

    public Bond build(){
        return new Bond(ticker, name, shortName, latName,
                nominal, nominalValue, issueDate, isin,
                issueSize,securityHistory, type, matDate,
                coupon, dayToRedemption);
    }

    public BondBuilder withSecurityHistory(SecurityHistory securityHistory) {
        this.securityHistory = securityHistory;
        return this;
    }
}