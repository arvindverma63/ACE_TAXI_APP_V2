package com.app.ace_taxi_v2.Models;

public class QuotesResponse {
    private boolean fromBase;
    private String tariff;
    private int deadMileage;
    private int deadMinutes;
    private int journeyMileage;
    private int journeyMinutes;
    private int totalMileage;
    private int totalMinutes;
    private int totalPrice;
    private String mileageText;
    private String durationText;
    private int legs;


    // Getters and Setters
    public boolean isFromBase() { return fromBase; }
    public void setFromBase(boolean fromBase) { this.fromBase = fromBase; }

    public String getTariff() { return tariff; }
    public void setTariff(String tariff) { this.tariff = tariff; }

    public int getDeadMileage() { return deadMileage; }
    public void setDeadMileage(int deadMileage) { this.deadMileage = deadMileage; }

    public int getDeadMinutes() { return deadMinutes; }
    public void setDeadMinutes(int deadMinutes) { this.deadMinutes = deadMinutes; }

    public int getJourneyMileage() { return journeyMileage; }
    public void setJourneyMileage(int journeyMileage) { this.journeyMileage = journeyMileage; }

    public int getJourneyMinutes() { return journeyMinutes; }
    public void setJourneyMinutes(int journeyMinutes) { this.journeyMinutes = journeyMinutes; }

    public int getTotalMileage() { return totalMileage; }
    public void setTotalMileage(int totalMileage) { this.totalMileage = totalMileage; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getMileageText() { return mileageText; }
    public void setMileageText(String mileageText) { this.mileageText = mileageText; }

    public String getDurationText() { return durationText; }
    public void setDurationText(String durationText) { this.durationText = durationText; }

    public int getLegs() { return legs; }
    public void setLegs(int legs) { this.legs = legs; }

    @Override
    public String toString() {
        return "QuotesResponse{" +
                "fromBase=" + fromBase +
                ", tariff='" + tariff + '\'' +
                ", deadMileage=" + deadMileage +
                ", deadMinutes=" + deadMinutes +
                ", journeyMileage=" + journeyMileage +
                ", journeyMinutes=" + journeyMinutes +
                ", totalMileage=" + totalMileage +
                ", totalMinutes=" + totalMinutes +
                ", totalPrice=" + totalPrice +
                ", mileageText='" + mileageText + '\'' +
                ", durationText='" + durationText + '\'' +
                ", legs=" + legs +
                '}';
    }
}
