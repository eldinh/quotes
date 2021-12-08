package ru.sfedu.builder;


import ru.sfedu.entity.Bond;
import ru.sfedu.model.MarketName;

public class BondBuilder implements ISecurityBuilder, IBondBuilder {
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
    private String matDate; // Дата погашения
    private Double coupon;
    private Integer dayToRedemption;
    private Bond.BondType type;

    @Override
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public void setLatName(String latName) {
        this.latName = latName;
    }

    @Override
    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    @Override
    public void setNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
    }

    @Override
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public void setIsin(String isin) {
        this.isin = isin;
    }

    @Override
    public void setIssueSize(long issueSize) {
        this.issueSize = issueSize;
    }

    @Override
    public void setMarketName(MarketName marketName) {
        this.marketName = marketName;
    }

    public void setMatDate(String matDate) {
        this.matDate = matDate;
    }

    public void setCoupon(Double coupon) {
        this.coupon = coupon;
    }

    public void setDayToRedemption(Integer dayToRedemption) {
        this.dayToRedemption = dayToRedemption;
    }

    public void setType(Bond.BondType type) {
        this.type = type;
    }

    public Bond getResult(){
        return new Bond(ticker, name, shortName, latName,
                nominal, nominalValue, issueDate, isin,
                issueSize, marketName, type, matDate,
                coupon, dayToRedemption);
    }
}
