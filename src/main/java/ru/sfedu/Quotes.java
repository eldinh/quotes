package ru.sfedu;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.sfedu.model.dto.SecurityDto;
import ru.sfedu.utils.Moex;
import ru.sfedu.utils.Request;

import java.util.List;

public class Quotes {
    public static  void main(String[] args) throws Exception {
        Request req = new Request();
        List<SecurityDto> list = req.getStocks();
        System.out.println(list.size());
    }

}
