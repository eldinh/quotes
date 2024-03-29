package ru.sfedu.api;

import ru.sfedu.BaseTest;
import ru.sfedu.model.*;
import ru.sfedu.utils.IdGenerator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;


import static org.junit.Assert.assertNotEquals;
import static ru.sfedu.Constants.*;

public class DataProviderCsvTest extends BaseTest {
    DataProviderCsv data = new DataProviderCsv();

    private void deleteFile(String fileName){
        File file = new File(String.format("./src/main/resources/data/%s.csv", fileName));
        if (file.exists()){
            file.delete();
        }
    }
    @Override
    protected void setUp() {
        deleteFile(USER_TABLE_NAME);
        deleteFile(STOCK_TABLE_NAME);
        deleteFile(BOND_TABLE_NAME);
        deleteFile(ACTON_TABLE_NAME);
        deleteFile(MARKET_TABLE_NAME);
        data.deleteAllSecurityHistories("SBER");
        data.deleteAllSecurityHistories("SBERBOND");
    }


    // users
    public void testAppendUsers(){
        // appending users
        result = data.appendUsers(users);
        assertEquals(result.getStatus(), SUCCESS);

        // getting users
        result = data.getUsers();
        assertEquals(result.getBody().size(), users.size());
    }

    public void testFailAppendUsers(){
        // appending empty list
        result = data.appendUsers(new ArrayList<>());
        assertEquals(result.getStatus(), FAIL);

        // null
        result = data.appendUsers(null);
        assertEquals(result.getStatus(), FAIL);

    }

    public void testWarnAppendUsers(){
        // appending same users twice

        result = data.appendUsers(users);

        result = data.appendUsers(users);
        assertEquals(result.getStatus(), WARN);
        assertEquals(result.getBody().size(), users.size());
    }

    public void testGetUsers(){
        // append bonds and stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        // user to append
        User user = new UserBuilder().withName("Dinh").withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>(List.of(stocks.get(0), bonds.get(0)))).build();
        result = data.appendUsers(new ArrayList<>(List.of(user)));
        System.out.println(result);
        assertEquals(result.getStatus(), SUCCESS);

        // getting users
        result = data.getUsers();
        System.out.println(result);
        assertEquals(result.getStatus(), SUCCESS);

        // checking user's security list
        User userFromDB = result.getBody().get(0);
        assertEquals(user.getSecurityList().size(), 2);
    }

    public void testFailGetUsers(){
        // getting users from non-exist file
        result = data.getUsers();
        assertEquals(result.getStatus(), FAIL);
    }


    public void testUpdateUsers(){
        // appending stocks and bonds
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        User userToUpdate1 = new UserBuilder(users.get(0).getId()).withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();

        User userToUpdate2 = new UserBuilder(users.get(1).getId()).withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();
        // appending users
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);

        // updating users
        result = data.updateUsers(new ArrayList<>(List.of(userToUpdate1, userToUpdate2)));
        System.out.println(result);
        assertEquals(result.getStatus(), SUCCESS);

        // comparing 2 users
        Optional<User> user = data.getUserById(users.get(0).getId());
        assert user.isPresent();
        assert compareTwoUsers(user.get(), userToUpdate1);

        // comparing 2 users
        user = data.getUserById(users.get(1).getId());
        assert user.isPresent();
        assert compareTwoUsers(user.get(), userToUpdate2);
    }

    public void testFailUpdateUsers(){
        User userToUpdate1 = new UserBuilder("").withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();

        User userToUpdate2 = new UserBuilder("31412").withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();

        // updating users in file that doesn't exist
        result = data.updateUsers(new ArrayList<>(List.of(userToUpdate1, userToUpdate2)));
        assertEquals(result.getStatus(), FAIL);

        // null and empty
        assertEquals(data.updateUsers(new ArrayList<>()).getStatus(), FAIL);
        assertEquals(data.updateUsers(null).getStatus(), FAIL);

    }

    public void testWarnUpdateUsers(){
        User userToUpdate1 = new UserBuilder("").withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();

        User userToUpdate2 = new UserBuilder("31412").withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();

        // updating users that doesn't exist
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);
        result = data.updateUsers(new ArrayList<>(List.of(userToUpdate1, userToUpdate2)));

        assertEquals(result.getStatus(), WARN);
        assertEquals(result.getBody().size(), 2);
    }

    public void testUpdateUser(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // appending user
        User userToUpdate1 = new UserBuilder(users.get(0).getId()).withName("EEEE")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>()).build();
        assertEquals(data.appendUsers(new ArrayList<>(List.of(users.get(0))) ).getStatus(), SUCCESS);
        assertEquals(data.getUsers().getBody().size(), 1);
        // updating user
        result = data.updateUser(userToUpdate1);
        System.out.println(result);
        assertEquals(result.getStatus(), SUCCESS);

        // comparing 2 users
        Optional<User> user = data.getUserById(users.get(0).getId());
        assert user.isPresent();
//        assert compareTwoUsers(user.get(), userToUpdate1);
    }

    public void testFailUpdateUser(){
        // updating user in file that doesn't exist
        result = data.updateUser(users.get(0));
        assertEquals(result.getStatus(), FAIL);
        // null
        result = data.updateUser(null);
        assertEquals(result.getStatus(), FAIL);
    }


    public void testGetUserById(){
        // appending bonds and stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // appending users
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);

        // getting user by id
        Optional<User> user = data.getUserById(users.get(0).getId());
        System.out.println(user);
        assert user.isPresent();
        // comparing 2 users
        assertEquals(user.get().getActionHistory(), users.get(0).getActionHistory());
        assert compareTwoUsers(user.get(), users.get(0));
    }

    public void testFailGetUserById(){
        // getting user that doesn't exist in file
        Optional<User> user = data.getUserById(users.get(0).getId());
        assert user.isEmpty();
        // null
        user = data.getUserById(null);
        assert user.isEmpty();

        // getting user that doesn't exist
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);
        user = data.getUserById(IdGenerator.generate());
        assert user.isEmpty();
    }

    public void testDeleteUserById(){
        // appending bonds and stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // appending users
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);

        // deleting by id
        Optional<User> user = data.deleteUserById(users.get(0).getId());
        System.out.println(user);
        assert user.isPresent();
        assert compareTwoUsers(user.get(), users.get(0));
    }

    public void testFailDeleteUserById(){
        // deleting user in file that doesn't exist
        Optional<User> user = data.deleteUserById("DAWFADWAFAGS");
        assert user.isEmpty();
        // null
        user = data.deleteUserById(null);
        assert user.isEmpty();
        // deleting user that doesn't exist
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);
        user = data.deleteUserById(IdGenerator.generate());
        assert user.isEmpty();

    }



    public void testDeleteAllUsers(){
        // appending users to delete
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);
        result = data.getUsers();
        assertEquals(result.getStatus(), SUCCESS);
        assertEquals(result.getBody().size(), users.size());
        // deleting all users
        result = data.deleteAllUsers();
        System.out.println(result);
        assertEquals(result.getStatus(), SUCCESS);
        assertEquals(result.getBody().size(), users.size());
    }

    public void testFailDeleteAllUsers(){
        // deleting file that doesn't exist
        result = data.deleteAllUsers();
        System.out.println(result);
        assertEquals(result.getStatus(), FAIL);
    }

    public void testAppendAction(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        // appending a user
        Optional<String> userId = data.appendUser("Anton");
        assert userId.isPresent();

        // appending second user
        Optional<String> user2Id = data.appendUser("Eldinh");
        assert user2Id.isPresent();

        Optional<String> actionId = data.appendAction(ActionType.ADD, user2Id.get(), "SBER");
        assert actionId.isPresent();
        actionId = data.appendAction(ActionType.ADD, user2Id.get(), "QWER");
        assert actionId.isPresent();
        actionId = data.appendAction(ActionType.ADD, user2Id.get(), "QQQ");
        assert actionId.isPresent();

        // appending an action
        actionId = data.appendAction(ActionType.ADD, userId.get(), "SBER");
        assert actionId.isPresent();

        // getting user's actions
        actionResult = data.getActionHistory(userId.get());
        System.out.println(actionResult);
        assertEquals(actionResult.getStatus(), SUCCESS);
        assertEquals(actionResult.getBody().size(), 1);
        // checking result
        Optional<User> user = data.getUserById(userId.get());
        assert user.isPresent();
        assertEquals(user.get().getSecurityList().size(), 1);

        // deleting security in case
        actionId = data.appendAction(ActionType.DELETE, userId.get(), "SBER");
        assert actionId.isPresent();

        // checking result
        user = data.getUserById(userId.get());
        assert user.isPresent();
        assertEquals(user.get().getSecurityList().size(), 0);
        System.out.println(user.get().getActionHistory());
        assertEquals(user.get().getActionHistory().size(), 2);

        // checking second user
        user = data.getUserById(user2Id.get());
        assert user.isPresent();
        assertEquals(user.get().getActionHistory().size(), 3);
        assertEquals(user.get().getSecurityList().size(), 3);
    }

    public void testFailAppendAction(){
        // appending a user
        Optional<String> userId = data.appendUser("Anton");
        assert userId.isPresent();
        // appending to case security that doesn't exist
        Optional<String> actionId = data.appendAction(ActionType.ADD, userId.get(), "SBER");
        assert actionId.isEmpty();
        // deleting security from case that not in it
        actionId = data.appendAction(ActionType.DELETE, userId.get(), "SBER");
        assert actionId.isEmpty();
        // incorrect user id
        actionId = data.appendAction(ActionType.ADD, "ds", "SBER");
        assert actionId.isEmpty();

        // appending existing security to case twice
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        actionId = data.appendAction(ActionType.ADD, userId.get(), "SBER");
        assert actionId.isPresent();

        actionId = data.appendAction(ActionType.ADD, userId.get(), "SBER");
        assert actionId.isEmpty();

        // deleting security twice
        actionId = data.appendAction(ActionType.DELETE, userId.get(), "SBER");
        assert actionId.isPresent();

        actionId = data.appendAction(ActionType.DELETE, userId.get(), "SBER");
        assert actionId.isEmpty();
        // null
        actionId = data.appendAction(null, userId.get(), "SBER");
        assert actionId.isEmpty();

        // null
        actionId = data.appendAction(ActionType.ADD, userId.get(), null);
        assert actionId.isEmpty();

    }

    public void testDeleteActionHistory(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        // appending a user
        Optional<String> userId = data.appendUser("Anton");
        assert userId.isPresent();
        // appending action to second user
        Optional<String> user2Id = data.appendUser("Eldinh");
        assert user2Id.isPresent();

        Optional<String> actionId = data.appendAction(ActionType.ADD, user2Id.get(), "SBER");
        assert actionId.isPresent();
        actionId = data.appendAction(ActionType.ADD, user2Id.get(), "QWER");
        assert actionId.isPresent();

        // appending an action
        assert data.appendAction(ActionType.ADD, userId.get(), "SBER").isPresent();
        assert data.appendAction(ActionType.DELETE, userId.get(), "SBER").isPresent();

        // getting user's actions
        actionResult = data.getActionHistory(userId.get());
        System.out.println(actionResult);
        assertEquals(actionResult.getBody().size(), 2);

        // deleting user's actions
        actionResult = data.deleteActionHistory(userId.get());
        System.out.println(actionResult);
        assertEquals(actionResult.getBody().size(), 2);

        // checking action list
        Optional<User> user = data.getUserById(userId.get());
        assertEquals(data.getActionHistory(userId.get()).getBody().size(), 0);


        // checking for second user
        user = data.getUserById(user2Id.get());
        assert user.isPresent();
        assertEquals(user.get().getActionHistory().size(), 2);
        assertEquals(user.get().getSecurityList().size(), 2);

        // deleting action history
        assertEquals(data.deleteActionHistory(user2Id.get()).getStatus(), SUCCESS);
        user = data.getUserById(user2Id.get());
        assert user.isPresent();
        assertEquals(user.get().getActionHistory().size(), 0);
        assertEquals(user.get().getSecurityList().size(), 2);

    }

    public void testFailDeleteActionHistory(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        // appending a user
        Optional<String> userId = data.appendUser("Anton");
        assert userId.isPresent();

        // deleting from non-exist table
        actionResult = data.deleteActionHistory("dwads");
        assertEquals(actionResult.getStatus(), FAIL);

        // null
        assert data.appendAction(ActionType.ADD, userId.get(), "SBER").isPresent();
        actionResult = data.deleteActionHistory(null);
        assertEquals(actionResult.getStatus(), FAIL);

    }

    public void testGetActionHistory(){
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        // appending a user
        Optional<String> userId = data.appendUser("Anton");
        assert userId.isPresent();
        assert data.appendAction(ActionType.ADD, userId.get(), "SBER").isPresent();
        assert data.appendAction(ActionType.DELETE, userId.get(), "SBER").isPresent();
        assert data.appendAction(ActionType.ADD, userId.get(), "SBER").isPresent();
        assert data.appendAction(ActionType.DELETE, userId.get(), "SBER").isPresent();
        assert data.appendAction(ActionType.ADD, userId.get(), "SBER").isPresent();

        // checking actions
        actionResult = data.getActionHistory(userId.get());
        assertEquals(actionResult.getStatus(), SUCCESS);
        assertEquals(actionResult.getBody().size(), 5);
        // checking user's briefcase
        Optional<User> user = data.getUserById(userId.get());
        assert user.isPresent();
        assertEquals(user.get().getSecurityList().size(), 1);
    }

    public void testFailGetActionHistory(){

        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        // appending a user
        Optional<String> userId = data.appendUser("Anton");
        assert userId.isPresent();
        // getting history from non-exist file
        assertEquals(data.getActionHistory(userId.get()).getStatus(), FAIL);
        // null
        assertEquals(data.getActionHistory(null).getStatus(), FAIL);


    }


// market


    public void testAppendOrUpdateMarket(){
        //  append stock market
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assert data.appendOrUpdateMarket(MarketType.SHARES);

        //  checking that market exist in files
        Optional<Market> market = data.getMarket(MarketType.SHARES);
        assert market.isPresent();
        assertEquals(market.get().getSecurityList().size(), stocks.size());

        // new stock to append
        Stock stock = new StockBuilder().withTicker("SHUSHU").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SHUSHU"))
                .withType(StockType.COMMON).withDividendSum(0).withCapitalization(0)
                .build();

        assertEquals(data.appendStocks(new ArrayList<>(List.of(stock))).getStatus(), SUCCESS);

        //  checking that market was updated
        market = data.getMarket(MarketType.SHARES);
        assert market.isPresent();
        assertEquals(market.get().getSecurityList().size(), stocks.size() + 1);
    }

    public void testFailAppendOrUpdateMarket(){
        //  null
        assertFalse(data.appendOrUpdateMarket(null));
    }

    public void testGetMarkets(){
        // append bonds and stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        // check that they exist in file
        marketResult = data.getMarkets();
        assertEquals(marketResult.getStatus(), SUCCESS);
        assertEquals(marketResult.getBody().size(), 2);
    }
    public void testFailGetMarkets(){

        // get market from non-exist file
        marketResult = data.getMarkets();
        assertEquals(marketResult.getStatus(), FAIL);
    }

    public void testGetMarket(){
        // append bonds and stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        // check that stock market exists
        Optional<Market> market = data.getMarket(MarketType.SHARES);
        assert market.isPresent();
        // check that num of stocks correct
        assertEquals(market.get().getSecurityList().size(), stocks.size());

        // check that bond market exists
        market = data.getMarket(MarketType.BONDS);
        assert market.isPresent();
        // check that num of bonds correct
        assertEquals(market.get().getSecurityList().size(), bonds.size());
    }

    public void testFailGetMarket(){
        // get market from non-exist file
        Optional<Market> market = data.getMarket(MarketType.SHARES);
        assert market.isEmpty();

        // append bonds
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        // get market that doesn't exist
        market = data.getMarket(MarketType.SHARES);
        assert market.isEmpty();
    }

    public void testAppendStocks()  {
        // append stocks
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        System.out.println(data.getStocks());
        assertEquals(stockResult.getStatus(), SUCCESS);
    }

    public void testFailAppendStocks()  {
        // copy stocks
        List<Stock> otherStocks = new ArrayList<>(stocks);
        otherStocks.add(stocks.get(0));
        // append stocks that have same tickers
        stockResult = data.appendStocks(otherStocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);

        // append null
        stockResult = data.appendStocks(null);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);

        stockResult = data.appendStocks(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);
        // append stocks twice
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), WARN);
        assertEquals(stockResult.getBody().size(), stocks.size());


    }

    public void testGetStocks()  {
        // append and get stocks
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);

        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.getStocks();
        System.out.println(stockResult);
        // compare that num of stocks correct
        assertEquals(stockResult.getBody().size(), stocks.size());
    }

    public void testFailGetStocks()  {
        // get stocks from file that doesn't exist
        stockResult = data.getStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
    }

    public void testUpdateStocks(){
        // append stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        System.out.println(data.getStocks());

        // create stock to update
        Stock stock = new StockBuilder().withTicker("SBER").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBER"))
                .withType(StockType.COMMON).withDividendSum(0).withCapitalization(0)
                .build();
        // update SBER stock
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);

        // get stocks to check that update was successful
        stockResult = data.getStocks();
        System.out.println(stockResult);
        Optional<Stock> stockFromDB = stockResult.getBody().stream().filter(x -> x.getTicker().equals("SBER")).findFirst();
        if (stockFromDB.isPresent())
            assertEquals(stockFromDB.get(), stock);
        else
            assert(false);
    }

    public void testFailUpdateStocks(){
        // create stock to update
        Stock stock = new StockBuilder().withTicker("SHUSHU").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SHUSHU"))
                .withType(StockType.COMMON).withDividendSum(0).withCapitalization(0)
                .build();
        // update stock in file that doesn't exist
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        assertEquals(stockResult.getStatus(), FAIL);

        // append stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        System.out.println(data.getStocks());

        // try to update stock that doesn't exist
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), WARN);
        // empty list
        stockResult = data.updateStocks(new ArrayList<>());
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);

        // null
        stockResult = data.updateStocks(null);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
    }



    public void testDeleteStockByTicker()  {
        // append SBER's histories
        data.appendSecurityHistory(histories, "SBER");
        securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);

        // appending stocks
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);

        // deleting stock - SBER
        Optional<Stock> deletedStock = data.deleteStockByTicker("SBER");

        // checking that history was deleted too
        securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

        // checking that deleted stock and stock from example are the same
        Optional<Stock> stock= stocks.stream().filter(x -> x.getTicker().equals("SBER")).findFirst();
        if (deletedStock.isPresent())
            assertEquals(deletedStock, stock);
        else
            assert(false);

        // get all stocks to confirm that stock SBER was deleted
        stockResult = data.getStocks();
        assertEquals(stockResult.getStatus(), SUCCESS);

        List<Stock> updatedStocks = new ArrayList<>(stocks);
        assertEquals( stockResult.getBody().size(), stocks.size() -1);

        // comparing stocks again to confirm
        updatedStocks.removeAll(stockResult.getBody());
        assertEquals(updatedStocks.size(), 1);
        assertEquals(updatedStocks.get(0), deletedStock.get());


    }

    public void testFailDeleteStockByTicker(){
        // deleting stock from non-exist file
        Optional<Stock> stock = data.deleteStockByTicker("SBABABABQQWEQR");
        assert stock.isEmpty();
        // appending stocks to create file

        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        // deleting stock by ticker that doesn't exist in db
        Optional<Stock> deletedStock = data.deleteStockByTicker("SBABABABQQWEQR");
        assertEquals(deletedStock, Optional.empty());

        // null
        stock = data.deleteStockByTicker(null);
        assert stock.isEmpty();

    }
    public void testGetStockByTicker()  {
        // appending SBER's histories
        data.appendSecurityHistory(histories, "SBER");
        // appending stocks
        data.appendStocks(stocks);

        // get stock by id
        Optional<Stock> stock = data.getStockByTicker("SBER");
        System.out.println(stock);
        assert stock.isPresent();
        // checking his history
        assertEquals(stock.get().getHistory(), data.getSecurityHistoryByDate("SBER"));
    }

    public void testFailGetStockByTicker(){
        // get stock from db that doesn't exist
        Optional<Stock> stock = data.getStockByTicker("SBER");
        System.out.println(stock);
        assert stock.isEmpty();

        // appending stocks
        data.appendStocks(stocks);

        // getting stock by wrong ticker
        stock = data.getStockByTicker("SBER1231421");
        System.out.println(stock);
        assert stock.isEmpty();
    }


    public void testDeleteAllStocks(){
        // appending stocks
        stockResult = data.appendStocks(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);

        // deleting stocks
        stockResult = data.deleteAllStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);
        // check that all stock was deleted
        assertEquals(stockResult.getBody().size(), stocks.size());

        // checking that stock's histories was deleted too
        for (String ticker : stocks.stream().map(Security::getTicker).toList()){
            securityHistoryResult = data.getSecurityHistories(ticker);
            assertEquals(securityHistoryResult.getStatus(), FAIL);
        }
    }

    public void testFailDeleteAllStocks(){
        // deleting stocks from non-exist file
        stockResult = data.deleteAllStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        assertEquals(stockResult.getBody().size(), 0);

    }

    public void testAppendSecurityHistory(){
        // appending
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        // checking that histories were appended
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), histories.size());
    }

    public void testFailAppendSecurityHistory(){
        // appending histories with a wrong ticker
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER1");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        // null ticker
        securityHistoryResult = data.appendSecurityHistory(histories, null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        // null
        securityHistoryResult = data.appendSecurityHistory(null, "");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

    }

    public void testWarnAppendSecurityHistory(){
        // appending histories with the same date
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getStatus(), WARN);
        assertEquals(securityHistoryResult.getBody() ,histories);
    }


    public void testGetSecurityHistories(){
        // appending security's histories
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);
        // getting history
        securityHistoryResult = data.getSecurityHistories("SBER");
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getBody(), histories);
    }

    public void testFailGetSecurityHistories(){
        // Getting history from non-exist bd
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);

        // Wrong ticker
        securityHistoryResult = data.getSecurityHistories("SBER1");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

        // null
        securityHistoryResult = data.getSecurityHistories(null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
    }

    public void testGetSecurityHistoryByDate(){
        // checking that SecurityHistory is never null
        for (String date : dateList){
            SecurityHistory securityHistory1 = data.getSecurityHistoryByDate("SBER", date);
            assertEquals(securityHistory1, new SecurityHistoryBuilder().empty(date, "SBER"));
        }
        // appending SBER's history
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);
        for (String date : dateList){
            // getting history by date and comparing with an example
            SecurityHistory securityHistory = data.getSecurityHistoryByDate("SBER", date);
            Optional<SecurityHistory> securityHistory1 = histories.stream().filter(x -> x.getDate().equals(date)).findFirst();
            assert securityHistory1.isPresent();
            assertEquals(securityHistory1.get(), securityHistory);
        }
    }

    public void testFailGetSecurityHistoryByDate(){
        SecurityHistory securityHistory = new SecurityHistoryBuilder().empty("SBER1");
        for (String date : dateList){
            // different tickers
            SecurityHistory securityHistory1 = data.getSecurityHistoryByDate("SBER",date);
            assertNotEquals(securityHistory1, securityHistory);
        }
    }

    public void testAppendOrUpdate(){
        // appending empty history
        assert(data.appendOrUpdateSecurityHistory(new SecurityHistoryBuilder().empty("SBER"), "SBER"));
        securityHistoryResult = data.getSecurityHistories("SBER");

        // Getting SBER history
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), 1);

        // updating SBER history
        SecurityHistory securityHistory = new SecurityHistoryBuilder().withTicker("SBER").withAveragePerDay(12314)
                .withClosePrice(10).withOpenPrice(9).withVolume(1000)
                .withDate(new SimpleDateFormat("yyyy-MM-dd")
                .format(Calendar.getInstance().getTime()))
                .build();

        assert(data.appendOrUpdateSecurityHistory(securityHistory, "SBER"));
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        // checking that history was updated
        assertEquals(securityHistoryResult.getBody().size(), 1);
        //
        assertEquals(securityHistoryResult.getBody().get(0), securityHistory);
    }

    public void testFailAppendOrUpdate(){
        // null
        assertFalse(data.appendOrUpdateSecurityHistory(null, "SBER"));
        assertFalse(data.appendOrUpdateSecurityHistory(new SecurityHistoryBuilder().empty("SBER"), null));
        // tickers not the same
        assertFalse(data.appendOrUpdateSecurityHistory(new SecurityHistoryBuilder().empty("SBER"), "SBERE!"));
        assertFalse(data.appendOrUpdateSecurityHistory(new SecurityHistoryBuilder().empty("SBE1R"), "SBERE!"));
    }

    public void testDeleteAllSecurityHistories(){
        // appending history
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);

        // deleting history
        Result<SecurityHistory> securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), histories.size());
    }


    public void testFailDeleteAllSecurityHistories(){
        // deleting from non-exist file
        data.deleteAllSecurityHistories("SBER");
        assertEquals(data.deleteAllSecurityHistories("SBER").getStatus(), FAIL);
    }

    public void testAppendBonds(){

        // appending bonds
        bondResult = data.appendBonds(bonds);
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), SUCCESS);
        // checking that bonds have been appended
        bondResult = data.getBonds();
        assertEquals(bondResult.getStatus(), SUCCESS);
        assertEquals(bondResult.getBody().size(), bonds.size());
    }

    public void testFailAppendBonds(){
        // appending bonds
        bondResult = data.appendBonds(bonds);
        Bond bond = new BondBuilder().withTicker("SBERBOND").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        // appending list that consist 2 same bonds
        bondResult = data.appendBonds(new ArrayList<>(List.of(bond, bond)));
        assertEquals(bondResult.getStatus(), FAIL);
        // null
        assertEquals(data.appendBonds(null).getStatus(), FAIL);
        // empty list
        assertEquals(data.appendBonds(new ArrayList<>()).getStatus(), FAIL);
    }

    public void testWarnAppendBonds(){
        // appending bonds twice
        bondResult = data.appendBonds(bonds);
        bondResult = data.appendBonds(bonds);
        assertEquals(bondResult.getStatus(), WARN);
        assertEquals(bondResult.getBody().size(), bonds.size());
    }

    public void testGetBonds(){
        // Appending and getting bonds
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), bonds.size());
    }

    public void testFailGetBonds(){
        // getting bonds from non-exist file
        bondResult = data.getBonds();
        assertEquals(bondResult.getStatus(), FAIL);
    }

    public void testUpdateBonds(){
        // appending bonds
        bondResult = data.appendBonds(bonds);
        assertEquals(bondResult.getStatus(), SUCCESS);

        // bond to update
        Bond bond = new BondBuilder().withTicker("SBERBOND").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond)));
        assertEquals(bondResult.getStatus(), SUCCESS);

        // checking that history was updated
        securityHistoryResult = data.getSecurityHistories(SBERBOND);
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        System.out.println();

        assert(securityHistoryResult.getBody().contains(getHistories(SBERBOND)));

        // checking that bond was updated
        Optional<Bond> bond1 = data.getBondByTicker(SBERBOND);
        assert(bond1.isPresent());
        assertEquals(bond1.get(), bond);
    }

    public void testFailUpdateBonds(){
        Bond bond = new BondBuilder().withTicker("SQFWQDQ").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        // non-exist db
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond)));
        assertEquals(bondResult.getStatus(), FAIL);

        // null
        bondResult = data.updateBonds(null);
        assertEquals(bondResult.getStatus(), FAIL);

        // empty list
        bondResult = data.updateBonds(new ArrayList<>());
        assertEquals(bondResult.getStatus(), FAIL);

        // ticker contains null
        Bond bond1 = new BondBuilder().withTicker(null).withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond1)));
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), FAIL);
    }

    public void testWarnUpdateBonds(){
        // updating bond with ticker that doesn't exist in db(incorrect ticker)
        Bond bond = new BondBuilder().withTicker("SQFWQDQ").withName("")
                .withShortName("").withLatName("").withNominal(90)
                .withNominalValue("RUB").withIssueDate(DATE).withIsin("qwer")
                .withIssueSize(90).withSecurityHistory(getHistories("SBERBOND"))
                .withType(BondType.CB).withMatDate("").withCoupon(0D).withDayToRedemption(0)
                .build();
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        bondResult = data.updateBonds(new ArrayList<>(List.of(bond)));
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), WARN);
    }

    public void testDeleteBondByTicker(){
        // appending bonds
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // deleting bonds by ticker
        Optional<Bond> bond = data.deleteBondByTicker(SBERBOND);
        assert(bond.isPresent());

        // checking that bonds are the same
        assertEquals(bond, bonds.stream().filter(x -> x.getTicker().equals(SBERBOND)).findFirst());
        bondResult = data.getBonds();

        // checking that bond was deleted in db
        assertEquals(bondResult.getBody().size(), bonds.size() - 1);
    }

    public void testFailDeleteBondByTicker(){
        // appending bonds
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        // deleting bond with incorrect ticker
        Optional<Bond> bond = data.deleteBondByTicker("FFFFFFFFFFFFFFFFFFF");
        assert(bond.isEmpty());
        assertEquals(data.getBonds().getBody().size(), bonds.size());
    }

    public void testDeleteAllBonds(){
        // appending bonds
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // deleting all bonds
        bondResult = data.deleteAllBonds();
        System.out.println(bondResult);
        assertEquals(bondResult.getStatus(), SUCCESS);
        // comparing deleted and appended bonds
        assertEquals(bondResult.getBody().size(), bonds.size());
        // checking that bond's histories have been deleted
        for (String ticker : bonds.stream().map(Security::getTicker).toList()){
            securityHistoryResult = data.getSecurityHistories(ticker);
            assertEquals(securityHistoryResult.getStatus(), FAIL);
        }

        // checking that bonds have been deleted from db
        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), 0);
    }

    public void testFailDeleteAllBonds(){
        // deleting bonds from non-exist db
        assertEquals(data.deleteAllBonds().getStatus(), FAIL);
    }

    public void testGetBondByTicker(){
        // appending bonds
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // get bond by ticker
        Optional<Bond> bond = data.getBondByTicker(SBERBOND);
        assert(bond.isPresent());

        // comparing 2 bond
        assertEquals(bond, bonds.stream().filter(x -> x.getTicker().equals(SBERBOND)).findFirst());
        bondResult = data.getBonds();
        assertEquals(bondResult.getBody().size(), bonds.size());
    }

    public void testFailGetBondByTicker(){
        // getting bond from non-exist db
        Optional<Bond> bond = data.getBondByTicker(SBERBOND);
        assert bond.isEmpty();

        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // getting bond with incorrect ticker
        bond = data.getBondByTicker("QWEFASWFASF");
        assert bond.isEmpty();
    }

    public void testGetSecuritiesByTickerList(){
        // appending stocks and bonds
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        // getting securities by ticker list
        List<String> tickerList = new ArrayList<>(List.of("SBER", "SBERBOND", "QWER", "QWERBOND"));
        securityResult = data.getSecuritiesByTickerList(tickerList);
        System.out.println(securityResult);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), tickerList.size());
    }

    public void testFailGetSecuritiesByTickerList(){
        // getting securities from non-exist db
        List<String> tickerList = new ArrayList<>(List.of("SBER", "SBERBOND", "QWER", "QWERBOND"));
        securityResult = data.getSecuritiesByTickerList(tickerList);
        System.out.println(securityResult);
        assertEquals(securityResult.getBody().size(), 0);

        // empty list
        securityResult = data.getSecuritiesByTickerList(new ArrayList<>());
        assertEquals(securityResult.getStatus(), FAIL);

        // null
        securityResult = data.getSecuritiesByTickerList(null);
        assertEquals(securityResult.getStatus(), FAIL);
    }

    public void testGetSecurityByTicker(){
        // appending stocks and bonds
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);

        Optional<Security> security = data.getSecurityByTicker(SBER);
        System.out.println(security);
        assert security.isPresent();

        security = data.getSecurityByTicker(SBERBOND);
        System.out.println(security);
        assert security.isPresent();
    }

    public void testFailGetSecurityByTicker(){
        //getting from non-exist security
        Optional<Security> security = data.getSecurityByTicker(SBER);
        System.out.println(security);
        assert security.isEmpty();

        security = data.getSecurityByTicker(SBERBOND);
        System.out.println(security);
        assert security.isEmpty();

        //null
        security = data.getSecurityByTicker(null);
        assert security.isEmpty();
    }

    public void initData(){
        // appending stocks bonds
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        assertEquals(data.appendBonds(bonds).getStatus(), SUCCESS);
        // history
        assertEquals(data.appendSecurityHistory(histories, SBER).getStatus(), SUCCESS);
    }

    public String initUser(){
        Optional<String> userId = data.appendUser("dinh");
        assert userId.isPresent();
        return userId.get();
    }

    public void testGetActiveSecurities(){
        initData();

        // Correct output
        securityResult = data.getActiveSecurities(MarketType.BONDS);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), bonds.size());

        // Correct output
        securityResult = data.getActiveSecurities(MarketType.SHARES);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), stocks.size());


    }

    public void testFailGetActiveSecurities(){
        initData();

        // null
        securityResult = data.getActiveSecurities(null);
        assertEquals(securityResult.getStatus(), FAIL);
    }

    public void testShowDetailedInfo(){
        initData();

        securityHistoryResult = data.showDetailedInfo(SBER);
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), histories.size() + 1);
    }


    public void testFailShowDetailedInfo(){
        initData();

        // incorrect ticker
        securityHistoryResult = data.showDetailedInfo("WWWWWWWWWW");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

        // null
        securityHistoryResult = data.showDetailedInfo(null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
    }

    public void testShowInfo(){
        initData();

        // getting info
        String info = data.showInfo(SBER);
        assertFalse(info.isEmpty());
    }

    public void testFailShowInfo(){
        initData();

        // incorrect ticker
        String info = data.showInfo("WWWWWWWWWDAWFWAFS");
        assert info.isEmpty();

        // null
        info = data.showInfo(null);
        assert info.isEmpty();
    }

    public void testFindSecurity(){
        initData();


        securityHistoryResult = data.findSecurity(SBER);
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), histories.size() + 1);


        // Correct output
        securityResult = data.findSecurity(MarketType.BONDS);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), bonds.size());

        // Correct output
        securityResult = data.findSecurity(MarketType.SHARES);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), stocks.size());
    }

    public void testFailFindSecurity(){
        initData();

        // null
        securityResult = data.findSecurity((MarketType) null);
        assertEquals(securityResult.getStatus(), FAIL);

        // incorrect ticker
        securityHistoryResult = data.findSecurity("WWWWWWWWWW");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

        // null
        securityHistoryResult = data.findSecurity((String)null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
    }

    public void testPerformActon(){
        initData();
        String id = initUser();

        assert data.performActon(id, "add", SBER);
        assert data.performActon(id, "add", SBERBOND);
        assert data.performActon(id, "delete", SBERBOND);
        assert data.performActon(id, "delete", SBER);
    }

    public void testFailPerformActon(){
        initData();

        String id = initUser();

        // incorrect ticker
        assertFalse(data.performActon(id, "add", "SDWAFAWFKAWFOW"));

        // incorrect command
        assertFalse(data.performActon(id, "update", SBER));

        // incorrect id
        assertFalse(data.performActon("wqfq", "add", SBER));

        // deleting smth that not in case
        assertFalse(data.performActon(id, "delete", SBER));

        // adding same security twice
        assert(data.performActon(id, "add", SBER));
        assertFalse(data.performActon(id, "add", SBER));
        assert(data.performActon(id, "delete", SBER));

        // deleting twice
        assert(data.performActon(id, "add", SBER));
        assert(data.performActon(id, "delete", SBER));
        assertFalse(data.performActon(id, "delete", SBER));
        
        // null
        assertFalse(data.performActon(null, "add", SBER));
        assertFalse(data.performActon(id, null, SBER));
        assertFalse(data.performActon(id, "add", null));
    }

    public void testCheckVirtualBriefCase(){
        initData();

        String id = initUser();

        assert data.performActon(id, "add", SBER);
        assert data.performActon(id, "add", SBERBOND);

        // checking briefcase
        securityResult = data.checkVirtualBriefCase(id);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), 2);

        assert data.performActon(id, "delete", SBER);
        assert data.performActon(id, "delete", SBERBOND);

        // checking briefcase again after deleting
        securityResult = data.checkVirtualBriefCase(id);
        assertEquals(securityResult.getStatus(), SUCCESS);
        assertEquals(securityResult.getBody().size(), 0);
    }

    public void testFailCheckVirtualBriefCase(){
        // incorrect id
        assertEquals(data.checkVirtualBriefCase("dwadw").getStatus(), FAIL);
        // null
        assertEquals(data.checkVirtualBriefCase(null).getStatus(), FAIL);
    }

    public void testShowHistory(){
        initData();
        String id = initUser();

        assert data.performActon(id, "add", SBER);
        assert data.performActon(id, "add", SBERBOND);

        String info = data.showHistory(id);
        assertFalse(info.isEmpty());
    }

    public void testFailShowHistory(){
        // incorrect id
        String info = data.showHistory("id");
        assert(info.isEmpty());
        // null
        info = data.showHistory(null);
        assert info.isEmpty();
    }


}