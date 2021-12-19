package ru.sfedu.utils;

import com.opencsv.bean.AbstractCsvConverter;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.ArrayList;
import java.util.Arrays;

public class StringListCsvConverter extends AbstractCsvConverter {


    @Override
    public Object convertToRead(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        return new ArrayList<>(Arrays.stream(s.split(" ")).toList());
    }
}
