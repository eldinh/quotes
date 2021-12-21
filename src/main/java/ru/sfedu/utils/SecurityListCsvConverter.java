package ru.sfedu.utils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.Security;

import java.util.ArrayList;
import java.util.List;

public class SecurityListCsvConverter extends AbstractBeanField<List<Security>,String> {
    DataProviderCSV data = new DataProviderCSV();
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        try {
            List<String> tickerList = Utils.buildStringListFromString(s, " ");
            return data.getSecuritiesByTickerList(tickerList).getBody();
        }catch (Exception e){
            throw new CsvDataTypeMismatchException(e.getMessage());
        }
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        return super.convertToWrite(Utils.buildStringFromStringList( ( (ArrayList<Security>) value ).stream().map(Security::getTicker).toList() , " ") );
    }
}
