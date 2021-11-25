package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.Constants;
import ru.sfedu.model.Result;
import ru.sfedu.model.entity.Stock;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static ru.sfedu.Constants.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderJDBC implements IDateProvider{
    private final Logger log = (Logger) LogManager.getLogger(DataProviderJDBC.class.getName());
    Connection dbConnection;
    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException, IOException {
        String connectionString = String.format("jdbc:h2:%s%s", getConfigurationEntry(DB_PATH),
                getConfigurationEntry(DB_NAME));
        Class.forName("org.h2.Driver");

        dbConnection = DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                getConfigurationEntry(DB_PASSWORD));
        return dbConnection;
    }



    private void creatingTable() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        log.info("Starting DataProviderJDBC creatingTable[0]");
        try {
            conn = getDbConnection();
            log.info("Connect to db[1]");
            stmt = conn.createStatement();
            log.debug("Create table if it doesn't exist[2]");
            stmt.executeUpdate(SQL_CREATE_USERS_TABLE);

        } catch (Exception e) {
            log.error("Function DataProviderJDBC creatingTable had failed[3]");
            throw new Exception(e);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e){
                log.error("Failed to close db, creatingTable[4]");
                throw new Exception(e);
            }

        }

    }

    @Override
    public Result<Stock> appendStocks(List<Stock> stocks) throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        List<String> idColumn = new ArrayList<>();
        log.info("Starting DataProviderJDBC appendStocks[5]");
        try {
            log.info("appendStocks[]: {}, type: {}[6]", Arrays.toString(stocks.toArray()), stocks.getClass().getName());
            if (stocks.isEmpty()){
                log.error("Empty size[7]");
                throw new Exception("Empty size");
            }
            if (stocks.contains(null)){
                log.error("List contains null[8]");
                throw new Exception("List contains null");
            }
            log.debug("Create table[9]");
            creatingTable();
            log.debug("Connect to db[10]");
            Connection connection = getDbConnection();
            log.debug("Get all ID[]");
            preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_USERS);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                idColumn.add( rs.getString("ticker") );
            }

            for (Stock stock : stocks){
                if (stock.getTicker() != null && Collections.binarySearch(idColumn, stock.getTicker()) >= 0){
                    log.error("ID is already contained in db[]: {}", stock);
                    return new Result<Stock>(Constants.FAIL, "ID is already contained in db: " + stock , new ArrayList<>(List.of(stock)));
                }
            }


            log.debug("Insert users with id[11]");
            preparedStatement = connection.prepareStatement(SQL_INSERT_USERS);
            int num = 0;
            for (Stock user : stocks.stream().filter(x -> x.getTicker() != null).toList()){
//
//                preparedStatement.setLong(1, user.getId());
//                preparedStatement.setString(2, user.getName());
//                preparedStatement.setInt(3, user.getAge());
//                num += preparedStatement.executeUpdate();


            }
            log.info("Num of append users with id: {}",num);

            log.debug("Insert users without id[12]");
            preparedStatement = connection.prepareStatement(SQL_INSERT_USERS_WITHOUT_ID);

            num = 0;
            for (Stock stock : stocks.stream().filter(x -> x.getTicker() == null).toList()){
//                preparedStatement.setString(1, user.getName());
//                preparedStatement.setInt(2, user.getAge());
//
//                num += preparedStatement.executeUpdate();
            }
            log.info("Num of append users without id: {}", num);
        } catch (Exception e){
                log.error("Function DataProviderJDBC appendUsers had failed[13]");
                status = FAIL;
                message = e.getMessage();
        }
        return new Result<Stock>(status, message, new ArrayList<>());
    }

    @Override
    public Result<Stock> getStocks() throws Exception {
        String status = SUCCESS;
        String message = "";
        ArrayList<Stock> users = new ArrayList<>();
        log.info("Starting DataProviderJDBC getUsers[14]");
        try {
            log.debug("Connect to db[15]");
            Connection connection = getDbConnection();
            log.debug("Get users from db[16]");
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_USERS);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
//                users.add(new User( rs.getInt("id"),
//                                    rs.getString("name"),
//                                    rs.getInt("age")
//                ));
            }
        } catch (Exception e){
            log.error("Function DataProviderJDBC getUsers had failed[17]");
            status = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, users);
    }

    @Override
    public Result<Stock> updateStocks(List<Stock> stocks) throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        log.info("Starting DataProviderJDBC updateUsers[18]");
        List<String> idColumn = new ArrayList<>();
        try {
            log.info("updateUsers[19]: {}, type: {}", Arrays.toString(stocks.toArray()), stocks.getClass().getName());
            if (stocks.isEmpty()){
                log.error("Empty size[20]");
                throw new Exception("Empty size");
            }
            if (stocks.contains(null)){
                log.error("List contains null[21]");
                throw new Exception("List contains null");
            }
            log.debug("Connect to db[22]");
            Connection connection = getDbConnection();
            log.debug("Update users[23]");
            preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_USERS);
            ResultSet rs = preparedStatement.executeQuery();

            log.debug("Get all ID[24]");
            while (rs.next())
            {
                idColumn.add( rs.getString("ticker") );
            }
            for (Stock stock : stocks){
                if (stock.getTicker() == null){
                    log.error("ID is null[25]");
                    return new Result<Stock>(FAIL, "ID is null", new ArrayList<>(List.of(stock)));
                }
                if (Collections.binarySearch(idColumn, stock.getTicker()) < 0){
                    log.error("ID wasn't found[26]: {}", stock);
                    return new Result<Stock>(Constants.FAIL, "ID wasn't found: " + stock, new ArrayList<>(List.of(stock)));
                }
            }

            log.debug("Update users[27]");
            for (Stock stock : stocks){
                preparedStatement = connection.prepareStatement(SQL_UPDATE_USERS);
//                preparedStatement.setString(1, user.getName());
//                preparedStatement.setInt(2, user.getAge());
//                preparedStatement.setLong(3, user.getId());
//                preparedStatement.executeUpdate();
            }


        } catch (Exception e){
            log.error("Function DataProviderJDBC updateUsers had failed[28]");
            status  = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, new ArrayList<>());
    }

    @Override
    public Result<Stock> deleteStockByTicker(String ticker) throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        log.info("Starting DataProviderJDBC deleteUserById[29]");
        try {
            log.info("deleteUserById[30]: {}",ticker);

            log.debug("Connect to db[31]");
            Connection connection = getDbConnection();
            log.debug("Delete user by ID[32]");
            preparedStatement = connection.prepareStatement(SQL_DELETE_BY_ID_USERS);
            preparedStatement.setString(1, ticker);
            if (preparedStatement.executeUpdate() == 0){
                log.error("Id wasn't found[32]");
                throw new Exception("Id wasn't found");
            }

        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteUserById had failed[33]");
            status  = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, new ArrayList<>());
    }

    @Override
    public Result<Stock> deleteAllStocks() throws Exception {
        log.info("Starting DataProviderJDBC deleteAllUsers [34]");
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        try {
            log.debug("Connect to db[35]");
            Connection connection = getDbConnection();
            log.debug("Delete all users[36]");
            preparedStatement = connection.prepareStatement(SQL_DELETE_ALL_USERS);
            log.info("Number of delete users: {}" , preparedStatement.executeUpdate());

        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllUsers had failed[37]");
            status  = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, new ArrayList<>());
    }

    @Override
    public Optional<Stock> getStockByTicker(String ticker) throws Exception {
        log.info("Starting DataProviderJDBC getUSerById[38]");
        try {
            log.debug("Connect to db[39]");
            Connection connection = getDbConnection();
            log.debug("Prepared Statement[40]");
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_USERS_BY_ID);
            preparedStatement.setString(1,ticker);
            log.debug("Get result[41]");
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                log.debug("Return User[42]");
//                Stock stock = new Stock( id,
//                    rs.getString("name"),
//                    rs.getInt("age"));
//                return Optional.of(stock);
                return Optional.empty();
            } else {
                log.warn("User wasn't found[43]");
                return Optional.empty();
            }

        } catch (Exception e){
            log.error("Function DataProvider JDBC getUserById had failed[44]");
            throw new Exception(e);
        }
    }
}
