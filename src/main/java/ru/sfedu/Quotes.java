package ru.sfedu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProviderJDBC;
import ru.sfedu.model.*;


import java.time.format.DateTimeFormatter;
import java.util.*;

public class Quotes {
    private final static Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.GERMAN);
    public static  void main(String[] args) throws Exception {
        ArrayList<SecurityHistory> histories = new ArrayList<>(List.of(
                new SecurityHistory("2021-12-15", 123, "SBER", 12, 13, 100),
                new SecurityHistory("2021-11-21", 123, "SBER", 12, 13, 100),
                new SecurityHistory("2021-01-12", 123, "SBER", 12, 13, 100),
                new SecurityHistory("2021-12-12", 123, "SBER", 12, 13, 100),
                new SecurityHistory("2021-01-01", 123, "SBER", 12, 13, 100),
                new SecurityHistory("2021-01-13", 123, "SBER", 12, 13, 100)
        ));

        List<Stock> stocks = new ArrayList<>(Arrays.asList(
                new Stock("SBER", "", "" , "",
                        100, "RUB", "1", "qwe",
                        1000, MarketType.SHARES, null, Stock.StockType.COMMON, 0, 0)
        ));


        DataProviderJDBC data = new DataProviderJDBC();
        log.info(data.appendSecurityHistory(histories, "SBER"));
        log.info(data.getSecurityHistories("SBER"));
        data.deleteAllSecurityHistories("SBER");
        log.info(data.getSecurityHistories("SBER"));
    }

}