package ru.sfedu.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.builder.IUserBuilder;
import ru.sfedu.builder.UserBuilder;
import ru.sfedu.entity.User;
import ru.sfedu.model.Result;
import ru.sfedu.entity.Bond;
import ru.sfedu.entity.Security;
import ru.sfedu.entity.Stock;
import ru.sfedu.Constants;
import ru.sfedu.utils.ValidEntityListValidator;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static ru.sfedu.model.CommandName.*;
import static ru.sfedu.model.RepositoryName.*;
import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.Constants.*;

public class DataProviderCSV implements IDateProvider{
    private final Logger log = (Logger) LogManager.getLogger(DataProviderCSV.class.getName());

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
                updatedUsers.add(new User(i, user.getName(), user.getAge()));
            }
        }
        return updatedUsers;
    }

    private CSVWriter getCSVWriter(Class<?> bean) throws Exception {
        log.info("Starting DataProviderCSV getCSVWriter[]");
        try {
            log.debug("getCSVWriter[]: {}", bean);
            log.debug("getCSVWriter[]: Creating FileWriter");
            Files.createDirectories(Paths.get(getConfigurationEntry(CSV_PATH)));
            FileWriter writer = new FileWriter(getConfigurationEntry(CSV_PATH)
                    + bean.getSimpleName().toLowerCase()
                    + getConfigurationEntry(CSV_FILE_EXTENTION), false);
            log.debug("getCSVWriter[]: Creating CSVWriter");
            return new CSVWriter(writer);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSVWriter had failed[]");
            throw new Exception(e);
        }
    }

    private <T> StatefulBeanToCsvBuilder<T> getBeanToCSVBuilder(CSVWriter writer) throws Exception {
        log.info("Starting DataProviderCSV getBeanToCSVBuilder[]");
        try{
            log.info("getBeanToCSVBuilder[]: {}", writer);
            return new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)   //
                    .withLineEnd(writer.DEFAULT_LINE_END);
        }catch (Exception e){
            log.error("Function DataProviderCSV getBeanToCSVBuilder had failed[]");
            throw new Exception(e);
        }
    }

    private <T> void write(List<T> securityList, Class<T> security) throws Exception {
        log.info("Starting DataProviderCSV write");
        try {
            log.info("write[]: {}, {}", securityList, security);
            log.debug("write[]: Creating csvWriter[7]");
            CSVWriter csvWriter = getCSVWriter(security);
            log.debug("write[]: Creating StatefulBean[8]");
            StatefulBeanToCsvBuilder<T> beanToCsvBuilder = getBeanToCSVBuilder(csvWriter);
            StatefulBeanToCsv<T> beanToCsv = beanToCsvBuilder.build();
            log.debug("write[]: Writing to csv file[9]");
            beanToCsv.write(securityList);
            log.debug("write[]: Closing CSVWriter[10]");
            csvWriter.close();
        } catch (Exception e){
            log.error("Function DataProviderCSV write had failed");
            throw new Exception(e);
        }
    }

    private <T> CSVReader getCSWReader(Class<T> ob) throws Exception {
        log.info("Starting DataProviderCSV getCSVReader[12]");
        try {
            log.info("getCSVWriter[13]: {}, type: {}", ob, ob.getSimpleName());
            log.debug("Creating FileReader[14]");
            FileReader reader = new FileReader(getConfigurationEntry(CSV_PATH)
                    + ob.getSimpleName().toLowerCase()
                    + getConfigurationEntry(CSV_FILE_EXTENTION));
            log.debug("Creating CSWReader[15]");
            return new CSVReader(reader);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSWReader had failed[16]");
            throw new Exception(e);
        }
    }

    private <T> CsvToBeanBuilder<T> getCsvToBeanBuilder(Class<T> bean) throws Exception {
        log.info("Starting DataProviderCSV getCsvToBeanBuilder[17]");
        try
        {
            log.info("getCsvToBeanBuilder[18]: {}, type: {}", bean, bean.getSimpleName());
            log.debug("Creating CSVReader[19]");
            CSVReader reader = getCSWReader(bean);
            log.debug("Creating CsvToBeanBuilder[20]");
            return new CsvToBeanBuilder<>(reader);
        }catch (Exception e){
            log.error("Function DataProviderCSV CsvToBeanBuilder had failed[21]");
            throw new Exception(e);
        }
    }

    private <T> List<T> read(Class<T> bean) throws Exception {
        log.info("Starting DataProviderCSV write");
        try {
            log.info("write: {}", bean);
            log.debug("write[]: creating csvToBean");
            CsvToBeanBuilder<T> csvToBeanBuilder = getCsvToBeanBuilder(bean);
            CsvToBean<T> csvToBean = csvToBeanBuilder
                    .withType(bean)
                    .build();
            log.debug("Parse CSVToBean[]");
            return csvToBean.parse();
        }catch (Exception e){
            log.error("Function read had failed[]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> appendSecurities(List<T> list, Class<T> security) {
        log.info("Starting DataProviderCSV appendSecurities[]");
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
            log.error("Function DataProviderCSV appendSecurities had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    public <T extends Security> Result<T> getSecurities(Class<T>securityClass) {
        log.info("Starting DataProviderCSV getSecurities[]");
        try
        {
            return new Result<>(Constants.SUCCESS, "", new ArrayList<>(read(securityClass)));
        } catch (Exception e) {
            log.error("Function DataProviderCSV getSecurities had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, Class<T> securityClass) throws Exception {
        log.info("Starting DataProviderCSV deleteSecurityByTicker[]");
        try {
            log.info("deleteSecurityByTicker[]: {}, type: {}",ticker, ticker.getClass());
            log.debug("deleteSecurityByTicker: GetSecurity from csv file[]");
            List<T> securities = new ArrayList<>(read(securityClass));
            log.debug("deleteSecurityByTicker[]: Search a {} by ticker {}[]",securityClass.getSimpleName(), ticker);
            Optional<T> sec = securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(sec.isPresent()){
                securities.remove(sec.get());
                write(securities, securityClass);
                MongoDBLog.save(DELETE, CSV, sec.get());
            } else
                log.warn("{} wasn't found by ticker {}",securityClass.getSimpleName(), ticker);
            return sec;
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteSecurityByTicker has crashed[]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> deleteAllSecurities(Class<T> securityClass)  {
        log.info("Starting DataProviderCSV deleteAllSecurities[]");
        try {
            log.info("deleteAllSecurities[]: {}", securityClass);
            log.debug("deleteAllSecurities[]: get securities from file");
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("deleteAllSecurities[]: delete all securities");
            write(null, securityClass);
            MongoDBLog.save(DELETE, CSV, securityList);
            return new Result<>(Constants.SUCCESS, "", securityList);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllSecurities had failed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    public <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class<T> securityClass) throws Exception {
        log.info("Starting DataProviderCSV getSecurityByTicker[]");
        log.info("getSecurityByTicker[]: {}, type: {}", ticker, ticker.getClass());
        try {
            log.debug("getSecurityByTicker[]: Get {} from CSV[]", securityClass.getSimpleName());
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("getSecurityByTicker[]: Search for a {} by ticker {}[]", securityClass.getSimpleName(), ticker);
            return securityList.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getSecurityByTicker had failed[]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> updateSecurities(List<T> securities, Class<T> securityClass)  {
        log.info("Starting DataProviderCSV updateSecurities[]");
        try {
            ValidEntityListValidator.isValidSecurity(securities);
            log.info("updateSecurities: {}, type: {}[]", Arrays.toString(securities.toArray()), securities.getClass());
            List<T> oldList = new ArrayList<>(read(securityClass));
            List<String> tickerList = getSecurityTicker(securities);
            log.debug("updateSecurities[]: Update csv file: {}", securityClass.getSimpleName());
            List<T> securityToUpdate = securities.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            write(Stream.concat(securityToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(T::getTicker)).toList(), securityClass);
            MongoDBLog.save(UPDATE, CSV, securityToUpdate);
            return new Result<>(Constants.SUCCESS, "", securities.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    @Override
    public Result<User> getUsers()  {
        log.info("Starting DataProviderCSV getUsers[]");
        try
        {
            return new Result<>(Constants.SUCCESS, "", new ArrayList<>(read(User.class)));
        } catch (Exception e) {
            log.error("Function DataProviderCSV getUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> appendUsers(List<User> list)  {
        log.info("Starting DataProviderCSV appendSecurities[]");
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
            log.error("Function DataProviderCSV appendUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users)  {
        log.info("Starting DataProviderCSV updateUsers[]");
        try {

            log.info("updateUsers: {}, type: {}[]", Arrays.toString(users.toArray()), users.getClass());
            ValidEntityListValidator.isValidUserToUpdate(users);

            List<User> oldList = new ArrayList<>(read(User.class));
            List<Long> idList = getUsersId(oldList);
            log.debug("updateUsers[]: Update csv file: {}", "user");
            List<User> userToUpdate = users.stream().filter(x -> idList.contains(x.getId())).toList();
            write(Stream.concat(userToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(User::getId)).toList(), User.class);
            MongoDBLog.save(UPDATE, CSV, userToUpdate);
            return new Result<>(Constants.SUCCESS, "", users.stream().filter(x -> !idList.contains(x.getId())).toList());
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id) throws Exception {
        log.info("Starting DataProviderCSV deleteUserById[]");
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
                MongoDBLog.save(DELETE, CSV, user.get());
            }
            return user;
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteUserById has crashed[]");
            throw new Exception(e);
        }
    }


    @Override
    public Result<User> deleteAllUsers()  {
        log.info("Starting DataProviderCSV deleteAllUsers[]");
        try {
            log.debug("deleteAllUsers[]: get users from file");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("deleteAllUsers[]: delete all securities");
            write(new ArrayList<>(), User.class);
            MongoDBLog.save(DELETE, CSV, securityList);
            return new Result<>(Constants.SUCCESS, "", securityList);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllUsers had failed[]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> getUserById(long id) throws Exception {
        log.info("Starting DataProviderCSV getUserById[]");
        log.info("getUserById[]: {}",id);
        try {
            log.debug("getUserById[]: Get users from CSV file[]");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("getUserById[]: Search for the user by id {}", id);
            return securityList.stream().filter(x -> x.getId().equals(id)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getUserById had failed[]");
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
