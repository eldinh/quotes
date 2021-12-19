package ru.sfedu.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;

import java.util.Objects;

public class SecurityHistory {
    @Attribute
    @CsvBindByPosition(position = 0)
    private String date;
    @Attribute
    @CsvBindByPosition(position = 1)
    private String ticker;
    @Attribute
    @CsvBindByPosition(position = 2)
    private double averagePerDay;
    @Attribute
    @CsvBindByPosition(position = 3)
    private double openPrice;
    @Attribute
    @CsvBindByPosition(position = 4)
    private double closePrice;
    @Attribute
    @CsvBindByPosition(position = 5)
    private long volume;

    // http://www.netinvestor.ru/manual_nipro_A.aspx

    public SecurityHistory(String date, double averagePerDay, String ticker, double openPrice, double closePrice, long volume) {
        this.date = date;
        this.averagePerDay = averagePerDay;
        this.ticker = ticker;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.volume = volume;


    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }



    public SecurityHistory(){}

    public String getTicker() {
        return ticker;
    }

    public String getDate() {
        return date;
    }

    public double getAveragePerDay() {
        return averagePerDay;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public long getVolume() {
        return volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityHistory that = (SecurityHistory) o;
        return Double.compare(that.averagePerDay, averagePerDay) == 0 && Double.compare(that.openPrice, openPrice) == 0 && Double.compare(that.closePrice, closePrice) == 0 && volume == that.volume && Objects.equals(date, that.date) && Objects.equals(ticker, that.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, ticker, averagePerDay, openPrice, closePrice, volume);
    }

    @Override
    public String toString() {
        return "SecurityHistory{" +
                "date='" + date + '\'' +
                ", ticker='" + ticker + '\'' +
                ", averagePerDay=" + averagePerDay +
                ", openPrice=" + openPrice +
                ", closePrice=" + closePrice +
                ", volume=" + volume +
                '}';
    }
}
