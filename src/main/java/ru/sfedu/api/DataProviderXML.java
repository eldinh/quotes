package ru.sfedu.api;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.model.*;
import ru.sfedu.utils.Validator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static ru.sfedu.Constants.*;
import static ru.sfedu.Constants.WARN;
import static ru.sfedu.model.CommandType.*;
import static ru.sfedu.model.RepositoryType.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderXML implements DataProvider {

    private final Logger log = (Logger) LogManager.getLogger(DataProviderXML.class.getName());

    /**
     * Method for getting FileReader to read a file
     * @param filename - filename
     * @param extraPath - extra path
     * @return FileReader
     * @throws Exception - Exception
     */
    private FileReader getFileReader(String filename, String extraPath) throws Exception {
        log.info("Starting DataProviderXML getFileReader[0]");
        try {
            log.debug("getFileReader[1]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("getFileReader[2]: Creating FileReader");
            return new FileReader(getConfigurationEntry(XML_PATH)
                    .concat(extraPath).concat(filename)
                    .concat(getConfigurationEntry(XML_FILE_EXTENTION)));
        }catch (Exception e){
            log.error("Function DataProviderXML getFileWriter had failed[3]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Method for getting FileReader to read a file
     * @param ob - filename
     * @return FileReader
     * @throws Exception - Exception
     */
    private FileReader getFileReader(Class<?> ob) throws Exception {
        return getFileReader(ob.getSimpleName().toUpperCase(), "");
    }

    /**
     * Method for getting FileWriter to write pojo into file
     * @param filename - file name
     * @param extraPath - extra path
     * @return - FileWriter
     * @throws Exception - Exception
     */
    private FileWriter getFileWriter(String filename, String extraPath) throws Exception {
        log.info("Starting DataProviderXML getFileWriter[0]");
        try {
            log.debug("getFileWriter[1]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("getFileWriter[2]: Creating FileWriter");
            Files.createDirectories(Paths.get(getConfigurationEntry(XML_PATH).concat(extraPath)));
            return new FileWriter(getConfigurationEntry(XML_PATH)
                    .concat(extraPath).concat(filename)
                    .concat(getConfigurationEntry(XML_FILE_EXTENTION)), false);
        }catch (Exception e){
            log.error("Function DataProviderXML getFileWriter had failed[3]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Method for getting FileWriter to write pojo into file
     * @param pojo - file name
     * @return - FileWriter
     * @throws Exception - Exception
     */
    private FileWriter getFileWriter(Class<?> pojo) throws Exception {
        return getFileWriter(pojo.getSimpleName().toUpperCase(), "");
    }

    /**
     * Method for writing list of beans to file
     * @param list - list of beans to write
     * @param pojo - file name
     * @param <T> - Type of bean
     * @throws Exception - Exception
     */
    private <T> void write(List<T> list  , Class<T> pojo) throws Exception {
        log.info("Starting DataProviderXML write[0]");
        try {
            log.info("write[1]: list - {}, pojo - {}", Arrays.toString(list.toArray()), pojo);
            Serializer serializer = new Persister();
            log.debug("write[2]: Getting writer");
            FileWriter writer = getFileWriter(pojo);
            log.debug("write[3]: Writing to XML file[]");
            serializer.write(new Wrapper<>(list), writer);
        } catch (Exception e){
            log.error("Function DataProviderXML write[4]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Method for reading data from file and converting it into list of beans
     * @param bean - name of file
     * @param <T> - Type of bean
     * @return List<T> - List of beans
     * @throws Exception - Exception
     */
    private <T> List<T> read(Class<T> bean) throws Exception {
        log.info("Starting DataProviderXML read[0]");
        try {
            log.info("read[1]: bean - {}", bean);
            Wrapper<T> wrapper = new Wrapper<>();
            log.debug("read[2]: Get serializer");
            Serializer serializer = new Persister();
            log.debug("read[3]: Read from file");
            serializer.read(wrapper, getFileReader(bean));
            return wrapper.getContainer();
        }catch (Exception e){
            log.error("Function read had failed[4]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    //market
    @Override
    public boolean appendOrUpdateMarket(MarketType marketType){
        log.info("Starting DataProviderXML appendOrUpdateMarket[0]");
        try {
            Validator.isValid(marketType);
            log.info("appendOrUpdateMarket[1]: marketType - {}", marketType);
            log.debug("appendOrUpdateMarket[2]: Getting all markets");
            List<Market> markets = new ArrayList<>(getMarkets().getBody().stream().filter(x -> !x.getMarketType().equals(marketType)).toList());
            Market market = null;
            switch (marketType){
                case SHARES -> market = new Market(MarketType.SHARES, new ArrayList<>(getStocks().getBody()));
                case BONDS -> market = new Market(MarketType.BONDS, new ArrayList<>(getBonds().getBody()));
            }
            log.debug("appendOrUpdateMarket[3]: Updating markets");
            markets.add(market);
            write(markets, Market.class);
            return true;
        }catch (Exception e){
            log.error("Function DataProviderXML appendOrUpdateMarket had failed[4]: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public Result<Market> getMarkets(){
        log.info("Starting DataProviderXML getMarkets[0]");
        try {
            log.debug("getMarkets[1]: Getting all markets from file");
            List<Market> markets = read(Market.class);
            return new Result<>(SUCCESS, String.format("Number of markets: %d", markets.size()), markets);
        }catch (Exception e){
            log.error("Function DataProviderXML getMarkets had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Market> getMarket(MarketType marketType){
        log.info("Starting DataProviderXML getMarket[0]");
        try {
            Validator.isValid(marketType);
            log.info("getMarket[1]: marketType - {}", marketType);
            log.debug("getMarket[2]: Getting all markets");
            List<Market> markets = read(Market.class);
            log.debug("getMarket[3]: Getting {}", marketType);
            return markets.stream().filter(x -> x.getMarketType().equals(marketType)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getMarket had failed[4]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    //security
    /**
     * Method for getting security's ticker
     * @param securities - list of securities
     * @param <T> - Type of securities
     * @return List<String> - list of security's tickers
     */
    private <T extends Security> List<String> getSecurityTicker(List<T> securities){
        return new ArrayList<>(securities.stream().map(T::getTicker).toList());
    }

    /**
     * Generic method for appending security to db
     * @param list - list of securities to append
     * @param security - class that function work with(securities type)
     * @param marketType - security's market
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities that haven't been appended
     */
    private <T extends Security> Result<T> appendSecurities(List<T> list, Class<T> security, MarketType marketType) {
        log.info("Starting DataProviderXML appendSecurities[0]");
        List<T> oldList = new ArrayList<>(getSecurities(security).getBody());
        try
        {
            Validator.isValidSecurity(list);
            log.info("appendSecurities[1]: list - {}, security - {}", list, security);
            List<String> tickerList = oldList.stream().map(T::getTicker).toList();
            List<T> securityToAppend = list.stream().filter(x -> !tickerList.contains(x.getTicker())).toList();
            oldList.addAll(securityToAppend);
            oldList.sort(Comparator.comparing(T::getTicker));
            List<T> response = list.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            log.debug("appendSecurities[2]: Writing to csv file");
            write(oldList, security);
            log.debug("appendSecurities[3]: Appending security's history");
            securityToAppend.forEach(x -> appendOrUpdateSecurityHistory(x.getHistory(), x.getTicker()));
            log.debug("appendSecurities[4]: Updating or appending market: {}", marketType);
            appendOrUpdateMarket(marketType);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderXML appendSecurities had crashed[5]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    /**
     * Generic method for getting securities
     * @param securityClass - class that function work with(securities type)
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all stocks from database
     */
    private <T extends Security> Result<T> getSecurities(Class<T>securityClass) {
        log.info("Starting DataProviderXML getSecurities[0]");
        try
        {
            log.info("getSecurities[1]: securityClass - {}", securityClass);
            List<T> response = new ArrayList<>(read(securityClass));
            return new Result<>(SUCCESS, String.format("Number of securities in file: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderXML getSecurities had crashed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Generic method for deleting security by ticker
     * @param ticker - security's ticker
     * @param securityClass  - class that function work with(securities type)
     * @param <T> - Type of securities
     * @return - Optional<T extends Security> security that was deleted if it existed
     */
    private <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, Class<T> securityClass)  {
        log.info("Starting DataProviderXML deleteSecurityByTicker[0]");
        try {
            log.info("deleteSecurityByTicker[1]: ticker - {}, securityClass - {}",ticker, securityClass);
            log.debug("deleteSecurityByTicker: GetSecurity from csv file[]");
            List<T> securities = new ArrayList<>(read(securityClass));
            log.debug("deleteSecurityByTicker[2]: Search a {} by ticker {}",securityClass.getSimpleName(), ticker);
            Optional<T> sec = securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(sec.isPresent()){
                securities.remove(sec.get());
                log.debug("deleteSecurityByTicker[3]: Updating securities");
                write(securities, securityClass);
                log.debug("deleteSecurityByTicker[4]: Deleting security history");
                deleteAllSecurityHistories(ticker);
                log.debug("deleteSecurityByTicker[5]: Updating market");
                appendOrUpdateMarket(sec.get().getMarketType());
                MongoHistory.save(DELETE, CSV, sec.get());
            } else
                log.warn("deleteSecurityByTicker[6]: {} wasn't found by ticker {}",securityClass.getSimpleName(), ticker);
            return sec;
        }catch (Exception e){
            log.error("Function DataProviderXML deleteSecurityByTicker has crashed[7]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Generic method for deleting all securities
     * @param securityClass - class that function work with(securities type)
     * @param marketType - security's market
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities that have been deleted
     */
    private <T extends Security> Result<T> deleteAllSecurities(Class<T> securityClass, MarketType marketType)  {
        log.info("Starting DataProviderXML deleteAllSecurities[0]");
        try {
            log.info("deleteAllSecurities[1]: securityClass - {}, marketType - {}", securityClass, marketType);
            log.debug("deleteAllSecurities[2]: get securities from file");
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("deleteAllSecurities[3]: delete all securities");
            write(new ArrayList<>(), securityClass);
            log.debug("deleteAllSecurities[4]: deleting all security's history");
            deleteAllSecurityHistories(securityList.stream().map(T::getTicker).toList());
            log.debug("deleteAllSecurities[5]: updating market");
            appendOrUpdateMarket(marketType);
            MongoHistory.save(DELETE, CSV, securityList);
            return new Result<>(SUCCESS, String.format("Number of deleted securities: %d", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllSecurities had failed[6]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    /**
     * Generic method for getting security by id
     * @param ticker - security's ticker
     * @param securityClass - class that function work with(securities type)
     * @param <T> - Type of securities
     * @return Optional<T extends Security> - Security if it exists
     */
    private <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class<T> securityClass)  {
        log.info("Starting DataProviderXML getSecurityByTicker[0]");
        log.info("getSecurityByTicker[1]: ticker - {}, securityClass - {}", ticker, ticker.getClass());
        try {
            log.debug("getSecurityByTicker[2]: Get {} from CSV[]", securityClass.getSimpleName());
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("getSecurityByTicker[3]: Search for a {} by ticker {}[]", securityClass.getSimpleName(), ticker);
            return securityList.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getSecurityByTicker had failed[4]: {}", e.getMessage());
        }
        return Optional.empty();
    }


    /**
     * Generic method for updating securities by their id depending on their type
     * @param securities - list of securities to update
     * @param securityClass - class that function work with(securities type)
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities that haven't been updated
     */
    private <T extends Security> Result<T> updateSecurities(List<T> securities, Class<T> securityClass)  {
        log.info("Starting DataProviderXML updateSecurities[0]");
        try {
            Validator.isValidSecurity(securities);
            log.info("updateSecurities[1]: securities - {}, securityClass - {}", securities, securityClass);
            List<T> oldList = new ArrayList<>(read(securityClass));
            List<String> tickerList = getSecurityTicker(oldList);
            List<T> response = new ArrayList<>(securities.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
            log.debug("updateSecurities[2]: Updating xml file: {}", securityClass.getSimpleName());
            List<T> securityToUpdate = securities.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            write(Stream.concat(securityToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(T::getTicker)).toList(), securityClass);
            log.debug("updateSecurities[3]: Updating security's history[3]");
            securityToUpdate.forEach(x -> appendOrUpdateSecurityHistory(x.getHistory(), x.getTicker()));
            MongoHistory.save(UPDATE, CSV, securityToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been updated successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e) {
            log.error("Function DataProviderXML updateUsers had crashed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Generic function for getting securities by ticker list
     * @param tickers - ticker list
     * @param securityClass - class that function work with
     * @param <T> - Type of securities
     * @return Result<T extends Security> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of securities
     */
    private <T extends Security> Result<T> getSecuritiesByTickerList(List<String> tickers, Class<T> securityClass){
        log.info("Starting DataProviderXML getSecuritiesByTickerList[0]");
        try {
            Validator.isValid(tickers);
            log.info("getSecuritiesByTickerList[1]: tickers - {}, securityClass - {}", tickers, securityClass);
            log.debug("getSecuritiesByTickerList[2]: Getting all tickers");
            List<T> securityList = getSecurities(securityClass).getBody();
            log.debug("getSecuritiesByTickerList[3]: Filtering security list");
            List<T> response = new ArrayList<>(securityList.stream().filter(x -> tickers.contains(x.getTicker())).toList());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderXML had failed[4]: {}", e.getMessage());
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
        return deleteAllSecurities(Stock.class, MarketType.SHARES);
    }

    @Override
    public Result<Bond> deleteAllBonds()  {
        return deleteAllSecurities(Bond.class, MarketType.BONDS);
    }

    @Override
    public Optional<Stock> getStockByTicker(String ticker)  {
        return getSecurityByTicker(ticker, Stock.class);
    }

    @Override
    public Optional<Bond> getBondByTicker(String ticker)  {
        return getSecurityByTicker(ticker, Bond.class);
    }

    /**
     * Method for getting list of stocks by ticker list
     * @param ticker - ticker list
     * @return - list of stocks
     */
    private Result<Stock> getStocksByTickerList(List<String> ticker){
        return getSecuritiesByTickerList(ticker, Stock.class);
    }

    /**
     * Method for getting list of bonds by ticker list
     * @param ticker - ticker list
     * @return - list of bonds
     */
    private Result<Bond> getBondsByTickerList(List<String> ticker){
        return getSecuritiesByTickerList(ticker, Bond.class);
    }

    //
    public Result<Security> getSecuritiesByTickerList(List<String> tickers){
        log.info("Starting DataProviderXML getSecuritiesByTickerList[0]");
        try {
            Validator.isValid(tickers);
            log.info("getSecuritiesByTickerList[1]: {}", tickers);
            log.debug("getSecuritiesByTickerList[2]: Getting securities from markets");
            List<Security> response = new ArrayList<>();
            response.addAll(getStocksByTickerList(tickers).getBody());
            response.addAll(getBondsByTickerList(tickers).getBody());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderXML getSecuritiesByTickerList had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<Security> getSecurityByTicker(String ticker){
        log.info("Starting DataProviderXML getSecurityByTicker[0]");
        try {
            Validator.isValid(ticker);
            log.info("getSecurityByTicker[1]: ticker - {}", ticker);
            log.debug("getSecurityByTicker[2]: Getting security by ticker {}", ticker);
            Optional<Stock> security = getStockByTicker(ticker);
            if (security.isPresent())
                return Optional.of(security.get());
            Optional<Bond> bond = getBondByTicker(ticker);
            if (bond.isPresent())
                return Optional.of(bond.get());
        }catch (Exception e){
            log.error("Function DataProviderXML getSecurityByTicker had failed[3]: {}", e.getMessage());
        }
        return Optional.empty();
    }


    // security history
    /**
     * Method for appending security's history to file, method will create file for each security if it doesn't exist
     * @param securityList - list of security's history to append
     * @param ticker - name of file
     * @throws Exception - Exception
     */
    private void writeHistory(List<SecurityHistory> securityList, String ticker) throws Exception {
        log.info("Starting DataProviderXML writeHistory[0]");
        try {
            log.info("writeHistory[1]: securityList - {}, ticker - {}", securityList, ticker);
            Serializer serializer = new Persister();
            log.debug("writeHistory[2]: Getting writer");
            FileWriter writer = getFileWriter(ticker.toUpperCase(), SECURITY_HISTORY_PATH);
            log.debug("writeHistory[3]: Writing to XML file");
            serializer.write(new Wrapper<>(securityList), writer);
        } catch (Exception e){
            log.error("Function DataProviderXML writeHistory[4]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * Method for reading security's history file
     * @param ticker - name of file
     * @return - List<SecurityHistory> - list of security's history
     * @throws Exception - file doesn't exist
     */
    private List<SecurityHistory> readHistory(String ticker) throws Exception {
        log.info("Starting DataProviderXML readHistory[0]");
        try {
            log.info("readHistory[1]: ticker - {}", ticker);
            Wrapper<SecurityHistory> wrapper = new Wrapper<>();
            log.debug("readHistory[2]: Get serializer");
            Serializer serializer = new Persister();
            log.debug("readHistory[3]: Read from file");
            serializer.read(wrapper, getFileReader(ticker.toUpperCase(), SECURITY_HISTORY_PATH));
            return wrapper.getContainer();
        }catch (Exception e){
            log.error("Function readHistory had failed[4]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    @Override
    public Result<SecurityHistory> appendSecurityHistory(List<SecurityHistory> securityHistories, String ticker){
        List<SecurityHistory> oldList = getSecurityHistories(ticker).getBody();
        log.info("Starting DataProviderCSV appendSecuritiesHistory[0]");
        try {
            log.info("appendSecuritiesHistory[1]: securityHistories - {}, ticker - {}", securityHistories, ticker);
            Validator.isValidSecurityHistory(securityHistories, ticker);
            log.debug("appendSecuritiesHistory[2]: Data filtering");
            List<String> dateList = oldList.stream().map(SecurityHistory::getDate).toList();
            oldList.addAll(securityHistories.stream().filter(x -> !dateList.contains(x.getDate())).toList());
            oldList.sort(Comparator.comparing(SecurityHistory::getDate).reversed());
            List<SecurityHistory> response = securityHistories.stream().filter(x -> dateList.contains(x.getDate())).toList();
            log.debug("appendSecuritiesHistory[3]: Appending to file");
            writeHistory(oldList, ticker);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "SecurityHistories have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of SecurityHistories that haven't been appended: %d", response.size()), response);
        }catch (Exception e){
            log.error("Function DataProviderJDBC appendSecurityHistory had failed[4]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<SecurityHistory> getSecurityHistories(String ticker) {
        log.info("Starting DataProviderCSV getSecurityHistories[0]");
        try {
            List<SecurityHistory> securityHistories = readHistory(ticker);
            return new Result<>(SUCCESS, String.format("Number of histories in file: %d", securityHistories.size()), securityHistories);
        } catch (Exception e) {
            log.error("Function DataProviderCSV getSecurityHistory had crashed[1]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public SecurityHistory getSecurityHistoryByDate(String ticker, String date){
        log.info("Starting DataProviderCSV getSecurityHistoryByDate[0]");
        try {
            log.info("getSecurityHistoryByDate[1]: ticker - {}, date - {}", ticker, date);
            log.debug("getSecurityHistoryByDate[2]: security story search by date");
            Optional<SecurityHistory> securityHistory = readHistory(ticker).stream().filter(x -> x.getDate().equals(date)).findFirst();
            if(securityHistory.isPresent())
                return securityHistory.get();
        }catch (Exception e){
            log.error("Function DataProviderCSV getSecurityHistoryByDate had failed[3]: {}", e.getMessage());
        }
        return new SecurityHistoryBuilder().empty(date, ticker);
    }

    @Override
    public SecurityHistory getSecurityHistoryByDate(String ticker){
        return getSecurityHistoryByDate(ticker, DATE);
    }

    @Override
    public Result<SecurityHistory> deleteAllSecurityHistories(String ticker){
        log.info("Starting DataProviderCSV deleteAllSecurityHistories[0]");
        try {
            log.info("deleteAllSecurityHistories[1]: ticker - {}", ticker);
            Result<SecurityHistory> securityHistoryResult = getSecurityHistories(ticker);
            if (securityHistoryResult.getStatus().equals(FAIL))
                throw new Exception(String.format("Database %s wasn't found", ticker));
            log.debug("deleteAllSecurityHistories[2]: Delete all security histories");
            File file = new File(getConfigurationEntry(XML_PATH)
                    .concat(SECURITY_HISTORY_PATH).concat(ticker)
                    .concat(getConfigurationEntry(XML_FILE_EXTENTION)));
            if (!file.delete())
                throw new Exception("Processing of deleting had failed");
            return new Result<>(SUCCESS, String.format("Num of deleted history: %d", securityHistoryResult.getBody().size()), securityHistoryResult.getBody());
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllSecurityHistories had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }

    }

    @Override
    public void deleteAllSecurityHistories(List<String> tickerList){
        tickerList.forEach(this::deleteAllSecurityHistories);
    }

    @Override
    public boolean appendOrUpdateSecurityHistory(SecurityHistory securityHistory, String ticker){
        log.info("Starting DataProviderCSV appendOrUpdateSecurityHistory[0]");
        try {
            Validator.isValidSecurityHistory(securityHistory, ticker);
            log.info("appendOrUpdateSecurityHistory[1]: securityHistory - {},  ticker - {}",securityHistory, ticker);
            log.debug("appendOrUpdateSecurityHistory[2]: Get all histories");
            List<SecurityHistory> securityHistoryList = getSecurityHistories(ticker).getBody();
            log.debug("appendOrUpdateSecurityHistory[3]: Filter the list, to find securityHistory to update");
            Optional<SecurityHistory> sec = securityHistoryList.stream().filter(x -> x.getDate().equals(securityHistory.getDate())).findFirst();
            sec.ifPresent(securityHistoryList::remove);
            securityHistoryList.add(securityHistory);
            log.debug("appendOrUpdateSecurityHistory[4]: update history file");
            writeHistory(securityHistoryList, ticker);
            return true;
        }catch (Exception e){
            log.error("Function DataProviderCSV appendOrUpdateSecurityHistory had failed[5]: {}", e.getMessage());
        }
        return false;
    }

    //user
    /**
     * Method for getting user's id from user's list
     * @param users - list of users
     * @return list of user's id
     */
    public static List<String> getUsersId(List<User> users){
        return users.stream().map(User::getId).toList();
    }

    @Override
    public Optional<String> appendUser(String name){
        log.info("Starting DataProviderXML appendUser[0]");
        try {
            Validator.isValid(name);
            log.info("appendUser[1]: name - {}", name);
            User user = new UserBuilder().withName(name).withTickerList(new ArrayList<>())
                    .withActionHistory(new ArrayList<>()).build();
            log.debug("appendUser[2]: Appending user");
            appendUsers(new ArrayList<>(List.of(user)));
            return Optional.of(user.getId());
        }catch (Exception e){
            log.error("Function DataProviderXML appendUser had failed[3]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<User> appendUsers(List<User> users){
        log.info("Starting DataProviderXML appendUsers[0]");
        try {
            Validator.isValid(users);
            log.info("appendUsers[1]: users - {}", users);
            log.debug("appendUsers[2]: Getting all users");
            List<User> oldList = getUsers().getBody();
            List<String> usersId = getUsersId(oldList);
            log.debug("appendUsers[3]: Filtering users");
            List<User> userToAppend = users.stream().filter(x -> !usersId.contains(x.getId())).toList();
            List<User> response = users.stream().filter(x -> usersId.contains(x.getId())).toList();
            log.debug("appendUsers[4]: Writing to file");
            log.info(userToAppend);
            write(Stream.concat(oldList.stream(), userToAppend.stream()).toList(), User.class);
            if (response.size() != 0)
                return new Result<>(WARN, String.format("Users that weren't added %d ", response.size()), response);
            return new Result<>(SUCCESS, "User was appended successfully", response);
        }catch (Exception e){
            log.error("Function DataProviderXML appendUsers had failed[5]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> getUsers(){
        log.info("Starting DataProviderXML getUsers[0]");
        try {
            log.debug("getUsers[1]: Getting users from csv");
            List<User> users = read(User.class);
            return new Result<>(SUCCESS, String.format("Number of users: %d", users.size()), users);
        }catch (Exception e){
            log.error("Function DataProviderXML getUsers had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users){
        log.info("Starting DataProviderXML updateUsers[0]");
        try {
            Validator.isValidUserToUpdate(users);
            log.info("updateUsers[1]: users - {}", users);
            log.debug("updateUsers[2]: Getting users from db");
            List<User> userFromDb = read(User.class);
            List<String> idList = getUsersId(userFromDb);
            log.debug("updateUsers[3]: Filtering users");
            List<User> usersToAppend = Stream.concat(users.stream().filter(x -> idList.contains(x.getId()))
                    , userFromDb.stream()).distinct().toList();
            log.debug("updateUsers[4]: Writing to file");
            write(usersToAppend, User.class);
            List<User> response = users.stream().filter(x -> !idList.contains(x.getId())).toList();
            if (response.size() != 0)
                return new Result<>(WARN, String.format("Number of users that weren't updated: %d", response.size()), response);
            return new Result<>(SUCCESS, "Users were updated successfully", response);
        }catch (Exception e){
            log.error("Function DataProviderXML updateUsers had failed[5]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUser(User user){
        log.info("Starting DataProviderXML updateUser[0]");
        try {
            Validator.isValid(user);
            log.info("updateUser[1]: user - {}", user);
            return updateUsers(new ArrayList<>(List.of(user)));
        }catch (Exception e){
            log.error("Function DataProviderXML updateUser had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> getUserById(String id){
        log.info("Starting DataProviderXML getUserById[0]");
        try {
            Validator.isValid(id);
            log.info("getUserById[1]: id - {}", id);
            log.debug("getUserById[2]: Getting all users");
            List<User> users = read(User.class);
            log.debug("getUserById[3]: Get user by id {}", id);
            return users.stream().filter(x -> x.getId().equals(id)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getUserById had failed[4]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> deleteUserById(String id){
        log.info("Starting DataProviderXML deleteUserById[0]");
        try {
            Validator.isValid(id);
            log.info("deleteUserById[1]: id - {}", id);
            log.debug("deleteUserById[2]: Getting all users");
            List<User> users = read(User.class);
            log.debug("deleteUserById[3]: Deleting user with id {}", id);
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            if (user.isPresent()) {
                users.remove(user.get());
                log.debug("deleteUserById[4]: Deleting all actions");
                deleteActionHistory(id);
                log.debug("deleteUserById[5]: Updating users");
                write(users, User.class);
                return user;
            }
        }catch (Exception e){
            log.error("Function DataProviderXML deleteUserById had failed[6]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<User> deleteAllUsers(){
        log.info("Starting DataProviderXML deleteAllUsers[0]");
        try {
            log.debug("deleteAllUsers[1]: Getting all users");
            List<User> users = read(User.class);
            log.debug("deleteAllUsers[2]: Deleting all users");
            write(new ArrayList<>(), User.class);
            deleteAllActions();
            return new Result<>(SUCCESS, String.format("Number of deleted users: %d", users.size()), users);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllUsers had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    //action
    @Override
    public Optional<String> appendAction(ActionType actionType, String userID, String ticker){
        log.info("Starting DataProviderCSV appendAction[0]");
        try {
            log.info("appendAction[1]: actionType - {}, userID - {}, ticker - {}", actionType, userID, ticker);
            log.debug("appendAction[2]: Getting security by ticker if it exist");
            Optional<Security> security = getSecurityByTicker(ticker);
            if (security.isEmpty())
                throw new Exception(String.format("Security hasn't been found by ticker %s", ticker));
            Action action = new ActionBuilder().withAction(actionType).withDate(CURRENT_TIME).withUserID(userID).withSecurity(security.get()).build();
            Validator.isValid(action);
            log.debug("appendAction[3]: Checking for valid adding");
            if (!processAction(action))
                return Optional.empty();
            log.debug("appendAction[4]: Getting all actions");
            List<Action> actionList = getAllActions().getBody();
            actionList.add(action);
            log.debug("appendAction[5]: Writing action to db");
            write(actionList, Action.class);
            return Optional.of(action.getId());
        }catch (Exception e){
            log.error("Function DataProviderCSV appendAction had failed[5]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Result<Action> getActionHistory(String userID){
        log.info("Starting DataProviderXML getActionHistory[0]");
        try {
            Validator.isValid(userID);
            log.info("getActionHistory[1]: userID - {}", userID);
            log.debug("getActionHistory[2]: Getting all actions");
            List<Action> actions = new ArrayList<>(read(Action.class).stream().filter(x -> x.getUserID().equals(userID)).toList());
            return new Result<>(SUCCESS, String.format("Number of actions: %d", actions.size()), actions);
        }catch (Exception e){
            log.error("Function DataProviderXML getActionHistory had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Action> deleteActionHistory(String userID){
        log.info("Starting DataProviderCSV deleteAllUserAction[0]");
        try {
            Validator.isValid(userID);
            log.info("deleteAllUserAction[1]: userID - {}", userID);
            log.debug("deleteAllUserAction[2]: Getting all actions");
            List<Action> actions = read(Action.class);
            log.debug("deleteAllUserAction[3]: Getting user by id {}", userID);
            Optional<User> user = getUserById(userID);
            if (user.isEmpty())
                throw new Exception(String.format("User wasn't found by id %s", userID));
            User updatedUser = new UserBuilder(user.get()).withActionHistory(new ArrayList<>()).build();
            log.debug("deleteAllUserAction[4]: Getting all user's {}  actions", userID);
            List<Action> actionToDelete = new ArrayList<>(actions.stream().filter(x -> x.getUserID().equals(userID)).toList());
            log.debug("deleteAllUserAction[5]: Deleting all user's {} actions", userID);
            actions.removeAll(actionToDelete);
            write(actions, Action.class);
            log.debug("deleteAllUserAction[6]: Updating user");
            updateUser(updatedUser);
            return new Result<>(SUCCESS, String.format("Number of deleted actions: %d", actionToDelete.size()), actionToDelete);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllUserAction had failed[7]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Method for getting all actions from db
     * @return Result<Action> - (Status, Message, Response)
     * Status
     * Message
     * Response - list of all actions
     */
    private Result<Action> getAllActions(){
        log.info("Starting DataProviderXML getAllActions[0]");
        try {
            log.debug("getAllActions[1]: Getting all actions");
            List<Action> actionList = read(Action.class);
            return new Result<>(SUCCESS, String.format("Number of actions: %d", actionList.size()), actionList);
        }catch (Exception e){
            log.error("Function DataProviderXML getAllActions had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Method for processing action depending on actionType
     * It will add security or delete security if the expression matches the condition
     * @param action - user's action
     * @return status of function
     * true - if function ended up successfully
     * false - otherwise
     */
    private boolean processAction(Action action){
        log.info("Starting DataProviderXML processAction[0]");
        try {
            log.info("processAction[1]: action - {}", action);
            log.debug("processAction[2]: Getting user by id");
            Optional<User> user = getUserById(action.getUserID());
            if (user.isEmpty())
                throw new Exception(String.format("User wasn't found by id %s", action.getUserID()));
            switch (action.getAction()){
                case ADD -> {
                    return addUsersSecurity(action, user.get());
                }
                case DELETE -> {
                    return deleteUsersSecurity(action, user.get());
                }
            }
        }catch (Exception e){
            log.error("Function DataProviderXML processAction had failed[3]: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Method for adding updating user's briefcase(add)
     * @param action - user's action
     * @param user - user
     * @return status of function
     * true - if function ended up successfully
     * false - otherwise
     */
    private boolean addUsersSecurity(Action action, User user){
        log.info("Starting DataProviderXML addUsersSecurity[0]");
        try {
            log.info("addUsersSecurity[1]: action - {}, user - {}", action, user);
            log.debug("addUsersSecurity[2]: Adding action");
            if (action.getAction().equals(ActionType.ADD) && !user.getSecurityList().contains(action.getSecurity())){
                log.debug("addUsersSecurity[3]: Updating user, adding security");
                user.getActionHistory().add(action);
                user.getSecurityList().add(action.getSecurity());
                updateUser(new UserBuilder(user).build());
                return true;
            }
        }catch (Exception e){
            log.error("Function DataProviderXML addUsersSecurity had failed[4]: {}", e.getMessage());
        }
        return false;
    }


    /**
     * Method for adding updating user's briefcase(delete)
     * @param action - user's action
     * @param user - user
     * @return status of function
     * true - if function ended up successfully
     * false - otherwise
     */
    private boolean deleteUsersSecurity(Action action, User user){
        log.info("Starting DataProviderXML deleteUsersSecurity[0]");
        try {
            log.info("deleteUsersSecurity[1]: action - {}, user - {}", action, user);
            log.debug("deleteUsersSecurity[2]: Adding action");
            if (action.getAction().equals(ActionType.DELETE) && user.getSecurityList().contains(action.getSecurity())){
                log.debug("deleteUsersSecurity[3]: Deleting user, deleting security");
                user.getActionHistory().add(action);
                user.getSecurityList().remove(action.getSecurity());
                updateUser(new UserBuilder(user).build());
                return true;
            }
        }catch (Exception e){
            log.error("Function DataProviderXML deleteUsersSecurity had failed[4]: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Method for deleting all actions from db
     */
    private void deleteAllActions(){
        log.debug("Starting DataProviderXML deleteAllActions[0]");
        try {
            log.debug("deleteAllActions[1]: Deleting all actions");
            write(new ArrayList<>(), Action.class);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllActions had failed[2]: {}", e.getMessage());
        }
    }

    // use case
    @Override
    public Result<SecurityHistory> findSecurity(String ticker){
        log.info("Starting DataProviderXML findSecurity[0]");
        try {
            Validator.isValid(ticker);
            log.info("findSecurity[1]: ticker - {}", ticker);
            return showDetailedInfo(ticker);
        }catch (Exception e){
            log.error("Function DataProviderXML had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }
    @Override
    public Result<Security> findSecurity(MarketType marketType){
        log.info("Starting DataProviderXML findSecurity[0]");
        try {
            Validator.isValid(marketType);
            log.info("findSecurity[1]: marketType - {}", marketType);
            return getActiveSecurities(marketType);
        }catch (Exception e){
            log.error("Function DataProviderXML had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Security> getActiveSecurities(MarketType marketType){
        log.info("Starting DataProviderXML getActiveSecurities[0]");
        try {
            log.info("getActiveSecurities[1]: marketType - {}", marketType);
            Optional<Market> market = getMarket(marketType);
            if (market.isEmpty())
                throw new Exception("Market wasn't found");
            List<Security> securityList = market.get().getSecurityList();
            securityList.sort(Comparator.comparing( (Security x) -> x.getHistory().getVolume() ).reversed());
            return new Result<>(SUCCESS, String.format("Number of securities: %d", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderXML getActiveSecurities had failed[2]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<SecurityHistory> showDetailedInfo(String ticker) {
        log.info("Starting DataProviderXML showDetailedInfo[0]");
        try {
            Validator.isValid(ticker);
            log.debug("showDetailedInfo[1]: ticker - {}", ticker);
            Optional<Security> security = getSecurityByTicker(ticker);
            if (security.isEmpty())
                throw new Exception("Security wasn't found");
            log.debug("showDetailedInfo[2]: Getting and sorting history");
            List<SecurityHistory> securityHistoryList = getSecurityHistories(security.get().getTicker()).getBody();
            securityHistoryList.sort(Comparator.comparing(SecurityHistory::getDate).reversed());
            return new Result<>(SUCCESS, String.format("Providing %s's histories: \n", ticker), securityHistoryList);
        }catch (Exception e){
            log.error("Function DataProviderXML showDetailedInfo had failed[3]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public String showInfo(String ticker){
        log.info("Starting DataProviderXML showInfo[0]");
        try {
            Validator.isValid(ticker);
            log.info("showInfo[1]: ticker - {}", ticker);
            log.debug("showInfo[2]: Getting security by ticker {}", ticker);
            Optional<Security> security = getSecurityByTicker(ticker);
            if (security.isEmpty())
                throw new Exception(String.format("Security %s wasn't found", ticker));
            log.debug("showInfo[3]: Getting extra information");
            String extraInfo = security.get().getMarketType().equals(MarketType.SHARES) ?
                    String.format("dividendSum: %.3f \ncapitalization: %.3f \ntype: %s \n"
                            ,((Stock) security.get()).getDividendSum()
                            ,((Stock) security.get()).getCapitalization()
                            ,((Stock) security.get()).getType()):
                    String.format("matDate: %s \ncoupon: %.3f \ndayToRedemption: %d \ntype: %s \n"
                            ,((Bond) security.get()).getMatDate()
                            ,((Bond) security.get()).getCoupon()
                            ,((Bond) security.get()).getDayToRedemption()
                            ,((Bond) security.get()).getType());
            return String.format("\nticker: %s \nname: %s \nisin: %s \nnominal: %.3f \nnominalValue: %s \nissueDate: %s \nlatName: %s \nissueSize: %d \ngroup: %s \n"
                            ,security.get().getTicker()
                            ,security.get().getShortName()
                            ,security.get().getIsin()
                            ,security.get().getNominal()
                            ,security.get().getNominalValue()
                            ,security.get().getIssueDate()
                            ,security.get().getLatName()
                            ,security.get().getIssueSize()
                            ,security.get().getMarketType())
                    .concat(extraInfo);

        }catch (Exception e){
            log.error("Function DataProviderXML showInfo had failed[4]: {}", e.getMessage());
        }
        return "";
    }

    @Override
    public Result<Security> checkVirtualBriefCase(String userId){
        log.info("Starting DataProviderXML checkVirtualBriefCase[0]");
        try {
            Validator.isValid(userId);
            log.info("checkVirtualBriefCase[1]: userId - {}", userId);
            log.debug("checkVirtualBriefCase[2]: Getting user by id {}", userId);
            Optional<User> user = getUserById(userId);
            if (user.isEmpty())
                throw new Exception(String.format("User wasn't found by id %s", userId));
            return new Result<>(SUCCESS, String.format("Number of saved securities: %d", user.get().getSecurityList().size()), user.get().getSecurityList()) ;
        }catch (Exception e){
            log.error("Function DataProviderXML checkVirtualBriefCase[3]: {}" ,e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public String showStatistics(String userId){
        log.info("Starting DataProviderXML showStatistics[0]");
        try {
            Validator.isValid(userId);
            log.info("showStatistics[1]: userId - {}", userId);
            log.debug("showStatistics[2]: Getting user by id {}", userId);
            Optional<User> user = getUserById(userId);
            if (user.isEmpty())
                throw new Exception(String.format("User %s wasn't found", userId));
            StringBuilder info = new StringBuilder("\n");
            for (Security security: user.get().getSecurityList())
                info.append(security.getTicker()).append(": ").append(security.getHistory().getAveragePerDay()).append("\n");
            List<Action> actions = user.get().getActionHistory();
            actions.sort(Comparator.comparing(Action::getDate).reversed());
            for (Action action : actions)
                info.append("Time: ").append(action.getDate()).append("\n Action: ").append(action.getAction())
                        .append("\n Security: ").append(action.getSecurity().getTicker()).append("\n");
            return info.toString();
        }catch (Exception e){
            log.error("Function DataProviderXML showStatistics had failed[3]: {}" , e.getMessage());
        }
        return "";
    }

    @Override
    public boolean performActon(String userId, String actionType, String ticker){
        log.info("Starting DataProviderXML performActon[0]");
        try {
            log.info("performActon[1]: userId - {}, actionType - {}, ticker - {}", userId, actionType, ticker);
            return appendAction(ActionType.valueOf(actionType.toUpperCase()),userId ,ticker).isPresent();
        }catch (Exception e){
            log.error("Function DataProviderXML performActon had failed[2]: {}", e.getMessage());
        }
        return false;
    }

}
