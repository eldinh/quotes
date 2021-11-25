package ru.sfedu.api;

import ru.sfedu.model.Result;
import ru.sfedu.model.entity.Stock;
import ru.sfedu.model.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface IDateProvider {
    public Result<Stock> getStocks() throws Exception;
    public Result<Stock> appendStocks(List<Stock> list) throws Exception;
    public Result<Stock> updateStocks(List<Stock> ob) throws Exception;
    public Result<Stock> deleteStockByTicker(String ticker) throws Exception;
    public Result<Stock> deleteAllStocks() throws Exception;
    public Optional<Stock> getStockByTicker(String ticker) throws Exception;


}
