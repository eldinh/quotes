package api;


import ru.sfedu.Constants;
import ru.sfedu.api.DataProviderJDBC;
import ru.sfedu.api.IDateProvider;
import ru.sfedu.entity.User;
import ru.sfedu.model.Result;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotEquals;
import static ru.sfedu.Constants.SQL_DROP_USERS_TABLE;


public class DataProviderJDBCTest extends BaseTest {
    DataProviderJDBC data = new DataProviderJDBC();
    private void deleteFile() throws SQLException, IOException, ClassNotFoundException {
        Connection connection = data.getDbConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_DROP_USERS_TABLE);
        preparedStatement.executeUpdate();
    }

    public void testGetUsers()  {
        result = data.appendUsers(users);
        assertEquals(data.deleteAllUsers().getStatus(), Constants.SUCCESS);
        result = data.appendUsers(users);
        result = data.getUsers();
        assert(result.getStatus().equals(Constants.SUCCESS));
        assert(result.equals(new Result<>(Constants.SUCCESS, "", users)));
    }

    public void testFailGetUsers() throws SQLException, IOException, ClassNotFoundException {
        deleteFile();
        assert(data.getUsers().getStatus().equals(Constants.FAIL));


    }

    public void testAppendUsers()  {
        assertEquals(data.deleteAllUsers().getStatus(), Constants.SUCCESS);
        result = data.appendUsers(users);
        assert(result.getStatus().equals(Constants.SUCCESS));
        assertEquals(data.getUsers().getBody(), users);
        result = data.appendUsers(userWithoutID);
        System.out.println(result.getMessage());
        assert(result.getStatus().equals(Constants.SUCCESS));

    }

    public void testFailAppendUsers()  {
        assertEquals(data.deleteAllUsers().getStatus(), Constants.SUCCESS);
        result = data.appendUsers(null);
        assertNotEquals(result.getStatus(), Constants.SUCCESS);

        ArrayList<User> list = new ArrayList<>(users.subList(0,5));
        data.appendUsers(users);
        result = data.appendUsers(list);
        System.out.println(result.getMessage());

        assert(result.getBody().equals(users.subList(0,5)));
    }


    public void testDeleteAllUsers()  {
        result = data.deleteAllUsers();
        assert(result.getStatus().equals(Constants.SUCCESS));
        result = data.getUsers();
        assert(result.getBody().equals(new ArrayList<User>()));
    }

    public void testFailDeleteAllUsers() throws SQLException, IOException, ClassNotFoundException {
        deleteFile();
        result = data.deleteAllUsers();
        assert(result.getStatus().equals(Constants.FAIL));
    }

    public void testGetUserById() throws Exception {
        result = data.appendUsers(users);
        assert(result.getStatus().equals(Constants.SUCCESS));
        Optional<User> user;
        for (int i = 0; i < users.size(); i ++){
            user = data.getUserById(i);
            if (user.isPresent()){
                assertEquals(user.get(), users.get(i));
            } else
                assert(false);
        }
    }

    public void testFailGetUserById()  {
        result = data.appendUsers(users);
        assert(result.getStatus().equals(Constants.SUCCESS));
        try{
            Optional<User> user;
            user = data.getUserById(100);
            if (user.isEmpty())
                assert(true);
            else
                assert(false);
            deleteFile();
            user = data.getUserById(0);
            if (user.isEmpty())
                assert(true);
            else
                assert(false);

            assert(false);
        } catch (Exception e){
            assert(true);
        }



    }

    public void testUpdateUsers() throws Exception {
        assertEquals(data.deleteAllUsers().getStatus(), Constants.SUCCESS);
        assertEquals(data.appendUsers(users).getStatus(), Constants.SUCCESS);
        result = data.updateUsers(users);
        System.out.println(result.getMessage());
        assertEquals(result.getStatus(), Constants.SUCCESS);
        User user = new User(1, " ", 10);
        System.out.println(users.get(1));
        result = data.updateUsers(new ArrayList<>(List.of(user)));
        assertEquals(result.getStatus(), Constants.SUCCESS);
        Optional<User> userOp = data.getUserById(1);
        if (userOp.isPresent())
            assertEquals(userOp.get(), user);
        else
            assert(false);
    }

    public void testFailUpdateUsers() throws SQLException, IOException, ClassNotFoundException {
        deleteFile();
        result = data.updateUsers(users);
        assertNotEquals(result.getStatus(), Constants.SUCCESS);
        assertEquals(data.appendUsers(users).getStatus(), Constants.SUCCESS);
        result = data.updateUsers(null);
        assertNotEquals(result.getStatus(), Constants.SUCCESS);


        result = data.updateUsers(new ArrayList<>(List.of(new User())));
        System.out.println(result.getMessage());
        System.out.println(Arrays.toString(result.getBody().toArray()));
        System.out.println(result.getStatus());
        System.out.println(new User());
        assertNotEquals(result.getStatus(), Constants.SUCCESS);


        result = data.updateUsers(new ArrayList<>(List.of(new User(7, " ", 10) ,new User(10,"", 10))));
        System.out.println(result.getBody());
        assertEquals(result.getBody().size(), 1);

    }
}