package ru.sfedu;

import junit.framework.TestCase;
import ru.sfedu.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.sfedu.Constants.DATE;

public class BaseTest extends TestCase {
    protected final String SBER = "SBER";
    protected final String SBERBOND = "SBERBOND";



    protected List<Stock> stocks = new ArrayList<>(Arrays.asList(
            new StockBuilder()
                    .withCapitalization(0).withDividendSum(0).withTicker("SBER")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("SBER"))
                    .withNominalValue("RUB").withIssueDate("").withType(StockType.COMMON)
                    .build(),
            new StockBuilder()
                    .withCapitalization(0).withDividendSum(0).withTicker("QWER")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("QWER"))
                    .withNominalValue("RUB").withIssueDate("").withType(StockType.COMMON)
                    .build(),
            new StockBuilder()
                    .withCapitalization(0).withDividendSum(0).withTicker("QQQ")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("QQQ"))
                    .withNominalValue("RUB").withIssueDate("").withType(StockType.COMMON)
                    .build(),
            new StockBuilder()
                    .withCapitalization(0).withDividendSum(0).withTicker("IOUO")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("IOUO"))
                    .withNominalValue("RUB").withIssueDate("").withType(StockType.COMMON)
                    .build(),
            new StockBuilder()
                    .withCapitalization(0).withDividendSum(0).withTicker("IOUO1")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("IOUO1"))
                    .withNominalValue("RUB").withIssueDate("").withType(StockType.COMMON)
                    .build()
    ));

    protected List<Bond> bonds = new ArrayList<>(Arrays.asList(
            new BondBuilder()
                    .withTicker("SBERBOND")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("SBERBOND"))
                    .withNominalValue("RUB").withIssueDate("").withType(BondType.CB).withCoupon(0.0).withDayToRedemption(0)
                    .build(),
            new BondBuilder()
                    .withTicker("QWERBOND")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("QWERBOND"))
                    .withNominalValue("RUB").withIssueDate("").withType(BondType.CB).withCoupon(0.0).withDayToRedemption(0)
                    .build(),
            new BondBuilder()
                    .withTicker("QQQBOND").withName("").withShortName("")
                    .withLatName("").withNominal(100).withSecurityHistory(getHistories("QQQBOND"))
                    .withNominalValue("RUB").withIssueDate("").withType(BondType.CB)
                    .withCoupon(0.0).withDayToRedemption(0)
                    .build(),
            new BondBuilder()
                    .withTicker("IOUOBOND")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("IOUOBOND"))
                    .withNominalValue("RUB").withIssueDate("").withType(BondType.CB).withCoupon(0.0).withDayToRedemption(0)
                    .build(),
            new BondBuilder()
                   .withTicker("IOUO1BOND")
                    .withName("").withShortName("").withLatName("")
                    .withNominal(100).withSecurityHistory(getHistories("IOUO1BOND"))
                    .withNominalValue("RUB").withIssueDate("").withType(BondType.CB).withCoupon(0.0).withDayToRedemption(0)
                    .build()
    ));
    protected ArrayList<SecurityHistory> histories = new ArrayList<>(List.of(
            new SecurityHistory("2021-12-15", 123, "SBER", 12, 13, 100),
            new SecurityHistory("2021-11-21", 123, "SBER", 12, 13, 100),
            new SecurityHistory("2021-01-12", 123, "SBER", 12, 13, 100),
            new SecurityHistory("2021-12-12", 123, "SBER", 12, 13, 100),
            new SecurityHistory("2021-01-01", 123, "SBER", 12, 13, 100),
            new SecurityHistory("2021-01-13", 123, "SBER", 12, 13, 100)
    ));
    protected ArrayList<SecurityHistory> historiesBond = new ArrayList<>(List.of(
            new SecurityHistory("2021-12-16", 123, "SBERBOND", 12, 13, 100),
            new SecurityHistory("2021-11-21", 123, "SBERBOND", 12, 13, 100),
            new SecurityHistory("2021-01-12", 123, "SBERBOND", 12, 13, 100),
            new SecurityHistory("2021-12-12", 123, "SBERBOND", 12, 13, 100),
            new SecurityHistory("2021-01-01", 123, "SBERBOND", 12, 13, 100),
            new SecurityHistory("2021-01-13", 123, "SBERBOND", 12, 13, 100)
    ));


    protected List<User> users = new ArrayList<>(Arrays.asList(
            new UserBuilder().withName("Andrew").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>(bonds)).build()
            , new UserBuilder().withName("newAndrew").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>(stocks)).build()
            , new UserBuilder().withName("Dinh").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>(List.of(bonds.get(0), stocks.get(0)) )).build()
            , new UserBuilder().withName("Rodion").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>()).build()
            , new UserBuilder().withName("Vlad").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>()).build()
            , new UserBuilder().withName("Danil").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>()).build()
            , new UserBuilder().withName("Artem").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>()).build()
            , new UserBuilder().withName("Sanya").withActionHistory(new ArrayList<>()).withTickerList(new ArrayList<>()).build()
    ));

    protected Action action = new ActionBuilder().withAction(ActionType.ADD).withSecurity(bonds.get(0))
            .withDate(DATE).withUserID("0").build();

    protected ArrayList<String> dateList = new ArrayList<>(histories.stream().map(SecurityHistory::getDate).toList());
    protected ArrayList<String> dateBondList = new ArrayList<>(historiesBond.stream().map(SecurityHistory::getDate).toList());


    protected Result<User> result;
    protected Result<Stock> stockResult;
    protected Result<SecurityHistory> securityHistoryResult;
    protected Result<Bond> bondResult;
    protected Result<Action> actionResult;
    protected Result<Market> marketResult;
    protected Result<Security> securityResult;


    protected SecurityHistory getHistories(String ticker){
        return new SecurityHistoryBuilder().empty(ticker);
    }

    protected boolean compareTwoUsers(User user1, User user2){
        return user1.getId().equals(user2.getId()) && user1.getSecurityList().equals(user2.getSecurityList())
                && user1.getName().equals(user2.getName());
    }

}