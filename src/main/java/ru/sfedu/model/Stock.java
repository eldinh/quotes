package ru.sfedu.model;
import com.opencsv.bean.CsvBindByName;
import org.simpleframework.xml.Attribute;


public class Stock extends Security {
    @Attribute(empty = "")
    @CsvBindByName
    protected StockType type;
    @Attribute
    @CsvBindByName
    protected double dividendSum;
    @Attribute
    @CsvBindByName
    protected double capitalization;


    public enum StockType {
        COMMON, PREFERRED
    }


    public Stock(String ticker, String name, String shortName, String latName, double nominal, String nominalValue, String issueDate, String isin, long issueSize,  SecurityHistory history, StockType type, double dividendSum, double capitalization) {
        super(ticker, name, shortName, latName, nominal, nominalValue, issueDate, isin, issueSize, history);
        this.marketType = MarketType.SHARES;
        this.type = type;
        this.dividendSum = dividendSum;
        this.capitalization = capitalization;
    }

    public Stock(){
        super(MarketType.SHARES);
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
