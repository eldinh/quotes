package ru.sfedu.utils.conventerCSV;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.SecurityHistory;

import java.util.List;
import java.util.Optional;


public class HistoryConventerCSV extends AbstractBeanField<List<SecurityHistory>, String> {
    private final DataProviderCSV data = new DataProviderCSV();
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            return data.getSecurityHistory(s, SecurityHistory.class);
        }catch (Exception e){
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        Optional<String> ticker = ((List<SecurityHistory>) value).stream().map(SecurityHistory::getTicker).findFirst();
        return super.convertToWrite(ticker.orElse(""));
    }
}
