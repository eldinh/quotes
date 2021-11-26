package ru.sfedu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.Result;
import ru.sfedu.model.entity.Stock;
import ru.sfedu.utils.parser.Request;
import ru.sfedu.utils.parser.mapper.SecurityMapper;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Quotes {
    private final static Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMAN);
    public static  void main(String[] args) throws Exception {
        Request req = new Request();
        SecurityMapper mapper = new SecurityMapper();
        DataProviderCSV data = new DataProviderCSV();
//        List<Stock> stocks = req.getStocks().stream().map(mapper::mapStock).toList();
//        data.appendStocks(stocks);
//        List<Bond> bonds = req.getBonds().stream().map(mapper::mapBond).toList();
        Result<Stock> result = data.getStocks();
        result.getBody().forEach(log::info);
        log.info(data.getStockByTicker("SBER"));
    }
}