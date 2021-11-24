package ru.sfedu.model.entity;

import com.opencsv.bean.CsvBindByPosition;

import java.time.LocalDate;

public class Security {
    @Override
    public String toString() {
        return "Security [" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isin='" + isin + '\'' +
                ", nominal=" + nominal +
                ", nominalValue='" + nominalValue + '\'' +
                ", issueDate=" + issueDate +
                ", latName='" + latName + '\'' +
                ", issueSize=" + issueSize +
                ", group='" + group + '\'' +
                ']';
    }
    @CsvBindByPosition(position = 0)
    protected String ticker;
    @CsvBindByPosition(position = 1)
    protected String name;
    @CsvBindByPosition(position = 2)
    protected String shortName;
    @CsvBindByPosition(position = 3)
    protected String isin;
    @CsvBindByPosition(position = 4)
    protected Double nominal;
    @CsvBindByPosition(position = 5)
    protected String nominalValue;
    @CsvBindByPosition(position = 6)
    protected LocalDate issueDate;
    @CsvBindByPosition(position = 7)
    protected String latName;
    @CsvBindByPosition(position = 8)
    protected Long issueSize;
    @CsvBindByPosition(position = 9)
    protected String group;  // тип биржи

    public Security(){}

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

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

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public String getLatName() {
        return latName;
    }

    public void setLatName(String latName) {
        this.latName = latName;
    }

    public Long getIssueSize() {
        return issueSize;
    }

    public void setIssueSize(Long issueSize) {
        this.issueSize = issueSize;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }


}
