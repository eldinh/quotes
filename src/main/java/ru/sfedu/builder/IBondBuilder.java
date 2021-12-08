package ru.sfedu.builder;

import ru.sfedu.entity.Bond;

public interface IBondBuilder {
    public void setMatDate(String matDate);
    public void setCoupon(Double coupon);
    public void setDayToRedemption(Integer dayToRedemption);
    public void setType(Bond.BondType type);
}
