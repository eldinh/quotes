package ru.sfedu.model;

import com.opencsv.bean.CsvBindAndSplitByPosition;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import ru.sfedu.utils.SecurityListCsvConverter;

import java.util.List;

public class Market {
    @Attribute
    @CsvBindByPosition(position = 0)
    private MarketType marketType;
    @ElementList(inline = true)
    @CsvCustomBindByPosition(position = 1, converter = SecurityListCsvConverter.class)
    private List<Security> securityList;

    public Market(){}

    public Market(MarketType marketType, List<Security> securityList) {
        this.marketType = marketType;
        this.securityList = securityList;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public void setMarketType(MarketType marketType) {
        this.marketType = marketType;
    }

    public List<Security> getSecurityList() {
        return securityList;
    }

    public void setSecurityList(List<Security> securityList) {
        this.securityList = securityList;
    }

    @Override
    public String toString() {
        return "Market{" +
                "marketType=" + marketType +
                ", securityList=" + securityList +
                '}';
    }
}
