package ru.sfedu.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.model.Result;
import ru.sfedu.model.entity.User;
import ru.sfedu.Constants;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.Constants.*;

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



    public void creatringTable() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getDbConnection();
            log.info("Creating table in given database...");
            stmt = conn.createStatement();
            String sql =  "CREATE TABLE IF NOT EXISTS USERS " +
                    "(id INT NOT NULL AUTO_INCREMENT, " +
                    " name VARCHAR(255), " +
                    " age INTEGER," +
                    "PRIMARY KEY (id))";
            stmt.executeUpdate(sql);

            log.info("Created table in given database...");
            stmt.close();
            conn.close();
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e){
                throw new Exception(e);
            }

        }

    }

    @Override
    public Result<User> appendUsers(List<User> users) throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        try {
            log.info("appendUsers[]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass().getName());
            if (users.isEmpty()){
                log.error("Empty size[]");
                throw new Exception("Empty size");
            }
            if (users.contains(null)){
                log.error("List contains null[]");
                throw new Exception("List contains null");
            }

            creatringTable();
            Connection connection = getDbConnection();
            preparedStatement = connection.prepareStatement(INSERT_USERS_SQL);
            for (User user : users.stream().filter(x -> x.getId() != null).toList()){
                preparedStatement.setLong(1, user.getId());
                preparedStatement.setString(2, user.getName());
                preparedStatement.setInt(3, user.getAge());
                preparedStatement.executeUpdate();

            }
            preparedStatement = connection.prepareStatement(INSERT_USERS_SQL_WITHOUT_ID);
            for (User user : users.stream().filter(x -> x.getId() == null).toList()){
                preparedStatement.setString(1, user.getName());
                preparedStatement.setInt(2, user.getAge());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e){
                status = FAIL;
                message = e.getMessage();
        }
        return new Result<User>(status, message, new ArrayList<>());
    }

    @Override
    public Result<User> getUsers() throws Exception {
        String status = SUCCESS;
        String message = "";
        ArrayList<User> users = new ArrayList<>();
        try {
            Connection connection = getDbConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FROM_USERS);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                users.add(new User( rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getInt("age")
                ));
            }
        } catch (Exception e){
            status = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, users);
    }

    @Override
    public Result<User> updateUsers(List<User> users) throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        List<Long> idColumn = new ArrayList<>();
        try {
            log.info("appendUsers[]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass().getName());
            if (users.isEmpty()){
                log.error("Empty size[]");
                throw new Exception("Empty size");
            }
            if (users.contains(null)){
                log.error("List contains null[]");
                throw new Exception("List contains null");
            }
            Connection connection = getDbConnection();
            preparedStatement = connection.prepareStatement(SELECT_FROM_USERS);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
            {
                idColumn.add( rs.getLong("id") );
            }

            for (User user : users){
                if (user.getId() == null){
                    log.error("id is null");
                    return new Result<User>(FAIL, "ID is null", new ArrayList<>(List.of(user)));
                }
                if (!idColumn.contains(user.getId())){
                    log.error("ID wasn't found: {}", user);
                    return new Result<User>(Constants.FAIL, "ID wasn't found", new ArrayList<>(List.of(user)));
                }
            }


            for (User user : users){
                preparedStatement = connection.prepareStatement(UPDATE_USERS);
                preparedStatement.setString(1, user.getName());
                preparedStatement.setInt(2, user.getAge());
                preparedStatement.setLong(3, user.getId());
                preparedStatement.executeUpdate();
            }


        } catch (Exception e){
            status  = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, new ArrayList<>());
    }

    @Override
    public Result<User> deleteUserById(long id) throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        try {
            Connection connection = getDbConnection();

            preparedStatement = connection.prepareStatement(DELETE_BY_ID_USERS);
            preparedStatement.setLong(1, id);
            if (preparedStatement.executeUpdate() == 0){
                throw new Exception("Id wasn't found");
            }

        }catch (Exception e){
            status  = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, new ArrayList<>());
    }

    @Override
    public Result<User> deleteAllUsers() throws Exception {
        String status = SUCCESS;
        String message = "";
        PreparedStatement preparedStatement;
        try {
            Connection connection = getDbConnection();
            preparedStatement = connection.prepareStatement(DELETE_ALL_USERS);
            preparedStatement.executeUpdate();

        }catch (Exception e){
            status  = FAIL;
            message = e.getMessage();
        }
        return new Result<>(status, message, new ArrayList<>());
    }

    @Override
    public User getUserById(long id) throws Exception {
        log.info("getUSerById");
        try {
            log.debug("Connect to ds");
            Connection connection = getDbConnection();
            log.debug("Prepared Statement");
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FROM_USERS_BY_ID);
            preparedStatement.setLong(1,id);
            log.debug("Get result");
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            log.debug("Return User");
            return new User( id,
                    rs.getString("name"),
                    rs.getInt("age"));
        } catch (Exception e){

            throw new Exception(e);
        }
    }
}
