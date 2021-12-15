package ru.sfedu.api;



import org.junit.jupiter.api.AfterEach;
import ru.sfedu.BaseTest;
import ru.sfedu.Constants;
import ru.sfedu.builder.SecurityHistoryBuilder;
import ru.sfedu.model.*;


import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertNotEquals;
import static ru.sfedu.Constants.*;


public class DataProviderJDBCTest extends BaseTest {
    DataProviderJDBC data = new DataProviderJDBC();
    private void deleteFile(String dbName) {
        data.dropTable(dbName);
    }



    public void testGetUsers()  {
        deleteFile(USER_TABLE_NAME);
        result = data.appendUsers(users);
        assertEquals(data.deleteAllUsers().getStatus(), Constants.SUCCESS);
        result = data.appendUsers(users);
        result = data.getUsers();
        assert(result.getStatus().equals(Constants.SUCCESS));
        assertEquals(result.getStatus(), SUCCESS);
        assertEquals(result.getBody(), users);
    }

    public void testFailGetUsers()  {
        deleteFile(USER_TABLE_NAME);
        assert(data.getUsers().getStatus().equals(Constants.FAIL));
    }

    public void testAppendUsers()  {
        deleteFile(USER_TABLE_NAME);
        result = data.appendUsers(users);
        assert(result.getStatus().equals(Constants.SUCCESS));
        assertEquals(data.getUsers().getBody(), users);
        result = data.appendUsers(userWithoutID);
        System.out.println(result.getMessage());
        assert(result.getStatus().equals(Constants.SUCCESS));

    }

    public void testFailAppendUsers()  {
        deleteFile(USER_TABLE_NAME);
        result = data.appendUsers(null);
        assertNotEquals(result.getStatus(), Constants.SUCCESS);

        ArrayList<User> list = new ArrayList<>(users.subList(0,5));
        data.appendUsers(users);
        result = data.appendUsers(list);
        System.out.println(result.getMessage());

        assert(result.getBody().equals(users.subList(0,5)));
    }


    public void testDeleteAllUsers()  {
        deleteFile(USER_TABLE_NAME);
        assertEquals(data.appendUsers(users).getStatus(), SUCCESS);
        result = data.deleteAllUsers();
        assert(result.getStatus().equals(Constants.SUCCESS));
        result = data.getUsers();
        assert(result.getBody().equals(new ArrayList<User>()));
    }

    public void testFailDeleteAllUsers()  {
        deleteFile(USER_TABLE_NAME);
        result = data.deleteAllUsers();
        assert(result.getStatus().equals(Constants.FAIL));
    }

    public void testGetUserById() throws Exception {
        deleteFile(USER_TABLE_NAME);
        result = data.appendUsers(users);
        assert(result.getStatus().equals(Constants.SUCCESS));
        Optional<User> user;
        for (int i = 0; i < users.size(); i ++){
            user = data.getUserById(i);
            if (user.isPresent()){
                assertEquals(user.get(), users.get(i));
            } else
                assert(false);
        }
    }

    public void testFailGetUserById()  {
        deleteFile(USER_TABLE_NAME);
        result = data.appendUsers(users);
        assert(result.getStatus().equals(Constants.SUCCESS));
        Optional<User> user;
        user = data.getUserById(100);
        if (user.isEmpty())
            assert(true);
        else
            assert(false);
        deleteFile(USER_TABLE_NAME);
        user = data.getUserById(0);
        if (user.isEmpty())
            assert(true);
        else
            assert(false);




    }

    public void testUpdateUsers() throws Exception {
        deleteFile(USER_TABLE_NAME);
        assertEquals(data.appendUsers(users).getStatus(), Constants.SUCCESS);
        result = data.updateUsers(users);
        System.out.println(result.getMessage());
        assertEquals(result.getStatus(), Constants.SUCCESS);
        User user = new User(1, " ", 10);
        System.out.println(users.get(1));
        result = data.updateUsers(new ArrayList<>(List.of(user)));
        assertEquals(result.getStatus(), Constants.SUCCESS);
        Optional<User> userOp = data.getUserById(1);
        if (userOp.isPresent())
            assertEquals(userOp.get(), user);
        else
            assert(false);
    }

    public void testFailUpdateUsers() {
        deleteFile(USER_TABLE_NAME);
        result = data.updateUsers(users);
        assertNotEquals(result.getStatus(), Constants.SUCCESS);
        assertEquals(data.appendUsers(users).getStatus(), Constants.SUCCESS);
        result = data.updateUsers(null);
        assertNotEquals(result.getStatus(), Constants.SUCCESS);


        result = data.updateUsers(new ArrayList<>(List.of(new User())));
        System.out.println(result.getMessage());
        System.out.println(Arrays.toString(result.getBody().toArray()));
        System.out.println(result.getStatus());
        System.out.println(new User());
        assertNotEquals(result.getStatus(), Constants.SUCCESS);


        result = data.updateUsers(new ArrayList<>(List.of(new User(7, " ", 10) ,new User(10,"", 10))));
        System.out.println(result.getBody());
        assertEquals(result.getBody().size(), 1);

    }

    public void testDeleteUserById() throws Exception {
        deleteFile(USER_TABLE_NAME);
        assertEquals(data.appendUsers(users).getStatus(), Constants.SUCCESS);
        Optional<User> user =  data.deleteUserById(0);
        assertEquals(user, users.stream().filter(x -> x.getId() == 0).findFirst());

    }

    public void testAppendStocks()  {
        deleteFile(STOCK_TABLE_NAME);
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);

    }

    public void testFailAppendStocks()  {
        deleteFile(STOCK_TABLE_NAME);
        List<Stock> otherStocks = new ArrayList<>(stocks);
        otherStocks.add(stocks.get(0));
        stockResult = data.appendStocks(otherStocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        stockResult = data.appendStocks(null);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        stockResult = data.appendStocks(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), WARN);
        assertEquals(stockResult.getBody().size(), stocks.size());


    }

    public void testGetStocks()  {
        deleteFile(STOCK_TABLE_NAME);
        stockResult = data.appendStocks(stocks);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.getStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getBody().size(), stocks.size());
    }

    public void testFailGetStocks()  {
        deleteFile(STOCK_TABLE_NAME);
        stockResult = data.getStocks();
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
    }

    public void testUpdateStocks(){
        deleteFile(STOCK_TABLE_NAME);
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        System.out.println(data.getStocks());
        Stock stock = new Stock("SBER", "", "" , "",
                90, "RUB", "1", "qwe",
                90, MarketType.SHARES, getHistories("SBER"),Stock.StockType.COMMON, 0, 0);
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), SUCCESS);
        stockResult = data.getStocks();
        System.out.println(stockResult);
        Optional<Stock> stockFromDB = stockResult.getBody().stream().filter(x -> x.getTicker().equals("SBER")).findFirst();
        if (stockFromDB.isPresent())
            assertEquals(stockFromDB.get(), stock);
        else
            assert(false);
    }

    public void testFailUpdateStocks(){
        deleteFile(STOCK_TABLE_NAME);
        Stock stock = new Stock("SHUSHU", "", "" , "",
                90, "RUB", "1", "qwe",
                90, MarketType.SHARES,getHistories("SBER") ,Stock.StockType.COMMON, 0, 0);
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        assertEquals(stockResult.getStatus(), FAIL);
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        System.out.println(data.getStocks());
        stockResult = data.updateStocks(new ArrayList<>(List.of(stock)));
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), WARN);
        stockResult = data.updateStocks(new ArrayList<>());
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
        stockResult = data.updateStocks(null);
        System.out.println(stockResult);
        assertEquals(stockResult.getStatus(), FAIL);
    }
    public void testDeleteStockByTicker() throws Exception {
        deleteFile(STOCK_TABLE_NAME);
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        Optional<Stock> deletedStock = data.deleteStockByTicker("SBER");
        Optional<Stock> stock= stocks.stream().filter(x -> x.getTicker().equals("SBER")).findFirst();
        if (deletedStock.isPresent())
            assertEquals(deletedStock, stock);
        else
            assert(false);
        stockResult = data.getStocks();
        List<Stock> updatedStocks = new ArrayList<>(stocks);
        assertEquals(stockResult.getStatus(), SUCCESS);
        assertEquals( stockResult.getBody().size(), stocks.size() -1);
        updatedStocks.removeAll(stockResult.getBody());
        assertEquals(updatedStocks.size(), 1);
        assertEquals(updatedStocks.get(0), deletedStock.get());
    }

    public void testFailDeleteStockByTicker(){
        deleteFile(STOCK_TABLE_NAME);
        try{
            data.deleteStockByTicker("SBABABABQQWEQR");
        }catch (Exception e){
            System.out.println(e.getMessage());
            assert(true);
        }
        assertEquals(data.appendStocks(stocks).getStatus(), SUCCESS);
        try{
            Optional<Stock> deletedStock = data.deleteStockByTicker("SBABABABQQWEQR");
            assertEquals(deletedStock, Optional.empty());
        }catch (Exception e){
            System.out.println(e.getMessage());
            assert(false);
        }


    }
    public void testGetStockByTicker()  {
        deleteFile(STOCK_TABLE_NAME);
        data.appendSecurityHistory(histories, "SBER");
        data.appendStocks(stocks);
        Optional<Stock> stock = data.getStockByTicker("SBER");
        System.out.println(stock);
        if (stock.isPresent())
            assert(true);
        else
            assert(false);
        assertEquals(stock.get().getHistory(), data.getSecurityHistoryByDate("SBER"));
    }

    public void testFailGetStockByTicker(){
        deleteFile(STOCK_TABLE_NAME);
        Optional<Stock> stock = data.getStockByTicker("SBER");
        System.out.println(stock);
        if (stock.isEmpty())
            assert(true);
        else
            assert(false);
        data.appendStocks(stocks);
        stock = data.getStockByTicker("SBER1231421");
        System.out.println(stock);
        if (stock.isEmpty())
            assert(true);
        else
            assert(false);
    }

    public void testAppendSecurityHistory(){
        data.deleteAllSecurityHistories("SBER");
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);

    }

    public void testFailAppendSecurityHistory(){
        data.deleteAllSecurityHistories("SBER");
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER1");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        securityHistoryResult = data.appendSecurityHistory(histories, null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        securityHistoryResult = data.appendSecurityHistory(null, "");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getStatus(), WARN);
        assertEquals(securityHistoryResult.getBody() ,histories);
    }

    public void testGetSecurityHistories(){
        data.deleteAllSecurityHistories("SBER");
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);
        securityHistoryResult = data.getSecurityHistories("SBER");
        System.out.println(securityHistoryResult);
        assertEquals(securityHistoryResult.getBody(), histories);
    }

    public void testFailGetSecurityHistories(){
        data.deleteAllSecurityHistories("SBER");
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), FAIL);
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);

        securityHistoryResult = data.getSecurityHistories("SBER1");
        assertEquals(securityHistoryResult.getStatus(), FAIL);

        securityHistoryResult = data.getSecurityHistories(null);
        assertEquals(securityHistoryResult.getStatus(), FAIL);
    }

    public void testGetSecurityHistoryByDate(){
        data.deleteAllSecurityHistories("SBER");
        assertEquals(data.appendSecurityHistory(histories, "SBER").getStatus(), SUCCESS);
        for (String date : dateList){
            SecurityHistory securityHistory = data.getSecurityHistoryByDate("SBER", date);
            Optional<SecurityHistory> securityHistory1 = histories.stream().filter(x -> x.getDate().equals(date)).findFirst();
            assert securityHistory1.isPresent();
            assertEquals(securityHistory1.get(), securityHistory);
        }
    }

    public void testFailGetSecurityHistoryByDate(){
        data.deleteAllSecurityHistories("SBER");
        SecurityHistory securityHistory = new SecurityHistoryBuilder().empty("SBER1");
        for (String date : dateList){
            SecurityHistory securityHistory1 = data.getSecurityHistoryByDate("SBER",date);
            assertNotEquals(securityHistory1, securityHistory);
        }
        for (String date : dateList){
            SecurityHistory securityHistory1 = data.getSecurityHistoryByDate("SBER", date);
            assertEquals(securityHistory1, new SecurityHistoryBuilder().empty(date, "SBER"));
        }
    }

    public void testAppendOrUpdate(){
        data.deleteAllSecurityHistories("SBER");
        assertEquals(data.getSecurityHistories("SBER").getStatus(), FAIL);
        assert(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBER"), "SBER"));
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), 1);
        SecurityHistory securityHistory = new SecurityHistoryBuilder().withTicker("SBER").withAveragePerDay(12314)
                .withClosePrice(10).withOpenPrice(9).withVolume(1000).withDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()))
                .build();
        assert(data.appendOrUpdate(securityHistory, "SBER"));
        securityHistoryResult = data.getSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), 1);
        assertEquals(securityHistoryResult.getBody().get(0), securityHistory);
    }

    public void testFailAppendOrUpdate(){
        data.deleteAllSecurityHistories("SBER");
        assertFalse(data.appendOrUpdate(null, "SBER"));
        assertFalse(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBER"), null));
        assertFalse(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBER"), "SBERE!"));
        assertFalse(data.appendOrUpdate(new SecurityHistoryBuilder().empty("SBE1R"), "SBERE!"));
    }

    public void testDeleteAllSecurityHistories(){
        data.deleteAllSecurityHistories("SBER");
        securityHistoryResult = data.appendSecurityHistory(histories, "SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        Result<SecurityHistory> securityHistoryResult = data.deleteAllSecurityHistories("SBER");
        assertEquals(securityHistoryResult.getStatus(), SUCCESS);
        assertEquals(securityHistoryResult.getBody().size(), histories.size());
    }

    public void testFailDeleteAllSecurityHistories(){
        data.deleteAllSecurityHistories("SBER");
        assertEquals(data.deleteAllSecurityHistories("SBER").getStatus(), FAIL);
    }





}