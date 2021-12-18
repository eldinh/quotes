package ru.sfedu.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import ru.sfedu.utils.HistoryCsvConverter;

import java.util.Objects;

public class Security {

    @Attribute
    @CsvBindByName
    protected String ticker;
    @Attribute
    @CsvBindByName
    protected String name;
    @Attribute
    @CsvBindByName
    protected String shortName;
    @Attribute
    @CsvBindByName
    protected String latName;
    @Attribute
    @CsvBindByName
    protected double nominal;
    @Attribute
    @CsvBindByName
    protected String nominalValue;
    @Attribute
    @CsvBindByName
    protected String issueDate;
    @Attribute(required = false)
    @CsvBindByName
    protected String isin;
    @Attribute
    @CsvBindByName
    protected long issueSize;
    @Attribute
    @CsvBindByName
    protected MarketType marketType;  // тип биржи
    @Element(name = "SecurityHistory")
    @CsvCustomBindByName(converter = HistoryCsvConverter.class)
    protected SecurityHistory history;


    public Security(MarketType marketType){
        this.marketType = marketType;
    }
    @Override
    public String toString() {
        return "Security{" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", latName='" + latName + '\'' +
                ", nominal=" + nominal +
                ", nominalValue='" + nominalValue + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", isin='" + isin + '\'' +
                ", issueSize=" + issueSize +
                ", marketType=" + marketType +
                ", history=" + history +
                '}';
    }


    // builder instead of setters


    public SecurityHistory getHistory() {
        return history;
    }

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


    public Security(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize, SecurityHistory history) {
        this.ticker = ticker;
        this.name = name;
        this.shortName = shortName;
        this.latName = latName;
        this.nominal = nominal;
        this.nominalValue = nominalValue;
        this.issueDate = issueDate;
        this.isin = isin;
        this.issueSize = issueSize;
        this.history = history;
    }

    public void setHistory(SecurityHistory history) {
        this.history = history;
    }

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
