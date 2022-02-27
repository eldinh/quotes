package ru.sfedu.api;

import ru.sfedu.model.*;

import java.util.List;
import java.util.Optional;

public interface DataProvider {

    // market

    /**
     * Method for appending market or updating it in database
     * @param marketType - market to update or append
     * @return status of this method
     * true - successfully
     * false - fail
     */
    public boolean appendOrUpdateMarket(MarketType marketType);

    /**
     * Method for getting all available markets
     * @return Result<Market>
     * Status
     * Message
     * Response - list of markets
     */
    public Result<Market> getMarkets();

    /**
     * Method for getting all securities in the current market
     * @param marketType - market type
     * @return Optional<Market> - Market object that contain list of securities
     */
    public Optional<Market> getMarket(MarketType marketType);

    // security

    /**
     * Method for getting all stocks from database
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all stocks from database
     */
    public Result<Stock> getStocks() ;

    /**
     * Method for appending stocks to database
     * @param stocks - list of stocks to append
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of stocks that haven't been appended
     */
    public Result<Stock> appendStocks(List<Stock> stocks) ;

    /**
     * Method for updating stocks by ticker
     * @param stocks - list of stocks to update
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - stocks that haven't been updated
     */
    public Result<Stock> updateStocks(List<Stock> stocks) ;

    /**
     * Method for deleting stock by ticker from database
     * @param ticker - Stock ticker
     * @return Optional<Stock> stock that was deleted if it existed
     */
    public Optional<Stock> deleteStockByTicker(String ticker);

    /**
     * Method for deleting all stocks from database
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of stocks that have been deleted
     */
    public Result<Stock> deleteAllStocks() ;

    /**
     * Method for getting stock by his ticker
     * @param ticker - stock's ticker
     * @return Optional<Stock> - Stock that was found by ticker(Optional.empty() if it wasn't found)
     */
    public Optional<Stock> getStockByTicker(String ticker) ;

    /**
     * Method for getting all bonds from database
     * @return Result<Bond> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all bonds from database
     */
    public Result<Bond> getBonds() ;

    /**
     * Method for appending bonds to database
     * @param bonds - list of bonds to append
     * @return Result<Bond> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of bonds that haven't been appended
     */
    public Result<Bond> appendBonds(List<Bond> bonds) ;

    /**
     * Method for updating bonds by ticker
     * @param bonds - list of bonds to update
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - bonds that haven't been updated
     */
    public Result<Bond> updateBonds(List<Bond> bonds) ;

    /**
     * Method for deleting bond by ticker from database
     * @param ticker - bond's ticker
     * @return Optional<Bond> bond that was deleted if it existed
     */
    public Optional<Bond> deleteBondByTicker(String ticker) ;

    /**
     * Method for deleting all bonds from database
     * @return Result<Stock> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of stocks that have been deleted
     */
    public Result<Bond> deleteAllBonds() ;

    /**
     * Method for getting bond by ticker from database
     * @param ticker - Bond ticker
     * @return Optional<Bond> bond that was deleted if it existed
     */
    public Optional<Bond> getBondByTicker(String ticker) ;

    /**
     * Method for getting security by his ticker
     * @param ticker - security's ticker
     * @return - Optional<Security>
     */
    public Optional<Security> getSecurityByTicker(String ticker);

    // security history

    /**
     * Method for appending security's histories to database, for each ticker method creates a separate table
     * @param securityHistories - list of histories to append
     * @param ticker - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of histories that haven't been appended
     */
    public Result<SecurityHistory> appendSecurityHistory(List<SecurityHistory> securityHistories, String ticker);

    /**
     * Method for getting security history by ticker
     * @param ticker - Security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security's history
     */
    public Result<SecurityHistory> getSecurityHistories(String ticker);

    /**
     * Method for getting Security history by date and ticker
     * @param ticker - Security's ticker
     * @param date - date that was required
     * @return SecurityHistory with special date that consists information or empty SecurityHistory that consists only date and ticker if it won't find
     */
    public SecurityHistory getSecurityHistoryByDate(String ticker, String date);

    /**
     * Method for getting today's Security history by ticker
     * @param ticker - Security's ticker
     * @return today's Security history. If Security history wasn't found in database method will return empty history
     */
    public SecurityHistory getSecurityHistoryByDate(String ticker);

    /**
     * Method for deleting security's history database by his ticker
     * @param ticker - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security history that was in database
     */
    public Result<SecurityHistory> deleteAllSecurityHistories(String ticker);

    /**
     * Method for deleting security's history database by ticker
     * @param tickerList - list of security's ticker histories that need to be deleted
     */
    public void deleteAllSecurityHistories(List<String> tickerList);

    /**
     * Method for appending or updating security's history by one date
     * @param securityHistory - Security history
     * @param ticker - security's ticker
     * @return boolean - result of the work
     */
    public boolean appendOrUpdateSecurityHistory(SecurityHistory securityHistory, String ticker);

    // user

    /**
     * Method for appending users to database
     * @param userList - list of users to append
     * @return Result<User>
     * Status
     * Message
     * Response - list of users that haven't been appended
     */
    public Result<User> appendUsers(List<User> userList);

    /**
     * Method for appending a user to database
     * @param name - name
     * @return Optional<String> - user's id if user was created and appended
     */
    public Optional<String> appendUser(String name);

    /**
     * Method for getting all users from database
     * @return Result<User>
     * Status
     * Message
     * Response - list of all users from database
     */
    public Result<User> getUsers();

    /**
     * Method for updating users by id
     * @param userList - list of users to update
     * @return Result<User>
     * Status
     * Message
     * Response - list of users that haven't been updated
     */
    public Result<User> updateUsers(List<User> userList);

    /**
     * Method for updating a user by id
     * @param user - user to update
     * @return Result<User>
     * Status
     * Message
     * Response - user if that hasn't been updated
     */
    public Result<User> updateUser(User user);

    /**
     * Method for updating a user by id
     * @param id - user's id
     * @param name - updated name
     * @return - user if that has been updated
     */
    public Optional<User> updateUser(String id, String name);

    /**
     * Method for finding user by id
     * @param id - user's id
     * @return Optional<User> - User that was found by id(Optional.empty() if it wasn't found)
     */
    public Optional<User> getUserById(String id);

    /**
     * Method for deleting user by id
     * @param id - user's id
     * @return Optional<User> - User that was found and deleted by id(Optional.empty() if it wasn't found and deleted)
     */
    public Optional<User> deleteUserById(String id);

    /**
     * Method for deleting all users from database
     * @return Result<User> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of users that have been deleted
     */
    public Result<User> deleteAllUsers();

    // action

    /**
     * Method for appending user's action
     * @param actionType - type of Action(ADD, DELETE)
     * @param userID - user's id
     * @param ticker - security's ticker which the action is performed
     * @return Optional<String> - action's id if it has been appended successfully
     */
    public Optional<String> appendAction(ActionType actionType, String userID, String ticker);

    /**
     * Method for getting all user's action by user's id
     * @param userID - user's id
     * @return Result<Action>
     * Status
     * Message
     * Response - list all user's action
     */
    public Result<Action> getActionHistory(String userID);

    /**
     * Method for deleting all user's actions by user's id
     * @param userID - user's id
     * @return
     * Status
     * Message
     * Response - list of user's actions that have been deleted
     */
    public Result<Action> deleteActionHistory(String userID);

    // use case

    /**
     * Основной метод, отображающий котировки активных(которые продаются или покупаются чаще других) ценных бумаг в виде таблицы.
     * Имеет также расширяющий вариант ShowDetailedInfo, который вызывается, когда сработает обработка событий на ценной бумаги той или иной компании (т.е. когда пользователь кликнет на интересующую его компанию).
     * В качестве параметра передается название рынка
     * @param marketType - Тип биржи
     * @return - Массив объектов классов ценных бумаг, который котируется на данной рынке
     */
    public Result<Security> getActiveSecurities(MarketType marketType);

    /**
     * Метод для поиска ценной бумаги по его названию или тикеру(кодовое обозначение актива на бирже).
     * При успешном обнаружении компании будет выводиться котировка его акции, т.е. вызовется функция ShowDetailedInfo.
     * При вводе названия рынка вызывается функция getActiveSecurities
     * @param ticker - Тикер ценной бумаги
     * @return Массив, содержащий котировку ценной бумаги за весь его период, пока он продавался на рынке
     */
    public Result<SecurityHistory> findSecurity(String ticker);

    /**
     * Метод для поиска ценной бумаги по его названию или тикеру(кодовое обозначение актива на бирже).
     * При успешном обнаружении компании будет выводиться котировка его акции, т.е. вызовется функция ShowDetailedInfo.
     * При вводе названия рынка вызывается функция getActiveSecurities
     * @param marketType - Тип рынка
     * @return Массив объектов классов ценных бумаг, который котируется на данной рынке
     */
    public Result<Security> findSecurity(MarketType marketType);

    /**
     * Функция, которая является включающей для findSecurityByTicker.
     * Она выводит информацию об стоимости акции за указанный период в виде графика. В качестве входного параметра принимает тикер ценной бумаги.
     * Имеет включающий вариант использования showInfo
     * Расширяется вариантом использования setNotification в случае, если пользователь решит задать оповещение.
     * @param ticker - тикер ценной бумаги
     * @return List<SecurityHistory> - котировка данной ценной бумаги за все время его торговли на рынке
     */
    public Result<SecurityHistory> showDetailedInfo(String ticker);

    /**
     * Метод выводит информацию об компании:
     *  - Число акции
     *  - Номинал
     *  - Капитализация
     *  - Выручка
     *  - Прибыль в текущем квартале
     * @param ticker - Тикер ценной бумаги
     * @return - строка, содержащую информацию о компании, описанная выше
     */
    public String showInfo(String ticker);

    /**
     * Выводит информацию об акциях пользователя, который он сохранил в портфель. Метод доступен только для авторизованных пользователей.
     * Включает в себя вариант использования ShowStatictics.
     * Расширяется вариантом использования performAction, который срабатывает, если пользователь решил произвести какое-либо действие над акциями в портфеле.
     * @param userId - айди пользователя
     * @return - Массив с объектами класса ценных бумаг, который сохранил пользователь.
     */
    public Result<Security> checkVirtualBriefCase(String userId);

    /**
     * Метод выводит краткую информацию об ценных бумагах в портфеле пользователя и информацию об его активностях
     * @param userId - айди пользователя
     * @return - строка, содержащую информацию, описанная выше.
     */
    public String showHistory(String userId);

    /**
     * Метод, который обрабатывает портфель пользователя(массив объектов класса акций) и исполняет указанное действие, который задал пользователь.
     * Изменения будут сохранены в базе данных и записаны в историю пользователя.
     * @param userId - айди пользователя
     * @param actionType - имя команды
     * @param ticker - тикер ценной бумаги
     * @return
     * true<boolean> - операция выполнена успешно
     * false <boolean> - произошла ошибка при выполнении операции
     */
    public boolean performActon(String userId, String actionType, String ticker);
    
}
