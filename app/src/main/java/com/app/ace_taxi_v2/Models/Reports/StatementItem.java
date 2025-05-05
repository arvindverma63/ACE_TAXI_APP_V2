package com.app.ace_taxi_v2.Models.Reports;

import android.os.Build;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Remove: import kotlinx.coroutines.Job;

public class StatementItem {
    private String startDate;
    private String endDate;
    private int statementId;
    private double subTotal;
    private int totalJobCount;
    private String statementDate;

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getStatementId() {
        return statementId;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public int getTotalJobCount() {
        return totalJobCount;
    }

    public String getStatementDate() {
        return statementDate;
    }
}