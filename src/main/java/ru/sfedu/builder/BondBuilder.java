package ru.sfedu.builder;


import ru.sfedu.model.Bond;
import ru.sfedu.model.MarketType;

public class BondBuilder extends SecurityBuilder {
    private double coupon;
    private int dayToRedemption;
    private String matDate;
    private Bond.BondType type;

    public BondBuilder(){}

    public BondBuilder(SecurityBuilder securityBuilder){
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


    public BondBuilder withMarketName(MarketType marketName) {
        this.marketType = marketName;
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
                issueSize, marketType, type, matDate,
                coupon, dayToRedemption);
    }
}
