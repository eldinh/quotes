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

    }

}