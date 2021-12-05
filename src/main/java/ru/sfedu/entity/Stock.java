package ru.sfedu.entity;

import com.opencsv.bean.CsvBindByPosition;

public class Stock extends Security {
    @CsvBindByPosition(position = 10)
    protected StockType type;
    @CsvBindByPosition(position = 11)
    protected double dividendSum;
    @CsvBindByPosition(position = 12)
    protected double capitalization;

    public Stock(){}

    public enum StockType {
        common_share, preferred_share
    }


    public Stock(Security security){
        this.ticker = security.getTicker();
        this.name = security.getName();
        this.shortName = security.getShortName();
        this.isin = security.getIsin();
        this.nominal = security.getNominal();
        this.nominalValue = security.getNominalValue();
        this.issueDate = security.getIssueDate();
        this.latName = security.getLatName();
        this.issueSize = security.getIssueSize();
        this.group = security.group;
    }

    public StockType getType() {
        return type;
    }

    public void setType(StockType type) {
        this.type = type;
    }

    public double getDividendSum() {
        return dividendSum;
    }

    public void setDividendSum(double dividendSum) {
        this.dividendSum = dividendSum;
    }

    public double getCapitalization() {
        return capitalization;
    }

    public void setCapitalization(double capitalization) {
        this.capitalization = capitalization;
    }

    @Override
    public String toString() {
        return "Stock [" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isin='" + isin + '\'' +
                ", nominal=" + nominal +
                ", nominalValue='" + nominalValue + '\'' +
                ", issueDate=" + issueDate +
                ", latName='" + latName + '\'' +
                ", issueSize=" + issueSize +
                ", group='" + group + '\'' +
                ", type=" + type + '\'' +
                ", dividendSum=" + dividendSum + '\'' +
                ", capitalization=" + capitalization + '\'' +
                ']';
    }

}
