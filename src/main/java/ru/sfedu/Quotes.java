package ru.sfedu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProvider;
import ru.sfedu.api.DataProviderCsv;
import ru.sfedu.api.DataProviderJdbc;
import ru.sfedu.api.DataProviderXml;
import ru.sfedu.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ru.sfedu.Constants.DATE;
import static ru.sfedu.Constants.SUCCESS;


public class Quotes {
    private static final Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    public static String USER_ID = "0";
    public static DataProvider data;
    private static final List<Stock> stocks = new ArrayList<>(Arrays.asList(
            new StockBuilder()
                    .withCapitalization(100000).withDividendSum(1000).withTicker("SBER")
                    .withName("SBERBANK").withShortName("SBER").withLatName("Сбербанк")
                    .withNominal(100).withSecurityHistory(getHistories("SBER"))
                    .withNominalValue("USD").withIssueDate("2021").withType(StockType.COMMON).withIsin("RU0009029540")
                    .build(),
            new StockBuilder()
                    .withCapitalization(20000).withDividendSum(2000).withTicker("SBERP")
                    .withName("SBERBANK_P").withShortName("SBER_P").withLatName("Сбербанк_Привилегированный").withIsin("RU0009029540")
                    .withNominal(100).withSecurityHistory(getHistories("SBERP"))
                    .withNominalValue("USD").withIssueDate("2021").withType(StockType.PREFERRED)
                    .build(),
            new StockBuilder()
                    .withCapitalization(1000000).withDividendSum(2200).withTicker("MGNT")
                    .withName("MAGNIT").withShortName("MGNT").withLatName("Магнит").withIsin("RU0009029540")
                    .withNominal(100).withSecurityHistory(getHistories("MGNT"))
                    .withNominalValue("USD").withIssueDate("2021").withType(StockType.COMMON)
                    .build(),
            new StockBuilder()
                    .withCapitalization(2321410).withDividendSum(321410).withTicker("GAZP")
                    .withName("GAZPROM").withShortName("GAZP").withLatName("Газпром").withIsin("RU0009029540")
                    .withNominal(100).withSecurityHistory(getHistories("GAZP"))
                    .withNominalValue("USD").withIssueDate("2021").withType(StockType.COMMON)
                    .build(),
            new StockBuilder()
                    .withCapitalization(100000).withDividendSum(20000).withTicker("YNDX")
                    .withName("YANDEX").withShortName("YNDX").withLatName("Яндекс").withIsin("RU0009029540")
                    .withNominal(100).withSecurityHistory(getHistories("YNDX"))
                    .withNominalValue("USD").withIssueDate("2021").withType(StockType.COMMON)
                    .build()
    ));


    private static final List<Bond> bonds = new ArrayList<>(Arrays.asList(
            new BondBuilder()
                    .withTicker("GAZPBOND")
                    .withName("Gazprom capital LLC BO-03").withShortName("GAZPROM").withLatName("Газпром капитал ООО БО-03")
                    .withNominal(100).withSecurityHistory(getHistories("GAZPBOND")).withIsin("RU0009029540").withMatDate("")
                    .withNominalValue("RUB").withIssueDate("2022").withType(BondType.CB).withCoupon(35.65).withDayToRedemption(300)
                    .build(),
            new BondBuilder()
                    .withTicker("TNKFBOND")
                    .withName("Tinkoff Bank BO 001P-02R").withShortName("Tinkoff").withLatName("Тинькофф Банк БО 001Р-02R")
                    .withNominal(100).withSecurityHistory(getHistories("TNKFBOND")).withIsin("RU0009029540").withMatDate("")
                    .withNominalValue("RUB").withIssueDate("2023").withType(BondType.CB).withCoupon(46.12).withDayToRedemption(700)
                    .build(),
            new BondBuilder()
                    .withTicker("SBERBOND").withName("Sber 001P-SBER24").withShortName("SBERBANK").withMatDate("")
                    .withLatName("Сбербанк ПАО 001Р-SBER24").withNominal(1234).withSecurityHistory(getHistories("SBERBOND"))
                    .withNominalValue("RUB").withIssueDate("2023").withType(BondType.CB).withIsin("RU0009029540")
                    .withCoupon(33.16).withDayToRedemption(600)
                    .build(),
            new BondBuilder()
                    .withTicker("BNFTBOND")
                    .withName("Bashneft ANK 09").withShortName("Bashneft").withLatName("ПАО АНК \"Башнефть\" об. 09")
                    .withNominal(100).withSecurityHistory(getHistories("BNFTBOND")).withIsin("RU0009029540").withMatDate("")
                    .withNominalValue("RUB").withIssueDate("2021").withType(BondType.CB).withCoupon(27.42).withDayToRedemption(3)
                    .build(),
            new BondBuilder()
                    .withTicker("KMZBOND")
                    .withName("KAMAZ PTC BO-P01").withShortName("KAMAZ").withLatName("КАМАЗ ПАО БО-П01").withMatDate("")
                    .withNominal(100).withSecurityHistory(getHistories("KMZBOND")).withIsin("RU0009029540")
                    .withNominalValue("RUB").withIssueDate("2021").withType(BondType.CB).withCoupon(22.44).withDayToRedemption(10)
                    .build()
    ));

    public static void main(String[] args){
        log.info("Starting Quotes main[0]");
        log.info("main[1]: args - {}", Arrays.toString(args));
        switch (args[0]) {
            case "csv" -> data = new DataProviderCsv();
            case "xml" -> data = new DataProviderXml();
            case "jdbc" -> data = new DataProviderJdbc();
            default -> log.warn("main[2]: data wasn't found");
        }

        switch (args[1]){
            case "init" -> init();
            case "user" -> users_function(Arrays.copyOfRange(args, 2, args.length));
            case "security" -> security_function(Arrays.copyOfRange(args, 2, args.length));
            default ->  log.warn("main[3]: tag is incorrect");
        }


    }

    public static void users_function(String[] args){
        log.info("Starting Quotes users_function[0]");
        log.info("users_function[1]: args - {}", Arrays.toString(args));
        switch (args[0].toLowerCase()) {
            case "perform_action" -> perform_action(Arrays.copyOfRange(args, 1, args.length));
            case "check_virtual_briefcase" -> check_virtual_briefcase();
            case "insert" -> insert(Arrays.copyOfRange(args, 1, args.length));
            case "update" -> update(Arrays.copyOfRange(args, 1, args.length));
            case "get_all" -> get_all();
            case "delete" -> delete(Arrays.copyOfRange(args, 1, args.length));
            case "get" -> get(Arrays.copyOfRange(args, 1, args.length));
            default -> log.warn("users_function[2]: Function is incorrect");
        }
    }

    public static void security_function(String[] args){
        log.info("Starting Quotes security_function[0]");
        log.info("security_function[1]: args - {}", Arrays.toString(args));
        switch (args[0].toLowerCase()){
            case "find_security" -> find_security(Arrays.copyOfRange(args, 1, args.length));
            default -> log.warn("Function is incorrect");
        }
    }


    public static void perform_action(String[] args){
        log.info("Starting Quotes perform_action[0]");
        log.info("perform_action[1]: args - {}", Arrays.toString(args));
        if(data.performActon(USER_ID, args[0], args[1].toUpperCase()))
            log.info("perform_action[2]: Action has been performed");
        else
            log.warn("perform_action[3]: Fail to perform action");
    }
    
    public static void check_virtual_briefcase(){
        log.info("Starting Quotes check_virtual_briefcase[0]");
        Result<Security> result = data.checkVirtualBriefCase(USER_ID);
        log.info("check_virtual_briefcase[1]: message - {}", result.getMessage());
        if (result.getStatus().equals(SUCCESS)) {
            result.getBody().forEach(log::info);
            log.info("check_virtual_briefcase[1]: history: {}", data.showHistory(USER_ID));
        }
    }

    public static void insert(String[] args){
        log.info("Starting Quotes insert[0]");
        log.info("insert[1]: args - {}", Arrays.toString(args));
        Optional<String> id = data.appendUser(args[0]);
        if (id.isPresent())
            log.info("insert[2]: User's id : {}", id.get());
        else
            log.warn("insert[3]: Fail to insert user");
    }

    public static void update(String[] args){
        log.info("Starting Quotes update[0]");
        log.info("update[1]: args - {}", Arrays.toString(args));
        Optional<User> user = data.updateUser(args[0], args[1]);
        if (user.isPresent())
            log.info("update[2]: User {} was updated", args[0]);
        else
            log.warn("update[3]: Fail to update user");
    }

    public static void get_all(){
        log.info("Starting Quotes get_all[0]");
        Result<User> result  = data.getUsers();
        log.info("get_all[1]: message - {}",result.getMessage());
        result.getBody().forEach(log::info);
    }

    public static void delete(String[] args){
        log.info("Starting Quotes delete[0]");
        log.info("delete[1]: args - {}", Arrays.toString(args));
        Optional<User> user = data.deleteUserById(args[0]);
        if (user.isPresent())
            log.info("delete[2]: User {} has been deleted", args[0]);
        else
            log.warn("delete[3]: Fail to delete user by id");
    }

    public static void get(String[] args){
        log.info("Starting Quotes get[0]");
        log.info("get[1]: args - {}", Arrays.toString(args));
        Optional<User> user = data.getUserById(args[0]);
        if (user.isPresent())
            log.info("get[2]: {}", user.get());
        else
            log.warn("get[3]: Fail to find user by id");
    }




    public static void find_security(String[] args){
        log.info("Starting Quotes find_security[0]");
        log.info("find_security[1]: args - {}", Arrays.toString(args));

        if (args[0].equals("shares") || args[0].equals("bonds"))
            data.findSecurity(MarketType.valueOf(args[0].toUpperCase())).getBody().forEach(log::info);
        else{
            Result<SecurityHistory> result = data.findSecurity(args[0].toUpperCase());
            log.info("find_security[2]: message - {}",result.getMessage());
            if (result.getStatus().equals(SUCCESS)){
                result.getBody().forEach(log::info);
                log.info("find_security[3]: {}",data.showInfo(args[0].toUpperCase()));
            }
        }
    }

    public static void init(){
        data.deleteAllBonds();
        data.deleteAllStocks();
        bonds.forEach(x -> data.deleteAllSecurityHistories(x.getTicker()));
        stocks.forEach(x -> data.deleteAllSecurityHistories(x.getTicker()));

        for (int i = 0; i < 5 ; i ++){
            data.appendSecurityHistory(setHistory(bonds.get(i).getTicker()), bonds.get(i).getTicker());
            data.appendSecurityHistory(setHistory(stocks.get(i).getTicker()), stocks.get(i).getTicker());
        }
        data.appendStocks(stocks);

        data.appendBonds(bonds);

        data.appendUsers(new ArrayList<>(List.of(new UserBuilder(USER_ID)
                .withName("user")
                .withActionHistory(new ArrayList<>())
                .withTickerList(new ArrayList<>())
                .build())));
    }

    public static SecurityHistory getHistories(String ticker){
        return new SecurityHistory(DATE, getRandomNum(100, 10000), ticker, getRandomNum(0, 100), getRandomNum(0, 100), getRandomNum(0, 1000));
    }

    public static List<SecurityHistory> setHistory(String ticker){
        List<String> dates = new ArrayList<>(List.of(
                "2021-12-15",
                "2021-11-21",
                "2021-01-12",
                "2021-12-12",
                "2021-01-01"
        ));

        return new ArrayList<>(dates.stream().map(x ->
                        new SecurityHistory(x, getRandomNum(100, 150), ticker, getRandomNum(100, 150), 13, getRandomNum(100, 1500))
                ).toList());

    }

    public static int getRandomNum(int a, int b){
        return a + (int) (Math.random() * b);
    }






}