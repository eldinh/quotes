package ru.sfedu.model;

import com.opencsv.bean.CsvBindByPosition;

public class Stock extends Security {
    @CsvBindByPosition(position = 10)
    protected StockType type;
    @CsvBindByPosition(position = 11)
    protected double dividendSum;
    @CsvBindByPosition(position = 12)
    protected double capitalization;

    public Stock(){
    }

    public enum StockType {
        COMMON, PREFERRED
    }




    public Stock(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, MarketType marketName, StockType type, double dividendSum, double capitalization) {
        super(ticker, name, shortName, latName, nominal, nominalValue, issueDate, isin, issueSize, marketName);
        this.type = type;
        this.dividendSum = dividendSum;
        this.capitalization = capitalization;
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
                ", group='" + marketType + '\'' +
                ", type=" + type + '\'' +
                ", dividendSum=" + dividendSum + '\'' +
                ", capitalization=" + capitalization + '\'' +
                ']';
    }

}
