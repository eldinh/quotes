package ru.sfedu.entity;

import com.opencsv.bean.CsvBindByPosition;
import ru.sfedu.model.MarketName;

public class Bond extends Security{
    @CsvBindByPosition(position = 10)
    private BondType type;
    @CsvBindByPosition(position = 11)
    private String matDate; // Дата погашения
    @CsvBindByPosition(position = 12)
    private Double coupon;
    @CsvBindByPosition(position = 13)
    private Integer dayToRedemption;

    public Bond(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, MarketName marketName, BondType type, String matDate, Double coupon, Integer dayToRedemption) {
        super(ticker, name, shortName, latName, nominal, nominalValue, issueDate, isin, issueSize, marketName);
        this.type = type;
        this.matDate = matDate;
        this.coupon = coupon;
        this.dayToRedemption = dayToRedemption;
    }

    public enum BondType {
        subfederal_bond, municipal_bond, corporate_bond,
        exchange_bond, ifi_bond, euro_bond, ofz_bond, cb_bond, non_exchange_bond
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
                ", group='" + marketName + '\'' +
                ", type=" + type + '\'' +
                ", matDate='" + matDate + '\'' +
                ", coupon='" + coupon + '\'' +
                ", dayToRedemption='" + dayToRedemption + '\'' +
                ", type='" + type + '\'' +
                ']';
    }
}
