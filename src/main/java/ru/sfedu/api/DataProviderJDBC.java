package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.model.*;
import ru.sfedu.utils.Validator;

import java.sql.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.Function;

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

    private List<String> getDate(List<SecurityHistory> securityHistories){
        return new ArrayList<>(securityHistories.stream().map(SecurityHistory::getDate).toList());
    }

    private User resultSetToUser(ResultSet rs) throws SQLException {
        return new UserBuilder().setId(rs.getLong(USER_COLUMN_ID))
                                .setName(rs.getString(USER_COLUMN_NAME))
                                .setAge(rs.getInt(USER_COLUMN_AGE))
                                .build();
    }

    private String sqlUpdateUser(User user){
        return String.format(SQL_UPDATE, USER_TABLE_NAME)
                .concat(String.format(SQL_SET_USER, user.getName(), user.getAge()))
                .concat(SQL_WHERE + String.format(SQL_USER_ID, user.getId()));
    }





    public Connection getDbConnection(String dbName, String extraPath)
            throws Exception {
        log.info("Starting DataProviderJDBC getDbConnection[0]");
        try {
            log.info("getDbConnection[1]: dbName - {}, extraPath - {}", dbName, extraPath);
            String connectionString = String.format(getConfigurationEntry(JDBC_CONNECTION), getConfigurationEntry(DB_PATH).concat(extraPath),
                    dbName.toUpperCase());
            Class.forName(getConfigurationEntry(H2_DRIVER));
            log.debug("getDbConnection[1]: connect to DB");
            return DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                    getConfigurationEntry(DB_PASSWORD));

        } catch (Exception e){
            log.error("Function DataProviderJDBC getDbConnection had failed[2]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    public void dropTable(String dbName) {
        log.info("Starting DataProviderJDBC dropTable[]");
        try {
            log.info("dropTable: dbName - {}", dbName);
            log.debug("dropTable: Getting connection");
            Connection connection = getDbConnection(dbName);
            log.debug("dropTable: Dropping {} table", dbName);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, dbName));
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e){
            log.error("Function DataProvider dropTable had failed: {}", e.getMessage());
        }
    }

    public Connection getDbConnection(String dbName)
            throws Exception {
        return getDbConnection(dbName, "");
    }
    public Connection getSecurityHistoryDBConnection(String dbName)
            throws Exception {
        log.info("Starting DataProviderJDBC getHistoryDBConnection[3]");
        return getDbConnection(dbName, SECURITY_HISTORY_PATH);
    }

    private void creatingTable(String tableName,String direction, String column) throws Exception {
        log.info("Starting DataProviderJDBC creatingTable[3]");
        try {
            Connection conn = getDbConnection(tableName, direction);
            log.info("creatingTable[]: tableName - {}, direction - {}, column - {}", tableName, direction, column);

            log.debug("creatingTable[4]: Connect to db");
            Statement stmt = conn.createStatement();
            log.debug("creatingTable[5]: Create table if it doesn't exist");
            stmt.executeUpdate(String.format(SQL_CREATE_TABLE, tableName.toUpperCase()).concat(column));
            log.debug("creatingTable[6]: Closing statement");
            stmt.close();
            log.debug("creatingTable[7]: Closing connection");
            conn.close();
        } catch (Exception e) {
            log.error("Function DataProviderJDBC creatingTable had failed[8]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private void creatingTable(Class<?> objClass, String column) throws Exception {
        creatingTable(objClass.getSimpleName().toUpperCase(), "",column);
    }

    @Override
    public Result<User> appendUsers(List<User> users) {
        log.info("Starting DataProviderJDBC appendUsers[9]");
        try {
            log.info("appendUsers[10]: users - {}", Arrays.toString(users.toArray()));
            Validator.isValidUser(users);
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
            log.error("Function DataProviderJDBC appendUsers had failed[16]: {}", e.getMessage());
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
            log.error("Function DataProviderJDBC getUsers had failed[20]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users){
        log.info("Starting DataProviderJDBC updateUsers[21]");
        try {
            log.info("updateUsers[22]: {}", Arrays.toString(users.toArray()));
            Validator.isValidUserToUpdate(users);
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
            log.error("Function DataProviderJDBC updateUsers had failed[26]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id) {
        log.info("Starting DataProviderJDBC deleteUserById[27]");
        try {
            log.info("deleteUserById[28]: id - {}",id);
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
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteUserById had failed[31]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<User> deleteAllUsers() {
        log.info("Starting DataProviderJDBC deleteAllUsers [32]");
        try {
            log.debug("deleteAllUsers[33]: get all users");
            Result<User> result = getUsers();
            if (result.getStatus().equals(FAIL))
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
            log.error("Function DataProviderJDBC deleteAllUsers had failed[37]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }

    }

    @Override
    public Optional<User> getUserById(long id) {
        log.info("Starting DataProviderJDBC getUSerById[38]");
        try {
            log.info("getUserById[] : id - {}", id);
            log.debug("getUserById[39]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("getUserById[40]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id))).executeQuery();
            connection.commit();
            if (rs.next())
                return Optional.of(resultSetToUser(rs));
        } catch (Exception e){
            log.error("Function DataProvider JDBC getUserById had failed[41]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private <T extends Security> void setHistory(List<T> securities){
        for (T security : securities)
            security.setHistory(getSecurityHistoryByDate(security.getTicker()));
    }

    public <T extends Security> Result<T> getSecurities(String tableName, Function<ResultSet, Optional<T>> resultSetToSecurityFunction){
        log.info("Starting DataProviderJDBC getSecurities[42]");
        List<T> securities = new ArrayList<>();
        try {
            log.info("getSecurities[]: tableName - {}", tableName);
            log.debug("getSecurities[]: Connect to db");
            Connection connection = getDbConnection(tableName);
            log.debug("getSecurities[]: Get securities from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, tableName));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Optional<T> sec = resultSetToSecurityFunction.apply(rs);
                if (sec.isEmpty())
                    throw new Exception("Function DataProviderJDBC resultSetToSecurityFunction had failed");
                securities.add(sec.get());
            }
            log.debug("getSecurities[]: Set history to securities");
            setHistory(securities);
            return new Result<>(SUCCESS, String.format("Number of securities in file: %d", securities.size()), securities);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getSecurities had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Result<T> appendSecurity(List<T> securities, String securityTableName, String columns, Class<T> securityClass,
                                                         Function<T,String> setSecurityValueFunction, Supplier<Result<T>> getSecuritiesFunction){
        log.info("Starting DataProviderJDBC appendSecurity[]");
        try {
            log.info("appendSecurity[]: securities - {}, securityTableName - {}, columns - {}, securityClass - {}", securities, securityTableName, columns, securityClass);
            Validator.isValidSecurity(securities);
            log.debug("appendSecurity[]: Create table");
            creatingTable(securityClass, columns);
            log.debug("appendSecurity[]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("appendSecurity[]: Get all ticker");
            List<String> allTicker = getSecuritiesTicker(getSecuritiesFunction.get().getBody());
            List<T> response = new ArrayList<>(securities.stream().filter(x -> allTicker.contains(x.getTicker())).toList());
            log.debug("appendSecurity[]: append stocks with ticker");
            for (T sec : securities.stream().filter(x ->!allTicker.contains(x.getTicker())).toList()) {
                connection.createStatement().executeUpdate(String.format(SQL_INSERT, securityTableName)
                        .concat(setSecurityValueFunction.apply(sec)));
                appendOrUpdate(sec.getHistory(), sec.getTicker());
            }
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurity had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Result<T> updateSecurities(List<T> securities, String securityTableName,
                                                           Function<T, String> sqlUpdateSecurityFunction){
        log.info("Starting DataProviderJDBC updateSecurities[]");
        try {
            log.info("updateSecurities[]: securities - {}, securityTableName - {}", securities, securityTableName);
            Validator.isValidSecurity(securities);
            List<T> response = new ArrayList<>();
            List<T> securityToUpdate = new ArrayList<>(securities);
            log.debug("updateSecurities[]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("updateSecurities[]: Update users");
            for (int i = 0; i < securityToUpdate.size(); i ++)
                if( connection.createStatement().executeUpdate(sqlUpdateSecurityFunction.apply(securityToUpdate.get(i))) == 0)
                    response.add(securityToUpdate.remove(i));
                else
                    appendOrUpdate(securityToUpdate.get(i).getHistory(), securityToUpdate.get(i).getTicker());
            connection.commit();
            MongoHistory.save(UPDATE, JDBC, securityToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, String.format("Securities have been updated successfully, number of updated users: %d", securityToUpdate.size()), response);
            return new Result<>(WARN, String.format("Number of Securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateSecurities had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, String securityTableName,
                                                                   Function<ResultSet, Optional<T>> resultSetToSecurityFunction){
        log.info("Starting DataProviderJDBC deleteSecurityByTicker[]");
        try {
            log.info("deleteSecurityByTicker[]: ticker - {}, securityTableName - {}",ticker, securityTableName);
            log.debug("deleteSecurityByTicker[]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("deleteSecurityByTicker[]: Get security by ticker: {}", ticker);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, securityTableName)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next()){
                log.debug("deleteSecurityByTicker[]: Delete security by ticker: {}", ticker);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, securityTableName)
                        .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker)));
                deleteAllSecurityHistories(ticker);
                log.debug("deleteSecurityByTicker[]: Delete {}'s SecurityHistory table", ticker);
                deleteAllSecurityHistories(ticker);
                connection.commit();
                MongoHistory.save(DELETE, JDBC, resultSetToSecurityFunction.apply(rs));
                return resultSetToSecurityFunction.apply(rs);
            }
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteSecurityByTicker had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public <T extends Security> Result<T> deleteAllSecurities(String securityTableName, Supplier<Result<T>> getSecuritiesFunction){
        log.info("Starting DataProviderJDBC deleteAllSecurities[65]");
        try {
            log.info("deleteAllSecurities[]: securityTableName - {}", securityTableName);
            log.debug("deleteAllSecurities[]: get all stocks");
            Result<T> result = getSecuritiesFunction.get();
            if (result.getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllSecurities[]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("deleteAllSecurities[]: Delete all stocks");
            int count  = connection.prepareStatement(String.format(SQL_DELETE_FROM, securityTableName)).executeUpdate();
            log.info("deleteAllSecurities[]: Number of delete stocks: {}" , count);
            log.debug("deleteAllSecurities[]: Delete all histories");
            deleteAllSecurityHistories(result.getBody().stream().map(Security::getTicker).toList());
            connection.commit();
            MongoHistory.save(DELETE, JDBC, result.getBody());
            return new Result<>(result.getStatus(), String.format("Number of delete securities: %d", count), result.getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllSecurities had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Optional<T> getSecurityByTicker(String ticker,String securityTableName ,
                                                                Function<ResultSet, Optional<T>> resultSetToSecurityFunction){
        log.info("Starting DataProviderJDBC getSecurityByTicker[]");
        try {
            log.info("getSecurityByTicker[]: ticker - {}, securityTableName - {}", ticker, securityTableName);
            log.debug("getSecurityByTicker[]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("getSecurityByTicker[]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, securityTableName)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next())
                return resultSetToSecurityFunction.apply(rs);
        } catch (Exception e){
            log.error("Function DataProvider JDBC getSecurityByTicker had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
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

    private String sqlUpdateBond(Bond bond){
        return String.format(SQL_UPDATE, BOND_TABLE_NAME)
                .concat(String.format(Locale.ROOT,SQL_SET_BOND , bond.getName(), bond.getShortName(),
                        bond.getLatName(), bond.getNominal(), bond.getNominalValue(),
                        bond.getIssueDate(), bond.getIsin(), bond.getIssueSize(),
                        bond.getMarketType().toString(), bond.getType().toString(),
                        bond.getMatDate(), bond.getCoupon(), bond.getDayToRedemption()))
                .concat(SQL_WHERE + String.format(SQL_SECURITY_TICKER, bond.getTicker()));
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
        return String.format(Locale.ROOT,SQL_BOND_VALUES, bond.getTicker(), bond.getName(),
                bond.getShortName(), bond.getLatName(),
                bond.getNominal(), bond.getNominalValue(),
                bond.getIssueDate(), bond.getIsin(),
                bond.getIssueSize(), bond.getMarketType().toString(),
                bond.getType(), bond.getMatDate(),
                bond.getCoupon(), bond.getDayToRedemption());
    }

    private Optional<Stock> resultSetToStock(ResultSet rs) {
        log.info("Starting DataProviderJDBC resultSetToStock[]");
        try {
            log.info("resultSetToStock[]: ResultSet - {}", rs);
            log.debug("resultSetToStock[]: Get stock from resultSet[]");
            return Optional.of(new StockBuilder()
                    .withTicker(rs.getString(SECURITY_COLUMN_TICKER))
                    .withName(rs.getString(SECURITY_COLUMN_NAME))
                    .withShortName(rs.getString(SECURITY_COLUMN_SHORTNAME))
                    .withLatName(rs.getString(SECURITY_COLUMN_LATNAME))
                    .withNominal(rs.getDouble(SECURITY_COLUMN_NOMINAL))
                    .withNominalValue(rs.getString(SECURITY_COLUMN_NOMINALVALUE))
                    .withIssueDate(rs.getString(SECURITY_COLUMN_ISSUEDATE))
                    .withIsin(rs.getString(SECURITY_COLUMN_ISIN))
                    .withIssueSize(rs.getLong(SECURITY_COLUMN_ISSUESIZE))
                    .withSecurityHistory(getSecurityHistoryByDate(rs.getString(SECURITY_COLUMN_TICKER)))
                    .withType(Stock.StockType.valueOf(rs.getString(STOCK_COLUMN_TYPE)))
                    .withDividendSum(rs.getDouble(STOCK_COLUMN_DIVIDENDSUM))
                    .withCapitalization(rs.getDouble(STOCK_COLUMN_CAPITALIZATION))
                    .build());
        }catch (Exception e){
            log.error("Function DataProviderJDBC resultSetToStock had failed: {}", e.getMessage());
        }
        return Optional.empty();

    }

    private Optional<Bond> resultSetToBond(ResultSet rs) {
        log.info("Starting DataProviderJDBC resultSetToBond[]");
        try {
            log.info("resultSetToBond[]: ResultSet - {}", rs);
            log.debug("resultSetToBond[]: Get stock from resultSet[]");
            return Optional.of(new BondBuilder()
                    .withTicker(rs.getString(SECURITY_COLUMN_TICKER))
                    .withName(rs.getString(SECURITY_COLUMN_NAME))
                    .withShortName(rs.getString(SECURITY_COLUMN_SHORTNAME))
                    .withLatName(rs.getString(SECURITY_COLUMN_LATNAME))
                    .withNominal(rs.getDouble(SECURITY_COLUMN_NOMINAL))
                    .withNominalValue(rs.getString(SECURITY_COLUMN_NOMINALVALUE))
                    .withIssueDate(rs.getString(SECURITY_COLUMN_ISSUEDATE))
                    .withIsin(rs.getString(SECURITY_COLUMN_ISIN))
                    .withIssueSize(rs.getLong(SECURITY_COLUMN_ISSUESIZE))
                    .withSecurityHistory(getSecurityHistoryByDate(rs.getString(SECURITY_COLUMN_TICKER)))
                    .withCoupon(rs.getDouble(BOND_COLUMN_COUPON))
                    .withDayToRedemption(rs.getInt(BOND_COLUMN_DAYTOREDEMPTION))
                    .withMatDate(rs.getString(BOND_COLUMN_MATDATE))
                    .withType(Bond.BondType.valueOf(rs.getString(BOND_COLUMN_TYPE)))
                    .build());
        }catch (Exception e){
            log.error("Function DataProviderJDBC resultSetToBond had failed: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<Stock> getStocks() {
        return getSecurities(STOCK_TABLE_NAME, this::resultSetToStock);
    }


    @Override
    public Result<Stock> appendStocks(List<Stock> stocks) {
        return appendSecurity(stocks, STOCK_TABLE_NAME, SQL_STOCK_COLUMNS, Stock.class, this::setStockValues, this::getStocks);
    }


    @Override
    public Result<Stock> updateStocks(List<Stock> stocks) {
        return updateSecurities(stocks, STOCK_TABLE_NAME, this::sqlUpdateStock);
    }

    @Override
    public Optional<Stock> deleteStockByTicker(String ticker) {
        return deleteSecurityByTicker(ticker, STOCK_TABLE_NAME, this::resultSetToStock);
    }


    @Override
    public Result<Stock> deleteAllStocks() {
        return deleteAllSecurities(STOCK_TABLE_NAME, this::getStocks);
    }


    @Override
    public Optional<Stock> getStockByTicker(String ticker){
        return getSecurityByTicker(ticker, STOCK_TABLE_NAME, this::resultSetToStock);
    }

    @Override
    public Result<Bond> getBonds() {
        return getSecurities(BOND_TABLE_NAME, this::resultSetToBond);
    }


    @Override
    public Result<Bond> appendBonds(List<Bond> bonds) {
        return appendSecurity(bonds, BOND_TABLE_NAME, SQL_BOND_COLUMNS, Bond.class, this::setBondValues, this::getBonds);
    }

    @Override
    public Result<Bond> updateBonds(List<Bond> bonds) {
        return updateSecurities(bonds, BOND_TABLE_NAME, this::sqlUpdateBond);
    }

    @Override
    public Optional<Bond> deleteBondByTicker(String ticker) {
        return deleteSecurityByTicker(ticker, BOND_TABLE_NAME, this::resultSetToBond);
    }

    @Override
    public Result<Bond> deleteAllBonds() {
        return deleteAllSecurities(BOND_TABLE_NAME, this::getBonds);
    }

    @Override
    public Optional<Bond> getBondByTicker(String ticker) {
        return getSecurityByTicker(ticker, BOND_TABLE_NAME, this::resultSetToBond);
    }


    // Security history

    private SecurityHistory resultSetToSecurityHistory(ResultSet rs) throws SQLException {
        return new SecurityHistoryBuilder().withDate(rs.getString(SECURITY_HISTORY_COLUMN_DATE))
                .withTicker(rs.getString(SECURITY_HISTORY_COLUMN_TICKER))
                .withAveragePerDay(rs.getDouble(SECURITY_HISTORY_COLUMN_AVERAGEPERDAY))
                .withOpenPrice(rs.getDouble(SECURITY_HISTORY_COLUMN_OPENPRICE))
                .withClosePrice(rs.getDouble(SECURITY_HISTORY_COLUMN_CLOSEPRICE))
                .withVolume(rs.getLong(SECURITY_HISTORY_COLUMN_VOLUME))
                .build();
    }

    private String setSecurityHistoryValues(SecurityHistory securityHistory){
        return String.format(Locale.ROOT,SQL_SECURITY_HISTORY_VALUES, securityHistory.getDate(),
                securityHistory.getTicker(), securityHistory.getAveragePerDay(),
                securityHistory.getOpenPrice(), securityHistory.getClosePrice(),
                securityHistory.getVolume());
    }

    private String sqlUpdateSecurityHistory(SecurityHistory securityHistory, String ticker){
        return String.format(SQL_UPDATE, ticker.toUpperCase())
                .concat(String.format(Locale.ROOT,SQL_SET_SECURITY_HISTORY, securityHistory.getTicker(),
                        securityHistory.getAveragePerDay(), securityHistory.getOpenPrice(),
                        securityHistory.getClosePrice(), securityHistory.getVolume()));
    }

    private void creatingHistoryTable(String tableName) throws Exception {
        log.info("Starting DataProviderJDBC creatingHistoryTable[]");
        creatingTable(tableName, SECURITY_HISTORY_PATH, ru.sfedu.Constants.SQL_SECURITY_HISTORY_COLUMNS);
    }

    /**
     * Method to append security histories to database, for each ticker method creates a separate table
     * @param securityHistories - list of histories to append
     * @param ticker - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of histories that haven't been appended
     */
    public Result<SecurityHistory> appendSecurityHistory(List<SecurityHistory> securityHistories, String ticker) {
        log.info("Starting DataProviderJDBC appendSecurityHistory[]");
        try {
            log.info("appendSecurityHistory[]: securityHistories - {}, ticker - {}", securityHistories, ticker);
            Validator.isValidSecurityHistory(securityHistories, ticker);
            log.debug("appendSecurityHistory[]: Create table");
            creatingHistoryTable(ticker);
            log.debug("appendSecurityHistory[]: Connect to db");
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("appendSecurityHistory[]: Get all date");
            List<String> allDate = getDate(getSecurityHistories(ticker).getBody());
            List<SecurityHistory> response = new ArrayList<>(securityHistories.stream().filter(x -> allDate.contains(x.getDate())).toList());
            log.debug("appendSecurityHistory[]: Append security histories");
            for (SecurityHistory secHis : securityHistories.stream().filter(x -> !allDate.contains(x.getDate())).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,ticker)
                        .concat(setSecurityHistoryValues(secHis)));
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "SecurityHistories have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of SecurityHistories that haven't been appended: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurityHistory had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Method to get security history by ticker
     * @param ticker - Security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security's history
     */
    public Result<SecurityHistory> getSecurityHistories(String ticker){
        log.info("Starting DataProviderJDBC getSecurityHistory[]");
        List<SecurityHistory> securityHistories = new ArrayList<>();
        try {
            log.info("getSecurityHistories[]: ticker - {}", ticker);
            log.debug("getSecurityHistory[]: Connect to history db");
            Connection connection = getSecurityHistoryDBConnection(ticker.toUpperCase());
            log.debug("getSecurityHistory[]: Get security histories from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, ticker.toUpperCase()));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next())
                securityHistories.add(resultSetToSecurityHistory(rs));
            return new Result<>(SUCCESS, String.format("Number of histories in file: %d", securityHistories.size()), securityHistories);
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurityHistory had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), securityHistories);
        }
    }


    /**
     * Method to get Security history by date and ticker
     * @param ticker - Security's ticker
     * @param date - date that was required
     * @return SecurityHistory with special date that consists information or empty SecurityHistory that consists only date and ticker if it won't find
     */
    public SecurityHistory getSecurityHistoryByDate(String ticker, String date) {
        log.info("Starting DataProviderJDBC getSecurityHistoryByDate[]");
        try {
            log.info("getSecurityHistoryByDate[]: ticker - {}, date - {}", ticker, date);
            log.debug("getSecurityHistoryByDate[]: Connect to db");
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("getSecurityHistoryByDate[]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, ticker.toUpperCase())
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_HISTORY_DATE, date))).executeQuery();
            if (rs.next())
                return resultSetToSecurityHistory(rs);
        }catch (Exception e){
            log.error("Function DataProviderJDBC getSecurityHistoryByDate had failed: {}", e.getMessage());
        }
        return new SecurityHistoryBuilder().empty(date, ticker);
    }

    /**
     * Method to get today's Security history by ticker
     * @param ticker - Security's ticker
     * @return today's Security history. If Security history wasn't found in database method will return empty history
     */
    public SecurityHistory getSecurityHistoryByDate(String ticker) {
        return getSecurityHistoryByDate(ticker, DATE);
    }

    /**
     * Method to delete security's history database by his ticker
     * @param ticker - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security history that was in database
     */
    public Result<SecurityHistory> deleteAllSecurityHistories(String ticker) {
        log.info("Starting DataProviderJDBC dropHistoryTable[]");
        try {
            log.info("deleteAllSecurityHistories[]: {}", ticker);
            log.debug("deleteAllSecurityHistories[]: Get data from table");
            Result<SecurityHistory> securityHistoryResult = getSecurityHistories(ticker);
            if (securityHistoryResult.getStatus().equals(FAIL))
                throw new Exception(String.format("Database %s wasn't found", ticker));
            log.debug("deleteAllSecurityHistories[]: Getting connection");
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("deleteAllSecurityHistories[]: Dropping {} table", ticker);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, ticker.toUpperCase()));
            preparedStatement.executeUpdate();
            connection.commit();
            return new Result<>(SUCCESS, String.format("Num of deleted history: %d", securityHistoryResult.getBody().size()), securityHistoryResult.getBody());
        } catch (Exception e){
            log.error("Function DataProvider deleteAllSecurityHistories had failed: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Method to delete security's history database by ticker
     * @param tickerList - list of security's ticker histories that need to be deleted
     */
    public void deleteAllSecurityHistories(List<String> tickerList){
        tickerList.forEach(this::deleteAllSecurityHistories);
    }

    /**
     * Method to append or update security's history by one date
     * @param securityHistory - Security history
     * @param ticker - security's ticker
     * @return boolean - result of the work
     */
    public boolean appendOrUpdate(SecurityHistory securityHistory, String ticker){
        log.info("Starting DataProviderJDBC appendOrUpdate[]");
        try {
            log.info("appendOrUpdate[]: securityHistory - {}, ticker - {}", securityHistory, ticker);
            Validator.isValidSecurityHistory(securityHistory, ticker);
            log.debug("appendSecurityHistory[]: Create table");
            creatingHistoryTable(ticker);
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("appendSecurityHistory[]: append or update security history");
            if( connection.createStatement().executeUpdate(sqlUpdateSecurityHistory(securityHistory, ticker)) == 0)
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,ticker)
                        .concat(setSecurityHistoryValues(securityHistory)));
            connection.commit();
            return true;
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendOrUpdate had failed: {}", e.getMessage());
        }
        return false;
    }

}
