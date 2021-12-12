package ru.sfedu.api;

import ru.sfedu.model.User;
import ru.sfedu.model.Result;
import ru.sfedu.model.Bond;
import ru.sfedu.model.Stock;

import java.util.List;
import java.util.Optional;

public interface DateProvider {
    /**
     *
     * @return
     */
    public Result<User> getUsers() ;
    public Result<User> appendUsers(List<User> users) ;
    public Result<User> updateUsers(List<User> users) ;
    public Optional<User> deleteUserById(long id) throws Exception;
    public Result<User> deleteAllUsers() ;
    public Optional<User> getUserById(long id) throws Exception;

    public Result<Stock> getStocks() ;
    public Result<Stock> appendStocks(List<Stock> stocks) ;
    public Result<Stock> updateStocks(List<Stock> stocks) ;
    public Optional<Stock> deleteStockByTicker(String ticker) throws Exception;
    public Result<Stock> deleteAllStocks() ;
    public Optional<Stock> getStockByTicker(String ticker) throws Exception;

    public Result<Bond> getBonds() ;
    public Result<Bond> appendBonds(List<Bond> bonds) ;
    public Result<Bond> updateBonds(List<Bond> bonds) ;
    public Optional<Bond> deleteBondByTicker(String ticker) throws Exception;
    public Result<Bond> deleteAllBonds() ;
    public Optional<Bond> getBondByTicker(String ticker) throws Exception;



}
