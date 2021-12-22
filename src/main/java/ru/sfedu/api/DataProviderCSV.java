package ru.sfedu.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import ru.sfedu.model.*;
import ru.sfedu.utils.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static ru.sfedu.model.CommandType.*;
import static ru.sfedu.model.RepositoryType.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.Constants.*;

public class DataProviderCSV implements DataProvider {
    private final Logger log = (Logger) LogManager.getLogger(DataProviderCSV.class.getName());

    private <T extends Security> List<String> getSecurityTicker(List<T> securities){
        return new ArrayList<>(securities.stream().map(T::getTicker).toList());
    }

    private CSVWriter getCSVWriter(String filename, String extraPath, boolean append) throws Exception {
        log.info("Starting DataProviderCSV getCSVWriter[0]");
        try {
            log.debug("getCSVWriter[1]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("getCSVWriter[2]: Creating FileWriter");
            Files.createDirectories(Paths.get(getConfigurationEntry(CSV_PATH).concat(extraPath)));
            FileWriter writer = new FileWriter(getConfigurationEntry(CSV_PATH).concat(extraPath)
                    .concat(filename)
                    .concat(getConfigurationEntry(CSV_FILE_EXTENTION)), append);
            log.debug("getCSVWriter[3]: Creating CSVWriter");
            return new CSVWriter(writer);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSVWriter had failed[4]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private CSVWriter getCSVWriter(String filename, boolean append) throws Exception {
        return getCSVWriter(filename, "",append);
    }
    private <T> StatefulBeanToCsvBuilder<T> getBeanToCSVBuilder(CSVWriter writer) throws Exception {
        log.info("Starting DataProviderCSV getBeanToCSVBuilder[5]");
        try{
            log.info("getBeanToCSVBuilder[6]: writer - {}", writer);
            return new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)   //
                    .withLineEnd(writer.DEFAULT_LINE_END);
        }catch (Exception e){
            log.error("Function DataProviderCSV getBeanToCSVBuilder had failed[7]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private <T> void write(List<T> pojoList, String filename, boolean append) throws Exception {
        log.info("Starting DataProviderCSV write[8]");
        try {
            log.info("write[9]: pojoList - {}, filename - {}", pojoList, filename);
            log.debug("write[10]: Creating csvWriter");
            CSVWriter csvWriter = getCSVWriter(filename, append);
            log.debug("write[11]: Creating StatefulBean");
            StatefulBeanToCsvBuilder<T> beanToCsvBuilder = getBeanToCSVBuilder(csvWriter);
            StatefulBeanToCsv<T> beanToCsv = beanToCsvBuilder.build();
            log.debug("write[12]: Writing to csv file");
            beanToCsv.write(pojoList);
            log.debug("write[13]: Closing CSVWriter");
            csvWriter.close();
            log.debug("write[14]: Writing history");
        } catch (Exception e){
            log.error("Function DataProviderCSV write had failed[14]: {}",e.getMessage());
            throw new Exception(e);
        }
    }

    private <T> void write(List<T> pojoList, Class<T> pojoClass) throws Exception {
        write(pojoList, pojoClass.getSimpleName().toUpperCase(), false);
    }

    private <T> void write(List<T> pojoList, Class<T> pojoClass, boolean append) throws Exception {
        write(pojoList, pojoClass.getSimpleName().toUpperCase(), append);
    }

    private <T> void write(T pojo, String filename, boolean append) throws Exception{
        write(new ArrayList<>(List.of(pojo)), filename, append);
    }

    private <T> void write(T pojo, Class<T> pojoClass, boolean append) throws Exception {
        write(pojo, pojoClass.getSimpleName().toUpperCase(), append);
    }

    private CSVReader getCSWReader(String filename, String extraPath) throws Exception {
        log.info("Starting DataProviderCSV getCSVReader[15]");
        try {
            log.info("getCSVWriter[16]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("Creating FileReader[17]");
            FileReader reader = new FileReader(getConfigurationEntry(CSV_PATH)
                    .concat(extraPath).concat(filename)
                    .concat(getConfigurationEntry(CSV_FILE_EXTENTION)));
            log.debug("Creating CSWReader[18]");
            return new CSVReader(reader);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSWReader had failed[19]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private <T> CsvToBeanBuilder<T> getCsvToBeanBuilder(String filename, String extraPath) throws Exception {
        log.info("Starting DataProviderCSV getCsvToBeanBuilder[20]");
        try
        {
            log.info("getCsvToBeanBuilder[21]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("Creating CSVReader[22]");
            CSVReader reader = getCSWReader(filename,extraPath);
            log.debug("Creating CsvToBeanBuilder[23]");
            return new CsvToBeanBuilder<>(reader);
        }catch (Exception e){
            log.error("Function DataProviderCSV CsvToBeanBuilder had failed[24]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private <T> CsvToBeanBuilder<T> getCsvToBeanBuilder(Class<T> bean) throws Exception {
        return getCsvToBeanBuilder(bean.getSimpleName().toUpperCase(), "");
    }

    private <T> List<T> read(Class<T> pojoClass) throws Exception {
        log.info("Starting DataProviderCSV read[25]");
        try {
            log.info("write[26]: pojoClass - {}", pojoClass);
            log.debug("read[27]: creating csvToBean");
            CsvToBeanBuilder<T> csvToBeanBuilder = getCsvToBeanBuilder(pojoClass);
            CsvToBean<T> csvToBean = csvToBeanBuilder
                    .withType(pojoClass)
                    .build();
            log.debug("Parse CSVToBean[28]");
            return new ArrayList<>(csvToBean.parse());
        }catch (Exception e){
            log.error("Function read had failed[29]: {}", e.getMessage());
            throw new Exception(e);
        }
    }


    // market
    @Override
    public boolean appendOrUpdateMarket(MarketType marketType){
        log.info("Starting DataProviderCSV appendOrUpdateMarket[]");
        try {
            Validator.isValid(marketType);
            log.info("appendOrUpdateMarket[]: marketType - {}", marketType);
            log.debug("appendOrUpdateMarket[]: Getting all markets");
            List<Market> markets = new ArrayList<>(getMarkets().getBody().stream().filter(x -> !x.getMarketType().equals(marketType)).toList());
            Market market = null;
            switch (marketType){
                case SHARES -> market = new Market(MarketType.SHARES, getStocks().getBody().stream().map(Security::getTicker).toList());
                case BONDS -> market = new Market(MarketType.BONDS, getBonds().getBody().stream().map(Security::getTicker).toList());
            }
            log.debug("appendOrUpdateMarket[]: Updating markets");
            markets.add(market);
            write(markets, Market.class, false);
            return true;
        }catch (Exception e){
            log.error("Function DataProviderCSV appendOrUpdateMarket had failed[]: {}", e.getMessage());
        }
        return false;
    }
    @Override
    public Result<Market> getMarkets(){
        log.info("Starting DataProviderCSV getMarkets");
        try {
            log.debug("getMarkets[]: Getting all markets from file");
            List<Market> markets = read(Market.class);
            return new Result<>(SUCCESS, String.format("Number of markets: %d", markets.size()), markets);
        }catch (Exception e){
            log.error("Function DataProviderCSV getMarkets had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }
    @Override
    public Optional<Market> getMarket(MarketType marketType){
        log.info("Starting DataProviderCSV getMarket");
        try {
            Validator.isValid(marketType);
            log.info("getMarket[]: marketType - {}", marketType);
            log.debug("getMarket[]: Getting all markets");
            List<Market> markets = read(Market.class);
            log.debug("getMarket[]: Getting {}", marketType);
            return markets.stream().filter(x -> x.getMarketType().equals(marketType)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getMarket had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    // securities

    private <T extends Security> Result<T> appendSecurities(List<T> list, Class<T> security, MarketType marketType) {
        log.info("Starting DataProviderCSV appendSecurities[]");
        List<T> oldList = new ArrayList<>(getSecurities(security).getBody());
        try
        {
            Validator.isValidSecurity(list);
            log.info("appendSecurities[]: list - {}, security - {}", list, security);
            List<String> tickerList = oldList.stream().map(T::getTicker).toList();
            log.debug("appendSecurities[]: Getting securities to append");
            List<T> securityToAppend = list.stream().filter(x -> !tickerList.contains(x.getTicker())).toList();
            List<T> response = list.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            log.debug("appendSecurities[]: Writing to csv file");
            write(securityToAppend, security, true);
            securityToAppend.forEach(x -> appendOrUpdate(x.getHistory(), x.getTicker()));
            appendOrUpdateMarket(marketType);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV appendSecurities had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    private <T extends Security> Result<T> getSecurities(Class<T>securityClass) {
        log.info("Starting DataProviderCSV getSecurities[]");
        try
        {
            log.info("getSecurities[]: securityClass - {}", securityClass);
            List<T> response = new ArrayList<>(read(securityClass));
            return new Result<>(SUCCESS, String.format("Number of securities in file: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV getSecurities had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, Class<T> securityClass)  {
        log.info("Starting DataProviderCSV deleteSecurityByTicker[]");
        try {
            log.info("deleteSecurityByTicker[]: ticker - {}, securityClass - {}",ticker, securityClass);
            log.debug("deleteSecurityByTicker[]: GetSecurity from csv file[38]");
            List<T> securities = new ArrayList<>(read(securityClass));
            log.debug("deleteSecurityByTicker[]: Search a {} by ticker {}",securityClass.getSimpleName(), ticker);
            Optional<T> sec = securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(sec.isPresent()){
                securities.remove(sec.get());
                write(securities, securityClass);
                deleteAllSecurityHistories(ticker);
                MongoHistory.save(DELETE, CSV, sec.get());
            } else
                log.warn("deleteSecurityByTicker[]: {} wasn't found by ticker {}",securityClass.getSimpleName(), ticker);
            return sec;
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteSecurityByTicker has crashed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private <T extends Security> Result<T> deleteAllSecurities(Class<T> securityClass)  {
        log.info("Starting DataProviderCSV deleteAllSecurities[]");
        try {
            log.info("deleteAllSecurities[]: securityClass - {}", securityClass);
            log.debug("deleteAllSecurities[]: get securities from file");
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("deleteAllSecurities[]: delete all securities");
            write(new ArrayList<>(), securityClass);
            deleteAllSecurityHistories(securityList.stream().map(T::getTicker).toList());
            MongoHistory.save(DELETE, CSV, securityList);
            return new Result<>(SUCCESS, String.format("Number of deleted securities: %d", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllSecurities had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class<T> securityClass)  {
        log.info("Starting DataProviderCSV getSecurityByTicker[]");
        log.info("getSecurityByTicker[]: ticker - {}, securityClass - {}", ticker, ticker.getClass());
        try {
            log.debug("getSecurityByTicker[]: Get {} from CSV[]", securityClass.getSimpleName());
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("getSecurityByTicker[]: Search for a {} by ticker {}[]", securityClass.getSimpleName(), ticker);
            return securityList.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getSecurityByTicker had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private <T extends Security> Result<T> updateSecurities(List<T> securities, Class<T> securityClass)  {
        log.info("Starting DataProviderCSV updateSecurities[]");
        try {
            Validator.isValidSecurity(securities);
            log.info("updateSecurities[]: securities - {}, securityClass - {}", securities, securityClass);
            List<T> oldList = new ArrayList<>(read(securityClass));
            List<String> tickerList = getSecurityTicker(oldList);
            List<T> response = new ArrayList<>(securities.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
            log.debug("updateSecurities[]: Update csv file: {}", securityClass.getSimpleName());
            List<T> securityToUpdate = securities.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            write(Stream.concat(securityToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(T::getTicker)).toList(), securityClass);
            securityToUpdate.forEach(x -> appendOrUpdate(x.getHistory(), x.getTicker()));
            MongoHistory.save(UPDATE, CSV, securityToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been updated successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Result<T> getSecuritiesByTickerList(List<String> tickers, Class<T> securityClass){
        log.info("Starting DataProviderCSV getSecuritiesByTickerList[]");
        try {
            Validator.isValid(tickers);
            log.info("getSecuritiesByTickerList[]: tickers - {}, securityClass - {}", tickers, securityClass);
            log.debug("getSecuritiesByTickerList[]: Getting all tickers");
            List<T> securityList = getSecurities(securityClass).getBody();
            log.debug("getSecuritiesByTickerList[]: Filtering security list");
            List<T> response = new ArrayList<>(securityList.stream().filter(x -> tickers.contains(x.getTicker())).toList());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderCSV had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Stock> updateStocks(List<Stock> list)  {
        return updateSecurities(list, Stock.class);
    }

    @Override
    public Result<Bond> updateBonds(List<Bond> list)  {
        return updateSecurities(list, Bond.class);
    }

    @Override
    public Result<Stock> appendStocks(List<Stock> list)  {
        return appendSecurities(list, Stock.class, MarketType.SHARES);

    }

    @Override
    public Result<Bond> appendBonds(List<Bond> list) {
        return appendSecurities(list, Bond.class, MarketType.BONDS);
    }

    @Override
    public Result<Stock> getStocks()  {
        return getSecurities(Stock.class);
    }

    @Override
    public Result<Bond> getBonds()  {
        return getSecurities(Bond.class);
    }

    @Override
    public Optional<Stock> deleteStockByTicker(String ticker)  {
        return deleteSecurityByTicker(ticker, Stock.class);
    }

    @Override
    public Optional<Bond> deleteBondByTicker(String ticker)  {
        return deleteSecurityByTicker(ticker, Bond.class);
    }

    @Override
    public Result<Stock> deleteAllStocks()  {
        return deleteAllSecurities(Stock.class);
    }

    @Override
    public Result<Bond> deleteAllBonds()  {
        return deleteAllSecurities(Bond.class);
    }

    @Override
    public Optional<Stock> getStockByTicker(String ticker)  {
        return getSecurityByTicker(ticker, Stock.class);
    }

    @Override
    public Optional<Bond> getBondByTicker(String ticker)  {
        return getSecurityByTicker(ticker, Bond.class);
    }

    public Result<Stock> getStocksByTickerList(List<String> ticker){
        return getSecuritiesByTickerList(ticker, Stock.class);
    }

    public Result<Bond> getBondsByTickerList(List<String> ticker){
        return getSecuritiesByTickerList(ticker, Bond.class);
    }

    public Result<Security> getSecuritiesByTickerList(List<String> tickers){
        log.info("Starting DataProviderCSV getSecuritiesByTickerList[]");
        try {
            log.info("getSecuritiesByTickerList[]: {}", tickers);
            log.debug("getSecuritiesByTickerList[]: Getting securities from markets");
            List<Security> response = new ArrayList<>();
            response.addAll(getStocksByTickerList(tickers).getBody());
            response.addAll(getBondsByTickerList(tickers).getBody());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderCSV getSecuritiesByTickerList had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    //security history

    private void writeHistory(List<SecurityHistory> securityList, String ticker, boolean append) throws Exception {
        log.info("Starting DataProviderCSV write[]");
        try {
            log.info("writeHistory[]: securityList - {}, ticker - {}", securityList, ticker);
            log.debug("writeHistory[]: Creating csvWriter[]");
            CSVWriter csvWriter = getCSVWriter(ticker.toUpperCase(), SECURITY_HISTORY_PATH, append);
            log.debug("writeHistory[]: Creating StatefulBean");
            StatefulBeanToCsvBuilder<SecurityHistory> beanToCsvBuilder = getBeanToCSVBuilder(csvWriter);
            StatefulBeanToCsv<SecurityHistory> beanToCsv = beanToCsvBuilder.build();
            log.debug("writeHistory[]: Writing to csv file");
            beanToCsv.write(securityList);
            log.debug("writeHistory[]: Closing CSVWriter");
            csvWriter.close();
        } catch (Exception e){
            log.error("Function DataProviderCSV writeHistory had failed[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private List<SecurityHistory> readHistory(String ticker) throws Exception {
        log.info("Starting DataProviderCSV readHistory[]");
        try {
            log.info("readHistory[]: ticker - {}", ticker);
            log.debug("readHistory[]: creating csvToBean");
            CsvToBeanBuilder<SecurityHistory> csvToBeanBuilder = getCsvToBeanBuilder(ticker, SECURITY_HISTORY_PATH);
            CsvToBean<SecurityHistory> csvToBean = csvToBeanBuilder
                    .withType(SecurityHistory.class)
                    .build();
            log.debug("readHistory:[] Parse CSVToBean");
            return csvToBean.parse();
        }catch (Exception e){
            log.error("Function readHistory had failed[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public Result<SecurityHistory> appendSecurityHistory(List<SecurityHistory> securityHistories, String ticker){
        List<SecurityHistory> oldList = getSecurityHistories(ticker).getBody();
        log.info("Starting DataProviderCSV appendSecuritiesHistory");
        try {
            log.info("appendSecuritiesHistory[]: securityHistories - {}, ticker - {}", securityHistories, ticker);
            Validator.isValidSecurityHistory(securityHistories, ticker);
            log.debug("appendSecuritiesHistory[]: Data filtering");
            List<String> dateList = oldList.stream().map(SecurityHistory::getDate).toList();
            List<SecurityHistory> historyToAppend = securityHistories.stream().filter(x -> !dateList.contains(x.getDate())).toList();
            List<SecurityHistory> response = securityHistories.stream().filter(x -> dateList.contains(x.getDate())).toList();
            log.debug("appendSecuritiesHistory[]: write to file");
            writeHistory(historyToAppend, ticker, true);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "SecurityHistories have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of SecurityHistories that haven't been appended: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurityHistory had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<SecurityHistory> getSecurityHistories(String ticker) {
        log.info("Starting DataProviderCSV getSecurityHistories[]");
        try {
            List<SecurityHistory> securityHistories = readHistory(ticker);
            return new Result<>(SUCCESS, String.format("Number of histories in file: %d", securityHistories.size()), securityHistories);
        } catch (Exception e) {
            log.error("Function DataProviderCSV getSecurityHistory had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public SecurityHistory getSecurityHistoryByDate(String ticker, String date){
        log.info("Starting DataProviderCSV getSecurityHistoryByDate[]");
        try {
            log.info("getSecurityHistoryByDate[]: ticker - {}, date - {}", ticker, date);
            log.debug("getSecurityHistoryByDate[]: security story search by date");
            Optional<SecurityHistory> securityHistory = readHistory(ticker).stream().filter(x -> x.getDate().equals(date)).findFirst();
            if(securityHistory.isPresent())
                return securityHistory.get();
        }catch (Exception e){
            log.error("Function DataProviderCSV getSecurityHistoryByDate had failed: {}", e.getMessage());
        }
        return new SecurityHistoryBuilder().empty(date, ticker);
    }

    @Override
    public SecurityHistory getSecurityHistoryByDate(String ticker){
        return getSecurityHistoryByDate(ticker, DATE);
    }

    @Override
    public Result<SecurityHistory> deleteAllSecurityHistories(String ticker){
        log.info("Starting DataProviderCSV deleteAllSecurityHistories[]");
        try {
            log.info("deleteAllSecurityHistories[]: ticker - {}", ticker);
            Result<SecurityHistory> securityHistoryResult = getSecurityHistories(ticker);
            if (securityHistoryResult.getStatus().equals(FAIL))
                throw new Exception(String.format("Database %s wasn't found", ticker));
            log.debug("deleteAllSecurityHistories[]: Delete all security histories");
            File file = new File(getConfigurationEntry(CSV_PATH)
                    .concat(SECURITY_HISTORY_PATH).concat(ticker)
                    .concat(getConfigurationEntry(CSV_FILE_EXTENTION)));
            if (!file.delete())
                throw new Exception("Processing of deleting had failed");
            return new Result<>(SUCCESS, String.format("Num of deleted history: %d", securityHistoryResult.getBody().size()), securityHistoryResult.getBody());
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllSecurityHistories had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }

    }

    @Override
    public void deleteAllSecurityHistories(List<String> tickerList){
        tickerList.forEach(this::deleteAllSecurityHistories);
    }

    @Override
    public boolean appendOrUpdate(SecurityHistory securityHistory, String ticker){
        log.info("Starting DataProviderCSV appendOrUpdate[]");
        try {
            Validator.isValidSecurityHistory(securityHistory, ticker);
            log.info("appendOrUpdate[]: securityHistory - {},  ticker - {}",securityHistory, ticker);
            log.debug("appendOrUpdate[]: Get all histories");
            List<SecurityHistory> securityHistoryList = getSecurityHistories(ticker).getBody();
            log.debug("appendOrUpdate[]: Filter the list, to find securityHistory to update");
            Optional<SecurityHistory> sec = securityHistoryList.stream().filter(x -> x.getDate().equals(securityHistory.getDate())).findFirst();
            sec.ifPresent(securityHistoryList::remove);
            securityHistoryList.add(securityHistory);
            log.debug("appendOrUpdate[]: update history file");
            writeHistory(securityHistoryList, ticker, false);
            return true;
        }catch (Exception e){
            log.error("Function DataProviderCSV appendOrUpdate had failed: {}", e.getMessage());
        }
        return false;
    }



    // user

    public static List<String> getUsersId(List<User> users){
        return users.stream().map(User::getId).toList();
    }

    public Result<User> appendUser(User user){
        log.info("Starting DataProviderCSV appendUser[]");
        try {
            Validator.isValid(user);
            log.info("appendUser[]: user - {}", user);
            return appendUsers(new ArrayList<>(List.of(user)));
        }catch (Exception e){
            log.error("Function DataProviderCSV appendUser had failed");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public Result<User> appendUsers(List<User> users){
        log.info("Starting DataProviderCSV appendUsers[]");
        try {
            Validator.isValidUser(users);
            log.info("appendUsers[]: users - {}", users);
            log.debug("appendUsers[]: Getting all users");
            List<String> usersId = getUsersId(getUsers().getBody());
            log.debug("appendUsers[]: Filtering users");
            List<User> userToAppend = users.stream().filter(x -> !usersId.contains(x.getId())).toList();
            List<User> response = users.stream().filter(x -> usersId.contains(x.getId())).toList();
            log.debug("appendUsers[]: Writing to file");
            write(userToAppend, User.class, true);
            if (response.size() != 0)
                return new Result<>(WARN, String.format("Users that weren't added %d ", response.size()), response);
            return new Result<>(SUCCESS, "User was appended successfully", response);
        }catch (Exception e){
            log.error("Function DataProviderCSV appendUsers had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public Result<User> getUsers(){
        log.info("Starting DataProviderCSV getUsers[]");
        try {
            log.debug("getUsers[]: Getting users from csv");
            List<User> users = read(User.class);
            return new Result<>(SUCCESS, String.format("Number of users: %d", users.size()), users);
        }catch (Exception e){
            log.error("Function DataProviderCSV getUsers had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    // update active ?
    public Result<User> updateUsers(List<User> users){
        log.info("Starting DataProviderCSV updateUsers[]");
        try {
            Validator.isValidUserToUpdate(users);
            log.info("updateUsers[]: users - {}", users);
            log.debug("updateUsers[]: Getting users from db");
            List<User> userFromDb = read(User.class);
            List<String> idList = getUsersId(userFromDb);
            log.debug("updateUsers[]: Filtering users");
            List<User> usersToAppend = Stream.concat(users.stream().filter(x -> idList.contains(x.getId()))
                    , userFromDb.stream()).distinct().toList();
            log.debug("updateUsers[]: Writing to file");
            write(usersToAppend, User.class);
            List<User> response = users.stream().filter(x -> !idList.contains(x.getId())).toList();
            if (response.size() != 0)
                return new Result<>(WARN, String.format("Number of users that weren't updated: %d", response.size()), response);
            return new Result<>(SUCCESS, "Users were updated successfully", response);
        }catch (Exception e){
            log.error("Function DataProviderCSV updateUsers had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public Result<User> updateUser(User user){
        log.info("Starting DataProviderCSV updateUser[]");
        try {
            Validator.isValid(user);
            log.info("updateUser[]: user - {}", user);
            return updateUsers(new ArrayList<>(List.of(user)));
        }catch (Exception e){
            log.error("Function DataProviderCSV updateUser had failed");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public Optional<User> getUserById(String id){
        log.info("Starting DataProviderCSV getUserById[]");
        try {
            Validator.isValid(id);
            log.info("getUserById[]: id - {}", id);
            log.debug("getUserById[]: Getting all users");
            List<User> users = read(User.class);
            log.debug("getUserById[]: Get user by id {}", id);
            return users.stream().filter(x -> x.getId().equals(id)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getUserById had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<User> deleteUserById(String id){
        log.info("Starting DataProviderCSV deleteUserById[]");
        try {
            Validator.isValid(id);
            log.info("deleteUserById[]: id - {}", id);
            log.debug("deleteUserById[]: Getting all users");
            List<User> users = read(User.class);
            log.debug("deleteUserById[]: Deleting user with id {}", id);
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            if (user.isPresent()) {
                users.remove(user.get());
                // delete user active
                write(users, User.class);
                return user;
            }
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteUserById had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public Result<User> deleteAllUsers(){
        log.info("Starting DataProviderCSV deleteAllUsers[]");
        try {
            log.debug("deleteAllUsers[]: Getting all users");
            List<User> users = read(User.class);
            log.debug("deleteAllUsers[]: Deleting all users");
            write(new ArrayList<>(), User.class);
            // delete all active
            return new Result<>(SUCCESS, String.format("Number of deleted users: %d", users.size()), users);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllUsers had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    // action

    public boolean appendAction(Action action){
        log.info("Starting DataProviderCSV appendAction[]");
        try {
            Validator.isValidAction(action);
            log.info("appendAction[]: action - {}", action);
            write(action, Action.class, true);
            return true;
        }catch (Exception e){
            log.error("Function DataProviderCSV appendAction had failed[]: {}", e.getMessage());
        }
        return false;
    }

    public Optional<String> appendAction(ActionType actionType, String userID, Security security){
        log.info("Starting DataProviderCSV appendAction[]");
        try {
            log.info("appendAction[]: actionType - {}, userID - {}, security - {}", actionType, userID, security);
            Action action = new ActionBuilder().withAction(actionType).withDate(DATE).withUserID(userID).withSecurity(security).build();
            write(action, Action.class, true);
            return Optional.of(action.getId());
        }catch (Exception e){
            log.error("Function DataProviderCSV appendAction had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }


    public Result<Action> getActionHistory(String userID){
        log.info("Starting DataProviderCSV getActionHistory");
        try {
            Validator.isValid(userID);
            log.info("getActionHistory[]: userID - {}", userID);
            log.debug("getActionHistory[]: Getting all actions");
            List<Action> actions = new ArrayList<>(read(Action.class).stream().filter(x -> x.getUserID().equals(userID)).toList());
            return new Result<>(SUCCESS, String.format("Number of actions: %d", actions.size()), actions);
        }catch (Exception e){
            log.error("Function DataProviderCSV getActionHistory had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public Result<Action> deleteActionHistory(String userID){
        log.info("Starting DataProviderCSV deleteAllUserAction[]");
        try {
            Validator.isValid(userID);
            log.info("deleteAllUserAction[]: userID - {}", userID);
            log.debug("deleteAllUserAction[]: Getting all actions");
            List<Action> actions = read(Action.class);
            log.debug("deleteAllUserAction[]: Getting all user's {}  actions", userID);
            List<Action> actionToDelete = new ArrayList<>(actions.stream().filter(x -> x.getUserID().equals(userID)).toList());
            actions.removeAll(actionToDelete);
            write(actions, Action.class);
            return new Result<>(SUCCESS, String.format("Number of deleted actions: %d", actionToDelete.size()), actionToDelete);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllUserAction had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }
}
