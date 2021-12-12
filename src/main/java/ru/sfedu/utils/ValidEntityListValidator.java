package ru.sfedu.utils;

import ru.sfedu.model.Security;
import ru.sfedu.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidEntityListValidator {
    ValidEntityListValidator(){}

    public static <T> void isValid(List<T> list) throws Exception {
        if (list.contains(null))
            throw new IllegalArgumentException("List contains null");
        if (list.isEmpty())
            throw new IllegalArgumentException("List is empty");
    }


    public static <T extends Security> void isValidSecurity(List<T> securityList) throws Exception {
        isValid(securityList);
        Optional<T> security = securityList.stream().filter(x -> x.getTicker() == null).findFirst();
        if (security.isPresent())
            throw new Exception("Ticker equals null" + security);
        if (securityList.stream().distinct().toList().size() != securityList .size())
            throw new IllegalArgumentException("List consists entity with the same key");
    }

    public static void isValidUser(List<User> userList) throws Exception {
        isValid(userList);
        List<User> userWithId = new ArrayList<>(userList.stream().filter(x -> x.getId() != null).toList());
        if (userWithId.stream().distinct().toList().size() != userWithId .size())
            throw new IllegalArgumentException("List consists entity with the same key");
    }

    public static void isValidUserToUpdate(List<User> userList) throws Exception{
        isValid(userList);
        Optional<User> user =  userList.stream().filter(x-> x.getId() == null).findFirst();
        if (user.isPresent())
            throw new Exception("Id equals null " + user);
        if (userList.stream().distinct().toList().size() != userList .size())
            throw new IllegalArgumentException("List consists entity with the same key");

    }





}