package ru.sfedu.entity;

import com.opencsv.bean.CsvBindByPosition;
import ru.sfedu.model.MarketName;

public class Stock extends Security {
    @CsvBindByPosition(position = 10)
    protected final StockType type;
    @CsvBindByPosition(position = 11)
    protected final double dividendSum;
    @CsvBindByPosition(position = 12)
    protected final double capitalization;

    public enum StockType {
        common_share, preferred_share
    }




    public Stock(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, MarketName marketName, StockType type, double dividendSum, double capitalization) {
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
                ", group='" + marketName + '\'' +
                ", type=" + type + '\'' +
                ", dividendSum=" + dividendSum + '\'' +
                ", capitalization=" + capitalization + '\'' +
                ']';
    }

}
