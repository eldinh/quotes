package ru.sfedu.entity;

import java.util.Date;

public class SecurityStory {
    private Date date;
    private double averagePerHour;
    private double averagePerDay;
    private double averagePerWeek;
    private double averagePerMonth;
    private double averagePerYear;


    public SecurityStory(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAveragePerHour() {
        return averagePerHour;
    }

    public void setAveragePerHour(double averagePerHour) {
        this.averagePerHour = averagePerHour;
    }

    public double getAveragePerDay() {
        return averagePerDay;
    }

    public void setAveragePerDay(double averagePerDay) {
        this.averagePerDay = averagePerDay;
    }

    public double getAveragePerWeek() {
        return averagePerWeek;
    }

    public void setAveragePerWeek(double averagePerWeek) {
        this.averagePerWeek = averagePerWeek;
    }

    public double getAveragePerMonth() {
        return averagePerMonth;
    }

    public void setAveragePerMonth(double averagePerMonth) {
        this.averagePerMonth = averagePerMonth;
    }

    public double getAveragePerYear() {
        return averagePerYear;
    }

    public void setAveragePerYear(double averagePerYear) {
        this.averagePerYear = averagePerYear;
    }

    @Override
    public String toString() {
        return "SecurityStory{" +
                "date=" + date +
                ", averagePerHour=" + averagePerHour +
                ", averagePerDay=" + averagePerDay +
                ", averagePerWeek=" + averagePerWeek +
                ", averagePerMonth=" + averagePerMonth +
                ", averagePerYear=" + averagePerYear +
                '}';
    }
}
