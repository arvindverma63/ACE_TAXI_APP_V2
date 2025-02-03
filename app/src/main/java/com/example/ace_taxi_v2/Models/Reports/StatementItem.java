package com.example.ace_taxi_v2.Models.Reports;

import android.os.Build;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import kotlinx.coroutines.Job;

public class StatementItem {
    private int accountJobsTotalCount;
    private int cashJobsTotalCount;
    private double commissionDue;
    private String dateCreated;
    private String dateUpdated;
    private double earningsAccount;
    private double earningsCash;
    private double earningsCard;
    private int earningsRank;
    private double cardFees;
    private String startDate;
    private String endDate;
    private boolean paidInFull;
    private double paymentDue;
    private int rankJobsTotalCount;
    private int statementId;
    private double subTotal;
    private double totalEarned;
    private int totalJobCount;
    private int userId;
    private String identifier;
    private String colorCode;
    private List<Job> jobs; // Assuming `jobs` is a list of job objects

    public int getAccountJobsTotalCount() {
        return accountJobsTotalCount;
    }

    public void setAccountJobsTotalCount(int accountJobsTotalCount) {
        this.accountJobsTotalCount = accountJobsTotalCount;
    }

    public int getCashJobsTotalCount() {
        return cashJobsTotalCount;
    }

    public void setCashJobsTotalCount(int cashJobsTotalCount) {
        this.cashJobsTotalCount = cashJobsTotalCount;
    }

    public double getCommissionDue() {
        return commissionDue;
    }

    public void setCommissionDue(double commissionDue) {
        this.commissionDue = commissionDue;
    }

    public String getDateCreated() {
        try {
            // Parse the ISO 8601 datetime string
            LocalDateTime dateTime = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.parse(dateCreated);
            }

            // Define the desired output format
            DateTimeFormatter formatter = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
            }

            // Format the parsed datetime and return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return dateTime.format(formatter);
            }
        } catch (Exception e) {
            // Handle parsing errors gracefully
            e.printStackTrace();
            return dateCreated; // Return the original if formatting fails
        }
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public double getEarningsAccount() {
        return earningsAccount;
    }

    public void setEarningsAccount(double earningsAccount) {
        this.earningsAccount = earningsAccount;
    }

    public double getEarningsCash() {
        return earningsCash;
    }

    public void setEarningsCash(double earningsCash) {
        this.earningsCash = earningsCash;
    }

    public double getEarningsCard() {
        return earningsCard;
    }

    public void setEarningsCard(double earningsCard) {
        this.earningsCard = earningsCard;
    }

    public int getEarningsRank() {
        return earningsRank;
    }

    public void setEarningsRank(int earningsRank) {
        this.earningsRank = earningsRank;
    }

    public double getCardFees() {
        return cardFees;
    }

    public void setCardFees(double cardFees) {
        this.cardFees = cardFees;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isPaidInFull() {
        return paidInFull;
    }

    public void setPaidInFull(boolean paidInFull) {
        this.paidInFull = paidInFull;
    }

    public double getPaymentDue() {
        return paymentDue;
    }

    public void setPaymentDue(double paymentDue) {
        this.paymentDue = paymentDue;
    }

    public int getRankJobsTotalCount() {
        return rankJobsTotalCount;
    }

    public void setRankJobsTotalCount(int rankJobsTotalCount) {
        this.rankJobsTotalCount = rankJobsTotalCount;
    }

    public int getStatementId() {
        return statementId;
    }

    public void setStatementId(int statementId) {
        this.statementId = statementId;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(double totalEarned) {
        this.totalEarned = totalEarned;
    }

    public int getTotalJobCount() {
        return totalJobCount;
    }

    public void setTotalJobCount(int totalJobCount) {
        this.totalJobCount = totalJobCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
