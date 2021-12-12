package ru.sfedu.api;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import ru.sfedu.model.User;
import ru.sfedu.model.Result;
import ru.sfedu.model.Bond;
import ru.sfedu.model.Security;
import ru.sfedu.model.Stock;
import ru.sfedu.utils.ValidEntityListValidator;

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

public class DataProviderCSV implements DateProvider {
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
        log.info("Starting DataProviderCSV getCSVWriter[0]");
        try {
            log.debug("getCSVWriter[1]: {}", bean);
            log.debug("getCSVWriter[2]: Creating FileWriter");
            Files.createDirectories(Paths.get(getConfigurationEntry(CSV_PATH)));
            FileWriter writer = new FileWriter(getConfigurationEntry(CSV_PATH)
                    .concat(bean.getSimpleName().toUpperCase())
                    .concat(getConfigurationEntry(CSV_FILE_EXTENTION)), false);
            log.debug("getCSVWriter[3]: Creating CSVWriter");
            return new CSVWriter(writer);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSVWriter had failed[4]");
            throw new Exception(e);
        }
    }

    private <T> StatefulBeanToCsvBuilder<T> getBeanToCSVBuilder(CSVWriter writer) throws Exception {
        log.info("Starting DataProviderCSV getBeanToCSVBuilder[5]");
        try{
            log.info("getBeanToCSVBuilder[6]: {}", writer);
            return new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)   //
                    .withLineEnd(writer.DEFAULT_LINE_END);
        }catch (Exception e){
            log.error("Function DataProviderCSV getBeanToCSVBuilder had failed[7]");
            throw new Exception(e);
        }
    }

    private <T> void write(List<T> securityList, Class<T> security) throws Exception {
        log.info("Starting DataProviderCSV write[8]");
        try {
            log.info("write[9]: {}, {}", securityList, security);
            log.debug("write[10]: Creating csvWriter[7]");
            CSVWriter csvWriter = getCSVWriter(security);
            log.debug("write[11]: Creating StatefulBean[8]");
            StatefulBeanToCsvBuilder<T> beanToCsvBuilder = getBeanToCSVBuilder(csvWriter);
            StatefulBeanToCsv<T> beanToCsv = beanToCsvBuilder.build();
            log.debug("write[12]: Writing to csv file[9]");
            beanToCsv.write(securityList);
            log.debug("write[13]: Closing CSVWriter[10]");
            csvWriter.close();
        } catch (Exception e){
            log.error("Function DataProviderCSV write had failed[14]");
            throw new Exception(e);
        }
    }


    private <T> CSVReader getCSWReader(Class<T> ob) throws Exception {
        log.info("Starting DataProviderCSV getCSVReader[15]");
        try {
            log.info("getCSVWriter[16]: {}, type: {}", ob, ob.getSimpleName());
            log.debug("Creating FileReader[17]");
            FileReader reader = new FileReader(getConfigurationEntry(CSV_PATH)
                    .concat(ob.getSimpleName().toUpperCase())
                    .concat(getConfigurationEntry(CSV_FILE_EXTENTION)));
            log.debug("Creating CSWReader[18]");
            return new CSVReader(reader);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSWReader had failed[19]");
            throw new Exception(e);
        }
    }

    private <T> CsvToBeanBuilder<T> getCsvToBeanBuilder(Class<T> bean) throws Exception {
        log.info("Starting DataProviderCSV getCsvToBeanBuilder[20]");
        try
        {
            log.info("getCsvToBeanBuilder[21]: {}, type: {}", bean, bean.getSimpleName());
            log.debug("Creating CSVReader[22]");
            CSVReader reader = getCSWReader(bean);
            log.debug("Creating CsvToBeanBuilder[23]");
            return new CsvToBeanBuilder<>(reader);
        }catch (Exception e){
            log.error("Function DataProviderCSV CsvToBeanBuilder had failed[24]");
            throw new Exception(e);
        }
    }

    private <T> List<T> read(Class<T> bean) throws Exception {
        log.info("Starting DataProviderCSV write[25]");
        try {
            log.info("write[26]: {}", bean);
            log.debug("write[27]: creating csvToBean");
            CsvToBeanBuilder<T> csvToBeanBuilder = getCsvToBeanBuilder(bean);
            CsvToBean<T> csvToBean = csvToBeanBuilder
                    .withType(bean)
                    .build();
            log.debug("Parse CSVToBean[28]");
            return csvToBean.parse();
        }catch (Exception e){
            log.error("Function read had failed[29]");
            throw new Exception(e);
        }
    }

    private <T extends Security> Result<T> appendSecurities(List<T> list, Class<T> security) {
        log.info("Starting DataProviderCSV appendSecurities[30]");
        List<T> oldList = new ArrayList<>(getSecurities(security).getBody());
        try
        {
            ValidEntityListValidator.isValidSecurity(list);
            log.info("appendSecurities[31]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
            List<String> tickerList = oldList.stream().map(T::getTicker).toList();
            oldList.addAll(list.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
            oldList.sort(Comparator.comparing(T::getTicker));
            List<T> response = list.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            log.debug("appendSecurities[32]: write to csv file");
            write(oldList, security);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been appended successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been appended: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV appendSecurities had crashed[33]");
            return new Result<>(FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    private <T extends Security> Result<T> getSecurities(Class<T>securityClass) {
        log.info("Starting DataProviderCSV getSecurities[34]");
        try
        {
            List<T> response = new ArrayList<>(read(securityClass));
            return new Result<>(SUCCESS, String.format("Number of securities in file: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV getSecurities had crashed[35]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Optional<T> deleteSecurityByTicker(String ticker, Class<T> securityClass) throws Exception {
        log.info("Starting DataProviderCSV deleteSecurityByTicker[36]");
        try {
            log.info("deleteSecurityByTicker[37]: {}, type: {}",ticker, ticker.getClass());
            log.debug("deleteSecurityByTicker: GetSecurity from csv file[38]");
            List<T> securities = new ArrayList<>(read(securityClass));
            log.debug("deleteSecurityByTicker[39]: Search a {} by ticker {}",securityClass.getSimpleName(), ticker);
            Optional<T> sec = securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(sec.isPresent()){
                securities.remove(sec.get());
                write(securities, securityClass);
                MongoHistory.save(DELETE, CSV, sec.get());
            } else
                log.warn("deleteSecurityByTicker[40]: {} wasn't found by ticker {}",securityClass.getSimpleName(), ticker);
            return sec;
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteSecurityByTicker has crashed[41]");
            throw new Exception(e);
        }
    }

    private <T extends Security> Result<T> deleteAllSecurities(Class<T> securityClass)  {
        log.info("Starting DataProviderCSV deleteAllSecurities[42]");
        try {
            log.info("deleteAllSecurities[43]: {}", securityClass);
            log.debug("deleteAllSecurities[44]: get securities from file");
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("deleteAllSecurities[45]: delete all securities");
            write(new ArrayList<>(), securityClass);
            MongoHistory.save(DELETE, CSV, securityList);
            return new Result<>(SUCCESS, String.format("Number of deleted securities: %d", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllSecurities had failed[46]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    private <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class<T> securityClass) throws Exception {
        log.info("Starting DataProviderCSV getSecurityByTicker[47]");
        log.info("getSecurityByTicker[48]: {}, type: {}", ticker, ticker.getClass());
        try {
            log.debug("getSecurityByTicker[49]: Get {} from CSV[]", securityClass.getSimpleName());
            List<T> securityList = new ArrayList<>(read(securityClass));
            log.debug("getSecurityByTicker[50]: Search for a {} by ticker {}[]", securityClass.getSimpleName(), ticker);
            return securityList.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getSecurityByTicker had failed[51]");
            throw new Exception(e);
        }
    }

    private <T extends Security> Result<T> updateSecurities(List<T> securities, Class<T> securityClass)  {
        log.info("Starting DataProviderCSV updateSecurities[52]");
        try {
            ValidEntityListValidator.isValidSecurity(securities);
            log.info("updateSecurities[53]: {}, type: {}", Arrays.toString(securities.toArray()), securities.getClass());
            List<T> oldList = new ArrayList<>(read(securityClass));
            List<String> tickerList = getSecurityTicker(oldList);
            List<T> response = new ArrayList<>(securities.stream().filter(x -> !tickerList.contains(x.getTicker())).toList());
            log.debug("updateSecurities[54]: Update csv file: {}", securityClass.getSimpleName());
            List<T> securityToUpdate = securities.stream().filter(x -> tickerList.contains(x.getTicker())).toList();
            write(Stream.concat(securityToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(T::getTicker)).toList(), securityClass);
            MongoHistory.save(UPDATE, CSV, securityToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Securities have been updated successfully", response);
            return new Result<>(WARN, String.format("Number of securities that haven't been updated: %d", response.size()),response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed[55]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    @Override
    public Result<User> getUsers()  {
        log.info("Starting DataProviderCSV getUsers[56]");
        try
        {
            List<User> response =  new ArrayList<>(read(User.class));
            return new Result<>(SUCCESS, String.format("Number of users in file: %d", response.size()), response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV getUsers had crashed[57]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> appendUsers(List<User> list)  {
        log.info("Starting DataProviderCSV appendSecurities[58]");
        List<User> oldList = new ArrayList<>(getUsers().getBody());
        try
        {
            ValidEntityListValidator.isValidUser(list);
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
            log.error("Function DataProviderCSV appendUsers had crashed[59]");
            return new Result<>(FAIL, e.getMessage(),new ArrayList<>());
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users)  {
        log.info("Starting DataProviderCSV updateUsers[60]");
        try {

            log.info("updateUsers[61]: {}, type: {}", Arrays.toString(users.toArray()), users.getClass());
            ValidEntityListValidator.isValidUserToUpdate(users);
            List<User> oldList = new ArrayList<>(read(User.class));
            List<Long> idList = getUsersId(oldList);
            List<User> response = users.stream().filter(x -> !idList.contains(x.getId())).toList();
            log.debug("updateUsers[62]: Update csv file: {}", "user");
            List<User> userToUpdate = users.stream().filter(x -> idList.contains(x.getId())).toList();
            write(Stream.concat(userToUpdate.stream(), oldList.stream())
                    .distinct().sorted(Comparator.comparing(User::getId)).toList(), User.class);
            MongoHistory.save(UPDATE, CSV, userToUpdate);
            if (response.isEmpty())
                return new Result<>(SUCCESS, "Users have been updated successfully", response);
            return new Result<>(WARN, String.format("Number of users that haven't been updated: %d", response.size()),response);
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed[63]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> deleteUserById(long id) throws Exception {
        log.info("Starting DataProviderCSV deleteUserById[64]");
        try {
            log.info("deleteUserById[65]: {}",id);
            log.debug("deleteUserById[]: GetSecurity from csv file[66]");
            List<User> users = new ArrayList<>(read(User.class));
            log.debug("deleteUserById[67]: Search a user by id {}",id);
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            if(user.isPresent()) {
                users.remove(user.get());
                log.debug("deleteUserById[68]: Update CSV File");
                write(users, User.class);
                MongoHistory.save(DELETE, CSV, user.get());
            }
            return user;
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteUserById has crashed[69]");
            throw new Exception(e);
        }
    }


    @Override
    public Result<User> deleteAllUsers()  {
        log.info("Starting DataProviderCSV deleteAllUsers[70]");
        try {
            log.debug("deleteAllUsers[71]: get users from file");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("deleteAllUsers[72]: delete all securities");
            write(new ArrayList<>(), User.class);
            MongoHistory.save(DELETE, CSV, securityList);
            return new Result<>(SUCCESS, String.format("Number of deleted users: %d ", securityList.size()), securityList);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAllUsers had failed[73]");
            return new Result<>(FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Optional<User> getUserById(long id) throws Exception {
        log.info("Starting DataProviderCSV getUserById[74]");
        log.info("getUserById[75]: {}",id);
        try {
            log.debug("getUserById[76]: Get users from CSV file");
            List<User> securityList = new ArrayList<>(read(User.class));
            log.debug("getUserById[77]: Search for the user by id {}", id);
            return securityList.stream().filter(x -> x.getId().equals(id)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV getUserById had failed[78]");
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
