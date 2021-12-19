package ru.sfedu.model;

import com.opencsv.bean.CsvBindAndJoinByPosition;
import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import ru.sfedu.utils.StringListCsvConverter;

import java.util.List;

public class Market {
    @Attribute
    @CsvBindByPosition(position = 0)
    private MarketType marketType;
    @ElementList(inline = true)
    @CsvBindAndSplitByPosition(position = 1,required = true, elementType = String.class, splitOn = " ", collectionType = List.class)
    private List<String> tickerList;

    public Market(){}

    public Market(MarketType marketType, List<String> tickerList) {
        this.marketType = marketType;
        this.tickerList = tickerList;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }

    public List<String> getTickerList() {
        return tickerList;
    }

    public void setTickerList(List<String> tickerList) {
        this.tickerList = tickerList;
    }

    @Override
    public String toString() {
        return "Market{" +
                "marketType=" + marketType +
                ", tickerList=" + tickerList +
                '}';
    }
}
