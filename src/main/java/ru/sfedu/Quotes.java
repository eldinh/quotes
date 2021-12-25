package ru.sfedu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProvider;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.api.DataProviderJDBC;
import ru.sfedu.api.DataProviderXML;
import ru.sfedu.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.sfedu.Constants.DATE;
import static ru.sfedu.Constants.SUCCESS;


public class Quotes {
    private static final Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    public static String USER_ID = "0";
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
            case "init" -> init();
            case "user" -> users_function(Arrays.copyOfRange(args, 2, args.length));
            case "security" -> security_function(Arrays.copyOfRange(args, 2, args.length));
            default ->  log.error("tag is incorrect");
        }


    }

    public static void users_function(String[] args){
        switch (args[0].toLowerCase()) {
            case "perform_action" -> perform_action(Arrays.copyOfRange(args, 1, args.length));
            case "check_virtual_briefcase" -> check_virtual_briefcase();
            default -> log.info("Function is incorrect");
        }
    }

    public static void security_function(String[] args){
        switch (args[0].toLowerCase()){
            case "find_security" -> find_security(Arrays.copyOfRange(args, 1, args.length));
            default -> log.info("Function is incorrect");
        }
    }


    public static void perform_action(String[] args){
        if(data.performActon(USER_ID, args[0], args[1].toUpperCase()))
            log.info("Action has been performed");
        else
            log.error("Fail to perform action");
    }
    
    public static void check_virtual_briefcase(){
        Result<Security> result = data.checkVirtualBriefCase(USER_ID);
        log.info(result.getMessage());
        if (result.getStatus().equals(SUCCESS)) {
            result.getBody().forEach(log::info);
            log.info(data.showStatistics(USER_ID));
        }


    }

    public static void find_security(String[] args){
        if (args[0].equals("shares") || args[0].equals("bonds"))
            data.findSecurity(MarketType.valueOf(args[0].toUpperCase())).getBody().forEach(log::info);
        else{
            Result<SecurityHistory> result = data.findSecurity(args[0].toUpperCase());
            log.info(result.getMessage());
            if (result.getStatus().equals(SUCCESS)){
                result.getBody().forEach(log::info);
                log.info(data.showInfo(args[0]));
            }
        }
    }

    public static void init(){

         List<Stock> stocks = new ArrayList<>(Arrays.asList(
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


        List<Bond> bonds = new ArrayList<>(Arrays.asList(
                new BondBuilder()
                        .withTicker("GAZPBOND")
                        .withName("Gazprom capital LLC BO-03").withShortName("GAZPROM").withLatName("Газпром капитал ООО БО-03")
                        .withNominal(100).withSecurityHistory(getHistories("GAZPBOND")).withIsin("RU0009029540")
                        .withNominalValue("RUB").withIssueDate("2022").withType(BondType.CB).withCoupon(35.65).withDayToRedemption(300)
                        .build(),
                new BondBuilder()
                        .withTicker("TNKFBOND")
                        .withName("Tinkoff Bank BO 001P-02R").withShortName("Tinkoff").withLatName("Тинькофф Банк БО 001Р-02R")
                        .withNominal(100).withSecurityHistory(getHistories("TNKFBOND")).withIsin("RU0009029540")
                        .withNominalValue("RUB").withIssueDate("2023").withType(BondType.CB).withCoupon(46.12).withDayToRedemption(700)
                        .build(),
                new BondBuilder()
                        .withTicker("SBERBOND").withName("Sber 001P-SBER24").withShortName("SBERBANK")
                        .withLatName("Сбербанк ПАО 001Р-SBER24").withNominal(1234).withSecurityHistory(getHistories("SBERBOND"))
                        .withNominalValue("RUB").withIssueDate("2023").withType(BondType.CB).withIsin("RU0009029540")
                        .withCoupon(33.16).withDayToRedemption(600)
                        .build(),
                new BondBuilder()
                        .withTicker("BNFTBOND")
                        .withName("Bashneft ANK 09").withShortName("Bashneft").withLatName("ПАО АНК \"Башнефть\" об. 09")
                        .withNominal(100).withSecurityHistory(getHistories("BNFTBOND")).withIsin("RU0009029540")
                        .withNominalValue("RUB").withIssueDate("2021").withType(BondType.CB).withCoupon(27.42).withDayToRedemption(3)
                        .build(),
                new BondBuilder()
                        .withTicker("KMZBOND")
                        .withName("KAMAZ PTC BO-P01").withShortName("KAMAZ").withLatName("КАМАЗ ПАО БО-П01")
                        .withNominal(100).withSecurityHistory(getHistories("KMZBOND")).withIsin("RU0009029540")
                        .withNominalValue("RUB").withIssueDate("2021").withType(BondType.CB).withCoupon(22.44).withDayToRedemption(10)
                        .build()
        ));

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
        return new SecurityHistory(DATE, 123, ticker, 12, 13, 100);
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