package ru.sfedu.utils;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.model.Action;
import ru.sfedu.model.Security;

import java.util.ArrayList;
import java.util.List;

public class ActionListCsvConverter extends AbstractBeanField<List<Action>,String> {
    DataProviderCSV data = new DataProviderCSV();

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return data.getActionHistory(s).getBody();
    }

    @Override
    protected String convertToWrite(Object value) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        if (( (ArrayList<Action>) value ).isEmpty())
            return super.convertToWrite("");
        else
            return super.convertToWrite( ((ArrayList<Action>) value).get(0).getUserID());
    }
}
