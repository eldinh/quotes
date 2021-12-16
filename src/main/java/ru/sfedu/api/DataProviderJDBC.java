package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.builder.*;
import ru.sfedu.model.*;
import ru.sfedu.utils.ValidEntityListValidator;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.sfedu.Constants.*;
import static ru.sfedu.model.CommandType.*;
import static ru.sfedu.model.RepositoryType.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderJDBC implements DateProvider {
    private final Logger log = LogManager.getLogger(DataProviderJDBC.class.getName());
    final String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

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
                .withMarketType(MarketType.valueOf(rs.getString(SECURITY_COLUMN_MARKETTYPE)))
                .withSecurityHistory(getSecurityHistoryByDate(rs.getString(SECURITY_COLUMN_TICKER)));
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

    private SecurityHistory resultSetToSecurityHistory(ResultSet rs) throws SQLException {
        return new SecurityHistoryBuilder().withDate(rs.getString(SECURITY_HISTORY_COLUMN_DATE))
                .withTicker(rs.getString(SECURITY_HISTORY_COLUMN_TICKER))
                .withAveragePerDay(rs.getDouble(SECURITY_HISTORY_COLUMN_AVERAGEPERDAY))
                .withOpenPrice(rs.getDouble(SECURITY_HISTORY_COLUMN_OPENPRICE))
                .withClosePrice(rs.getDouble(SECURITY_HISTORY_COLUMN_CLOSEPRICE))
                .withVolume(rs.getLong(SECURITY_HISTORY_COLUMN_VOLUME))
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
        return String.format(Locale.ROOT,SQL_BOND_VALUES, bond.getTicker(), bond.getName(),
                bond.getShortName(), bond.getLatName(),
                bond.getNominal(), bond.getNominalValue(),
                bond.getIssueDate(), bond.getIsin(),
                bond.getIssueSize(), bond.getMarketType().toString(),
                bond.getType(), bond.getMatDate(),
                bond.getCoupon(), bond.getDayToRedemption());
    }

    private String setSecurityHistoryValues(SecurityHistory securityHistory){
        return String.format(Locale.ROOT,SQL_SECURITY_HISTORY_VALUES, securityHistory.getDate(),
                securityHistory.getTicker(), securityHistory.getAveragePerDay(),
                securityHistory.getOpenPrice(), securityHistory.getClosePrice(),
                securityHistory.getVolume());
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

    private String sqlUpdateBond(Bond bond){
        return String.format(SQL_UPDATE, BOND_TABLE_NAME)
                .concat(String.format(Locale.ROOT,SQL_SET_BOND , bond.getName(), bond.getShortName(),
                        bond.getLatName(), bond.getNominal(), bond.getNominalValue(),
                        bond.getIssueDate(), bond.getIsin(), bond.getIssueSize(),
                        bond.getMarketType().toString(), bond.getType().toString(),
                        bond.getMatDate(), bond.getCoupon(), bond.getDayToRedemption()))
                .concat(SQL_WHERE + String.format(SQL_SECURITY_TICKER, bond.getTicker()));
    }

    private String sqlUpdateSecurityHistory(SecurityHistory securityHistory, String ticker){
        return String.format(SQL_UPDATE, ticker.toUpperCase())
                .concat(String.format(Locale.ROOT,SQL_SET_SECURITY_HISTORY, securityHistory.getTicker(),
                        securityHistory.getAveragePerDay(), securityHistory.getOpenPrice(),
                        securityHistory.getClosePrice(), securityHistory.getVolume()));
    }

    public void dropTable(String dbName) {
        log.info("Starting DataProviderJDBC dropTable");
        try {
            log.info("dropTable: {}", dbName);
            log.debug("dropTable: Getting connection");
            Connection connection = getDbConnection(dbName);
            log.debug("dropTable: Dropping {} table", dbName);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, dbName));
            log.info(preparedStatement.executeUpdate());
        } catch (Exception e){
            log.error("Function DataProvider dropTable had failed: {}", e.getMessage());
        }

    }

    public Connection getDbConnection(String dbName, String extraPath)
            throws Exception {
        log.info("Starting DataProviderJDBC getDbConnection[0]");
        try {
            log.info("getDbConnection[1]: {}, {}", dbName, extraPath);
            String connectionString = String.format(getConfigurationEntry(JDBC_CONNECTION), getConfigurationEntry(DB_PATH).concat(extraPath),
                    dbName.toUpperCase());
            Class.forName(getConfigurationEntry(H2_DRIVER));
            log.debug("getDbConnection[1]: connect to DB");
            return DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                    getConfigurationEntry(DB_PASSWORD));

        } catch (Exception e){
            log.error("Function DataProviderJDBC getDbConnection had failed[2]");
            throw new Exception(e);
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
            log.info("creatingTable[4]: Connect to db");
            Statement stmt = conn.createStatement();
            log.debug("creatingTable[5]: Create table if it doesn't exist");
            stmt.executeUpdate(String.format(SQL_CREATE_TABLE, tableName.toUpperCase()).concat(column));
            log.debug("creatingTable[6]: Closing statement");
            stmt.close();
            log.debug("creatingTable[7]: Closing connection");
            conn.close();
        } catch (Exception e) {
            log.error("Function DataProviderJDBC creatingTable had failed[8]");
            throw new Exception(e);
        }
    }
    private void creatingHistoryTable(String tableName, String column) throws Exception {
        log.info("Starting DataProviderJDBC creatingHistoryTable[9]");
        creatingTable(tableName, SECURITY_HISTORY_PATH, column);
    }

    private void creatingTable(Class<?> objClass, String column) throws Exception {
        creatingTable(objClass.getSimpleName().toUpperCase(), "",column);
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
    public Optional<User> deleteUserById(long id) {
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
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteUserById had failed[31]");
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
            log.error("Function DataProviderJDBC deleteAllUsers had failed[37]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }

    }

    @Override
    public Optional<User> getUserById(long id) {
        log.info("Starting DataProviderJDBC getUSerById[38]");
        try {
            log.debug("getUserById[39]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("getUserById[40]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id))).executeQuery();
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

    @Override
    public Result<Stock> getStocks() {
        log.info("Starting DataProviderJDBC getStocks[42]");
        List<Stock> stocks = new ArrayList<>();
        try {
            log.debug("getStocks[43]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("getStocks[44]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, STOCK_TABLE_NAME));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next())
                stocks.add(resultSetToStock(rs));
            log.debug("getStocks[]: Set history to Stocks");
            setHistory(stocks);
            return new Result<>(SUCCESS, String.format("Number of stocks in file: %d", stocks.size()), stocks);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getStocks had failed[45]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Stock> appendStocks(List<Stock> stocks) {
        log.info("Starting DataProviderJDBC appendStocks[46]");
        try {
            log.info("appendStocks[47]: {}, type: {}", Arrays.toString(stocks.toArray()), stocks.getClass().getName());
            ValidEntityListValidator.isValidSecurity(stocks);
            log.debug("appendStocks[48]: Create table");
            creatingTable(Stock.class, SQL_STOCK_COLUMNS);
            log.debug("appendStocks[49]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("appendStocks[50]: Get all ticker");
            List<String> allTicker = getSecuritiesTicker(getStocks().getBody());
            List<Stock> response = new ArrayList<>(stocks.stream().filter(x -> allTicker.contains(x.getTicker())).toList());
            log.debug("appendStocks[51]: append stocks with ticker");
            for (Stock stock : stocks.stream().filter(x ->!allTicker.contains(x.getTicker())).toList()) {
                connection.createStatement().executeUpdate(String.format(SQL_INSERT, STOCK_TABLE_NAME)
                        .concat(setStockValues(stock)));
                appendOrUpdate(stock.getHistory(), stock.getTicker());
            }
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendStocks had failed[52]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Stock> updateStocks(List<Stock> stocks) {
        log.info("Starting DataProviderJDBC updateStocks[53]");
        try {
            log.info("updateStocks[54]: {}, type: {}", Arrays.toString(stocks.toArray()), stocks.getClass().getName());
            ValidEntityListValidator.isValidSecurity(stocks);
            List<Stock> response = new ArrayList<>();
            List<Stock> stockToUpdate = new ArrayList<>(stocks);
            log.debug("updateStocks[55]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("updateStocks[56]: Update users");
            for (int i = 0; i < stockToUpdate.size(); i ++)
                if( connection.createStatement().executeUpdate(sqlUpdateStock(stockToUpdate.get(i))) == 0)
                    response.add(stockToUpdate.remove(i));
                else
                    appendOrUpdate(stockToUpdate.get(i).getHistory(), stockToUpdate.get(i).getTicker());
            connection.commit();
            MongoHistory.save(UPDATE, JDBC, stockToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, String.format("Securities have been updated successfully, number of updated users: %d", stockToUpdate.size()), response);
            return new Result<>(WARN, String.format("Number of Securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateStocks had failed[57]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Stock> deleteStockByTicker(String ticker) {
        log.info("Starting DataProviderJDBC deleteStockByTicker[58]");
        try {
            log.info("deleteStockByTicker[59]: {}",ticker);
            log.debug("deleteStockByTicker[60]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("deleteStockByTicker[61]: Get stock by ticker: {}", ticker);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, STOCK_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next()){
                log.debug("deleteStockByTicker[62]: Delete stock by ticker: {}", ticker);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, STOCK_TABLE_NAME)
                        .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker)));
                deleteAllSecurityHistories(ticker);
                connection.commit();
                MongoHistory.save(DELETE, JDBC, resultSetToStock(rs));
                log.debug("deleteStockByTicker[63]: Delete {}'s SecurityHistory table", ticker);
                deleteAllSecurityHistories(ticker);
                return Optional.of(resultSetToStock(rs));
            }
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteStockByTicker had failed[64]");
        }
        return Optional.empty();
    }

    @Override
    public Result<Stock> deleteAllStocks() {
        log.info("Starting DataProviderJDBC deleteAllStocks[65]");
        try {
            log.debug("deleteAllStocks[66]: get all stocks");
            Result<Stock> result = getStocks();
            if (result.getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllStocks[67]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("deleteAllStocks[68]: Delete all stocks");
            int count  = connection.prepareStatement(String.format(SQL_DELETE_FROM, STOCK_TABLE_NAME)).executeUpdate();
            log.info("deleteAllStocks[68]: Number of delete stocks: {}" , count);
            log.debug("deleteAllStocks[69]: Delete all histories");
            deleteAllSecurityHistories(result.getBody().stream().map(Security::getTicker).toList());
            connection.commit();
            MongoHistory.save(DELETE, JDBC, result.getBody());
            return new Result<>(result.getStatus(), String.format("Number of delete stocks: %d", count), result.getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllStocks had failed[70]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Stock> getStockByTicker(String ticker){
        log.info("Starting DataProviderJDBC getStockByTicker[71]");
        try {
            log.debug("getStockByTicker[72]: Connect to db");
            Connection connection = getDbConnection(STOCK_TABLE_NAME);
            log.debug("getStockByTicker[73]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, STOCK_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next())
                return Optional.of(resultSetToStock(rs));
        } catch (Exception e){
            log.error("Function DataProvider JDBC getStockByTicker had failed[74]");
        }
        return Optional.empty();
    }

    @Override
    public Result<Bond> getBonds() {
        log.info("Starting DataProviderJDBC getBonds[75]");
        List<Bond> bonds = new ArrayList<>();
        try {
            log.debug("getBonds[76]: Connect to db");
            Connection connection = getDbConnection(BOND_TABLE_NAME);
            log.debug("getBonds[77]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, BOND_TABLE_NAME));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next())
                bonds.add(resultSetToBond(rs));
            log.debug("getBonds[78]: Set history to Stocks");
            setHistory(bonds);
            return new Result<>(SUCCESS, String.format("Number of bonds in file: %d", bonds.size()), bonds);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getBonds had failed[79]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Bond> appendBonds(List<Bond> bonds) {
        log.info("Starting DataProviderJDBC appendBonds[80]");
        try {
            log.info("appendBonds[81]: {}, type: {}", Arrays.toString(bonds.toArray()), bonds.getClass().getName());
            ValidEntityListValidator.isValidSecurity(bonds);
            log.debug("appendBonds[82]: Create table");
            creatingTable(Bond.class, SQL_BOND_COLUMNS);
            log.debug("appendBonds[83]: Connect to db");
            Connection connection = getDbConnection(BOND_TABLE_NAME);
            log.debug("appendBonds[84]: Get all ticker");
            List<String> allTicker = getSecuritiesTicker(getBonds().getBody());
            List<Bond> response = new ArrayList<>(bonds.stream().filter(x -> allTicker.contains(x.getTicker())).toList());
            log.debug("appendBonds[85]: append bonds with ticker");
            for (Bond bond : bonds.stream().filter(x ->!allTicker.contains(x.getTicker())).toList()) {
                connection.createStatement().executeUpdate(String.format(SQL_INSERT, BOND_TABLE_NAME)
                        .concat(setBondValues(bond)));
                appendOrUpdate(bond.getHistory(), bond.getTicker());
            }
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendBonds had failed[86]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Bond> updateBonds(List<Bond> bonds) {
        log.info("Starting DataProviderJDBC updateBonds[87]");
        try {
            log.info("updateBonds[88]: {}, type: {}", Arrays.toString(bonds.toArray()), bonds.getClass().getName());
            ValidEntityListValidator.isValidSecurity(bonds);
            List<Bond> response = new ArrayList<>();
            List<Bond> bondToUpdate = new ArrayList<>(bonds);
            log.debug("updateBonds[89]: Connect to db");
            Connection connection = getDbConnection(BOND_TABLE_NAME);
            log.debug("updateBonds[90]: Update users");
            for (int i = 0; i < bondToUpdate.size(); i ++)
                if( connection.createStatement().executeUpdate(sqlUpdateBond(bondToUpdate.get(i))) == 0)
                    response.add(bondToUpdate.remove(i));
                else
                    appendOrUpdate(bondToUpdate.get(i).getHistory(), bondToUpdate.get(i).getTicker());
            connection.commit();
            MongoHistory.save(UPDATE, JDBC, bondToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, String.format("Securities have been updated successfully, number of updated users: %d", bondToUpdate.size()), response);
            return new Result<>(WARN, String.format("Number of Securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateBonds had failed[91]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Bond> deleteBondByTicker(String ticker) {
        log.info("Starting DataProviderJDBC deleteBondByTicker[92]");
        try {
            log.info("deleteBondByTicker[93]: {}",ticker);
            log.debug("deleteBondByTicker[94]: Connect to db");
            Connection connection = getDbConnection(BOND_TABLE_NAME);
            log.debug("deleteBondByTicker[95]: Get bond by ticker: {}", ticker);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, BOND_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next()){
                log.debug("deleteBondByTicker[96]: Delete bond by ticker: {}", ticker);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, BOND_TABLE_NAME)
                        .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker)));
                deleteAllSecurityHistories(ticker);
                MongoHistory.save(DELETE, JDBC, resultSetToBond(rs));
                log.debug("deleteBondByTicker[97]: Delete {}'s SecurityHistory table", ticker);
                deleteAllSecurityHistories(ticker);
                connection.commit();
                return Optional.of(resultSetToBond(rs));
            }
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteBondByTicker had failed[98]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<Bond> deleteAllBonds() {
        log.info("Starting DataProviderJDBC deleteAllBonds[99]");
        try {
            log.debug("deleteAllBonds[100]: get all bonds");
            Result<Bond> result = getBonds();
            if (result.getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllBonds[101]: Connect to db");
            Connection connection = getDbConnection(BOND_TABLE_NAME);
            log.debug("deleteAllBonds[102]: Delete all bonds");
            int count  = connection.prepareStatement(String.format(SQL_DELETE_FROM, BOND_TABLE_NAME)).executeUpdate();
            log.info("deleteAllBonds[68]: Number of delete bonds: {}" , count);
            log.debug("deleteAllBonds[103]: Delete all histories");
            deleteAllSecurityHistories(result.getBody().stream().map(Security::getTicker).toList());
            connection.commit();
            MongoHistory.save(DELETE, JDBC, result.getBody());
            return new Result<>(result.getStatus(), String.format("Number of delete bonds: %d", count), result.getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllBonds had failed[104]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Bond> getBondByTicker(String ticker) {
        log.info("Starting DataProviderJDBC getBondByTicker[105]");
        try {
            log.debug("getBondByTicker[106]: Connect to db");
            Connection connection = getDbConnection(BOND_TABLE_NAME);
            log.debug("getBondByTicker[107]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, BOND_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next())
                return Optional.of(resultSetToBond(rs));
        } catch (Exception e){
            log.error("Function DataProvider JDBC getBondByTicker had failed[108]");
        }
        return Optional.empty();
    }




    // Security history

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
            ValidEntityListValidator.isValidSecurityHistory(securityHistories, ticker);
            log.debug("appendSecurityHistory[]: Create table");
            creatingHistoryTable(ticker,SQL_SECURITY_HISTORY_COLUMNS);
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
        return getSecurityHistoryByDate(ticker, date);
    }

    /**
     * Method to delete security's history database by his ticker
     * @param dbName - security's ticker
     * @return Result<SecurityHistory> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of security history that was in database
     */
    public Result<SecurityHistory> deleteAllSecurityHistories(String dbName) {
        log.info("Starting DataProviderJDBC dropHistoryTable[]");
        try {
            log.info("deleteAllSecurityHistories[]: {}", dbName);
            log.debug("deleteAllSecurityHistories[]: Get data from table");
            Result<SecurityHistory> securityHistoryResult = getSecurityHistories(dbName);
            if (securityHistoryResult.getStatus().equals(FAIL))
                throw new Exception(String.format("Database %s wasn't found", dbName));
            log.debug("deleteAllSecurityHistories[]: Getting connection");
            Connection connection = getSecurityHistoryDBConnection(dbName);
            log.debug("deleteAllSecurityHistories[]: Dropping {} table", dbName);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, dbName.toUpperCase()));
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
            ValidEntityListValidator.isValidSecurityHistory(securityHistory, ticker);
            log.debug("appendSecurityHistory[]: Create table");
            creatingHistoryTable(ticker,SQL_SECURITY_HISTORY_COLUMNS);
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("appendSecurityHistory[]: append or update security history");
            if( connection.createStatement().executeUpdate(sqlUpdateSecurityHistory(securityHistory, ticker)) == 0)
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,ticker)
                        .concat(setSecurityHistoryValues(securityHistory)));
            return true;
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendOrUpdate had failed: {}", e.getMessage());
        }
        return false;
    }

}
