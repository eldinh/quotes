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

public class DataProviderXML implements DateProvider {

    private final Logger log = (Logger) LogManager.getLogger(DataProviderXML.class.getName());

    private <T extends Security> List<String> getSecurityTicker(List<T> securities){
        return new ArrayList<>(securities.stream().map(T::getTicker).toList());
    }

    private static List<User> unionTwoUserLists(List<User> mainList, List<User> appendList){
        List<Long> idList = mainList.stream().map(User::getId).toList();
        mainList.addAll(appendList.stream().filter(x -> !idList.contains(x.getId())).toList());
        return appendList.stream().filter(x -> idList.contains(x.getId())).toList();

    }

    private static List<Long> getUsersId(List<User> users){
        return users.stream().map(User::getId).toList();
    }

    private static List<User> generateIdForUsers(List<Long> idList, List<User> userWithoutId){
        List<User> updatedUsers = new ArrayList<>();
        List<User> users = new ArrayList<>(userWithoutId);
        for (long i = 0; ; i ++){
            if (users.isEmpty())
                break;
            if (!idList.contains(i)) {
                User user = users.remove(0);
                updatedUsers.add(new User(i, user.getName(), user.getAge()));
            }
        }
        return updatedUsers;
    }

    private FileReader getFileReader(String filename, String extraPath) throws Exception {
        log.info("Starting DataProviderXML getFileReader[]");
        try {
            log.debug("getFileReader[]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("getFileReader[]: Creating FileReader");
            return new FileReader(getConfigurationEntry(XML_PATH)
                    .concat(extraPath).concat(filename)
                    .concat(getConfigurationEntry(XML_FILE_EXTENTION)));
        }catch (Exception e){
            log.error("Function DataProviderXML getFileWriter had failed[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private FileReader getFileReader(Class<?> ob) throws Exception {
        return getFileReader(ob.getSimpleName().toUpperCase(), "");
    }

    private FileWriter getFileWriter(String filename, String extraPath) throws Exception {
        log.info("Starting DataProviderXML getFileWriter[]");
        try {
            log.debug("getFileWriter[]: filename - {}, extraPath - {}", filename, extraPath);
            log.debug("getFileWriter[]: Creating FileWriter[]");
            Files.createDirectories(Paths.get(getConfigurationEntry(XML_PATH).concat(extraPath)));
            return new FileWriter(getConfigurationEntry(XML_PATH)
                    .concat(extraPath).concat(filename)
                    .concat(getConfigurationEntry(XML_FILE_EXTENTION)), false);
        }catch (Exception e){
            log.error("Function DataProviderXML getFileWriter had failed[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private FileWriter getFileWriter(Class<?> user) throws Exception {
        return getFileWriter(user.getSimpleName().toUpperCase(), "");
    }

    private <T> void write(List<T> list  , Class<T> pojo) throws Exception {
        log.info("Starting DataProviderXML write[]");
        try {
            log.info("write[]: list - {}, pojo - {}", Arrays.toString(list.toArray()), pojo);
            Serializer serializer = new Persister();
            log.debug("write[]: Getting writer");
            FileWriter writer = getFileWriter(pojo);
            log.debug("write[]: Writing to XML file[]");
            serializer.write(new Wrapper<>(list), writer);
        } catch (Exception e){
            log.error("Function DataProviderXML write[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private <T> List<T> read(Class<T> bean) throws Exception {
        log.info("Starting DataProviderXML read");
        try {
            log.info("read[]: bean - {}", bean);
            Wrapper<T> wrapper = new Wrapper<>();
            log.debug("read[]: Get serializer");
            Serializer serializer = new Persister();
            log.debug("read[]: Read from file");
            serializer.read(wrapper, getFileReader(bean));
            return wrapper.getContainer();
        }catch (Exception e){
            log.error("Function read had failed[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private <T extends Security> Result<T> appendSecurities(List<T> list, Class<T> security) {
        log.info("Starting DataProviderXML appendSecurities[]");
        List<T> oldList = new ArrayList<>(getSecurities(security).getBody());
        try
        {
            Validator.isValidSecurity(list);
            log.info("appendSecurities[]: list - {}, security - {}", list, security);
            List<String> tickerList = oldList.stream().map(T::getTicker).toList();
            List<T> securityToAppend = list.stream().filter(x -> !tickerList.contains(x.getTicker())).toList();
            oldList.addAll(securityToAppend);
            oldList.sort(Comparator.comparing(T::getTicker));
            List<T> response = list.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            log.debug("appendSecurities[]: Writing to csv file");
            write(oldList, security);
            securityToAppend.forEach(x -> appendOrUpdate(x.getHistory(), x.getTicker()));
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderXML appendSecurities had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    private <T extends Security> Result<T> getSecurities(Class<T>securityClass) {
        log.info("Starting DataProviderXML getSecurities[]");
        try
        {
            log.info("getSecurities[]: securityClass - {}", securityClass);
            List<T> response = new ArrayList<>(read(securityClass));
            return new Result<>(SUCCESS, String.format("Number of securities in file: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderXML getSecurities had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, Class<T> securityClass)  {
        log.info("Starting DataProviderXML deleteSecurityByTicker[]");
        try {
            log.info("deleteSecurityByTicker[]: ticker - {}, securityClass - {}",ticker, securityClass);
            log.debug("deleteSecurityByTicker: GetSecurity from csv file[]");
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
            log.error("Function DataProviderXML deleteSecurityByTicker has crashed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private <T extends Security> Result<T> deleteAllSecurities(Class<T> securityClass)  {
        log.info("Starting DataProviderXML deleteAllSecurities[]");
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
            log.error("Function DataProviderXML deleteAllSecurities had failed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class<T> securityClass)  {
        log.info("Starting DataProviderXML getSecurityByTicker[]");
        log.info("getSecurityByTicker[]: ticker - {}, securityClass - {}", ticker, ticker.getClass());
        try {
            log.debug("getSecurityByTicker[]: Get {} from CSV[]", securityClass.getSimpleName());
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("getSecurityByTicker[]: Search for a {} by ticker {}[]", securityClass.getSimpleName(), ticker);
            return securityList.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getSecurityByTicker had failed[]: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private <T extends Security> Result<T> updateSecurities(List<T> securities, Class<T> securityClass)  {
        log.info("Starting DataProviderXML updateSecurities[]");
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
            log.error("Function DataProviderXML updateUsers had crashed[]: {}", e.getMessage());
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    @Override
    public Result<User> getUsers()  {
        log.info("Starting DataProviderXML getUsers[]");
        try
        {
            List<User> response =  new ArrayList<>(read(User.class));
            return new Result<>(SUCCESS, String.format("Number of users in file: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderXML getUsers had crashed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> appendUsers(List<User> list)  {
        log.info("Starting DataProviderXML appendSecurities[]");
        List<User> oldList = new ArrayList<>(getUsers().getBody());
        try
        {
            Validator.isValidUser(list);
            log.info("appendUsers[]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
            List<User> response =  unionTwoUserLists(oldList, list.stream().filter(x -> x.getId() != null).toList());
            oldList.addAll(generateIdForUsers(getUsersId(oldList), list.stream().filter(x -> x.getId() == null).toList()));
            oldList.sort(Comparator.comparing(User::getId));
            log.debug("appendSecurities[]: write to csv file");
            write(oldList, User.class);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Users have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of users that haven't been appended: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderXML appendUsers had crashed[]");
            return new Result<>(FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users)  {
        log.info("Starting DataProviderXML updateUsers[]");
        try {

            log.info("updateUsers: {}, type: {}[]", Arrays.toString(users.toArray()), users.getClass());
            Validator.isValidUserToUpdate(users);
            List<User> oldList = new ArrayList<>(read(User.class));
            List<Long> idList = getUsersId(oldList);
            List<User> response = users.stream().filter(x -> !idList.contains(x.getId())).toList();
            log.debug("updateUsers[]: Update csv file: {}", "user");
            List<User> userToUpdate = users.stream().filter(x -> idList.contains(x.getId())).toList();
            write(Stream.concat(userToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(User::getId)).toList(), User.class);
            MongoHistory.save(UPDATE, XML, userToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Users have been updated successfully", response);
            return new Result<>(WARN, String.format("Number of users that haven't been updated: %d", response.size()),response);
        } catch (Exception e) {
            log.error("Function DataProviderXML updateUsers had crashed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id)  {
        log.info("Starting DataProviderXML deleteUserById[]");
        try {
            log.info("deleteUserById[]: {}",id);
            log.debug("deleteUserById[]: GetSecurity from csv file[]");
            List<User> users = new ArrayList<>(read(User.class));
            log.debug("deleteUserById[]: Search a user by id {}",id);
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            if(user.isPresent()) {
                users.remove(user.get());
                log.debug("deleteUserById[]: Update CSV File[33]");
                write(users, User.class);
                MongoHistory.save(DELETE, XML, user.get());
            }
            return user;
        }catch (Exception e){
            log.error("Function DataProviderXML deleteUserById has crashed[]");
        }
        return Optional.empty();
    }


    @Override
    public Result<User> deleteAllUsers()  {
        log.info("Starting DataProviderXML deleteAllUsers[]");
        try {
            log.debug("deleteAllUsers[]: get users from file");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("deleteAllUsers[]: delete all securities");
            write(new ArrayList<>(), User.class);
            MongoHistory.save(DELETE, XML, securityList);
            return new Result<>(SUCCESS, String.format("Number of deleted users: %d ", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllUsers had failed[]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> getUserById(long id) {
        log.info("Starting DataProviderXML getUserById[]");
        log.info("getUserById[]: {}",id);
        try {
            log.debug("getUserById[]: Get users from CSV file[]");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("getUserById[]: Search for the user by id {}", id);
            return securityList.stream().filter(x -> x.getId().equals(id)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getUserById had failed[]");
        }
        return Optional.empty();
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
        return appendSecurities(list, Stock.class);

    }

    @Override
    public Result<Bond> appendBonds(List<Bond> list) {
        return appendSecurities(list, Bond.class);
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



    private void writeHistory(List<SecurityHistory> securityList, String ticker) throws Exception {
        log.info("Starting DataProviderXML writeHistory[]");
        try {
            log.info("writeHistory[]: securityList - {}, ticker - {}", securityList, ticker);
            Serializer serializer = new Persister();
            log.debug("writeHistory[]: Getting writer");
            FileWriter writer = getFileWriter(ticker.toUpperCase(), SECURITY_HISTORY_PATH);
            log.debug("writeHistory[]: Writing to XML file[]");
            serializer.write(new Wrapper<>(securityList), writer);
        } catch (Exception e){
            log.error("Function DataProviderXML writeHistory[]: {}", e.getMessage());
            throw new Exception(e);
        }
    }

    private List<SecurityHistory> readHistory(String ticker) throws Exception {
        log.info("Starting DataProviderXML readHistory");
        try {
            log.info("readHistory[]: ticker - {}", ticker);
            Wrapper<SecurityHistory> wrapper = new Wrapper<>();
            log.debug("readHistory[]: Get serializer");
            Serializer serializer = new Persister();
            log.debug("readHistory[]: Read from file");
            serializer.read(wrapper, getFileReader(ticker.toUpperCase(), SECURITY_HISTORY_PATH));
            return wrapper.getContainer();
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
            oldList.addAll(securityHistories.stream().filter(x -> !dateList.contains(x.getDate())).toList());
            oldList.sort(Comparator.comparing(SecurityHistory::getDate).reversed());
            List<SecurityHistory> response = securityHistories.stream().filter(x -> dateList.contains(x.getDate())).toList();
            log.debug("appendSecuritiesHistory[]: write to file");
            writeHistory(securityHistories, ticker);
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
            File file = new File(getConfigurationEntry(XML_PATH)
                    .concat(SECURITY_HISTORY_PATH).concat(ticker)
                    .concat(getConfigurationEntry(XML_FILE_EXTENTION)));
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
            writeHistory(securityHistoryList, ticker);
            return true;
        }catch (Exception e){
            log.error("Function DataProviderCSV appendOrUpdate had failed: {}", e.getMessage());
        }
        return false;
    }

}
