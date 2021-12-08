package ru.sfedu.api;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.Constants;
import ru.sfedu.entity.Bond;
import ru.sfedu.entity.Security;
import ru.sfedu.entity.Stock;
import ru.sfedu.entity.User;
import ru.sfedu.model.Result;
import ru.sfedu.model.Wrapper;
import ru.sfedu.utils.ValidEntityListValidator;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Stream;

import static ru.sfedu.Constants.XML_FILE_EXTENTION;
import static ru.sfedu.Constants.XML_PATH;
import static ru.sfedu.model.CommandName.*;
import static ru.sfedu.model.RepositoryName.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;

public class DataProviderXML implements IDateProvider {

    private final Logger log = (Logger) LogManager.getLogger(DataProviderXML.class.getName());

    private <T extends Security> List<String> getSecurityTicker(List<T> securities){
        return new ArrayList<>(securities.stream().map(T::getTicker).toList());
    }

    private static List<User> unionTwoUserLists(List<User> mainList, List<User> appendList){
        List<Long> idList = mainList.stream().map(User::getId).toList();
        mainList.addAll(appendList.stream().filter(x -> !idList.contains(x.getId())).toList());
        return appendList.stream().filter(x -> idList.contains(x.getId())).toList();

    }

    public static List<Long> getUsersId(List<User> users){
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
                user.setId(i);
                updatedUsers.add(user);
            }
        }
        return updatedUsers;
    }

    private FileReader getFileReader(Class<?> ob) throws Exception {
        log.info("Starting DataProviderXML getFileReader[]");
        try {
            log.debug("getFileReader[]: {}", ob);
            log.debug("getFileReader[]: Creating FileReader");
            return new FileReader(getConfigurationEntry(XML_PATH)
                    + ob.getSimpleName().toLowerCase()
                    + getConfigurationEntry(XML_FILE_EXTENTION));
        }catch (Exception e){
            log.error("Function DataProviderXML getFileWriter had failed[]");
            throw new Exception(e);
        }
    }

    private FileWriter getFileWriter(Class<?> user) throws Exception {
        log.info("Starting DataProviderXML getFileWriter[]");
        try {
            log.debug("getFileWriter[]: {} type: {}", user, user.getSimpleName());
            log.debug("getFileWriter[]: Creating FileWriter[]");
            return new FileWriter(getConfigurationEntry(XML_PATH)
                    + user.getSimpleName().toLowerCase()
                    + getConfigurationEntry(XML_FILE_EXTENTION), false);
        }catch (Exception e){
            log.error("Function DataProviderXML getFileWriter had failed[]");
            throw new Exception(e);
        }
    }

    private <T> void write(List<T> list  , Class<T> pojo) throws Exception {
        log.info("Starting DataProviderXML write[]");
        try {
            log.info("write[]: {}, {}", Arrays.toString(list.toArray()), pojo);
            Serializer serializer = new Persister();
            log.debug("write[]: Getting writer");
            FileWriter writer = getFileWriter(pojo);
            log.debug("write[]: Writing to XML file[]");
            serializer.write(new Wrapper<>(list), writer);
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    private <T> List<T> read(Class<T> bean) throws Exception {
        log.info("Starting DataProviderXML read");
        try {
            Wrapper<T> wrapper = new Wrapper<>();
            log.debug("read[]: Get serializer");
            Serializer serializer = new Persister();
            log.debug("read[]: Read from file");
            serializer.read(wrapper, getFileReader(bean));
            return wrapper.getContainer();
        }catch (Exception e){
            log.error("Function read had failed[]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> appendSecurities(List<T> list, Class<T> security) {
        log.info("Starting DataProviderXML appendSecurities[]");
        List<T> oldList = new ArrayList<>(getSecurities(security).getBody());
        try
        {
            ValidEntityListValidator.isValidSecurity(list);
            log.info("appendSecurities[]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
            List<String> tickerList = oldList.stream().map(T::getTicker).toList();
            oldList.addAll(list.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
            oldList.sort(Comparator.comparing(T::getTicker));
            List<T> response = list.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            log.debug("appendSecurities[]: write to csv file");
            write(oldList, security);
            return new Result<>(Constants.SUCCESS, "", response);
        } catch (Exception e) {
            log.error("Function DataProviderXML appendSecurities had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    public <T extends Security> Result<T> getSecurities(Class<T>securityClass) {
        log.info("Starting DataProviderXML getSecurities[]");
        try
        {
            return new Result<>(Constants.SUCCESS, "", new ArrayList<>(read(securityClass)));
        } catch (Exception e) {
            log.error("Function DataProviderXML getSecurities had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, Class<T> securityClass) throws Exception {
        log.info("Starting DataProviderXML deleteSecurityByTicker[]");
        try {
            log.info("deleteSecurityByTicker[]: {}, type: {}",ticker, ticker.getClass());
            log.debug("deleteSecurityByTicker: GetSecurity from csv file[]");
            List<T> securities = new ArrayList<>(read(securityClass));
            log.debug("deleteSecurityByTicker[]: Search a {} by ticker {}[]",securityClass.getSimpleName(), ticker);
            Optional<T> sec = securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(sec.isPresent()){
                securities.remove(sec.get());
                write(securities, securityClass);
                MongoDBLog.save(DELETE, XML, sec.get());
            } else
                log.warn("{} wasn't found by ticker {}",securityClass.getSimpleName(), ticker);
            return sec;
        }catch (Exception e){
            log.error("Function DataProviderXML deleteSecurityByTicker has crashed[]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> deleteAllSecurities(Class<T> securityClass)  {
        log.info("Starting DataProviderXML deleteAllSecurities[]");
        try {
            log.info("deleteAllSecurities[]: {}", securityClass);
            log.debug("deleteAllSecurities[]: get securities from file");
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("deleteAllSecurities[]: delete all securities");
            write(null, securityClass);
            MongoDBLog.save(DELETE, XML, securityList);
            return new Result<>(Constants.SUCCESS, "", securityList);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllSecurities had failed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class<T> securityClass) throws Exception {
        log.info("Starting DataProviderXML getSecurityByTicker[]");
        log.info("getSecurityByTicker[]: {}, type: {}", ticker, ticker.getClass());
        try {
            log.debug("getSecurityByTicker[]: Get {} from CSV[]", securityClass.getSimpleName());
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("getSecurityByTicker[]: Search for a {} by ticker {}[]", securityClass.getSimpleName(), ticker);
            return securityList.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getSecurityByTicker had failed[]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> updateSecurities(List<T> securities, Class<T> securityClass)  {
        log.info("Starting DataProviderXML updateSecurities[]");
        try {
            ValidEntityListValidator.isValidSecurity(securities);
            log.info("updateSecurities: {}, type: {}[]", Arrays.toString(securities.toArray()), securities.getClass());
            List<T> oldList = new ArrayList<>(read(securityClass));
            List<String> tickerList = getSecurityTicker(securities);
            log.debug("updateSecurities[]: Update csv file: {}", securityClass.getSimpleName());
            List<T> securityToUpdate = securities.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            write(Stream.concat(securityToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(T::getTicker)).toList(), securityClass);
            MongoDBLog.save(UPDATE, XML, securityToUpdate);
            return new Result<>(Constants.SUCCESS, "", securities.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
        } catch (Exception e) {
            log.error("Function DataProviderXML updateUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    @Override
    public Result<User> getUsers()  {
        log.info("Starting DataProviderXML getUsers[]");
        try
        {
            return new Result<>(Constants.SUCCESS, "", new ArrayList<>(read(User.class)));
        } catch (Exception e) {
            log.error("Function DataProviderXML getUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> appendUsers(List<User> list)  {
        log.info("Starting DataProviderXML appendSecurities[]");
        List<User> oldList = new ArrayList<>(getUsers().getBody());
        try
        {
            ValidEntityListValidator.isValid(list);
            log.info("appendUsers[]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
            List<User> response =  unionTwoUserLists(oldList, list.stream().filter(x -> x.getId() != null).toList());
            oldList.addAll(generateIdForUsers(getUsersId(oldList), list.stream().filter(x -> x.getId() == null).toList()));
            oldList.sort(Comparator.comparing(User::getId));
            log.debug("appendSecurities[]: write to csv file");
            write(oldList, User.class);
            return new Result<>(Constants.SUCCESS, "", response);
        } catch (Exception e) {
            log.error("Function DataProviderXML appendUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users)  {
        log.info("Starting DataProviderXML updateUsers[]");
        try {

            log.info("updateUsers: {}, type: {}[]", Arrays.toString(users.toArray()), users.getClass());
            ValidEntityListValidator.isValidUserToUpdate(users);

            List<User> oldList = new ArrayList<>(read(User.class));
            List<Long> idList = getUsersId(oldList);
            log.debug("updateUsers[]: Update csv file: {}", "user");
            List<User> userToUpdate = users.stream().filter(x -> idList.contains(x.getId())).toList();
            write(Stream.concat(userToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(User::getId)).toList(), User.class);
            MongoDBLog.save(UPDATE, XML, userToUpdate);
            return new Result<>(Constants.SUCCESS, "", users.stream().filter(x -> !idList.contains(x.getId())).toList());
        } catch (Exception e) {
            log.error("Function DataProviderXML updateUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id) throws Exception {
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
                MongoDBLog.save(DELETE, XML, user.get());
            }
            return user;
        }catch (Exception e){
            log.error("Function DataProviderXML deleteUserById has crashed[]");
            throw new Exception(e);
        }
    }


    @Override
    public Result<User> deleteAllUsers()  {
        log.info("Starting DataProviderXML deleteAllUsers[]");
        try {
            log.debug("deleteAllUsers[]: get users from file");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("deleteAllUsers[]: delete all securities");
            write(new ArrayList<>(), User.class);
            MongoDBLog.save(DELETE, XML, securityList);
            return new Result<>(Constants.SUCCESS, "", securityList);
        }catch (Exception e){
            log.error("Function DataProviderXML deleteAllUsers had failed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> getUserById(long id) throws Exception {
        log.info("Starting DataProviderXML getUserById[]");
        log.info("getUserById[]: {}",id);
        try {
            log.debug("getUserById[]: Get users from CSV file[]");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("getUserById[]: Search for the user by id {}", id);
            return securityList.stream().filter(x -> x.getId().equals(id)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderXML getUserById had failed[]");
            throw new Exception(e);
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
    public Optional<Stock> deleteStockByTicker(String ticker) throws Exception {
        return deleteSecurityByTicker(ticker, Stock.class);
    }

    @Override
    public Optional<Bond> deleteBondByTicker(String ticker) throws Exception {
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
    public Optional<Stock> getStockByTicker(String ticker) throws Exception {
        return getSecurityByTicker(ticker, Stock.class);
    }

    @Override
    public Optional<Bond> getBondByTicker(String ticker) throws Exception {
        return getSecurityByTicker(ticker, Bond.class);
    }
}
