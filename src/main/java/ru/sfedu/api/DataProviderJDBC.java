package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.model.*;
import ru.sfedu.utils.Validator;
import ru.sfedu.model.BondType;

import java.sql.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.function.Function;

import static ru.sfedu.Constants.*;
import static ru.sfedu.model.CommandType.*;
import static ru.sfedu.model.RepositoryType.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;


public class DataProviderJDBC implements DataProvider {
    private final Logger log = LogManager.getLogger(DataProviderJDBC.class.getName());


    /**
     * Method for connecting to db in local server to work with it
     * @param dbName - name of db
     * @param extraPath - extra path
     * @return - connection's object to work with db
     * @throws Exception - Exception
     */
    private Connection getDbConnection(String dbName, String extraPath)throws Exception {
        log.info("Starting DataProviderJDBC getDbConnection[0]");
        try {
            log.info("getDbConnection[1]: dbName - {}, extraPath - {}", dbName, extraPath);
            String connectionString = String.format(getConfigurationEntry(JDBC_CONNECTION), getConfigurationEntry(DB_PATH).concat(extraPath),
                    dbName.toUpperCase());
            Class.forName(getConfigurationEntry(H2_DRIVER));
            log.debug("getDbConnection[2]: Connecting to DB");
            return DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                    getConfigurationEntry(DB_PASSWORD));

        } catch (Exception e){
            log.error("Function DataProviderJDBC getDbConnection had failed[3]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Method for dropping table by name
     * @param dbName - db name
     */
    public void dropTable(String dbName) {
        log.info("Starting DataProviderJDBC dropTable[0]");
        try {
            log.info("dropTable[1]: dbName - {}", dbName);
            log.debug("dropTable[2]: Getting connection");
            Connection connection = getDbConnection(dbName);
            log.debug("dropTable[3]: Dropping {} table", dbName);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, dbName));
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (Exception e){
            log.error("Function DataProvider dropTable had failed[4]: {}", e.getMessage());
        }
    }

    /**
     * Method for connecting to db to work with it
     * @param dbName - name of db
     * @return - connection's object to work with db
     * @throws Exception - Exception
     */
    private Connection getDbConnection(String dbName)throws Exception {
        return getDbConnection(dbName, "");
    }

    /**
     * Method for creating table in local server
     * @param tableName - table name
     * @param direction - extra path
     * @param column - table's column
     * @throws Exception - Exception
     */
    private void creatingTable(String tableName,String direction, String column) throws Exception {
        log.info("Starting DataProviderJDBC creatingTable[0]");
        try {
            Connection conn = getDbConnection(tableName, direction);
            log.info("creatingTable[1]: tableName - {}, direction - {}, column - {}", tableName, direction, column);

            log.debug("creatingTable[2]: Connect to db");
            Statement stmt = conn.createStatement();
            log.debug("creatingTable[3]: Create table if it doesn't exist");
            stmt.executeUpdate(String.format(SQL_CREATE_TABLE, tableName.toUpperCase()).concat(column));
            log.debug("creatingTable[4]: Closing statement");
            stmt.close();
            log.debug("creatingTable[5]: Closing connection");
            conn.close();
        } catch (Exception e) {
            log.error("Function DataProviderJDBC creatingTable had failed[6]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Method for creating table in local server
     * @param objClass - table name
     * @param column - table's column
     * @throws Exception - Exception
     */
    private void creatingTable(Class<?> objClass, String column) throws Exception {
        creatingTable(objClass.getSimpleName().toUpperCase(), "",column);
    }


    // market

    /**
     * Method for converting result set to market object
     * @param rs - result set to convert
     * @return Market object
     * @throws SQLException - SQLException
     */
    private Market resultSetToMarket(ResultSet rs) throws SQLException {
        MarketType marketType = MarketType.valueOf(rs.getString(MARKET_COLUMN_MARKET_TYPE));
        return new Market(marketType, getSecurityList(marketType));
    }

    /**
     * Method for getting securities from market
     * @param marketType - market type
     * @return list of securities from market
     */
    private List<Security> getSecurityList(MarketType marketType){
        List<Security> securityList = new ArrayList<>();
        switch (marketType){
            case SHARES -> securityList.addAll(getStocks().getBody());
            case BONDS -> securityList.addAll(getBonds().getBody());
        }
        return securityList;
    }

    @Override
    public boolean appendOrUpdateMarket(MarketType marketType) {
        log.info("Starting DataProviderJDBC appendOrUpdateMarket[0]");
        try {
            Validator.isValid(marketType);
            log.info("appendOrUpdateMarket[1]: marketType - {}", marketType);
            log.debug("appendOrUpdateMarket[2]: Getting all markets");
            Optional<Market> market = getMarkets().getBody().stream().filter(x -> x.getMarketType().equals(marketType)).findFirst();
            if (market.isPresent())
                return true;
            log.debug("appendOrUpdateMarket[3]: Creating table");
            creatingTable(Market.class, SQL_MARKET_COLUMNS);
            log.debug("appendOrUpdateMarket[4]: Connect to db");
            Connection connection = getDbConnection(MARKET_TABLE_NAME);
            log.debug("appendOrUpdateMarket[5]: insert into table");
            connection.createStatement().executeUpdate(String.format(SQL_INSERT,MARKET_TABLE_NAME )
                    .concat(String.format(SQL_MARKET_VALUES, marketType)));
            connection.commit();
            return true;
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendOrUpdateMarket had failed[6]: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public Result<Market> getMarkets() {
        log.info("Starting DataProviderJDBC getMarkets[0]");
        List<Market> markets = new ArrayList<>();
        try {
            log.debug("getMarkets[1]: Connect to db");
            Connection connection = getDbConnection(MARKET_TABLE_NAME);
            log.debug("getMarkets[2]: Get market from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, MARKET_TABLE_NAME));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
                markets.add(resultSetToMarket(rs));
            return new Result<>(SUCCESS, String.format("Number of markets: %d", markets.size()), markets);
        }catch (Exception e){
            log.error("Function DataProviderJDBC getMarkets had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Market> getMarket(MarketType marketType) {
        log.info("Starting DataProviderJDBC getMarket[0]");
        try {
            log.info("getMarket[1]: marketType - {}", marketType);
            log.debug("getMarket[2]: Connect to db");
            Connection connection = getDbConnection(MARKET_TABLE_NAME);
            log.debug("getMarket[3]: Get market from db");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, MARKET_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_MARKET_MARKET_TYPE, marketType))).executeQuery();
            connection.commit();
            if (rs.next())
                return Optional.of(resultSetToMarket(rs));
        }catch (Exception e){
            log.error("Function DataProviderJDBC getMarket had failed[4]: {}", e.getMessage());
        }
        return Optional.empty();
    }


    //  security

    /**
     * Method for getting security's ticker
     * @param securities - list of securities
     * @param <T> - Type of securities
     * @return List<String> - list of security's tickers
     */
    private <T extends Security> List<String> getSecuritiesTicker(List<T> securities){
        return new ArrayList<>(securities.stream().map(Security::getTicker).toList());
    }

    /**
     * Method for setting history for securities
     * @param securities - list of securities
     * @param <T> - Type of security
     */
    private <T extends Security> void setHistory(List<T> securities){
        for (T security : securities)
            security.setHistory(getSecurityHistoryByDate(security.getTicker()));
    }


    /**
     * Method for getting security's ticker
     * @param tableName - table name
     * @param resultSetToSecurityFunction - function for converting result set into pojo
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all stocks from database
     */
    public <T extends Security> Result<T> getSecurities(String tableName, Function<ResultSet, Optional<T>> resultSetToSecurityFunction){
        log.info("Starting DataProviderJDBC getSecurities[0]");
        List<T> securities = new ArrayList<>();
        try {
            log.info("getSecurities[1]: tableName - {}", tableName);
            log.debug("getSecurities[2]: Connect to db");
            Connection connection = getDbConnection(tableName);
            log.debug("getSecurities[3]: Get securities from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, tableName));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                Optional<T> sec = resultSetToSecurityFunction.apply(rs);
                if (sec.isEmpty())
                    throw new Exception("Function DataProviderJDBC resultSetToSecurityFunction had failed");
                securities.add(sec.get());
            }
            log.debug("getSecurities[4]: Set history to securities");
            setHistory(securities);
            return new Result<>(SUCCESS, String.format("Number of securities in file: %d", securities.size()), securities);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getSecurities had failed[5]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Generic method for appending security to db
     * @param securities - list of securities to append
     * @param securityTableName - table name
     * @param marketType - security's market
     * @param columns - security's database columns
     * @param securityClass - class that function work with(securities type)
     * @param setSecurityValueFunction -  function for converting pojo into sql function(string)
     * @param getSecuritiesFunction - function for getting securities from db
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities that haven't been appended
     */
    public <T extends Security> Result<T> appendSecurity(List<T> securities, String securityTableName,MarketType marketType, String columns, Class<T> securityClass,
                                                         Function<T,String> setSecurityValueFunction, Supplier<Result<T>> getSecuritiesFunction){
        log.info("Starting DataProviderJDBC appendSecurity[0]");
        try {
            log.info("appendSecurity[1]: securities - {}, securityTableName - {}, columns - {}, securityClass - {}", securities, securityTableName, columns, securityClass);
            Validator.isValidSecurity(securities);
            log.debug("appendSecurity[2]: Create table");
            creatingTable(securityClass, columns);
            log.debug("appendSecurity[3]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("appendSecurity[4]: Get all ticker");
            List<String> allTicker = getSecuritiesTicker(getSecuritiesFunction.get().getBody());
            List<T> response = new ArrayList<>(securities.stream().filter(x -> allTicker.contains(x.getTicker())).toList());
            log.debug("appendSecurity[5]: append stocks with ticker");
            for (T sec : securities.stream().filter(x ->!allTicker.contains(x.getTicker())).toList()) {
                connection.createStatement().executeUpdate(String.format(SQL_INSERT, securityTableName)
                        .concat(setSecurityValueFunction.apply(sec)));
                appendOrUpdateSecurityHistory(sec.getHistory(), sec.getTicker());
            }
            log.debug("appendSecurity[6]: Append market if it doesn't exist");
            appendOrUpdateMarket(marketType);
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurity had failed[7]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Generic method for updating securities by their id depending on their type
     * @param securities - list of securities to update
     * @param securityTableName - table name
     * @param sqlUpdateSecurityFunction - function for converting pojo into sql function to update(String)
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities that haven't been updated
     */
    public <T extends Security> Result<T> updateSecurities(List<T> securities, String securityTableName,
                                                           Function<T, String> sqlUpdateSecurityFunction){
        log.info("Starting DataProviderJDBC updateSecurities[0]");
        try {
            log.info("updateSecurities[1]: securities - {}, securityTableName - {}", securities, securityTableName);
            Validator.isValidSecurity(securities);
            List<T> response = new ArrayList<>();
            List<T> securityToUpdate = new ArrayList<>(securities);
            log.debug("updateSecurities[2]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("updateSecurities[3]: Update users");
            for (int i = 0; i < securityToUpdate.size(); i ++)
                if( connection.createStatement().executeUpdate(sqlUpdateSecurityFunction.apply(securityToUpdate.get(i))) == 0)
                    response.add(securityToUpdate.remove(i));
                else
                    appendOrUpdateSecurityHistory(securityToUpdate.get(i).getHistory(), securityToUpdate.get(i).getTicker());
            connection.commit();
            MongoHistory.save(UPDATE, JDBC, securityToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, String.format("Securities have been updated successfully, number of updated users: %d", securityToUpdate.size()), response);
            return new Result<>(WARN, String.format("Number of Securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateSecurities had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Generic method for deleting security by ticker
     * @param ticker - security's ticker
     * @param securityTableName - table name
     * @param resultSetToSecurityFunction - function for converting result set into pojo
     * @param <T> - Type of securities
     * @return - Optional<T extends Security> security that was deleted if it existed
     */
    public <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, String securityTableName,
                                                                   Function<ResultSet, Optional<T>> resultSetToSecurityFunction){
        log.info("Starting DataProviderJDBC deleteSecurityByTicker[0]");
        try {
            Validator.isValid(ticker);
            log.info("deleteSecurityByTicker[1]: ticker - {}, securityTableName - {}",ticker, securityTableName);
            log.debug("deleteSecurityByTicker[2]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("deleteSecurityByTicker[3]: Get security by ticker: {}", ticker);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, securityTableName)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next()){
                log.debug("deleteSecurityByTicker[4]: Delete security by ticker: {}", ticker);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, securityTableName)
                        .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker)));
                log.debug("deleteSecurityByTicker[5]: Delete {}'s SecurityHistory table", ticker);
                deleteAllSecurityHistories(ticker);
                connection.commit();
                MongoHistory.save(DELETE, JDBC, resultSetToSecurityFunction.apply(rs));
                return resultSetToSecurityFunction.apply(rs);
            }
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteSecurityByTicker had failed[6]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Generic method for deleting all securities
     * @param securityTableName - table name
     * @param getSecuritiesFunction -function for getting securities from db
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities that have been deleted
     */
    public <T extends Security> Result<T> deleteAllSecurities(String securityTableName, Supplier<Result<T>> getSecuritiesFunction){
        log.info("Starting DataProviderJDBC deleteAllSecurities[0]");
        try {
            log.info("deleteAllSecurities[1]: securityTableName - {}", securityTableName);
            log.debug("deleteAllSecurities[2]: get all stocks");
            Result<T> result = getSecuritiesFunction.get();
            if (result.getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllSecurities[3]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("deleteAllSecurities[4]: Delete all stocks");
            int count  = connection.prepareStatement(String.format(SQL_DELETE_FROM, securityTableName)).executeUpdate();
            log.info("deleteAllSecurities[5]: Number of delete stocks: {}" , count);
            log.debug("deleteAllSecurities[6]: Delete all histories");
            deleteAllSecurityHistories(result.getBody().stream().map(Security::getTicker).toList());
            connection.commit();
            MongoHistory.save(DELETE, JDBC, result.getBody());
            return new Result<>(result.getStatus(), String.format("Number of delete securities: %d", count), result.getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllSecurities had failed[7]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    /**
     * Generic method for getting security by ticker
     * @param ticker - security's ticker
     * @param securityTableName - table name
     * @param resultSetToSecurityFunction - function for converting result set into pojo
     * @param <T> - Type of securities
     * @return Optional<T extends Security> - Security if it exists
     */
    public <T extends Security> Optional<T> getSecurityByTicker(String ticker,String securityTableName ,
                                                                Function<ResultSet, Optional<T>> resultSetToSecurityFunction){
        log.info("Starting DataProviderJDBC getSecurityByTicker[0]");
        try {
            Validator.isValid(ticker);
            log.info("getSecurityByTicker[1]: ticker - {}, securityTableName - {}", ticker, securityTableName);
            log.debug("getSecurityByTicker[2]: Connect to db");
            Connection connection = getDbConnection(securityTableName);
            log.debug("getSecurityByTicker[3]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, securityTableName)
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_TICKER, ticker))).executeQuery();
            if (rs.next())
                return resultSetToSecurityFunction.apply(rs);
        } catch (Exception e){
            log.error("Function DataProvider JDBC getSecurityByTicker had failed[4]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Generic function for getting securities by ticker list
     * @param tickerList - ticker list
     * @param getSecuritiesFunction - function for getting securities from db
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities
     */
    public <T extends Security> Result<T> getSecuritiesByTickerList(List<String> tickerList, Supplier<Result<T>> getSecuritiesFunction){
        log.info("Starting DataProviderJDBC getSecuritiesByTickerList[0]");
        try {
            log.info("getSecuritiesByTickerList[1]: tickerList - {}", tickerList);
            log.debug("getSecuritiesByTickerList[2]: Getting all securities");
            List<T> securityList = getSecuritiesFunction.get().getBody();
            log.debug("getSecuritiesByTickerList[3]: Filtering security list");
            List<T> response = new ArrayList<>(securityList.stream().filter(x -> tickerList.contains(x.getTicker())).toList());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", response.size()), response);

        }catch (Exception e){
            log.error("Function DataProviderJDBC getSecuritiesByTickerList had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    /**
     * Method for converting stock object into sql function to update
     * @param stock - stock object
     * @return sql function as string
     */
    private String sqlUpdateStock(Stock stock){
        return String.format(SQL_UPDATE, STOCK_TABLE_NAME)
                .concat(String.format(Locale.ROOT,SQL_SET_STOCK , stock.getName(), stock.getShortName(),
                        stock.getLatName(), stock.getNominal(), stock.getNominalValue(),
                        stock.getIssueDate(), stock.getIsin(), stock.getIssueSize(),
                        stock.getMarketType().toString(), stock.getType().toString(),
                        stock.getDividendSum(), stock.getCapitalization()))
                .concat(SQL_WHERE + String.format(SQL_SECURITY_TICKER, stock.getTicker()));
    }

    /**
     * Method for converting bond object into sql function to update
     * @param bond - bond object
     * @return sql function as string
     */
    private String sqlUpdateBond(Bond bond){
        return String.format(SQL_UPDATE, BOND_TABLE_NAME)
                .concat(String.format(Locale.ROOT,SQL_SET_BOND , bond.getName(), bond.getShortName(),
                        bond.getLatName(), bond.getNominal(), bond.getNominalValue(),
                        bond.getIssueDate(), bond.getIsin(), bond.getIssueSize(),
                        bond.getMarketType().toString(), bond.getType().toString(),
                        bond.getMatDate(), bond.getCoupon(), bond.getDayToRedemption()))
                .concat(SQL_WHERE + String.format(SQL_SECURITY_TICKER, bond.getTicker()));
    }

    /**
     * Method for converting stock object into sql function
     * @param stock - stock object to convert
     * @return sql function as string
     */
    private String setStockValues(Stock stock){
        return String.format(Locale.ROOT, SQL_STOCK_VALUES, stock.getTicker(), stock.getName(),
                stock.getShortName(), stock.getLatName(),
                stock.getNominal(), stock.getNominalValue(),
                stock.getIssueDate(), stock.getIsin(),
                stock.getIssueSize(), stock.getMarketType().toString(),
                stock.getType(), stock.getDividendSum(),
                stock.getCapitalization());
    }

    /**
     * Method for converting bond object into sql function
     * @param bond - stock object to convert
     * @return sql function as string
     */
    private String setBondValues(Bond bond){
        return String.format(Locale.ROOT,SQL_BOND_VALUES, bond.getTicker(), bond.getName(),
                bond.getShortName(), bond.getLatName(),
                bond.getNominal(), bond.getNominalValue(),
                bond.getIssueDate(), bond.getIsin(),
                bond.getIssueSize(), bond.getMarketType().toString(),
                bond.getType(), bond.getMatDate(),
                bond.getCoupon(), bond.getDayToRedemption());
    }

    /**
     * Method for converting result set into stock object
     * @param rs - result set
     * @return - Optional<Stock>
     */
    private Optional<Stock> resultSetToStock(ResultSet rs) {
        log.info("Starting DataProviderJDBC resultSetToStock[0]");
        try {
            log.info("resultSetToStock[1]: ResultSet - {}", rs);
            log.debug("resultSetToStock[2]: Get stock from resultSet");
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
                    .withType(StockType.valueOf(rs.getString(STOCK_COLUMN_TYPE)))
                    .withDividendSum(rs.getDouble(STOCK_COLUMN_DIVIDENDSUM))
                    .withCapitalization(rs.getDouble(STOCK_COLUMN_CAPITALIZATION))
                    .build());
        }catch (Exception e){
            log.error("Function DataProviderJDBC resultSetToStock had failed[3]: {}", e.getMessage());
        }
        return Optional.empty();

    }

    /**
     * Method for converting result set into bond object
     * @param rs - result set
     * @return - Optional<Bond>
     */
    private Optional<Bond> resultSetToBond(ResultSet rs) {
        log.info("Starting DataProviderJDBC resultSetToBond[0]");
        try {
            log.info("resultSetToBond[1]: ResultSet - {}", rs);
            log.debug("resultSetToBond[2]: Get stock from resultSet");
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
                    .withType(BondType.valueOf(rs.getString(BOND_COLUMN_TYPE)))
                    .build());
        }catch (Exception e){
            log.error("Function DataProviderJDBC resultSetToBond had failed[3]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<Stock> getStocks() {
        return getSecurities(STOCK_TABLE_NAME, this::resultSetToStock);
    }

    @Override
    public Result<Stock> appendStocks(List<Stock> stocks) {
        return appendSecurity(stocks, STOCK_TABLE_NAME,MarketType.SHARES ,SQL_STOCK_COLUMNS,Stock.class, this::setStockValues, this::getStocks);
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
        return appendSecurity(bonds, BOND_TABLE_NAME, MarketType.BONDS, SQL_BOND_COLUMNS, Bond.class, this::setBondValues, this::getBonds);
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

    /**
     * Method for getting list of stocks by ticker list
     * @param tickerList - ticker list
     * @return - list of stocks
     */
    public Result<Stock> getStocksByTickerList(List<String> tickerList){
        return getSecuritiesByTickerList(tickerList, this::getStocks);
    }

    /**
     * Method for getting list of bonds by ticker list
     * @param tickerList - ticker list
     * @return - list of bonds
     */
    public Result<Bond> getBondsByTickerList(List<String> tickerList){
        return getSecuritiesByTickerList(tickerList, this::getBonds);
    }

    //
    public Result<Security> getSecuritiesByTickerList(List<String> tickers){
        log.info("Starting DataProviderJDBC getSecuritiesByTickerList[0]");
        try {
            Validator.isValid(tickers);
            log.info("getSecuritiesByTickerList[1]: {}", tickers);
            log.debug("getSecuritiesByTickerList[2]: Getting securities from markets");
            List<Security> response = new ArrayList<>();
            response.addAll(getStocksByTickerList(tickers).getBody());
            response.addAll(getBondsByTickerList(tickers).getBody());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC getSecuritiesByTickerList had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Security> getSecurityByTicker(String ticker){
        log.info("Starting DataProviderJDBC getSecurityByTicker[0]");
        try {
            Validator.isValid(ticker);
            log.info("getSecurityByTicker[1]: ticker - {}", ticker);
            log.debug("getSecurityByTicker[2]: Getting security by ticker {}", ticker);
            Optional<Stock> security = getStockByTicker(ticker);
            if (security.isPresent())
                return Optional.of(security.get());
            Optional<Bond> bond = getBondByTicker(ticker);
            if (bond.isPresent())
                return Optional.of(bond.get());
        }catch (Exception e){
            log.error("Function DataProviderJDBC had failed[3]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    // Security history

    /**
     * Method for getting connection to history db
     * @param dbName - table name
     * @return - connection's object to work with db
     * @throws Exception - Exception
     */
    private Connection getSecurityHistoryDBConnection(String dbName)throws Exception {
        log.info("Starting DataProviderJDBC getHistoryDBConnection[0]");
        return getDbConnection(dbName, SECURITY_HISTORY_PATH);
    }

    /**
     * Method for getting date from securityHistory
     * @param securityHistories - list of securityHistories
     * @return List of dates
     */
    private List<String> getDate(List<SecurityHistory> securityHistories){
        return new ArrayList<>(securityHistories.stream().map(SecurityHistory::getDate).toList());
    }

    /**
     * Method for converting result set into SecurityHistory object
     * @param rs - result set
     * @return - SecurityHistory object
     */
    private SecurityHistory resultSetToSecurityHistory(ResultSet rs) throws SQLException {
        return new SecurityHistoryBuilder().withDate(rs.getString(SECURITY_HISTORY_COLUMN_DATE))
                .withTicker(rs.getString(SECURITY_HISTORY_COLUMN_TICKER))
                .withAveragePerDay(rs.getDouble(SECURITY_HISTORY_COLUMN_AVERAGEPERDAY))
                .withOpenPrice(rs.getDouble(SECURITY_HISTORY_COLUMN_OPENPRICE))
                .withClosePrice(rs.getDouble(SECURITY_HISTORY_COLUMN_CLOSEPRICE))
                .withVolume(rs.getLong(SECURITY_HISTORY_COLUMN_VOLUME))
                .build();
    }

    /**
     * Method for converting securityHistory object into sql function
     * @param securityHistory - securityHistory object to convert
     * @return sql function as string
     */
    private String setSecurityHistoryValues(SecurityHistory securityHistory){
        return String.format(Locale.ROOT,SQL_SECURITY_HISTORY_VALUES, securityHistory.getDate(),
                securityHistory.getTicker(), securityHistory.getAveragePerDay(),
                securityHistory.getOpenPrice(), securityHistory.getClosePrice(),
                securityHistory.getVolume());
    }
    /**
     * Method for converting securityHistory object into sql function to update
     * @param securityHistory - securityHistory object
     * @return sql function as string
     */
    private String sqlUpdateSecurityHistory(SecurityHistory securityHistory, String ticker){
        return String.format(SQL_UPDATE, ticker.toUpperCase())
                .concat(String.format(Locale.ROOT,SQL_SET_SECURITY_HISTORY, securityHistory.getTicker(),
                        securityHistory.getAveragePerDay(), securityHistory.getOpenPrice(),
                        securityHistory.getClosePrice(), securityHistory.getVolume()));
    }

    /**
     * Method for creating table in history path
     * @param tableName - table name by name
     * @throws Exception - Exception
     */
    private void creatingHistoryTable(String tableName) throws Exception {
        log.info("Starting DataProviderJDBC creatingHistoryTable[0]");
        creatingTable(tableName, SECURITY_HISTORY_PATH, ru.sfedu.Constants.SQL_SECURITY_HISTORY_COLUMNS);
    }

    @Override
    public Result<SecurityHistory> appendSecurityHistory(List<SecurityHistory> securityHistories, String ticker) {
        log.info("Starting DataProviderJDBC appendSecurityHistory[0]");
        try {
            log.info("appendSecurityHistory[1]: securityHistories - {}, ticker - {}", securityHistories, ticker);
            Validator.isValidSecurityHistory(securityHistories, ticker);
            log.debug("appendSecurityHistory[2]: Create table");
            creatingHistoryTable(ticker);
            log.debug("appendSecurityHistory[3]: Connect to db");
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("appendSecurityHistory[4]: Get all date");
            List<String> allDate = getDate(getSecurityHistories(ticker).getBody());
            List<SecurityHistory> response = new ArrayList<>(securityHistories.stream().filter(x -> allDate.contains(x.getDate())).toList());
            log.debug("appendSecurityHistory[5]: Append security histories");
            for (SecurityHistory secHis : securityHistories.stream().filter(x -> !allDate.contains(x.getDate())).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,ticker)
                        .concat(setSecurityHistoryValues(secHis)));
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "SecurityHistories have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of SecurityHistories that haven't been appended: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurityHistory had failed[6]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<SecurityHistory> getSecurityHistories(String ticker){
        log.info("Starting DataProviderJDBC getSecurityHistory[0]");
        List<SecurityHistory> securityHistories = new ArrayList<>();
        try {
            log.info("getSecurityHistories[1]: ticker - {}", ticker);
            log.debug("getSecurityHistory[2]: Connect to history db");
            Connection connection = getSecurityHistoryDBConnection(ticker.toUpperCase());
            log.debug("getSecurityHistory[3]: Get security histories from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, ticker.toUpperCase()));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next())
                securityHistories.add(resultSetToSecurityHistory(rs));
            return new Result<>(SUCCESS, String.format("Number of histories in file: %d", securityHistories.size()), securityHistories);
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurityHistory had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), securityHistories);
        }
    }

    @Override
    public SecurityHistory getSecurityHistoryByDate(String ticker, String date) {
        log.info("Starting DataProviderJDBC getSecurityHistoryByDate[0]");
        try {
            log.info("getSecurityHistoryByDate[1]: ticker - {}, date - {}", ticker, date);
            log.debug("getSecurityHistoryByDate[2]: Connect to db");
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("getSecurityHistoryByDate[3]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, ticker.toUpperCase())
                    .concat(SQL_WHERE).concat(String.format(SQL_SECURITY_HISTORY_DATE, date))).executeQuery();
            if (rs.next())
                return resultSetToSecurityHistory(rs);
        }catch (Exception e){
            log.error("Function DataProviderJDBC getSecurityHistoryByDate had failed[4]: {}", e.getMessage());
        }
        return new SecurityHistoryBuilder().empty(date, ticker);
    }

    @Override
    public SecurityHistory getSecurityHistoryByDate(String ticker) {
        return getSecurityHistoryByDate(ticker, DATE);
    }

    @Override
    public Result<SecurityHistory> deleteAllSecurityHistories(String ticker) {
        log.info("Starting DataProviderJDBC dropHistoryTable[0]");
        try {
            log.info("deleteAllSecurityHistories[1]: {}", ticker);
            log.debug("deleteAllSecurityHistories[2]: Getting data from table");
            Result<SecurityHistory> securityHistoryResult = getSecurityHistories(ticker);
            if (securityHistoryResult.getStatus().equals(FAIL))
                throw new Exception(String.format("Database %s wasn't found", ticker));
            log.debug("deleteAllSecurityHistories[3]: Getting connection");
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("deleteAllSecurityHistories[4]: Dropping {} table", ticker);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_DROP_TABLE, ticker.toUpperCase()));
            preparedStatement.executeUpdate();
            connection.commit();
            return new Result<>(SUCCESS, String.format("Num of deleted history: %d", securityHistoryResult.getBody().size()), securityHistoryResult.getBody());
        } catch (Exception e){
            log.error("Function DataProvider deleteAllSecurityHistories had failed[5]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public void deleteAllSecurityHistories(List<String> tickerList){
        tickerList.forEach(this::deleteAllSecurityHistories);
    }

    @Override
    public boolean appendOrUpdateSecurityHistory(SecurityHistory securityHistory, String ticker){
        log.info("Starting DataProviderJDBC appendOrUpdate[0]");
        try {
            log.info("appendOrUpdateSecurityHistory[1]: securityHistory - {}, ticker - {}", securityHistory, ticker);
            Validator.isValidSecurityHistory(securityHistory, ticker);
            log.debug("appendOrUpdateSecurityHistory[2]: Creating table");
            creatingHistoryTable(ticker);
            Connection connection = getSecurityHistoryDBConnection(ticker);
            log.debug("appendOrUpdateSecurityHistory[3]: Appending or updating security history");
            if( connection.createStatement().executeUpdate(sqlUpdateSecurityHistory(securityHistory, ticker)) == 0)
                connection.createStatement().executeUpdate(String.format(SQL_INSERT,ticker)
                        .concat(setSecurityHistoryValues(securityHistory)));
            connection.commit();
            return true;
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendOrUpdateSecurityHistory had failed[4]: {}", e.getMessage());
        }
        return false;
    }

    // user
    /**
     * Method for getting user's id from user's list
     * @param users - list of users
     * @return list of user's id
     */
    private List<String> getUsersId(List<User> users){
        return users.stream().map(User::getId).toList();
    }

    /**
     * Method for converting result set into User object
     * @param rs - result set
     * @return - User object
     */
    private User resultSetToUser(ResultSet rs) throws SQLException {
        String userId = rs.getString(USER_COLUMN_ID);
        return new UserBuilder(userId)
                .withName(rs.getString(USER_COLUMN_NAME))
                .withActionHistory(getActionHistory(userId).getBody())
                .withTickerList(getUsersSecurity(userId).getBody())
                .build();
    }
    /**
     * Method for converting user object into sql function to update
     * @param user - user
     * @return sql function as string
     */
    private String sqlUpdateUser(User user){
        return String.format(SQL_UPDATE, USER_TABLE_NAME)
                .concat(String.format(SQL_SET_USER, user.getName()))
                .concat(SQL_WHERE + String.format(SQL_USER_ID, user.getId()));
    }

    /**
     * Method for converting user object into sql function
     * @param user - user object to convert
     * @return sql function as string
     */
    private String setUserValues(User user){
        return String.format(SQL_USER_VALUES, user.getId(), user.getName());
    }

    @Override
    public Result<User> appendUsers(List<User> userList){
        log.info("Starting DataProviderJDBC appendUsers[0]");
        try {
            Validator.isValidUser(userList);
            log.info("appendUsers[1]: users - {}", userList);
            log.debug("appendUsers[2]: Getting all users");
            List<String> idList = getUsersId(getUsers().getBody());
            log.debug("appendUsers[3]: Creating table");
            creatingTable(User.class, SQL_USER_COLUMNS);
            log.debug("appendUsers[4]: Connecting to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("appendUsers[5]: Appending to db");
            for (User user : userList.stream().filter(x ->!idList.contains(x.getId())).toList()) {
                connection.createStatement().executeUpdate(String.format(SQL_INSERT, USER_TABLE_NAME)
                        .concat(setUserValues(user)));
            }
            List<User> response = userList.stream().filter(x ->idList.contains(x.getId())).toList();
            connection.commit();
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Users were appended successfully", new ArrayList<>());
            return new Result<>(WARN, String.format("Number of users that weren't appended: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC had failed[6]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<String> appendUser(String name){
        log.info("Starting DataProviderJDBC appendUser[0]");
        try {
            Validator.isValid(name);
            log.info("appendUser[1]: name - {}", name);
            User user = new UserBuilder().withName(name).withTickerList(new ArrayList<>())
                    .withActionHistory(new ArrayList<>()).build();
            log.debug("appendUser[2]: Appending user");
            appendUsers(new ArrayList<>(List.of(user)));
            return Optional.of(user.getId());
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendUser had failed[3]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<User> getUsers(){
        List<User> usersList = new ArrayList<>();
        log.info("Starting DataProviderJDBC getUsers[0]");
        try {
            log.debug("getUsers[1]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("getUsers[2]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                usersList.add(resultSetToUser(rs));
            }
            return new Result<>(SUCCESS, String.format("Number of users: %d", usersList.size()),usersList );
        }catch (Exception e){
            log.error("Function DataProviderJDBC had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> userList){
        log.info("Starting DataProviderJDBC updateUsers[0]");
        try {
            Validator.isValidUserToUpdate(userList);
            log.info("updateUsers[1]: users - {}", userList);
            log.debug("updateUsers[2]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("updateUsers[3]: Updating user");
            List<User> response = new ArrayList<>();
            for (User user : userList)
                if( connection.createStatement().executeUpdate(sqlUpdateUser(user)) == 0)
                    response.add(user);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Users were updated successfully", new ArrayList<>());
            return new Result<>(WARN, String.format("Number of users that weren't updated: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC updateUsers had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUser(User user){
        log.info("Starting DataProviderJDBC updateUser[0]");
        try {
            Validator.isValid(user);
            log.info("updateUser[1]: user - {}", user);
            return updateUsers(new ArrayList<>(List.of(user)));
        }catch (Exception e){
            log.error("Function DataProviderJDBC updateUsers had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> getUserById(String id){
        log.info("Starting DataProviderJDBC getUserById[0]");
        try {
            Validator.isValid(id);
            log.info("getUserById[1]: id - {}", id);
            log.debug("updateUsers[2]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("updateUsers[3]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id))).executeQuery();
            if (rs.next())
                return Optional.of(resultSetToUser(rs));
        }catch (Exception e){
            log.error("Function DataProviderJDBC getUserById had failed[4]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUserById(String id) {
        log.info("Starting DataProviderJDBC deleteUserById[0]");
        try {
            log.info("deleteUserById[1]: id - {}", id);
            log.debug("deleteUserById[2]: Connecting to table");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("deleteUserById[3]: Getting resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM, USER_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id))).executeQuery();
            if (rs.next()){
                log.debug("deleteUserById[4]: Delete user by id: {}", id);
                log.debug("deleteUserById[5]: Deleting all user's securities");
                deleteAllUsersSecurity(id);
                log.debug("deleteUserById[6]: Deleting all user's action");
                deleteActionHistory(id);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, USER_TABLE_NAME)
                        .concat(SQL_WHERE).concat(String.format(SQL_USER_ID, id)));
                return Optional.of(resultSetToUser(rs));
            }
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteUserById had failed[7]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<User> deleteAllUsers(){
        log.info("Starting DataProviderJDBC deleteAllUsers[0]");
        try {
            log.debug("deleteAllUsers[1]: get all stocks");
            Result<User> result = getUsers();
            if (result.getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllUsers[2]: Connect to db");
            Connection connection = getDbConnection(USER_TABLE_NAME);
            log.debug("deleteAllUsers[3]: Delete all users");
            int count = connection.prepareStatement(String.format(SQL_DELETE_FROM, USER_TABLE_NAME)).executeUpdate();
            log.info("deleteAllUsers[4]: Number of delete stocks: {}", count);
            log.debug("deleteAllUsers[5]: Deleting all user's security");
            deleteAllUsersSecurity();
            log.debug("deleteAllUsers[6]: Deleting all actions");
            deleteAllActions();
            return new Result<>(SUCCESS, String.format("Number of deleted users: %d", result.getBody().size()), result.getBody());
        } catch (Exception e){
            log.error("Function DataProviderJDBC had failed[7]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    // action
    /**
     * Method for converting action object into sql function
     * @param action - action object to convert
     * @return sql function as string
     */
    private String setActionValues(Action action){
        return String.format(SQL_ACTION_VALUES, action.getId(), action.getDate(),
                action.getAction(), action.getUserID(), action.getSecurity().getTicker());
    }

    /**
     * Method for converting result set into Action object
     * @param rs - result set
     * @return - Action object
     */
    private Action resultSetToAction(ResultSet rs) throws SQLException {
        return new ActionBuilder()
                .withId(rs.getString(ACTION_COLUMN_ID))
                .withDate(rs.getString(ACTION_COLUMN_DATE))
                .withUserID(rs.getString(ACTION_COLUMN_USER_ID))
                .withAction(ActionType.valueOf(rs.getString(ACTION_COLUMN_ACTION)))
                .withSecurity(getSecurityByTicker(rs.getString(ACTION_COLUMN_SECURITY)).get())
                .build();
    }

    @Override
    public Optional<String> appendAction(ActionType actionType, String userID, String ticker){
        log.info("Starting DataProviderJDBC appendAction had failed[0]");
        try {
            if (getUserById(userID).isEmpty())
                throw new  Exception(String.format("User %s wasn't found", userID));
            log.info("appendAction[1]: actionType - {}, userID - {}, security - {}", actionType, userID, ticker);
            Optional<Security> security = getSecurityByTicker(ticker);
            if (security.isEmpty())
                throw new Exception(String.format("Security hasn't been found by ticker %s", ticker));
            Action action = new ActionBuilder().withAction(actionType)
                    .withUserID(userID).withDate(CURRENT_TIME).withSecurity(security.get()).build();
            Validator.isValidAction(action);
            if (!appendOrDeleteUsersSecurity(action))
                return Optional.empty();
            log.debug("appendUsers[2]: Creating table");
            creatingTable(Action.class, SQL_ACTION_COLUMNS);
            log.debug("appendUsers[3]: Connecting to db");
            Connection connection = getDbConnection(ACTON_TABLE_NAME);
            log.debug("appendAction[4]: Appending action");
            connection.createStatement().executeUpdate(String.format(SQL_INSERT, ACTON_TABLE_NAME)
                    .concat(setActionValues(action)));
            return Optional.of(action.getId());
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendAction had failed[5]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<Action> getActionHistory(String userId){
        List<Action> actionHistory = new ArrayList<>();
        log.info("Starting DataProviderJDBC getActionHistory[0]");
        try {
            Validator.isValid(userId);
            log.info("getActionHistory[1]: userId - {}", userId);
            log.debug("getActionHistory[2]: Connecting to db");
            Connection connection = getDbConnection(ACTON_TABLE_NAME);
            log.debug("getActionHistory[3]: Getting actions from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, ACTON_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_ACTION_USER_ID, userId)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
                actionHistory.add(resultSetToAction(rs));
            return new Result<>(SUCCESS, String.format("Number of actions: %d", actionHistory.size()), actionHistory);
        }catch (Exception e){
            log.error("Function DataProviderJDBC getActionHistory had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Action> deleteActionHistory(String userId){
        log.info("Starting DataProviderJDBC deleteActionHistory[0]");
        try {
            Validator.isValid(userId);
            log.info("deleteActionHistory[1]: userId - {}", userId);
            log.debug("deleteActionHistory[2]: Connecting to db");
            Connection connection = getDbConnection(ACTON_TABLE_NAME);
            log.debug("deleteActionHistory[3]: Getting all actions");
            List<Action> actions = getActionHistory(userId).getBody();
            log.debug("deleteActionHistory[4]: Deleting action history");
            connection.createStatement().executeUpdate(String.format(SQL_DELETE_FROM, ACTON_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_ACTION_USER_ID, userId)));
            return new Result<>(SUCCESS, String.format("Number of deleted actions: %d", actions.size()), actions);
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteActionHistory had failed[5]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Method for deleting all actions from db
     */
    private void deleteAllActions(){
        log.info("Starting DataProviderJDBC deleteAllActions[0]");
        try {
            log.debug("deleteAllActions[1]: Connecting to db");
            Connection connection = getDbConnection(ACTON_TABLE_NAME);
            log.debug("deleteAllActions[2]: Deleting all user's securities");
            int count = connection.prepareStatement(String.format(SQL_DELETE_FROM, ACTON_TABLE_NAME))
                    .executeUpdate();
            log.info("deleteAllActions[3]: Number of deleted actions: {}", count);
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllActions had failed[3]: {}", e.getMessage());
        }
    }

    /**
     * Method for adding updating user's briefcase
     * @param action - user's action
     * @return status of function
     * true - if function ended up successfully
     * false - otherwise
     */
    private boolean appendOrDeleteUsersSecurity(Action action){
        log.info("Starting DataProviderJDBC appendUsersSecurity[0]");
        try {
            Validator.isValidAction(action);
            log.info("appendUsersSecurity[1]: action - {}", action);
            log.debug("appendUsersSecurity[2]: Checking if action is valid");
            boolean result = processAction(action);
            log.debug("appendUsersSecurity[3]: Creating table");
            creatingTable(USERS_SECURITY_TABLE_NAME,"", SQL_USERS_SECURITY_COLUMNS);
            log.debug("appendUsersSecurity[4]: Connecting to db");
            Connection connection = getDbConnection(USERS_SECURITY_TABLE_NAME);
            if (action.getAction().equals(ActionType.DELETE) && result ) {
                log.debug("appendUsersSecurity[5]: Deleting info");
                return connection.prepareStatement(String.format(SQL_DELETE_FROM, USERS_SECURITY_TABLE_NAME)
                        .concat(SQL_WHERE)
                        .concat(String.format(SQL_ACTION_USER_ID, action.getUserID())).concat(SQL_AND)
                        .concat(String.format(SQL_ACTION_SECURITY, action.getSecurity().getTicker()))).executeUpdate() > 0;
            }
            if (action.getAction().equals(ActionType.ADD) && !result) {
                log.debug("appendUsersSecurity[6]: Appending info");
                return connection.createStatement().executeUpdate(String.format(SQL_INSERT, USERS_SECURITY_TABLE_NAME)
                        .concat(String.format(SQL_USERS_SECURITY_VALUES, action.getUserID(), action.getSecurity().getTicker()))) > 0;
            }
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendUsersSecurity had failed[7]: {}", e.getMessage());
        }
        return false;
    }
    /**
     * Method for processing action depending on actionType
     * It will add security or delete security if the expression matches the condition
     * @param action - user's action
     * @return status of function
     * true - if function ended up successfully
     * false - otherwise
     */
    private boolean processAction(Action action) {
        log.info("Starting DataProviderJDBC processAction[0]");
        try {
            Validator.isValidAction(action);
            log.info("processAction[1]: action - {}", action);
            log.debug("processAction[2]: Connecting to db");
            Connection connection = getDbConnection(USERS_SECURITY_TABLE_NAME);
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, USERS_SECURITY_TABLE_NAME)
                    .concat(SQL_WHERE)
                    .concat(String.format(SQL_ACTION_USER_ID, action.getUserID())).concat(SQL_AND)
                    .concat(String.format(SQL_ACTION_SECURITY, action.getSecurity().getTicker())));
            return preparedStatement.executeQuery().next();
        }catch (Exception e){
            log.error("Function DataProviderJDBC processAction had failed[3]: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Method for getting all user's securities that user has saved
     * @param userId - user's id
     * @return - Result<Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities
     */
    private Result<Security> getUsersSecurity(String userId){
        log.info("Starting DataProviderJDBC getUsersSecurity[0]");
        List<String> tickerList = new ArrayList<>();
        try {
            Validator.isValid(userId);
            log.info("getUsersSecurity[1]: userId - {}", userId);
            log.debug("getUsersSecurity[2]: Connecting to db");
            Connection connection = getDbConnection(USERS_SECURITY_TABLE_NAME);
            log.debug("getUsersSecurity[3]: Getting tickers from db");
            PreparedStatement preparedStatement = connection.prepareStatement(String.format(SQL_SELECT_FROM, USERS_SECURITY_TABLE_NAME)
                    .concat(SQL_WHERE).concat(String.format(SQL_ACTION_USER_ID, userId)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
                tickerList.add(rs.getString(ACTION_COLUMN_SECURITY));
            Validator.isValid(tickerList);
            return new Result<>(SUCCESS, String.format("Number of securities: %d", tickerList.size()), getSecuritiesByTickerList(tickerList).getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC getUsersSecurity had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Method for deleting all securities that user has saved
     * @param userId - user's id
     */
    private void deleteAllUsersSecurity(String userId){
        log.info("Starting DataProviderJDBC deleteAllUsersSecurity[0]");
        try {
            Validator.isValid(userId);
            log.info("deleteAllUsersSecurity[1]: userId- {}", userId);
            log.debug("deleteAllUsersSecurity[2]: Connecting to db");
            Connection connection = getDbConnection(USERS_SECURITY_TABLE_NAME);
            log.debug("deleteAllUsersSecurity[3]: Deleting all user's securities");
            connection.prepareStatement(String.format(SQL_DELETE_FROM, USERS_SECURITY_TABLE_NAME)
                    .concat(SQL_WHERE)
                    .concat(String.format(SQL_ACTION_USER_ID, userId)))
                    .executeUpdate();
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllUsersSecurity had failed[4]: {}", e.getMessage());
        }
    }

    /**
     * Method for deleting database that consists securities that all users have saved
     */
    private void deleteAllUsersSecurity(){
        log.info("Starting DataProviderJDBC deleteAllUsersSecurity[0]");
        try {
            log.debug("deleteAllUsersSecurity[1]: Connecting to db");
            Connection connection = getDbConnection(USERS_SECURITY_TABLE_NAME);
            log.debug("deleteAllUsersSecurity[2]: Deleting all user's securities");
            connection.prepareStatement(String.format(SQL_DELETE_FROM, USERS_SECURITY_TABLE_NAME))
                    .executeUpdate();
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllUsersSecurity had failed[3]: {}", e.getMessage());
        }
    }


    // use case
    @Override
    public Result<SecurityHistory> findSecurity(String ticker){
        log.info("Starting DataProviderJDBC findSecurity[0]");
        try {
            log.info("findSecurity[1]: ticker - {}", ticker);
            return showDetailedInfo(ticker);
        }catch (Exception e){
            log.error("Function DataProviderJDBC had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }
    @Override
    public Result<Security> findSecurity(MarketType marketType){
        log.info("Starting DataProviderJDBC findSecurity[0]");
        try {
            log.info("findSecurity[1]: marketType - {}", marketType);
            return getActiveSecurities(marketType);
        }catch (Exception e){
            log.error("Function DataProviderJDBC had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Security> getActiveSecurities(MarketType marketType){
        log.info("Starting DataProviderJDBC getActiveSecurities[0]");
        try {
            log.info("getActiveSecurities[1]: marketType - {}", marketType);
            Optional<Market> market = getMarket(marketType);
            if (market.isEmpty())
                throw new Exception("Market wasn't found");
            List<Security> securityList = market.get().getSecurityList();
            securityList.sort(Comparator.comparing( (Security x) -> x.getHistory().getVolume() ).reversed());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderJDBC getActiveSecurities had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<SecurityHistory> showDetailedInfo(String ticker) {
        log.info("Starting DataProviderJDBC showDetailedInfo[0]");
        try {
            log.debug("showDetailedInfo[1]: ticker - {}", ticker);
            Optional<Security> security = getSecurityByTicker(ticker);
            if (security.isEmpty())
                throw new Exception("Security wasn't found");
            log.debug("showDetailedInfo[2]: Getting and sorting history");
            List<SecurityHistory> securityHistoryList = getSecurityHistories(security.get().getTicker()).getBody();
            securityHistoryList.sort(Comparator.comparing(SecurityHistory::getDate).reversed());
            return new Result<>(SUCCESS, String.format("Providing %s's histories: \n", ticker), securityHistoryList);
        }catch (Exception e){
            log.error("Function DataProviderJDBC showDetailedInfo had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public String showInfo(String ticker){
        log.info("Starting DataProviderCSV showInfo[0]");
        try {
            Validator.isValid(ticker);
            log.info("showInfo[1]: ticker - {}", ticker);
            log.debug("showInfo[2]: Getting security by ticker {}", ticker);
            Optional<Security> security = getSecurityByTicker(ticker);
            if (security.isEmpty())
                throw new Exception(String.format("Security %s wasn't found", ticker));
            log.debug("showInfo[3]: Getting extra information");
            String extraInfo = security.get().getMarketType().equals(MarketType.SHARES) ?
                    String.format("dividendSum: %.3f \ncapitalization: %.3f \ntype: %s \n"
                            ,((Stock) security.get()).getDividendSum()
                            ,((Stock) security.get()).getCapitalization()
                            ,((Stock) security.get()).getType()):
                    String.format("matDate: %s \ncoupon: %.3f \ndayToRedemption: %d \ntype: %s \n"
                            ,((Bond) security.get()).getMatDate()
                            ,((Bond) security.get()).getCoupon()
                            ,((Bond) security.get()).getDayToRedemption()
                            ,((Bond) security.get()).getType());
            return String.format("\nticker: %s \nname: %s \nisin: %s \nnominal: %.3f \nnominalValue: %s \nissueDate: %s \nlatName: %s \nissueSize: %d \ngroup: %s \n"
                            ,security.get().getTicker()
                            ,security.get().getShortName()
                            ,security.get().getIsin()
                            ,security.get().getNominal()
                            ,security.get().getNominalValue()
                            ,security.get().getIssueDate()
                            ,security.get().getLatName()
                            ,security.get().getIssueSize()
                            ,security.get().getMarketType())
                    .concat(extraInfo);

        }catch (Exception e){
            log.error("Function DataProviderCSV showInfo had failed[4]: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public Result<Security> checkVirtualBriefCase(String userId){
        log.info("Starting DataProviderJDBC checkVirtualBriefCase[0]");
        try {
            log.info("checkVirtualBriefCase[1]: userId - {}", userId);
            log.debug("checkVirtualBriefCase[2]: Getting user by id {}", userId);
            Optional<User> user = getUserById(userId);
            if (user.isEmpty())
                throw new Exception(String.format("User wasn't found by id %s", userId));
            return new Result<>(SUCCESS, String.format("Number of saved securities: %d", user.get().getSecurityList().size()), user.get().getSecurityList()) ;
        }catch (Exception e){
            log.error("Function DataProviderJDBC checkVirtualBriefCase[3]: {}" ,e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public String showStatistics(String userId){
        log.info("Starting DataProviderJDBC showStatistics[0]");
        try {
            log.info("showStatistics[1]: userId - {}", userId);
            log.debug("showStatistics[2]: Getting user by id {}", userId);
            Optional<User> user = getUserById(userId);
            if (user.isEmpty())
                throw new Exception(String.format("User %s wasn't found", userId));
            StringBuilder info = new StringBuilder("\n");
            for (Security security: user.get().getSecurityList())
                info.append(security.getTicker()).append(": ").append(security.getHistory().getAveragePerDay()).append("\n");
            List<Action> actions = user.get().getActionHistory();
            actions.sort(Comparator.comparing(Action::getDate).reversed());
            for (Action action : actions)
                info.append("Time: ").append(action.getDate()).append("\n Action: ").append(action.getAction())
                        .append("\n Security: ").append(action.getSecurity().getTicker()).append("\n");
            return info.toString();
        }catch (Exception e){
            log.error("Function DataProviderJDBC showStatistics had failed[3]: {}" , e.getMessage());
        }
        return "";
    }

    @Override
    public boolean performActon(String userId, String actionType, String ticker){
        log.info("Starting DataProviderJDBC performActon[0]");
        try {
            log.info("performActon[1]: userId - {}, actionType - {}, ticker - {}", userId, actionType, ticker);
            return appendAction(ActionType.valueOf(actionType.toUpperCase()),userId ,ticker).isPresent();
        }catch (Exception e){
            log.error("Function DataProviderJDBC performActon had failed[2]: {}", e.getMessage());
        }
        return false;
    }

}
