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
import ru.sfedu.model.entity.Stock;
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
        log.info("Starting DataProviderCSV getCSVWriter[6]");
        try {
            log.debug("getCSVWriter[7]: {}", bean);
            log.info(getConfigurationEntry(CSV_PATH));
            log.debug("Creating FileWriter[8]");

            FileWriter writer = new FileWriter(getConfigurationEntry(CSV_PATH)
                    + bean.getSimpleName().toLowerCase()
                    + getConfigurationEntry(CSV_FILE_EXTENTION), append);

            log.debug("Creating CSVWriter[9]");
            CSVWriter csvWriter = new CSVWriter(writer);
            return csvWriter;
        }catch (Exception e){
            log.error("Function DataProviderCSV getCSVWriter had failed[10]");
            throw new Exception(e);
        }
    }

    private <T> StatefulBeanToCsvBuilder getBeanToCSVBuilder(CSVWriter writer) throws Exception {
        log.info("Starting DataProviderCSV getBeanToCSVBuilder[11]");
        try{
            log.info("getBeanToCSVBuilder[12]: {}", writer);
            StatefulBeanToCsvBuilder<T> beanToCsvBuilder = new StatefulBeanToCsvBuilder<T>(writer)
                    .withApplyQuotesToAll(false)
                    .withOrderedResults(true)   //
                    .withLineEnd(writer.DEFAULT_LINE_END);
            return beanToCsvBuilder;

        }catch (Exception e){
            log.error("Function DataProviderCSV getBeanToCSVBuilder had failed[13]");
            throw new Exception(e);

        }
    }
    @Override
    public Result<Stock> appendStocks(List<Stock> list) throws Exception {
        log.info("Starting DataProviderCSV appendStocks[0]");
        String status = Constants.SUCCESS;

        String message = "";
        Result<Stock> result = getStocks();
        // creating hashmap, key: StockTicker value: Stock
        Map<String, Stock> stockNote = new HashMap<>();
        result.getBody().forEach(x -> stockNote.put(x.getTicker(), x));
        try
        {
            log.info("appendStocks[1]: {}, type: {}", Arrays.toString(list.toArray()), list.getClass().getName());
            if (list.isEmpty()){
                log.error("Empty size[2]");
                throw new Exception("Empty size");
            }
            if (list.contains(null)){
                log.error("List contains null[3]");
                throw new Exception("List contains null");
            }

            log.debug("Status of getting file[4]: " + result.getStatus());

            for (Stock Stock : list.stream().filter(x -> x.getTicker() != null).toList()){
                if (!stockNote.containsKey(Stock.getTicker()))
                    stockNote.put(Stock.getTicker(), Stock);
                else{
                    log.error("Stock with this ID already exists[5]: {}", Stock);
                    message = "Stock with this ID already exists";
                    return new Result<Stock>(Constants.FAIL, message, new ArrayList<Stock>(List.of(Stock)));
                }

            }
            ArrayList<Stock> StockWithoutId = new ArrayList<>(list.stream().filter(x -> x.getTicker() == null).toList());
            for (int i = 0; ; i++){
                if (StockWithoutId.isEmpty())
                    break;
                if (!stockNote.containsKey(i)){
                    Stock stock = StockWithoutId.remove(0);
                    stock.setTicker(Integer.toString(i));
                    stockNote.put(Integer.toString(i), stock);
                }
            }
            log.debug("Creating csvWriter[7]");
            CSVWriter csvWriter = getCSVWriter(Stock.class, false);
            log.debug("Creating StatefulBean[8]");
            StatefulBeanToCsv<Stock> beanToCsv = getBeanToCSVBuilder(csvWriter).build();
            log.debug("Writing to csv file[9]");
            beanToCsv.write(stockNote.values().stream().toList());
            log.debug("Closing CSVWriter[10]");
            csvWriter.close();
        } catch (Exception e) {
            log.error("Function DataProviderCSV appendStocks had crashed[11]");
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<Stock>(status, message,new ArrayList<>());
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


    private <T extends Stock> CsvToBeanBuilder getCsvToBeanBuilder(Class bean) throws Exception {
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

    @Override
    public Result<Stock> getStocks() throws Exception {
        log.info("Starting DataProviderCSV getStocks[22]");
        log.info("getStocks[23]");
        String status = Constants.SUCCESS;
        String message = "";
        List<Stock> body = new ArrayList<>();
        try
        {
            log.debug("Creating CsvToBean[24]");
            CsvToBean<Stock> csvToBean = getCsvToBeanBuilder(Stock.class)
                    .withType(Stock.class)
                    .build();

            log.debug("Parse CSVToBean[25]");
            body = csvToBean.parse();
        } catch (Exception e) {
            log.error("Function DataProviderCSV getStocks had crashed[26]");
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<Stock>(status,message,body);

    }



    @Override
    public Result<Stock> deleteStockByTicker(String ticker) throws Exception {
        log.info("Starting DataProviderCSV deleteUserById[27]");
        String status = Constants.SUCCESS;
        String message = "";
        try {
            log.info("deleteUserById[28]: {}, type: {}",ticker, "long");
            log.debug("GetStock from csv[29]");
            Result<Stock> result = getStocks();
            if (result.getStatus().equals(Constants.FAIL)){
                return result;
            }
            List<Stock> stocks = result.getBody();
            log.debug("Search a user by id[30]");
            Optional<Stock> user = stocks.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            if(user.isPresent()){
                log.debug("Delete user by id[31]");
                stocks.remove(user.get());
            }
            else{
                log.error("User wasn't found by id[32]");
                return new Result<>(Constants.FAIL, "User wasn't found by ticker: " + ticker, new ArrayList<>());
            }
            log.debug("Update CSV File[33]");
            deleteAllStocks();
            return appendStocks(stocks);
        }catch (Exception e){
            log.error("Function DataProviderCSV deleteUserById has crashed[34]");
            return new Result<>(Constants.FAIL, e.getMessage(), new ArrayList<>());
        }
    }

    @Override
    public Result<Stock> deleteAllStocks() throws Exception {
        log.info("Starting DataProviderCSV deleteAllStock[35]");
        String status = Constants.SUCCESS;
        String message = "";
        Result<Stock> result = getStocks();

        try (
                CSVWriter csvWriter = getCSVWriter(Stock.class, false);
                ){
        if (result.getStatus().equals(Constants.FAIL)){
            return result;
        }
        //csvWriter.writeNext(null);

        }catch (Exception e){
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<Stock>(status, message, new ArrayList<>());
    }

    @Override
    public Stock getStockByTicker(String ticker) throws Exception {
        log.info("Starting DataProviderCSV getUserById[36]");
        log.info("getUserById[37]: {}, type: {}", ticker, ticker.getClass());
        try {
            log.debug("Get Stock from CSV[38]");
            Result<Stock> result = getStocks();
            if (result.getStatus().equals(Constants.FAIL))
                throw  new Exception(result.getMessage());
            List<Stock> Stock = result.getBody();
            log.debug("Search for a user by id[39]");
            Optional<Stock> stock = Stock.stream().filter(x -> x.getTicker().equals(ticker)).findFirst();
            return stock.orElseGet(Stock::new);
        }catch (Exception e){
            log.error("Function DataProviderCSV getUserById had failed[40]");
            throw new Exception(e);
        }
    }

    @Override
    public Result<Stock> updateStocks(List<Stock> Stock) throws Exception {
        log.info("Starting DataProviderCSV updateStock[41]");
        Result<Stock> result = getStocks();
        String status = Constants.SUCCESS;
        String message = "";
        try {
            log.info("updateStock: {}, type: {}[42]", Arrays.toString(Stock.toArray()), Stock.getClass());
            if (Stock.isEmpty()) {
                log.error("Empty size[43]");
                throw new Exception("Empty size");
            }
            if (Stock.contains(null)) {
                log.error("List contains null[44]");
                throw new Exception("List contains null");
            }
            if (result.getStatus().equals(Constants.FAIL)){
                log.error("File wasn't found[45]");
                throw new Exception("File wasn't found");
            }


            log.debug("Status of getting file[46]: " + result.getStatus());
            Map<String, Stock> StockFromCSV = new TreeMap<>();
            result.getBody().forEach(x -> StockFromCSV.put(x.getTicker(), x));

            for (Stock stock : Stock){
                if (stock.getTicker() == null){
                    log.error("ID contain null: {}[47]", stock);
                    return new Result<Stock>(Constants.FAIL, "ID contain null", new ArrayList<>(List.of(stock)));
                }

                if (!StockFromCSV.containsKey(stock.getTicker())){
                    log.error("ID wasn't found: {}[48]", stock);
                    return new Result<Stock>(Constants.FAIL, "ID wasn't found", new ArrayList<>(List.of(stock)));
                }

                StockFromCSV.put(stock.getTicker(), stock);
            }
            log.debug("appendStock[49]");
            deleteAllStocks();
            return appendStocks(StockFromCSV.values().stream().toList());
        } catch (Exception e) {
            log.error("Function DataProviderCSV updateStock had crashed[50]");
            status = Constants.FAIL;
            message = e.getMessage();
        }
        return new Result<Stock>(status, message, new ArrayList<>());
    }
}
