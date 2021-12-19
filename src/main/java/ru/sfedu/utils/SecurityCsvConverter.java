package ru.sfedu.utils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.Security;
import ru.sfedu.model.Stock;

import java.util.Optional;

public class SecurityCsvConverter extends AbstractBeanField<Security, String> {
    private final DataProviderCSV data = new DataProviderCSV();
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        Optional<Stock> stock = data.getStockByTicker(s);
        if (stock.isPresent())
            return stock.get();
        return data.getBondByTicker(s).get();
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        String ticker = ((Security) value).getTicker();
        return super.convertToWrite(ticker);
    }
}
