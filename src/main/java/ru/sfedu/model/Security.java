package ru.sfedu.model;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import ru.sfedu.utils.HistoryCsvConverter;

import java.util.Objects;

public class Security {

    @Attribute
    @CsvBindByPosition(position = 0)
    protected String ticker;
    @Attribute
    @CsvBindByPosition(position = 1)
    protected String name;
    @Attribute
    @CsvBindByPosition(position = 2)
    protected String shortName;
    @Attribute
    @CsvBindByPosition(position = 3)
    protected String latName;
    @Attribute
    @CsvBindByPosition(position = 4)
    protected double nominal;
    @Attribute
    @CsvBindByPosition(position = 5)
    protected String nominalValue;
    @Attribute
    @CsvBindByPosition(position = 6)
    protected String issueDate; // дата выпуска
    @Attribute(required = false)
    @CsvBindByPosition(position = 7)
    protected String isin; // Международный идентификационный код ценной бумаги
    @Attribute
    @CsvBindByPosition(position = 8)
    protected long issueSize; // объем ценной бумаги
    @Attribute
    @CsvBindByPosition(position = 9)
    protected MarketType marketType;  // тип биржи
    @Element(name = "SecurityHistory")
    @CsvCustomBindByPosition(position = 10,converter = HistoryCsvConverter.class)
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
