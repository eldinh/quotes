package ru.sfedu.api;


import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.model.Result;
import ru.sfedu.model.dao.User;
import ru.sfedu.utils.ConfigurationUtil;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class DataProviderCSV implements IDateProvider {
    private final Logger log = (Logger) LogManager.getLogger(DataProviderCSV.class.getName());
    private final String PATH = "csv_path";
    private final String FILE_EXTENTION = "csv";

    private <T> CSVWriter getCSVWriter(Class bean, boolean append) throws Exception {
        log.info("Starting DataCSVProvider getCSVWriter[6]");
        try {
            log.debug("getCSVWriter[7]: {}", bean);
            log.debug("Creating FileWriter[8]");
            FileWriter writer = new FileWriter(ConfigurationUtil.getConfigurationEntry(PATH)
                    + bean.getSimpleName().toLowerCase()
                    + ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION), append);

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
        log.info("appendUsers[1]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
        String status = "Success";
        String message = "";
        Result<User> result = getUsers();
        // creating hashmap, key: userId value: User
        Map<Long, User> userNote = new HashMap<>();
        result.getBody().forEach(x -> userNote.put(x.getId(), x));
        try
        {
            if (list.isEmpty()){
                log.error("Empty size");
                throw new Exception("Empty size");
            }
            if (list.contains(null)){
                log.error("List contains null");
                throw new Exception("List contains null");
            }

            log.debug("Status of getting file: " + result.getStatus());

            for (User user : list.stream().filter(x -> x.getId() != null).toList()){
                if (!userNote.containsKey(user.getId()))
                    userNote.put(user.getId(), user);
                else{
                    log.error("User with this ID already exists: {}", user);
                    message = "User with this ID already exists";
                    return new Result<User>("Fail", message, new ArrayList<User>(List.of(user)));
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
            log.debug("Creating csvWriter");
            CSVWriter csvWriter = getCSVWriter(User.class, false);
            log.debug("Creating StatefulBean");
            StatefulBeanToCsv<User> beanToCsv = getBeanToCSVBuilder(csvWriter).build();
            log.debug("Writing to csv file");
            beanToCsv.write(userNote.values().stream().toList());
            log.debug("Closing CSVWriter");
            csvWriter.close();
        } catch (Exception e) {
            log.error("Function DataCSVProvider appendUsers had crashed[5]");
            status = "Fail";
            message = e.getMessage();
        }
        return new Result<User>(status, message,new ArrayList<>());
    }


    private <T> CSVReader getCSWReader(Class ob) throws Exception {
        log.info("Starting DataCSVProvider getCSVReader[19]");
        try {
            log.debug("getCSVWriter[20]: {}, type: {}", ob, ob.getClass());
            log.debug("Creating FileReader[21]");
            FileReader reader = new FileReader(ConfigurationUtil.getConfigurationEntry(PATH)
                    + ob.getSimpleName().toLowerCase()
                    + ConfigurationUtil.getConfigurationEntry(FILE_EXTENTION));
            log.debug("Creating CSWReader[22]");
            CSVReader csvReader = new CSVReader(reader);

            return csvReader;
        }catch (Exception e){

            log.error("Function DataCSVProvider getCSWReader had failed[23]");
            throw new Exception(e);
        }
    }


    private <T extends User> CsvToBeanBuilder getCsvToBeanBuilder(Class bean) throws Exception {
        log.info("Starting DataCSVProvider getCsvToBeanBuilder[24]");
        log.info("getCsvToBeanBuilder[25]: {}, type: {}", bean, bean.getClass());
        try
        {
            log.debug("Creating CSVReader[26]");
            CSVReader reader = getCSWReader(bean);
            log.debug("Creating CsvToBeanBuilder[27]");
            CsvToBeanBuilder<T> csvToBean = new CsvToBeanBuilder<T>(reader);

            return csvToBean;
        }catch (Exception e){
            log.error("Function DataCSVProvider CsvToBeanBuilder had failed[28]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<User> getUsers() throws Exception {
        log.info("Starting DataCSVProvider getUsers[13]");
        log.info("getUsers[14]");
        String status = "";
        String message = "";
        List<User> body = new ArrayList<>();
        try
        {
            log.debug("Creating CsvToBean[15]");
            CsvToBean<User> csvToBean = getCsvToBeanBuilder(User.class)
                    .withType(User.class)
                    .build();

            log.debug("Parse CSVToBean[16]");
            body = csvToBean.parse();
            status = "Success";
        } catch (Exception e) {
            log.error("Function DataCSVProvider getUsers had crashed[18]");
            status = "Fail";
            message = e.getMessage();
        }
        return new Result<User>(status,message,body);

    }



    @Override
    public Result<User> deleteUserById(long id) throws Exception {
        log.info("Starting DataCSVProvider deleteUserById[29");
        log.info("deleteUserById[30]: {}, type: {}",id, "long");
        String status = "Success";
        String message = "";
        try {
            log.debug("GetUsers from csv[31]");
            Result<User> result = getUsers();
            if (result.getStatus().equals("Fail")){
                return result;
            }
            List<User> users = result.getBody();
            log.debug("Search a user by id[32]");
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            if(user.isPresent()){
                log.debug("Delete user by id[33]");
                users.remove(user.get());
            }
            else{
                log.error("User wasn't found by id[34]");
                return new Result<>("Fail", "User wasn't found by id: " + id, new ArrayList<>());
            }
            log.debug("Update CSV File[35]");
            deleteAllUsers();
            return appendUsers(users);
        }catch (Exception e){
            log.error("Function DataCSVProvider deleteUserById has crashed[36]");
            return new Result<>("Fail", e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<User> deleteAllUsers() throws Exception {
        log.info("Starting DataProviderCSV deleteAllUsers");
        String status = "";
        String message = "";
        Result<User> result = getUsers();

        try (
                CSVWriter csvWriter = getCSVWriter(User.class, false);
                ){
        if (result.getStatus().equals("Fail")){
            return result;
        }
        //csvWriter.writeNext(null);
        status = "Success";

        }catch (Exception e){
            status = "Fail";
            message = e.getMessage();
        }
        return new Result<User>(status, message, new ArrayList<>());
    }

    @Override
    public User getUserById(long id) throws Exception {
        log.info("Starting DataCSVProvider getUserById[37]");
        log.info("getUserById[38]: {}, type: {}", id, "long");
        try {
            log.debug("Get Users from CSV[39]");
            Result<User> result = getUsers();
            if (result.getStatus().equals("Fail"))
                throw  new Exception(result.getMessage());
            List<User> users = result.getBody();
            log.debug("Search for a user by id[40]");
            Optional<User> user = users.stream().filter(x -> x.getId().equals(id)).findFirst();
            return user.orElseGet(User::new);
        }catch (Exception e){
            log.error("Function DataCSVProvider getUserById had failed[42]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<User> updateUsers(List<User> users) throws Exception {
        log.info("Starting DataCSVProvider updateUsers");
        log.info("updateSuers: {}, type: {}", Arrays.toString(users.toArray()), users.getClass());
        Result<User> result = getUsers();
        String status = "Success";
        String message = "";
        try {
            if (users.isEmpty()) {
                log.error("Empty size");
                throw new Exception("Empty size");
            }
            if (users.contains(null)) {
                log.error("List contains null");
                throw new Exception("List contains null");
            }


            log.debug("Status of getting file: " + result.getStatus());
            Map<Long, User> usersFromCSV = new TreeMap<>();
            result.getBody().forEach(x -> usersFromCSV.put(x.getId(), x));

            for (User user : users){
                if (user.getId() == null){
                    log.error("ID contain null: {}", user);
                    return new Result<User>("Fail", "ID contain null", new ArrayList<>(List.of(user)));
                }

                if (!usersFromCSV.containsKey(user.getId())){
                    log.error("ID wasn't found: {}", user);
                    return new Result<User>("Fail", "ID wasn't found", new ArrayList<>(List.of(user)));
                }

                usersFromCSV.put(user.getId(), user);
            }
            log.debug("Creating csvWriter");
            CSVWriter csvWriter = getCSVWriter(User.class, false);
            log.debug("Creating StatefulBean");
            StatefulBeanToCsv<User> beanToCsv = getBeanToCSVBuilder(csvWriter).build();
            log.debug("Update csv file");
            beanToCsv.write(usersFromCSV.values().stream().toList());
            log.debug("Closing CSVWriter");
            csvWriter.close();
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateUsers had crashed");
            status = "Fail";
            message = e.getMessage();
        }
        return new Result<User>(status, message, new ArrayList<>());
    }
}
