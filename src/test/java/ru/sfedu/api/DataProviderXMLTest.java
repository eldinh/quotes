package ru.sfedu.api;

import ru.sfedu.BaseTest;
import ru.sfedu.Constants;
import ru.sfedu.model.MarketType;
import ru.sfedu.model.Stock;
import ru.sfedu.model.User;
import ru.sfedu.model.Result;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotEquals;
import static ru.sfedu.Constants.*;
import static ru.sfedu.Constants.FAIL;

public class DataProviderXMLTest extends BaseTest {
    DataProviderXML data = new DataProviderXML();

    private void deleteFile(String fileName){
        File file = new File(String.format("./src/main/resources/data/%s.xml", fileName));
        if (file.exists()){
            file.delete();
        }
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
        try{
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

            assert(false);
        } catch (Exception e){
            assert(true);
        }



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
                90, MarketType.SHARES, Stock.StockType.COMMON, 0, 0);
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
                90, MarketType.SHARES, Stock.StockType.COMMON, 0, 0);
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
}