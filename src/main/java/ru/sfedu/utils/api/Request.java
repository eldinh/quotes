package ru.sfedu.utils.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.sfedu.utils.api.model.type.SecurityType;
import ru.sfedu.utils.api.model.dto.SecurityDto;

import java.util.ArrayList;
import java.util.List;

import static ru.sfedu.utils.api.model.type.SecurityType.stock_shares;

public class Request {
    private Moex moex;
    private ObjectMapper objectMapper;
    public Request(){
        objectMapper = new ObjectMapper();
        moex = new Moex();
    }

    public JsonNode getSecurities(int start, SecurityType type) throws Exception {
        try {
            String rawJson = moex.getSecurities(start, type);
            JsonNode jsonNode = objectMapper.readTree(rawJson);
            return jsonNode.get("securities").get("data");
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    public List<SecurityDto> getStocks() throws Exception {
        try{
            int i = 0;
            JsonNode json = getSecurities(i, stock_shares);

            List<SecurityDto> stockList = new ArrayList<>();
            while (!json.isEmpty()){
                i += 100;
                stockList.addAll(objectMapper.readValue(json.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, SecurityDto.class)));
                json = getSecurities(i, stock_shares);
            }

            return stockList;
        }catch (Exception e){
            throw new Exception(e);
        }
    }




}
