package ru.sfedu.model;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class Bond extends Security {
    @CsvBindByPosition(position = 11)
    private BondType type;
    @CsvBindByPosition(position = 12)
    private String matDate; // Дата погашения
    @CsvBindByPosition(position = 13)
    private double coupon;
    @CsvBindByPosition(position = 14)
    private int dayToRedemption;

    public Bond(BondBuilder bondBuilder){
        super(bondBuilder.getTicker(), bondBuilder.getName(), bondBuilder.getShortName(), bondBuilder.getLatName(), bondBuilder.getNominal(), bondBuilder.getNominalValue(), bondBuilder.getIssueDate(), bondBuilder.getIsin(), bondBuilder.getIssueSize(), bondBuilder.getSecurityHistory());
        this.marketType = MarketType.BONDS;
        this.type = bondBuilder.getType();
        this.matDate = bondBuilder.getMatDate();
        this.coupon = bondBuilder.getCoupon();
        this.dayToRedemption = bondBuilder.getDayToRedemption();
    }


    public Bond(){
        super(MarketType.BONDS);
    }


    public String getMatDate() {
        return matDate;
    }

    public Double getCoupon() {
        return coupon;
    }

    public Integer getDayToRedemption() {
        return dayToRedemption;
    }

    public BondType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Bond [" +
                "ticker='" + ticker + '\'' +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", isin='" + isin + '\'' +
                ", nominal=" + nominal +
                ", nominalValue='" + nominalValue + '\'' +
                ", issueDate=" + issueDate +
                ", latName='" + latName + '\'' +
                ", issueSize=" + issueSize +
                ", group='" + marketType + '\'' +
                ", type=" + type + '\'' +
                ", matDate='" + matDate + '\'' +
                ", coupon='" + coupon + '\'' +
                ", dayToRedemption='" + dayToRedemption + '\'' +
                ", type='" + type + '\'' +
                ']';
    }
}
