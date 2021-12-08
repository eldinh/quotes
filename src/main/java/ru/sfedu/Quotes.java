package ru.sfedu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.*;
import ru.sfedu.entity.User;


import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Quotes {
    private final static Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMAN);
    public static  void main(String[] args) throws Exception {
//        IDateProvider data = new DataProviderCSV();
//        Result<Stock> stockResult;
//        stockResult = data.getStocks();
//        Result<Bond> bondResult;
//        bondResult = data.getBonds();
//        log.info(stockResult.getStatus());
//        log.info(stockResult.getMessage());
//        stockResult.getBody().forEach(log::info);
//
//        log.info(bondResult.getStatus());
//        log.info(bondResult.getMessage());
//        bondResult.getBody().forEach(log::info);

        List<User> users = new ArrayList<>(Arrays.asList(
                new User(0, "Andrew", 29)
                , new User(1, "NorAdeww", 10)
                , new User(3, "Dinh", 19)
                , new User(2, "Rodion", 19)
                , new User(5, "Danil", 19)
                , new User(7, "Artem", 19)
                , new User(8, "Sanya", 19)
        ));

        List<User> users1 = new ArrayList<>(Arrays.asList(
                new User(3, "Yes", 29)
                , new User(4, "NOooo", 10)
        ));

        List<User> users2 = new ArrayList<>(Arrays.asList(
                new User("Anton", 20)
                , new User( "Hello", 100)
        ));





    }
}