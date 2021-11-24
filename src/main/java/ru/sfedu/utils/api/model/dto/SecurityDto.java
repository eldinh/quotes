package ru.sfedu.utils.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.sfedu.utils.api.model.type.StockType;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class SecurityDto {
    private int id;
    private String secID;
    private String shortName;
    private String regNumber;
    private String name;
    private String isin;
    private int isTraded;
    private int eminentID;
    private String emitentTitle;
    private String emitentInn;
    private String emitentOkpo;
    private String gosReg;
    private String type;
    private String group;
    private String primaryBoardid;
    private String marketpriceBoardid;

    private Double nominal;  // Номинал
    private String nominalValue;  // Валюта номинала
    private Long issueSize;  // Объем выпуска
    private String latName;  // Название на агл.
    private String issueDate; // Дата начали торгов
    //stock

    //bond
    private String matDate;  // Дата погащения
    private Double couponValue;  // Сумма купона


    public Double getNominal() {
        return nominal;
    }

    public void setNominal(Double nominal) {
        this.nominal = nominal;
    }

    public String getNominalValue() {
        return nominalValue;
    }

    public void setNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public Long getIssueSize() {
        return issueSize;
    }

    public void setIssueSize(Long issueSize) {
        this.issueSize = issueSize;
    }

    public String getLatName() {
        return latName;
    }

    public void setLatName(String latName) {
        this.latName = latName;
    }

    public String getMatDate() {
        return matDate;
    }

    public void setMatDate(String matDate) {
        this.matDate = matDate;
    }


    SecurityDto(){}


    @Override
    public String toString() {
        return "SecurityDto{" +
                "id=" + id +
                ", secID='" + secID + '\'' +
                ", shortName='" + shortName + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", name='" + name + '\'' +
                ", isin='" + isin + '\'' +
                ", isTraded=" + isTraded +
                ", eminentID=" + eminentID +
                ", emitentTitle='" + emitentTitle + '\'' +
                ", emitentInn='" + emitentInn + '\'' +
                ", emitentOkpo='" + emitentOkpo + '\'' +
                ", gosReg='" + gosReg + '\'' +
                ", type=" + type +
                ", group='" + group + '\'' +
                ", primaryBoardid='" + primaryBoardid + '\'' +
                ", marketpriceBoardid='" + marketpriceBoardid + '\'' +
                ", matDate='" + matDate + '\'' +
                ", couponValue=" + couponValue +
                ", nominal=" + nominal +
                ", nominalValue='" + nominalValue + '\'' +
                ", issueDate='" + issueDate + '\'' +
                ", issueSize=" + issueSize +
                ", latName='" + latName + '\'' +
                '}';
    }

    public Double getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(Double couponValue) {
        this.couponValue = couponValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSecID() {
        return secID;
    }

    public void setSecID(String secID) {
        this.secID = secID;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }


    public String getEmitentTitle() {
        return emitentTitle;
    }

    public void setEmitentTitle(String emitentTitle) {
        this.emitentTitle = emitentTitle;
    }

    public int getEminentID() {
        return eminentID;
    }

    public void setEminentID(int eminentID) {
        this.eminentID = eminentID;
    }

    public int getIsTraded() {
        return isTraded;
    }

    public void setIsTraded(int isTraded) {
        this.isTraded = isTraded;
    }

    public String getEmitentInn() {
        return emitentInn;
    }

    public void setEmitentInn(String emitentInn) {
        this.emitentInn = emitentInn;
    }

    public String getEmitentOkpo() {
        return emitentOkpo;
    }

    public void setEmitentOkpo(String emitentOkpo) {
        this.emitentOkpo = emitentOkpo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGosReg() {
        return gosReg;
    }

    public void setGosReg(String gosReg) {
        this.gosReg = gosReg;
    }



    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPrimaryBoardid() {
        return primaryBoardid;
    }

    public void setPrimaryBoardid(String primaryBoardid) {
        this.primaryBoardid = primaryBoardid;
    }

    public String getMarketpriceBoardid() {
        return marketpriceBoardid;
    }

    public void setMarketpriceBoardid(String marketpriceBoardid) {
        this.marketpriceBoardid = marketpriceBoardid;
    }

}
