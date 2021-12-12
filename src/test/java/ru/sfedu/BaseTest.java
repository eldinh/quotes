package ru.sfedu;

import junit.framework.TestCase;
import ru.sfedu.api.MongoHistory;
import ru.sfedu.model.MarketType;
import ru.sfedu.model.Stock;
import ru.sfedu.model.User;
import ru.sfedu.model.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseTest extends TestCase {
    protected List<User> users = new ArrayList<>(Arrays.asList(
            new User(0, "Andrew", 29)
            , new User(1, "NorAdeww", 10)
            , new User(2, "Dinh", 19)
            , new User(3, "Rodion", 19)
            , new User(4, "Ev", 20)
            , new User(5, "Danil", 19)
            , new User(6, "Artem", 19)
            , new User(7, "Sanya", 19)));

    protected List<User> userWithoutID = new ArrayList<>(Arrays.asList(
            new User("Andrew", 29)
            , new User( "NorAdeww", 10)
            , new User( "Dinh", 19)
            , new User( "Rodion", 19)
            , new User( "Ev", 20)
            , new User( "Danil", 19)
            , new User( "Artem", 19)
            , new User( "Sanya", 19)
    ));

    protected List<Stock> stocks = new ArrayList<>(Arrays.asList(
            new Stock("SBER", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.SHARES, Stock.StockType.COMMON, 0, 0),
            new Stock("QWER", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.SHARES, Stock.StockType.COMMON, 0, 0),
            new Stock("QQQ", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.SHARES, Stock.StockType.COMMON, 0, 0),
            new Stock("IOUO", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.SHARES, Stock.StockType.PREFERRED, 0, 0),
            new Stock("IOUO2", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.SHARES, Stock.StockType.PREFERRED, 0, 0),
            new Stock("IOUO1", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.SHARES, Stock.StockType.PREFERRED, 0, 0)
    ));

    protected List<Stock> bonds = new ArrayList<>(Arrays.asList(
            new Stock("SBER", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.BONDS, Stock.StockType.COMMON, 0, 0),
            new Stock("QWER", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.BONDS, Stock.StockType.COMMON, 0, 0),
            new Stock("QQQ", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.BONDS, Stock.StockType.COMMON, 0, 0),
            new Stock("IOUO", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.BONDS, Stock.StockType.PREFERRED, 0, 0),
            new Stock("IOUO1", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.BONDS, Stock.StockType.PREFERRED, 0, 0),
            new Stock("IOUO2", "", "" , "",
                    100, "RUB", "1", "qwe",
                    1000, MarketType.BONDS, Stock.StockType.PREFERRED, 0, 0)
    ));


    protected Result<User> result;
    protected Result<Stock> stockResult;


}
