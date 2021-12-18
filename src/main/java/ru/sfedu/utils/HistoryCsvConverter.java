package ru.sfedu.utils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.SecurityHistory;
import java.util.List;

public class HistoryCsvConverter extends AbstractBeanField<List<SecurityHistory>, String> {
    private final DataProviderCSV data = new DataProviderCSV();
    @Override
    protected Object convert(String s)  {
        return data.getSecurityHistoryByDate(s);
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        String ticker = ((SecurityHistory) value).getTicker();
        return super.convertToWrite(ticker);
    }
}
