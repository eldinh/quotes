package ru.sfedu.model.entity;

import java.util.Date;

public class Bond extends Security{
    Date dayOfRepayment;
    double coupon;
    public Bond(){}

    public Date getDayOfRepayment() {
        return dayOfRepayment;
    }

    public void setDayOfRepayment(Date dayOfRepayment) {
        this.dayOfRepayment = dayOfRepayment;
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }
}
