package ru.sfedu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.utils.api.Request;
import ru.sfedu.utils.api.model.dto.SecurityDto;

import java.util.List;

public class Quotes {
    private final Logger log = (Logger) LogManager.getLogger(Quotes.class.getName());
    public static  void main(String[] args) throws Exception {
        Request req = new Request();
        List<SecurityDto> list = req.getStocks();
        list.forEach(System.out::println);
        System.out.println(list.size());
    }


}
