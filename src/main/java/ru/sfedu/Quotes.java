package ru.sfedu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.entity.Stock;
import ru.sfedu.utils.api.Request;
import ru.sfedu.utils.api.mapper.SecurityMapper;
import ru.sfedu.utils.api.model.dto.SecurityDto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Quotes {
    private final Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMAN);
    public static  void main(String[] args) throws Exception {
        Request req = new Request();
        SecurityMapper mapper = new SecurityMapper();
        DataProviderCSV data = new DataProviderCSV();
        List<Stock> stocks = req.getStocks().stream().map(mapper::mapStock).toList();
        data.appendStocks(stocks);

    }
}