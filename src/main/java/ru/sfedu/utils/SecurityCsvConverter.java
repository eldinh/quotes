package ru.sfedu.utils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.api.DataProviderCsv;
import ru.sfedu.model.Security;
import ru.sfedu.model.Stock;

import java.util.Optional;

public class SecurityCsvConverter extends AbstractBeanField<Security, String> {
    private final DataProviderCsv data = new DataProviderCsv();
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
