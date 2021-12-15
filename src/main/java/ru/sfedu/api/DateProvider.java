package ru.sfedu.api;

import ru.sfedu.model.User;
import ru.sfedu.model.Result;
import ru.sfedu.model.Bond;
import ru.sfedu.model.Stock;

import java.util.List;
import java.util.Optional;

public interface DateProvider {
    /**
     * Method to get all users
     * @return
     *
     */
    public Result<User> getUsers() ;

    /**
     *
     * @param users
     * @return
     */
    public Result<User> appendUsers(List<User> users) ;

    /**
     *
     * @param users
     * @return
     */
    public Result<User> updateUsers(List<User> users) ;

    /**
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Optional<User> deleteUserById(long id) throws Exception;

    /**
     *
     * @return
     */
    public Result<User> deleteAllUsers() ;

    /**
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Optional<User> getUserById(long id) throws Exception;

    /**
     *
     * @return
     */
    public Result<Stock> getStocks() ;

    /**
     *
     * @param stocks
     * @return
     */
    public Result<Stock> appendStocks(List<Stock> stocks) ;

    /**
     *
     * @param stocks
     * @return
     */
    public Result<Stock> updateStocks(List<Stock> stocks) ;

    /**
     *
     * @param ticker
     * @return
     * @throws Exception
     */
    public Optional<Stock> deleteStockByTicker(String ticker) throws Exception;

    /**
     *
     * @return
     */
    public Result<Stock> deleteAllStocks() ;

    /**
     *
     * @param ticker
     * @return
     * @throws Exception
     */
    public Optional<Stock> getStockByTicker(String ticker) throws Exception;

    public Result<Bond> getBonds() ;
    public Result<Bond> appendBonds(List<Bond> bonds) ;
    public Result<Bond> updateBonds(List<Bond> bonds) ;
    public Optional<Bond> deleteBondByTicker(String ticker) throws Exception;
    public Result<Bond> deleteAllBonds() ;
    public Optional<Bond> getBondByTicker(String ticker) throws Exception;



}
