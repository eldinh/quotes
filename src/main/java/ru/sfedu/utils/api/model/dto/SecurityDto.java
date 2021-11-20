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
    private StockType type;
    private String group;
    private String primaryBoardid;
    private String marketpriceBoardid;

    SecurityDto(){}

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

    public StockType getType() {
        return type;
    }

    public void setType(StockType type) {
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

    @Override
    public String toString() {
        return "StockDto{" +
                "id=" + id +
                ", secID='" + secID + '\'' +
                ", shortName='" + shortName + '\'' +
                ", regNumber='" + regNumber + '\'' +
                ", name='" + name + '\'' +
                ", isin='" + isin + '\'' +
                ", isTraded=" + isTraded +
                ", eminentID=" + eminentID +
                ", emitentTitle='" + emitentTitle + '\'' +
                ", emitentInn=" + emitentInn +
                ", emitentOkpo=" + emitentOkpo +
                ", gosReg='" + gosReg + '\'' +
                ", type=" + type +
                ", group='" + group + '\'' +
                ", primaryBoardid='" + primaryBoardid + '\'' +
                ", marketpriceBoardid='" + marketpriceBoardid + '\'' +
                '}';
    }
}
