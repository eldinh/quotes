package ru.sfedu.builder;


import ru.sfedu.model.SecurityHistory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SecurityHistoryBuilder {
    private String date;
    private String ticker;
    private double averagePerDay;
    private double openPrice;
    private double closePrice;
    private long volume;

    public SecurityHistoryBuilder withDate(String date) {
        this.date = date;
        return this;
    }

    public SecurityHistoryBuilder withTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public SecurityHistoryBuilder withAveragePerDay(double averagePerDay) {
        this.averagePerDay = averagePerDay;
        return this;
    }

    public SecurityHistoryBuilder withOpenPrice(double openPrice) {
        this.openPrice = openPrice;
        return this;
    }

    public SecurityHistoryBuilder withClosePrice(double closePrice) {
        this.closePrice = closePrice;
        return this;
    }

    public SecurityHistoryBuilder withVolume(long volume) {
        this.volume = volume;
        return this;
    }
    public SecurityHistory build(){
        return new SecurityHistory(date, averagePerDay,
                ticker, openPrice, closePrice, volume);
    }

    public SecurityHistory empty(String date ,String ticker){
        return new SecurityHistory(date, 0, ticker, 0, 0, 0);
    }

    public SecurityHistory empty(String ticker){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        return new SecurityHistory(date, 0, ticker, 0, 0, 0);
    }
}
