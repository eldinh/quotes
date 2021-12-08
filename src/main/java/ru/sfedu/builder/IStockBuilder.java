package ru.sfedu.builder;

import ru.sfedu.entity.Stock;
import ru.sfedu.model.MarketName;

public interface IStockBuilder {
    public void setType(Stock.StockType type);
    public void setDividendSum(double dividendSum);
    public void setCapitalization(double capitalization);
}
