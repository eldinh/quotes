package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.entity.User;
import ru.sfedu.model.Result;
import ru.sfedu.entity.Bond;
import ru.sfedu.entity.Stock;
import ru.sfedu.utils.ValidEntityListValidator;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static ru.sfedu.Constants.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderJDBC implements IDateProvider{
    private final Logger log = LogManager.getLogger(DataProviderJDBC.class.getName());
    Connection dbConnection;

    public User resultSetToUser(ResultSet rs) throws SQLException {
        return new User( rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("age") );
    }

    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException, IOException {
        String connectionString = String.format("jdbc:h2:%s%s", getConfigurationEntry(DB_PATH),
                getConfigurationEntry(DB_NAME));
        Class.forName("org.h2.Driver");

        dbConnection = DriverManager.getConnection(connectionString, getConfigurationEntry(DB_LOGIN),
                getConfigurationEntry(DB_PASSWORD));
        return dbConnection;
    }

    public List<Long> getUsersID(List<User> users){
        return users.stream().map(User::getId).toList();
    }



    private void creatingTable() throws Exception {
        log.info("Starting DataProviderJDBC creatingTable");
        try {
            Connection conn = getDbConnection();
            log.info("creatingTable[]: Connect to db");
            Statement stmt = conn.createStatement();
            log.debug("creatingTable[]: Create table if it doesn't exist");
            stmt.executeUpdate(SQL_CREATE_USERS_TABLE);
            log.debug("creatingTable[]: Closing statement");
            stmt.close();
            log.debug("creatingTable[]: Closing connection");
            conn.close();
        } catch (Exception e) {
            log.error("Function DataProviderJDBC creatingTable had failed[]");
            throw new Exception(e);
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


    @Override
    public Result<User> appendUsers(List<User> users) {
        log.info("Starting DataProviderJDBC appendUsers[]");
        try {
            log.info("appendUsers[]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass().getName());
            ValidEntityListValidator.isValid(users);
            log.debug("appendUsers[]: Create table");
            creatingTable();
            log.debug("appendUsers[]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("appendUsers[]: Get all ID");
            List<Long> allId = getUsersID(getUsers().getBody());
            log.debug("appendUsers[]: append users with ID");
            for (User user : users.stream().filter(x ->!allId.contains(x.getId())).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT_USERS, user.getId(), user.getName(), user.getAge()));
            log.debug("appendUsers[]: append users without ID");
            for (User user : users.stream().filter(x -> x.getId()== null).toList())
                connection.createStatement().executeUpdate(String.format(SQL_INSERT_USERS_WITHOUT_ID, user.getName(), user.getAge()));
            return new Result<>(SUCCESS, "", users.stream().filter(x -> allId.contains(x.getId())).toList());
        } catch (Exception e){
            log.error("Function DataProviderJDBC appendUsers had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> getUsers()  {
        log.info("Starting DataProviderJDBC getUsers[]");
        List<User> users = new ArrayList<>();
        try {
            log.debug("getUsers[]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("getUsers[]: Get users from db");
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_USERS);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                users.add(resultSetToUser(rs));
            }
            return new Result<>(SUCCESS, "", users);
        } catch (Exception e){
            log.error("Function DataProviderJDBC getUsers had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users){
        log.info("Starting DataProviderJDBC updateUsers[]");
        try {
            log.info("updateUsers[]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass().getName());
            ValidEntityListValidator.isValidUserToUpdate(users);
            log.debug("updateUsers[]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("updateUsers[]: Get all ID");
            Result<User> result = getUsers();
            if (getUsers().getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            List<Long> idColumn = getUsersID(result.getBody());
            log.debug("updateUsers[]: Update users");
            for (User user : users.stream().filter(x -> Collections.binarySearch(idColumn, x.getId()) >= 0).toList())
                connection.createStatement().executeUpdate(String.format(SQL_UPDATE_USERS, user.getName(), user.getAge(), user.getId()));
            return new Result<>(SUCCESS, "", users.stream().filter(x -> Collections.binarySearch(idColumn, x.getId()) < 0).toList());
        } catch (Exception e){
            log.error("Function DataProviderJDBC updateUsers had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id) throws Exception {
        log.info("Starting DataProviderJDBC deleteUserById[]");
        try {
            log.info("deleteUserById[]: {}",id);
            log.debug("deleteUserById[]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("deleteUserById[]: Delete user by ID {}", id);
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM_USERS_BY_ID,id)).executeQuery();
            if (rs.next()){
                connection.createStatement().executeUpdate(String.format(SQL_DELETE_BY_ID_USERS, id));
                return Optional.of(resultSetToUser(rs));
            }
            return Optional.empty();
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteUserById had failed[]");
            throw new Exception(e);
        }

    }

    @Override
    public Result<User> deleteAllUsers() {
        log.info("Starting DataProviderJDBC deleteAllUsers []");
        try {
            log.debug("deleteAllUsers[]: get all users");
            Result<User> result = getUsers();
            if (getUsers().getStatus().equals(FAIL))
                throw new Exception(result.getMessage());
            log.debug("deleteAllUsers[]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("deleteAllUsers[]: Delete all users");
            int count  = connection.prepareStatement(SQL_DELETE_ALL_USERS).executeUpdate();
            log.info("deleteAllUsers[]: Number of delete users: {}" , count);
            return new Result<>(result.getStatus(), String.format("Number of delete users: %d", count), result.getBody());
        }catch (Exception e){
            log.error("Function DataProviderJDBC deleteAllUsers had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }

    }

    @Override
    public Optional<User> getUserById(long id) throws Exception {
        log.info("Starting DataProviderJDBC getUSerById[]");
        try {
            log.debug("getUserById[]: Connect to db");
            Connection connection = getDbConnection();
            log.debug("getUserById[]: Get resultSet");
            ResultSet rs = connection.prepareStatement(String.format(SQL_SELECT_FROM_USERS_BY_ID,id)).executeQuery();
            if (rs.next())
                return Optional.of(resultSetToUser(rs));
            return Optional.empty();
        } catch (Exception e){
            log.error("Function DataProvider JDBC getUserById had failed[]");
            throw new Exception(e);
        }
    }
}
