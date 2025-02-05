package com.example.ace_taxi_v2.Models;

import java.util.Date;

public class EarningResponse {
    private String date;
    private int userId;
    private double cashTotal; // Changed from int to double
    private double accTotal; // Changed from int to double
    private double rankTotal; // Changed from int to double
    private double commsTotal; // Changed from int to double
    private double grossTotal; // Changed from int to double
    private double netTotal; // Changed from int to double
    private int cashJobsCount;
    private int accJobsCount;
    private int rankJobsCount;
    private double cashMilesCount; // Changed from int to double
    private double accMilesCount; // Changed from int to double
    private double rankMilesCount; // Changed from int to double

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getCashTotal() {
        return cashTotal;
    }

    public void setCashTotal(double cashTotal) {
        this.cashTotal = cashTotal;
    }

    public double getAccTotal() {
        return accTotal;
    }

    public void setAccTotal(double accTotal) {
        this.accTotal = accTotal;
    }

    public double getRankTotal() {
        return rankTotal;
    }

    public void setRankTotal(double rankTotal) {
        this.rankTotal = rankTotal;
    }

    public double getCommsTotal() {
        return commsTotal;
    }

    public void setCommsTotal(double commsTotal) {
        this.commsTotal = commsTotal;
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(double grossTotal) {
        this.grossTotal = grossTotal;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(double netTotal) {
        this.netTotal = netTotal;
    }

    public int getCashJobsCount() {
        return cashJobsCount;
    }

    public void setCashJobsCount(int cashJobsCount) {
        this.cashJobsCount = cashJobsCount;
    }

    public int getAccJobsCount() {
        return accJobsCount;
    }

    public void setAccJobsCount(int accJobsCount) {
        this.accJobsCount = accJobsCount;
    }

    public int getRankJobsCount() {
        return rankJobsCount;
    }

    public void setRankJobsCount(int rankJobsCount) {
        this.rankJobsCount = rankJobsCount;
    }

    public double getCashMilesCount() {
        return cashMilesCount;
    }

    public void setCashMilesCount(double cashMilesCount) {
        this.cashMilesCount = cashMilesCount;
    }

    public double getAccMilesCount() {
        return accMilesCount;
    }

    public void setAccMilesCount(double accMilesCount) {
        this.accMilesCount = accMilesCount;
    }

    public double getRankMilesCount() {
        return rankMilesCount;
    }

    public void setRankMilesCount(double rankMilesCount) {
        this.rankMilesCount = rankMilesCount;
    }
}
