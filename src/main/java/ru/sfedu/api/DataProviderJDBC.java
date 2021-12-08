package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.entity.Security;
import ru.sfedu.entity.User;
import ru.sfedu.model.Result;
import ru.sfedu.entity.Bond;
import ru.sfedu.entity.Stock;
import ru.sfedu.utils.ValidEntityListValidator;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static ru.sfedu.Constants.*;
import static ru.sfedu.model.CommandName.*;
import static ru.sfedu.model.RepositoryName.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderJDBC implements IDateProvider{
    private final Logger log = LogManager.getLogger(DataProviderJDBC.class.getName());


    public List<Long> getUsersID(List<User> users){
        return users.stream().map(User::getId).toList();
    }

    public User resultSetToUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt(USER_ID_COLUMN),
                        rs.getString(USER_NAME_COLUMN),
                        rs.getInt(USER_AGE_COLUMN));
    }


    public Connection getDbConnection()
            throws Exception {
        log.info("Starting DataProviderJDBC getDbConnection[0]");
        try {
            String connectionString = String.format(getConfigurationEntry(JDBC_CONNECTION), getConfigurationEntry(DB_PATH),
                    getConfigurationEntry(DB_NAME));
            Class.forName(getConfigurationEntry(H2_DRIVER));
            log.debug("getDbConnection[1]: connect to DB");
            return DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                    getConfigurationEntry(DB_PASSWORD));

        } catch (Exception e){
            log.error("Function DataProviderJDBC getDbConnection had failed[2]");
            throw new Exception(e);
        }

    }




    private void creatingTable() throws Exception {
        log.info("Starting DataProviderJDBC creatingTable[3]");
        try {
            Connection conn = getDbConnection();
            log.info("creatingTable[4]: Connect to db");
            Statement stmt = conn.createStatement();
            log.debug("creatingTable[5]: Create table if it doesn't exist");
            stmt.executeUpdate(SQL_CREATE_USER_TABLE);
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
            ValidEntityListValidator.isValid(users);
            log.debug("appendUsers[11]: Create table");
            creatingTable();
            log.debug("appendUsers[12]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("appendUsers[13]: Get all ID");
            List<Long> allId = getUsersID(getUsers().getBody());
            log.debug("appendUsers[14]: append users with ID");
            for (User user : users.stream().filter(x ->!allId.contains(x.getId())).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT_USER, user.getId(), user.getName(), user.getAge()));
            log.debug("appendUsers[15]: append users without ID");
            for (User user : users.stream().filter(x -> x.getId()== null).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT_USER_WITHOUT_ID, user.getName(), user.getAge()));

            connection.commit();
            return new Result<>(SUCCESS, "", users.stream().filter(x -> allId.contains(x.getId())).toList());
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
            Connection connection = getDbConnection();
            log.debug("getUsers[19]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_USER);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                users.add(resultSetToUser(rs));
            }
            return new Result<>(SUCCESS, "", users);
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
            log.debug("updateUsers[23]: Get all ID");
            Result<User> result = getUsers();
            if (getUsers().getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            List<Long> idColumn = getUsersID(result.getBody());
            List<User> userToUpdate = new ArrayList<>(users.stream().filter(x -> Collections.binarySearch(idColumn, x.getId()) >= 0).toList());
            log.debug("updateUsers[24]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("updateUsers[25]: Update users");
            for (User user : userToUpdate)
                connection.createStatement().executeUpdate(String.format(SQL_UPDATE_USER, user.getName(), user.getAge(), user.getId()));
            MongoDBLog.save(UPDATE, JDBC, userToUpdate);
            connection.commit();
            return new Result<>(SUCCESS, "", users.stream().filter(x -> Collections.binarySearch(idColumn, x.getId()) < 0).toList());
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
            Connection connection = getDbConnection();
            log.debug("deleteUserById[29]: Get user by ID {}", id);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM_USER_BY_ID,id)).executeQuery();
            if (rs.next()){
                log.debug("deleteUserById[30]: Delete user by ID {}", id);
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_BY_ID_USER, id));
                MongoDBLog.save(DELETE, JDBC, resultSetToUser(rs));
                connection.commit();
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
            Connection connection = getDbConnection();
            log.debug("deleteAllUsers[35]: Delete all users");
            int count  = connection.prepareStatement(SQL_DELETE_ALL_USER).executeUpdate();
            log.info("deleteAllUsers[36]: Number of delete users: {}" , count);
            MongoDBLog.save(DELETE, JDBC, result.getBody());
            connection.commit();
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
            Connection connection = getDbConnection();
            log.debug("getUserById[40]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM_USER_BY_ID,id)).executeQuery();
            if (rs.next())
                return Optional.of(resultSetToUser(rs));
            return Optional.empty();
        } catch (Exception e){
            log.error("Function DataProvider JDBC getUserById had failed[41]");
            throw new Exception(e);
        }
    }


    private <T extends Security> Result<T> getSecurities(Class<T> securityClass){
        log.info("Starting DataProviderJDBC getSecurities[]");
        try {
            log.info("getSecurities[]: {}", securityClass);
            return null;
        } catch (Exception e){
            log.error("Function DataProviderJDBC getSecurities had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    @Override
    public Result<Stock> getStocks() {
        return null;
    }

    @Override
    public Result<Stock> appendStocks(List<Stock> stocks) {
        return null;
    }

    @Override
    public Result<Stock> updateStocks(List<Stock> stocks) {
        return null;
    }

    @Override
    public Optional<Stock> deleteStockByTicker(String ticker) throws Exception {
        return Optional.empty();
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
