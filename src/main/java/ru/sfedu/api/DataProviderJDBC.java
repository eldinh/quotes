package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.builder.BondBuilder;
import ru.sfedu.builder.SecurityBuilder;
import ru.sfedu.builder.StockBuilder;
import ru.sfedu.builder.UserBuilder;
import ru.sfedu.model.*;
import ru.sfedu.utils.ValidEntityListValidator;

import java.sql.*;
import java.util.*;

import static ru.sfedu.Constants.*;
import static ru.sfedu.model.CommandType.*;
import static ru.sfedu.model.RepositoryType.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderJDBC implements DateProvider {
    private final Logger log = LogManager.getLogger(DataProviderJDBC.class.getName());


    private List<Long> getUsersID(List<User> users){
        return users.stream().map(User::getId).toList();
    }

    private <T extends Security> List<String> getSecuritiesTicker(List<T> securities){
        return new ArrayList<>(securities.stream().map(Security::getTicker).toList());
    }

    private User resultSetToUser(ResultSet rs) throws SQLException {
        return new UserBuilder().setId(rs.getLong(USER_COLUMN_ID))
                                .setName(rs.getString(USER_COLUMN_NAME))
                                .setAge(rs.getInt(USER_COLUMN_AGE))
                                .build();
    }

    private SecurityBuilder resultToSecurityBuilder(ResultSet rs) throws SQLException {
        return new SecurityBuilder()
                .withTicker(rs.getString(SECURITY_COLUMN_TICKER))
                .withName(rs.getString(SECURITY_COLUMN_NAME))
                .withShortName(rs.getString(SECURITY_COLUMN_SHORTNAME))
                .withLatName(rs.getString(SECURITY_COLUMN_LATNAME))
                .withNominal(rs.getDouble(SECURITY_COLUMN_NOMINAL))
                .withNominalValue(rs.getString(SECURITY_COLUMN_NOMINALVALUE))
                .withIssueDate(rs.getString(SECURITY_COLUMN_ISSUEDATE))
                .withIsin(rs.getString(SECURITY_COLUMN_ISIN))
                .withIssueSize(rs.getLong(SECURITY_COLUMN_ISSUESIZE))
                .withMarketType(MarketType.valueOf(rs.getString(SECURITY_COLUMN_MARKETTYPE)));
    }

    private Stock resultSetToStock(ResultSet rs) throws SQLException{
        return new StockBuilder(resultToSecurityBuilder(rs))
                .withType(Stock.StockType.valueOf(rs.getString(STOCK_COLUMN_TYPE)))
                .withDividendSum(rs.getDouble(STOCK_COLUMN_DIVIDENDSUM))
                .withCapitalization(rs.getDouble(STOCK_COLUMN_CAPITALIZATION))
                .build();
    }

    private Bond resultSetToBond(ResultSet rs) throws SQLException{
        return new BondBuilder(resultToSecurityBuilder(rs))
                .withCoupon(rs.getDouble(BOND_COLUMN_COUPON))
                .withDayToRedemption(rs.getInt(BOND_COLUMN_DAYTOREDEMPTION))
                .withMatDate(rs.getString(BOND_COLUMN_MATDATE))
                .withType(Bond.BondType.valueOf(rs.getString(BOND_COLUMN_TYPE)))
                .build();
    }



    private String setStockValues(Stock stock){
        return String.format(Locale.ROOT, SQL_STOCK_VALUES, stock.getTicker(), stock.getName(),
                stock.getShortName(), stock.getLatName(),
                stock.getNominal(), stock.getNominalValue(),
                stock.getIssueDate(), stock.getIsin(),
                stock.getIssueSize(), stock.getMarketType().toString(),
                stock.getType(), stock.getDividendSum(),
                stock.getCapitalization());
    }

    private String setBondValues(Bond bond){
        return String.format(SQL_BOND_VALUES, bond.getTicker(), bond.getName(),
                bond.getShortName(), bond.getLatName(),
                bond.getNominal(), bond.getNominalValue(),
                bond.getIssueDate(), bond.getIsin(),
                bond.getIssueSize(), bond.getMarketType().toString(),
                bond.getType(), bond.getMatDate(),
                bond.getCoupon(), bond.getDayToRedemption());
    }

    private String sqlUpdateUser(User user){
        return String.format(SQL_UPDATE, USER_TABLE_NAME)
                .concat(String.format(SQL_SET_USER, user.getName(), user.getAge()))
                .concat(SQL_WHERE + String.format(SQL_USER_ID, user.getId()));
    }

    private String sqlUpdateStock(Stock stock){
        return String.format(SQL_UPDATE, STOCK_TABLE_NAME)
                .concat(String.format(Locale.ROOT,SQL_SET_STOCK , stock.getName(), stock.getShortName(),
                        stock.getLatName(), stock.getNominal(), stock.getNominalValue(),
                        stock.getIssueDate(), stock.getIsin(), stock.getIssueSize(),
                        stock.getMarketType().toString(), stock.getType().toString(),
                        stock.getDividendSum(), stock.getCapitalization()))
                .concat(SQL_WHERE + String.format(SQL_SECURITY_TICKER, stock.getTicker()));
    }

    public void dropTable(String dbName) {
        log.info("Starting DataProviderJDBC dropTable");
        try {
            log.info("dropTable: {}", dbName);
            log.debug("dropTable: Getting connection");
            Connection connection = getDbConnection(dbName);
            log.debug("dropTable: Dropping {} table", dbName);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, dbName));

            preparedStatement.executeUpdate();
        } catch (Exception e){
            log.error("Function DataProvider dropTable had failed: {}", e.getMessage());
        }

    }

    public Connection getDbConnection(String dbName)
            throws Exception {
        log.info("Starting DataProviderJDBC getDbConnection[0]");
        try {
            String connectionString = String.format(getConfigurationEntry(JDBC_CONNECTION), getConfigurationEntry(DB_PATH),
                    dbName);
            Class.forName(getConfigurationEntry(H2_DRIVER));
            log.debug("getDbConnection[1]: connect to DB");
            return DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                    getConfigurationEntry(DB_PASSWORD));

        } catch (Exception e){
            log.error("Function DataProviderJDBC getDbConnection had failed[2]");
            throw new Exception(e);
        }

    }




    private void creatingTable(Class<?> objClass, String column) throws Exception {
        log.info("Starting DataProviderJDBC creatingTable[3]");
        try {
            Connection conn = getDbConnection(objClass.getSimpleName().toUpperCase());
            log.info("creatingTable[4]: Connect to db");
            Statement stmt = conn.createStatement();
            log.debug("creatingTable[5]: Create table if it doesn't exist");
            stmt.executeUpdate(String.format(SQL_CREATE_TABLE, objClass.getSimpleName().toUpperCase()).concat(column));
            log.debug("creatingTable[6]: Closing statement");
            stmt.close();
            log.debug("creatingTable[7]: Closing connection");
            conn.close();
        } catch (Exception e) {
            log.error("Function DataProviderJDBC creatingTable had failed[8]");
            throw new Exception(e);
        }

    }

    @Override
    public Result<User> appendUsers(List<User> users) {
        log.info("Starting DataProviderJDBC appendUsers[9]");
        try {
            log.info("appendUsers[10]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass().getName());
            ValidEntityListValidator.isValidUser(users);
            log.debug("appendUsers[11]: Create table");
            creatingTable(User.class, SQL_USER_COLUMNS);
            log.debug("appendUsers[12]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("appendUsers[13]: Get all ID");
            List<Long> allId = getUsersID(getUsers().getBody());
            List<User> response = users.stream().filter(x -> allId.contains(x.getId())).toList();
            log.debug("appendUsers[14]: append users with ID");
            for (User user : users.stream().filter(x ->!allId.contains(x.getId())).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,USER_TABLE_NAME )
                        .concat(String.format(SQL_USER_VALUES, user.getId(), user.getName(), user.getAge())));
            log.debug("appendUsers[15]: append users without ID");
            for (User user : users.stream().filter(x -> x.getId()== null).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,USER_TABLE_NAME )
                        .concat(String.format(SQL_USER_VALUES_WITHOUT_ID,  user.getName(), user.getAge())));
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Users have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of users that haven't been appended: %d", response.size()), response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendUsers had failed[16]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> getUsers()  {
        log.info("Starting DataProviderJDBC getUsers[17]");
        List<User> users = new ArrayList<>();
        try {
            log.debug("getUsers[18]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("getUsers[19]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                users.add(resultSetToUser(rs));
            }
            return new Result<>(SUCCESS, String.format("Number of users in file: %d", users.size()), users);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getUsers had failed[20]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users){
        log.info("Starting DataProviderJDBC updateUsers[21]");
        try {
            log.info("updateUsers[22]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass().getName());
            ValidEntityListValidator.isValidUserToUpdate(users);
            List<User> response = new ArrayList<>();
            List<User> userToUpdate = new ArrayList<>(users);
            log.debug("updateUsers[24]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("updateUsers[25]: Update users");
            for (int i = 0; i < userToUpdate.size(); i ++)
                if( connection.createStatement().executeUpdate(sqlUpdateUser(userToUpdate.get(i))) == 0)
                    response.add(userToUpdate.remove(i));
            connection.commit();
            MongoHistory.save(UPDATE, JDBC, userToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, String.format("Users have been updated successfully, number of updated users: %d", userToUpdate.size()), response);
            return new Result<>(WARN, String.format("Number of users that haven't been updated: %d", response.size()),response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateUsers had failed[26]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id) throws Exception {
        log.info("Starting DataProviderJDBC deleteUserById[27]");
        try {
            log.info("deleteUserById[28]: {}",id);
            log.debug("deleteUserById[]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("deleteUserById[29]: Get user by ID {}", id);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id))).executeQuery();
            if (rs.next()){
                log.debug("deleteUserById[30]: Delete user by ID {}", id);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, USER_TABLE_NAME)
                        .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id)));
                connection.commit();
                MongoHistory.save(DELETE, JDBC, resultSetToUser(rs));
                return Optional.of(resultSetToUser(rs));
            }

            return Optional.empty();
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteUserById had failed[31]");
            throw new Exception(e);
        }

    }

    @Override
    public Result<User> deleteAllUsers() {
        log.info("Starting DataProviderJDBC deleteAllUsers [32]");
        try {
            log.debug("deleteAllUsers[33]: get all users");
            Result<User> result = getUsers();
            if (getUsers().getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllUsers[34]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("deleteAllUsers[35]: Delete all users");
            int count  = connection.prepareStatement(String.format(SQL_DELETE_FROM, USER_TABLE_NAME)).executeUpdate();
            log.info("deleteAllUsers[36]: Number of delete users: {}" , count);
            connection.commit();
            MongoHistory.save(DELETE, JDBC, result.getBody());
            return new Result<>(result.getStatus(), String.format("Number of delete users: %d", count), result.getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllUsers had failed[37]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }

    }

    @Override
    public Optional<User> getUserById(long id) throws Exception {
        log.info("Starting DataProviderJDBC getUSerById[38]");
        try {
            log.debug("getUserById[39]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("getUserById[40]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id))).executeQuery();
            if (rs.next())
                return Optional.of(resultSetToUser(rs));
            return Optional.empty();
        } catch (Exception e){
            log.error("Function DataProvider JDBC getUserById had failed[41]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<Stock> getStocks() {
        log.info("Starting DataProviderJDBC getStocks[]");
        List<Stock> stocks = new ArrayList<>();
        try {
            log.debug("getStocks[]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("getStocks[]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, STOCK_TABLE_NAME));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                stocks.add(resultSetToStock(rs));

            }
            return new Result<>(SUCCESS, String.format("Number of stocks in file: %d", stocks.size()), stocks);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getStocks had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Stock> appendStocks(List<Stock> stocks) {
        log.info("Starting DataProviderJDBC appendStocks[]");
        try {
            log.info("appendStocks[]: {}, type: {}", Arrays.toString(stocks.toArray()), stocks.getClass().getName());
            ValidEntityListValidator.isValidSecurity(stocks);
            log.debug("appendStocks[]: Create table");
            creatingTable(Stock.class, SQL_STOCK_COLUMNS);
            log.debug("appendStocks[]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("appendStocks[]: Get all ticker");
            List<String> allTicker = getSecuritiesTicker(getStocks().getBody());
            List<Stock> response = new ArrayList<>(stocks.stream().filter(x -> allTicker.contains(x.getTicker())).toList());
            log.debug("appendStocks[]: append users with ticker");
            for (Stock stock : stocks.stream().filter(x ->!allTicker.contains(x.getTicker())).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,STOCK_TABLE_NAME)
                        .concat(setStockValues(stock)));
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendStocks had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Stock> updateStocks(List<Stock> stocks) {
        log.info("Starting DataProviderJDBC updateStocks[]");
        try {
            log.info("updateStocks[]: {}, type: {}", Arrays.toString(stocks.toArray()), stocks.getClass().getName());
            ValidEntityListValidator.isValidSecurity(stocks);
            List<Stock> response = new ArrayList<>();
            List<Stock> stockToUpdate = new ArrayList<>(stocks);
            log.debug("updateStocks[]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("updateStocks[]: Update users");
            for (int i = 0; i < stockToUpdate.size(); i ++)
                if( connection.createStatement().executeUpdate(sqlUpdateStock(stockToUpdate.get(i))) == 0)
                    response.add(stockToUpdate.remove(i));
            connection.commit();
            MongoHistory.save(UPDATE, JDBC, stockToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, String.format("Securities have been updated successfully, number of updated users: %d", stockToUpdate.size()), response);
            return new Result<>(WARN, String.format("Number of Securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateStocks had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Stock> deleteStockByTicker(String ticker) throws Exception {
        log.info("Starting DataProviderJDBC deleteStockByTicker[27]");
        try {
            log.info("deleteStockByTicker[]: {}",ticker);
            log.debug("deleteStockByTicker[]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("deleteStockByTicker[]: Get stock by ticker: {}", ticker);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, STOCK_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next()){
                log.debug("deleteStockByTicker[]: Delete stock by ticker: {}", ticker);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, STOCK_TABLE_NAME)
                        .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker)));
                connection.commit();
                MongoHistory.save(DELETE, JDBC, resultSetToStock(rs));
                return Optional.of(resultSetToStock(rs));
            }
            return Optional.empty();
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteStockByTicker had failed[]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<Stock> deleteAllStocks() {
        return null;
    }

    @Override
    public Optional<Stock> getStockByTicker(String ticker) throws Exception {
        return Optional.empty();
    }



    @Override
    public Result<Bond> getBonds() {
        return null;
    }

    @Override
    public Result<Bond> appendBonds(List<Bond> bonds) {
        return null;
    }

    @Override
    public Result<Bond> updateBonds(List<Bond> bonds) {
        return null;
    }

    @Override
    public Optional<Bond> deleteBondByTicker(String ticker) throws Exception {
        return Optional.empty();
    }

    @Override
    public Result<Bond> deleteAllBonds() {
        return null;
    }

    @Override
    public Optional<Bond> getBondByTicker(String ticker) throws Exception {
        return Optional.empty();
    }

}
