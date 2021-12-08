package ru.sfedu.entity;

import com.opencsv.bean.CsvBindByPosition;
import ru.sfedu.model.MarketName;

import java.util.Objects;

public class Security {

    @CsvBindByPosition(position = 0)
    protected final String ticker;
    @CsvBindByPosition(position = 1)
    protected final String name;
    @CsvBindByPosition(position = 2)
    protected final String shortName;
    @CsvBindByPosition(position = 3)
    protected final String latName;
    @CsvBindByPosition(position = 4)
    protected final double nominal;
    @CsvBindByPosition(position = 5)
    protected final String nominalValue;
    @CsvBindByPosition(position = 6)
    protected final String issueDate;
    @CsvBindByPosition(position = 7)
    protected final String isin;
    @CsvBindByPosition(position = 8)
    protected final long issueSize;
    @CsvBindByPosition(position = 9)
    protected final MarketName marketName;  // тип биржи



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
                ", group='" + marketName + '\'' +
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

    public Security(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, MarketName marketName) {
        this.ticker = ticker;
        this.name = name;
        this.shortName = shortName;
        this.latName = latName;
        this.nominal = nominal;
        this.nominalValue = nominalValue;
        this.issueDate = issueDate;
        this.isin = isin;
        this.issueSize = issueSize;
        this.marketName = marketName;
    }

    public MarketName getMarketName() {
        return marketName;
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
