package ru.sfedu.utils.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import ru.sfedu.utils.api.model.dto.SecurityExtraInfoDto;
import ru.sfedu.utils.api.model.type.SecurityType;
import ru.sfedu.utils.api.model.dto.SecurityDto;

import java.util.ArrayList;
import java.util.List;

import static ru.sfedu.utils.api.model.type.SecurityType.stock_bonds;
import static ru.sfedu.utils.api.model.type.SecurityType.stock_shares;

public class Request {
    private final Logger log =(Logger) LogManager.getLogger(Request.class.getName());
    private Moex moex;
    private ObjectMapper objectMapper;
    public Request(){
        objectMapper = new ObjectMapper();
        moex = new Moex();
    }

    private JsonNode getSecuritiesJSON(int start, SecurityType type) throws Exception {
        log.info("Starting Request getSecuritiesJSON[0]");
        try {
            log.info("getSecuritiesJSON[1]:  {}, {}", start, type);
            log.debug("Get raw JSON[2]");
            String rawJson = moex.getSecurities(start, type);

            log.debug("Get defined value in fetched JSON[3]");
            JsonNode jsonNode = objectMapper.readTree(rawJson);
            return jsonNode.get("securities").get("data");
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    private List<SecurityDto> getSecurities(SecurityType type) throws Exception {
        log.info("Starting Request getSecurities[4]");
        try{
            log.info("getSecurities[5]: {}", type);
            int i = 0;
            log.debug("Get JSON[6]");
            JsonNode json = getSecuritiesJSON(i, type);
            log.debug("Get securities list[7]");
            List<SecurityDto> stockList = new ArrayList<>();
            log.debug("Add extra information and map JSON list into List of Objects[8]");
            while (!json.isEmpty()){
                i += 100;
                List<SecurityDto> secList = objectMapper.readValue(json.toString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, SecurityDto.class));
                for (SecurityDto sec : secList){
                    getExtraInfo(sec);
                }
                stockList.addAll(secList);
                json = getSecuritiesJSON(i, type);
                log.info("Page[9]: {}", i);
            }
            return stockList;
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    private JsonNode getExtraInfoJSON(String ticker) throws Exception {
        log.info("Starting Request getExtraInfoJSON[10]");
        try {
            log.info("getExtraInfoJSON[11]: {}", ticker);

            log.debug("Get extra info for securities(rawJSON)[12]");
            String rawJson = moex.getSecurityInfo(ticker);

            log.debug("Get defined value in fetched JSON[13]");
            JsonNode jsonNode = objectMapper.readTree(rawJson);
            return jsonNode.get("description").get("data");
        }catch (Exception e){
            throw new Exception(e);
        }

    }

    private void getExtraInfo(SecurityDto dto) throws Exception {
        log.info("Starting Request getExtraInfo[14]");
        try{
            log.info("getExtraInfo[15]: {}", dto);

            log.debug("Get extra info for securities[16]");
            JsonNode json = getExtraInfoJSON(dto.getSecID());

            log.debug("Map json list to List of objects[17]");
            List<SecurityExtraInfoDto> secList = objectMapper.readValue(json.toString(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, SecurityExtraInfoDto.class));
            for (SecurityExtraInfoDto sec : secList){
                switch (sec.getName()) {
                    case ("LATNAME") ->
                            dto.setLatName(sec.getValue());
                    case ("FACEUNIT") ->  // Валюта
                            dto.setNominalValue(sec.getValue());
                    case ("FACEVALUE") -> // номинал
                            dto.setNominal(Double.parseDouble(sec.getValue()));
                    case ("ISSUEDATE") -> // Дата начала торгов
                            dto.setIssueDate(sec.getValue());
                    case ("ISSUESIZE") ->  // Объем выпуска
                            dto.setIssueSize(Long.parseLong(sec.getValue()));
                    case ("MATDATE") ->  // Дата погащения
                            dto.setMatDate(sec.getValue());
                    case ("COUPONVALUE") ->  // Сумма купона
                            dto.setCouponValue(Double.parseDouble(sec.getValue()));
                }

            }

        }catch (Exception e){
            throw new Exception(e);
        }

    }


    public List<SecurityDto> getStocks() throws Exception {
        log.info("Starting Request getStocks[18]");
        return getSecurities(stock_shares);
    }

    public List<SecurityDto> getBonds() throws Exception {
        log.info("Starting Request getBonds[19]");
        return getSecurities(stock_bonds);
    }




}
