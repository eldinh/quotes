package ru.sfedu.api;

import ru.sfedu.model.Result;
import ru.sfedu.model.entity.User;

import java.util.List;

public interface IDateProvider {
    public Result<User> getUsers() throws Exception;
    public Result<User> appendUsers(List<User> list) throws Exception;
    public Result<User> updateUsers(List<User> ob) throws Exception;
    public Result<User> deleteUserById(long id) throws Exception;
    public Result<User> deleteAllUsers() throws Exception;
    public User getUserById(long id) throws Exception;



}
