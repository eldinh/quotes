package ru.sfedu.utils;

import ru.sfedu.entity.Security;
import ru.sfedu.entity.User;

import java.util.List;
import java.util.Objects;
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
    }


    public static void isValidUserToUpdate(List<User> userList) throws Exception{
        isValid(userList);
        Optional<User> user =  userList.stream().filter(x-> x.getId() == null).findFirst();
        if (user.isPresent())
            throw new Exception("Id equals null " + user);
    }





}