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
import ru.sfedu.model.entity.Bond;
import ru.sfedu.model.entity.Security;
import ru.sfedu.model.entity.Stock;
import ru.sfedu.Constants;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static ru.sfedu.utils.ConfigurationUtil.getConfigurationEntry;
import static ru.sfedu.Constants.CSV_FILE_EXTENTION;
import static ru.sfedu.Constants.CSV_PATH;

public class DataProviderCSV implements IDateProvider{
    private final Logger log = (Logger) LogManager.getLogger(DataProviderCSV.class.getName());
    private <T> CSVWriter getCSVWriter(Class bean, boolean append) throws Exception {
        log.info("Starting DataProviderCSV getCSVWriter[6]");
        try {
            log.debug("getCSVWriter[7]: {}", bean);
            log.info(getConfigurationEntry(CSV_PATH));
            log.debug("Creating FileWriter[8]");

            FileWriter writer = new FileWriter(getConfigurationEntry(CSV_PATH)
                    + bean.getSimpleName().toLowerCase()
                    + getConfigurationEntry(CSV_FILE_EXTENTION), append);

            log.debug("Creating CSVWriter[9]");
            return new CSVWriter(writer);
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSVWriter had failed[10]");
            throw new Exception(e);
        }
    }

    private <T> StatefulBeanToCsvBuilder getBeanToCSVBuilder(CSVWriter writer) throws Exception {
        log.info("Starting DataProviderCSV getBeanToCSVBuilder[11]");
        try{
            log.info("getBeanToCSVBuilder[12]: {}", writer);
            return new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)   //
                    .withLineEnd(writer.DEFAULT_LINE_END);

        }catch (Exception e){
            log.error("Function DataProviderCSV getBeanToCSVBuilder had failed[13]");
            throw new Exception(e);

        }
    }

    public <T extends Security> Result<T> appendSecurities(List<T> list, Class security) throws Exception {
        log.info("Starting DataProviderCSV append{}[0]", security.getSimpleName());
        String status = Constants.SUCCESS;

        String message = "";
        Result<T> result = getSecurities(security);
        // creating hashmap, key: StockTicker value: Stock
        Map<String, T> securityNote = new HashMap<>();
        result.getBody().forEach(x -> securityNote.put(x.getTicker(), x));
        try
        {
            log.info("append{}s[1]: {}, type: {}",security.getSimpleName(), Arrays.toString(list.toArray()), list.getClass().getName());
            if (list.isEmpty()){
                log.error("Empty size[2]");
                throw new Exception("Empty size");
            }
            if (list.contains(null)){
                log.error("List contains null[3]");
                throw new Exception("List contains null");
            }

            log.debug("Status of getting file[4]: " + result.getStatus());

            for (T t : list.stream().filter(x -> x.getTicker() != null).toList()){
                if (!securityNote.containsKey(t.getTicker()))
                    securityNote.put(t.getTicker(), t);
                else{
                    log.error("{} with this ticker already exists[5]: {}",security.getSimpleName(), t);
                    message =  String.format("%s with this ticker already exists",security.getSimpleName());
                    return new Result<T>(Constants.FAIL, message, new ArrayList<T>(List.of(t)));
                }

            }
            ArrayList<T> securityWithoutTicker = new ArrayList<>(list.stream().filter(x -> x.getTicker() == null).toList());
            for (int i = 0; ; i++){
                if (securityWithoutTicker.isEmpty())
                    break;
                if (!securityNote.containsKey(i)){
                    T t = securityWithoutTicker.remove(0);
                    t.setTicker(Integer.toString(i));
                    securityNote.put(Integer.toString(i), t);
                }
            }
            log.debug("Creating csvWriter[7]");
            CSVWriter csvWriter = getCSVWriter(Stock.class, false);
            log.debug("Creating StatefulBean[8]");
            StatefulBeanToCsv<T> beanToCsv = getBeanToCSVBuilder(csvWriter).build();
            log.debug("Writing to csv file[9]");
            beanToCsv.write(securityNote.values().stream().toList());
            log.debug("Closing CSVWriter[10]");
            csvWriter.close();
        } catch (Exception e) {
            log.error("Function DataProviderCSV append{} had crashed[11]", security.getSimpleName());
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<T>(status, message,new ArrayList<>());
    }

    private <T> CSVReader getCSWReader(Class ob) throws Exception {
        log.info("Starting DataProviderCSV getCSVReader[12]");
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

            log.error("Function DataProviderCSV getCSWReader had failed[16]");
            throw new Exception(e);
        }
    }


    private <T> CsvToBeanBuilder getCsvToBeanBuilder(Class bean) throws Exception {
        log.info("Starting DataProviderCSV getCsvToBeanBuilder[17]");

        try
        {
            log.info("getCsvToBeanBuilder[18]: {}, type: {}", bean, bean.getClass());
            log.debug("Creating CSVReader[19]");
            CSVReader reader = getCSWReader(bean);
            log.debug("Creating CsvToBeanBuilder[20]");
            CsvToBeanBuilder<T> csvToBean = new CsvToBeanBuilder<T>(reader);

            return csvToBean;
        }catch (Exception e){
            log.error("Function DataProviderCSV CsvToBeanBuilder had failed[21]");
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> getSecurities(Class securityClass) throws Exception {
        log.info("Starting DataProviderCSV get{}s[22]",securityClass.getSimpleName());
        String status = Constants.SUCCESS;
        String message = "";
        List<T> body = new ArrayList<>();
        try
        {
            log.debug("Creating CsvToBean[23]");
            CsvToBean<T> csvToBean = getCsvToBeanBuilder(securityClass)
                    .withType(securityClass)
                    .build();

            log.debug("Parse CSVToBean[24]");
            body = csvToBean.parse();
        } catch (Exception e) {
            log.error("Function DataProviderCSV get{}s had crashed[25]",securityClass.getSimpleName());
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<T>(status,message,body);

    }


    public <T extends Security> Result<T> deleteSecurityByTicker(String ticker, Class securityClass) throws Exception {
        log.info("Starting DataProviderCSV delete{}ByTicker[27]", securityClass.getSimpleName());
        String status = Constants.SUCCESS;
        String message = "";
        try {
            log.info("delete{}ByTicker[28]: {}, type: {}",securityClass.getSimpleName(),ticker, "long");
            log.debug("Get{}s from csv[29]", securityClass.getSimpleName());
            Result<T> result = getSecurities(securityClass);
            if (result.getStatus().equals(Constants.FAIL)){
                return result;
            }
            List<T> securities = result.getBody();
            log.debug("Search a {} by ticker[30]",securityClass.getSimpleName());
            Optional<T> sec = securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(sec.isPresent()){
                log.debug("Delete {} by ticker[31]",securityClass.getSimpleName());
                securities.remove(sec.get());
            }
            else{
                log.error("{} wasn't found by ticker[32]",securityClass.getSimpleName());
                return new Result<>(Constants.FAIL, String.format("%s wasn't found by ticker: %s",securityClass.getSimpleName(), ticker), new ArrayList<>());
            }
            log.debug("Update CSV File[33]");
            deleteAllSecurities(securityClass);
            return appendSecurities(securities, securityClass);
        }catch (Exception e){
            log.error("Function DataProviderCSV delete{}ByTicker has crashed[34]", securityClass);
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }


    public <T extends Security> Result<T> deleteAllSecurities(Class securityClass) throws Exception {
        log.info("Starting DataProviderCSV deleteAll{}s[35]", securityClass.getSimpleName());
        String status = Constants.SUCCESS;
        String message = "";
        Result<Stock> result = getSecurities(securityClass);

        try (
                CSVWriter csvWriter = getCSVWriter(securityClass, false);
        ){
            if (result.getStatus().equals(Constants.FAIL)){
                throw new Exception(result.getMessage());
            }
            //csvWriter.writeNext(null);

        }catch (Exception e){
            log.error("Function DataProviderCSV deleteAll{}s had failed[35.6]", securityClass.getSimpleName());
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<T>(status, message, new ArrayList<>());
    }

    public <T extends Security> Optional<T> getSecurityByTicker(String ticker, Class securityClass) throws Exception {
        log.info("Starting DataProviderCSV get{}ByTicker[36]", securityClass.getSimpleName());
        log.info("get{}ByTicker[37]: {}, type: {}",securityClass.getSimpleName(), ticker, ticker.getClass());
        try {
            log.debug("Get {} from CSV[38]", securityClass.getSimpleName());
            Result<T> result = getSecurities(securityClass);
            if (result.getStatus().equals(Constants.FAIL))
                throw  new Exception(result.getMessage());
            List<T> securities = result.getBody();
            log.debug("Search for a {} by ticker[39]", securityClass.getSimpleName());
            return securities.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
        }catch (Exception e){
            log.error("Function DataProviderCSV get{}ByTicker had failed[40]", securityClass.getSimpleName());
            throw new Exception(e);
        }
    }

    public <T extends Security> Result<T> updateSecurities(List<T> securities, Class securityClass) throws Exception {
        log.info("Starting DataProviderCSV update{}s[41]", securityClass.getSimpleName());
        Result<T> result = getSecurities(securityClass);
        String status = Constants.SUCCESS;
        String message = "";
        try {
            log.info("update{}s: {}, type: {}[42]",securityClass.getSimpleName()
                    , Arrays.toString(securities.toArray()), securities.getClass());
            if (securities.isEmpty()) {
                log.error("Empty size[43]");
                throw new Exception("Empty size");
            }
            if (securities.contains(null)) {
                log.error("List contains null[44]");
                throw new Exception("List contains null");
            }
            if (result.getStatus().equals(Constants.FAIL)){
                log.error("File wasn't found[45]");
                throw new Exception("File wasn't found");
            }


            log.debug("Status of getting file[46]: " + result.getStatus());
            Map<String, T> securityFromCSV = new TreeMap<>();
            result.getBody().forEach(x -> securityFromCSV.put(x.getTicker(), x));

            for (T t : securities){
                if (t.getTicker() == null){
                    log.error("Ticker contain null: {}[47]", t);
                    return new Result<>(Constants.FAIL, "Ticker contain null", new ArrayList<>(List.of(t)));
                }

                if (!securityFromCSV.containsKey(t.getTicker())){
                    log.error("Ticker wasn't found: {}[48]", t);
                    return new Result<>(Constants.FAIL, "Ticker wasn't found", new ArrayList<>(List.of(t)));
                }

                securityFromCSV.put(t.getTicker(), t);
            }
            log.debug("append{}s[49]", securityClass.getSimpleName());
            deleteAllSecurities(securityClass);
            return appendSecurities(securityFromCSV.values().stream().toList(), securityClass);
        } catch (Exception e) {
            log.error("Function DataProviderCSV update{}s had crashed[50]", securityClass.getSimpleName());
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<T>(status, message, new ArrayList<>());
    }



    public Result<Stock> appendStocks(List<Stock> list) throws Exception {
        return appendSecurities(list, Stock.class);

    }

    public Result<Bond> appendBonds(List<Bond> list) throws Exception {
        return appendSecurities(list, Bond.class);
    }

    public Result<Stock> getStocks() throws Exception {
        return getSecurities(Stock.class);
    }

    public Result<Bond> getBonds() throws Exception {
        return getSecurities(Bond.class);
    }

    public Result<Stock> deleteStockByTicker(String ticker) throws Exception {
        return deleteSecurityByTicker(ticker, Stock.class);
    }

    public Result<Bond> deleteBondByTicker(String ticker) throws Exception {
        return deleteSecurityByTicker(ticker, Bond.class);
    }
    public Result<Stock> deleteAllStocks() throws Exception {
        return deleteAllSecurities(Stock.class);
    }

    public Result<Bond> deleteAllBonds() throws Exception {
        return deleteAllSecurities(Bond.class);
    }

    public Optional<Stock> getStockByTicker(String ticker) throws Exception {
        return getSecurityByTicker(ticker, Stock.class);
    }

    public Optional<Bond> getBondByTicker(String ticker) throws Exception {
        return getSecurityByTicker(ticker, Bond.class);
    }

    public Result<Stock> updateStocks(List<Stock> list) throws Exception {
        return updateSecurities(list, Stock.class);
    }

    public Result<Bond> updateBonds(List<Bond> list) throws Exception {
        return updateSecurities(list, Bond.class);
    }
}
