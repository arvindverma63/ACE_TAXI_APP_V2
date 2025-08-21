package com.app.ace_taxi_v2.Models;

public class QuotesResponse {
    private boolean fromBase;
    private String tariff;
    private double deadMileage;
    private int deadMinutes;
    private double journeyMileage;
    private int journeyMinutes;
    private double totalMileage;
    private int totalMinutes;
    private double priceDriver;
    private String mileageText;
    private String durationText;
    private int legs;

    // Getters and Setters

    public boolean isFromBase() {
        return fromBase;
    }

    public void setFromBase(boolean fromBase) {
        this.fromBase = fromBase;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public double getDeadMileage() {
        return deadMileage;
    }

    public void setDeadMileage(double deadMileage) {
        this.deadMileage = deadMileage;
    }

    public int getDeadMinutes() {
        return deadMinutes;
    }

    public void setDeadMinutes(int deadMinutes) {
        this.deadMinutes = deadMinutes;
    }

    public double getJourneyMileage() {
        return journeyMileage;
    }

    public void setJourneyMileage(double journeyMileage) {
        this.journeyMileage = journeyMileage;
    }

    public int getJourneyMinutes() {
        return journeyMinutes;
    }

    public void setJourneyMinutes(int journeyMinutes) {
        this.journeyMinutes = journeyMinutes;
    }

    public double getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(double totalMileage) {
        this.totalMileage = totalMileage;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(int totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public double getTotalPrice() {
        return priceDriver;
    }

    public void setTotalPrice(double totalPrice) {
        this.priceDriver = totalPrice;
    }

    public String getMileageText() {
        return mileageText;
    }

    public void setMileageText(String mileageText) {
        this.mileageText = mileageText;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public int getLegs() {
        return legs;
    }

    public void setLegs(int legs) {
        this.legs = legs;
    }
}
