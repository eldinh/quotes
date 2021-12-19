package ru.sfedu.api;

import ru.sfedu.model.*;

import java.util.List;
import java.util.Optional;

public interface DateProvider {

    public boolean appendOrUpdateMarket(MarketType marketType);
    public Result<Market> getMarkets();
    public Optional<Market> getMarket(MarketType marketType);

    /**
     * Method to get all users from database
     * @return Result<User> - (Status, Message, Response)
     * Status
     * Message
     * Response - List of all users in database
     *
     */
    public Result<User> getUsers() ;

    /**
     * Method to append users
     * @param users - list os user to append to database
     * @return Result<User> - (Status, Message, Response)
     * Status
     * Message
     * Response - users that haven't been appended
     */
    public Result<User> appendUsers(List<User> users) ;

    /**
     * Method to update users by id
     * @param users - list of users to update
     * @return Result<User> - (Status, Message, Response)
     * Status
     * Message
     * Response - users that haven't been updated
     */
    public Result<User> updateUsers(List<User> users) ;

    /**
     * Method to delete user by id
     * @param id - UserID
     * @return Optional<User> user that was deleted if it existed
     */
    public Optional<User> deleteUserById(long id);

    /**
     * Method to delete all users
     * @return Result<User> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of users that have been deleted
     */
    public Result<User> deleteAllUsers() ;

    /**
     * Method to get user by ID
     * @param id - UserID
     * @return Optional<User> - User that was found by id(Optional.empty() if it wasn't found)
     */
    public Optional<User> getUserById(long id) ;

    /**
     * Method to get all stocks from database
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all stocks from database
     */
    public Result<Stock> getStocks() ;

    /**
     *
     * @param stocks - list of stocks to append
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of stocks that haven't been appended
     */
    public Result<Stock> appendStocks(List<Stock> stocks) ;

    /**
     * Method to update stocks by ticker
     * @param stocks - list of stocks to update
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - stocks that haven't been updated
     */
    public Result<Stock> updateStocks(List<Stock> stocks) ;

    /**
     *
     * @param ticker - Stock ticker
     * @return Optional<Stock> stock that was deleted if it existed
     */
    public Optional<Stock> deleteStockByTicker(String ticker);

    /**
     * Method to delete all stocks from database
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of stocks that have been deleted
     */
    public Result<Stock> deleteAllStocks() ;

    /**
     * Method to get stock by his ticker
     * @param ticker - stock's ticker
     * @return Optional<Stock> - Stock that was found by ticker(Optional.empty() if it wasn't found)
     */
    public Optional<Stock> getStockByTicker(String ticker) ;


    /**
     * Method to get all bonds from database
     * @return Result<Bond> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all bonds from database
     */
    public Result<Bond> getBonds() ;
    /**
     *
     * @param bonds - list of bonds to append
     * @return Result<Bond> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of bonds that haven't been appended
     */
    public Result<Bond> appendBonds(List<Bond> bonds) ;

    /**
     * Method to update bonds by ticker
     * @param bonds - list of bonds to update
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - bonds that haven't been updated
     */
    public Result<Bond> updateBonds(List<Bond> bonds) ;


    /**
     *
     * @param ticker - bond's ticker
     * @return Optional<Bond> bond that was deleted if it existed
     */
    public Optional<Bond> deleteBondByTicker(String ticker) ;

    /**
     * Method to delete all stocks from database
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of stocks that have been deleted
     */
    public Result<Bond> deleteAllBonds() ;


    /**
     *
     * @param ticker - Bond ticker
     * @return Optional<Bond> bond that was deleted if it existed
     */
    public Optional<Bond> getBondByTicker(String ticker) ;

    /**
     * Method to append security histories to database, for each ticker method creates a separate table
     * @param securityHistories - list of histories to append
     * @param ticker - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of histories that haven't been appended
     */
    public Result<SecurityHistory> appendSecurityHistory(List<SecurityHistory> securityHistories, String ticker);

    /**
     * Method to get security history by ticker
     * @param ticker - Security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security's history
     */
    public Result<SecurityHistory> getSecurityHistories(String ticker);

    /**
     * Method to get Security history by date and ticker
     * @param ticker - Security's ticker
     * @param date - date that was required
     * @return SecurityHistory with special date that consists information or empty SecurityHistory that consists only date and ticker if it won't find
     */
    public SecurityHistory getSecurityHistoryByDate(String ticker, String date);

    /**
     * Method to get today's Security history by ticker
     * @param ticker - Security's ticker
     * @return today's Security history. If Security history wasn't found in database method will return empty history
     */
    public SecurityHistory getSecurityHistoryByDate(String ticker);

    /**
     * Method to delete security's history database by his ticker
     * @param ticker - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security history that was in database
     */
    public Result<SecurityHistory> deleteAllSecurityHistories(String ticker);

    /**
     * Method to delete security's history database by ticker
     * @param tickerList - list of security's ticker histories that need to be deleted
     */
    public void deleteAllSecurityHistories(List<String> tickerList);

    /**
     * Method to append or update security's history by one date
     * @param securityHistory - Security history
     * @param ticker - security's ticker
     * @return boolean - result of the work
     */
    public boolean appendOrUpdate(SecurityHistory securityHistory, String ticker);

}
