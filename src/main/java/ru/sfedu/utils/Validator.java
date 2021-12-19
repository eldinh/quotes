package ru.sfedu.utils;

import ru.sfedu.model.Action;
import ru.sfedu.model.Security;
import ru.sfedu.model.SecurityHistory;
import ru.sfedu.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Validator {
    Validator(){}

    public static <T> void isValid(T pojo) throws Exception {
        if(pojo == null)
            throw new IllegalArgumentException("Object is null");
    }

    public static void isValidAction(Action action) throws Exception {
        isValid(action.getAction());
        isValid(action.getId());
        isValid(action.getDate());
        isValid(action.getSecurity());
        isValid(action.getUserID());
    }

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

    public static void isValidSecurityHistory(List<SecurityHistory> securityHistories, String ticker) throws Exception {
        isValid(securityHistories);
        Optional<SecurityHistory> securityHistory = securityHistories.stream().filter(x -> x.getTicker() == null || !Objects.equals(x.getTicker(), ticker)).findFirst();
        if (securityHistory.isPresent())
            throw new Exception("Ticker equals null or tickers not the same");
        if (securityHistories.stream().distinct().toList().size() != securityHistories .size())
            throw new IllegalArgumentException("List consists entity with the same date");

    }

    public static void isValidSecurityHistory(SecurityHistory securityHistory, String ticker) throws Exception {
        if (securityHistory.getTicker() == null || ticker == null)
            throw new Exception("Ticker equals null");
        if (!securityHistory.getTicker().equals(ticker))
            throw new Exception("Tickers are not the same");
    }







}