package ru.sfedu.utils.parser.mapper;

import ru.sfedu.model.entity.Bond;
import ru.sfedu.model.entity.Security;
import ru.sfedu.model.entity.Stock;
import ru.sfedu.utils.parser.model.dto.SecurityDto;
import ru.sfedu.utils.parser.model.type.BondType;
import ru.sfedu.utils.parser.model.type.StockType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SecurityMapper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA_FRENCH);
    public Security mapSecurity(SecurityDto dto){
        if (dto == null){
            return null;
        }
        Security security = new Security();
        security.setTicker(dto.getSecID());
        security.setName(dto.getName());
        security.setShortName(dto.getShortName());
        security.setIsin(dto.getIsin());
        security.setNominal(dto.getNominal());
        security.setNominalValue(dto.getNominalValue());
        security.setLatName(dto.getLatName());
        security.setIssueSize(dto.getIssueSize());
        security.setGroup(dto.getGroup());
        if (dto.getIssueDate() != null)
            security.setIssueDate(dto.getIssueDate());
        return security;
    }
    public Stock mapStock(SecurityDto dto){
        if (dto == null){
            return null;
        }
        Stock stock = new Stock(mapSecurity(dto));
        stock.setType(StockType.valueOf(dto.getType()));

        return stock;
    }

    public Bond mapBond(SecurityDto dto){
        if (dto == null){
            return null;
        }
        Bond bond = new Bond(mapSecurity(dto));

        bond.setType(BondType.valueOf(dto.getType()));
        bond.setCoupon(dto.getCouponValue());
        bond.setMatDate(dto.getMatDate());

        return bond;
    }
}