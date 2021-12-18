package ru.sfedu.model;

import com.opencsv.bean.CsvBindByName;
public class Bond extends Security {
    @CsvBindByName
    private BondType type;
    @CsvBindByName
    private String matDate; // Дата погашения
    @CsvBindByName
    private double coupon;
    @CsvBindByName
    private int dayToRedemption;

    public Bond(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, SecurityHistory history, BondType type, String matDate, double coupon, int dayToRedemption) {
        super(ticker, name, shortName, latName, nominal, nominalValue, issueDate, isin, issueSize, history);
        this.marketType = MarketType.BONDS;
        this.type = type;
        this.matDate = matDate;
        this.coupon = coupon;
        this.dayToRedemption = dayToRedemption;
    }

    public enum BondType {
        SUBFEDERAL, MUNICIPAL, CORPORATE,
        EXCHANGE, IFI, EURO, OFZ, CB, NON_EXCHANGE
    }

    public Bond(){
        super(MarketType.BONDS);
    }


    public String getMatDate() {
        return matDate;
    }

    public Double getCoupon() {
        return coupon;
    }

    public Integer getDayToRedemption() {
        return dayToRedemption;
    }

    public BondType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Bond [" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isin='" + isin + '\'' +
                ", nominal=" + nominal +
                ", nominalValue='" + nominalValue + '\'' +
                ", issueDate=" + issueDate +
                ", latName='" + latName + '\'' +
                ", issueSize=" + issueSize +
                ", group='" + marketType + '\'' +
                ", type=" + type + '\'' +
                ", matDate='" + matDate + '\'' +
                ", coupon='" + coupon + '\'' +
                ", dayToRedemption='" + dayToRedemption + '\'' +
                ", type='" + type + '\'' +
                ']';
    }
}
