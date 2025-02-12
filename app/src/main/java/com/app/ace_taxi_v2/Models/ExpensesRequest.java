package com.app.ace_taxi_v2.Models;

public class ExpensesRequest {
    public int userId;
    public String date;
    public String description;
    public double amount;
    public int category;

    public ExpensesRequest(int userId, String date, String description, double amount, int category) {
        this.userId = userId;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

}
