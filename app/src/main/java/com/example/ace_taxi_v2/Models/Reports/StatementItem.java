package com.example.ace_taxi_v2.Models.Reports;

public class StatementItem {
    private final String statementNumber;
    private final String date;
    private final String amount;

    public StatementItem(String statementNumber, String date, String amount) {
        this.statementNumber = statementNumber;
        this.date = date;
        this.amount = amount;
    }

    public String getStatementNumber() {
        return statementNumber;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }
}
