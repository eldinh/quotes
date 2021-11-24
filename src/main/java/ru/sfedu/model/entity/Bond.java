package ru.sfedu.model.entity;

import com.opencsv.bean.CsvBindByPosition;
import ru.sfedu.utils.api.model.type.BondType;

import java.time.LocalDate;

public class Bond extends Security{
    @CsvBindByPosition(position = 10)
    private LocalDate matDate; // Дата погашения
    @CsvBindByPosition(position = 11)
    private Double coupon;
    @CsvBindByPosition(position = 12)
    private Integer dayToRedemption;
    @CsvBindByPosition(position = 13)
    private BondType type;

    public LocalDate getMatDate() {
        return matDate;
    }

    public void setMatDate(LocalDate matDate) {
        this.matDate = matDate;
    }

    public Bond(Security security){
        this.ticker = security.getTicker();
        this.name = security.getName();
        this.shortName = security.getShortName();
        this.isin = security.getIsin();
        this.nominal = security.getNominal();
        this.nominalValue = security.getNominalValue();
        this.issueDate = security.getIssueDate();
        this.latName = security.getLatName();
        this.issueSize = security.getIssueSize();
        this.group = security.group;
    }

    public BondType getType() {
        return type;
    }

    public void setType(BondType type) {
        this.type = type;
    }

    public Double getCoupon() {
        return coupon;
    }

    public void setCoupon(Double coupon) {
        this.coupon = coupon;
    }

    public Integer getDayToRedemption() {
        return dayToRedemption;
    }

    public void setDayToRedemption(Integer dayToRedemption) {
        this.dayToRedemption = dayToRedemption;
    }

    public Bond(){}

}
