package ru.sfedu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.api.DataProvider;
import ru.sfedu.api.DataProviderCSV;
import ru.sfedu.api.DataProviderJDBC;
import ru.sfedu.api.DataProviderXML;
import ru.sfedu.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Quotes {
    private final Logger log = (Logger) LogManager.getLogger(DataProviderCSV.class.getName());
    public static DataProvider data;
    public static void main(String[] args) throws Exception {
        String str= "SBER SBERBOND";
        System.out.println(Utils.buildStringListFromString(str, " "));


    }


}