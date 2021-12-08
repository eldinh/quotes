package ru.sfedu.builder;

import ru.sfedu.model.MarketName;

public interface ISecurityBuilder {
    public void setTicker(String ticker);
    public void setName(String name);
    public void setShortName(String shortName);
    public void setLatName(String latName);
    public void setNominal(double nominal);
    public void setNominalValue(String nominalValue);
    public void setIssueDate(String issueDate);
    public void setIsin(String isin);
    public void setIssueSize(long issueSize);
    public void setMarketName(MarketName marketName);
}
