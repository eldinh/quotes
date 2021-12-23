package ru.sfedu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProvider;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.api.DataProviderJDBC;
import ru.sfedu.api.DataProviderXML;
import ru.sfedu.model.Result;
import ru.sfedu.model.User;

import java.util.Arrays;
import java.util.Optional;


public class Quotes {
    private static final Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    public static DataProvider data;
    public static void main(String[] args) throws Exception {
        log.info(Arrays.toString(args));
        switch (args[0]) {
            case "csv" -> data = new DataProviderCSV();
            case "xml" -> data = new DataProviderXML();
            case "jdbc" -> data = new DataProviderJDBC();
            default -> log.info("data wasn't found");
        }

        switch (args[1]){
            case "user" -> users_function(Arrays.copyOfRange(args, 2, args.length));
            default ->  log.error("tag is incorrect");
        }


    }

    public static void users_function(String[] args){
        switch (args[0].toLowerCase()) {
            case "insert" -> insert(Arrays.copyOfRange(args, 1, args.length));
            case "delete" -> delete(Arrays.copyOfRange(args, 1, args.length));
            case "get" -> get(Arrays.copyOfRange(args, 1, args.length));
            case "get_all" -> get_all();
            case "delete_all" -> delete_all();
            case "perform_action" -> perform_action(Arrays.copyOfRange(args, 1, args.length));
            default -> log.info("Function is incorrect");
        }
    }

    public static void insert(String[] args){
        Optional<String> userId = data.appendUser(args[0]);
        if (userId.isEmpty())
            log.error("User has been created");
        else
            log.info("Your id : {}", userId.get());
    }

    public static void delete(String[] args){
        Optional<User> user = data.deleteUserById(args[0]);
        if (user.isEmpty())
            log.error("Failed to delete user by id {}", args[0]);
        else
            log.info("User has been deleted");
    }

    public static void get_all(){
        Result<User> userResult = data.getUsers();
        userResult.getBody().forEach(log::info);
        log.info(userResult.getMessage());
    }

    public static void delete_all(){
        Result<User> userResult = data.deleteAllUsers();
        userResult.getBody().forEach(log::info);
        log.info(userResult.getMessage());
    }

    public static void get(String [] args){
        Optional<User> user = data.getUserById(args[0]);
        if (user.isPresent() )
            log.info(user.get());
        else
            log.info("User wasn't found by id {}", args[0]);
    }

    public static void perform_action(String[] args){
        if(data.performActon(args[0], args[1], args[2]))
            log.info("Action has been performed");
        else
            log.error("Fail to perform action");
    }





}