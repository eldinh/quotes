
package ru.sfedu.model;


public class BondBuilder {
    protected String ticker;
    protected String name;
    protected String shortName;
    protected String latName;
    protected double nominal;
    protected String nominalValue;
    protected String issueDate;
    protected String isin;
    protected long issueSize;
    protected SecurityHistory securityHistory;
    private double coupon;
    private int dayToRedemption;
    private String matDate;
    private BondType type;

    public BondBuilder(){}

    public BondBuilder withTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }


    public BondBuilder withName(String name) {
        this.name = name;
        return this;
    }


    public BondBuilder withShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }


    public BondBuilder withLatName(String latName) {
        this.latName = latName;
        return this;
    }


    public BondBuilder withNominal(double nominal) {
        this.nominal = nominal;
        return this;
    }


    public BondBuilder withNominalValue(String nominalValue) {
        this.nominalValue = nominalValue;
        return this;
    }


    public BondBuilder withIssueDate(String issueDate) {
        this.issueDate = issueDate;
        return this;
    }


    public BondBuilder withIsin(String isin) {
        this.isin = isin;
        return this;
    }


    public BondBuilder withIssueSize(long issueSize) {
        this.issueSize = issueSize;
        return this;
    }


    public BondBuilder withMatDate(String matDate) {
        this.matDate = matDate;
        return this;
    }

    public BondBuilder withCoupon(Double coupon) {
        this.coupon = coupon;
        return this;
    }

    public BondBuilder withDayToRedemption(Integer dayToRedemption) {
        this.dayToRedemption = dayToRedemption;
        return this;
    }


    public BondBuilder withType(BondType type) {
        this.type = type;
        return this;
    }

    public BondBuilder withSecurityHistory(SecurityHistory securityHistory) {
        this.securityHistory = securityHistory;
        return this;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLatName() {
        return latName;
    }

    public double getNominal() {
        return nominal;
    }

    public String getNominalValue() {
        return nominalValue;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getIsin() {
        return isin;
    }

    public long getIssueSize() {
        return issueSize;
    }

    public SecurityHistory getSecurityHistory() {
        return securityHistory;
    }

    public double getCoupon() {
        return coupon;
    }

    public int getDayToRedemption() {
        return dayToRedemption;
    }

    public String getMatDate() {
        return matDate;
    }

    public BondType getType() {
        return type;
    }

    public Bond build(){
        return new Bond(this);
    }
}