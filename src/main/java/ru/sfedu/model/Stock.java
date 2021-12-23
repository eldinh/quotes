package ru.sfedu.model;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;


public class Stock extends Security {
    @Attribute(empty = "")
    @CsvBindByPosition(position = 11)
    protected StockType type;
    @Attribute
    @CsvBindByPosition(position = 12)
    protected double dividendSum;
    @Attribute
    @CsvBindByPosition(position = 13)
    protected double capitalization;


    public Stock(StockBuilder stockBuilder){
        super(stockBuilder.getTicker(), stockBuilder.getName(), stockBuilder.getShortName(), stockBuilder.getLatName(), stockBuilder.getNominal(), stockBuilder.getNominalValue(), stockBuilder.getIssueDate(), stockBuilder.getIsin(), stockBuilder.getIssueSize(), stockBuilder.getSecurityHistory());
        this.marketType = MarketType.SHARES;
        this.type = stockBuilder.getType();
        this.dividendSum = stockBuilder.getDividendSum();
        this.capitalization = stockBuilder.getCapitalization();
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
