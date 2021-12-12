package ru.sfedu.model;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Objects;

public class Security {

    @CsvBindByPosition(position = 0)
    protected String ticker;
    @CsvBindByPosition(position = 1)
    protected String name;
    @CsvBindByPosition(position = 2)
    protected String shortName;
    @CsvBindByPosition(position = 3)
    protected String latName;
    @CsvBindByPosition(position = 4)
    protected double nominal;
    @CsvBindByPosition(position = 5)
    protected String nominalValue;
    @CsvBindByPosition(position = 6)
    protected String issueDate;
    @CsvBindByPosition(position = 7)
    protected String isin;
    @CsvBindByPosition(position = 8)
    protected long issueSize;
    @CsvBindByPosition(position = 9)
    protected MarketType marketType;  // тип биржи




    @Override
    public String toString() {
        return "Security [" +
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
                ']';
    }
    // builder instead of setters


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Security security = (Security) o;
        return Objects.equals(ticker, security.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    public Security(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, MarketType marketType) {
        this.ticker = ticker;
        this.name = name;
        this.shortName = shortName;
        this.latName = latName;
        this.nominal = nominal;
        this.nominalValue = nominalValue;
        this.issueDate = issueDate;
        this.isin = isin;
        this.issueSize = issueSize;
        this.marketType = marketType;
    }

    public Security(){}

    public MarketType getMarketType() {
        return marketType;
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


}
