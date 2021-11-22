package ru.sfedu.api;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.model.Result;
import ru.sfedu.model.entity.User;
import ru.sfedu.Constants;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.Constants.CSV_FILE_EXTENTION;
import static ru.sfedu.Constants.CSV_PATH;


public class DataProviderCSV implements IDateProvider {
    private final Logger log = (Logger) LogManager.getLogger(DataProviderCSV.class.getName());


    private <T> CSVWriter getCSVWriter(Class bean, boolean append) throws Exception {
        log.info("Starting DataCSVProvider getCSVWriter[6]");
        try {
            log.debug("getCSVWriter[7]: {}", bean);
            log.debug("Creating FileWriter[8]");
            FileWriter writer = new FileWriter(getConfigurationEntry(CSV_PATH)
                    + bean.getSimpleName().toLowerCase()
                    + getConfigurationEntry(CSV_FILE_EXTENTION), append);

            log.debug("Creating CSVWriter[9]");
            CSVWriter csvWriter = new CSVWriter(writer);
            return csvWriter;
        }catch (Exception e){
            log.error("Function DataCSVProvider getCSVWriter had failed[10]");
            throw new Exception(e);
        }
    }

    private <T> StatefulBeanToCsvBuilder getBeanToCSVBuilder(CSVWriter writer) throws Exception {
        log.info("Starting DataCSVProvider getBeanToCSVBuilder[11]");
        try{
            log.info("getBeanToCSVBuilder[12]: {}", writer);
            StatefulBeanToCsvBuilder<T> beanToCsvBuilder = new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)   //
                    .withLineEnd(writer.DEFAULT_LINE_END);
            return beanToCsvBuilder;


        }catch (Exception e){
            log.error("Function DataCSVProvider getBeanToCSVBuilder had failed[13]");
            throw new Exception(e);

        }
    }

    public Result<User> appendUsers(List<User> list) throws Exception {
        log.info("Starting DataCSVProvider appendUsers[0]");
        String status = Constants.SUCCESS;

        String message = "";
        Result<User> result = getUsers();
        // creating hashmap, key: userId value: User
        Map<Long, User> userNote = new HashMap<>();
        result.getBody().forEach(x -> userNote.put(x.getId(), x));
        try
        {
            log.info("appendUsers[1]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
            if (list.isEmpty()){
                log.error("Empty size[2]");
                throw new Exception("Empty size");
            }
            if (list.contains(null)){
                log.error("List contains null[3]");
                throw new Exception("List contains null");
            }

            log.debug("Status of getting file[4]: " + result.getStatus());

            for (User user : list.stream().filter(x -> x.getId() != null).toList()){
                if (!userNote.containsKey(user.getId()))
                    userNote.put(user.getId(), user);
                else{
                    log.error("User with this ID already exists[5]: {}", user);
                    message = "User with this ID already exists";
                    return new Result<User>(Constants.FAIL, message, new ArrayList<User>(List.of(user)));
                }

            }
            ArrayList<User> userWithoutId = new ArrayList<>(list.stream().filter(x -> x.getId() == null).toList());
            for (long i = 0; ; i++){
                if (userWithoutId.isEmpty())
                    break;
                if (!userNote.containsKey(i)){
                    User user = userWithoutId.remove(0);
                    user.setId(i);
                    userNote.put(i, user);
                }
            }
            log.debug("Creating csvWriter[7]");
            CSVWriter csvWriter = getCSVWriter(User.class, false);
            log.debug("Creating StatefulBean[8]");
            StatefulBeanToCsv<User> beanToCsv = getBeanToCSVBuilder(csvWriter).build();
            log.debug("Writing to csv file[9]");
            beanToCsv.write(userNote.values().stream().toList());
            log.debug("Closing CSVWriter[10]");
            csvWriter.close();
        } catch (Exception e) {
            log.error("Function DataCSVProvider appendUsers had crashed[11]");
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<User>(status, message,new ArrayList<>());
    }


    private <T> CSVReader getCSWReader(Class ob) throws Exception {
        log.info("Starting DataCSVProvider getCSVReader[12]");
        try {
            log.info("getCSVWriter[13]: {}, type: {}", ob, ob.getClass());
            log.debug("Creating FileReader[14]");
            FileReader reader = new FileReader(getConfigurationEntry(CSV_PATH)
                    + ob.getSimpleName().toLowerCase()
                    + getConfigurationEntry(CSV_FILE_EXTENTION));
            log.debug("Creating CSWReader[15]");
            CSVReader csvReader = new CSVReader(reader);

            return csvReader;
        }catch (Exception e){

            log.error("Function DataCSVProvider getCSWReader had failed[16]");
            throw new Exception(e);
        }
    }


    private <T extends User> CsvToBeanBuilder getCsvToBeanBuilder(Class bean) throws Exception {
        log.info("Starting DataCSVProvider getCsvToBeanBuilder[17]");

        try
        {
            log.info("getCsvToBeanBuilder[18]: {}, type: {}", bean, bean.getClass());
            log.debug("Creating CSVReader[19]");
            CSVReader reader = getCSWReader(bean);
            log.debug("Creating CsvToBeanBuilder[20]");
            CsvToBeanBuilder<T> csvToBean = new CsvToBeanBuilder<T>(reader);

            return csvToBean;
        }catch (Exception e){
            log.error("Function DataCSVProvider CsvToBeanBuilder had failed[21]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<User> getUsers() throws Exception {
        log.info("Starting DataProviderCSV getUsers[22]");
        log.info("getUsers[23]");
        String status = Constants.SUCCESS;
        String message = "";
        List<User> body = new ArrayList<>();
        try
        {
            log.debug("Creating CsvToBean[24]");
            CsvToBean<User> csvToBean = getCsvToBeanBuilder(User.class)
                    .withType(User.class)
                    .build();

            log.debug("Parse CSVToBean[25]");
            body = csvToBean.parse();
        } catch (Exception e) {
            log.error("Function DataProviderCSV getUsers had crashed[26]");
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<User>(status,message,body);

    }



    @Override
    public Result<User> deleteUserById(long id) throws Exception {
        log.info("Starting DataCSVProvider deleteUserById[27]");
        String status = Constants.SUCCESS;
        String message = "";
        try {
            log.info("deleteUserById[28]: {}, type: {}",id, "long");
            log.debug("GetUsers from csv[29]");
            Result<User> result = getUsers();
            if (result.getStatus().equals(Constants.FAIL)){
                return result;
            }
            List<User> users = result.getBody();
            log.debug("Search a user by id[30]");
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            if(user.isPresent()){
                log.debug("Delete user by id[31]");
                users.remove(user.get());
            }
            else{
                log.error("User wasn't found by id[32]");
                return new Result<>(Constants.FAIL, "User wasn't found by id: " + id, new ArrayList<>());
            }
            log.debug("Update CSV File[33]");
            deleteAllUsers();
            return appendUsers(users);
        }catch (Exception e){
            log.error("Function DataCSVProvider deleteUserById has crashed[34]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> deleteAllUsers() throws Exception {
        log.info("Starting DataProviderCSV deleteAllUsers[35]");
        String status = Constants.SUCCESS;
        String message = "";
        Result<User> result = getUsers();

        try (
                CSVWriter csvWriter = getCSVWriter(User.class, false);
                ){
        if (result.getStatus().equals(Constants.FAIL)){
            return result;
        }
        //csvWriter.writeNext(null);

        }catch (Exception e){
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<User>(status, message, new ArrayList<>());
    }

    @Override
    public User getUserById(long id) throws Exception {
        log.info("Starting DataCSVProvider getUserById[36]");
        log.info("getUserById[37]: {}, type: {}", id, "long");
        try {
            log.debug("Get Users from CSV[38]");
            Result<User> result = getUsers();
            if (result.getStatus().equals(Constants.FAIL))
                throw  new Exception(result.getMessage());
            List<User> users = result.getBody();
            log.debug("Search for a user by id[39]");
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            return user.orElseGet(User::new);
        }catch (Exception e){
            log.error("Function DataCSVProvider getUserById had failed[40]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users) throws Exception {
        log.info("Starting DataCSVProvider updateUsers[41]");
        Result<User> result = getUsers();
        String status = Constants.SUCCESS;
        String message = "";
        try {
            log.info("updateUsers: {}, type: {}[42]", Arrays.toString(users.toArray()), users.getClass());
            if (users.isEmpty()) {
                log.error("Empty size[43]");
                throw new Exception("Empty size");
            }
            if (users.contains(null)) {
                log.error("List contains null[44]");
                throw new Exception("List contains null");
            }
            if (result.getStatus().equals(Constants.FAIL)){
                log.error("File wasn't found[45]");
                throw new Exception("File wasn't found");
            }


            log.debug("Status of getting file[46]: " + result.getStatus());
            Map<Long, User> usersFromCSV = new TreeMap<>();
            result.getBody().forEach(x -> usersFromCSV.put(x.getId(), x));

            for (User user : users){
                if (user.getId() == null){
                    log.error("ID contain null: {}[47]", user);
                    return new Result<User>(Constants.FAIL, "ID contain null", new ArrayList<>(List.of(user)));
                }

                if (!usersFromCSV.containsKey(user.getId())){
                    log.error("ID wasn't found: {}[48]", user);
                    return new Result<User>(Constants.FAIL, "ID wasn't found", new ArrayList<>(List.of(user)));
                }

                usersFromCSV.put(user.getId(), user);
            }
            log.debug("appendUsers[49]");
            deleteAllUsers();
            return appendUsers(usersFromCSV.values().stream().toList());
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed[50]");
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<User>(status, message, new ArrayList<>());
    }
}
