package ru.sfedu.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import java.util.List;

public class Stock extends Security {
    @CsvBindByName
    protected StockType type;
    @CsvBindByName
    protected double dividendSum;
    @CsvBindByName
    protected double capitalization;

    public Stock(){
    }

    public enum StockType {
        COMMON, PREFERRED
    }


    public Stock(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, MarketType marketType, SecurityHistory history, StockType type, double dividendSum, double capitalization) {
        super(ticker, name, shortName, latName, nominal, nominalValue, issueDate, isin, issueSize, marketType, history);
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
                ", history =" + history + '\'' +
                ", type=" + type + '\'' +
                ", dividendSum=" + dividendSum + '\'' +
                ", capitalization=" + capitalization + '\'' +
                ']';
    }

}
